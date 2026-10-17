package com.b0dr0dev.gromvisuals.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Small drawing helpers shared by the in-game HUD and the settings UI. */
public final class HudDraw {
    private HudDraw() {}

    public static void text(GuiGraphics graphics, String value, int x, int y, int color) {
        graphics.drawString(Minecraft.getInstance().font, value, x, y, color, false);
    }

    public static void roundRect(GuiGraphics graphics, int x, int y, int width, int height, int radius, int color) {
        if (width <= 0 || height <= 0) return;
        int r = Math.max(0, Math.min(radius, Math.min(width / 2, height / 2)));
        if (r == 0) {
            graphics.fill(x, y, x + width, y + height, color);
            return;
        }
        graphics.fill(x + r, y, x + width - r, y + height, color);
        graphics.fill(x, y + r, x + width, y + height - r, color);
        for (int dy = 0; dy < r; dy++) {
            int fromCenter = r - dy;
            int dx = r - (int) Math.floor(Math.sqrt((double) r * r - (double) fromCenter * fromCenter));
            if (dx > 0) {
                graphics.fill(x + dx, y + dy, x + width - dx, y + dy + 1, color);
                graphics.fill(x + dx, y + height - dy - 1, x + width - dx, y + height - dy, color);
            }
        }
    }

    public static void outline(GuiGraphics graphics, int x, int y, int width, int height, int radius, int color) {
        roundRect(graphics, x, y, width, height, radius, color);
        roundRect(graphics, x + 1, y + 1, width - 2, height - 2, Math.max(0, radius - 1), 0xF20E111A);
    }

    public static void moduleCard(GuiGraphics graphics, int x, int y, int width, int height, int accent, boolean active) {
        roundRect(graphics, x, y, width, height, 8, active ? 0xF41A1D2A : 0xE9161821);
        graphics.fill(x + 1, y + 9, x + 3, y + height - 9, active ? accent : 0xFF363946);
        graphics.fill(x + 12, y + height - 1, x + width - 12, y + height, 0x182A2D3A);
    }
}
