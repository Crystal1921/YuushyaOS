package com.yuushya.yuushya_os.gui.screen;

import com.yuushya.modelling.gui.engrave.EngraveBlockResultLoader;
import com.yuushya.modelling.gui.engrave.EngraveItemResultLoader;
import com.yuushya.yuushya_os.YuushyaOS;
import com.yuushya.yuushya_os.gui.widget.ItemButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreativeWorkshopScreen extends Screen {
    public static final int WIDTH = 320;
    public static final int HEIGHT = 160;
    public static final int BUTTON_INTERVAL = 38;
    public static final int BUTTON_WIDTH = 28;
    public static final int BUTTON_HEIGHT = 14;
    public int page = 0;

    List<ItemInfo> favoriteItems = new ArrayList<>();

    Map<String, ItemInfo> itemInfoMap = new HashMap<>();
    List<ItemButton> itemButtons = new ArrayList<>();

    public CreativeWorkshopScreen() {
        super(Component.literal("Creative Workshop"));
    }

    @Override
    protected void init() {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int widthCenter = this.width / 2;
        int heightCenter = this.height / 2;

        int leftPos = 72;
        Button localButton = Button.builder(Component.translatable("yuushya_os.local"), (button -> onLocalButtonClick()))
                .bounds(widthCenter - leftPos, heightCenter - 75, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();

        Button favoritesButton = Button.builder(Component.translatable("yuushya_os.favorites"), (button -> onFavoritesButtonClick()))
                .bounds(widthCenter - leftPos + BUTTON_INTERVAL, heightCenter - 75, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();

        Button serverButton = Button.builder(Component.translatable("yuushya_os.server"), (button -> onServerButtonClick()))
                .bounds(widthCenter - leftPos + BUTTON_INTERVAL * 2, heightCenter - 75, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        Button uploadButton = Button.builder(Component.translatable("yuushya_os.upload"), (button -> onUploadButtonClick()))
                .bounds(widthCenter - leftPos + BUTTON_INTERVAL * 3, heightCenter - 75, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();

        Button exitButton = Button.builder(Component.literal("x"), (button -> this.onClose()))
                .bounds(widthCenter + WIDTH / 2 - BUTTON_WIDTH, heightCenter - HEIGHT / 2, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();

        Button nextPageButton = Button.builder(Component.literal(">"), (button -> {
                    page++;
                    refreshPage();
                }))
                .bounds(widthCenter + WIDTH / 2 - BUTTON_HEIGHT * 2, heightCenter + HEIGHT / 2 - BUTTON_HEIGHT, BUTTON_HEIGHT, BUTTON_HEIGHT)
                .build();

        Button prevPageButton = Button.builder(Component.literal("<"), (button -> {
                    if (page > 0) {
                        page--;
                        refreshPage();
                    }
                }))
                .bounds(widthCenter + WIDTH / 2 - BUTTON_HEIGHT * 4, heightCenter + HEIGHT / 2 - BUTTON_HEIGHT, BUTTON_HEIGHT, BUTTON_HEIGHT)
                .build();

        this.addRenderableWidget(localButton);
        this.addRenderableWidget(favoritesButton);
        this.addRenderableWidget(serverButton);
        this.addRenderableWidget(uploadButton);
        this.addRenderableWidget(exitButton);
        this.addRenderableWidget(nextPageButton);
        this.addRenderableWidget(prevPageButton);
    }

    private void onLocalButtonClick() {
        this.itemInfoMap.clear();
        this.page = 0;
        EngraveBlockResultLoader.SHOWBLOCK_ITEM_MAP.forEach((key, value) ->
                this.itemInfoMap.put(key, new ItemInfo(value.getResultItem(), value.getName(), "Local")));
        EngraveItemResultLoader.ITEMBLOCK_ITEM_MAP.forEach((key, value) ->
                this.itemInfoMap.put(key, new ItemInfo(value.getResultItem(), value.getName(), "Local")));

        refreshPage();
    }

    private void onFavoritesButtonClick() {
        this.itemInfoMap.clear();
        this.page = 0;
        this.favoriteItems.forEach(itemInfo -> {
            this.itemInfoMap.put(itemInfo.name, itemInfo);
        });
        refreshPage();
    }

    private void onServerButtonClick() {

    }

    private void onUploadButtonClick() {

    }

    private void refreshPage() {
        itemButtons.clear();
        ArrayList<ItemInfo> itemInfos = new ArrayList<>(this.itemInfoMap.values());
        for (int i = 0; i < itemInfos.size(); i++) {
            if (i >= page * 6 && i < (page + 1) * 6) {
                ItemInfo itemInfo = itemInfos.get(i);
                ItemButton itemButton = new ItemButton(itemInfo, this.width / 2 - 72 + (i % 6) * BUTTON_INTERVAL, this.height / 2 - 30, Component.literal(itemInfo.name()));
                this.itemButtons.add(itemButton);
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(YuushyaOS.MODID, "textures/gui/test.png");

        int widthCenter = this.width / 2;
        int heightCenter = this.height / 2;
        guiGraphics.blit(resourceLocation, widthCenter - WIDTH / 2, heightCenter - HEIGHT / 2, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);

        itemButtons.forEach(button -> button.render(guiGraphics, mouseX, mouseY, partialTick));

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    public record ItemInfo(ItemStack itemStack, String name, String author) {

    }
}
