package com.yuushya.yuushya_os;

import com.yuushya.yuushya_os.init.ItemRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(YuushyaOS.MODID)
public class YuushyaOS {
    public static final String MODID = "yuushya_os";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public YuushyaOS(IEventBus modEventBus, ModContainer modContainer) {
        ItemRegistry.ITEMS.register(modEventBus);
    }
}
