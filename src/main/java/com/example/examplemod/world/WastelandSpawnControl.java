package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.config.WastelandConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WastelandSpawnControl {

    private static final ResourceLocation WASTELAND_BIOME_ID = new ResourceLocation(ExampleMod.MODID, "wasteland");

    @SubscribeEvent
    public static void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        // Only server-side relevant
        LevelAccessor level = event.getLevel();
        if (level.isClientSide()) return;

        Mob entity = event.getEntity();
        BlockPos pos = BlockPos.containing(entity.position());

        // Check biome at spawn position
        ResourceKey<Biome> biomeKey = level.getBiome(pos).unwrapKey().orElse(null);
        if (biomeKey == null || !biomeKey.location().equals(WASTELAND_BIOME_ID)) return;

        // If config allows external mobs, do nothing
        if (WastelandConfig.COMMON.allowExternalMobsInWasteland.get()) return;

        // Only restrict natural-like spawns; allow spawners, eggs, commands, etc.
        MobSpawnType spawnType = event.getSpawnType();
        switch (spawnType) {
            case NATURAL, CHUNK_GENERATION, PATROL, STRUCTURE -> {
                // fall-through to check namespace
            }
            default -> {
                return; // allow other spawn reasons
            }
        }

        // Whitelist vanilla + this mod's own entities
        ResourceLocation typeId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (typeId == null) return; // unknown? allow to avoid false positives
        String ns = typeId.getNamespace();
        if ("minecraft".equals(ns) || ExampleMod.MODID.equals(ns)) return;

        // Block external mod mobs in Wasteland unless opted-in
        event.setCanceled(true);
    }
}
