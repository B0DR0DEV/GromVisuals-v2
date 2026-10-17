package com.b0dr0dev.gromvisuals.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** User options for GromVisuals. Stored separately from the mod jar. */
public final class VisualConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("gromvisuals.json");
    private static VisualConfig instance = new VisualConfig();

    public boolean hudEnabled = true;
    public boolean showBrand = true;
    public boolean showFps = true;
    public boolean showPing = true;
    public boolean showCoordinates = true;
    public boolean showBiome = true;
    public boolean showKeystrokes = true;
    public boolean showWorldTime = false;
    public boolean animations = true;
    public boolean compactHud = false;
    /** 0 = violet, 1 = cyan, 2 = amber, 3 = mint. */
    public int accentPreset = 0;

    private VisualConfig() {
    }

    public static VisualConfig get() {
        return instance;
    }

    public static void load() {
        try {
            Files.createDirectories(FILE.getParent());
            if (Files.exists(FILE)) {
                VisualConfig loaded = GSON.fromJson(Files.readString(FILE, StandardCharsets.UTF_8), VisualConfig.class);
                if (loaded != null) {
                    instance = loaded;
                }
            } else {
                save();
            }
        } catch (Exception exception) {
            System.err.println("[GromVisuals] Couldn't load config; using defaults: " + exception.getMessage());
            instance = new VisualConfig();
        }
        instance.sanitize();
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(instance), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.err.println("[GromVisuals] Couldn't save config: " + exception.getMessage());
        }
    }

    private void sanitize() {
        if (accentPreset < 0 || accentPreset > 3) accentPreset = 0;
    }

    public int accent() {
        return switch (accentPreset) {
            case 1 -> 0xFF46D9F2;
            case 2 -> 0xFFFFB454;
            case 3 -> 0xFF70E1B5;
            default -> 0xFF9A7BFF;
        };
    }

    public int accentSoft() {
        return switch (accentPreset) {
            case 1 -> 0xFF173D4C;
            case 2 -> 0xFF4A3320;
            case 3 -> 0xFF1C4539;
            default -> 0xFF30264F;
        };
    }

    public String accentName() {
        return switch (accentPreset) {
            case 1 -> "CYAN";
            case 2 -> "AMBER";
            case 3 -> "MINT";
            default -> "VIOLET";
        };
    }
}
