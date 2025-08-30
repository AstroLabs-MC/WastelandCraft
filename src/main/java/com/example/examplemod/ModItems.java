package com.example.examplemod;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExampleMod.MODID);

    public static final RegistryObject<Item> WASTELAND_BLOCK = ITEMS.register("wasteland_block",
            () -> new BlockItem(ModBlocks.WASTELAND_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> WASTELAND_DIRT = ITEMS.register("wasteland_dirt",
            () -> new BlockItem(ModBlocks.WASTELAND_DIRT.get(), new Item.Properties()));
}
