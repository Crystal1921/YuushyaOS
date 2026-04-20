package com.yuushya.yuushya_os.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen.HEIGHT;
import static com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen.WIDTH;

public class MainScreen extends Screen {
    ResourceLocation texture;
    String name;

    public MainScreen() {
        super(Component.literal("Main Screen"));
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            this.texture = Minecraft.getInstance().getSkinManager().getInsecureSkin(player.getGameProfile()).texture();
            this.name = player.getGameProfile().getName();
        }
    }

    @Override
    protected void init() {
        int widthCenter = this.width / 2;
        int heightCenter = this.height / 2;
        Button workshopButton = Button
                .builder(Component.translatable("yuushya_os.panel"), (button -> openWorkshopScreen()))
                .bounds(widthCenter - 20, heightCenter - 50, 30, 30)
                .build();

        Button calender = Button
                .builder(Component.translatable("yuushya_os.calender"), (button -> openCalenderScreen()))
                .bounds(widthCenter + 30, heightCenter - 50, 30, 30)
                .build();

        this.addRenderableWidget(workshopButton);
        this.addRenderableWidget(calender);
    }

    private void openWorkshopScreen() {
        Minecraft.getInstance().pushGuiLayer(new CreativeWorkshopScreen());
    }

    private void openCalenderScreen() {
        Minecraft.getInstance().pushGuiLayer(new CalenderScreen());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (minecraft == null) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player != null) {
            int widthCenter = this.width / 2;
            int heightCenter = this.height / 2;
            guiGraphics.fill(widthCenter - WIDTH / 2, heightCenter - HEIGHT / 2,
                    widthCenter + WIDTH / 2, heightCenter + HEIGHT / 2, 0xFF000000);
            if (texture != null) {
                PlayerFaceRenderer.draw(guiGraphics, texture, widthCenter - WIDTH / 2 + 60, heightCenter - 8, 24);
                guiGraphics.drawCenteredString(minecraft.font, name, widthCenter - WIDTH / 2 + 60 + 12, heightCenter + 20, 0xFFFFFFFF);
            }
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }
}
