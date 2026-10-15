# GromVisuals 1.2.0

Client-side Fabric visual HUD for Minecraft 1.21.11 (Java 21).

## 1.2.0
- Drag-and-drop positioning for HUD widget groups. Open the menu with Right Shift, choose MOVE HUD, drag widgets, press Right Shift to finish.
- Crosshair Studio with a 21x21 pixel canvas, colors and saved settings.
- When custom crosshair is enabled, a client mixin hides the vanilla crosshair.
- Dark/neon control center and sidebar navigation.
- Positions and crosshair settings persist in config/grom-visuals.json.

Build with Java 21: `gradle clean build`. The remapped JAR is in `build/libs/`.

Cosmetic toggles (China Hat, Halo, Angel Wings, Devil Horns and particles) remain UI placeholders; renderers are not implemented yet.
