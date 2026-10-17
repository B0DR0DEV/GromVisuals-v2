package dev.gromclient.visuals.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Central, versioned local configuration. Missing fields keep their safe defaults. */
public final class GromConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("gromvisuals.json");
    private static final int CONFIG_VERSION = 2;

    public int configVersion = CONFIG_VERSION;
    public boolean hudEnabled = true;
    public boolean showInfo = true;
    public boolean showCoordinates = true;
    public boolean showPotionEffects = true;
    public boolean showKeystrokes = true;
    public boolean showClickHud = true;
    public boolean showArmor = true;
    public boolean showInventory = false;
    public boolean showTargetHud = true;
    public boolean showMiniMap = true;
    public boolean showDirection = true;
    public boolean showHealthWarning = true;
    public boolean showVitals = true;
    public boolean showBiome = true;

    public boolean zoomEnabled = true;
    public float zoomMax = 8.0f;
    public float zoomMin = 1.5f;
    public float zoomSpeed = 0.65f;
    public float zoomTarget = 3.0f;

    public boolean customCrosshairEnabled = false;
    public int crosshairColor = 0xFFFFC247;
    public int crosshairSize = 2;
    public boolean[] crosshairPixels = defaultCrosshair();

    public boolean orbitSparks = true;
    public float particleIntensity = 0.75f;
    public int accentColor = 0xFF8B5CF6;
    public int hudOpacity = 220;
    public boolean animationsEnabled = true;
    public Map<String, HudPosition> positions = defaultPositions();

    public static final class HudPosition {
        public int x;
        public int y;
        public HudPosition() { }
        public HudPosition(int x, int y) { this.x = x; this.y = y; }
    }

    private static GromConfig INSTANCE = new GromConfig();

    private GromConfig() { }

    public static GromConfig get() { return INSTANCE; }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                GromConfig read = GSON.fromJson(Files.readString(FILE, StandardCharsets.UTF_8), GromConfig.class);
                if (read != null) INSTANCE = read;
            }
        } catch (IOException | JsonParseException | IllegalStateException exception) {
            System.err.println("[GromVisuals] Could not read config; using defaults: " + exception.getMessage());
            INSTANCE = new GromConfig();
        }
        INSTANCE.normalize();
        save();
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            INSTANCE.configVersion = CONFIG_VERSION;
            Files.writeString(FILE, GSON.toJson(INSTANCE), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.err.println("[GromVisuals] Could not save config: " + exception.getMessage());
        }
    }

    public static Path getFile() { return FILE; }

    public static boolean importBackup() {
        Path backup = FILE.resolveSibling("gromvisuals-backup.json");
        if (!Files.exists(backup)) return false;
        try {
            GromConfig read = GSON.fromJson(Files.readString(backup, StandardCharsets.UTF_8), GromConfig.class);
            if (read == null) return false;
            INSTANCE = read;
            INSTANCE.normalize();
            save();
            return true;
        } catch (IOException | JsonParseException | IllegalStateException exception) {
            System.err.println("[GromVisuals] Backup import failed: " + exception.getMessage());
            return false;
        }
    }

    public static boolean exportBackup() {
        Path backup = FILE.resolveSibling("gromvisuals-backup.json");
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(backup, GSON.toJson(INSTANCE), StandardCharsets.UTF_8);
            return true;
        } catch (IOException exception) {
            System.err.println("[GromVisuals] Backup export failed: " + exception.getMessage());
            return false;
        }
    }

    public HudPosition position(String id) {
        if (positions == null) positions = defaultPositions();
        return positions.computeIfAbsent(id, ignored -> new HudPosition(12, 12));
    }

    public int x(String id, int screenWidth) {
        int value = position(id).x;
        return value < 0 ? Math.max(0, screenWidth + value) : value;
    }

    public void normalize() {
        configVersion = CONFIG_VERSION;
        zoomMin = clamp(zoomMin, 1.0f, 8.0f);
        zoomMax = clamp(zoomMax, Math.max(zoomMin, 1.25f), 16.0f);
        zoomSpeed = clamp(zoomSpeed, 0.1f, 2.5f);
        zoomTarget = clamp(zoomTarget, zoomMin, zoomMax);
        crosshairSize = Math.max(1, Math.min(5, crosshairSize));
        hudOpacity = Math.max(70, Math.min(255, hudOpacity));
        particleIntensity = clamp(particleIntensity, 0.0f, 1.5f);
        if (crosshairPixels == null || crosshairPixels.length != 441) crosshairPixels = defaultCrosshair();
        if (positions == null) positions = new LinkedHashMap<>();
        for (Map.Entry<String, HudPosition> entry : defaultPositions().entrySet()) {
            positions.putIfAbsent(entry.getKey(), entry.getValue());
            if (positions.get(entry.getKey()) == null) positions.put(entry.getKey(), entry.getValue());
            HudPosition p = positions.get(entry.getKey());
            p.x = Math.max(-300, Math.min(5000, p.x));
            p.y = Math.max(-300, Math.min(3000, p.y));
        }
    }

    public static GromConfig freshDefaults() {
        GromConfig fresh = new GromConfig();
        INSTANCE = fresh;
        save();
        return INSTANCE;
    }

    public void resetPositions() {
        positions = defaultPositions();
        save();
    }

    private static float clamp(float v, float min, float max) { return Math.max(min, Math.min(max, v)); }

    private static Map<String, HudPosition> defaultPositions() {
        Map<String, HudPosition> map = new LinkedHashMap<>();
        map.put("info", new HudPosition(12, 10));
        map.put("coordinates", new HudPosition(12, 43));
        map.put("effects", new HudPosition(12, 78));
        map.put("keystrokes", new HudPosition(-122, 12));
        map.put("clicks", new HudPosition(-126, 166));
        map.put("armor", new HudPosition(-124, -208));
        map.put("inventory", new HudPosition(-232, -126));
        map.put("target", new HudPosition(12, -88));
        map.put("minimap", new HudPosition(-235, 12));
        map.put("biome", new HudPosition(240, 10));
        map.put("vitals", new HudPosition(240, -60));
        return map;
    }

    private static boolean[] defaultCrosshair() {
        boolean[] result = new boolean[21 * 21];
        for (int i = 0; i < 21; i++) {
            result[10 * 21 + i] = i >= 7 && i <= 13;
            result[i * 21 + 10] = i >= 7 && i <= 13;
        }
        return result;
    }
}
