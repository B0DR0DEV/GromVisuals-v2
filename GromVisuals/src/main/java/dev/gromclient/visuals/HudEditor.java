package dev.gromclient.visuals;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Drag-and-drop positioning for HUD groups while the mouse cursor is free. */
public final class HudEditor {
    private static boolean wasDown;
    private static int selected = -1;
    private static double lastX, lastY;

    private HudEditor() {}

    public static void tick(MinecraftClient mc) {
        VisualConfig c = GromVisualsClient.CONFIG;
        if (!c.editHud || !(mc.currentScreen instanceof HudEditorScreen) || mc.player == null) {
            wasDown = false;
            selected = -1;
            return;
        }

        long window = mc.getWindow().getHandle();
        boolean down = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        double sx = mc.mouse.getScaledX(mc.getWindow());
        double sy = mc.mouse.getScaledY(mc.getWindow());
        int x = (int) sx, y = (int) sy;

        if (down && !wasDown) {
            selected = pick(mc, c, x, y);
            lastX = sx;
            lastY = sy;
        } else if (down && selected >= 0) {
            move(c, selected, (int) Math.round(sx - lastX), (int) Math.round(sy - lastY));
            lastX = sx;
            lastY = sy;
        } else if (!down && wasDown && selected >= 0) {
            c.save();
            selected = -1;
        }
        wasDown = down;
    }

    private static boolean in(int x, int y, int a, int b, int w, int h) {
        return x >= a && x <= a + w && y >= b && y <= b + h;
    }

    private static int pick(MinecraftClient m, VisualConfig c, int x, int y) {
        int w = m.getWindow().getScaledWidth(), h = m.getWindow().getScaledHeight();
        if (in(x, y, 8 + c.infoX, 8 + c.infoY, 200, 190)) return 0;
        if (c.cps && in(x, y, 8 + c.cpsX, h - (c.keystrokes ? 103 : 29) + c.cpsY, 190, 35)) return 1;
        if (c.keystrokes && in(x, y, w - 100 + c.keysX, h - 125 + c.keysY, 115, 130)) return 2;
        if (c.armor && in(x, y, 8 + c.armorX, h / 2 - 55 + c.armorY, 50, 110)) return 3;
        if (c.durability && in(x, y, 8 + c.durabilityX, h - 36 + c.durabilityY, 190, 35)) return 4;
        if (c.potionEffects && in(x, y, w - 175 + c.effectsX, 8 + c.effectsY, 175, 120)) return 5;
        if (c.crosshair && in(x, y, w / 2 - 30 + c.crosshairX, h / 2 - 30 + c.crosshairY, 60, 60)) return 6;
        if (c.targetHud && in(x, y, w / 2 + c.targetX, h / 2 + c.targetY, 200, 65)) return 7;
        if (c.saturationHud && in(x, y, 8 + c.saturationX, h - 60 + c.saturationY, 200, 40)) return 8;
        if (in(x, y, w / 2 - 105 + c.warningX, h / 2 + 12 + c.warningY, 210, 65)) return 9;
        return -1;
    }

    private static void move(VisualConfig c, int i, int dx, int dy) {
        switch (i) {
            case 0 -> { c.infoX += dx; c.infoY += dy; }
            case 1 -> { c.cpsX += dx; c.cpsY += dy; }
            case 2 -> { c.keysX += dx; c.keysY += dy; }
            case 3 -> { c.armorX += dx; c.armorY += dy; }
            case 4 -> { c.durabilityX += dx; c.durabilityY += dy; }
            case 5 -> { c.effectsX += dx; c.effectsY += dy; }
            case 6 -> { c.crosshairX += dx; c.crosshairY += dy; }
            case 7 -> { c.targetX += dx; c.targetY += dy; }
            case 8 -> { c.saturationX += dx; c.saturationY += dy; }
            case 9 -> { c.warningX += dx; c.warningY += dy; }
        }
    }
}
