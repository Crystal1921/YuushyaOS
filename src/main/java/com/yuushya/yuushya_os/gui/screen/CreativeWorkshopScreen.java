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
    public static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(YuushyaOS.MODID, "textures/gui/test.png");
    public int page = 0;
    List<ItemInfo> favoriteItems = new ArrayList<>();
    Map<String, ItemInfo> itemInfoMap = new HashMap<>();
    Map<String, ItemInfo> allItemsMap = new HashMap<>();  // 保存当前标签页的完整物品列表
    List<ItemButton> itemButtons = new ArrayList<>();
    EditBox searchBox;
    private TabButton currentTab = TabButton.LOCAL;
    private boolean needsRefresh = false;  // 延迟刷新标志

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
            filterItems(text);
        });

        onTabButtonClick(this.currentTab);

        this.addRenderableWidget(exitButton);
        this.addRenderableWidget(nextPageButton);
        this.addRenderableWidget(prevPageButton);
        this.addRenderableWidget(searchBox);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 使用副本遍历，避免在遍历过程中列表被修改
        for (ItemButton itemButton : new ArrayList<>(itemButtons)) {
            itemButton.onClick(mouseX, mouseY, button);
        }
        // 在所有点击事件处理完成后，检查是否需要刷新
        if (needsRefresh) {
            needsRefresh = false;
            refreshPage();
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
        this.allItemsMap.clear();
        this.page = 0;
        this.currentTab = tabButton;

        // 清空搜索框
        searchBox.setValue("");

        switch (tabButton) {
            case LOCAL -> onLocalButtonClick();
            case FAVORITES -> onFavoritesButtonClick();
            case SERVER -> onServerButtonClick();
        }

        // 保存完整物品列表
        this.allItemsMap.putAll(this.itemInfoMap);

        refreshPage();
    }

    private void onLocalButtonClick() {
        EngraveBlockResultLoader.SHOWBLOCK_ITEM_MAP.forEach((key, value) ->
                this.itemInfoMap.put(key, new ItemInfo(value.getResultItem(), value.getName(), "Local", ItemType.SHOWBLOCK)));
        EngraveItemResultLoader.ITEMBLOCK_ITEM_MAP.forEach((key, value) ->
                this.itemInfoMap.put(key, new ItemInfo(value.getResultItem(), value.getName(), "Local", ItemType.ITEMBLOCK)));
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
                        this,
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
        // 如果当前在收藏夹标签页，标记需要刷新（延迟到 mouseClicked 结束后执行）
        if (this.currentTab == TabButton.FAVORITES) {
            // 更新 itemInfoMap 和 allItemsMap 以反映收藏状态变化
            this.itemInfoMap.clear();
            this.allItemsMap.clear();
            this.favoriteItems.forEach(item -> {
                this.itemInfoMap.put(item.name(), item);
                this.allItemsMap.put(item.name(), item);
            });
            // 设置刷新标志，让 mouseClicked 在处理完所有事件后刷新
            needsRefresh = true;
        }
    }

    /**
     * 根据搜索文本过滤物品列表
     * @param searchText 搜索文本
     */
    private void filterItems(String searchText) {
        // 重置页码
        this.page = 0;

        // 如果搜索文本为空，显示所有物品
        if (searchText.trim().isEmpty()) {
            this.itemInfoMap.clear();
            this.itemInfoMap.putAll(this.allItemsMap);
            refreshPage();
            return;
        }

        // 过滤物品：保留名称包含搜索文本的物品（不区分大小写）
        String lowerSearchText = searchText.toLowerCase();
        this.itemInfoMap.clear();

        this.allItemsMap.forEach((key, value) -> {
            if (value.name().toLowerCase().contains(lowerSearchText)) {
                this.itemInfoMap.put(key, value);
            }
        });

        refreshPage();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int widthCenter = this.width / 2;
        int heightCenter = this.height / 2;
        guiGraphics.blit(BG, widthCenter - WIDTH / 2, heightCenter - HEIGHT / 2, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);

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

    /**
     * 物品信息类型枚举，用于区分物品来源
     */
    public enum ItemType {
        SHOWBLOCK,  // 来自 SHOWBLOCK_ITEM_MAP
        ITEMBLOCK   // 来自 ITEMBLOCK_ITEM_MAP
    }

    public record ItemInfo(ItemStack itemStack, String name, String author, ItemType itemType) {

    }
}
