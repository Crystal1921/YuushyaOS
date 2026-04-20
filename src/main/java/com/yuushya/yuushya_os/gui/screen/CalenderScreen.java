package com.yuushya.yuushya_os.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class CalenderScreen extends LayerScreen {
    protected CalenderScreen() {
        super(Component.literal("Calendar Screen"));
    }

    @Override
    protected void init() {
        super.init();

    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (minecraft == null) {
            return;
        }
    }
}
