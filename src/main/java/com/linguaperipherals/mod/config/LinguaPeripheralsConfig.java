package com.linguaperipherals.mod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class LinguaPeripheralsConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue GLOBAL_MAX_RANGE;
    public static final ModConfigSpec.IntValue SPEECH_MAX_FREQUENCY;

    static {
        BUILDER.push("general");

        GLOBAL_MAX_RANGE = BUILDER
                .comment("Maximum range (in blocks) for the playVoice() method's rad parameter." +
                        " Any value larger than this will be clamped. Default: 128")
                .defineInRange("globalMaxRange", 128.0, 1.0, Double.MAX_VALUE);

        SPEECH_MAX_FREQUENCY = BUILDER
                .comment("Minimum interval in milliseconds between speech plays.")
                .defineInRange("speechMaxFrequency", 0, 0, Integer.MAX_VALUE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}