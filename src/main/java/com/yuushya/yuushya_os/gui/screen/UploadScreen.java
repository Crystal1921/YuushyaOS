package com.yuushya.yuushya_os.gui.screen;

import com.yuushya.modelling.utils.ShareUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen.*;

public class UploadScreen extends AbstractYuushyaScreen {
    ItemInfo itemInfo = null;
    public UploadScreen(CreativeWorkshopScreen parent) {
        super(Component.literal("Upload"), parent);
    }

    @Override
    protected void init() {
        super.init();
        String string = getClipboard();
        try {
            ShareUtils.ShareBlockInformation shareBlockInformation = ShareUtils.from(string);
            if (shareBlockInformation.blocks().isEmpty()) {
                return;
            }
            checkModLack(shareBlockInformation);
        } catch (Exception noBlock) {
            try {
                ShareUtils.ShareItemInformation shareBlockInformation = ShareUtils.fromItems(string);
                if (shareBlockInformation.items().isEmpty()) {
                    return;
                }
                checkModLack(shareBlockInformation);
            } catch (Exception noItem) {
                // 既不是方块信息也不是物品信息，忽略
            }
        }
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

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private String getClipboard() {
        return this.minecraft != null ? TextFieldHelper.getClipboardContents(this.minecraft) : "";
    }

    public void checkModLack(ShareUtils.ShareBlockInformation shareBlockInformation) {
        List<String> unLoaded = shareBlockInformation.mods().stream().filter(id -> !ModList.get().isLoaded(id)).toList();
        Minecraft.getInstance().getToasts().addToast(
                SystemToast.multiline(Minecraft.getInstance(), SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("Mod Lack"), Component.literal(String.join(", ", unLoaded)))
        );
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
