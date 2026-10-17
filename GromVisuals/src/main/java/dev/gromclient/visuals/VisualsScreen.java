package dev.gromclient.visuals;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/** A custom client-side control center: layered panels, rounded cards and subtle animated hover states. */
public final class VisualsScreen extends Screen {
    private static final int WHITE = 0xFFF5F2FF;
    private static final int MUTED = 0xFFAAA2BC;
    private static final int PURPLE = 0xFFB66BFF;
    private static final int BG = 0xF6090A11;
    private static final int CARD = 0xE9161722;
    private static final int CARD_HOVER = 0xF0252034;
    private int px, py, pw, ph, scroll, page;
    private int sideW = 166;
    private int contentX, contentW;
    private long pageChangedAt = System.currentTimeMillis();
    private final List<Setting> visible = new ArrayList<>();

    private record Setting(String label, String description, Getter getter, Setter setter) {}
    @FunctionalInterface private interface Getter { boolean get(VisualConfig c); }
    @FunctionalInterface private interface Setter { void set(VisualConfig c, boolean value); }

    public VisualsScreen() { super(Text.literal("Grom Visuals Control Center")); }

    @Override protected void init() {
        pw = Math.min(910, width - 24);
        ph = Math.min(540, height - 24);
        px = (width - pw) / 2;
        py = (height - ph) / 2;
        sideW = Math.max(132, Math.min(166, pw / 5));
        contentX = px + sideW + 28;
        contentW = pw - sideW - 46;
        scroll = 0;
    }

    @Override public void render(DrawContext d, int mx, int my, float delta) {
        long ms = System.currentTimeMillis();
        float breathe = (float)(0.5 + 0.5 * Math.sin(ms / 850.0));
        float transition = Math.min(1.0f, Math.max(0.0f, (ms - pageChangedAt) / 210.0f));
        int slide = Math.round((1.0f - transition) * 10.0f);
        int accent = GromVisualsClient.CONFIG.accent;
        // Background dimmer with restrained, shifting ambient light bands.
        d.fill(0, 0, width, height, 0xD9070810);
        for (int i = 0; i < 8; i++) {
            int y = (int)((i * 79L + ms / 48) % Math.max(1, height));
            d.fill(0, y, width, y + 1, 0x083A2363);
        }
        rounded(d, px - 2, py - 2, pw + 4, ph + 4, 18, 0x553E2759);
        rounded(d, px, py, pw, ph, 16, BG);
        rounded(d, px + 1, py + 1, pw - 2, ph - 2, 15, 0xF80D0F18);
        rounded(d, px + 16, py + 14, 40, 40, 12, 0xFF231A34);
        rounded(d, px + 17, py + 15, 38, 38, 11, accent);
        rounded(d, px + 20, py + 18, 32, 32, 9, 0xFF15131E);
        d.drawCenteredTextWithShadow(textRenderer, "G", px + 36, py + 27, accent);
        d.drawTextWithShadow(textRenderer, "GROM VISUALS", px + 67, py + 17, WHITE);
        d.drawText(textRenderer, "VISUAL CLIENT  /  CONTROL CENTER", px + 68, py + 35, MUTED, false);
        d.drawText(textRenderer, "1.5.0  •  FABRIC 1.21.11", px + 68, py + 49, 0xFF827A96, false);
        rounded(d, px + pw - 41, py + 20, 24, 24, 8, hot(mx, my, px + pw - 41, py + 20, 24, 24) ? 0xFF3A233F : 0xFF1B1B27);
        d.drawCenteredTextWithShadow(textRenderer, "×", px + pw - 29, py + 27, WHITE);

        // Left navigation column.
        rounded(d, px + 14, py + 68, sideW, ph - 84, 12, 0xA70C0D15);
        String[] tabs = {"OVERVIEW", "HUD MODULES", "VISUALS", "COSMETICS", "GENERAL"};
        String[] symbols = {"◈", "▦", "✦", "◇", "⚙"};
        String[] subtitles = {"Your setup", "Widgets & info", "Style & zoom", "Player effects", "Client options"};
        for (int i = 0; i < tabs.length; i++) {
            int y = py + 82 + i * 57;
            boolean selected = page == i;
            boolean hover = hot(mx, my, px + 22, y, sideW - 16, 47);
            if (selected) {
                rounded(d, px + 22, y, sideW - 16, 47, 10, 0xFF2B1D3A);
                rounded(d, px + 22, y + 7, 3, 33, 2, accent);
                rounded(d, px + 32, y + 8, 29, 29, 8, 0xFF38264D);
            } else if (hover) rounded(d, px + 22, y, sideW - 16, 47, 10, 0xFF191824);
            d.drawCenteredTextWithShadow(textRenderer, symbols[i], px + 46, y + 9, selected ? accent : 0xFF8D849F);
            d.drawText(textRenderer, tabs[i], px + 66, y + 7, selected ? WHITE : 0xFFD2CCDF, false);
            d.drawText(textRenderer, subtitles[i], px + 66, y + 23, 0xFF827B91, false);
        }
        int footY = py + ph - 54;
        rounded(d, px + 24, footY, sideW - 20, 35, 9, 0xFF181720);
        rounded(d, px + 24, footY, 3, 35, 2, accent);
        d.drawText(textRenderer, "●", px + 35, footY + 13, 0xFF6EE7A8, false);
        d.drawText(textRenderer, "CONFIG SAVED", px + 48, footY + 7, 0xFFD8D3E5, false);
        d.drawText(textRenderer, "LOCAL PROFILE", px + 48, footY + 20, 0xFF827B91, false);

        // Main heading and page subheading.
        String[] titles = {"Welcome back", "HUD modules", "Visual settings", "Cosmetics studio", "General settings"};
        String[] descriptions = {
                "Make your game feel like your own.",
                "Choose what appears on your screen.",
                "Personalize appearance, motion and zoom.",
                "Animated client-side effects around your player.",
                "Layout, configuration and convenience."
        };
        d.drawTextWithShadow(textRenderer, titles[page], contentX + slide, py + 78, WHITE);
        d.drawText(textRenderer, descriptions[page], contentX + slide, py + 94, MUTED, false);

        // Two large shortcut cards with soft accents and animated focus.
        int actionY = py + 116;
        int actionGap = 10;
        int actionW = (contentW - actionGap) / 2;
        action(d, mx, my, contentX, actionY, actionW, 50, "HUD EDITOR", "Drag widgets freely", accent, breathe, "↗");
        action(d, mx, my, contentX + actionW + actionGap, actionY, actionW, 50,
                "CROSSHAIR STUDIO", "Draw or restore vanilla", 0xFF4EDCF2, breathe, "＋");

        visible.clear(); populate();
        int listTop = py + 178;
        int listBottom = py + ph - 48;
        int rowHeight = 49, rowGap = 7;
        d.enableScissor(contentX, listTop, px + pw - 15, listBottom);
        for (int i = 0; i < visible.size(); i++) {
            Setting setting = visible.get(i);
            int y = listTop + i * (rowHeight + rowGap) - scroll;
            if (y + rowHeight < listTop || y > listBottom) continue;
            boolean hover = hot(mx, my, contentX, y, contentW, rowHeight);
            rounded(d, contentX, y, contentW, rowHeight, 10, hover ? CARD_HOVER : CARD);
            rounded(d, contentX + 1, y + 10, 3, rowHeight - 20, 2,
                    hover ? accent : (setting.getter().get(GromVisualsClient.CONFIG) ? 0xFF6E4CA1 : 0xFF373441));
            d.drawText(textRenderer, setting.label(), contentX + 13 + slide, y + 9, WHITE, false);
            String description = setting.description();
            if (setting.label().equals("Zoom strength")) description = "Hold C and use the wheel  •  current: " + String.format(java.util.Locale.ROOT, "%.1fx", GromVisualsClient.CONFIG.zoomLevel);
            d.drawText(textRenderer, trim(description, Math.max(18, (contentW - 102) / 6)), contentX + 13 + slide, y + 27, MUTED, false);
            boolean on = setting.getter().get(GromVisualsClient.CONFIG);
            int sx = px + pw - 57, sy = y + 15;
            rounded(d, sx, sy, 34, 19, 10, on ? accent : 0xFF3B3A47);
            int knobX = on ? sx + 18 : sx + 3;
            rounded(d, knobX, sy + 3, 13, 13, 7, 0xFFF8F5FF);
            if (setting.label().equals("Zoom strength")) {
                d.drawText(textRenderer, "" + Math.round(GromVisualsClient.CONFIG.zoomLevel) + "x", sx - 29, sy + 5, accent, false);
            }
        }
        d.disableScissor();

        // Bottom bar stays minimal and gives the UI a clear finish line.
        int fy = py + ph - 35;
        d.fill(contentX, fy - 4, px + pw - 16, fy - 3, 0x443E3153);
        d.drawText(textRenderer, "SMOOTH UI  •  AUTO-SAVE ENABLED", contentX, fy + 4, 0xFF827B91, false);
        rounded(d, px + pw - 141, fy - 2, 63, 25, 8, hot(mx, my, px + pw - 141, fy - 2, 63, 25) ? 0xFF39243F : 0xFF1B1925);
        d.drawCenteredTextWithShadow(textRenderer, "RESET", px + pw - 109, fy + 6, MUTED);
        rounded(d, px + pw - 70, fy - 2, 54, 25, 8, hot(mx, my, px + pw - 70, fy - 2, 54, 25) ? 0xFF8C57C3 : 0xFF5B3584);
        d.drawCenteredTextWithShadow(textRenderer, "DONE", px + pw - 43, fy + 6, WHITE);
    }

    private void action(DrawContext d, int mx, int my, int x, int y, int w, int h,
                        String title, String sub, int accent, float breathe, String glyph) {
        boolean hover = hot(mx, my, x, y, w, h);
        rounded(d, x, y, w, h, 11, hover ? 0xFF242033 : 0xE8151620);
        rounded(d, x + 1, y + 1, w - 2, 2, 1, accent);
        rounded(d, x + 10, y + 11, 27, 27, 8, 0xFF2A2139);
        d.drawCenteredTextWithShadow(textRenderer, glyph, x + 23, y + 20, accent);
        d.drawText(textRenderer, title, x + 45, y + 9, WHITE, false);
        d.drawText(textRenderer, sub, x + 45, y + 27, MUTED, false);
        if (hover) rounded(d, x + w - 7, y + 8, 2, h - 16, 1, (int)(0x55 + 45 * breathe) << 24 | (accent & 0x00FFFFFF));
    }

    private void populate() {
        switch (page) {
            case 0 -> {
                add("FPS counter", "Current frames per second", c->c.fps, (c,v)->c.fps=v);
                add("CPS counter", "Left and right clicks over the last second", c->c.cps, (c,v)->c.cps=v);
                add("Keystrokes", "W / A / S / D and jump status", c->c.keystrokes, (c,v)->c.keystrokes=v);
                add("Mini map", "Local terrain radar · M changes map scale", c->c.miniMap, (c,v)->c.miniMap=v);
                add("Target HUD", "Health and distance of the targeted entity", c->c.targetHud, (c,v)->c.targetHud=v);
                add("Halo aura", "Animated cosmetic around the player", c->c.cosmeticHalo, (c,v)->c.cosmeticHalo=v);
            }
            case 1 -> {
                add("HUD title", "Compact GROM VISUALS label", c->c.hudTitle, (c,v)->c.hudTitle=v);
                add("FPS counter", "Current frames per second", c->c.fps, (c,v)->c.fps=v);
                add("Coordinates XYZ", "Player position in the world", c->c.coordinates, (c,v)->c.coordinates=v);
                add("CPS counter", "Left and right clicks over the last second", c->c.cps, (c,v)->c.cps=v);
                add("Keystrokes", "W / A / S / D and jump status", c->c.keystrokes, (c,v)->c.keystrokes=v);
                add("Mouse buttons", "Animated LMB and RMB indicators", c->c.keystrokeMouseButtons, (c,v)->c.keystrokeMouseButtons=v);
                add("Mini map", "Local terrain radar · M changes map scale", c->c.miniMap, (c,v)->c.miniMap=v);
                add("Direction", "Compass direction", c->c.direction, (c,v)->c.direction=v);
                add("Ping", "Latency to the current server", c->c.ping, (c,v)->c.ping=v);
                add("Clock", "Real-world clock overlay", c->c.clock, (c,v)->c.clock=v);
                add("Movement speed", "Horizontal movement speed", c->c.speed, (c,v)->c.speed=v);
                add("Biome", "Current biome name", c->c.biome, (c,v)->c.biome=v);
                add("Armor status", "Armor pieces and remaining durability", c->c.armor, (c,v)->c.armor=v);
                add("Held item durability", "Durability of the current tool", c->c.durability, (c,v)->c.durability=v);
                add("Potion effects", "Active status effects and timers", c->c.potionEffects, (c,v)->c.potionEffects=v);
                add("Custom crosshair", "Draw or restore the vanilla crosshair", c->c.crosshair, (c,v)->c.crosshair=v);
                add("Target HUD", "Health and distance of the targeted entity", c->c.targetHud, (c,v)->c.targetHud=v);
                add("Food & saturation", "Food level and saturation value", c->c.saturationHud, (c,v)->c.saturationHud=v);
                add("Low health warning", "Subtle warning at low health", c->c.lowHealthWarning, (c,v)->c.lowHealthWarning=v);
                add("Armor notifier", "Warn when equipped armor is nearly broken", c->c.armorNotifier, (c,v)->c.armorNotifier=v);
            }
            case 2 -> {
                add("Compact HUD", "Tighter widget spacing", c->c.compactHud, (c,v)->c.compactHud=v);
                add("HUD animations", "Animate visual states and indicators", c->c.animations, (c,v)->c.animations=v);
                add("Show over menus", "Keep the Grom HUD visible on GUI screens", c->c.showInScreens, (c,v)->c.showInScreens=v);
                add("Zoom strength", "Hold C and scroll to change magnification", c->c.zoomLevel>=4.0f, (c,v)->{c.zoomLevel=v?6.0f:3.0f;c.save();});
                add("Purple theme", "Electric violet accents", c->"Purple".equals(c.theme), (c,v)->{if(v){c.theme="Purple";c.accent=0xFFB66BFF;}});
                add("Neon theme", "Cool cyan accents", c->"Neon".equals(c.theme), (c,v)->{if(v){c.theme="Neon";c.accent=0xFF35E6FF;}});
                add("GROM theme", "Orange energy accents", c->"GROM".equals(c.theme), (c,v)->{if(v){c.theme="GROM";c.accent=0xFFFF7A35;}});
                add("White theme", "Clean monochrome accents", c->"White".equals(c.theme), (c,v)->{if(v){c.theme="White";c.accent=0xFFFFFFFF;}});
            }
            case 3 -> {
                add("Halo aura", "Floating orbiting light particles above your player", c->c.cosmeticHalo, (c,v)->c.cosmeticHalo=v);
                add("China hat sparkle", "Small playful particles above the head", c->c.cosmeticChinaHat, (c,v)->c.cosmeticChinaHat=v);
                add("Wings aura", "Twin soul particles behind your player", c->c.cosmeticWings, (c,v)->c.cosmeticWings=v);
                add("Devil horn flames", "Animated flame accents near the head", c->c.cosmeticDevilHorns, (c,v)->c.cosmeticDevilHorns=v);
                add("Cosmic particles", "Orbiting enchantment particles", c->c.cosmeticParticles, (c,v)->c.cosmeticParticles=v);
            }
            default -> {
                add("HUD scale", "Switch between standard and enlarged HUD", c->c.hudScale>1.0f, (c,v)->c.hudScale=v?1.2f:1.0f);
                add("Panel opacity", "Switch between translucent and solid cards", c->c.panelAlpha>=200, (c,v)->c.panelAlpha=v?235:160);
                add("Reset HUD positions", "Restore widget groups to default positions", c->false, (c,v)->resetPositions(c));
                add("Restore Minecraft crosshair", "Disable custom crosshair and restore vanilla", c->c.customCrosshair, (c,v)->{c.customCrosshair=false;c.crosshair=false;});
            }
        }
    }

    private void add(String label, String description, Getter getter, Setter setter) { visible.add(new Setting(label, description, getter, setter)); }

    private void resetPositions(VisualConfig c) {
        c.infoX=c.infoY=c.cpsX=c.cpsY=c.keysX=c.keysY=c.armorX=c.armorY=0;
        c.durabilityX=c.durabilityY=c.effectsX=c.effectsY=c.crosshairX=c.crosshairY=0;
        c.targetX=c.targetY=c.saturationX=c.saturationY=c.warningX=c.warningY=0;
    }

    @Override public boolean mouseClicked(Click click, boolean doubleClick) {
        if (click.button() != 0) return super.mouseClicked(click, doubleClick);
        int mx=(int)click.x(), my=(int)click.y();
        if (hot(mx,my,px+pw-41,py+20,24,24)) { close(); return true; }
        for (int i=0;i<5;i++) {
            int y=py+82+i*57;
            if (hot(mx,my,px+22,y,sideW-16,47)) { page=i; scroll=0; pageChangedAt=System.currentTimeMillis(); return true; }
        }
        int actionY=py+116, gap=10, actionW=(contentW-gap)/2;
        if (hot(mx,my,contentX,actionY,actionW,50)) {
            GromVisualsClient.CONFIG.editHud=true; GromVisualsClient.CONFIG.save(); client.setScreen(new HudEditorScreen()); return true;
        }
        if (hot(mx,my,contentX+actionW+gap,actionY,actionW,50)) { client.setScreen(new CrosshairEditorScreen()); return true; }
        int fy=py+ph-35;
        if (hot(mx,my,px+pw-141,fy-2,63,25)) { resetPositions(GromVisualsClient.CONFIG); GromVisualsClient.CONFIG.save(); return true; }
        if (hot(mx,my,px+pw-70,fy-2,54,25)) { close(); return true; }
        int top=py+178, bottom=py+ph-48, rowHeight=49, rowGap=7;
        if (my>=top && my<=bottom && mx>=contentX && mx<=px+pw-15) {
            int i=(my-top+scroll)/(rowHeight+rowGap);
            if (i>=0 && i<visible.size()) {
                Setting setting=visible.get(i); VisualConfig config=GromVisualsClient.CONFIG;
                setting.setter().set(config,!setting.getter().get(config));
                if (setting.label().equals("Custom crosshair")) {
                    if (config.crosshair) client.setScreen(new CrosshairEditorScreen());
                    else { config.customCrosshair=false; config.save(); }
                }
                config.save(); return true;
            }
        }
        return super.mouseClicked(click,doubleClick);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        int rowHeight=49,rowGap=7,listHeight=py+ph-48-(py+178);
        int max=Math.max(0,visible.size()*(rowHeight+rowGap)-listHeight);
        scroll=Math.max(0,Math.min(max,scroll-(int)(vertical*42)));
        return true;
    }

    private static boolean hot(int mx,int my,int x,int y,int w,int h) { return mx>=x && mx<x+w && my>=y && my<y+h; }
    private static String trim(String value,int max) { return value.length()<=max?value:value.substring(0,Math.max(1,max-1))+"…"; }

    /** Pixel-smoothed rounded rectangle helper compatible with DrawContext on 1.21.11. */
    private static void rounded(DrawContext d,int x,int y,int w,int h,int radius,int color) {
        if (w<=0 || h<=0) return;
        int r=Math.max(0,Math.min(radius,Math.min(w/2,h/2)));
        for (int iy=0;iy<h;iy++) {
            int inset=0;
            if (iy<r) { double dy=r-iy-0.5; inset=Math.max(0,r-(int)Math.floor(Math.sqrt(Math.max(0,r*r-dy*dy)))); }
            else if (iy>=h-r) { double dy=iy-(h-r)+0.5; inset=Math.max(0,r-(int)Math.floor(Math.sqrt(Math.max(0,r*r-dy*dy)))); }
            d.fill(x+inset,y+iy,x+w-inset,y+iy+1,color);
        }
    }

    @Override public boolean shouldPause() { return false; }
}
