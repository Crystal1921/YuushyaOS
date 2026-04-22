package com.yuushya.yuushya_os.network;

import com.yuushya.yuushya_os.YuushyaOS;
import com.yuushya.yuushya_os.util.ClientNoteData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端向客户端同步所有备注数据
 */
public record SyncNotePayload(Map<String, String> notes) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncNotePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(YuushyaOS.MODID, "sync_notes"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncNotePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new,
                    ByteBufCodecs.STRING_UTF8,
                    ByteBufCodecs.STRING_UTF8),
            SyncNotePayload::notes,
            SyncNotePayload::new
    );
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static void handle(final SyncNotePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                // 存储到客户端数据管理器
                ClientNoteData.clear();
                for (Map.Entry<String, String> entry : payload.notes.entrySet()) {
                    try {
                        LocalDate date = LocalDate.parse(entry.getKey(), FORMATTER);
                        ClientNoteData.setNote(date, entry.getValue());
                    } catch (Exception e) {
                        // 忽略无效的日期格式
                        YuushyaOS.LOGGER.warn("Invalid date format in note sync: {}", entry.getKey());
                    }
                }

                YuushyaOS.LOGGER.info("Received {} notes from server", payload.notes.size());
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
