# GromVisuals

Client-side visual mod for **Minecraft Java Edition 1.21.11**, Fabric Loader, and Java 21. Project metadata author: **B0DR0DEV**.

The HUD appearance takes its inspiration from the supplied reference: compact translucent charcoal panels, warm gold data accents, and small high-contrast indicators. The original Minecraft title screen, Singleplayer, Multiplayer, world/server lists, connection flow, and vanilla options screens are not replaced.

## Current implementation

- **Separate client menu** — opens with **Right Shift** in a world. The vanilla title menu remains untouched.
- **HUD modules** — FPS, latency when multiplayer latency is available, coordinates, potion status and duration, keystrokes, left/right click CPS, armor and durability, optional inventory preview, target entity health, compass direction, low-health warning, and zoom indicator.
- **HUD editor** — drag supported widgets, select them, reset positions, and persist their placement.
- **Zoom** — hold **C** to zoom; scroll while holding C to change the multiplier. Releasing C smoothly returns to the normal camera field of view. C is configurable in Minecraft Controls; the menu key is configurable there too.
- **Crosshair Studio** — 21×21 editable bitmap, Plus/Dot/X/Ring presets, color selection, pixel scale, clear, preview, and a Vanilla option that restores the normal Minecraft crosshair.
- **Local minimap** — small top-down sampler based only on already-loaded client chunks; no hidden chunk loading or remote data access.
- **Local cosmetic effect** — orbiting electrical sparks around the local player. Cosmetic particles are local-only and are not synchronized to other players.
- **Configuration** — settings, HUD positions, crosshair pixels, and zoom values saved to `config/gromvisuals.json`; settings backup import/export supported through the menu.
- **GitHub Actions** — runs `clean build` for pushes, pull requests, and manual workflow dispatch; uploads the remapped mod JAR as an artifact and saves diagnostic reports on failure.

## Build with GitHub Actions (no local Gradle installation needed)

1. Create a repository on GitHub and upload/push **all files and folders in this project**, including `.github`, `gradlew`, `gradlew.bat`, `gradle/wrapper`, and `src`.
2. Open the repository's **Actions** tab and enable Actions if GitHub asks.
3. The `Build GromVisuals` workflow starts automatically on `push`. You can also select it and click **Run workflow**.
4. Open the completed workflow run and download the artifact named **GromVisuals-Minecraft-1.21.11**.
5. Place the downloaded JAR in your Minecraft `mods` folder alongside Fabric Loader and Fabric API for Minecraft 1.21.11.

A failed build is marked as failed in Actions; no JAR artifact is uploaded from a failed run. Diagnostic logs/reports are uploaded separately where available.

## Build locally (optional)

Requires JDK 21 and network access. On Windows run `gradlew.bat build`; on Linux/macOS run `./gradlew build`. The wrapper bootstraps the pinned Gradle 9.2.1 distribution and does not require a preinstalled Gradle.

> This source archive has not been validated by a successful Gradle compilation in the authoring environment because its network/DNS could not reach the distribution/dependency hosts. Use the first GitHub Actions run as the actual compile check; this README does not claim that a successful build has already occurred.

## Controls

- **Right Shift** — open GromVisuals menu while inside a world.
- **C (hold)** — zoom.
- **Mouse wheel while C is held** — increase/decrease target zoom level.
- Minecraft **Options → Controls** can be used to rebind these keys.
- **Escape** closes the menu/editor and returns input to Minecraft.

## Configuration and backups

- Main config: `.minecraft/config/gromvisuals.json`
- Backup: `.minecraft/config/gromvisuals-backup.json`
- Export/import buttons inside the Settings tab create/use the backup file.

## Scope and known limitations

- The included 3D cosmetic implementation is intentionally limited to the working local orbit-spark effect. Halo, wings, horns, and hats are not presented as finished controls until proper model rendering is implemented.
- Minimap cells represent coarse surface-height/color samples from chunks already loaded by Minecraft, not a full-resolution navigation map; it may be sparse at chunk edges and in unloaded areas.
- Ping is displayed only when the local player has an available multiplayer player-list entry. No fake latency value is generated.
- Saturation and advanced saturation overlays, a full cosmetics preview studio, independent per-widget size/transparency controls, and a complete profile manager are not implemented yet.
- UI smoke test and in-game launch test have not been run in this environment.

## Technical notes

- Fabric Loom, Yarn mappings, Fabric Loader, and Fabric API versions are pinned in `gradle.properties`/`build.gradle` for 1.21.11.
- Mixin hooks are client-only and scoped to input, camera FOV, and the vanilla crosshair render path.
- There is no networking code, telemetry, server-side component, or hidden data transfer.
- The checked-in wrapper bootstrap JAR is a small Java 21 bootstrapper that follows `gradle-wrapper.properties` and downloads the declared Gradle distribution on first use. It is included with source; it is not the upstream Gradle wrapper binary.

## License

MIT — see [`LICENSE`](LICENSE).
