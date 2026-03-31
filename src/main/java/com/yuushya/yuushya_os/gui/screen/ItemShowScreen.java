package com.yuushya.yuushya_os.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

import static com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen.*;

public class ItemShowScreen extends Screen {
    private final CreativeWorkshopScreen.ItemInfo itemInfo;
    private final CreativeWorkshopScreen parent;

    public ItemShowScreen(CreativeWorkshopScreen parent, CreativeWorkshopScreen.ItemInfo itemInfo) {
        super(Component.literal("Item Show"));
        this.parent = parent;
        this.itemInfo = itemInfo;
    }

    @Override
    protected void init() {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int widthCenter = this.width / 2;
        int heightCenter = this.height / 2;
        Button exitButton = Button.builder(Component.literal("x"), (button -> this.onClose()))
                .bounds(widthCenter + WIDTH / 2 - BUTTON_WIDTH, heightCenter - HEIGHT / 2, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();

        this.addRenderableWidget(exitButton);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        int widthCenter = this.width / 2;
        int heightCenter = this.height / 2;
        guiGraphics.fill(widthCenter - WIDTH / 2, heightCenter - HEIGHT / 2,
                widthCenter + WIDTH / 2, heightCenter + HEIGHT / 2, 0xFF000000);
        guiGraphics.drawString(this.font, this.itemInfo.author(), widthCenter - WIDTH / 2 + 5, heightCenter - CreativeWorkshopScreen.HEIGHT / 2 + 5, 0xFFFFFFFF);
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(widthCenter, heightCenter, 10);
        pose.scale(16.0f, -16.0f, 16.0f);
        Minecraft.getInstance().getItemRenderer().renderStatic(this.itemInfo.itemStack(), ItemDisplayContext.GUI, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, pose, guiGraphics.bufferSource(), level, 0);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void onClose() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(parent);
    }
}
