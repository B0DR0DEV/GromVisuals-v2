package dev.gromclient.visuals.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.PressableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Re-skins title-screen buttons without changing their original click actions. */
@Mixin(PressableWidget.class)
public abstract class PressableWidgetMixin {
    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void gromVisuals$glassButtons(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.currentScreen instanceof TitleScreen)) return;

        PressableWidget button = (PressableWidget) (Object) this;
        int x = button.getX();
        int y = button.getY();
        int w = button.getWidth();
        int h = button.getHeight();
        boolean hovered = button.isHovered();
        boolean enabled = button.active;

        int fill = !enabled ? 0x6810141B : hovered ? 0xC32B1D40 : 0xA90B0D17;
        int edge = !enabled ? 0x765C5268 : hovered ? 0xFFE0B6FF : 0xA66D4A8A;
        int text = enabled ? (hovered ? 0xFFFFFFFF : 0xFFF0EAF9) : 0xFF958C9E;

        context.fill(x, y, x + w, y + h, fill);
        context.fill(x, y, x + w, y + 1, edge);
        context.fill(x, y + h - 1, x + w, y + h, edge);
        context.fill(x, y, x + 1, y + h, edge);
        context.fill(x + w - 1, y, x + w, y + h, edge);
        if (hovered && enabled) {
            context.fill(x + 2, y + 2, x + w - 2, y + 3, 0x775B3A7A);
        }
        context.drawCenteredTextWithShadow(client.textRenderer, button.getMessage(), x + w / 2, y + (h - 8) / 2, text);
        ci.cancel();
    }
}
