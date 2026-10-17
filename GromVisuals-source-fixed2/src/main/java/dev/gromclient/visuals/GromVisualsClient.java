package dev.gromclient.visuals;

import dev.gromclient.visuals.config.GromConfig;
import dev.gromclient.visuals.hud.ClickTracker;
import dev.gromclient.visuals.hud.GromHud;
import dev.gromclient.visuals.hud.MiniMapCache;
import dev.gromclient.visuals.input.ZoomController;
import dev.gromclient.visuals.ui.GromMenuScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraft.particle.ParticleTypes;
import org.lwjgl.glfw.GLFW;

public final class GromVisualsClient implements ClientModInitializer {
    public static KeyBinding OPEN_MENU_KEY;
    public static KeyBinding ZOOM_KEY;
    private static int particleTick;

    @Override
    public void onInitializeClient() {
        GromConfig.load();
        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("grom_visuals", "main"));
        OPEN_MENU_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.grom_visuals.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));
        ZOOM_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.grom_visuals.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, category));

        HudElementRegistry.addLast(Identifier.of("grom_visuals", "hud"), (context, tickCounter) -> GromHud.render(context));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_MENU_KEY.wasPressed()) {
                // Keep every vanilla title/options/multiplayer screen untouched.
                if (client.world != null && client.player != null && client.currentScreen == null) {
                    client.setScreen(new GromMenuScreen());
                }
            }
            if (client.currentScreen != null) ClickTracker.clearHeld();
            ZoomController.tick(client);
            MiniMapCache.tick(client);
            tickParticles(client);
        });
    }

    private static void tickParticles(net.minecraft.client.MinecraftClient client) {
        if (!GromConfig.get().orbitSparks || client.world == null || client.player == null) return;
        if (++particleTick % 3 != 0) return;
        int amount = Math.max(1, Math.min(4, Math.round(GromConfig.get().particleIntensity * 3.0f)));
        double time = particleTick * 0.11;
        for (int i = 0; i < amount; i++) {
            double angle = time + (Math.PI * 2.0 * i / amount);
            double radius = 0.62 + 0.08 * Math.sin(time * 1.7 + i);
            double x = client.player.getX() + Math.cos(angle) * radius;
            double z = client.player.getZ() + Math.sin(angle) * radius;
            double y = client.player.getY() + 0.35 + (Math.sin(time * 2 + i) + 1.0) * 0.55;
            // ClientWorld only: these decorative sparks are local and never sent to a server.
            client.world.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z,
                    Math.cos(angle) * 0.012, 0.012, Math.sin(angle) * 0.012);
        }
    }
}
