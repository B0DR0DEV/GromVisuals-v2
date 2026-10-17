package dev.gromclient.visuals.mixin;

import dev.gromclient.visuals.GromVisualsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Mouse wheel changes zoom strength only while C is held; otherwise vanilla scroll behaviour is untouched. */
@Mixin(Mouse.class)
public abstract class MouseZoomMixin {
    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void gromVisuals$adjustZoom(long window, double horizontal, double vertical, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (GromVisualsClient.isZoomKeyHeld() && client.currentScreen == null && client.player != null && vertical != 0) {
            GromVisualsClient.CONFIG.adjustZoom(vertical);
            ci.cancel();
        }
    }
}
