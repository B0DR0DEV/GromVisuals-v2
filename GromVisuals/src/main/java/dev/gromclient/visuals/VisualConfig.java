package dev.gromclient.visuals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;

/** Persistent client-side settings for GromVisuals. */
public final class VisualConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = MinecraftClient.getInstance().runDirectory.toPath()
            .resolve("config").resolve("grom-visuals.json");

    // HUD modules
    public boolean fps = true;
    public boolean coordinates = true;
    public boolean cps = true;
    public boolean keystrokes = true;
    public boolean direction = true;
    public boolean ping = true;
    public boolean clock = false;
    public boolean speed = false;
    public boolean biome = false;
    public boolean armor = true;
    public boolean durability = false;
    public boolean potionEffects = true;
    public boolean crosshair = false;
    public boolean keystrokeMouseButtons = true;
    public boolean hudTitle = true;
    public boolean showInScreens = false;
    public boolean saturationHud = false;
    public boolean armorNotifier = true;
    public boolean targetHud = true;
    public boolean lowHealthWarning = false;
    public boolean miniMap = true;
    public int miniMapZoom = 1;
    public float zoomLevel = 3.0f;
    public transient float zoomProgress = 0.0f;
    private transient int cosmeticTickCounter = 0;
    public boolean editHud = false;
    public boolean customCrosshair = false;
    public int crosshairColor = 0xFFB66BFF;
    public int crosshairCellSize = 5;
    public int[] crosshairPixels = new int[441];
    public int infoX, infoY, cpsX, cpsY, keysX, keysY, armorX, armorY;
    public int durabilityX, durabilityY, effectsX, effectsY, crosshairX, crosshairY;
    public int targetX, targetY, saturationX, saturationY, warningX, warningY;

    // Appearance
    public int accent = 0xFFB66BFF;
    public String theme = "Purple";
    public float hudScale = 1.0f;
    public int panelAlpha = 185;
    public boolean compactHud = false;
    public boolean animations = true;
    public boolean cosmeticChinaHat = false;
    public boolean cosmeticHalo = false;
    public boolean cosmeticWings = false;
    public boolean cosmeticDevilHorns = false;
    public boolean cosmeticParticles = false;

    private transient Deque<Long> leftClicks = new ArrayDeque<>();
    private transient Deque<Long> rightClicks = new ArrayDeque<>();
    private transient long leftClickUntil;
    private transient long rightClickUntil;

    public void tick(MinecraftClient client) {
        if (leftClicks == null) leftClicks = new ArrayDeque<>();
        if (rightClicks == null) rightClicks = new ArrayDeque<>();
        long now = System.currentTimeMillis();
        trimClicks(leftClicks, now);
        trimClicks(rightClicks, now);

        // Client-only animated cosmetic effects. They are local particles and never modify the server/world state.
        if (client.player != null && client.world != null && ++cosmeticTickCounter % 3 == 0) {
            spawnCosmeticEffects(client);
        }
    }

    public void tickZoom(boolean held) {
        float target = held ? 1.0f : 0.0f;
        zoomProgress += (target - zoomProgress) * 0.28f;
        if (Math.abs(target - zoomProgress) < 0.003f) zoomProgress = target;
    }

    public void adjustZoom(double wheelDelta) {
        if (wheelDelta == 0) return;
        zoomLevel = Math.max(1.0f, Math.min(8.0f, zoomLevel + (wheelDelta > 0 ? 0.5f : -0.5f)));
        save();
    }

    private void spawnCosmeticEffects(MinecraftClient client) {
        var player = client.player;
        double time = (client.world.getTime() + cosmeticTickCounter * 0.33) * 0.22;
        double yaw = Math.toRadians(player.getYaw());
        double backX = -Math.sin(yaw), backZ = Math.cos(yaw);
        double sideX = Math.cos(yaw), sideZ = Math.sin(yaw);
        if (cosmeticHalo) {
            double a = time;
            client.world.addParticleClient(net.minecraft.particle.ParticleTypes.END_ROD,
                    player.getX() + Math.cos(a) * 0.34, player.getY() + 2.12 + Math.sin(a) * 0.035,
                    player.getZ() + Math.sin(a) * 0.34, 0, 0.015, 0);
            client.world.addParticleClient(net.minecraft.particle.ParticleTypes.END_ROD,
                    player.getX() + Math.cos(a + Math.PI) * 0.34, player.getY() + 2.12 - Math.sin(a) * 0.035,
                    player.getZ() + Math.sin(a + Math.PI) * 0.34, 0, 0.015, 0);
        }
        if (cosmeticChinaHat && cosmeticTickCounter % 6 == 0) {
            client.world.addParticleClient(net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER,
                    player.getX() + (Math.random() - 0.5) * 0.55, player.getY() + 2.0,
                    player.getZ() + (Math.random() - 0.5) * 0.55, 0, 0.015, 0);
        }
        if (cosmeticWings) {
            for (int sign : new int[]{-1, 1}) {
                client.world.addParticleClient(net.minecraft.particle.ParticleTypes.SOUL,
                        player.getX() + backX * 0.28 + sideX * 0.34 * sign, player.getY() + 1.15 + Math.sin(time * 1.7) * 0.16,
                        player.getZ() + backZ * 0.28 + sideZ * 0.34 * sign, 0, 0.015, 0);
            }
        }
        if (cosmeticDevilHorns && cosmeticTickCounter % 5 == 0) {
            for (int sign : new int[]{-1, 1}) {
                client.world.addParticleClient(net.minecraft.particle.ParticleTypes.FLAME,
                        player.getX() + sideX * 0.17 * sign, player.getY() + 2.03,
                        player.getZ() + sideZ * 0.17 * sign, 0, 0.025, 0);
            }
        }
        if (cosmeticParticles && cosmeticTickCounter % 2 == 0) {
            double a = time * 1.6;
            client.world.addParticleClient(net.minecraft.particle.ParticleTypes.ENCHANT,
                    player.getX() + Math.cos(a) * 0.48, player.getY() + 0.9 + Math.sin(a * 1.3) * 0.38,
                    player.getZ() + Math.sin(a) * 0.48, 0, 0.035, 0);
        }
    }

    public void recordMouseClick(int button) {
        if (leftClicks == null) leftClicks = new ArrayDeque<>();
        if (rightClicks == null) rightClicks = new ArrayDeque<>();
        long now = System.currentTimeMillis();
        if (button == 0) { leftClicks.addLast(now); leftClickUntil = now + 140; }
        if (button == 1) { rightClicks.addLast(now); rightClickUntil = now + 140; }
        trimClicks(leftClicks, now);
        trimClicks(rightClicks, now);
    }

    private static void trimClicks(Deque<Long> clicks, long now) {
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000) clicks.removeFirst();
    }

    public int leftCps() { if (leftClicks == null) leftClicks = new ArrayDeque<>(); return leftClicks.size(); }
    public int rightCps() { if (rightClicks == null) rightClicks = new ArrayDeque<>(); return rightClicks.size(); }
    public boolean leftPressed() { return System.currentTimeMillis() < leftClickUntil; }
    public boolean rightPressed() { return System.currentTimeMillis() < rightClickUntil; }

    public void cycleTheme() {
        switch (theme) {
            case "GROM" -> { theme = "Purple"; accent = 0xFFB66BFF; }
            case "Purple" -> { theme = "Neon"; accent = 0xFF35E6FF; }
            case "Neon" -> { theme = "White"; accent = 0xFFFFFFFF; }
            default -> { theme = "GROM"; accent = 0xFFFF7A35; }
        }
        save();
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this));
        } catch (IOException e) {
            System.err.println("[GromVisuals] Не удалось сохранить конфиг: " + e.getMessage());
        }
    }

    public void load() {
        try {
            if (Files.exists(FILE)) {
                VisualConfig loaded = GSON.fromJson(Files.readString(FILE), VisualConfig.class);
                if (loaded != null) copySettings(loaded);
            }
        } catch (Exception e) {
            System.err.println("[GromVisuals] Конфиг не прочитан, используются значения по умолчанию: " + e.getMessage());
        }
    }

    private void copySettings(VisualConfig c) {
        fps=c.fps; coordinates=c.coordinates; cps=c.cps; keystrokes=c.keystrokes; direction=c.direction;
        ping=c.ping; clock=c.clock; speed=c.speed; biome=c.biome; armor=c.armor; durability=c.durability;
        potionEffects=c.potionEffects; crosshair=c.crosshair; keystrokeMouseButtons=c.keystrokeMouseButtons;
        hudTitle=c.hudTitle; showInScreens=c.showInScreens; saturationHud=c.saturationHud;
        armorNotifier=c.armorNotifier; targetHud=c.targetHud; lowHealthWarning=c.lowHealthWarning; miniMap=c.miniMap; miniMapZoom=Math.max(1,Math.min(3,c.miniMapZoom)); zoomLevel=Math.max(1.0f, Math.min(8.0f, c.zoomLevel <= 0 ? 3.0f : c.zoomLevel)); zoomProgress=0.0f; accent=c.accent;
        theme=c.theme == null ? "Purple" : c.theme; hudScale=clamp(c.hudScale, 0.6f, 1.6f);
        panelAlpha=Math.max(70, Math.min(255, c.panelAlpha)); compactHud=c.compactHud;
        animations=c.animations; cosmeticChinaHat=c.cosmeticChinaHat; cosmeticHalo=c.cosmeticHalo;
        cosmeticWings=c.cosmeticWings; cosmeticDevilHorns=c.cosmeticDevilHorns; cosmeticParticles=c.cosmeticParticles;
        editHud=false; customCrosshair=c.customCrosshair; crosshairColor=c.crosshairColor;
        crosshairCellSize=Math.max(2,Math.min(10,c.crosshairCellSize));
        crosshairPixels=c.crosshairPixels==null||c.crosshairPixels.length!=441?new int[441]:c.crosshairPixels.clone();
        infoX=c.infoX;infoY=c.infoY;cpsX=c.cpsX;cpsY=c.cpsY;keysX=c.keysX;keysY=c.keysY;
        armorX=c.armorX;armorY=c.armorY;durabilityX=c.durabilityX;durabilityY=c.durabilityY;
        effectsX=c.effectsX;effectsY=c.effectsY;crosshairX=c.crosshairX;crosshairY=c.crosshairY;
        targetX=c.targetX;targetY=c.targetY;saturationX=c.saturationX;saturationY=c.saturationY;
        warningX=c.warningX;warningY=c.warningY;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
