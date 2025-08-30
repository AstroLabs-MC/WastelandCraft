package com.example.examplemod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class WastelandConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    public static final class Common {
        public final ForgeConfigSpec.BooleanValue allowExternalStructures;
        public final ForgeConfigSpec.BooleanValue allowExternalMobsInWasteland;
        public final ForgeConfigSpec.IntValue waterPoisonDurationTicks;
        public final ForgeConfigSpec.IntValue waterPoisonAmplifier;
        public final ForgeConfigSpec.BooleanValue waterAffectsPlayersOnly;

        private Common(ForgeConfigSpec.Builder b) {
            b.push("worldgen");
            allowExternalStructures = b
                .comment("Allow other mods' structures to generate in the Wasteland biome (requires data reload/world restart)")
                .define("allowExternalStructures", false);
            b.pop();

            b.push("spawns");
            allowExternalMobsInWasteland = b
                .comment("Opt-in: allow mobs from other mods to spawn naturally in the Wasteland biome. Spawners and spawn eggs are not affected.")
                .define("allowExternalMobsInWasteland", false);
            b.pop();

            b.push("water_radiation");
            waterPoisonDurationTicks = b
                .comment("Duration in ticks of the Poison effect applied while in water in the Wasteland biome. 20 ticks = 1 second.")
                .defineInRange("poisonDurationTicks", 60, 10, 12000);
            waterPoisonAmplifier = b
                .comment("Amplifier of the Poison effect (0 = Poison I, 1 = Poison II, etc.)")
                .defineInRange("poisonAmplifier", 0, 0, 4);
            waterAffectsPlayersOnly = b
                .comment("If true, only players are affected by water radiation. If false, all living entities are affected.")
                .define("affectPlayersOnly", true);
            b.pop();
        }
    }

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        COMMON_SPEC = builder.build();
    }

    private WastelandConfig() {}
}
