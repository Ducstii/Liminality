package com.ducstii.liminality.world.chunk;

import com.ducstii.liminality.block.BackroomsBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.Random;

public class Level0ChunkGenerator extends BackroomsChunkGenerator {

    public static final Codec<Level0ChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource)
            ).apply(instance, instance.stable(Level0ChunkGenerator::new)));

    private static final int FLOOR_Y = 18;
    private static final int CEILING_Y = 23;
    private static final int CELL_SIZE = 4;
    private static final float PASSAGE_CHANCE = 0.40f;
    private static final int MIN_OPENINGS = 2;

    private static final int EAST = 0, SOUTH = 1, WEST = 2, NORTH = 3;

    public Level0ChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void generate(Chunk chunk) {
        int startX = chunk.getPos().getStartX();
        int startZ = chunk.getPos().getStartZ();
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int wx = startX + x;
                int wz = startZ + z;

                for (int y = 0; y < FLOOR_Y; y++) {
                    chunk.setBlockState(pos.set(wx, y, wz), Blocks.BEDROCK.getDefaultState(), false);
                }

                chunk.setBlockState(pos.set(wx, FLOOR_Y, wz),
                        BackroomsBlocks.BACKROOMS_CARPET.getDefaultState(), false);

                BlockState fill = isWall(wx, wz)
                        ? BackroomsBlocks.WALLPAPER.getDefaultState()
                        : Blocks.AIR.getDefaultState();
                for (int y = FLOOR_Y + 1; y < CEILING_Y; y++) {
                    chunk.setBlockState(pos.set(wx, y, wz), fill, false);
                }

                chunk.setBlockState(pos.set(wx, CEILING_Y, wz),
                        BackroomsBlocks.CEILING_TILE.getDefaultState(), false);
            }
        }
    }

    private boolean isWall(int wx, int wz) {
        int lx = Math.floorMod(wx, CELL_SIZE);
        int lz = Math.floorMod(wz, CELL_SIZE);

        if (lx == 0 && lz == 0) return true;

        if (lx == 0) {
            int cx = Math.floorDiv(wx, CELL_SIZE);
            int cz = Math.floorDiv(wz, CELL_SIZE);
            return !isWallOpen(cx - 1, cz, true);
        }

        if (lz == 0) {
            int cx = Math.floorDiv(wx, CELL_SIZE);
            int cz = Math.floorDiv(wz, CELL_SIZE);
            return !isWallOpen(cx, cz - 1, false);
        }

        return false;
    }

    private boolean isWallOpen(int cx, int cz, boolean isEast) {
        if (baseOpen(cx, cz, isEast)) return true;

        int nx = isEast ? cx + 1 : cx;
        int nz = isEast ? cz : cz + 1;
        int myDir = isEast ? EAST : SOUTH;
        int theirDir = isEast ? WEST : NORTH;

        return ((forcedMask(cx, cz) & (1 << myDir)) != 0)
            || ((forcedMask(nx, nz) & (1 << theirDir)) != 0);
    }

    private boolean baseOpen(int cx, int cz, boolean isEast) {
        long seed = isEast
                ? (long) cx * 341873128712L ^ (long) cz * 132897987541L ^ 0xDEADBEEFL
                : (long) cx * 341873128712L ^ (long) cz * 132897987541L ^ 0xCAFEBABEL;
        return new Random(seed).nextFloat() < PASSAGE_CHANCE;
    }

    // bitmask of which walls are naturally open for cell (cx, cz)
    private int naturalMask(int cx, int cz) {
        int mask = 0;
        if (baseOpen(cx, cz, true))      mask |= (1 << EAST);
        if (baseOpen(cx, cz, false))     mask |= (1 << SOUTH);
        if (baseOpen(cx - 1, cz, true))  mask |= (1 << WEST);
        if (baseOpen(cx, cz - 1, false)) mask |= (1 << NORTH);
        return mask;
    }

    // bitmask of walls this cell forces open to reach MIN_OPENINGS
    private int forcedMask(int cx, int cz) {
        int natural = naturalMask(cx, cz);
        int needed = MIN_OPENINGS - Integer.bitCount(natural);
        if (needed <= 0) return 0;

        long seed = (long) cx * 987654321L ^ (long) cz * 123456789L;
        Random rng = new Random(seed);

        // shuffle directions and pick 'needed' closed ones to force open
        int[] dirs = {EAST, SOUTH, WEST, NORTH};
        for (int i = 3; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = dirs[i]; dirs[i] = dirs[j]; dirs[j] = tmp;
        }

        int forced = 0, count = 0;
        for (int dir : dirs) {
            if (count >= needed) break;
            if ((natural & (1 << dir)) == 0) {
                forced |= (1 << dir);
                count++;
            }
        }
        return forced;
    }
}
