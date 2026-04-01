package com.yuushya.yuushya_os.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class YuushyaOSNetwork {
    public static final String VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION);

        // 客户端 -> 服务端：上传物品
        registrar.playToServer(UploadPayload.TYPE, UploadPayload.STREAM_CODEC, UploadPayload::handle);

        // 服务端 -> 客户端：同步物品列表
        registrar.playToClient(SyncItemsPayload.TYPE, SyncItemsPayload.STREAM_CODEC, SyncItemsPayload::handle);
    }
}
