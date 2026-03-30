package com.yuushya.yuushya_os.gui.screen;

import com.yuushya.modelling.gui.engrave.EngraveBlockResultLoader;
import com.yuushya.modelling.gui.engrave.EngraveItemResultLoader;
import com.yuushya.yuushya_os.YuushyaOS;
import com.yuushya.yuushya_os.gui.widget.ItemButton;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
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
    public static final int ITEM_INTERVAL = 60;
    public static final int BUTTON_WIDTH = 28;
    public static final int BUTTON_HEIGHT = 14;
    public int page = 0;
    List<ItemInfo> favoriteItems = new ArrayList<>();
    Map<String, ItemInfo> itemInfoMap = new HashMap<>();
    List<ItemButton> itemButtons = new ArrayList<>();
    EditBox searchBox;
    private TabButton currentTab = TabButton.LOCAL;

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

        // Create tab buttons using enum
        for (TabButton tabButton : TabButton.values()) {
            this.addRenderableWidget(tabButton.createButton(widthCenter, heightCenter, leftPos, this));
        }

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

        searchBox = new EditBox(font, widthCenter + WIDTH / 2 - 80, heightCenter - HEIGHT / 2 + 20, 80, BUTTON_HEIGHT, Component.literal("Search..."));
        searchBox.setResponder(text -> {
            // Implement search functionality here
        });

        onTabButtonClick(this.currentTab);

        this.addRenderableWidget(exitButton);
        this.addRenderableWidget(nextPageButton);
        this.addRenderableWidget(prevPageButton);
        this.addRenderableWidget(searchBox);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ItemButton itemButton : itemButtons) {
            itemButton.onClick(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void onTabButtonClick(TabButton tabButton) {
        if (tabButton == TabButton.UPLOAD) {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.setScreen(new UploadScreen(this));
            return;
        }
        this.itemInfoMap.clear();
        this.page = 0;
        this.currentTab = tabButton;
        switch (tabButton) {
            case LOCAL -> onLocalButtonClick();
            case FAVORITES -> onFavoritesButtonClick();
            case SERVER -> onServerButtonClick();
        }

        refreshPage();
    }

    private void onLocalButtonClick() {
        EngraveBlockResultLoader.SHOWBLOCK_ITEM_MAP.forEach((key, value) ->
                this.itemInfoMap.put(key, new ItemInfo(value.getResultItem(), value.getName(), "Local")));
        EngraveItemResultLoader.ITEMBLOCK_ITEM_MAP.forEach((key, value) ->
                this.itemInfoMap.put(key, new ItemInfo(value.getResultItem(), value.getName(), "Local")));
    }

    private void onFavoritesButtonClick() {
        this.favoriteItems.forEach(itemInfo -> this.itemInfoMap.put(itemInfo.name, itemInfo));
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
                ItemButton itemButton = new ItemButton(
                        itemInfo,
                        this.width / 2 - 72 + (i % 6) * ITEM_INTERVAL,
                        this.height / 2 - 35,  // 调整 y 坐标以适应新的高度
                        Component.literal(itemInfo.name()),
                        this::toggleFavorite,  // 收藏回调
                        () -> favoriteItems.contains(itemInfo)  // 收藏状态检查
                );
                this.itemButtons.add(itemButton);
            }
        }
    }

    private void toggleFavorite(ItemInfo itemInfo) {
        if (favoriteItems.contains(itemInfo)) {
            favoriteItems.remove(itemInfo);
        } else {
            favoriteItems.add(itemInfo);
        }
        // 如果当前在收藏夹标签页，刷新显示
        if (this.currentTab == TabButton.FAVORITES) {
            onTabButtonClick(TabButton.FAVORITES);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
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

    @Getter
    private enum TabButton {
        LOCAL("yuushya_os.local", 0),
        FAVORITES("yuushya_os.favorites", 1),
        SERVER("yuushya_os.server", 2),
        UPLOAD("yuushya_os.upload", 3);

        private final String translationKey;
        private final int index;

        TabButton(String translationKey, int index) {
            this.translationKey = translationKey;
            this.index = index;
        }

        public Button createButton(int widthCenter, int heightCenter, int leftPos, CreativeWorkshopScreen screen) {
            return Button.builder(
                            Component.translatable(translationKey),
                            button -> screen.onTabButtonClick(this)
                    )
                    .bounds(widthCenter - leftPos + BUTTON_INTERVAL * index, heightCenter - 75, BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build();
        }
    }

    public record ItemInfo(ItemStack itemStack, String name, String author) {

    }
}
