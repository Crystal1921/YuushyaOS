package com.yuushya.yuushya_os.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class UploadScreen extends AbstractYuushyaScreen {
    public UploadScreen(CreativeWorkshopScreen parent) {
        super(Component.literal("Upload"), parent);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
