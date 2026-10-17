package dev.gromclient.visuals.input;

import dev.gromclient.visuals.GromVisualsClient;
import dev.gromclient.visuals.config.GromConfig;
import net.minecraft.client.MinecraftClient;

public final class ZoomController {
    private static float current = 1.0f;
    private static float target = 3.0f;
    private static boolean wasHeld;

    private ZoomController() { }

    public static void tick(MinecraftClient client) {
        GromConfig cfg = GromConfig.get();
        boolean held = cfg.zoomEnabled && client.player != null && client.world != null
                && client.currentScreen == null && GromVisualsClient.ZOOM_KEY != null
                && GromVisualsClient.ZOOM_KEY.isPressed();
        if (held && !wasHeld) target = clamp(cfg.zoomTarget, cfg.zoomMin, cfg.zoomMax);
        if (held) target = clamp(target, cfg.zoomMin, cfg.zoomMax);
        else target = 1.0f;
        wasHeld = held;
        float response = held ? 0.22f : 0.18f;
        current += (target - current) * response;
        if (Math.abs(current - target) < 0.015f) current = target;
    }

    public static void adjust(double wheelDelta) {
        GromConfig cfg = GromConfig.get();
        target = clamp(target + (float) wheelDelta * cfg.zoomSpeed, cfg.zoomMin, cfg.zoomMax);
        cfg.zoomTarget = target;
        GromConfig.save();
    }

    public static float multiplier() { return current; }
    public static boolean isActive() { return current > 1.025f; }
    public static void setConfiguredTarget(float value) {
        GromConfig cfg = GromConfig.get();
        cfg.zoomTarget = clamp(value, cfg.zoomMin, cfg.zoomMax);
        if (!wasHeld) target = cfg.zoomTarget;
    }

    public static void resetTarget() { target = 1.0f; current = 1.0f; wasHeld = false; }

    private static float clamp(float v, float min, float max) { return Math.max(min, Math.min(max, v)); }
}
