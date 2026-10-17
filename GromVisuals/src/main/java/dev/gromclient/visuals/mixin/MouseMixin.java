package dev.gromclient.visuals.mixin;

import dev.gromclient.visuals.GromVisualsClient;
import dev.gromclient.visuals.config.GromConfig;
import dev.gromclient.visuals.hud.ClickTracker;
import dev.gromclient.visuals.input.ZoomController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void grom$trackMouse(long window, MouseInput input, int action, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        boolean gameplay = client.player != null && client.world != null && client.currentScreen == null;
        if (!gameplay) {
            ClickTracker.clearHeld();
            return;
        }
        if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_RELEASE) {
            ClickTracker.onMouseEvent(input.button(), action, true);
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void grom$zoomScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || client.currentScreen != null) return;
        if (!GromConfig.get().zoomEnabled || GromVisualsClient.ZOOM_KEY == null || !GromVisualsClient.ZOOM_KEY.isPressed()) return;
        if (vertical != 0.0) {
            ZoomController.adjust(vertical);
            ci.cancel();
        }
    }
}
