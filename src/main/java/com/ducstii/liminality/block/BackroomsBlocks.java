package com.ducstii.liminality.block;

import com.ducstii.liminality.Liminality;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class BackroomsBlocks {

    public static Block CEILING_TILE;
    public static Block WALLPAPER;
    public static Block BACKROOMS_CARPET;

    public static void Init() {
        CEILING_TILE = register("ceiling_tile",
                new Block(AbstractBlock.Settings.create().strength(1.0f, 1.0f).sounds(BlockSoundGroup.WOOD).luminance(state -> 15)));
        WALLPAPER = register("wallpaper",
                new Block(AbstractBlock.Settings.create().strength(0.5f).sounds(BlockSoundGroup.WOOL)));
        BACKROOMS_CARPET = register("backrooms_carpet",
                new Block(AbstractBlock.Settings.create().strength(0.5f).sounds(BlockSoundGroup.WOOL)));
    }

    private static Block register(String name, Block block) {
        Registry.register(Registries.ITEM, new Identifier(Liminality.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
        Block registered = Registry.register(Registries.BLOCK, new Identifier(Liminality.MOD_ID, name), block);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS)
                .register(entries -> entries.add(registered));
        return registered;
    }
}