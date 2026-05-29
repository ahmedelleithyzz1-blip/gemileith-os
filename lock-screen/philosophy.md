# 🔐 Lock Screen & Interactive Design

## Philosophy: Silent Seals on Dormant Display

The lock screen represents a **threshold state**—the device is sleeping but never truly off. Icons are present but dormant, waiting for interaction.

### Core Concept: Silent Seals (الأختام الصامتة)

Instead of traditional inactive icons, lock screen icons are reimagined as **"silent seals"**—ancient technological artifacts in dormant state.

---

## Silent Seals Design Philosophy

### Visual State: Inactive Dormancy

**Icon Appearance on Lock Screen**:

```
Silent Seal State:
├── Color: Dark metallic gray (#2A2A2A)
├── Saturation: 0% (completely desaturated)
├── Brightness: 40% (dim but visible)
├── Opacity: 70% (semi-translucent, ghostly)
├── Glow: Minimal—barely perceptible
├── Shadow: Soft, diffuse (not imposing)
└── Effect: "Sleeping but present, waiting for awakening"
```

**Metaphor**: Icons appear as dormant seals, locked away, not yet activated. The color drains to gray because the energy is not flowing—the artifact is at rest.

---

### Transition Rule: Badge Notification Ignition

**When notification arrives**, the seal awakens:

```
Ignition Sequence:
├── Duration: 0.3-0.5 seconds
├── Step 1: Color begins flowing back (desaturation decreases)
├── Step 2: Glow begins emanating from cracks
├── Step 3: App color **ignites from inside** the icon
├── Step 4: Badge appears with notification indicator
└── Result: Icon transforms from seal to active artifact
```

**Visual Process**:

```
DORMANT (no notification):
Icon = Gray + minimal glow + low opacity
  ↓
NOTIFICATION ARRIVES:
├── Color saturation increases (0% → 100%)
├── Glow brightens (barely visible → fully visible)
├── Original app color flows from inner core outward
├── Reaches cracks in weathered structure
├── Bursts through as bright badge/indicator
└── Icon now **ACTIVE**
  ↓
ACTIVE (with notification):
Icon = Full color + bright glow + badge glowing
```

**Technical Implementation**:

```CSS
/* Pseudo-code for animation */

@keyframes seal-ignition {
  0% {
    filter: grayscale(100%) brightness(40%);
    opacity: 0.7;
    box-shadow: none;
  }
  50% {
    filter: grayscale(50%) brightness(60%);
    opacity: 0.85;
  }
  100% {
    filter: grayscale(0%) brightness(100%);
    opacity: 1;
    box-shadow: 0 0 12px var(--app-color);
  }
}

.icon.has-notification {
  animation: seal-ignition 0.4s ease-out;
}
```

---

## Badge Notification System

### Badge Design

**Standard Badge** (for message/notification count):

```
Badge Appearance:
├── Shape: Circular or rounded square
├── Size: 24×24px (positioned at icon corner)
├── Color: App's original brand color (glowing)
├── Background: Semi-transparent dark circle
├── Number/Icon: White text or symbol
├── Glow: 6-8px outer glow in app color
├── Position: Upper-right corner of icon
└── Animation: Subtle pulse when notification arrives
```

**Badge Example - WhatsApp**:
```
[Icon] ← Badge
         Circle with green (#25D366) glow
         Contains: number or chat indicator
         Pulses slightly when message arrives
```

**Badge Example - Camera**:
```
[Icon] ← Badge  
         Circle with silver (#C0C0C0) glow
         Contains: new media indicator
         Shows when photo or video captured
```

**Badge Example - Settings**:
```
[Icon] ← Badge
         Circle with gray (#808080) glow
         Contains: update indicator
         Shows system updates available
```

---

### Badge Animation on Arrival

**Single Notification**:
```
Timeline:
T=0ms:     Silent seal state (gray)
T=100ms:   Icon color begins flowing
T=200ms:   Glow visible
T=300ms:   Badge appears and pulses outward
T=400ms:   Settles into steady state
```

**Multiple Notifications**:
```
If notification arrives while seal is active:
├── Badge count increments
├── Glow brightens (adds emphasis)
├── Icon pulses outward once
└── Returns to stable state
```

---

## Lock Screen Wallpaper Integration

### Interactive Wallpaper Concept: Light Columns & Power Beams

The lock screen wallpaper is not static—it's an **active energy field**.

**Wallpaper Design Elements**:

```
Energy Field Components:
├── Light Columns (أعمدة النور):
│   ├── Shape: Vertical light beams, 40-80px wide
│   ├── Color: Subtle, matching OS color scheme
│   ├── Opacity: 20-30% (barely visible)
│   ├── Animation: Slow pulsing (3-5 second cycle)
│   └── Purpose: Suggest invisible energy infrastructure
├── Magic Circles (دوائر سحرية):
│   ├── Shape: Circular glow zones, various sizes
│   ├── Diameter: 100-300px
│   ├── Color: Faint, color-matched to theme
│   ├── Opacity: 15-25%
│   ├── Animation: Subtle rotation or breathing
│   └── Purpose: Create sacred geometry of energy flow
└── Overall Effect: Mystical energy network underlying system
```

---

### Icon-Wallpaper Symbiosis: Energy Harvesting

**Core Interaction**: When an icon sits over an energy field, it appears to **draw power** from it.

**Example - WhatsApp over Light Column**:

```
Visual Sequence:
├── Icon positioned over vertical light column
├── Green glow concentrates toward light column
├── Appears as if green energy is drawn from column
├── Connection line (subtle) shows symbiosis
├── Message notifications intensify when over energy zone
└── Effect: "Icon feeds from the wallpaper's energy"
```

**Technical Concept**:

```
Detection Algorithm (Pseudo-code):
for each icon on lock screen:
  if icon.centerPoint is within any energy_zone:
    icon.glow_color = icon.brand_color
    icon.glow_intensity = 100%
    zone.brightness += 10%
    create_visual_connection(icon, zone)
  else:
    icon.glow_intensity = 50%
```

---

### Wallpaper Color Themes

**Light Theme Lock Screen**:
```
Background: Light gray (#F5F5F5)
Energy fields: Subtle white/light blue
Icon dormancy color: Medium gray (#505050)
Badge glow: Full app color
Effect: Clean, ethereal, peaceful
```

**Dark Theme Lock Screen**:
```
Background: Deep black (#0A0A0A)
Energy fields: Deep indigo/purple
Icon dormancy color: Dark gray (#1A1A1A)
Badge glow: Bright app color (high contrast)
Effect: Cosmic, mysterious, powerful
```

**Custom Theme Lock Screen**:
```
Background: User-selected color (60% opacity)
Energy fields: Derived from background (desaturated)
Icon dormancy color: Complementary to background
Badge glow: App colors with theme overlay
Effect: Personalized while maintaining system cohesion
```

---

## Lock Screen Animations & Transitions

### Unlock Sequence

**When user taps to unlock**:

```
Timeline:
T=0ms:      User taps/swipes to unlock
T=50ms:     Icons begin brightening
T=100ms:    All glows fade gradually
T=150ms:    Energy field wallpaper transitions
T=200ms:    Home screen begins appearing
T=300ms:    Lock screen fully faded
T=400ms:    Home screen fully visible
```

---

### Notification Arrival Animation

**Gentle awakening** (if device is locked):

```
Step 1: Seal Recognition (100ms)
├── Icon detects incoming notification
├── Begins subtle scale (1.0 → 1.05)
└── Color saturation increases slightly

Step 2: Ignition (200ms)
├── App color "ignites" from center
├── Flows outward through weathered cracks
├── Reaches badge area
└── Badge appears with glow

Step 3: Pulse (300ms)
├── Badge pulses outward (scale 1.0 → 1.2)
├── Icon stabilizes at full alert state
└── Settles into sustainable notification display

Step 4: Sustain
├── Icon remains bright with glowing badge
├── Light glow for duration of notification
└── Ready for user interaction
```

---

## Best Practices for Lock Screen Design

### DO ✅

- ✅ Use **full app color** for badges (high contrast)
- ✅ Position badges at **icon corners** (standard OS placement)
- ✅ Animate badge **entrance smoothly** (not jarring)
- ✅ Make icons **clearly visible** even in dormancy
- ✅ Place energy fields **randomly but distributed** on wallpaper
- ✅ Ensure notifications are **immediately noticeable**
- ✅ Create smooth transitions between states

### DON'T ❌

- ❌ Use muted colors for badges (low visibility)
- ❌ Place badges at random positions (inconsistent)
- ❌ Animate with sudden jumps (jarring experience)
- ❌ Make icons disappear or fade completely
- ❌ Overload wallpaper with energy fields (visual noise)
- ❌ Hide notification indicators
- ❌ Use jarring animation timing

---

## Integration with System UI

### Home Screen Transition

When user unlocks device:

```
Lock Screen State → Home Screen State

Icon Properties Change:
├── Background: Maintains lock screen wallpaper OR
├── Switches: To home screen wallpaper
├── Opacity: 1.0 (fully visible)
├── Saturation: 100% (full color)
├── Glow: Maintains if notification present
└── Interaction: Becomes fully interactive
```

### Badge Persistence

**Badges remain visible on home screen** until notification is cleared:

```
Lock Screen: Badge with glow
  ↓ (user unlocks)
Home Screen: Badge with glow (continues showing)
  ↓ (user interacts with app)
App Opens: Badge disappears after notification is read/dismissed
```

---

## Specifications Summary

| Element | Lock Screen | Home Screen |
|---------|-------------|-------------|
| **Icon Color** | Dormant gray | Full color |
| **Glow** | Minimal (hidden) | Visible (layer 2) |
| **Badge** | Bright glowing circle | Number/indicator with glow |
| **Animation** | Smooth transitions | Instant with notification pulse |
| **Wallpaper** | Energy fields visible | User wallpaper OR theme wallpaper |
| **Interactivity** | Limited (just notifications) | Full (tap to launch) |

---

## Technical Implementation Checklist

- [ ] Lock screen icons render at 50% brightness by default
- [ ] Notification badge system integrated with OS
- [ ] Color desaturation applied to dormant icons
- [ ] Energy field wallpaper layer created
- [ ] Icon-wallpaper position detection implemented
- [ ] Smooth animation transitions programmed
- [ ] Badge glow algorithm optimized
- [ ] Unlock transition animation smooth
- [ ] Battery/performance optimization verified
- [ ] Cross-theme compatibility tested

---

## Next Steps

- [ ] Design interactive wallpaper templates
- [ ] Create badge animation specifications
- [ ] Test icon visibility on various lock screen brightness levels
- [ ] Optimize energy field rendering for performance
- [ ] Integrate with notification system
- [ ] User testing for visual clarity and aesthetics

---

**Related Documents**:
- `case-studies/whatsapp/specifications.md` - WhatsApp notification example
- `case-studies/camera/specifications.md` - Camera notification example
- `case-studies/settings/specifications.md` - Settings notification example
- `wallpaper/interaction-design.md` - Detailed wallpaper system (coming soon)
