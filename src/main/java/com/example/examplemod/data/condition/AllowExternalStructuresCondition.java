package com.example.examplemod.data.condition;

import com.example.examplemod.config.WastelandConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class AllowExternalStructuresCondition implements ICondition {
    public static final ResourceLocation NAME = new ResourceLocation("wasteland", "allow_external_structures");
    public static final AllowExternalStructuresCondition INSTANCE = new AllowExternalStructuresCondition();

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return WastelandConfig.COMMON.allowExternalStructures.get();
    }

    public static class Serializer implements IConditionSerializer<AllowExternalStructuresCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(com.google.gson.JsonObject json, AllowExternalStructuresCondition value) {
            // no data
        }

        @Override
        public AllowExternalStructuresCondition read(com.google.gson.JsonObject json) {
            return AllowExternalStructuresCondition.INSTANCE;
        }

        @Override
        public ResourceLocation getID() {
            return AllowExternalStructuresCondition.NAME;
        }
    }
}
