package com.yuushya.yuushya_os.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ItemButton extends Button {
    protected static final CreateNarration DEFAULT_NARRATION = Supplier::get;
    private final CreativeWorkshopScreen.ItemInfo itemInfo;

    public ItemButton(CreativeWorkshopScreen.ItemInfo itemInfo, int x, int y, Component message) {
        super(x, y, 32, 32, message, ItemButton::onPress, DEFAULT_NARRATION);
        this.itemInfo = itemInfo;
    }

    private static void onPress(Button button) {
        System.out.println("ItemButton clicked: " + button.getMessage().getString());
        // 处理按钮点击事件
    }

    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!itemInfo.itemStack().isEmpty()) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF000000); // 绘制背景
            PoseStack pose = guiGraphics.pose();
            pose.pushPose();
            pose.translate(this.getX() + 16,this.getY() + 16,10);
            pose.scale(16.0f, -16.0f, 16.0f);
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;
            Minecraft.getInstance().getItemRenderer().renderStatic(this.itemInfo.itemStack(), ItemDisplayContext.GUI, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, pose, guiGraphics.bufferSource(), level, 0);
            pose.popPose();
        }
    }
}
