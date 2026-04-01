package com.yuushya.yuushya_os.network;

import com.yuushya.yuushya_os.YuushyaOS;
import com.yuushya.yuushya_os.util.ClientWorkshopData;
import com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 服务端向客户端同步物品列表的网络包
 */
public record SyncItemsPayload(List<CreativeWorkshopScreen.ItemInfo> items) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncItemsPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(YuushyaOS.MODID, "sync_items"));

    // ItemInfo 的 StreamCodec（复用 UploadPayload 中的定义）
    public static final StreamCodec<RegistryFriendlyByteBuf, CreativeWorkshopScreen.ItemInfo> ITEM_INFO_STREAM_CODEC =
            StreamCodec.composite(
                    net.minecraft.world.item.ItemStack.STREAM_CODEC,
                    CreativeWorkshopScreen.ItemInfo::itemStack,
                    ByteBufCodecs.STRING_UTF8,
                    CreativeWorkshopScreen.ItemInfo::name,
                    ByteBufCodecs.STRING_UTF8,
                    CreativeWorkshopScreen.ItemInfo::author,
                    NeoForgeStreamCodecs.enumCodec(CreativeWorkshopScreen.ItemType.class),
                    CreativeWorkshopScreen.ItemInfo::itemType,
                    CreativeWorkshopScreen.ItemInfo::new
            );

    // ItemInfo 列表的 StreamCodec
    public static final StreamCodec<RegistryFriendlyByteBuf, List<CreativeWorkshopScreen.ItemInfo>> LIST_STREAM_CODEC =
            ITEM_INFO_STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncItemsPayload> STREAM_CODEC = StreamCodec.composite(
            LIST_STREAM_CODEC,
            SyncItemsPayload::items,
            SyncItemsPayload::new
    );

    public static void handle(final SyncItemsPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            // 在客户端处理接收到的物品列表
            // 这里需要将其存储到客户端的某个全局位置，供 CreativeWorkshopScreen 使用
            // 可以通过 Minecraft.getInstance() 访问客户端实例
            var minecraft = net.minecraft.client.Minecraft.getInstance();
            if (minecraft.player != null) {
                // 存储到客户端数据管理器
                ClientWorkshopData.setItems(payload.items);

                YuushyaOS.LOGGER.info("Received {} items from server", payload.items.size());
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
