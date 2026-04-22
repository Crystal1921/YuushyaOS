package com.yuushya.yuushya_os.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.time.LocalDate;

public class NoteScreen extends LayerScreen{
    private final LocalDate localDate;
    public NoteScreen(Screen parent, LocalDate localDate) {
        super(Component.literal("Note Screen"), parent);
        this.localDate = localDate;
    }
}
