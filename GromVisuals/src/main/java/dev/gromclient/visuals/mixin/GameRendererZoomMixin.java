package dev.gromclient.visuals.mixin;

import dev.gromclient.visuals.GromVisualsClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Smooth client-side FOV zoom, active only while the configurable Zoom key is held. */
@Mixin(GameRenderer.class)
public abstract class GameRendererZoomMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void gromVisuals$applyZoom(Camera camera, float tickDelta, boolean changingFov,
                                      CallbackInfoReturnable<Float> cir) {
        var config = GromVisualsClient.CONFIG;
        float progress = config.zoomProgress;
        if (progress <= 0.001f) return;
        float multiplier = 1.0f + (config.zoomLevel - 1.0f) * progress;
        cir.setReturnValue(Math.max(5.0f, cir.getReturnValue() / multiplier));
    }
}
