package dev.gromclient.visuals.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.registry.Registries;

/** Small north-up cache around the local player, refreshed only a few times per second. */
public final class MiniMapCache {
    public static final int CELLS = 21;
    private static final int[] COLORS = new int[CELLS * CELLS];
    private static int lastCenterX = Integer.MIN_VALUE;
    private static int lastCenterZ = Integer.MIN_VALUE;
    private static int ticks;

    static {
        java.util.Arrays.fill(COLORS, 0xFF25252B);
    }

    private MiniMapCache() { }

    public static void tick(MinecraftClient client) {
        if (client.world == null || client.player == null) return;
        if (++ticks % 5 != 0) return;
        int centerX = client.player.getBlockX();
        int centerZ = client.player.getBlockZ();
        if (centerX == lastCenterX && centerZ == lastCenterZ && ticks % 20 != 0) return;
        lastCenterX = centerX;
        lastCenterZ = centerZ;
        for (int dz = -CELLS / 2; dz <= CELLS / 2; dz++) {
            for (int dx = -CELLS / 2; dx <= CELLS / 2; dx++) {
                int x = centerX + dx;
                int z = centerZ + dz;
                int color = 0xFF202027;
                if (client.world.isChunkLoaded(x >> 4, z >> 4)) {
                    int topY = client.world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
                    if (topY > client.world.getBottomY()) {
                        var state = client.world.getBlockState(new BlockPos(x, topY - 1, z));
                        String id = Registries.BLOCK.getId(state.getBlock()).getPath();
                        if (id.contains("water") || id.contains("kelp") || id.contains("seagrass")) color = 0xFF286B83;
                        else if (id.contains("lava")) color = 0xFFE97731;
                        else if (id.contains("grass") || id.contains("leaves") || id.contains("moss") || id.contains("vine")) color = 0xFF467A46;
                        else if (id.contains("sand") || id.contains("terracotta") || id.contains("gravel")) color = 0xFFC2A46A;
                        else if (id.contains("snow") || id.contains("ice") || id.contains("quartz")) color = 0xFFB7C5CA;
                        else if (id.contains("deepslate") || id.contains("blackstone")) color = 0xFF4F5058;
                        else if (id.contains("netherrack") || id.contains("crimson") || id.contains("warped")) color = 0xFF8D454A;
                        else if (id.contains("end_stone")) color = 0xFFB0AC68;
                        else if (id.contains("stone") || id.contains("andesite") || id.contains("diorite")) color = 0xFF777982;
                        else if (id.contains("dirt") || id.contains("podzol") || id.contains("mud")) color = 0xFF806047;
                        else color = 0xFF5B6747;
                    }
                }
                COLORS[(dz + CELLS / 2) * CELLS + dx + CELLS / 2] = color;
            }
        }
    }

    public static int colorAt(int column, int row) {
        if (column < 0 || row < 0 || column >= CELLS || row >= CELLS) return 0xFF202027;
        return COLORS[row * CELLS + column];
    }
}
