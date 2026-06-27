package com.linguaperipherals.mod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class LinguaPeripheralsConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue GLOBAL_MAX_RANGE;
    public static final ModConfigSpec.IntValue SPEECH_MAX_FREQUENCY;
    public static final ModConfigSpec.IntValue CASSETTE_TAPE_SIZE_LIMIT;
    public static final ModConfigSpec.DoubleValue MAX_VOLUME;

    static {
        BUILDER.push("general");

        GLOBAL_MAX_RANGE = BUILDER
                .comment("Maximum range (in blocks) for the playVoice() method's rad parameter. Any value larger than this will be clamped. Default: 128")
                .defineInRange("globalMaxRange", 128.0, 1.0, Double.MAX_VALUE);

        SPEECH_MAX_FREQUENCY = BUILDER
                .comment("Minimum interval in milliseconds between speech plays.")
                .defineInRange("speechMaxFrequency", 0, 0, Integer.MAX_VALUE);

        CASSETTE_TAPE_SIZE_LIMIT = BUILDER
                .comment("Maximum data size for a cassette tape, in bytes. Default: 256KB (262144)")
                .defineInRange("cassetteTapeSizeLimit", 256 * 1024, 1024, Integer.MAX_VALUE);

        MAX_VOLUME = BUILDER
                .comment("Maximum volume for cassette tape playback. Volume affects audible range (1.0 = 16m radius). Default: 3.0")
                .defineInRange("maxVolume", 3.0, 0.1, 10.0);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
