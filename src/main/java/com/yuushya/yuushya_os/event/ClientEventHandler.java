package com.yuushya.yuushya_os.event;

import com.yuushya.yuushya_os.util.LocalNoteStorage;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

/**
 * 客户端事件处理
 */
@EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {

    /**
     * 当客户端玩家加入世界时，加载本地备注数据
     */
    @SubscribeEvent
    public static void onPlayerJoin(FMLClientSetupEvent event) {
        // 加载本地备注数据
        event.enqueueWork(LocalNoteStorage::load);
    }
}
