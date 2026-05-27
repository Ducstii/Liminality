package com.ducstii.liminality.world.dimension;

import com.ducstii.liminality.Liminality;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class LiminalityDimensions {

    public static final RegistryKey<DimensionType> LEVEL0_DIM_TYPE =
            RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier(Liminality.MOD_ID, "level0_type"));

    public static final RegistryKey<World> LEVEL0_WORLD_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(Liminality.MOD_ID, "level0"));
}