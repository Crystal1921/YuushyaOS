package com.yuushya.yuushya_os.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

import static com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen.HEIGHT;
import static com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen.WIDTH;

public class ItemShowScreen extends LayerScreen {
    private final CreativeWorkshopScreen.ItemInfo itemInfo;

    public ItemShowScreen(CreativeWorkshopScreen.ItemInfo itemInfo, Screen parent) {
        super(Component.literal("Item Show"), parent);
        this.itemInfo = itemInfo;
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
        pose.popPose();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
