package com.linguaperipherals.mod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class LinguaPeripheralsConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue GLOBAL_MAX_RANGE;
    public static final ForgeConfigSpec.IntValue SPEECH_MAX_FREQUENCY;
    public static final ForgeConfigSpec.IntValue CASSETTE_TAPE_SIZE_LIMIT;
    public static final ForgeConfigSpec.DoubleValue MAX_VOLUME;
    public static final ForgeConfigSpec.BooleanValue CASSETTE_BROADCAST_AUDIO;

    static {
        BUILDER.push("general");

        GLOBAL_MAX_RANGE = BUILDER
                .comment("Maximum range (in blocks) for the playVoice() method's rad parameter. Any value larger than this will be clamped. Default: 128")
                .defineInRange("globalMaxRange", 128.0, 1.0, Double.MAX_VALUE);

        SPEECH_MAX_FREQUENCY = BUILDER
                .comment("Minimum interval in milliseconds between speech plays.")
                .defineInRange("speechMaxFrequency", 0, 0, Integer.MAX_VALUE);

        CASSETTE_TAPE_SIZE_LIMIT = BUILDER
                .comment("Maximum data size for a cassette tape, in bytes. Default: 1MB (1048576)")
                .defineInRange("cassetteTapeSizeLimit", 1024 * 1024, 1024, Integer.MAX_VALUE);

        MAX_VOLUME = BUILDER
                .comment("Maximum volume for cassette tape playback. Volume affects audible range (1.0 = 16m radius). Default: 3.0")
                .defineInRange("maxVolume", 3.0, 0.1, 10.0);

        CASSETTE_BROADCAST_AUDIO = BUILDER
                .comment("If true, cassette audio data is sent to all online players regardless of distance. If false (default), only sent to players tracking the chunk (within view distance).")
                .define("cassetteBroadcastAudio", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
