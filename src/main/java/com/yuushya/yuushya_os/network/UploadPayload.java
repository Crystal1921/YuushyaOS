package com.yuushya.yuushya_os.network;

import com.yuushya.yuushya_os.YuushyaOS;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record UploadPayload(ItemStack itemStack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UploadPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(YuushyaOS.MODID, "scan_scope"));
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
