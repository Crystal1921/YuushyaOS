package com.yuushya.yuushya_os.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class UploadScreen extends Screen {
    private final CreativeWorkshopScreen parent;
    public UploadScreen(CreativeWorkshopScreen parent) {
        super(Component.literal("Upload"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(parent);
    }
}
