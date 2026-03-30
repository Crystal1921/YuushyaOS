package com.yuushya.yuushya_os.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ImageButton extends Button {
    protected final int xTexStart;
    protected final int yTexStart;
    protected final int textureWidth;
    protected final int textureHeight;
    protected final ResourceLocation resourceLocation;

    public ImageButton(int x, int y, int xTexStart, int yTexStart, int textureWidth, int textureHeight, ResourceLocation resourceLocation, OnPress onPress) {
        super(x, y, textureWidth, textureHeight, Component.empty(), onPress, DEFAULT_NARRATION);
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.resourceLocation = resourceLocation;
    }

    public void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.blit(this.resourceLocation, this.getX(), this.getY(), this.xTexStart, this.yTexStart, this.width, this.height, 256, 256);
    }
}
