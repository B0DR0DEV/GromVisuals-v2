# GromVisuals 1.5.0

Client-side Fabric visual customization for Minecraft 1.21.11 (Java 21).

## New in 1.5.0
- Restored the original vanilla Minecraft title screen. GromVisuals no longer tints the panorama, adds branding over the vanilla logo, or changes the vanilla main-menu buttons.
- Added smooth hold-to-zoom: hold **C** to zoom, then use the mouse wheel while continuing to hold C to raise/lower the zoom strength from 1x to 8x. The key can be rebound in Minecraft Controls.
- Rebuilt the GromVisuals control center with rounded cards, animated accent states, cleaner navigation, pill toggles, a darker layered background, and a more consistent hierarchy.
- Reworked the HUD widgets to use rounded panels and added a zoom-strength indicator while zoom is active.
- Cosmetic controls now emit distinct local particle effects around the player: halo-like orbiting sparks, head sparkles, twin soul particles, flame accents, and enchantment particles. These are client-side effects, not 3D wearable models.
- Keeps the minimap, CPS, LMB/RMB indicators, customizable HUD positions and crosshair editor from earlier versions.

## Controls
- **Right Shift** — open/close GromVisuals.
- **C (hold)** — zoom in using the current zoom strength.
- **C + mouse wheel** — adjust zoom strength (1x–8x).
- **M** — cycle the minimap scale through its three steps.
- **Right Shift / Escape** — finish HUD editing and return mouse capture to the game.

## Build
Requires Java 21 and Gradle 9.2.0. Run `gradle clean build`; the remapped mod JAR is in `build/libs/`.
The GitHub Actions workflow builds the project and uploads the JAR as the `GromVisuals-Minecraft-1.21.11` artifact.

## Important validation note
This source ZIP was checked for archive integrity and reviewed for configuration consistency, but was not compiled or launched in Minecraft in this environment. Run the GitHub Actions build and test in Minecraft 1.21.11 before using it as a release. If the build fails, share the Actions log.
