package com.yuushya.yuushya_os.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 本地备注存储管理类
 * 将本地备注保存到本地文件系统中的 yuushya_note 文件夹
 */
public class LocalNoteStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalNoteStorage.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FOLDER_NAME = "yuushya_note";
    private static final String FILE_NAME = "local_notes.json";

    private static Map<LocalDate, String> localNotesCache = new HashMap<>();
    private static boolean loaded = false;

    /**
     * 获取备注存储文件夹路径
     */
    private static Path getStorageDirectory() {
        Minecraft mc = Minecraft.getInstance();
        return Paths.get(mc.gameDirectory.getPath(), FOLDER_NAME);
        // 默认使用当前目录下的 yuushya_note 文件夹
    }

    /**
     * 获取备注文件路径
     */
    private static Path getStorageFile() {
        return getStorageDirectory().resolve(FILE_NAME);
    }

    /**
     * 初始化存储系统，确保文件夹存在
     */
    private static void initializeStorage() throws IOException {
        Path dir = getStorageDirectory();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
            LOGGER.info("创建本地备注存储文件夹: {}", dir);
        }
    }

    /**
     * 从本地文件加载备注数据
     */
    public static void load() {
        if (loaded) {
            return; // 避免重复加载
        }

        Path file = getStorageFile();
        if (!Files.exists(file)) {
            LOGGER.info("本地备注文件不存在，使用空数据");
            localNotesCache = new HashMap<>();
            loaded = true;
            return;
        }

        try {
            initializeStorage();

            BufferedReader reader = Files.newBufferedReader(file);
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> data = GSON.fromJson(reader, type);
            reader.close();

            // 将字符串日期转换为 LocalDate
            localNotesCache = new HashMap<>();
            if (data != null) {
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    try {
                        LocalDate date = LocalDate.parse(entry.getKey());
                        localNotesCache.put(date, entry.getValue());
                    } catch (Exception e) {
                        LOGGER.warn("无法解析日期: {}", entry.getKey(), e);
                    }
                }
            }

            loaded = true;
            LOGGER.info("成功加载本地备注数据，共 {} 条", localNotesCache.size());
        } catch (Exception e) {
            LOGGER.error("加载本地备注数据失败", e);
            localNotesCache = new HashMap<>();
            loaded = true;
        }
    }

    /**
     * 保存备注数据到本地文件
     */
    public static void save() {
        try {
            initializeStorage();

            // 将 LocalDate 转换为字符串
            Map<String, String> data = new HashMap<>();
            for (Map.Entry<LocalDate, String> entry : localNotesCache.entrySet()) {
                data.put(entry.getKey().toString(), entry.getValue());
            }

            Path file = getStorageFile();
            BufferedWriter writer = Files.newBufferedWriter(file);
            GSON.toJson(data, writer);
            writer.close();

            LOGGER.info("成功保存本地备注数据，共 {} 条", localNotesCache.size());
        } catch (Exception e) {
            LOGGER.error("保存本地备注数据失败", e);
        }
    }

    /**
     * 获取指定日期的本地备注
     */
    public static String getNote(LocalDate date) {
        if (!loaded) {
            load();
        }
        return localNotesCache.getOrDefault(date, "");
    }

    /**
     * 设置指定日期的本地备注
     */
    public static void setNote(LocalDate date, String note) {
        if (!loaded) {
            load();
        }

        if (note == null || note.isEmpty()) {
            localNotesCache.remove(date);
        } else {
            localNotesCache.put(date, note);
        }

        save(); // 自动保存
    }

    /**
     * 删除指定日期的本地备注
     */
    public static void removeNote(LocalDate date) {
        if (!loaded) {
            load();
        }

        localNotesCache.remove(date);
        save();
    }

    /**
     * 检查指定日期是否有本地备注
     */
    public static boolean hasNote(LocalDate date) {
        if (!loaded) {
            load();
        }
        return localNotesCache.containsKey(date) && !localNotesCache.get(date).isEmpty();
    }

    /**
     * 获取所有本地备注
     */
    public static Map<LocalDate, String> getAllNotes() {
        if (!loaded) {
            load();
        }
        return new HashMap<>(localNotesCache);
    }

    /**
     * 清空所有本地备注
     */
    public static void clear() {
        localNotesCache.clear();
        save();
    }

    /**
     * 强制重新加载数据
     */
    public static void reload() {
        loaded = false;
        load();
    }
}
