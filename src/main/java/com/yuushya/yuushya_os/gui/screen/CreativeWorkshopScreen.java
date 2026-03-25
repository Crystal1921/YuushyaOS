package com.yuushya.yuushya_os.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CreativeWorkshopScreen extends Screen {
    public CreativeWorkshopScreen() {
        super(Component.literal("Creative Workshop"));
    }

    @Override
    protected void init() {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int widthCenter = this.width / 2;
        int heightCenter = this.height / 2;



    }
}
