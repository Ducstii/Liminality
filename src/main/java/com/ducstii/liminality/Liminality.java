package com.ducstii.liminality;

import com.ducstii.liminality.block.BackroomsBlocks;
import com.ducstii.liminality.world.chunk.Level0ChunkGenerator;
import com.ducstii.liminality.world.dimension.LiminalityDimensions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Liminality implements ModInitializer {

    public static final String MOD_ID = "liminality";

    private static final Map<UUID, Integer> wallTimer = new HashMap<>();

    @Override
    public void onInitialize() {
        BackroomsBlocks.Init();

        Registry.register(Registries.CHUNK_GENERATOR,
                new Identifier(MOD_ID, "level0_chunk_generator"),
                Level0ChunkGenerator.CODEC);

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.getWorld().getRegistryKey() == World.OVERWORLD && player.isInsideWall()) {
                    int ticks = wallTimer.getOrDefault(player.getUuid(), 0) + 1;
                    wallTimer.put(player.getUuid(), ticks);
                    if (ticks >= 40) {
                        wallTimer.remove(player.getUuid());
                        ServerWorld level0 = server.getWorld(LiminalityDimensions.LEVEL0_WORLD_KEY);
                        if (level0 != null) {
                            level0.getChunk(0, 0);
                            FabricDimensions.teleport(player, level0,
                                    new TeleportTarget(new Vec3d(2.5, 19, 2.5), Vec3d.ZERO, 0, 0));
                        }
                    }
                } else {
                    wallTimer.remove(player.getUuid());
                }
            }
        });
    }
}