package com.yuushya.yuushya_os.network;

import com.yuushya.yuushya_os.YuushyaOS;
import com.yuushya.yuushya_os.datastorage.NoteData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 客户端向服务端上传/保存备注
 */
public record UploadNotePayload(String dateStr, String note) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UploadNotePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(YuushyaOS.MODID, "upload_note"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UploadNotePayload> STREAM_CODEC = StreamCodec.composite(
            net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8,
            UploadNotePayload::dateStr,
            net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8,
            UploadNotePayload::note,
            UploadNotePayload::new
    );

    public static void handle(final UploadNotePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            try {
                LocalDate date = LocalDate.parse(payload.dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                NoteData noteData = NoteData.get(player.serverLevel());

                // 保存备注
                noteData.setNote(date, payload.note);

                // 同步所有备注数据给客户端
                syncNotesToPlayer(player);

                // 确认保存成功
                player.sendSystemMessage(Component.translatable("yuushya_os.note.saved"));

                YuushyaOS.LOGGER.info("Player {} saved note for date: {}, length: {}",
                    player.getName().getString(), payload.dateStr, payload.note.length());
            } catch (Exception e) {
                player.sendSystemMessage(Component.literal("Failed to save note: " + e.getMessage()));
                YuushyaOS.LOGGER.error("Failed to save note for date: " + payload.dateStr, e);
            }
        });
    }

    /**
     * 同步备注数据给指定玩家
     */
    public static void syncNotesToPlayer(ServerPlayer player) {
        NoteData noteData = NoteData.get(player.serverLevel());
        player.connection.send(new SyncNotePayload(noteData.getNotes()));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
