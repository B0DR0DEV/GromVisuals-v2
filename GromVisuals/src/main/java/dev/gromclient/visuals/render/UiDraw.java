package dev.gromclient.visuals.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;

/** Small immediate-mode UI primitives; built only on supported DrawContext fills/text. */
public final class UiDraw {
    private UiDraw() { }

    public static void rounded(DrawContext c, int x, int y, int w, int h, int radius, int color) {
        if (w <= 0 || h <= 0) return;
        int r = Math.max(0, Math.min(radius, Math.min(w / 2, h / 2)));
        if (r == 0) { c.fill(x, y, x + w, y + h, color); return; }
        c.fill(x + r, y, x + w - r, y + h, color);
        for (int row = 0; row < r; row++) {
            int dy = r - row;
            int inset = r - (int)Math.floor(Math.sqrt(Math.max(0, r * r - dy * dy)));
            c.fill(x + inset, y + row, x + w - inset, y + row + 1, color);
            c.fill(x + inset, y + h - row - 1, x + w - inset, y + h - row, color);
        }
    }

    public static void panel(DrawContext c, int x, int y, int w, int h, int opacity) {
        rounded(c, x + 1, y + 3, w, h, 9, alpha(0xFF000000, 75));
        rounded(c, x, y, w, h, 9, alpha(0xFF141419, opacity));
        c.fill(x + 10, y, x + w - 10, y + 1, 0x22FFFFFF);
    }

    public static void outline(DrawContext c, int x, int y, int w, int h, int color) {
        c.fill(x + 5, y, x + w - 5, y + 1, color);
        c.fill(x + 5, y + h - 1, x + w - 5, y + h, color);
        c.fill(x, y + 5, x + 1, y + h - 5, color);
        c.fill(x + w - 1, y + 5, x + w, y + h - 5, color);
    }

    public static void label(DrawContext c, TextRenderer font, String text, int x, int y, int color) {
        c.drawText(font, text, x, y, color, false);
    }

    public static void centered(DrawContext c, TextRenderer font, String text, int centerX, int y, int color) {
        label(c, font, text, centerX - font.getWidth(text) / 2, y, color);
    }

    public static int alpha(int color, int alpha) {
        return ((Math.max(0, Math.min(255, alpha)) & 255) << 24) | (color & 0x00FFFFFF);
    }

    public static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && my >= y && mx < x + w && my < y + h;
    }
}
