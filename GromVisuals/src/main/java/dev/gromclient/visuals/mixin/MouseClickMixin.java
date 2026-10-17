package dev.gromclient.visuals.mixin;

import dev.gromclient.visuals.GromVisualsClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseClickMixin {
    @Inject(method = "onMouseButton", at = @At("TAIL"))
    private void gromVisuals$trackClicks(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action != GLFW.GLFW_PRESS) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null && client.player != null) {
            GromVisualsClient.CONFIG.recordMouseClick(button);
        }
    }
}
