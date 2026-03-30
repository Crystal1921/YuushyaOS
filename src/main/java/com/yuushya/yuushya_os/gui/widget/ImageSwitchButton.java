package com.yuushya.yuushya_os.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ImageSwitchButton extends Button {
    protected final ResourceLocation resourceLocation;
    protected final int xTexStart;
    protected final int yTexStart;
    protected final int xDiffTex;
    protected final int textureWidth;
    protected final int textureHeight;
    protected final Font font;

    public ImageSwitchButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, ResourceLocation pResourceLocation, OnPress pOnPress, Component text) {
        this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pWidth, pResourceLocation, 256, 256, pOnPress, text);
    }

    public ImageSwitchButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pXDiffTex, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, OnPress pOnPress, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, DEFAULT_NARRATION);
        this.textureWidth = pTextureWidth;
        this.textureHeight = pTextureHeight;
        this.xTexStart = pXTexStart;
        this.yTexStart = pYTexStart;
        this.xDiffTex = pXDiffTex;
        this.resourceLocation = pResourceLocation;
        this.font = Minecraft.getInstance().font;
    }

    public void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderTexture(pGuiGraphics, this.resourceLocation, this.getX(), this.getY(), this.xTexStart, this.yTexStart, this.xDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
        pGuiGraphics.drawString(this.font, this.getMessage(), this.getX() + 10, this.getY() + (this.height - this.font.lineHeight) / 2, Color.BLACK.getRGB(), false);
    }

    public void renderTexture(GuiGraphics pGuiGraphics, ResourceLocation pTexture, int pX, int pY, int pUOffset, int pVOffset, int xDiffTex, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        int i = pUOffset + xDiffTex * 2;
        if (this.isHovered()) {
            i = pUOffset + xDiffTex;
        }

        RenderSystem.enableDepthTest();
        pGuiGraphics.blit(pTexture, pX, pY, (float) i, (float) yTexStart, pWidth, pHeight, pTextureWidth, pTextureHeight);
    }
}
