package com.yuushya.yuushya_os;

import com.yuushya.yuushya_os.init.ItemRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(YuushyaOS.MODID)
public class YuushyaOS {
    public static final String MODID = "yuushya_os";

    public YuushyaOS(IEventBus modEventBus, ModContainer modContainer) {
        ItemRegistry.ITEMS.register(modEventBus);
    }
}
