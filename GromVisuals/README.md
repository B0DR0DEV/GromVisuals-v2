# GromVisuals 1.3.0

Client-side Fabric visual HUD for Minecraft 1.21.11 (Java 21).

## 1.2.0
- Drag-and-drop positioning for HUD widget groups. Open the menu with Right Shift, choose MOVE HUD, drag widgets using the free cursor, then press Right Shift or Escape to finish.
- Crosshair Studio with a 21x21 pixel canvas, colors and saved settings.
- When custom crosshair is enabled, a client mixin hides the vanilla crosshair.
- Dark/neon control center and sidebar navigation.
- Positions and crosshair settings persist in config/grom-visuals.json.

Build with Java 21: `gradle clean build`. The remapped JAR is in `build/libs/`.

Cosmetic toggles (China Hat, Halo, Angel Wings, Devil Horns and particles) remain UI placeholders; renderers are not implemented yet.


## 1.3.0 update
- HUD editor now runs as a screen over the world, releasing the mouse cursor while editing and restoring game mouse capture when closed with Escape or Right Shift.
- Crosshair Studio includes a one-click option to restore the vanilla Minecraft crosshair (and stop hiding it).
- Minecraft title screen receives a Grom Visuals header, a subtle violet panorama treatment, and translucent hover-responsive buttons while retaining the original button actions.

The source still needs a successful Gradle build and an in-game check before treating the update as verified.
