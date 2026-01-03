package dev.thefern2.timestatus;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.lwjgl.glfw.GLFW;

@Mod(value = TimeStatus.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = TimeStatus.MODID, value = Dist.CLIENT)
public class TimeStatusClient {
    public static final KeyMapping TOGGLE_KEY = new KeyMapping(
        "key.timestatus.toggle",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_H,
        "key.categories.timestatus"
    );

    public TimeStatusClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.addListener(TimeStatusClient::onPlayerTick);
    }

    @SubscribeEvent
    static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_KEY);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        TimeStatus.LOGGER.info("Time Status client initialized");
    }

    static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            while (TOGGLE_KEY.consumeClick()) {
                boolean currentValue = Config.SHOW_TIME_STATUS.get();
                Config.SHOW_TIME_STATUS.set(!currentValue);
                Config.SPEC.save();
            }
        }
    }

    @SubscribeEvent
    static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        if (!Config.SHOW_TIME_STATUS.get()) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        
        long worldTime = minecraft.level.getDayTime();
        long ticksInDay = 24000;
        long timeOfDay = worldTime % ticksInDay;
        long dayNumber = worldTime / ticksInDay;
        
        int barWidth = 50;
        int barHeight = 3;
        int x = 10;
        int y = Config.HIDE_DAY_COUNT.get() ? 10 : 20;
        
        float progress = 0;
        int barColor = 0;
        String timeLabel = "";
        long periodStartTick = 0;
        long periodDurationTicks = 0;
        
        if (timeOfDay >= 0 && timeOfDay < 12000) {
            progress = timeOfDay / 12000f;
            barColor = 0xFF4A90E2;
            timeLabel = "Day";
            periodStartTick = 0;
            periodDurationTicks = 12000;
        } else if (timeOfDay >= 12000 && timeOfDay < 13000) {
            progress = (timeOfDay - 12000) / 1000f;
            barColor = 0xFFFFA500;
            timeLabel = "Sunset";
            periodStartTick = 12000;
            periodDurationTicks = 1000;
        } else if (timeOfDay >= 13000 && timeOfDay < 23000) {
            progress = (timeOfDay - 13000) / 10000f;
            barColor = 0xFFDC143C;
            timeLabel = "Night";
            periodStartTick = 13000;
            periodDurationTicks = 10000;
        } else {
            progress = (timeOfDay - 23000) / 1000f;
            barColor = 0xFFFFA500;
            timeLabel = "Sunrise";
            periodStartTick = 23000;
            periodDurationTicks = 1000;
        }
        
        guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF8B8B8B);
        
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0xFF333333);
        
        int filledWidth = (int)(barWidth * progress);
        guiGraphics.fill(x, y, x + filledWidth, y + barHeight, barColor);
        
        long elapsedTicks = timeOfDay - periodStartTick;
        int elapsedSeconds = (int)((elapsedTicks / 20.0) % 60);
        int elapsedMinutes = (int)((elapsedTicks / 20.0) / 60);
        String timeString = String.format("%d:%02d", elapsedMinutes, elapsedSeconds);
        
        if (!Config.HIDE_DAY_COUNT.get()) {
            String dayString = "Day " + dayNumber;
            int dayLabelY = y - minecraft.font.lineHeight - 2;
            guiGraphics.drawString(minecraft.font, dayString, x, dayLabelY, 0xFFFFFFFF, true);
        }
        
        if (!Config.HIDE_DAY_PERIOD.get()) {
            int labelY = y + barHeight + 2;
            guiGraphics.drawString(minecraft.font, timeLabel, x, labelY, 0xFFFFFFFF, true);
        }
        
        if (!Config.HIDE_TIME_COUNTER.get()) {
            int timeStringWidth = minecraft.font.width(timeString);
            int timerX = x + barWidth + 5;
            int timerY = y - (minecraft.font.lineHeight / 2) + (barHeight / 2);
            guiGraphics.drawString(minecraft.font, timeString, timerX, timerY, 0xFFAAAAAA, true);
        }
    }
}
