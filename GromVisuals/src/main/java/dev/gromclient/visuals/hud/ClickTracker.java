package dev.gromclient.visuals.hud;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

/** Counts press edges in a rolling one-second window. Holding a button is not a click. */
public final class ClickTracker {
    private static final Deque<Long> LEFT = new ArrayDeque<>();
    private static final Deque<Long> RIGHT = new ArrayDeque<>();
    private static boolean leftHeld;
    private static boolean rightHeld;

    private ClickTracker() { }

    public static void onMouseEvent(int button, int action, boolean gameplayFocused) {
        if (!gameplayFocused) {
            leftHeld = false;
            rightHeld = false;
            return;
        }
        boolean down = action == GLFW.GLFW_PRESS;
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            if (down && !leftHeld) LEFT.addLast(System.currentTimeMillis());
            leftHeld = down;
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
            if (down && !rightHeld) RIGHT.addLast(System.currentTimeMillis());
            rightHeld = down;
        }
    }

    public static void clearHeld() { leftHeld = false; rightHeld = false; }
    public static boolean isLeftHeld() { return leftHeld; }
    public static boolean isRightHeld() { return rightHeld; }
    public static int leftCps() { trim(LEFT); return LEFT.size(); }
    public static int rightCps() { trim(RIGHT); return RIGHT.size(); }

    private static void trim(Deque<Long> queue) {
        long cutoff = System.currentTimeMillis() - 1000L;
        while (!queue.isEmpty() && queue.peekFirst() < cutoff) queue.removeFirst();
    }
}
