package com.yuushya.yuushya_os.network;

import com.yuushya.yuushya_os.YuushyaOS;
import com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen;
import com.yuushya.yuushya_os.util.WorkshopItemManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record UploadPayload(CreativeWorkshopScreen.ItemInfo itemInfo) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UploadPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YuushyaOS.MODID, "upload_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CreativeWorkshopScreen.ItemInfo> ITEM_INFO_STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            CreativeWorkshopScreen.ItemInfo::itemStack,
            ByteBufCodecs.STRING_UTF8,
            CreativeWorkshopScreen.ItemInfo::name,
            ByteBufCodecs.STRING_UTF8,
            CreativeWorkshopScreen.ItemInfo::author,
            NeoForgeStreamCodecs.enumCodec(CreativeWorkshopScreen.ItemType.class),
            CreativeWorkshopScreen.ItemInfo::itemType,
            CreativeWorkshopScreen.ItemInfo::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, UploadPayload> STREAM_CODEC = StreamCodec.composite(
            ITEM_INFO_STREAM_CODEC,
            UploadPayload::itemInfo,
            UploadPayload::new
    );

    public static void handle(final UploadPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            CreativeWorkshopScreen.ItemInfo itemInfo = payload.itemInfo;

            // 获取服务端管理器并添加物品
            var server = context.player().getServer();
            if (server != null) {
                WorkshopItemManager manager = WorkshopItemManager.getInstance();
                manager.addItem(itemInfo);

                // 同步给所有玩家
                manager.syncToAllPlayers();
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
