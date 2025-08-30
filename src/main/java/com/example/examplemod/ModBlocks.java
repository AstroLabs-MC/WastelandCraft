package com.example.examplemod;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExampleMod.MODID);

    public static final RegistryObject<Block> WASTELAND_BLOCK = BLOCKS.register("wasteland_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(1.5F, 6.0F)
                    .sound(SoundType.STONE)));

    public static final RegistryObject<Block> WASTELAND_DIRT = BLOCKS.register("wasteland_dirt",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT)
                    .strength(0.6F)
                    .sound(SoundType.GRAVEL)));
}
