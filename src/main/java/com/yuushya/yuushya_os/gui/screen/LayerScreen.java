package com.yuushya.yuushya_os.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen.*;

/**
 * 抽象基类，封装 YuushyaOS GUI 界面的共同行为
 * 使用 NeoForge 的 GUI 层栈管理屏幕切换
 */
public abstract class LayerScreen extends Screen {
    protected Minecraft minecraft;
    protected Font font;
    protected int widthCenter;
    protected int heightCenter;

    /**
     * 构造函数
     *
     * @param title 界面标题
     */
    protected LayerScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        this.minecraft = Minecraft.getInstance();
        this.font = minecraft.font;
        this.widthCenter = this.width / 2;
        this.heightCenter = this.height / 2;

        // 添加退出按钮
        Button exitButton = Button.builder(Component.literal("x"), (button -> this.onClose()))
                .bounds(widthCenter + WIDTH / 2 - BUTTON_WIDTH, heightCenter - HEIGHT / 2, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        this.addRenderableWidget(exitButton);
    }

    @Override
    public void onClose() {
        // 使用 NeoForge 的 GUI 层栈返回上一个屏幕
        minecraft.popGuiLayer();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 默认不渲染背景，子类可以重写
    }
}
