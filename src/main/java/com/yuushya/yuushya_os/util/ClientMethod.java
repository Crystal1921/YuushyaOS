package com.yuushya.yuushya_os.util;

import com.yuushya.yuushya_os.gui.screen.MainScreen;
import net.minecraft.client.Minecraft;

public class ClientMethod {
    public static void openCreativeWorkshopScreen() {
        Minecraft mc = Minecraft.getInstance();
        MainScreen mainScreen = new MainScreen();
        mc.setScreen(mainScreen);
    }
}
