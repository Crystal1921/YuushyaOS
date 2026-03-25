package com.yuushya.yuushya_os.dataGen;

import com.yuushya.yuushya_os.YuushyaOS;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = YuushyaOS.MODID, value = Dist.CLIENT)
public class DataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var efh = event.getExistingFileHelper();
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        //Language
        generator.addProvider(
                event.includeClient(), new ModLanguage(output, "zh_cn"));
        generator.addProvider(
                event.includeClient(), new ModLanguage(output, "en_us"));
        //ItemModel
        generator.addProvider(
                event.includeClient(), new ModItem(output, YuushyaOS.MODID, efh));
    }
}
