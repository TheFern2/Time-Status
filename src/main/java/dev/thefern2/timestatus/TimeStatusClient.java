package dev.thefern2.timestatus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = TimeStatus.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = TimeStatus.MODID, value = Dist.CLIENT)
public class TimeStatusClient {
    public TimeStatusClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        TimeStatus.LOGGER.info("Time Status client initialized");
    }

    @SubscribeEvent
    static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        
        long worldTime = minecraft.level.getDayTime();
        long ticksInDay = 24000;
        long timeOfDay = worldTime % ticksInDay;
        
        int barWidth = 50;
        int barHeight = 3;
        int x = 10;
        int y = 10;
        
        float progress = 0;
        int barColor = 0;
        String timeLabel = "";
        long periodStartTick = 0;
        long periodDurationTicks = 0;
        
        if (timeOfDay >= 0 && timeOfDay < 11500) {
            progress = timeOfDay / 11500f;
            barColor = 0xFF4A90E2;
            timeLabel = "Day";
            periodStartTick = 0;
            periodDurationTicks = 11500;
        } else if (timeOfDay >= 11500 && timeOfDay < 13000) {
            progress = (timeOfDay - 11500) / 1500f;
            barColor = 0xFFFFA500;
            timeLabel = "Sunset";
            periodStartTick = 11500;
            periodDurationTicks = 1500;
        } else if (timeOfDay >= 13000 && timeOfDay < 22500) {
            progress = (timeOfDay - 13000) / 9500f;
            barColor = 0xFFDC143C;
            timeLabel = "Night";
            periodStartTick = 13000;
            periodDurationTicks = 9500;
        } else {
            progress = (timeOfDay - 22500) / 1500f;
            barColor = 0xFFFFA500;
            timeLabel = "Sunrise";
            periodStartTick = 22500;
            periodDurationTicks = 1500;
        }
        
        guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF000000);
        
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0xFF333333);
        
        int filledWidth = (int)(barWidth * progress);
        guiGraphics.fill(x, y, x + filledWidth, y + barHeight, barColor);
        
        long elapsedTicks = timeOfDay - periodStartTick;
        int elapsedSeconds = (int)((elapsedTicks / 20.0) % 60);
        int elapsedMinutes = (int)((elapsedTicks / 20.0) / 60);
        String timeString = String.format("%d:%02d", elapsedMinutes, elapsedSeconds);
        
        int labelY = y + barHeight + 2;
        guiGraphics.drawString(minecraft.font, timeLabel, x, labelY, 0xFFFFFFFF, true);
        
        int timerX = x + barWidth + 5;
        guiGraphics.drawString(minecraft.font, timeString, timerX, y, 0xFFAAAAAA, true);
    }
}
