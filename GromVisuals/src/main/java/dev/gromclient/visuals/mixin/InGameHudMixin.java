package dev.gromclient.visuals.mixin;
import dev.gromclient.visuals.GromVisualsClient;import net.minecraft.client.gui.hud.InGameHud;import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.At;import org.spongepowered.asm.mixin.injection.Inject;import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(InGameHud.class) public abstract class InGameHudMixin {
 @Inject(method="renderCrosshair",at=@At("HEAD"),cancellable=true)
 private void gromVisuals$hideVanillaCrosshair(CallbackInfo ci){if(GromVisualsClient.CONFIG.crosshair&&GromVisualsClient.CONFIG.customCrosshair)ci.cancel();}
}
