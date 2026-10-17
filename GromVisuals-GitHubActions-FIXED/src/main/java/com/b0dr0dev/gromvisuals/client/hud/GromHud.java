package com.b0dr0dev.gromvisuals.client.hud;

import com.b0dr0dev.gromvisuals.client.config.VisualConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Locale;

public final class GromHud {
    private static long startMillis = System.currentTimeMillis();

    private GromHud() {}

    public static void render(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        VisualConfig config = VisualConfig.get();
        if (!config.hudEnabled || mc.options.hideGui || mc.player == null || mc.level == null) return;

        int accent = config.accent();
        int soft = config.accentSoft();
        float pulse = config.animations ? (float) (0.5 + 0.5 * Math.sin((System.currentTimeMillis() - startMillis) / 620.0)) : 0.5f;
        int glow = config.animations ? withAlpha(accent, 20 + (int) (18 * pulse)) : withAlpha(accent, 22);
        int gap = config.compactHud ? 5 : 8;

        if (config.showBrand || config.showFps || config.showPing) {
            drawStatusBar(graphics, mc, config, accent, soft, glow);
        }

        int y = config.compactHud ? 54 : 64;
        if (config.showBiome) {
            String biome = getBiomeName(mc);
            drawPill(graphics, 16, y, Math.max(154, mc.font.width(biome) + 72), 29, accent, glow);
            HudDraw.roundRect(graphics, 25, y + 9, 7, 7, 2, accent);
            HudDraw.text(graphics, "BIOME", 39, y + 5, 0xFF9198AB);
            HudDraw.text(graphics, biome, 82, y + 5, 0xFFF3F4FA);
        }

        if (config.showCoordinates) {
            BlockPos pos = mc.player.blockPosition();
            String xyz = String.format(Locale.ROOT, "%d   %d   %d", pos.getX(), pos.getY(), pos.getZ());
            int width = Math.max(222, mc.font.width(xyz) + 86);
            int x = (mc.getWindow().getGuiScaledWidth() - width) / 2;
            drawPill(graphics, x, 16, width, 30, accent, glow);
            HudDraw.text(graphics, "XYZ", x + 13, 7 + 16, accent);
            HudDraw.text(graphics, xyz, x + 49, 23, 0xFFF0F2F8);
        }

        if (config.showWorldTime) {
            long dayTime = Math.floorMod(mc.level.getDayTime(), 24000L);
            long totalMinutes = (dayTime + 6000L) * 60L / 1000L;
            String time = String.format(Locale.ROOT, "%02d:%02d", (totalMinutes / 60L) % 24L, totalMinutes % 60L);
            int x = (mc.getWindow().getGuiScaledWidth() - 86) / 2;
            drawPill(graphics, x, 54, 86, 26, accent, glow);
            HudDraw.text(graphics, "☼  " + time, x + 15, 63, 0xFFF3F4FA);
        }

        if (config.showKeystrokes) {
            drawKeystrokes(graphics, mc, accent, glow, gap);
        }
    }

    private static void drawStatusBar(GuiGraphics graphics, Minecraft mc, VisualConfig cfg, int accent, int soft, int glow) {
        int x = 16;
        int y = 16;
        int width = 262;
        if (cfg.showFps) width += 73;
        if (cfg.showPing) width += 72;
        if (!cfg.showBrand) width -= 100;
        width = Math.max(104, width);
        HudDraw.roundRect(graphics, x, y, width, 39, 10, glow);
        HudDraw.roundRect(graphics, x, y, width, 37, 9, 0xF40C0E16);
        HudDraw.roundRect(graphics, x + 1, y + 1, 3, 35, 2, accent);

        int cursor = x + 13;
        if (cfg.showBrand) {
            HudDraw.roundRect(graphics, cursor, y + 8, 21, 21, 6, soft);
            HudDraw.text(graphics, "G", cursor + 6, y + 14, accent);
            HudDraw.text(graphics, "GROM", cursor + 28, y + 7, 0xFFF4F5FB);
            HudDraw.text(graphics, "VISUALS", cursor + 28, y + 21, 0xFF8A91A5);
            cursor += 101;
        }
        if (cfg.showFps) {
            int fps = mc.getFps();
            HudDraw.text(graphics, Integer.toString(fps), cursor, y + 13, accent);
            HudDraw.text(graphics, "FPS", cursor + mc.font.width(Integer.toString(fps)) + 4, y + 14, 0xFFB9BECC);
            cursor += 73;
        }
        if (cfg.showPing) {
            int ping = getPing(mc);
            HudDraw.roundRect(graphics, cursor, y + 14, 5, 5, 2, ping < 80 ? 0xFF70E1B5 : ping < 160 ? 0xFFFFC86A : 0xFFFF6B7A);
            HudDraw.text(graphics, ping + " ms", cursor + 11, y + 14, 0xFFD0D4DF);
        }
    }

    private static void drawKeystrokes(GuiGraphics graphics, Minecraft mc, int accent, int glow, int gap) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int x = screenWidth - 115;
        int y = 17;
        int w = 99;
        HudDraw.roundRect(graphics, x - 1, y - 1, w + 2, 121, 9, glow);
        HudDraw.roundRect(graphics, x, y, w, 119, 8, 0xE90D1018);
        HudDraw.text(graphics, "KEYS", x + 9, y + 7, 0xFF858DA2);
        drawKey(graphics, x + 35, y + 24, 29, "W", mc.options.keyUp.isDown(), accent);
        drawKey(graphics, x + 5, y + 56, 29, "A", mc.options.keyLeft.isDown(), accent);
        drawKey(graphics, x + 35, y + 56, 29, "S", mc.options.keyDown.isDown(), accent);
        drawKey(graphics, x + 65, y + 56, 29, "D", mc.options.keyRight.isDown(), accent);
        drawKey(graphics, x + 5, y + 88, 43, "JUMP", mc.options.keyJump.isDown(), accent);
        drawKey(graphics, x + 52, y + 88, 42, "SNEAK", mc.options.keyShift.isDown(), accent);
    }

    private static void drawKey(GuiGraphics graphics, int x, int y, int w, String label, boolean active, int accent) {
        HudDraw.roundRect(graphics, x, y, w, 25, 5, active ? withAlpha(accent, 78) : 0xFF232633);
        if (active) HudDraw.roundRect(graphics, x, y + 1, w, 2, 1, accent);
        int textWidth = Minecraft.getInstance().font.width(label);
        HudDraw.text(graphics, label, x + (w - textWidth) / 2, y + 8, active ? 0xFFFFFFFF : 0xFFB6BBC9);
    }

    private static void drawPill(GuiGraphics graphics, int x, int y, int w, int h, int accent, int glow) {
        HudDraw.roundRect(graphics, x - 1, y - 1, w + 2, h + 2, h / 2, glow);
        HudDraw.roundRect(graphics, x, y, w, h, h / 2, 0xED0D1018);
        HudDraw.roundRect(graphics, x + 1, y + 1, w - 2, 2, 1, withAlpha(accent, 70));
    }

    private static String getBiomeName(Minecraft mc) {
        try {
            ResourceKey<Biome> key = mc.level.getBiome(mc.player.blockPosition()).unwrapKey().orElse(null);
            if (key == null) return "UNKNOWN";
            String path = key.location().getPath().replace('_', ' ').replace('/', ' ');
            String[] words = path.split(" ");
            StringBuilder out = new StringBuilder();
            for (String word : words) {
                if (word.isBlank()) continue;
                if (!out.isEmpty()) out.append(' ');
                out.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
            return out.toString();
        } catch (Exception ignored) {
            return "UNKNOWN";
        }
    }

    private static int getPing(Minecraft mc) {
        try {
            if (mc.getConnection() == null || mc.player == null) return 0;
            PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
            return info == null ? 0 : Math.max(0, info.getLatency());
        } catch (Exception ignored) {
            return 0;
        }
    }

    private static int withAlpha(int argb, int alpha) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (argb & 0x00FFFFFF);
    }
}
