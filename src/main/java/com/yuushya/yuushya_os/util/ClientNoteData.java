package com.yuushya.yuushya_os.util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端备注数据缓存
 * 用于存储从服务端同步过来的备注数据
 */
public class ClientNoteData {
    private static final Map<LocalDate, String> noteCache = new HashMap<>();

    /**
     * 根据日期获取备注
     */
    public static String getNote(LocalDate date) {
        return noteCache.getOrDefault(date, "");
    }

    /**
     * 设置备注
     */
    public static void setNote(LocalDate date, String note) {
        if (note == null || note.isEmpty()) {
            noteCache.remove(date);
        } else {
            noteCache.put(date, note);
        }
    }

    /**
     * 检查指定日期是否有备注
     */
    public static boolean hasNote(LocalDate date) {
        return noteCache.containsKey(date) && !noteCache.get(date).isEmpty();
    }

    /**
     * 清空缓存
     */
    public static void clear() {
        noteCache.clear();
    }

    /**
     * 获取所有备注（用于批量操作）
     */
    public static Map<LocalDate, String> getAllNotes() {
        return new HashMap<>(noteCache);
    }
}
