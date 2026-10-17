package dev.gromclient.visuals.ui;

import dev.gromclient.visuals.config.GromConfig;
import dev.gromclient.visuals.render.UiDraw;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** 21 × 21 pixel canvas for an actual in-game crosshair bitmap. */
public final class CrosshairStudioScreen extends Screen {
    private final Screen parent;
    private int cell;
    private int gridX, gridY, gridSize;
    private boolean painting;
    private boolean paintValue;

    public CrosshairStudioScreen() { this(null); }
    public CrosshairStudioScreen(Screen parent) { super(Text.literal("GromVisuals Crosshair Studio")); this.parent = parent; }

    @Override
    public void render(DrawContext c, int mouseX, int mouseY, float delta) {
        GromConfig cfg = GromConfig.get();
        c.fill(0, 0, width, height, 0xD7000000);
        UiDraw.panel(c, 28, 24, width - 56, height - 48, 250);
        UiDraw.label(c, textRenderer, "CROSSHAIR STUDIO", 48, 42, 0xFFF5F3FF);
        UiDraw.label(c, textRenderer, "Paint the grid directly · the result renders in-game", 48, 61, 0xFF9895A8);
        button(c, width - 235, 40, 126, 31, cfg.customCrosshairEnabled ? "Custom: ON" : "Custom: OFF", mouseX, mouseY, true);
        button(c, width - 101, 40, 54, 31, "Done", mouseX, mouseY, false);

        cell = Math.max(8, Math.min(16, (height - 190) / 21));
        gridSize = cell * 21;
        gridX = Math.max(44, width / 2 - 292);
        gridY = Math.max(112, height / 2 - gridSize / 2 + 20);
        UiDraw.label(c, textRenderer, "21 × 21 GRID", gridX, gridY - 20, 0xFFAAA6B8);
        boolean[] pixels = cfg.crosshairPixels;
        for (int row = 0; row < 21; row++) {
            for (int col = 0; col < 21; col++) {
                int x = gridX + col * cell;
                int y = gridY + row * cell;
                int fill = pixels[row * 21 + col] ? cfg.crosshairColor : 0xFF1F1E26;
                c.fill(x, y, x + cell - 1, y + cell - 1, fill);
                if (col == 10 && row == 10) UiDraw.outline(c, x, y, cell - 1, cell - 1, 0x99FFFFFF);
            }
        }
        UiDraw.outline(c, gridX - 1, gridY - 1, gridSize + 1, gridSize + 1, 0x664F4B5D);

        int sx = gridX + gridSize + 40;
        int sy = gridY + 2;
        UiDraw.label(c, textRenderer, "PRESETS", sx, sy, 0xFFAAA6B8);
        button(c, sx, sy + 22, 102, 29, "Plus", mouseX, mouseY, false);
        button(c, sx + 110, sy + 22, 102, 29, "Dot", mouseX, mouseY, false);
        button(c, sx, sy + 58, 102, 29, "X shape", mouseX, mouseY, false);
        button(c, sx + 110, sy + 58, 102, 29, "Ring", mouseX, mouseY, false);
        UiDraw.label(c, textRenderer, "COLOR", sx, sy + 108, 0xFFAAA6B8);
        int[] colors = {0xFFFFFFFF, 0xFFFFC247, 0xFF8B5CF6, 0xFF55D6E8};
        for (int i = 0; i < colors.length; i++) {
            int bx = sx + i * 31;
            UiDraw.rounded(c, bx, sy + 128, 24, 24, 5, colors[i]);
            if (cfg.crosshairColor == colors[i]) UiDraw.outline(c, bx - 2, sy + 126, 28, 28, 0xFFF5F3FF);
        }
        UiDraw.label(c, textRenderer, "PIXEL SCALE  " + cfg.crosshairSize, sx, sy + 170, 0xFFAAA6B8);
        button(c, sx, sy + 188, 102, 29, "Smaller", mouseX, mouseY, false);
        button(c, sx + 110, sy + 188, 102, 29, "Larger", mouseX, mouseY, false);
        button(c, sx, sy + 230, 102, 29, "Clear", mouseX, mouseY, false);
        button(c, sx + 110, sy + 230, 102, 29, "Vanilla", mouseX, mouseY, true);

        int px = width - 156, py = height - 102;
        UiDraw.panel(c, px, py, 110, 66, 234);
        UiDraw.label(c, textRenderer, "PREVIEW", px + 10, py + 8, 0xFF9895A8);
        int previewCell = 1;
        for (int row = 0; row < 21; row++) for (int col = 0; col < 21; col++) {
            if (pixels[row * 21 + col]) {
                c.fill(px + 55 - 10 + col * previewCell, py + 22 + row * previewCell, px + 56 - 10 + col * previewCell, py + 23 + row * previewCell, cfg.crosshairColor);
            }
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x(), mouseY = click.y();
        int button = click.button();
        if (button != 0) return super.mouseClicked(click, doubled);
        GromConfig cfg = GromConfig.get();
        if (UiDraw.inside(mouseX, mouseY, width - 235, 40, 126, 31)) { cfg.customCrosshairEnabled = !cfg.customCrosshairEnabled; GromConfig.save(); return true; }
        if (UiDraw.inside(mouseX, mouseY, width - 101, 40, 54, 31)) { close(); return true; }
        if (insideGrid(mouseX, mouseY)) { paintAt(mouseX, mouseY); painting = true; paintValue = pixelAt(mouseX, mouseY); cfg.crosshairPixels[cellIndex(mouseX, mouseY)] = paintValue; GromConfig.save(); return true; }
        int sx = gridX + gridSize + 40, sy = gridY + 2;
        if (UiDraw.inside(mouseX, mouseY, sx, sy + 22, 102, 29)) applyPreset("plus");
        else if (UiDraw.inside(mouseX, mouseY, sx + 110, sy + 22, 102, 29)) applyPreset("dot");
        else if (UiDraw.inside(mouseX, mouseY, sx, sy + 58, 102, 29)) applyPreset("x");
        else if (UiDraw.inside(mouseX, mouseY, sx + 110, sy + 58, 102, 29)) applyPreset("ring");
        else if (mouseY >= sy + 126 && mouseY <= sy + 156) {
            int index = (int)((mouseX - sx) / 31);
            int[] colors = {0xFFFFFFFF, 0xFFFFC247, 0xFF8B5CF6, 0xFF55D6E8};
            if (index >= 0 && index < colors.length) { cfg.crosshairColor = colors[index]; GromConfig.save(); }
        } else if (UiDraw.inside(mouseX, mouseY, sx, sy + 188, 102, 29)) { cfg.crosshairSize = Math.max(1, cfg.crosshairSize - 1); GromConfig.save(); }
        else if (UiDraw.inside(mouseX, mouseY, sx + 110, sy + 188, 102, 29)) { cfg.crosshairSize = Math.min(5, cfg.crosshairSize + 1); GromConfig.save(); }
        else if (UiDraw.inside(mouseX, mouseY, sx, sy + 230, 102, 29)) { cfg.crosshairPixels = new boolean[441]; GromConfig.save(); }
        else if (UiDraw.inside(mouseX, mouseY, sx + 110, sy + 230, 102, 29)) { cfg.customCrosshairEnabled = false; GromConfig.save(); }
        return true;
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        double mouseX = click.x() + deltaX, mouseY = click.y() + deltaY;
        int button = click.button();
        if (button == 0 && painting && insideGrid(mouseX, mouseY)) {
            GromConfig.get().crosshairPixels[cellIndex(mouseX, mouseY)] = paintValue;
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (click.button() == 0 && painting) { painting = false; GromConfig.save(); return true; }
        return super.mouseReleased(click);
    }

    private boolean insideGrid(double mx, double my) { return mx >= gridX && my >= gridY && mx < gridX + gridSize && my < gridY + gridSize; }
    private int cellIndex(double mx, double my) { int col = Math.max(0, Math.min(20, ((int)mx - gridX) / cell)); int row = Math.max(0, Math.min(20, ((int)my - gridY) / cell)); return row * 21 + col; }
    private boolean pixelAt(double mx, double my) { return GromConfig.get().crosshairPixels[cellIndex(mx, my)]; }
    private void paintAt(double mx, double my) { GromConfig.get().crosshairPixels[cellIndex(mx, my)] = !pixelAt(mx, my); }

    private void applyPreset(String preset) {
        GromConfig cfg = GromConfig.get();
        boolean[] p = new boolean[441];
        if (preset.equals("dot")) p[220] = true;
        else if (preset.equals("x")) {
            for (int i = 6; i <= 14; i++) { p[i * 21 + i] = true; p[i * 21 + 20 - i] = true; }
        } else if (preset.equals("ring")) {
            for (int y = 0; y < 21; y++) for (int x = 0; x < 21; x++) {
                double d = Math.sqrt((x - 10) * (x - 10) + (y - 10) * (y - 10));
                if (d >= 6.2 && d <= 7.5) p[y * 21 + x] = true;
            }
        } else {
            for (int i = 5; i <= 15; i++) if (i < 9 || i > 11) { p[10 * 21 + i] = true; p[i * 21 + 10] = true; }
            p[220] = true;
        }
        cfg.crosshairPixels = p;
        cfg.customCrosshairEnabled = true;
        GromConfig.save();
    }

    private void button(DrawContext c, int x, int y, int w, int h, String text, int mx, int my, boolean primary) {
        boolean hover = UiDraw.inside(mx, my, x, y, w, h);
        UiDraw.rounded(c, x, y, w, h, 7, primary ? (hover ? 0xFFA78BFA : 0xFF8B5CF6) : (hover ? 0xFF36313F : 0xFF25232B));
        UiDraw.centered(c, textRenderer, text, x + w / 2, y + (h - 8) / 2, 0xFFF5F3FF);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) { close(); return true; }
        return super.keyPressed(input);
    }

    @Override
    public void close() { if (client != null) client.setScreen(parent); }

    @Override
    public boolean shouldPause() { return false; }
}
