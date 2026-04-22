package com.yuushya.yuushya_os.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端备注数据缓存
 * 用于存储从服务端同步过来的备注数据
 */
public class ClientNoteData {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final Map<String, String> noteCache = new HashMap<>();

    /**
     * 根据日期字符串获取备注
     */
    public static String getNote(String dateStr) {
        return noteCache.getOrDefault(dateStr, "");
    }

    /**
     * 根据日期获取备注
     */
    public static String getNote(LocalDate date) {
        return getNote(date.format(DATE_FORMATTER));
    }

    /**
     * 设置备注
     */
    public static void setNote(String dateStr, String note) {
        if (note == null || note.isEmpty()) {
            noteCache.remove(dateStr);
        } else {
            noteCache.put(dateStr, note);
        }
    }

    /**
     * 设置备注
     */
    public static void setNote(LocalDate date, String note) {
        setNote(date.format(DATE_FORMATTER), note);
    }

    /**
     * 清空缓存
     */
    public static void clear() {
        noteCache.clear();
    }
}
