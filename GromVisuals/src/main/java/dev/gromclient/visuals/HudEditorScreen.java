package dev.gromclient.visuals;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/** Transparent in-world editor screen: Minecraft unlocks the mouse while this screen is active. */
public final class HudEditorScreen extends Screen {
    private static final int WHITE = 0xFFF7F2FF;
    private static final int MUTED = 0xFFC6B8D4;
    private static final int PURPLE = 0xFFB66BFF;

    public HudEditorScreen() {
        super(Text.literal("Grom Visuals — HUD Editor"));
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Do not draw Minecraft's normal dark screen gradient; keep the world visible.
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        // The world remains visible underneath; only add a subtle tint and editor hints.
        context.fill(0, 0, width, height, 0x18080412);
        context.fill(12, 12, 290, 73, 0xC80B0B14);
        context.fill(12, 12, 290, 14, PURPLE);
        context.drawText(textRenderer, "GROM VISUALS  /  HUD EDITOR", 23, 23, WHITE, true);
        context.drawText(textRenderer, "Drag any highlighted HUD widget", 23, 40, MUTED, false);
        context.drawText(textRenderer, "RIGHT SHIFT or ESC  ·  finish editing", 23, 55, MUTED, false);

        int w = Math.min(250, width - 24);
        int x = (width - w) / 2;
        int y = height - 39;
        context.fill(x, y, x + w, y + 27, 0xCE0B0B14);
        context.fill(x, y, x + w, y + 1, PURPLE);
        String hint = "MOUSE FREE  ·  SETTINGS AUTO-SAVE";
        context.drawCenteredTextWithShadow(textRenderer, hint, width / 2, y + 10, WHITE);
    }

    @Override
    public void close() {
        client.setScreen(null);
    }

    @Override
    public void removed() {
        VisualConfig config = GromVisualsClient.CONFIG;
        config.editHud = false;
        config.save();
        super.removed();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
