package com.yuushya.yuushya_os.datastorage;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于存储备注数据，每个日期对应一条备注<br>
 * 数据存储在世界数据中，通过 SavedData 持久化
 */
@Setter
@Getter
public class NoteData extends SavedData {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private final Map<LocalDate, String> notes = new HashMap<>();

    private static NoteData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        NoteData data = new NoteData();
        ListTag notesList = compoundTag.getList("notes", CompoundTag.TAG_COMPOUND);

        for (int i = 0; i < notesList.size(); i++) {
            CompoundTag noteTag = notesList.getCompound(i);
            String dateStr = noteTag.getString("date");
            String note = noteTag.getString("note");
            try {
                LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
                data.notes.put(date, note);
            } catch (Exception e) {
                // 忽略无效的日期格式
            }
        }
        return data;
    }

    public static NoteData get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(new SavedData.Factory<>(NoteData::new, NoteData::load), "note_data");
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        ListTag notesList = new ListTag();

        for (Map.Entry<LocalDate, String> entry : notes.entrySet()) {
            CompoundTag noteTag = new CompoundTag();
            noteTag.putString("date", entry.getKey().format(DATE_FORMATTER));
            noteTag.putString("note", entry.getValue());
            notesList.add(noteTag);
        }

        compoundTag.put("notes", notesList);
        return compoundTag;
    }

    /**
     * 获取指定日期的备注
     */
    public String getNote(LocalDate date) {
        return notes.getOrDefault(date, "");
    }

    /**
     * 设置指定日期的备注
     */
    public void setNote(LocalDate date, String note) {
        if (note == null || note.isEmpty()) {
            notes.remove(date);
        } else {
            notes.put(date, note);
        }
        setDirty();
    }

    /**
     * 删除指定日期的备注
     */
    public void removeNote(LocalDate date) {
        notes.remove(date);
        setDirty();
    }

    /**
     * 检查指定日期是否有备注
     */
    public boolean hasNote(LocalDate date) {
        return notes.containsKey(date) && !notes.get(date).isEmpty();
    }

    /**
     * 获取所有备注（转换为字符串键的 Map 用于网络传输）
     */
    public Map<String, String> getNotes() {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<LocalDate, String> entry : notes.entrySet()) {
            result.put(entry.getKey().format(DATE_FORMATTER), entry.getValue());
        }
        return result;
    }
}
