package com.yuushya.yuushya_os.event;

import com.yuushya.yuushya_os.network.UploadNotePayload;
import com.yuushya.yuushya_os.util.WorkshopItemManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * 服务端事件处理
 */
@EventBusSubscriber
public class ServerEventHandler {

    /**
     * 当玩家加入服务器时，同步物品列表和备注数据
     */
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            var server = player.getServer();
            if (server != null) {
                WorkshopItemManager manager = WorkshopItemManager.getInstance();
                if (manager.getItemCount() > 0) {
                    manager.syncToPlayer(player);
                }

                // 同步备注数据
                UploadNotePayload.syncNotesToPlayer(player);
            }
        }
    }
}
