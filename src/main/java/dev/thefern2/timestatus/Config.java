package dev.thefern2.timestatus;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SHOW_TIME_STATUS;
    public static final ModConfigSpec.BooleanValue HIDE_DAY_COUNT;
    public static final ModConfigSpec.BooleanValue HIDE_TIME_COUNTER;
    public static final ModConfigSpec.BooleanValue HIDE_DAY_PERIOD;

    static {
        SHOW_TIME_STATUS = BUILDER
            .comment("Show the time status HUD")
            .define("showTimeStatus", true);
        
        HIDE_DAY_COUNT = BUILDER
            .comment("Hide the day count display")
            .define("hideDayCount", false);
        
        HIDE_TIME_COUNTER = BUILDER
            .comment("Hide the time counter display")
            .define("hideTimeCounter", false);
        
        HIDE_DAY_PERIOD = BUILDER
            .comment("Hide the day period label (Day/Night/Sunset/Sunrise)")
            .define("hideDayPeriod", false);
    }

    static final ModConfigSpec SPEC = BUILDER.build();
}
