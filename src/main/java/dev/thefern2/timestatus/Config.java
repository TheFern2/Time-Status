package dev.thefern2.timestatus;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SHOW_TIME_STATUS;

    static {
        SHOW_TIME_STATUS = BUILDER
            .comment("Show the time status HUD")
            .define("showTimeStatus", true);
    }

    static final ModConfigSpec SPEC = BUILDER.build();
}
