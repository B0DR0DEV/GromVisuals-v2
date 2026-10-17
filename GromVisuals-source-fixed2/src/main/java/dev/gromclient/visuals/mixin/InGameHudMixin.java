package dev.gromclient.visuals.mixin;

import dev.gromclient.visuals.config.GromConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void grom$replaceVanillaCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (GromConfig.get().customCrosshairEnabled) ci.cancel();
    }
}
