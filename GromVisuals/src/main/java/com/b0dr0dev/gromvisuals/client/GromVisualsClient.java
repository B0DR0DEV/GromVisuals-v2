package com.b0dr0dev.gromvisuals.client;

import com.b0dr0dev.gromvisuals.client.config.VisualConfig;
import com.b0dr0dev.gromvisuals.client.gui.GromVisualsScreen;
import com.b0dr0dev.gromvisuals.client.hud.GromHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GromVisualsClient implements ClientModInitializer {
    public static final String MOD_ID = "gromvisuals";
    public static final Logger LOGGER = LoggerFactory.getLogger("GromVisuals");
    private static KeyMapping openMenuKey;

    @Override
    public void onInitializeClient() {
        VisualConfig.load();
        openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.gromvisuals.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "key.category.gromvisuals"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new GromVisualsScreen());
                }
            }
        });

        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(MOD_ID, "main_hud"),
                (graphics, tickCounter) -> GromHud.render(graphics)
        );

        LOGGER.info("GromVisuals initialized. Press Right Shift to open settings.");
    }

    private GromVisualsClient() {
    }
}
