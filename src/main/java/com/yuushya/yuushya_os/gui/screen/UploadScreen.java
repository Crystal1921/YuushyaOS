package com.yuushya.yuushya_os.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yuushya.modelling.blockentity.transformData.TransformBlockData;
import com.yuushya.modelling.blockentity.transformData.TransformItemData;
import com.yuushya.modelling.registries.ItemRegistry;
import com.yuushya.modelling.utils.ShareUtils;
import com.yuushya.yuushya_os.network.UploadPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.yuushya.modelling.item.showblocktool.DestroyItem.saveToItem;
import static com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen.*;

public class UploadScreen extends LayerScreen {
    EditBox nameEditBox;
    private ItemInfo itemInfo = null;
    private ItemType itemType = ItemType.SHOWBLOCK;

    public UploadScreen() {
        super(Component.literal("Upload"));
    }

    @Override
    protected void init() {
        super.init();
        Button pasteButton = Button.builder(Component.literal("Paste"), (button -> {
                    ItemStack result = pasteFromClipboard();
                    if (result.isEmpty()) {
                        Minecraft.getInstance().getToasts().addToast(
                                SystemToast.multiline(Minecraft.getInstance(), SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("Parse Failed"), Component.literal("Failed to parse clipboard content."))
                        );
                    }
                }))
                .bounds(widthCenter + 100, heightCenter - BUTTON_HEIGHT / 2, BUTTON_WIDTH * 2, BUTTON_HEIGHT * 2)
                .build();

        Button uploadButton = Button.builder(Component.literal("Upload"), (button -> {
                    if (itemInfo != null) {
                        PacketDistributor.sendToServer(new UploadPayload(this.itemInfo));
                    }
                }))
                .bounds(widthCenter + 100, heightCenter + BUTTON_INTERVAL - BUTTON_HEIGHT, BUTTON_WIDTH * 2, BUTTON_HEIGHT * 2)
                .build();

        nameEditBox = new EditBox(minecraft.font, widthCenter - WIDTH / 2, heightCenter - HEIGHT / 2, 80, BUTTON_HEIGHT, Component.literal("Name"));
        nameEditBox.setMaxLength(20);

        this.addRenderableWidget(pasteButton);
        this.addRenderableWidget(uploadButton);
        this.addRenderableWidget(nameEditBox);
    }

    private ItemStack pasteFromClipboard() {
        String clipboardContent = getClipboard();
        String value = nameEditBox.getValue();
        if (value.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return ItemStack.EMPTY;
        }
        RegistryAccess access = level.registryAccess();

        // 尝试解析方块信息
        ItemStack blockResult = tryParseBlockInfo(clipboardContent, value, access);
        if (!blockResult.isEmpty()) {
            return blockResult;
        }

        // 尝试解析物品信息
        return tryParseItemInfo(clipboardContent, value, access);
    }

    private ItemStack tryParseBlockInfo(String content, String name, RegistryAccess access) {
        try {
            ShareUtils.ShareBlockInformation blockInfo = ShareUtils.from(content);
            if (blockInfo.blocks().isEmpty()) {
                return ItemStack.EMPTY;
            }
            if (!checkModLack(blockInfo)) {
                return ItemStack.EMPTY;
            }
            ItemStack result = getBlockItemStack(blockInfo, name, access);
            this.itemInfo = new ItemInfo(result, name, minecraft.player.getName().getString(), ItemType.SHOWBLOCK);
            this.itemType = ItemType.SHOWBLOCK;
            return result;
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    private ItemStack getBlockItemStack(ShareUtils.ShareBlockInformation blockInfo, String name, RegistryAccess access) {
        List<TransformBlockData> transformDataList = new ArrayList<>();
        blockInfo.transfer(transformDataList);
        ItemStack resultItemStack = ItemRegistry.SHOW_BLOCK.get().getDefaultInstance();
        resultItemStack.set(DataComponents.ITEM_NAME, Component.literal(name));
        saveToItem(resultItemStack, transformDataList, access);
        return resultItemStack;
    }

    private ItemStack tryParseItemInfo(String content, String name, RegistryAccess access) {
        try {
            ShareUtils.ShareItemInformation itemInfo = ShareUtils.fromItems(content);
            if (itemInfo.items().isEmpty()) {
                return ItemStack.EMPTY;
            }
            checkModLack(itemInfo);
            ItemStack result = getItemItemStack(itemInfo, name, access);
            this.itemInfo = new ItemInfo(result, name, minecraft.player.getName().getString(), ItemType.ITEMBLOCK);
            this.itemType = ItemType.ITEMBLOCK;
            return result;
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    public ItemStack getItemItemStack(ShareUtils.ShareItemInformation itemInfo, String name, RegistryAccess access) {
        List<TransformItemData> transformDataList = new ArrayList<>();
        itemInfo.transferItems(transformDataList);
        ItemStack resultItemStack = ItemRegistry.ITEM_BLOCK.get().getDefaultInstance();
        resultItemStack.set(DataComponents.ITEM_NAME, Component.literal(name));
        saveToItem(resultItemStack, transformDataList, access);
        return resultItemStack;
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

        if (itemInfo != null) {
            PoseStack pose = guiGraphics.pose();
            pose.pushPose();
            pose.translate(widthCenter, heightCenter, 50);
            pose.scale(64.0f, -64.0f, 64.0f);
            minecraft.getItemRenderer().renderStatic(this.itemInfo.itemStack(), ItemDisplayContext.GUI, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, pose, guiGraphics.bufferSource(), level, 0);
            pose.popPose();
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private String getClipboard() {
        return this.minecraft != null ? TextFieldHelper.getClipboardContents(this.minecraft) : "";
    }

    public boolean checkModLack(ShareUtils.ShareBlockInformation shareBlockInformation) {
        List<String> unLoaded = shareBlockInformation.mods().stream().filter(id -> !ModList.get().isLoaded(id)).toList();
        if (unLoaded.isEmpty()) return true;
        Minecraft.getInstance().getToasts().addToast(
                SystemToast.multiline(Minecraft.getInstance(), SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("Mod Lack"), Component.literal(String.join(", ", unLoaded)))
        );
        return false;
    }

    public void checkModLack(ShareUtils.ShareItemInformation shareInformation) {
        List<String> unLoaded = shareInformation.mods().stream().filter(id -> !ModList.get().isLoaded(id)).toList();
        if (unLoaded.isEmpty()) return;
        if (unLoaded.contains("yuushya")) return;
        Minecraft.getInstance().getToasts().addToast(
                SystemToast.multiline(Minecraft.getInstance(), SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("Mod Lack"), Component.literal(String.join(", ", unLoaded)))
        );
    }
}
