package com.yuushya.yuushya_os.util;

import com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen;
import net.minecraft.client.Minecraft;

public class ClientMethod {
    public static void openCreativeWorkshopScreen() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new CreativeWorkshopScreen());
    }
}
