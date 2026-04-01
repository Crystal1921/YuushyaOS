package com.yuushya.yuushya_os.util;

import com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端物品数据存储
 * 用于存储从服务端同步的物品列表
 */
public class ClientWorkshopData {
    private static final List<CreativeWorkshopScreen.ItemInfo> items = new ArrayList<>();

    /**
     * 设置物品列表（通常在接收到服务端同步时调用）
     */
    public static void setItems(List<CreativeWorkshopScreen.ItemInfo> newItems) {
        items.clear();
        items.addAll(newItems);
    }

    /**
     * 获取物品列表
     */
    public static List<CreativeWorkshopScreen.ItemInfo> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * 添加单个物品
     */
    public static void addItem(CreativeWorkshopScreen.ItemInfo itemInfo) {
        items.add(itemInfo);
    }

    /**
     * 清空物品列表
     */
    public static void clear() {
        items.clear();
    }

    /**
     * 获取物品数量
     */
    public static int getItemCount() {
        return items.size();
    }
}
