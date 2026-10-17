package dev.gromclient.visuals.ui;

import dev.gromclient.visuals.GromVisualsClient;
import dev.gromclient.visuals.config.GromConfig;
import dev.gromclient.visuals.input.ZoomController;
import dev.gromclient.visuals.render.UiDraw;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/** Standalone GromVisuals menu. It is never installed as a replacement for a vanilla screen. */
public final class GromMenuScreen extends Screen {
    private enum Tab { DASHBOARD, HUD, ZOOM, CROSSHAIR, COSMETICS, SETTINGS }
    private Tab tab = Tab.DASHBOARD;
    private int panelX, panelY, panelW, panelH, contentX, contentY, contentW, contentH;
    private String toast = "";
    private long toastUntil;
    private final long openedAt = System.currentTimeMillis();

    public GromMenuScreen() { super(Text.literal("GromVisuals")); }

    @Override
    public void render(DrawContext c, int mouseX, int mouseY, float delta) {
        GromConfig cfg = GromConfig.get();
        float progress = cfg.animationsEnabled ? Math.min(1.0f, (System.currentTimeMillis() - openedAt) / 210.0f) : 1.0f;
        float eased = 1.0f - (float)Math.pow(1.0f - progress, 3.0);
        int backdropAlpha = Math.max(0, Math.min(169, Math.round(169.0f * eased)));
        c.fill(0, 0, width, height, (backdropAlpha << 24));
        panelW = Math.min(width - 28, 980);
        panelH = Math.min(height - 24, 610);
        panelW = Math.max(540, panelW);
        panelH = Math.max(400, panelH);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2 + Math.round((1.0f - eased) * 12.0f);
        UiDraw.panel(c, panelX, panelY, panelW, panelH, 252);
        UiDraw.rounded(c, panelX, panelY, panelW, 54, 9, 0xFF111116);
        c.fill(panelX + 14, panelY + 53, panelX + panelW - 14, panelY + 54, 0x2AFFFFFF);
        UiDraw.rounded(c, panelX + 18, panelY + 12, 30, 30, 8, cfg.accentColor);
        UiDraw.centered(c, textRenderer, "G", panelX + 33, panelY + 22, 0xFFFFFFFF);
        UiDraw.label(c, textRenderer, "GromVisuals", panelX + 58, panelY + 15, 0xFFF5F3FF);
        UiDraw.label(c, textRenderer, "CLIENT VISUALS  •  1.21.11", panelX + 58, panelY + 32, 0xFF9895A8);
        UiDraw.label(c, textRenderer, "B0DR0DEV", panelX + panelW - 91, panelY + 22, 0xFFB6B2C4);
        UiDraw.label(c, textRenderer, "×", panelX + panelW - 25, panelY + 21, 0xFFAAA6B8);

        int sideX = panelX + 12, sideY = panelY + 68, sideW = 158;
        UiDraw.rounded(c, sideX, sideY, sideW, panelH - 82, 8, 0xFF101015);
        UiDraw.label(c, textRenderer, "WORKSPACE", sideX + 12, sideY + 13, 0xFF777486);
        String[] labels = {"Overview", "HUD Modules", "Zoom", "Crosshair Studio", "Cosmetics", "Settings"};
        Tab[] tabs = Tab.values();
        for (int i = 0; i < tabs.length; i++) {
            int y = sideY + 34 + i * 43;
            boolean selected = tab == tabs[i];
            if (selected) {
                UiDraw.rounded(c, sideX + 7, y - 5, sideW - 14, 34, 7, 0xFF292333);
                c.fill(sideX + 7, y + 3, sideX + 9, y + 21, cfg.accentColor);
            }
            UiDraw.label(c, textRenderer, tabIcon(tabs[i]), sideX + 17, y + 6, selected ? cfg.accentColor : 0xFF858194);
            UiDraw.label(c, textRenderer, labels[i], sideX + 37, y + 6, selected ? 0xFFF5F3FF : 0xFFAAA6B8);
        }
        UiDraw.label(c, textRenderer, "RIGHT SHIFT", sideX + 12, panelY + panelH - 42, 0xFFB4A2F6);
        UiDraw.label(c, textRenderer, "to open menu", sideX + 12, panelY + panelH - 29, 0xFF716D7F);

        contentX = sideX + sideW + 19;
        contentY = panelY + 72;
        contentW = panelX + panelW - contentX - 18;
        contentH = panelH - 88;
        switch (tab) {
            case DASHBOARD -> renderDashboard(c, cfg, mouseX, mouseY);
            case HUD -> renderHudTab(c, cfg, mouseX, mouseY);
            case ZOOM -> renderZoomTab(c, cfg, mouseX, mouseY);
            case CROSSHAIR -> renderCrosshairTab(c, cfg, mouseX, mouseY);
            case COSMETICS -> renderCosmeticsTab(c, cfg, mouseX, mouseY);
            case SETTINGS -> renderSettingsTab(c, cfg, mouseX, mouseY);
        }
        if (!toast.isEmpty() && System.currentTimeMillis() < toastUntil) {
            UiDraw.rounded(c, width / 2 - 110, panelY + panelH - 43, 220, 28, 8, 0xF0222028);
            UiDraw.centered(c, textRenderer, toast, width / 2, panelY + panelH - 33, 0xFFE5DFFF);
        }
    }

    private void renderDashboard(DrawContext c, GromConfig cfg, int mx, int my) {
        sectionTitle(c, "A cleaner view of your game", "Compact PvP HUD with local visual controls.");
        int gap = 10;
        int cardW = (contentW - 2 * gap) / 3;
        statCard(c, contentX, contentY + 55, cardW, 80, "PERFORMANCE", MinecraftClient.getInstance().getCurrentFps() + " FPS", "Live client counter", 0xFFFFC247);
        int enabled = (cfg.showInfo ? 1 : 0) + (cfg.showCoordinates ? 1 : 0) + (cfg.showPotionEffects ? 1 : 0)
                + (cfg.showKeystrokes ? 1 : 0) + (cfg.showClickHud ? 1 : 0) + (cfg.showArmor ? 1 : 0)
                + (cfg.showMiniMap ? 1 : 0) + (cfg.showTargetHud ? 1 : 0);
        statCard(c, contentX + cardW + gap, contentY + 55, cardW, 80, "HUD MODULES", enabled + " ACTIVE", "Toggle each module", cfg.accentColor);
        statCard(c, contentX + (cardW + gap) * 2, contentY + 55, cardW, 80, "ZOOM RANGE", String.format(java.util.Locale.ROOT, "%.1f×", cfg.zoomMax), "Hold C · wheel adjusts", 0xFF69D6C5);

        int y = contentY + 150;
        UiDraw.panel(c, contentX, y, contentW, 124, 238);
        UiDraw.label(c, textRenderer, "YOUR VISUAL WORKSPACE", contentX + 16, y + 14, 0xFFF5F3FF);
        UiDraw.label(c, textRenderer, "Vanilla title, multiplayer, world selection and settings screens stay untouched.", contentX + 16, y + 34, 0xFF9895A8);
        UiDraw.label(c, textRenderer, "The menu opens only in-game. Changes are stored in config/gromvisuals.json.", contentX + 16, y + 49, 0xFF9895A8);
        button(c, contentX + 16, y + 77, 140, 31, "Edit HUD", mx, my, true);
        button(c, contentX + 166, y + 77, 160, 31, "Crosshair Studio", mx, my, false);
        button(c, contentX + 336, y + 77, 142, 31, cfg.orbitSparks ? "Sparks: ON" : "Sparks: OFF", mx, my, false);

        int y2 = y + 138;
        UiDraw.panel(c, contentX, y2, contentW, contentH - (y2 - contentY), 238);
        UiDraw.label(c, textRenderer, "STYLE SNAPSHOT", contentX + 16, y2 + 14, 0xFFF5F3FF);
        UiDraw.label(c, textRenderer, "Graphite surfaces", contentX + 16, y2 + 37, 0xFFB5B1C1);
        UiDraw.label(c, textRenderer, "Gold telemetry", contentX + 16, y2 + 54, 0xFFFFC247);
        UiDraw.label(c, textRenderer, "Violet controls", contentX + 16, y2 + 71, cfg.accentColor);
        UiDraw.rounded(c, contentX + contentW - 204, y2 + 22, 54, 54, 12, 0xFF0A0A0D);
        UiDraw.rounded(c, contentX + contentW - 191, y2 + 35, 28, 28, 7, cfg.accentColor);
        c.fill(contentX + contentW - 185, y2 + 43, contentX + contentW - 179, y2 + 52, 0xFFFFFFFF);
        c.fill(contentX + contentW - 182, y2 + 43, contentX + contentW - 173, y2 + 46, 0xFFFFFFFF);
    }

    private void renderHudTab(DrawContext c, GromConfig cfg, int mx, int my) {
        sectionTitle(c, "HUD modules", "Enable only the components you want on screen.");
        int gap = 8, cardW = (contentW - gap) / 2, rowH = 42, startY = contentY + 56;
        String[] names = {"FPS / Ping", "Coordinates", "Potion effects", "Keystrokes", "LMB / RMB + CPS", "Armor durability", "Target HUD", "Local mini-map", "Inventory preview", "Health warning", "Direction HUD", "Player vitals", "Biome info", "All HUD"};
        boolean[] state = {cfg.showInfo, cfg.showCoordinates, cfg.showPotionEffects, cfg.showKeystrokes, cfg.showClickHud,
                cfg.showArmor, cfg.showTargetHud, cfg.showMiniMap, cfg.showInventory, cfg.showHealthWarning, cfg.showDirection, cfg.showVitals, cfg.showBiome, cfg.hudEnabled};
        for (int i = 0; i < names.length; i++) {
            int col = i % 2, row = i / 2;
            int x = contentX + col * (cardW + gap), y = startY + row * (rowH + 5);
            UiDraw.panel(c, x, y, cardW, rowH, 234);
            UiDraw.label(c, textRenderer, names[i], x + 12, y + 8, 0xFFECE9F5);
            UiDraw.label(c, textRenderer, state[i] ? "ENABLED" : "DISABLED", x + 12, y + 25, state[i] ? 0xFF7CE0AE : 0xFF8A8597);
            toggle(c, x + cardW - 50, y + 11, state[i], cfg.accentColor);
        }
        int by = startY + 7 * (rowH + 5) + 1;
        button(c, contentX, by, 154, 32, "Open HUD editor", mx, my, true);
        button(c, contentX + 164, by, 152, 32, "Reset positions", mx, my, false);
    }

    private void renderZoomTab(DrawContext c, GromConfig cfg, int mx, int my) {
        sectionTitle(c, "Smooth camera zoom", "Hold the zoom key and use the wheel to fine-tune magnification.");
        int y = contentY + 58;
        UiDraw.panel(c, contentX, y, contentW, 96, 239);
        UiDraw.label(c, textRenderer, "ZOOM MODULE", contentX + 16, y + 16, 0xFFAAA6B8);
        UiDraw.label(c, textRenderer, cfg.zoomEnabled ? "Enabled" : "Disabled", contentX + 16, y + 36, cfg.zoomEnabled ? 0xFF7CE0AE : 0xFFFF7A85);
        button(c, contentX + contentW - 135, y + 28, 116, 32, cfg.zoomEnabled ? "Turn off" : "Turn on", mx, my, true);
        int y2 = y + 112;
        UiDraw.panel(c, contentX, y2, contentW, 176, 239);
        UiDraw.label(c, textRenderer, "MAGNIFICATION", contentX + 16, y2 + 15, 0xFFAAA6B8);
        UiDraw.centered(c, textRenderer, String.format(java.util.Locale.ROOT, "%.1f×", cfg.zoomTarget), contentX + contentW / 2, y2 + 41, 0xFFFFC247);
        button(c, contentX + contentW / 2 - 91, y2 + 69, 82, 32, "− 0.5×", mx, my, false);
        button(c, contentX + contentW / 2 + 9, y2 + 69, 82, 32, "+ 0.5×", mx, my, false);
        UiDraw.label(c, textRenderer, String.format(java.util.Locale.ROOT, "Min %.1f×     Max %.1f×", cfg.zoomMin, cfg.zoomMax), contentX + 16, y2 + 119, 0xFFB4B0C0);
        button(c, contentX + 16, y2 + 135, 91, 28, "Min −", mx, my, false);
        button(c, contentX + 116, y2 + 135, 91, 28, "Max +", mx, my, false);
        button(c, contentX + 216, y2 + 135, 112, 28, "Speed +", mx, my, false);
        UiDraw.label(c, textRenderer, "Keybinds can be changed in Minecraft → Options → Controls.", contentX + 2, y2 + 190, 0xFF898496);
    }

    private void renderCrosshairTab(DrawContext c, GromConfig cfg, int mx, int my) {
        sectionTitle(c, "Crosshair Studio", "Design a pixel crosshair and switch back to the vanilla one at any time.");
        int y = contentY + 60;
        UiDraw.panel(c, contentX, y, contentW, 86, 239);
        UiDraw.label(c, textRenderer, "CUSTOM CROSSHAIR", contentX + 16, y + 14, 0xFFAAA6B8);
        UiDraw.label(c, textRenderer, cfg.customCrosshairEnabled ? "Custom crosshair active" : "Minecraft default active", contentX + 16, y + 37, cfg.customCrosshairEnabled ? 0xFFBBA5FF : 0xFF7CE0AE);
        button(c, contentX + contentW - 138, y + 27, 120, 34, cfg.customCrosshairEnabled ? "Disable custom" : "Enable custom", mx, my, true);
        int y2 = y + 102;
        UiDraw.panel(c, contentX, y2, contentW, 142, 239);
        UiDraw.label(c, textRenderer, "PRESETS", contentX + 16, y2 + 15, 0xFFAAA6B8);
        button(c, contentX + 16, y2 + 42, 95, 34, "Plus", mx, my, false);
        button(c, contentX + 121, y2 + 42, 95, 34, "Dot", mx, my, false);
        button(c, contentX + 226, y2 + 42, 95, 34, "X shape", mx, my, false);
        button(c, contentX + 331, y2 + 42, 105, 34, "Circle", mx, my, false);
        button(c, contentX + 16, y2 + 87, 160, 34, "Open pixel editor", mx, my, true);
        button(c, contentX + 186, y2 + 87, 196, 34, "Minecraft Default Crosshair", mx, my, false);
        UiDraw.label(c, textRenderer, "Color and pixel scale are saved in your local config.", contentX + 2, y2 + 258, 0xFF898496);
    }

    private void renderCosmeticsTab(DrawContext c, GromConfig cfg, int mx, int my) {
        sectionTitle(c, "Local cosmetics", "Lightweight client-side particles; other players will not see these effects.");
        int y = contentY + 60;
        UiDraw.panel(c, contentX, y, contentW, 102, 239);
        UiDraw.rounded(c, contentX + 16, y + 17, 40, 40, 10, 0xFF24202E);
        UiDraw.centered(c, textRenderer, "✦", contentX + 36, y + 31, 0xFFFFC247);
        UiDraw.label(c, textRenderer, "Orbit Sparks", contentX + 70, y + 19, 0xFFF5F3FF);
        UiDraw.label(c, textRenderer, "Small electric particles orbit your local player.", contentX + 70, y + 39, 0xFF9895A8);
        button(c, contentX + contentW - 136, y + 30, 118, 34, cfg.orbitSparks ? "Enabled" : "Disabled", mx, my, true);
        int y2 = y + 118;
        UiDraw.panel(c, contentX, y2, contentW, 94, 239);
        UiDraw.label(c, textRenderer, "PARTICLE INTENSITY", contentX + 16, y2 + 17, 0xFFAAA6B8);
        UiDraw.label(c, textRenderer, String.format(java.util.Locale.ROOT, "%.2f", cfg.particleIntensity), contentX + 16, y2 + 40, 0xFFFFC247);
        button(c, contentX + 118, y2 + 30, 94, 32, "Decrease", mx, my, false);
        button(c, contentX + 220, y2 + 30, 94, 32, "Increase", mx, my, false);
        UiDraw.label(c, textRenderer, "3D wings / horns / halo models are not advertised as available yet.", contentX + 2, y2 + 112, 0xFF898496);
    }

    private void renderSettingsTab(DrawContext c, GromConfig cfg, int mx, int my) {
        sectionTitle(c, "Preferences", "Configuration lives locally. No telemetry or network calls are performed by this mod.");
        int y = contentY + 58;
        UiDraw.panel(c, contentX, y, contentW, 146, 239);
        UiDraw.label(c, textRenderer, "COLOR ACCENT", contentX + 16, y + 15, 0xFFAAA6B8);
        UiDraw.label(c, textRenderer, cfg.accentColor == 0xFFFFC247 ? "Gold" : "Violet", contentX + 16, y + 38, cfg.accentColor);
        button(c, contentX + 16, y + 62, 130, 32, "Violet accent", mx, my, false);
        button(c, contentX + 156, y + 62, 130, 32, "Gold accent", mx, my, false);
        UiDraw.label(c, textRenderer, "HUD opacity", contentX + 16, y + 112, 0xFFAAA6B8);
        button(c, contentX + 126, y + 101, 80, 30, "−", mx, my, false);
        UiDraw.centered(c, textRenderer, cfg.hudOpacity + "/255", contentX + 250, y + 111, 0xFFF5F3FF);
        button(c, contentX + 294, y + 101, 80, 30, "+", mx, my, false);
        button(c, contentX + 390, y + 101, 116, 30, cfg.animationsEnabled ? "Motion: ON" : "Motion: OFF", mx, my, false);
        int y2 = y + 162;
        UiDraw.panel(c, contentX, y2, contentW, 112, 239);
        UiDraw.label(c, textRenderer, "CONFIGURATION", contentX + 16, y2 + 15, 0xFFAAA6B8);
        button(c, contentX + 16, y2 + 37, 138, 34, "Export backup", mx, my, true);
        button(c, contentX + 164, y2 + 37, 138, 34, "Import backup", mx, my, false);
        button(c, contentX + 312, y2 + 37, 138, 34, "Reset settings", mx, my, false);
        UiDraw.label(c, textRenderer, "Backup file: config/gromvisuals-backup.json", contentX + 16, y2 + 83, 0xFF898496);
    }

    private void sectionTitle(DrawContext c, String title, String subtitle) {
        UiDraw.label(c, textRenderer, title, contentX, contentY + 5, 0xFFF5F3FF);
        UiDraw.label(c, textRenderer, subtitle, contentX, contentY + 23, 0xFF9895A8);
    }

    private void statCard(DrawContext c, int x, int y, int w, int h, String title, String value, String caption, int accent) {
        UiDraw.panel(c, x, y, w, h, 238);
        c.fill(x + 12, y + 14, x + 14, y + h - 14, accent);
        UiDraw.label(c, textRenderer, title, x + 23, y + 14, 0xFF9895A8);
        UiDraw.label(c, textRenderer, value, x + 23, y + 32, 0xFFF5F3FF);
        UiDraw.label(c, textRenderer, caption, x + 23, y + 58, 0xFF858194);
    }

    private void toggle(DrawContext c, int x, int y, boolean enabled, int accent) {
        UiDraw.rounded(c, x, y, 34, 19, 10, enabled ? accent : 0xFF34323D);
        UiDraw.rounded(c, x + (enabled ? 17 : 3), y + 3, 13, 13, 7, 0xFFF8F6FF);
    }

    private boolean button(DrawContext c, int x, int y, int w, int h, String label, int mx, int my, boolean primary) {
        boolean hover = UiDraw.inside(mx, my, x, y, w, h);
        int fill = primary ? (hover ? 0xFFA78BFA : 0xFF8B5CF6) : (hover ? 0xFF302C3A : 0xFF232229);
        UiDraw.rounded(c, x, y, w, h, 7, fill);
        UiDraw.outline(c, x, y, w, h, primary ? 0x44FFFFFF : 0x33FFFFFF);
        UiDraw.centered(c, textRenderer, label, x + w / 2, y + (h - 8) / 2, primary ? 0xFFFFFFFF : 0xFFE7E3EF);
        return hover;
    }

    private String tabIcon(Tab value) {
        return switch (value) {
            case DASHBOARD -> "◈";
            case HUD -> "▦";
            case ZOOM -> "⌕";
            case CROSSHAIR -> "+";
            case COSMETICS -> "✦";
            case SETTINGS -> "⚙";
        };
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x(), mouseY = click.y();
        int button = click.button();
        if (button != 0) return super.mouseClicked(click, doubled);
        if (UiDraw.inside(mouseX, mouseY, panelX + panelW - 36, panelY + 4, 34, 45)) { close(); return true; }
        int sideX = panelX + 12, sideY = panelY + 68;
        for (int i = 0; i < Tab.values().length; i++) {
            if (UiDraw.inside(mouseX, mouseY, sideX + 5, sideY + 29 + i * 43, 148, 39)) { tab = Tab.values()[i]; return true; }
        }
        GromConfig cfg = GromConfig.get();
        int gap = 8, cardW = (contentW - gap) / 2, rowH = 42, startY = contentY + 56;
        if (tab == Tab.DASHBOARD) {
            int y = contentY + 150;
            if (UiDraw.inside(mouseX, mouseY, contentX + 16, y + 77, 140, 31)) { client.setScreen(new HudEditorScreen(this)); return true; }
            if (UiDraw.inside(mouseX, mouseY, contentX + 166, y + 77, 160, 31)) { client.setScreen(new CrosshairStudioScreen(this)); return true; }
            if (UiDraw.inside(mouseX, mouseY, contentX + 336, y + 77, 142, 31)) { cfg.orbitSparks = !cfg.orbitSparks; GromConfig.save(); return true; }
        } else if (tab == Tab.HUD) {
            boolean[] states = {cfg.showInfo, cfg.showCoordinates, cfg.showPotionEffects, cfg.showKeystrokes, cfg.showClickHud, cfg.showArmor,
                    cfg.showTargetHud, cfg.showMiniMap, cfg.showInventory, cfg.showHealthWarning, cfg.showDirection, cfg.showVitals, cfg.showBiome, cfg.hudEnabled};
            for (int i = 0; i < states.length; i++) {
                int col = i % 2, row = i / 2;
                int x = contentX + col * (cardW + gap), y = startY + row * (rowH + 5);
                if (UiDraw.inside(mouseX, mouseY, x, y, cardW, rowH)) {
                    switch (i) {
                        case 0 -> cfg.showInfo = !cfg.showInfo;
                        case 1 -> cfg.showCoordinates = !cfg.showCoordinates;
                        case 2 -> cfg.showPotionEffects = !cfg.showPotionEffects;
                        case 3 -> cfg.showKeystrokes = !cfg.showKeystrokes;
                        case 4 -> cfg.showClickHud = !cfg.showClickHud;
                        case 5 -> cfg.showArmor = !cfg.showArmor;
                        case 6 -> cfg.showTargetHud = !cfg.showTargetHud;
                        case 7 -> cfg.showMiniMap = !cfg.showMiniMap;
                        case 8 -> cfg.showInventory = !cfg.showInventory;
                        case 9 -> cfg.showHealthWarning = !cfg.showHealthWarning;
                        case 10 -> cfg.showDirection = !cfg.showDirection;
                        case 11 -> cfg.showVitals = !cfg.showVitals;
                        case 12 -> cfg.showBiome = !cfg.showBiome;
                        case 13 -> cfg.hudEnabled = !cfg.hudEnabled;
                    }
                    GromConfig.save(); return true;
                }
            }
            int by = startY + 7 * (rowH + 5) + 1;
            if (UiDraw.inside(mouseX, mouseY, contentX, by, 154, 32)) { client.setScreen(new HudEditorScreen(this)); return true; }
            if (UiDraw.inside(mouseX, mouseY, contentX + 164, by, 152, 32)) { resetPositions(); return true; }
        } else if (tab == Tab.ZOOM) {
            int y = contentY + 58, y2 = y + 112;
            if (UiDraw.inside(mouseX, mouseY, contentX + contentW - 140, y + 24, 122, 42)) cfg.zoomEnabled = !cfg.zoomEnabled;
            else if (UiDraw.inside(mouseX, mouseY, contentX + contentW / 2 - 91, y2 + 69, 82, 32)) cfg.zoomTarget = Math.max(cfg.zoomMin, cfg.zoomTarget - 0.5f);
            else if (UiDraw.inside(mouseX, mouseY, contentX + contentW / 2 + 9, y2 + 69, 82, 32)) cfg.zoomTarget = Math.min(cfg.zoomMax, cfg.zoomTarget + 0.5f);
            else if (UiDraw.inside(mouseX, mouseY, contentX + 16, y2 + 135, 91, 28)) cfg.zoomMin = Math.max(1.0f, cfg.zoomMin - 0.25f);
            else if (UiDraw.inside(mouseX, mouseY, contentX + 116, y2 + 135, 91, 28)) cfg.zoomMax = Math.min(16.0f, cfg.zoomMax + 1.0f);
            else if (UiDraw.inside(mouseX, mouseY, contentX + 216, y2 + 135, 112, 28)) cfg.zoomSpeed = Math.min(2.5f, cfg.zoomSpeed + 0.1f);
            else return true;
            cfg.zoomTarget = Math.max(cfg.zoomMin, Math.min(cfg.zoomMax, cfg.zoomTarget));
            ZoomController.setConfiguredTarget(cfg.zoomTarget);
            GromConfig.save(); return true;
        } else if (tab == Tab.CROSSHAIR) {
            int y = contentY + 60, y2 = y + 102;
            if (UiDraw.inside(mouseX, mouseY, contentX + contentW - 140, y + 20, 124, 48)) cfg.customCrosshairEnabled = !cfg.customCrosshairEnabled;
            else if (UiDraw.inside(mouseX, mouseY, contentX + 16, y2 + 42, 95, 34)) applyCrosshairPreset("plus");
            else if (UiDraw.inside(mouseX, mouseY, contentX + 121, y2 + 42, 95, 34)) applyCrosshairPreset("dot");
            else if (UiDraw.inside(mouseX, mouseY, contentX + 226, y2 + 42, 95, 34)) applyCrosshairPreset("x");
            else if (UiDraw.inside(mouseX, mouseY, contentX + 331, y2 + 42, 105, 34)) applyCrosshairPreset("circle");
            else if (UiDraw.inside(mouseX, mouseY, contentX + 16, y2 + 87, 160, 34)) { client.setScreen(new CrosshairStudioScreen(this)); return true; }
            else if (UiDraw.inside(mouseX, mouseY, contentX + 186, y2 + 87, 196, 34)) { cfg.customCrosshairEnabled = false; GromConfig.save(); showToast("Vanilla crosshair restored"); return true; }
            else return true;
            GromConfig.save(); return true;
        } else if (tab == Tab.COSMETICS) {
            int y = contentY + 60, y2 = y + 118;
            if (UiDraw.inside(mouseX, mouseY, contentX + contentW - 140, y + 20, 124, 50)) cfg.orbitSparks = !cfg.orbitSparks;
            else if (UiDraw.inside(mouseX, mouseY, contentX + 118, y2 + 24, 94, 42)) cfg.particleIntensity = Math.max(0, cfg.particleIntensity - 0.15f);
            else if (UiDraw.inside(mouseX, mouseY, contentX + 220, y2 + 24, 94, 42)) cfg.particleIntensity = Math.min(1.5f, cfg.particleIntensity + 0.15f);
            else return true;
            GromConfig.save(); return true;
        } else if (tab == Tab.SETTINGS) {
            int y = contentY + 58, y2 = y + 162;
            if (UiDraw.inside(mouseX, mouseY, contentX + 16, y + 62, 130, 32)) cfg.accentColor = 0xFF8B5CF6;
            else if (UiDraw.inside(mouseX, mouseY, contentX + 156, y + 62, 130, 32)) cfg.accentColor = 0xFFFFC247;
            else if (UiDraw.inside(mouseX, mouseY, contentX + 126, y + 101, 80, 30)) cfg.hudOpacity = Math.max(70, cfg.hudOpacity - 15);
            else if (UiDraw.inside(mouseX, mouseY, contentX + 294, y + 101, 80, 30)) cfg.hudOpacity = Math.min(255, cfg.hudOpacity + 15);
            else if (UiDraw.inside(mouseX, mouseY, contentX + 390, y + 101, 116, 30)) cfg.animationsEnabled = !cfg.animationsEnabled;
            else if (UiDraw.inside(mouseX, mouseY, contentX + 16, y2 + 37, 138, 34)) { boolean ok = GromConfig.exportBackup(); showToast(ok ? "Backup exported" : "Export failed"); return true; }
            else if (UiDraw.inside(mouseX, mouseY, contentX + 164, y2 + 37, 138, 34)) { boolean ok = GromConfig.importBackup(); showToast(ok ? "Backup imported" : "Backup not found / invalid"); return true; }
            else if (UiDraw.inside(mouseX, mouseY, contentX + 312, y2 + 37, 138, 34)) { GromConfig.freshDefaults(); showToast("Settings reset"); return true; }
            else return true;
            GromConfig.save(); return true;
        }
        return true;
    }

    private void applyCrosshairPreset(String preset) {
        GromConfig cfg = GromConfig.get();
        boolean[] p = new boolean[441];
        switch (preset) {
            case "dot" -> p[10 * 21 + 10] = true;
            case "x" -> {
                for (int i = 7; i <= 13; i++) { p[i * 21 + i] = true; p[i * 21 + (20 - i)] = true; }
            }
            case "circle" -> {
                for (int y = 0; y < 21; y++) for (int x = 0; x < 21; x++) {
                    double d = Math.sqrt((x - 10) * (x - 10) + (y - 10) * (y - 10));
                    if (d >= 6.4 && d <= 7.4) p[y * 21 + x] = true;
                }
            }
            default -> {
                for (int i = 6; i <= 14; i++) if (i < 9 || i > 11) { p[10 * 21 + i] = true; p[i * 21 + 10] = true; }
                p[10 * 21 + 10] = true;
            }
        }
        cfg.crosshairPixels = p;
        cfg.customCrosshairEnabled = true;
        GromConfig.save();
    }

    private void resetPositions() {
        GromConfig.get().resetPositions();
        showToast("HUD positions reset");
    }

    private void showToast(String message) { toast = message; toastUntil = System.currentTimeMillis() + 1800; }

    @Override
    public boolean keyPressed(KeyInput input) {
        int keyCode = input.key();
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) { close(); return true; }
        return super.keyPressed(input);
    }

    @Override
    public void close() { if (client != null) client.setScreen(null); }

    @Override
    public boolean shouldPause() { return false; }
}
