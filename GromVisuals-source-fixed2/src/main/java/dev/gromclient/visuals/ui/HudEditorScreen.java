package dev.gromclient.visuals.ui;

import dev.gromclient.visuals.config.GromConfig;
import dev.gromclient.visuals.config.GromConfig.HudPosition;
import dev.gromclient.visuals.render.UiDraw;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/** Drag-and-drop placement editor with persistent HUD coordinates. */
public final class HudEditorScreen extends Screen {
    private record Widget(String id, String label, int width, int height) { }
    private static final List<Widget> WIDGETS = List.of(
            new Widget("info", "GromVisuals  •  FPS 144  •  Ping 28 ms", 214, 26),
            new Widget("coordinates", "XYZ  1699.5  64  600.2", 214, 25),
            new Widget("effects", "ACTIVE EFFECTS  •  4:28", 194, 64),
            new Widget("keystrokes", "W  A  S  D  •  SPACE", 110, 64),
            new Widget("clicks", "CPS  •  LMB / RMB", 116, 52),
            new Widget("armor", "ARMOR  •  DURABILITY", 116, 84),
            new Widget("inventory", "INVENTORY  •  9 × 3", 220, 95),
            new Widget("target", "TARGET  •  HEALTH", 190, 50),
            new Widget("minimap", "LOCAL MAP", 100, 110),
            new Widget("vitals", "VITALS  •  HP / FOOD / SAT", 218, 48),
            new Widget("biome", "BIOME  •  FOREST", 184, 25)
    );

    private final Screen parent;
    private String selectedId;
    private String draggingId;
    private int dragOffsetX, dragOffsetY;
    private int resetX, resetY, doneX, doneY;

    public HudEditorScreen() { this(null); }
    public HudEditorScreen(Screen parent) { super(Text.literal("GromVisuals HUD Editor")); this.parent = parent; }

    @Override
    public void render(DrawContext c, int mouseX, int mouseY, float delta) {
        GromConfig cfg = GromConfig.get();
        c.fill(0, 0, width, height, 0xC6000000);
        UiDraw.panel(c, 22, 22, width - 44, 64, 246);
        UiDraw.rounded(c, 38, 37, 32, 32, 8, cfg.accentColor);
        UiDraw.centered(c, textRenderer, "↕", 54, 49, 0xFFFFFFFF);
        UiDraw.label(c, textRenderer, "HUD EDITOR", 83, 39, 0xFFF5F3FF);
        UiDraw.label(c, textRenderer, "Drag modules to position them · saved automatically", 83, 57, 0xFF9895A8);
        resetX = width - 250; resetY = 39; doneX = width - 125; doneY = 39;
        button(c, resetX, resetY, 112, 32, "Reset layout", mouseX, mouseY, false);
        button(c, doneX, doneY, 88, 32, "Done", mouseX, mouseY, true);

        for (Widget widget : WIDGETS) {
            HudPosition p = cfg.position(widget.id());
            int x = cfg.x(widget.id(), width);
            int y = resolveY(p.y);
            boolean selected = widget.id().equals(selectedId);
            int bg = selected ? 0xEE282333 : 0xD817171D;
            UiDraw.rounded(c, x, y, widget.width(), widget.height(), 7, bg);
            UiDraw.rounded(c, x, y, 3, widget.height(), 2, selected ? 0xFFFFC247 : cfg.accentColor);
            UiDraw.label(c, textRenderer, widget.label(), x + 10, y + 8, selected ? 0xFFFFFFFF : 0xFFE2DEEC);
            if (widget.id().equals("effects")) UiDraw.label(c, textRenderer, "Resistance       04:28", x + 10, y + 28, 0xFFB8B3C4);
            if (widget.id().equals("keystrokes")) UiDraw.label(c, textRenderer, "LMB   RMB   C   SHIFT", x + 10, y + 31, 0xFFB8B3C4);
            if (widget.id().equals("vitals")) {
                UiDraw.rounded(c, x + 8, y + 27, 196, 5, 2, 0xFF3B3541);
                UiDraw.rounded(c, x + 8, y + 27, 142, 5, 2, 0xFF75D6A5);
            }
            if (widget.id().equals("biome")) UiDraw.label(c, textRenderer, "FOREST", x + 57, y + 8, 0xFFE2DEEC);
            if (widget.id().equals("inventory")) {
                for (int i = 0; i < 9; i++) UiDraw.rounded(c, x + 8 + i * 22, y + 36, 18, 18, 3, 0xFF292930);
                for (int i = 0; i < 9; i++) UiDraw.rounded(c, x + 8 + i * 22, y + 58, 18, 18, 3, 0xFF292930);
            }
            if (selected) UiDraw.outline(c, x - 2, y - 2, widget.width() + 4, widget.height() + 4, 0xFFFFC247);
        }
        UiDraw.rounded(c, 24, height - 36, 222, 22, 6, 0xCC101015);
        UiDraw.label(c, textRenderer, selectedId == null ? "Select a module to move it" : "Selected: " + selectedId, 34, height - 28, 0xFFCAC5D7);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x(), mouseY = click.y();
        int button = click.button();
        if (button != 0) return super.mouseClicked(click, doubled);
        if (UiDraw.inside(mouseX, mouseY, doneX, doneY, 88, 32)) { close(); return true; }
        if (UiDraw.inside(mouseX, mouseY, resetX, resetY, 112, 32)) { GromConfig.get().resetPositions(); selectedId = null; return true; }
        GromConfig cfg = GromConfig.get();
        for (Widget widget : WIDGETS) {
            int x = cfg.x(widget.id(), width);
            int y = resolveY(cfg.position(widget.id()).y);
            if (UiDraw.inside(mouseX, mouseY, x, y, widget.width(), widget.height())) {
                selectedId = widget.id(); draggingId = widget.id();
                dragOffsetX = (int)mouseX - x;
                dragOffsetY = (int)mouseY - y;
                // Convert right/bottom anchored defaults to concrete coordinates on first drag.
                cfg.position(widget.id()).x = x;
                cfg.position(widget.id()).y = y;
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        double mouseX = click.x() + deltaX, mouseY = click.y() + deltaY;
        int button = click.button();
        if (button == 0 && draggingId != null) {
            Widget widget = WIDGETS.stream().filter(w -> w.id().equals(draggingId)).findFirst().orElse(null);
            if (widget != null) {
                HudPosition p = GromConfig.get().position(draggingId);
                p.x = Math.max(0, Math.min(width - widget.width(), (int)mouseX - dragOffsetX));
                p.y = Math.max(0, Math.min(height - widget.height(), (int)mouseY - dragOffsetY));
            }
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        int button = click.button();
        if (button == 0 && draggingId != null) {
            draggingId = null;
            GromConfig.save();
            return true;
        }
        return super.mouseReleased(click);
    }

    private int resolveY(int y) { return y < 0 ? Math.max(0, height + y) : y; }

    private void button(DrawContext c, int x, int y, int w, int h, String label, int mx, int my, boolean primary) {
        boolean hover = UiDraw.inside(mx, my, x, y, w, h);
        UiDraw.rounded(c, x, y, w, h, 7, primary ? (hover ? 0xFFA78BFA : 0xFF8B5CF6) : (hover ? 0xFF36313F : 0xFF25232B));
        UiDraw.centered(c, textRenderer, label, x + w / 2, y + (h - 8) / 2, 0xFFF5F3FF);
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
