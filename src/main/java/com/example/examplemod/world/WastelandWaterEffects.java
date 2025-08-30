package com.example.examplemod.world;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.config.WastelandConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WastelandWaterEffects {
    private static final ResourceLocation WASTELAND_BIOME_ID = new ResourceLocation(ExampleMod.MODID, "wasteland");

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        var living = event.getEntity();
        Level level = living.level();
        if (level.isClientSide) return;

        // Respect config for targets
        boolean playersOnly = WastelandConfig.COMMON.waterAffectsPlayersOnly.get();
        if (playersOnly && !(living instanceof Player)) return;
        if (living instanceof Player p) {
            if (p.isCreative() || p.isSpectator()) return;
        }

        // Only in Wasteland biome
        BlockPos pos = living.blockPosition();
        var biomeKeyOpt = level.getBiome(pos).unwrapKey();
        if (biomeKeyOpt.isEmpty() || !biomeKeyOpt.get().location().equals(WASTELAND_BIOME_ID)) return;

        // If touching water (feet or submerged), apply Poison
        boolean inWater = living.isInWaterOrBubble() || level.getFluidState(pos).is(FluidTags.WATER);
        if (!inWater) return;

        int durationTicks = WastelandConfig.COMMON.waterPoisonDurationTicks.get();
        int amplifier = WastelandConfig.COMMON.waterPoisonAmplifier.get();
        living.addEffect(new MobEffectInstance(MobEffects.POISON, durationTicks, amplifier, true, true));
    }
}
