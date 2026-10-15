package dev.gromclient.visuals;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

/** Grom Visuals dark-neon control center. */
public final class VisualsScreen extends Screen {
    private static final int PURPLE=0xFFB66BFF, WHITE=0xFFF6F2FF, MUTED=0xFFB5A9C4, PANEL=0xF20D0B13, ROW=0xEE191420;
    private int px,py,pw=730,ph=450,scroll,page;
    private final List<Setting> visible=new ArrayList<>();
    private record Setting(String label,String description,Getter getter,Setter setter){}
    @FunctionalInterface private interface Getter{boolean get(VisualConfig c);}
    @FunctionalInterface private interface Setter{void set(VisualConfig c,boolean v);}
    public VisualsScreen(){super(Text.literal("Grom Visuals"));}
    @Override protected void init(){pw=Math.min(790,width-24);pw=Math.max(560,pw);ph=Math.min(490,height-24);ph=Math.max(350,ph);px=(width-pw)/2;py=(height-ph)/2;scroll=0;}
    @Override public void render(DrawContext d,int mx,int my,float delta){
        d.fill(0,0,width,height,0xE9080710);for(int y=0;y<height;y+=5)d.fill(0,y,width,y+1,0x123D1761);
        d.fill(px-1,py-1,px+pw+1,py+ph+1,PURPLE);d.fill(px,py,px+pw,py+ph,PANEL);d.fill(px,py,px+pw,py+76,0xFF171020);d.fill(px,py,px+4,py+ph,PURPLE);
        d.fill(px+18,py+15,px+55,py+52,0xFF352044);d.fill(px+18,py+15,px+55,py+18,PURPLE);d.drawText(textRenderer,"G",px+31,py+26,WHITE,false);
        d.drawText(textRenderer,"GROM VISUALS",px+68,py+17,WHITE,false);d.drawText(textRenderer,"VISUAL CONTROL CENTER  /  FABRIC 1.21.11",px+68,py+37,MUTED,false);d.drawText(textRenderer,"RIGHT SHIFT",px+pw-88,py+25,MUTED,false);
        int nx=px+12,ny=py+88,nw=142;d.fill(nx,ny,nx+nw,py+ph-16,0xB9090710);
        String[] tabs={"HUD MODULES","VISUALS","COSMETICS","GENERAL"};String[] sub={"Overlay widgets","Style & effects","Appearance","Client options"};
        for(int i=0;i<4;i++){int y=ny+8+i*53;boolean sel=page==i;d.fill(nx+7,y,nx+nw-7,y+43,sel?0xFF342440:0x00201020);if(sel)d.fill(nx+7,y,nx+10,y+43,PURPLE);d.drawText(textRenderer,tabs[i],nx+17,y+8,sel?WHITE:MUTED,false);d.drawText(textRenderer,sub[i],nx+17,y+23,0xFF8F829F,false);}
        int cx=px+169,cw=pw-185;d.drawText(textRenderer,new String[]{"HUD MODULES","VISUAL SETTINGS","COSMETICS","GENERAL SETTINGS"}[page],cx,py+91,WHITE,false);d.drawText(textRenderer,"Customize your client experience",cx,py+105,MUTED,false);
        int ay=py+123,aw=(cw-8)/2;action(d,cx,ay,aw,"↔  MOVE HUD","Drag widgets anywhere",PURPLE,mx,my);action(d,cx+aw+8,ay,aw,"✣  CROSSHAIR STUDIO","Draw your own crosshair",0xFF35DDF5,mx,my);
        visible.clear();populate();int top=py+176,bottom=py+ph-45;d.enableScissor(cx,top,px+pw-14,bottom);
        for(int i=0;i<visible.size();i++){Setting s=visible.get(i);int y=top+i*45-scroll;if(y+39<top||y>bottom)continue;boolean hover=mx>=cx&&mx<=px+pw-14&&my>=y&&my<=y+39;d.fill(cx,y,px+pw-14,y+39,hover?0xFF2A2035:ROW);d.fill(cx,y,cx+2,y+39,hover?PURPLE:0xFF302638);d.drawText(textRenderer,s.label(),cx+11,y+7,WHITE,false);d.drawText(textRenderer,s.description(),cx+11,y+22,MUTED,false);boolean on=s.getter().get(GromVisualsClient.CONFIG);int sx=px+pw-55;d.fill(sx,y+12,sx+28,y+27,on?0xFF7544A6:0xFF49434F);d.fill(sx+(on?16:3),y+14,sx+(on?25:12),y+25,WHITE);}
        d.disableScissor();int fy=py+ph-31;d.fill(cx,fy-5,px+pw-14,fy-4,0x664D3866);d.drawText(textRenderer,"THEME  "+GromVisualsClient.CONFIG.theme.toUpperCase(),cx,fy+5,MUTED,false);d.drawText(textRenderer,"CONFIG SAVES AUTOMATICALLY",cx+150,fy+5,0xFF81758F,false);d.fill(px+pw-88,fy-1,px+pw-14,fy+21,0xFF492A70);d.drawText(textRenderer,"DONE",px+pw-66,fy+6,WHITE,false);
    }
    private void action(DrawContext d,int x,int y,int w,String title,String sub,int accent,int mx,int my){boolean hover=mx>=x&&mx<=x+w&&my>=y&&my<=y+43;d.fill(x,y,x+w,y+43,hover?0xFF30223D:0xFF211A29);d.fill(x,y,x+w,y+2,accent);d.drawText(textRenderer,title,x+8,y+8,WHITE,false);d.drawText(textRenderer,sub,x+8,y+25,MUTED,false);}
    private void add(String l,String d,Getter g,Setter s){visible.add(new Setting(l,d,g,s));}
    private void populate(){switch(page){
      case 0->{add("HUD title","GROM VISUALS heading",c->c.hudTitle,(c,v)->c.hudTitle=v);add("FPS counter","Current frames per second",c->c.fps,(c,v)->c.fps=v);add("Coordinates XYZ","Player coordinates",c->c.coordinates,(c,v)->c.coordinates=v);add("CPS counter","Left and right clicks per second",c->c.cps,(c,v)->c.cps=v);add("Keystrokes","W A S D and jump overlay",c->c.keystrokes,(c,v)->c.keystrokes=v);add("Mouse buttons","Show LMB / RMB",c->c.keystrokeMouseButtons,(c,v)->c.keystrokeMouseButtons=v);add("Direction","Current facing direction",c->c.direction,(c,v)->c.direction=v);add("Ping","Server latency",c->c.ping,(c,v)->c.ping=v);add("Clock","In-game time",c->c.clock,(c,v)->c.clock=v);add("Movement speed","Horizontal movement speed",c->c.speed,(c,v)->c.speed=v);add("Biome","Current biome name",c->c.biome,(c,v)->c.biome=v);add("Armor status","Armor items and durability",c->c.armor,(c,v)->c.armor=v);add("Held item durability","Tool durability display",c->c.durability,(c,v)->c.durability=v);add("Potion effects","Effect names and timers",c->c.potionEffects,(c,v)->c.potionEffects=v);add("Custom crosshair","Use client-drawn crosshair",c->c.crosshair,(c,v)->c.crosshair=v);add("Target HUD","Health and distance",c->c.targetHud,(c,v)->c.targetHud=v);add("Saturation HUD","Food and saturation",c->c.saturationHud,(c,v)->c.saturationHud=v);add("Low health warning","Visual warning at low health",c->c.lowHealthWarning,(c,v)->c.lowHealthWarning=v);add("Armor notifier","Warn about low durability",c->c.armorNotifier,(c,v)->c.armorNotifier=v);}
      case 1->{add("Compact HUD","Tighter widget spacing",c->c.compactHud,(c,v)->c.compactHud=v);add("HUD animations","Animated visual states",c->c.animations,(c,v)->c.animations=v);add("Show over menus","Keep HUD visible in screens",c->c.showInScreens,(c,v)->c.showInScreens=v);add("Purple theme","Violet neon accent",c->"Purple".equals(c.theme),(c,v)->{if(v){c.theme="Purple";c.accent=0xFFB66BFF;}});add("Neon theme","Cyan neon accent",c->"Neon".equals(c.theme),(c,v)->{if(v){c.theme="Neon";c.accent=0xFF35E6FF;}});add("GROM theme","Electric orange accent",c->"GROM".equals(c.theme),(c,v)->{if(v){c.theme="GROM";c.accent=0xFFFF7A35;}});add("White theme","Monochrome accent",c->"White".equals(c.theme),(c,v)->{if(v){c.theme="White";c.accent=0xFFFFFFFF;}});}
      case 2->{add("China Hat","Cosmetic toggle; renderer not implemented",c->c.cosmeticChinaHat,(c,v)->c.cosmeticChinaHat=v);add("Halo","Cosmetic toggle; renderer not implemented",c->c.cosmeticHalo,(c,v)->c.cosmeticHalo=v);add("Angel Wings","Cosmetic toggle; renderer not implemented",c->c.cosmeticWings,(c,v)->c.cosmeticWings=v);add("Devil Horns","Cosmetic toggle; renderer not implemented",c->c.cosmeticDevilHorns,(c,v)->c.cosmeticDevilHorns=v);add("Cosmetic particles","Cosmetic toggle; renderer not implemented",c->c.cosmeticParticles,(c,v)->c.cosmeticParticles=v);}
      default->{add("HUD scale","Toggle larger HUD scale",c->c.hudScale>1.0f,(c,v)->c.hudScale=v?1.2f:1.0f);add("Reset HUD positions","Restore all widgets to default positions",c->false,(c,v)->reset(c));add("Restore Minecraft crosshair","Disable custom crosshair and show vanilla",c->c.customCrosshair,(c,v)->{c.customCrosshair=false;c.crosshair=false;});}
    }}
    private void reset(VisualConfig c){c.infoX=c.infoY=c.cpsX=c.cpsY=c.keysX=c.keysY=c.armorX=c.armorY=0;c.durabilityX=c.durabilityY=c.effectsX=c.effectsY=c.crosshairX=c.crosshairY=0;c.targetX=c.targetY=c.saturationX=c.saturationY=c.warningX=c.warningY=0;}
    @Override public boolean mouseClicked(Click click,boolean dbl){if(click.button()!=0)return super.mouseClicked(click,dbl);int mx=(int)click.x(),my=(int)click.y(),nx=px+12,ny=py+88,nw=142;for(int i=0;i<4;i++){int y=ny+8+i*53;if(mx>=nx+7&&mx<=nx+nw-7&&my>=y&&my<=y+43){page=i;scroll=0;return true;}}
      int cx=px+169,cw=pw-185,ay=py+123,aw=(cw-8)/2;if(my>=ay&&my<=ay+43){if(mx>=cx&&mx<=cx+aw){GromVisualsClient.CONFIG.editHud=true;GromVisualsClient.CONFIG.save();client.setScreen(new HudEditorScreen());return true;}if(mx>=cx+aw+8&&mx<=cx+cw){client.setScreen(new CrosshairEditorScreen());return true;}}
      int top=py+176,bottom=py+ph-45;if(my>=top&&my<=bottom&&mx>=cx&&mx<=px+pw-14){int i=(my-top+scroll)/45;if(i>=0&&i<visible.size()){Setting s=visible.get(i);VisualConfig c=GromVisualsClient.CONFIG;if(page==3&&i==1)reset(c);else s.setter().set(c,!s.getter().get(c));if(s.label().equals("Custom crosshair")&&c.crosshair)client.setScreen(new CrosshairEditorScreen());c.save();return true;}}
      int fy=py+ph-31;if(my>=fy-1&&my<=fy+21&&mx>=px+pw-88&&mx<=px+pw-14){close();return true;}return super.mouseClicked(click,dbl);}
    @Override public boolean mouseScrolled(double mx,double my,double h,double v){int max=Math.max(0,visible.size()*45-(ph-221));scroll=Math.max(0,Math.min(max,scroll-(int)(v*45)));return true;}
    @Override public boolean shouldPause(){return false;}
}
