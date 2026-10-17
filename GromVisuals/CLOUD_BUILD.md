# Build GromVisuals on GitHub Actions

1. Upload the contents of the `GromVisuals` folder to the repository root (or retain the folder; the workflow detects both layouts).
2. Commit and push to `main` / `master`, or use **Actions → Build GromVisuals → Run workflow**.
3. Open the completed workflow run and download the `GromVisuals-Minecraft-1.21.11` artifact.
4. Extract the artifact and put `grom-visuals-1.5.0.jar` into `.minecraft/mods`, alongside Fabric API compatible with Minecraft 1.21.11.

The default Minecraft title screen is intentionally left vanilla in this version. The branded interface is available in-game with Right Shift.

## New controls
- Hold `C` to zoom. Keep holding `C` and scroll to change zoom strength from 1x to 8x.
- Press `M` to cycle the minimap scale.
- Press `Right Shift` to open/close the GromVisuals menu.

This source has not been compiled locally in this environment. A successful GitHub Actions build and in-game test are still required before treating the JAR as verified.
