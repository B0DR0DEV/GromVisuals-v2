package dev.gromclient.visuals.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Grom Visuals title-screen branding and a restrained violet treatment over the vanilla panorama. */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    private static final int PURPLE = 0xFFB66BFF;
    private static final int WHITE = 0xFFF7F2FF;
    private static final int MUTED = 0xFFC8BED8;

    @Inject(method = "renderBackground", at = @At("TAIL"))
    private void gromVisuals$tintPanorama(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        int w = client.getWindow().getScaledWidth();
        int h = client.getWindow().getScaledHeight();
        // Preserve the rotating Minecraft panorama, adding a dark violet cinematic overlay.
        context.fill(0, 0, w, h, 0x5A080715);
        context.fill(0, 0, w, h / 3, 0x1F4C1B78);
        context.fill(0, h * 2 / 3, w, h, 0x38050814);
        for (int y = 0; y < h; y += 6) {
            context.fill(0, y, w, y + 1, 0x092A1645);
        }
        context.fill(0, 0, w, 2, 0xCCB66BFF);
        context.fill(0, h - 2, w, h, 0x883D2360);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void gromVisuals$drawBrand(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        int w = client.getWindow().getScaledWidth();
        int h = client.getWindow().getScaledHeight();
        int center = w / 2;
        int panelW = Math.min(360, w - 28);
        int left = center - panelW / 2;
        int right = left + panelW;

        // The panel sits over the vanilla logo area, creating a clean branded header.
        context.fill(left, 12, right, 69, 0xD10A0B14);
        context.fill(left, 12, right, 14, PURPLE);
        context.fill(left, 14, left + 2, 69, 0xFF5D367E);
        context.fill(right - 2, 14, right, 69, 0xFF5D367E);
        context.drawCenteredTextWithShadow(client.textRenderer, Text.literal("GROM VISUALS"), center, 25, WHITE);
        context.drawCenteredTextWithShadow(client.textRenderer, Text.literal("CLIENT VISUALS  /  HUD  /  CROSSHAIR"), center, 43, MUTED);
        context.fill(center - 67, 59, center + 67, 60, 0xAA75469D);

        String footer = "RIGHT SHIFT  ·  VISUAL CONTROL CENTER";
        int fw = client.textRenderer.getWidth(footer);
        context.fill(10, h - 24, 18 + fw, h - 8, 0xA9080913);
        context.drawTextWithShadow(client.textRenderer, footer, 14, h - 20, MUTED);
        String version = "GROM VISUALS 1.3.0";
        context.drawTextWithShadow(client.textRenderer, version, w - client.textRenderer.getWidth(version) - 12, h - 20, 0xBFB66BFF);
    }
}
