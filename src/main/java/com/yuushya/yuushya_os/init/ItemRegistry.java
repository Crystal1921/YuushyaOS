package com.yuushya.yuushya_os.init;

import com.yuushya.yuushya_os.YuushyaOS;
import com.yuushya.yuushya_os.item.PanelItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(YuushyaOS.MODID);
    public static final DeferredItem<Item> PANEL_ITEM = ITEMS.register("panel_item", PanelItem::new);
}
