package com.yuushya.yuushya_os.util;

import com.yuushya.yuushya_os.YuushyaOS;
import com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen;
import com.yuushya.yuushya_os.network.SyncItemsPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端物品管理器单例
 * 用于管理上传的物品列表，并同步给所有客户端
 */
public class WorkshopItemManager {
    private static WorkshopItemManager instance;

    private final List<CreativeWorkshopScreen.ItemInfo> items;

    private WorkshopItemManager() {
        this.items = new ArrayList<>();
    }

    /**
     * 获取管理器实例
     */
    public static WorkshopItemManager getInstance() {
        if (instance == null) {
            instance = new WorkshopItemManager();
        }
        return instance;
    }

    /**
     * 添加物品到列表
     */
    public void addItem(CreativeWorkshopScreen.ItemInfo itemInfo) {
        items.add(itemInfo);
        YuushyaOS.LOGGER.info("Added item: {} by {}", itemInfo.name(), itemInfo.author());
    }

    /**
     * 获取所有物品
     */
    public List<CreativeWorkshopScreen.ItemInfo> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * 同步物品列表给指定玩家
     */
    public void syncToPlayer(ServerPlayer player) {
        SyncItemsPayload payload = new SyncItemsPayload(new ArrayList<>(items));
        PacketDistributor.sendToServer(payload);
        YuushyaOS.LOGGER.debug("Synced {} items to player {}", items.size(), player.getName().getString());
    }

    /**
     * 同步物品列表给所有玩家
     */
    public void syncToAllPlayers() {
        SyncItemsPayload payload = new SyncItemsPayload(new ArrayList<>(items));
        PacketDistributor.sendToAllPlayers(payload);
        YuushyaOS.LOGGER.info("Synced {} items to all players", items.size());
    }

    /**
     * 清空物品列表
     */
    public void clear() {
        items.clear();
        YuushyaOS.LOGGER.info("Cleared all items");
    }

    /**
     * 获取物品数量
     */
    public int getItemCount() {
        return items.size();
    }
}
