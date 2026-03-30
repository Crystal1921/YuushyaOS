package com.yuushya.yuushya_os.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemButton extends Button {
    protected static final CreateNarration DEFAULT_NARRATION = Supplier::get;
    public static final int WIDTH = 50;
    public static final int NAME_HEIGHT = 10;  // 上方名字区域高度
    public static final int ITEM_SIZE = 40;    // 中间物品区域高度
    public static final int FAVORITE_HEIGHT = 10;  // 下方收藏按钮高度
    public static final int TOTAL_HEIGHT = NAME_HEIGHT + ITEM_SIZE + FAVORITE_HEIGHT;  // 总高度 60

    private final CreativeWorkshopScreen.ItemInfo itemInfo;
    private final Consumer<CreativeWorkshopScreen.ItemInfo> favoriteCallback;

    public ItemButton(CreativeWorkshopScreen.ItemInfo itemInfo, int x, int y, Component message, Consumer<CreativeWorkshopScreen.ItemInfo> favoriteCallback) {
        super(x, y, WIDTH, TOTAL_HEIGHT, message, ItemButton::onPress, DEFAULT_NARRATION);
        this.itemInfo = itemInfo;
        this.favoriteCallback = favoriteCallback;
    }

    private static void onPress(Button button) {
        System.out.println("ItemButton clicked: " + button.getMessage().getString());
        // 处理按钮点击事件
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        // 检查是否点击了收藏按钮区域（底部 10 像素）
        int favoriteAreaY = this.getY() + NAME_HEIGHT + ITEM_SIZE;
        if (mouseX >= this.getX() && mouseX < this.getX() + this.width &&
            mouseY >= favoriteAreaY && mouseY < favoriteAreaY + FAVORITE_HEIGHT) {
            if (favoriteCallback != null) {
                favoriteCallback.accept(itemInfo);
            }
        }
    }

    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        if (!itemInfo.itemStack().isEmpty()) {
            // 1. 绘制整体背景
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF000000);

            // 2. 上面 10 像素：绘制模型名字
            String name = itemInfo.name();
            int nameWidth = font.width(name);
            int nameX = this.getX() + (this.width - nameWidth) / 2;  // 居中显示
            guiGraphics.drawString(font, name, nameX, this.getY() + 1, Color.WHITE.getRGB());

            // 3. 中间 40 像素：绘制物品
            int itemY = this.getY() + NAME_HEIGHT;
            PoseStack pose = guiGraphics.pose();
            pose.pushPose();
            pose.translate(this.getX() + 25, itemY + 20, 10);
            pose.scale(16.0f, -16.0f, 16.0f);
            ClientLevel level = mc.level;
            mc.getItemRenderer().renderStatic(this.itemInfo.itemStack(), ItemDisplayContext.GUI, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, pose, guiGraphics.bufferSource(), level, 0);
            pose.popPose();

            // 4. 下面 10 像素：绘制收藏按钮
            int favoriteY = this.getY() + NAME_HEIGHT + ITEM_SIZE;
            boolean isHovered = mouseX >= this.getX() && mouseX < this.getX() + this.width &&
                               mouseY >= favoriteY && mouseY < favoriteY + FAVORITE_HEIGHT;

            // 收藏按钮背景（鼠标悬停时变色）
            int favoriteColor = isHovered ? 0xFFFF6B6B : 0xFF888888;
            guiGraphics.fill(this.getX(), favoriteY, this.getX() + this.width, favoriteY + FAVORITE_HEIGHT, favoriteColor);

            // 收藏按钮文字（♥）
            String favoriteText = "♥";
            int favoriteTextWidth = font.width(favoriteText);
            int favoriteTextX = this.getX() + (this.width - favoriteTextWidth) / 2;
            guiGraphics.drawString(font, favoriteText, favoriteTextX, favoriteY + 1, Color.WHITE.getRGB());
        }
    }
}
