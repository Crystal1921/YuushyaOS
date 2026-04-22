package com.yuushya.yuushya_os.dataGen;
import com.google.gson.JsonObject;
import com.yuushya.yuushya_os.YuushyaOS;
import com.yuushya.yuushya_os.init.ItemRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public class ModLanguage implements DataProvider {
    private final Map<String, String> enData = new TreeMap<>();
    private final Map<String, String> cnData = new TreeMap<>();
    private final PackOutput output;
    private final String locale;

    public ModLanguage(PackOutput output, String locale) {
        this.output = output;
        this.locale = locale;
    }

    private void addTranslations() {
        this.add(ItemRegistry.PANEL_ITEM.get(), "Creative Shop Panel", "创意工坊面板");

        this.add("yuushya_os.panel", "Creative Shop Panel", "创意工坊面板");
        this.add("yuushya_os.local", "Local", "本地");
        this.add("yuushya_os.favorites", "Favorites", "收藏夹");
        this.add("yuushya_os.server", "Server", "服务器");
        this.add("yuushya_os.upload", "Upload", "上传");
        this.add("yuushya_os.search_bar", "Search Bar", "搜索栏");
        this.add("yuushya_os.building_materials", "Building Materials", "建材");
        this.add("yuushya_os.furniture", "Furniture", "家具");
        this.add("yuushya_os.food", "Food", "食物");

        this.add("yuushya_os.calender", "Calendar", "日历");

        // Note screen
        this.add("yuushya_os.note.title", "Note: %s", "备注: %s");
        this.add("yuushya_os.note.local", "Local Note", "本地备注");
        this.add("yuushya_os.note.server", "Server Note", "服务端备注");
        this.add("yuushya_os.note.button.edit", "Edit", "修改");
        this.add("yuushya_os.note.button.save", "Save", "保存");
        this.add("yuushya_os.note.saved", "Server note saved!", "服务端备注已保存！");
        this.add("yuushya_os.note.permission_denied", "Permission Denied", "权限不足");

        // Calendar screen
        this.add("yuushya_os.calendar.screen.title", "Calendar", "日历");
        this.add("yuushya_os.calendar.button.previous", "<", "<");
        this.add("yuushya_os.calendar.button.next", ">", ">");
        this.add("yuushya_os.calendar.button.today", "Today", "今天");
        this.add("yuushya_os.calendar.button.confirm", "Confirm", "确认");
        this.add("yuushya_os.calendar.message.selected_date", "Selected date: %s", "选中: %s");
        this.add("yuushya_os.calendar.text.selected", "Selected: %s", "选中: %s");

        // Calendar weekdays
        this.add("yuushya_os.calendar.sunday", "Sun", "日");
        this.add("yuushya_os.calendar.monday", "Mon", "一");
        this.add("yuushya_os.calendar.tuesday", "Tue", "二");
        this.add("yuushya_os.calendar.wednesday", "Wed", "三");
        this.add("yuushya_os.calendar.thursday", "Thu", "四");
        this.add("yuushya_os.calendar.friday", "Fri", "五");
        this.add("yuushya_os.calendar.saturday", "Sat", "六");

        // Calendar months
        this.add("yuushya_os.calendar.month january", "Jan", "1月");
        this.add("yuushya_os.calendar.month february", "Feb", "2月");
        this.add("yuushya_os.calendar.month march", "Mar", "3月");
        this.add("yuushya_os.calendar.month april", "Apr", "4月");
        this.add("yuushya_os.calendar.month may", "May", "5月");
        this.add("yuushya_os.calendar.month june", "Jun", "6月");
        this.add("yuushya_os.calendar.month july", "Jul", "7月");
        this.add("yuushya_os.calendar.month august", "Aug", "8月");
        this.add("yuushya_os.calendar.month september", "Sep", "9月");
        this.add("yuushya_os.calendar.month october", "Oct", "10月");
        this.add("yuushya_os.calendar.month november", "Nov", "11月");
        this.add("yuushya_os.calendar.month december", "Dec", "12月");

        // Month and year format
        this.add("yuushya_os.calendar.month_year_format", "%s %s", "%s年 %s");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        this.addTranslations();
        Path path = this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(YuushyaOS.MODID).resolve("lang");
        if (this.locale.equals("en_us") && !this.enData.isEmpty()) {
            return this.save(this.enData, cache, path.resolve("en_us.json"));
        }

        if (this.locale.equals("zh_cn") && !this.cnData.isEmpty()) {
            return this.save(this.cnData, cache, path.resolve("zh_cn.json"));
        }

        return CompletableFuture.allOf();
    }

    private CompletableFuture<?> save(Map<String, String> data, CachedOutput cache, Path target) {
        JsonObject json = new JsonObject();
        data.forEach(json::addProperty);
        return DataProvider.saveStable(cache, json, target);
    }

    public void add(Block key, String en, String cn) {
        this.add(key.getDescriptionId(), en, cn);
    }

    public void add(Item key, String en, String cn) {
        this.add(key.getDescriptionId(), en, cn);
    }

    private void add(String key, String en, String cn) {
        if (this.locale.equals("en_us") && !this.enData.containsKey(key)) {
            this.enData.put(key, en);
        } else if (this.locale.equals("zh_cn") && !this.cnData.containsKey(key)) {
            this.cnData.put(key, cn);
        }
    }

    @Override
    public @NotNull String getName() {
        return "language:" + this.locale;
    }
}
