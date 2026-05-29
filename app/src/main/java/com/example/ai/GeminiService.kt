package com.example.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.BuildConfig
import com.example.R
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.IOException
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}

class GeminiRepository {
    companion object {
        private const val TAG = "GeminiRepository"
    }

    suspend fun generateResponse(context: Context, prompt: String, systemInstruction: String? = null): String {
        if (!isNetworkAvailable(context)) {
            Log.w(TAG, "Network unavailable for Gemini request")
            return context.getString(R.string.gemini_no_network_message)
        }

        return try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                Log.w(TAG, "GEMINI_API_KEY is not configured or using placeholder")
                return context.getString(R.string.gemini_api_key_unconfigured)
            }
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                systemInstruction = systemInstruction?.let { Content(parts = listOf(Part(text = it))) }
            )
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "لم أستطع معالجة السؤال الكوني حالياً. يرجى تكرار المحاولة."
        } catch (e: IOException) {
            Log.e(TAG, "Network IO failure", e)
            return context.getString(R.string.gemini_network_error)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error from Gemini API", e)
            return context.getString(R.string.gemini_service_error)
        } catch (e: JsonDataException) {
            Log.e(TAG, "Gemini response parse failure", e)
            return context.getString(R.string.gemini_parse_error)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error calling Gemini API", e)
            return context.getString(R.string.gemini_service_error)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}
