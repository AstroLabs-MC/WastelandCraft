package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "wasteland";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ExampleMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);

        // Register content
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

        // Register for forge events if needed in the future
        MinecraftForge.EVENT_BUS.register(this);

        // Register common config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, com.example.examplemod.config.WastelandConfig.COMMON_SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Register custom data condition used in tags
        event.enqueueWork(() -> {
            CraftingHelper.register(com.example.examplemod.data.condition.AllowExternalStructuresCondition.Serializer.INSTANCE);
        });

        LOGGER.info("Wasteland mod initialized (world preset + config + conditions).");
    }
}
