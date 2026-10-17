package dev.gromclient.visuals.mixin;

import dev.gromclient.visuals.input.ZoomController;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "getFov(Lnet/minecraft/client/render/Camera;FZ)F", at = @At("RETURN"), cancellable = true)
    private void grom$applyZoom(Camera camera, float tickProgress, boolean changingFov,
                                CallbackInfoReturnable<Float> cir) {
        float multiplier = ZoomController.multiplier();
        if (multiplier > 1.01f) cir.setReturnValue(cir.getReturnValue() / multiplier);
    }
}
