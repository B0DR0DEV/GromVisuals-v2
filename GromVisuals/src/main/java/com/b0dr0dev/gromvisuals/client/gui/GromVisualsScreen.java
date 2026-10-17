package com.b0dr0dev.gromvisuals.client.gui;

import com.b0dr0dev.gromvisuals.client.config.VisualConfig;
import com.b0dr0dev.gromvisuals.client.hud.HudDraw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/** Custom animated settings menu for GromVisuals. */
public final class GromVisualsScreen extends Screen {
    private static final String[] NAV_NAMES = {"Overview", "HUD modules", "Accent theme", "Motion & layout", "About"};
    private static final String[] NAV_SUBS = {"Your visual setup", "Pick what you see", "Make it yours", "Fine-tune the feel", "Build information"};
    private static final String[] ACCENT_NAMES = {"Violet", "Cyan", "Amber", "Mint"};
    private static final String[] ACCENT_DETAILS = {"#9A7BFF", "#46D9F2", "#FFB454", "#70E1B5"};
    private static final int[] ACCENT_COLORS = {0xFF9A7BFF, 0xFF46D9F2, 0xFFFFB454, 0xFF70E1B5};

    private final List<Hitbox> hitboxes = new ArrayList<>();
    private int page = 0;
    private long openedAt = System.currentTimeMillis();
    private int panelX, panelY, panelW, panelH, mainX, mainW;
    private int uiWidth, uiHeight;
    private float uiScale = 1.0f;

    public GromVisualsScreen() {
        super(Component.literal("GromVisuals"));
    }

    @Override
    protected void init() {
        super.init();
        openedAt = System.currentTimeMillis();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        VisualConfig cfg = VisualConfig.get();
        int accent = cfg.accent();
        float appear = Math.min(1.0f, (System.currentTimeMillis() - openedAt) / 240.0f);
        float drift = cfg.animations ? (float) Math.sin(System.currentTimeMillis() / 850.0) : 0.0f;

        renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.fill(0, 0, width, height, 0xBF070911);
        uiScale = Math.min(1.0f, Math.min((width - 12.0f) / 900.0f, (height - 12.0f) / 560.0f));
        uiScale = Math.max(0.45f, uiScale);
        uiWidth = Math.round(width / uiScale);
        uiHeight = Math.round(height / uiScale);
        graphics.pose().pushMatrix();
        graphics.pose().scale(uiScale, uiScale);
        drawBackdrop(graphics, accent, drift);

        panelW = Math.min(900, Math.max(620, uiWidth - 28));
        panelH = Math.min(560, Math.max(420, uiHeight - 28));
        panelX = (uiWidth - panelW) / 2;
        panelY = (uiHeight - panelH) / 2 + (cfg.animations ? Math.round((1.0f - appear) * 12.0f) : 0);
        mainX = panelX + 214;
        mainW = panelW - 236;

        hitboxes.clear();
        drawMainPanel(graphics, cfg, accent, appear, Math.round(mouseX / uiScale), Math.round(mouseY / uiScale));
        graphics.pose().popMatrix();
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void drawBackdrop(GuiGraphics graphics, int accent, float drift) {
        int offset = (int) (drift * 18.0f);
        for (int x = -40; x < uiWidth + 40; x += 42) {
            int a = ((x / 42) % 4 == 0) ? 10 : 5;
            graphics.fill(x + offset, 0, x + 1 + offset, uiHeight, (a << 24) | 0x00FFFFFF);
        }
        for (int y = 0; y < uiHeight; y += 42) {
            graphics.fill(0, y, uiWidth, y + 1, 0x05000000);
        }
        HudDraw.roundRect(graphics, uiWidth / 2 - 250 + offset, -120, 500, 210, 80, withAlpha(accent, 15));
        HudDraw.roundRect(graphics, uiWidth / 2 + 160 - offset, uiHeight - 100, 400, 180, 70, withAlpha(accent, 10));
    }

    private void drawMainPanel(GuiGraphics g, VisualConfig cfg, int accent, float appear, int mouseX, int mouseY) {
        // Panel shadow, border and inner surface.
        HudDraw.roundRect(g, panelX - 2, panelY + 5, panelW + 4, panelH + 4, 18, 0x65000000);
        HudDraw.roundRect(g, panelX - 1, panelY - 1, panelW + 2, panelH + 2, 18, 0xA52B2D3E);
        HudDraw.roundRect(g, panelX, panelY, panelW, panelH, 17, 0xF40D0F17);
        HudDraw.roundRect(g, panelX + 1, panelY + 1, panelW - 2, panelH - 2, 16, 0xF610121B);
        HudDraw.roundRect(g, panelX + 1, panelY + 1, panelW - 2, 3, 1, withAlpha(accent, 210));

        drawSidebar(g, cfg, accent, mouseX, mouseY);
        drawHeader(g, cfg, accent);
        drawPage(g, cfg, accent, mouseX, mouseY);
        drawFooter(g, cfg, accent);
    }

    private void drawSidebar(GuiGraphics g, VisualConfig cfg, int accent, int mouseX, int mouseY) {
        int sx = panelX + 13;
        int sy = panelY + 14;
        int sw = 178;
        int sh = panelH - 28;
        HudDraw.roundRect(g, sx, sy, sw, sh, 12, 0xF50A0C13);
        HudDraw.roundRect(g, sx + 11, sy + 12, 37, 37, 11, cfg.accentSoft());
        // A sharp, simple lightning-style mark rather than an overdecorated logo.
        g.fill(sx + 29, sy + 18, sx + 34, sy + 29, accent);
        g.fill(sx + 24, sy + 25, sx + 30, sy + 35, accent);
        g.fill(sx + 24, sy + 25, sx + 34, sy + 28, accent);
        HudDraw.text(g, "GROM", sx + 57, sy + 14, 0xFFF5F6FC);
        HudDraw.text(g, "VISUALS", sx + 57, sy + 28, accent);
        HudDraw.text(g, "CLIENT INTERFACE", sx + 13, sy + 61, 0xFF737B91);
        g.fill(sx + 12, sy + 80, sx + sw - 12, sy + 81, 0x242D3140);

        int navY = sy + 94;
        for (int i = 0; i < NAV_NAMES.length; i++) {
            int rowY = navY + i * 47;
            boolean selected = page == i;
            boolean hover = inside(mouseX, mouseY, sx + 7, rowY, sw - 14, 39);
            if (selected) {
                HudDraw.roundRect(g, sx + 7, rowY, sw - 14, 39, 8, cfg.accentSoft());
                HudDraw.roundRect(g, sx + 7, rowY + 8, 2, 23, 1, accent);
            } else if (hover) {
                HudDraw.roundRect(g, sx + 7, rowY, sw - 14, 39, 8, 0xFF1A1D28);
            }
            drawNavGlyph(g, sx + 19, rowY + 11, i, selected ? accent : 0xFF7C8498);
            HudDraw.text(g, NAV_NAMES[i], sx + 42, rowY + 7, selected ? 0xFFF5F6FC : 0xFFB3B8C8);
            HudDraw.text(g, navHint(i), sx + 42, rowY + 21, selected ? 0xFFC2B7FF : 0xFF777E91);
            final int selectedPage = i;
            hitboxes.add(new Hitbox(sx + 7, rowY, sw - 14, 39, () -> page = selectedPage));
        }

        int statusY = sy + sh - 63;
        HudDraw.roundRect(g, sx + 10, statusY, sw - 20, 48, 8, 0xFF141722);
        HudDraw.roundRect(g, sx + 19, statusY + 12, 7, 7, 4, cfg.hudEnabled ? 0xFF70E1B5 : 0xFF72798B);
        HudDraw.text(g, cfg.hudEnabled ? "HUD IS ACTIVE" : "HUD IS DISABLED", sx + 34, statusY + 8, 0xFFE8EAF4);
        HudDraw.text(g, "RIGHT SHIFT  /  MENU", sx + 19, statusY + 25, 0xFF788095);
    }

    private String navHint(int i) {
        return switch (i) {
            case 0 -> "STATUS & QUICK ACCESS";
            case 1 -> "WIDGET VISIBILITY";
            case 2 -> "COLOR PALETTE";
            case 3 -> "ANIMATION SETTINGS";
            default -> "ABOUT THIS BUILD";
        };
    }

    private void drawNavGlyph(GuiGraphics g, int x, int y, int i, int color) {
        switch (i) {
            case 0 -> {
                g.fill(x, y + 3, x + 12, y + 5, color);
                g.fill(x + 2, y, x + 4, y + 12, color);
                g.fill(x + 8, y, x + 10, y + 12, color);
            }
            case 1 -> {
                HudDraw.roundRect(g, x, y, 12, 12, 3, color);
                g.fill(x + 3, y + 3, x + 9, y + 5, 0xFF151720);
                g.fill(x + 3, y + 7, x + 7, y + 9, 0xFF151720);
            }
            case 2 -> {
                HudDraw.roundRect(g, x, y, 12, 12, 6, color);
                HudDraw.roundRect(g, x + 3, y + 3, 6, 6, 3, 0xFF151720);
            }
            case 3 -> {
                g.fill(x + 1, y + 2, x + 4, y + 10, color);
                g.fill(x + 5, y, x + 8, y + 12, color);
                g.fill(x + 9, y + 4, x + 12, y + 8, color);
            }
            default -> {
                HudDraw.roundRect(g, x, y, 12, 12, 6, color);
                HudDraw.text(g, "i", x + 4, y + 2, 0xFF151720);
            }
        }
    }

    private void drawHeader(GuiGraphics g, VisualConfig cfg, int accent) {
        int x = mainX;
        int y = panelY + 25;
        HudDraw.text(g, NAV_NAMES[page].toUpperCase(), x, y, 0xFF777F94);
        HudDraw.text(g, pageTitle(page), x, y + 19, 0xFFF4F5FB);
        HudDraw.text(g, NAV_SUBS[page], x, y + 39, 0xFF9AA1B3);
        int pillW = 105;
        int px = panelX + panelW - pillW - 22;
        HudDraw.roundRect(g, px, y + 1, pillW, 25, 12, 0xFF171B25);
        HudDraw.roundRect(g, px + 9, y + 9, 6, 6, 3, 0xFF70E1B5);
        HudDraw.text(g, "CLIENT SIDE", px + 21, y + 8, 0xFFCAD0DE);
        g.fill(mainX, panelY + 91, panelX + panelW - 22, panelY + 92, 0x282F3342);
    }

    private String pageTitle(int page) {
        return switch (page) {
            case 0 -> "Your game, your style.";
            case 1 -> "Build your perfect HUD.";
            case 2 -> "Choose your signature.";
            case 3 -> "Smooth by design.";
            default -> "Made for your setup.";
        };
    }

    private void drawPage(GuiGraphics g, VisualConfig cfg, int accent, int mouseX, int mouseY) {
        int x = mainX;
        int y = panelY + 108;
        int w = mainW;
        switch (page) {
            case 0 -> drawOverview(g, cfg, accent, x, y, w, mouseX, mouseY);
            case 1 -> drawHudPage(g, cfg, accent, x, y, w, mouseX, mouseY);
            case 2 -> drawThemePage(g, cfg, accent, x, y, w, mouseX, mouseY);
            case 3 -> drawMotionPage(g, cfg, accent, x, y, w, mouseX, mouseY);
            default -> drawAboutPage(g, cfg, accent, x, y, w);
        }
    }

    private void drawOverview(GuiGraphics g, VisualConfig cfg, int accent, int x, int y, int w, int mouseX, int mouseY) {
        int heroH = 132;
        HudDraw.roundRect(g, x, y, w, heroH, 12, 0xFF171A26);
        HudDraw.roundRect(g, x + w - 124, y + 12, 112, heroH - 24, 9, 0xFF202436);
        HudDraw.roundRect(g, x + 15, y + 14, 30, 30, 9, cfg.accentSoft());
        HudDraw.text(g, "✦", x + 23, y + 22, accent);
        HudDraw.text(g, "GROM VISUALS", x + 56, y + 17, 0xFFE7E9F4);
        HudDraw.text(g, "CLEAN OVERLAYS. ZERO CLUTTER.", x + 56, y + 32, accent);
        HudDraw.text(g, "A minimal, animated layer built around the", x + 16, y + 62, 0xFFB5BBCA);
        HudDraw.text(g, "information you actually need while playing.", x + 16, y + 76, 0xFFB5BBCA);
        HudDraw.text(g, cfg.hudEnabled ? "READY TO PLAY" : "HUD PAUSED", x + 16, y + 106, cfg.hudEnabled ? 0xFF70E1B5 : 0xFFFFB454);
        HudDraw.text(g, cfg.hudEnabled ? "●" : "○", x + 108, y + 106, cfg.hudEnabled ? 0xFF70E1B5 : 0xFFFFB454);
        HudDraw.text(g, String.format("%02d", countEnabled(cfg)), x + w - 99, y + 31, accent);
        HudDraw.text(g, "/ 07", x + w - 64, y + 37, 0xFF777F93);
        HudDraw.text(g, "MODULES", x + w - 99, y + 57, 0xFF8F97AB);
        HudDraw.roundRect(g, x + w - 99, y + 78, 86, 4, 2, 0xFF303545);
        HudDraw.roundRect(g, x + w - 99, y + 78, Math.round(86 * countEnabled(cfg) / 7.0f), 4, 2, accent);
        addHitbox(x, y, w, heroH, () -> { page = 1; });

        int gap = 10;
        int cardY = y + heroH + 13;
        int cardW = (w - gap * 2) / 3;
        overviewStat(g, x, cardY, cardW, 77, "01", "HUD WIDGETS", countEnabled(cfg) + " / 07 active", accent);
        overviewStat(g, x + cardW + gap, cardY, cardW, 77, "02", "ACCENT", cfg.accentName(), accent);
        overviewStat(g, x + 2 * (cardW + gap), cardY, cardW, 77, "03", "ANIMATIONS", cfg.animations ? "Smooth & subtle" : "Reduced motion", accent);

        int quickY = cardY + 91;
        HudDraw.text(g, "QUICK CONTROLS", x, quickY, 0xFF8B93A8);
        int toggleY = quickY + 19;
        drawToggleCard(g, x, toggleY, cardW, 57, "HUD overlay", "All widgets", cfg.hudEnabled, accent, mouseX, mouseY,
                () -> { cfg.hudEnabled = !cfg.hudEnabled; VisualConfig.save(); });
        drawToggleCard(g, x + cardW + gap, toggleY, cardW, 57, "FPS counter", "Performance", cfg.showFps, accent, mouseX, mouseY,
                () -> { cfg.showFps = !cfg.showFps; VisualConfig.save(); });
        drawToggleCard(g, x + 2 * (cardW + gap), toggleY, cardW, 57, "Ping display", "Connection", cfg.showPing, accent, mouseX, mouseY,
                () -> { cfg.showPing = !cfg.showPing; VisualConfig.save(); });
        drawSmallAction(g, x, toggleY + 68, Math.min(178, w), 30, "CUSTOMIZE HUD   →", accent, mouseX, mouseY, () -> page = 1);
    }

    private void overviewStat(GuiGraphics g, int x, int y, int w, int h, String number, String title, String value, int accent) {
        HudDraw.roundRect(g, x, y, w, h, 9, 0xFF141722);
        HudDraw.text(g, number, x + 11, y + 10, accent);
        HudDraw.text(g, title, x + 37, y + 10, 0xFF828BA0);
        HudDraw.text(g, value, x + 11, y + 40, 0xFFE7E9F4);
    }

    private void drawHudPage(GuiGraphics g, VisualConfig cfg, int accent, int x, int y, int w, int mouseX, int mouseY) {
        int half = (w - 10) / 2;
        drawToggleCard(g, x, y, w, 57, "Enable custom HUD", "Master switch for all widgets", cfg.hudEnabled, accent, mouseX, mouseY,
                () -> { cfg.hudEnabled = !cfg.hudEnabled; VisualConfig.save(); });
        int rowY = y + 67;
        drawToggleCard(g, x, rowY, half, 62, "Brand badge", "GromVisuals identity", cfg.showBrand, accent, mouseX, mouseY,
                () -> { cfg.showBrand = !cfg.showBrand; VisualConfig.save(); });
        drawToggleCard(g, x + half + 10, rowY, half, 62, "FPS counter", "Live frames per second", cfg.showFps, accent, mouseX, mouseY,
                () -> { cfg.showFps = !cfg.showFps; VisualConfig.save(); });
        rowY += 72;
        drawToggleCard(g, x, rowY, half, 62, "Ping monitor", "Live connection latency", cfg.showPing, accent, mouseX, mouseY,
                () -> { cfg.showPing = !cfg.showPing; VisualConfig.save(); });
        drawToggleCard(g, x + half + 10, rowY, half, 62, "Coordinates", "XYZ position in the world", cfg.showCoordinates, accent, mouseX, mouseY,
                () -> { cfg.showCoordinates = !cfg.showCoordinates; VisualConfig.save(); });
        rowY += 72;
        drawToggleCard(g, x, rowY, half, 62, "Biome label", "Current biome name", cfg.showBiome, accent, mouseX, mouseY,
                () -> { cfg.showBiome = !cfg.showBiome; VisualConfig.save(); });
        drawToggleCard(g, x + half + 10, rowY, half, 62, "Keystrokes", "WASD, jump and sneak", cfg.showKeystrokes, accent, mouseX, mouseY,
                () -> { cfg.showKeystrokes = !cfg.showKeystrokes; VisualConfig.save(); });
        rowY += 72;
        drawToggleCard(g, x, rowY, half, 62, "World clock", "In-game time, not system time", cfg.showWorldTime, accent, mouseX, mouseY,
                () -> { cfg.showWorldTime = !cfg.showWorldTime; VisualConfig.save(); });
        drawToggleCard(g, x + half + 10, rowY, half, 62, "Compact spacing", "Tighter panels and margins", cfg.compactHud, accent, mouseX, mouseY,
                () -> { cfg.compactHud = !cfg.compactHud; VisualConfig.save(); });
    }

    private void drawThemePage(GuiGraphics g, VisualConfig cfg, int accent, int x, int y, int w, int mouseX, int mouseY) {
        HudDraw.text(g, "ACCENT PRESETS", x, y, 0xFF8E96AA);
        int gap = 10;
        int cardW = (w - gap * 3) / 4;
        int cy = y + 20;
        for (int i = 0; i < ACCENT_NAMES.length; i++) {
            int cx = x + i * (cardW + gap);
            boolean selected = cfg.accentPreset == i;
            int color = ACCENT_COLORS[i];
            HudDraw.roundRect(g, cx, cy, cardW, 113, 10, selected ? 0xFF262B3B : 0xFF171A24);
            if (selected) HudDraw.roundRect(g, cx + 1, cy + 1, cardW - 2, 2, 1, color);
            HudDraw.roundRect(g, cx + 12, cy + 13, cardW - 24, 35, 8, color);
            HudDraw.roundRect(g, cx + 20, cy + 21, cardW - 40, 4, 2, withAlpha(0xFFFFFFFF, 100));
            HudDraw.text(g, ACCENT_NAMES[i], cx + 11, cy + 59, selected ? 0xFFF5F6FC : 0xFFD1D5E1);
            HudDraw.text(g, ACCENT_DETAILS[i], cx + 11, cy + 76, 0xFF81899D);
            if (selected) HudDraw.text(g, "ACTIVE", cx + 11, cy + 94, color);
            else HudDraw.text(g, "SELECT →", cx + 11, cy + 94, 0xFF71798D);
            final int preset = i;
            hitboxes.add(new Hitbox(cx, cy, cardW, 113, () -> { cfg.accentPreset = preset; VisualConfig.save(); }));
        }

        int previewY = cy + 130;
        HudDraw.roundRect(g, x, previewY, w, 133, 11, 0xFF151822);
        HudDraw.text(g, "LIVE PREVIEW", x + 15, previewY + 13, 0xFF8A92A7);
        HudDraw.roundRect(g, x + 15, previewY + 39, w - 30, 43, 10, 0xFF0B0D14);
        HudDraw.roundRect(g, x + 24, previewY + 48, 25, 25, 7, cfg.accentSoft());
        HudDraw.text(g, "G", x + 32, previewY + 54, accent);
        HudDraw.text(g, "GROM", x + 60, previewY + 48, 0xFFF1F3FA);
        HudDraw.text(g, "VISUALS", x + 60, previewY + 62, 0xFF8A91A4);
        HudDraw.text(g, "164 FPS", x + 155, previewY + 54, accent);
        HudDraw.text(g, "PING  21 ms", x + 225, previewY + 54, 0xFFD4D8E4);
        HudDraw.roundRect(g, x + w - 128, previewY + 50, 110, 21, 11, cfg.accentSoft());
        HudDraw.text(g, "XYZ  -86  63  -17", x + w - 120, previewY + 56, 0xFFE5E7F0);
        HudDraw.text(g, "The preview is illustrative; in-world values use live game data.", x + 15, previewY + 99, 0xFF848CA0);

        int resetY = previewY + 146;
        drawSmallAction(g, x, resetY, 150, 30, "RESET TO VIOLET", accent, mouseX, mouseY, () -> { cfg.accentPreset = 0; VisualConfig.save(); });
    }

    private void drawMotionPage(GuiGraphics g, VisualConfig cfg, int accent, int x, int y, int w, int mouseX, int mouseY) {
        drawToggleCard(g, x, y, w, 68, "Fluid animations", "Subtle transitions, animated accents and hover states", cfg.animations, accent, mouseX, mouseY,
                () -> { cfg.animations = !cfg.animations; VisualConfig.save(); });
        drawToggleCard(g, x, y + 79, w, 68, "Compact HUD", "Reduce spacing for smaller screens", cfg.compactHud, accent, mouseX, mouseY,
                () -> { cfg.compactHud = !cfg.compactHud; VisualConfig.save(); });

        int noteY = y + 168;
        HudDraw.roundRect(g, x, noteY, w, 102, 10, 0xFF151822);
        HudDraw.roundRect(g, x + 15, noteY + 15, 3, 71, 2, accent);
        HudDraw.text(g, "DESIGNED TO STAY OUT OF THE WAY", x + 29, noteY + 16, 0xFFE4E7F2);
        HudDraw.text(g, "Animations are intentionally restrained: no giant panels", x + 29, noteY + 39, 0xFF9BA3B6);
        HudDraw.text(g, "covering the game, no fake stats, and no heavy shaders.", x + 29, noteY + 54, 0xFF9BA3B6);
        HudDraw.text(g, "Turn motion off any time for a static interface.", x + 29, noteY + 69, 0xFF9BA3B6);

        int resetY = noteY + 117;
        drawSmallAction(g, x, resetY, 160, 30, "RESTORE DEFAULTS", accent, mouseX, mouseY, () -> {
            cfg.hudEnabled = true;
            cfg.showBrand = true;
            cfg.showFps = true;
            cfg.showPing = true;
            cfg.showCoordinates = true;
            cfg.showBiome = true;
            cfg.showKeystrokes = true;
            cfg.showWorldTime = false;
            cfg.animations = true;
            cfg.compactHud = false;
            cfg.accentPreset = 0;
            VisualConfig.save();
        });
    }

    private void drawAboutPage(GuiGraphics g, VisualConfig cfg, int accent, int x, int y, int w) {
        HudDraw.roundRect(g, x, y, w, 126, 11, 0xFF171A26);
        HudDraw.roundRect(g, x + 16, y + 16, 44, 44, 12, cfg.accentSoft());
        HudDraw.text(g, "G", x + 29, y + 28, accent);
        HudDraw.text(g, "GromVisuals", x + 75, y + 18, 0xFFF1F3FA);
        HudDraw.text(g, "Version 1.0.0  •  Client-side visuals", x + 75, y + 37, accent);
        HudDraw.text(g, "Built for Minecraft Java 1.21.11 / Fabric / Java 21", x + 16, y + 78, 0xFFB4BAC9);
        HudDraw.text(g, "No server installation is required for the HUD.", x + 16, y + 96, 0xFF878FA3);

        int iy = y + 145;
        HudDraw.text(g, "WHAT'S INCLUDED", x, iy, 0xFF8B93A8);
        String[] features = {
                "Animated dark interface with four accent presets",
                "Live FPS, latency, coordinates and biome information",
                "Keystroke panel with live pressed-key states",
                "Persistent options in config/gromvisuals.json",
                "GitHub Actions workflow that builds the release JAR"
        };
        for (int i = 0; i < features.length; i++) {
            int fy = iy + 22 + i * 31;
            HudDraw.roundRect(g, x, fy, w, 25, 6, 0xFF141722);
            HudDraw.roundRect(g, x + 10, fy + 9, 6, 6, 3, accent);
            HudDraw.text(g, features[i], x + 25, fy + 7, 0xFFD5D9E5);
        }
    }

    private void drawFooter(GuiGraphics g, VisualConfig cfg, int accent) {
        int y = panelY + panelH - 24;
        HudDraw.text(g, "GROMVISUALS  /  1.0.0", mainX, y, 0xFF596176);
        String status = cfg.hudEnabled ? "●  HUD ACTIVE" : "○  HUD DISABLED";
        int sw = Minecraft.getInstance().font.width(status);
        HudDraw.text(g, status, panelX + panelW - sw - 23, y, cfg.hudEnabled ? 0xFF70E1B5 : 0xFFFFB454);
    }

    private void drawToggleCard(GuiGraphics g, int x, int y, int w, int h, String title, String subtitle,
                                boolean value, int accent, int mouseX, int mouseY, Runnable action) {
        boolean hover = inside(mouseX, mouseY, x, y, w, h);
        HudDraw.roundRect(g, x, y, w, h, 8, hover ? 0xFF1D2130 : 0xFF171A24);
        HudDraw.roundRect(g, x + 1, y + 1, w - 2, 2, 1, value ? withAlpha(accent, 130) : 0xFF272B37);
        HudDraw.text(g, title, x + 12, y + 13, 0xFFE6E9F3);
        HudDraw.text(g, subtitle, x + 12, y + 31, 0xFF8991A5);
        drawSwitch(g, x + w - 49, y + (h - 22) / 2, value, accent);
        hitboxes.add(new Hitbox(x, y, w, h, action));
    }

    private void drawSwitch(GuiGraphics g, int x, int y, boolean value, int accent) {
        HudDraw.roundRect(g, x, y, 36, 20, 10, value ? accent : 0xFF353947);
        int knobX = value ? x + 19 : x + 3;
        HudDraw.roundRect(g, knobX, y + 3, 14, 14, 7, 0xFFF8F9FC);
    }

    private void drawSmallAction(GuiGraphics g, int x, int y, int w, int h, String label, int accent,
                                 int mouseX, int mouseY, Runnable action) {
        boolean hover = inside(mouseX, mouseY, x, y, w, h);
        HudDraw.roundRect(g, x, y, w, h, 7, hover ? 0xFF2B3042 : 0xFF1D2230);
        HudDraw.roundRect(g, x + 1, y + 1, w - 2, 2, 1, withAlpha(accent, hover ? 255 : 110));
        int tw = Minecraft.getInstance().font.width(label);
        HudDraw.text(g, label, x + (w - tw) / 2, y + 11, hover ? 0xFFF8F9FE : 0xFFD9DDE9);
        hitboxes.add(new Hitbox(x, y, w, h, action));
    }

    private int countEnabled(VisualConfig cfg) {
        int count = 0;
        if (cfg.showBrand) count++;
        if (cfg.showFps) count++;
        if (cfg.showPing) count++;
        if (cfg.showCoordinates) count++;
        if (cfg.showBiome) count++;
        if (cfg.showKeystrokes) count++;
        if (cfg.showWorldTime) count++;
        return count;
    }

    private void addHitbox(int x, int y, int w, int h, Runnable action) {
        hitboxes.add(new Hitbox(x, y, w, h, action));
    }

    private static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        double logicalX = mouseX / uiScale;
        double logicalY = mouseY / uiScale;
        for (int i = hitboxes.size() - 1; i >= 0; i--) {
            Hitbox box = hitboxes.get(i);
            if (inside(logicalX, logicalY, box.x, box.y, box.w, box.h)) {
                box.action.run();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private static int withAlpha(int argb, int alpha) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (argb & 0x00FFFFFF);
    }

    @Override
    public void onClose() {
        VisualConfig.save();
        super.onClose();
    }

    private record Hitbox(int x, int y, int w, int h, Runnable action) {}
}
