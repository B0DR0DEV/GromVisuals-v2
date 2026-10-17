package dev.gromclient.visuals.hud;

import dev.gromclient.visuals.GromVisualsClient;
import dev.gromclient.visuals.config.GromConfig;
import dev.gromclient.visuals.input.ZoomController;
import dev.gromclient.visuals.render.UiDraw;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** In-game overlay styled after compact PvP-client HUDs: graphite panels + gold data highlights. */
public final class GromHud {
    private static final int GOLD = 0xFFFFC247;
    private static final int WHITE = 0xFFF5F3FF;
    private static final int MUTED = 0xFF9895A8;
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private GromHud() { }

    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        GromConfig cfg = GromConfig.get();
        if (!cfg.hudEnabled || client.player == null || client.world == null || client.currentScreen != null) return;
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();
        if (cfg.showInfo) renderInfo(context, client, cfg, sw);
        if (cfg.showCoordinates) renderCoordinates(context, client, cfg, sw);
        if (cfg.showPotionEffects) renderEffects(context, client, cfg, sw);
        if (cfg.showKeystrokes) renderKeystrokes(context, client, cfg, sw);
        if (cfg.showClickHud) renderClicks(context, client, cfg, sw);
        if (cfg.showArmor) renderArmor(context, client, cfg, sw, sh);
        if (cfg.showInventory) renderInventory(context, client, cfg, sw, sh);
        if (cfg.showTargetHud) renderTarget(context, client, cfg, sw, sh);
        if (cfg.showMiniMap) renderMiniMap(context, client, cfg, sw);
        if (cfg.showDirection) renderDirection(context, client, sw, sh);
        if (cfg.showVitals) renderVitals(context, client, cfg, sw);
        if (cfg.showBiome) renderBiome(context, client, cfg, sw);
        if (cfg.showHealthWarning && client.player.getHealth() <= 6.0f) {
            UiDraw.rounded(context, sw / 2 - 68, 18, 136, 23, 7, 0xCC4B171E);
            UiDraw.centered(context, client.textRenderer, "LOW HEALTH", sw / 2, 26, 0xFFFF727E);
        }
        if (ZoomController.isActive()) {
            String zoom = String.format(Locale.ROOT, "ZOOM  %.1f×", ZoomController.multiplier());
            UiDraw.rounded(context, sw / 2 - 42, 12, 84, 22, 7, 0xCC141419);
            UiDraw.centered(context, client.textRenderer, zoom, sw / 2, 19, GOLD);
        }
        if (cfg.customCrosshairEnabled && client.currentScreen == null) renderCustomCrosshair(context, sw, sh, cfg);
    }

    private static void renderInfo(DrawContext c, MinecraftClient client, GromConfig cfg, int sw) {
        int x = cfg.x("info", sw), y = cfg.position("info").y;
        String fps = client.getCurrentFps() + " FPS";
        String ping = "— ms";
        if (client.getNetworkHandler() != null && client.player != null) {
            var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            if (entry != null) ping = entry.getLatency() + " ms";
        }
        int width = 214;
        UiDraw.panel(c, x, y, width, 26, cfg.hudOpacity);
        UiDraw.rounded(c, x + 7, y + 6, 14, 14, 4, 0xFFFFC247);
        UiDraw.centered(c, client.textRenderer, "G", x + 14, y + 9, 0xFF17130B);
        UiDraw.label(c, client.textRenderer, "GromVisuals", x + 27, y + 9, WHITE);
        UiDraw.label(c, client.textRenderer, fps, x + 112, y + 9, GOLD);
        UiDraw.label(c, client.textRenderer, "Ping " + ping, x + 157, y + 9, MUTED);
    }

    private static void renderCoordinates(DrawContext c, MinecraftClient client, GromConfig cfg, int sw) {
        var p = client.player;
        int x = cfg.x("coordinates", sw), y = py(cfg.position("coordinates").y, client);
        UiDraw.panel(c, x, y, 214, 25, cfg.hudOpacity);
        UiDraw.label(c, client.textRenderer, "XYZ", x + 9, y + 8, GOLD);
        String coords = String.format(Locale.ROOT, "%.1f  %d  %.1f", p.getX(), p.getBlockY(), p.getZ());
        UiDraw.label(c, client.textRenderer, coords, x + 42, y + 8, WHITE);
    }

    private static void renderEffects(DrawContext c, MinecraftClient client, GromConfig cfg, int sw) {
        List<StatusEffectInstance> effects = new ArrayList<>(client.player.getStatusEffects());
        if (effects.isEmpty()) return;
        int x = cfg.x("effects", sw), y = py(cfg.position("effects").y, client);
        int rows = Math.min(5, effects.size());
        int h = 24 + rows * 18;
        UiDraw.panel(c, x, y, 194, h, cfg.hudOpacity);
        UiDraw.label(c, client.textRenderer, "✦  ACTIVE EFFECTS", x + 10, y + 8, GOLD);
        for (int i = 0; i < rows; i++) {
            StatusEffectInstance effect = effects.get(i);
            String name = effect.getEffectType().value().getName().getString();
            if (name.length() > 16) name = name.substring(0, 15) + "…";
            String duration = effect.getDuration() < 0 ? "∞" : String.format(Locale.ROOT, "%d:%02d", effect.getDuration() / 1200, (effect.getDuration() / 20) % 60);
            int yy = y + 25 + i * 18;
            UiDraw.rounded(c, x + 9, yy, 4, 10, 2, effect.getEffectType().value().isBeneficial() ? 0xFF67D6A3 : 0xFFF08B66);
            UiDraw.label(c, client.textRenderer, name, x + 19, yy + 1, WHITE);
            UiDraw.label(c, client.textRenderer, duration, x + 150, yy + 1, MUTED);
        }
    }

    private static void renderKeystrokes(DrawContext c, MinecraftClient client, GromConfig cfg, int sw) {
        int x = cfg.x("keystrokes", sw), y = py(cfg.position("keystrokes").y, client);
        boolean w = client.options.forwardKey.isPressed();
        boolean a = client.options.leftKey.isPressed();
        boolean s = client.options.backKey.isPressed();
        boolean d = client.options.rightKey.isPressed();
        boolean jump = client.options.jumpKey.isPressed();
        boolean sneak = client.options.sneakKey.isPressed();
        UiDraw.panel(c, x, y, 110, 143, cfg.hudOpacity);
        key(c, client, x + 38, y + 8, 34, 22, "W", w);
        key(c, client, x + 8, y + 34, 28, 22, "A", a);
        key(c, client, x + 40, y + 34, 28, 22, "S", s);
        key(c, client, x + 72, y + 34, 28, 22, "D", d);
        key(c, client, x + 8, y + 60, 45, 22, "LMB", ClickTracker.isLeftHeld());
        key(c, client, x + 57, y + 60, 45, 22, "RMB", ClickTracker.isRightHeld());
        key(c, client, x + 8, y + 86, 94, 20, "SPACE", jump);
        key(c, client, x + 8, y + 110, 45, 22, "C", GromVisualsClient.ZOOM_KEY != null && GromVisualsClient.ZOOM_KEY.isPressed());
        key(c, client, x + 57, y + 110, 45, 22, "SHIFT", sneak);
    }

    private static void key(DrawContext c, MinecraftClient client, int x, int y, int w, int h, String label, boolean active) {
        int fill = active ? 0xFFE5AC34 : 0xCC24242B;
        int text = active ? 0xFF19150C : 0xFFDBD8E4;
        UiDraw.rounded(c, x, y, w, h, 5, fill);
        UiDraw.outline(c, x, y, w, h, active ? 0xFFFFD46B : 0x332D2D35);
        UiDraw.centered(c, client.textRenderer, label, x + w / 2, y + (h - 8) / 2, text);
    }

    private static void renderClicks(DrawContext c, MinecraftClient client, GromConfig cfg, int sw) {
        int x = cfg.x("clicks", sw), y = py(cfg.position("clicks").y, client);
        UiDraw.panel(c, x, y, 116, 52, cfg.hudOpacity);
        UiDraw.label(c, client.textRenderer, "CPS", x + 9, y + 7, GOLD);
        UiDraw.label(c, client.textRenderer, "LMB", x + 9, y + 24, MUTED);
        UiDraw.label(c, client.textRenderer, Integer.toString(ClickTracker.leftCps()), x + 37, y + 24, WHITE);
        UiDraw.label(c, client.textRenderer, "RMB", x + 65, y + 24, MUTED);
        UiDraw.label(c, client.textRenderer, Integer.toString(ClickTracker.rightCps()), x + 91, y + 24, WHITE);
    }

    private static void renderArmor(DrawContext c, MinecraftClient client, GromConfig cfg, int sw, int sh) {
        int x = cfg.x("armor", sw), y = py(cfg.position("armor").y, client);
        UiDraw.panel(c, x, y, 116, 84, cfg.hudOpacity);
        UiDraw.label(c, client.textRenderer, "ARMOR", x + 9, y + 7, GOLD);
        for (int i = 0; i < 4; i++) {
            var stack = client.player.getEquippedStack(ARMOR_SLOTS[i]);
            int sx = x + 8 + i * 26;
            UiDraw.rounded(c, sx, y + 25, 22, 22, 4, 0xAA24242A);
            if (!stack.isEmpty()) {
                c.drawItem(stack, sx + 3, y + 28);
                int max = stack.getMaxDamage();
                if (max > 0) {
                    int remaining = max - stack.getDamage();
                    int bar = Math.max(0, Math.min(16, (int)(16.0 * remaining / max)));
                    c.fill(sx + 3, y + 49, sx + 19, y + 51, 0xFF43383B);
                    c.fill(sx + 3, y + 49, sx + 3 + bar, y + 51, remaining < max * 0.2 ? 0xFFE85D66 : 0xFF73D6A5);
                }
            }
        }
    }

    private static void renderInventory(DrawContext c, MinecraftClient client, GromConfig cfg, int sw, int sh) {
        int x = cfg.x("inventory", sw), y = py(cfg.position("inventory").y, client);
        UiDraw.panel(c, x, y, 220, 95, cfg.hudOpacity);
        UiDraw.label(c, client.textRenderer, "INVENTORY", x + 10, y + 7, GOLD);
        PlayerInventory inventory = client.player.getInventory();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int sx = x + 8 + col * 22;
                int sy = y + 24 + row * 22;
                UiDraw.rounded(c, sx, sy, 20, 20, 4, 0xAA25252B);
                var stack = inventory.getStack(9 + row * 9 + col);
                if (!stack.isEmpty()) c.drawItem(stack, sx + 2, sy + 2);
            }
        }
    }

    private static void renderTarget(DrawContext c, MinecraftClient client, GromConfig cfg, int sw, int sh) {
        HitResult hit = client.crosshairTarget;
        if (!(hit instanceof EntityHitResult entityHit) || !(entityHit.getEntity() instanceof LivingEntity target)) return;
        if (target == client.player || !target.isAlive()) return;
        int x = cfg.x("target", sw), y = py(cfg.position("target").y, client);
        UiDraw.panel(c, x, y, 190, 50, cfg.hudOpacity);
        String name = target.getName().getString();
        if (name.length() > 17) name = name.substring(0, 16) + "…";
        UiDraw.label(c, client.textRenderer, name, x + 10, y + 8, WHITE);
        String hp = String.format(Locale.ROOT, "%.1f / %.1f HP", target.getHealth(), target.getMaxHealth());
        UiDraw.label(c, client.textRenderer, hp, x + 10, y + 25, MUTED);
        int barW = 80;
        c.fill(x + 101, y + 29, x + 101 + barW, y + 35, 0xFF3A3037);
        int fill = Math.max(0, Math.min(barW, (int)(barW * target.getHealth() / Math.max(1.0f, target.getMaxHealth()))));
        c.fill(x + 101, y + 29, x + 101 + fill, y + 35, target.getHealth() <= 6 ? 0xFFFF6876 : 0xFF71D79D);
    }

    private static void renderMiniMap(DrawContext c, MinecraftClient client, GromConfig cfg, int sw) {
        int x = cfg.x("minimap", sw), y = py(cfg.position("minimap").y, client);
        int cell = 4;
        int mapSize = MiniMapCache.CELLS * cell;
        UiDraw.panel(c, x, y, mapSize + 16, mapSize + 30, cfg.hudOpacity);
        UiDraw.label(c, client.textRenderer, "LOCAL MAP", x + 8, y + 7, GOLD);
        int mapX = x + 8, mapY = y + 20;
        for (int row = 0; row < MiniMapCache.CELLS; row++) {
            for (int col = 0; col < MiniMapCache.CELLS; col++) {
                int color = MiniMapCache.colorAt(col, row);
                c.fill(mapX + col * cell, mapY + row * cell, mapX + (col + 1) * cell, mapY + (row + 1) * cell, color);
            }
        }
        int centerX = mapX + (MiniMapCache.CELLS / 2) * cell;
        int centerY = mapY + (MiniMapCache.CELLS / 2) * cell;
        c.fill(centerX - 2, centerY - 2, centerX + 3, centerY + 3, 0xFFFFFFFF);
        double yaw = Math.toRadians(client.player.getYaw());
        int dx = (int)Math.round(-Math.sin(yaw) * 5.0);
        int dz = (int)Math.round(Math.cos(yaw) * 5.0);
        c.fill(centerX + dx - 1, centerY + dz - 1, centerX + dx + 2, centerY + dz + 2, GOLD);
    }

    private static void renderDirection(DrawContext c, MinecraftClient client, int sw, int sh) {
        float yaw = client.player.getYaw();
        String[] dirs = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
        int index = Math.floorMod(Math.round(yaw / 45.0f), 8);
        String text = dirs[index] + "  " + String.format(Locale.ROOT, "%.0f°", (yaw % 360 + 360) % 360);
        UiDraw.rounded(c, sw / 2 - 32, 38, 64, 20, 6, 0xAA141419);
        UiDraw.centered(c, client.textRenderer, text, sw / 2, 44, GOLD);
    }


    private static void renderVitals(DrawContext c, MinecraftClient client, GromConfig cfg, int sw) {
        int x = cfg.x("vitals", sw), y = py(cfg.position("vitals").y, client);
        float health = Math.max(0.0f, client.player.getHealth());
        float maxHealth = Math.max(1.0f, client.player.getMaxHealth());
        int food = client.player.getHungerManager().getFoodLevel();
        float saturation = client.player.getHungerManager().getSaturationLevel();
        UiDraw.panel(c, x, y, 218, 48, cfg.hudOpacity);
        UiDraw.label(c, client.textRenderer, "VITALS", x + 9, y + 7, GOLD);
        UiDraw.label(c, client.textRenderer, String.format(Locale.ROOT, "%.1f HP", health), x + 63, y + 7, WHITE);
        UiDraw.label(c, client.textRenderer, String.format(Locale.ROOT, "FOOD %d  SAT %.1f", food, saturation), x + 112, y + 7, MUTED);
        int barWidth = 198;
        c.fill(x + 10, y + 25, x + 10 + barWidth, y + 30, 0xFF36313B);
        c.fill(x + 10, y + 25, x + 10 + Math.max(0, Math.min(barWidth, (int)(barWidth * health / maxHealth))), y + 30,
                health <= 6.0f ? 0xFFFF6876 : 0xFF75D6A5);
        c.fill(x + 10, y + 36, x + 10 + barWidth, y + 40, 0xFF36313B);
        c.fill(x + 10, y + 36, x + 10 + Math.max(0, Math.min(barWidth, Math.round(barWidth * food / 20.0f))), y + 40, GOLD);
    }

    private static void renderBiome(DrawContext c, MinecraftClient client, GromConfig cfg, int sw) {
        int x = cfg.x("biome", sw), y = py(cfg.position("biome").y, client);
        String biome = client.world.getBiome(client.player.getBlockPos()).getKey()
                .map(key -> key.getValue().getPath())
                .orElse("unknown")
                .replace('_', ' ').toUpperCase(Locale.ROOT);
        int maxLength = 20;
        if (biome.length() > maxLength) biome = biome.substring(0, maxLength - 1) + "…";
        UiDraw.panel(c, x, y, 184, 25, cfg.hudOpacity);
        UiDraw.label(c, client.textRenderer, "BIOME", x + 9, y + 8, GOLD);
        UiDraw.label(c, client.textRenderer, biome, x + 51, y + 8, WHITE);
    }

    private static void renderCustomCrosshair(DrawContext c, int sw, int sh, GromConfig cfg) {
        int cell = cfg.crosshairSize;
        int startX = sw / 2 - 21 * cell / 2;
        int startY = sh / 2 - 21 * cell / 2;
        boolean[] pixels = cfg.crosshairPixels;
        for (int row = 0; row < 21; row++) {
            for (int col = 0; col < 21; col++) {
                if (pixels[row * 21 + col]) {
                    c.fill(startX + col * cell, startY + row * cell,
                            startX + (col + 1) * cell, startY + (row + 1) * cell, cfg.crosshairColor);
                }
            }
        }
    }

    private static int py(int y, MinecraftClient client) {
        return y < 0 ? Math.max(0, client.getWindow().getScaledHeight() + y) : y;
    }
}
