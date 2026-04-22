package com.yuushya.yuushya_os.gui.screen;

import com.yuushya.yuushya_os.gui.widget.MultiLineTextDisplay;
import com.yuushya.yuushya_os.network.UploadNotePayload;
import com.yuushya.yuushya_os.util.ClientNoteData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NoteScreen extends LayerScreen{
    private final LocalDate localDate;
    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_HEIGHT = 200;
    private static final int BUTTON_WIDTH = 60;
    private static final int BUTTON_HEIGHT = 20;
    private static final int PADDING = 10;

    // 本地备注组件
    private MultiLineTextDisplay localNoteDisplay;
    private MultiLineEditBox localNoteBox;

    // 服务端备注组件
    private MultiLineTextDisplay serverNoteDisplay;
    private MultiLineEditBox serverNoteBox;

    private Button localEditButton;
    private Button localSaveButton;
    private Button serverEditButton;
    private Button serverSaveButton;

    private boolean isLocalEditing = false;
    private boolean isServerEditing = false;
    private String localNoteText = "";
    private String serverNoteText = "";

    public NoteScreen(Screen parent, LocalDate localDate) {
        super(Component.literal("Note Screen"), parent);
        this.localDate = localDate;
    }

    @Override
    protected void init() {
        super.init();

        int widthCenter = this.width / 2;
        int heightCenter = this.height / 2;

        // 加载已保存的备注
        loadNotes();

        // 左侧面板：本地客户端备注
        int localPanelX = widthCenter - PANEL_WIDTH - PADDING;
        int localPanelY = heightCenter - PANEL_HEIGHT / 2;

        // 右侧面板：服务端备注
        int serverPanelX = widthCenter + PADDING;
        int serverPanelY = heightCenter - PANEL_HEIGHT / 2;

        // 创建本地备注显示组件和编辑框
        localNoteDisplay = new MultiLineTextDisplay(
            localPanelX + 10,
            localPanelY + 30,
            PANEL_WIDTH - 20,
            PANEL_HEIGHT - 80,
            minecraft.font
        );
        localNoteDisplay.setText(localNoteText);

        localNoteBox = new MultiLineEditBox(
            minecraft.font,
            localPanelX + 10,
            localPanelY + 30,
            PANEL_WIDTH - 20,
            PANEL_HEIGHT - 80,
            Component.literal(""),
            Component.literal("Local Note")
        );
        localNoteBox.setValue(localNoteText);
        localNoteBox.visible = false;

        // 创建服务端备注显示组件和编辑框
        serverNoteDisplay = new MultiLineTextDisplay(
            serverPanelX + 10,
            serverPanelY + 30,
            PANEL_WIDTH - 20,
            PANEL_HEIGHT - 80,
            minecraft.font
        );
        serverNoteDisplay.setText(serverNoteText);

        serverNoteBox = new MultiLineEditBox(
            minecraft.font,
            serverPanelX + 10,
            serverPanelY + 30,
            PANEL_WIDTH - 20,
            PANEL_HEIGHT - 80,
            Component.literal(""),
            Component.literal("Server Note")
        );
        serverNoteBox.setValue(serverNoteText);
        serverNoteBox.visible = false;

        // 创建本地备注按钮
        localEditButton = Button.builder(
            Component.translatable("yuushya_os.note.button.edit"),
            button -> toggleLocalEdit()
        ).bounds(localPanelX + 10, localPanelY + PANEL_HEIGHT - 45, BUTTON_WIDTH, BUTTON_HEIGHT).build();

        localSaveButton = Button.builder(
            Component.translatable("yuushya_os.note.button.save"),
            button -> saveLocalNote()
        ).bounds(localPanelX + BUTTON_WIDTH + 20, localPanelY + PANEL_HEIGHT - 45, BUTTON_WIDTH, BUTTON_HEIGHT).build();

        // 创建服务端备注按钮
        serverEditButton = Button.builder(
            Component.translatable("yuushya_os.note.button.edit"),
            button -> toggleServerEdit()
        ).bounds(serverPanelX + 10, serverPanelY + PANEL_HEIGHT - 45, BUTTON_WIDTH, BUTTON_HEIGHT).build();

        serverSaveButton = Button.builder(
            Component.translatable("yuushya_os.note.button.save"),
            button -> saveServerNote()
        ).bounds(serverPanelX + BUTTON_WIDTH + 20, serverPanelY + PANEL_HEIGHT - 45, BUTTON_WIDTH, BUTTON_HEIGHT).build();

        // 更新按钮可见性
        updateButtonStates();

        this.addRenderableWidget(localNoteDisplay);
        this.addRenderableWidget(localNoteBox);
        this.addRenderableWidget(serverNoteDisplay);
        this.addRenderableWidget(serverNoteBox);
        this.addRenderableWidget(localEditButton);
        this.addRenderableWidget(localSaveButton);
        this.addRenderableWidget(serverEditButton);
        this.addRenderableWidget(serverSaveButton);
    }

    private void toggleLocalEdit() {
        isLocalEditing = !isLocalEditing;
        localNoteBox.visible = isLocalEditing;
        localNoteDisplay.visible = !isLocalEditing;
        if (isLocalEditing) {
            localNoteBox.setFocused(true);
        }
        updateButtonStates();
    }

    private void toggleServerEdit() {
        isServerEditing = !isServerEditing;
        serverNoteBox.visible = isServerEditing;
        serverNoteDisplay.visible = !isServerEditing;
        if (isServerEditing) {
            serverNoteBox.setFocused(true);
        }
        updateButtonStates();
    }

    private void saveLocalNote() {
        localNoteText = localNoteBox.getValue();
        localNoteDisplay.setText(localNoteText);
        // TODO: 保存到本地配置文件
        minecraft.player.sendSystemMessage(Component.literal("本地备注已保存"));
        toggleLocalEdit();
    }

    private void saveServerNote() {
        serverNoteText = serverNoteBox.getValue();
        serverNoteDisplay.setText(serverNoteText);

        // 发送到服务器
        String dateStr = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        if (minecraft.getConnection() != null) {
            minecraft.getConnection().send(new UploadNotePayload(dateStr, serverNoteText));

            // 同时更新客户端缓存
            ClientNoteData.setNote(dateStr, serverNoteText);
        }

        toggleServerEdit();
    }

    private void loadNotes() {
        // 从客户端缓存加载备注（在玩家加入时已从服务端同步）
        String dateStr = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        serverNoteText = ClientNoteData.getNote(dateStr);

        // TODO: 从本地配置文件加载本地备注
        localNoteText = "";
    }

    private void updateButtonStates() {
        localEditButton.active = !isLocalEditing;
        localSaveButton.active = isLocalEditing;
        serverEditButton.active = !isServerEditing;
        serverSaveButton.active = isServerEditing;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int widthCenter = this.width / 2;
        int heightCenter = this.height / 2;

        // 绘制日期标题
        String dateTitle = Component.translatable(
            "yuushya_os.note.title",
            localDate.format(DateTimeFormatter.ofPattern("yyyy年 M月 d日"))
        ).getString();
        guiGraphics.drawCenteredString(font, dateTitle, widthCenter, heightCenter - PANEL_HEIGHT / 2 - 30, 0xFFFFFFFF);

        // 左侧面板背景和标题
        int localPanelX = widthCenter - PANEL_WIDTH - PADDING;
        int localPanelY = heightCenter - PANEL_HEIGHT / 2;
        guiGraphics.fill(localPanelX, localPanelY, localPanelX + PANEL_WIDTH, localPanelY + PANEL_HEIGHT, 0xFF2D2D30);
        drawPanelBorder(guiGraphics, localPanelX, localPanelY, PANEL_WIDTH, PANEL_HEIGHT);

        String localTitle = Component.translatable("yuushya_os.note.local").getString();
        guiGraphics.drawCenteredString(font, localTitle, localPanelX + PANEL_WIDTH / 2, localPanelY + 10, 0xFFFFFFFF);
        localNoteDisplay.render(guiGraphics, mouseX, mouseY, partialTick);

        // 右侧面板背景和标题
        int serverPanelX = widthCenter + PADDING;
        int serverPanelY = heightCenter - PANEL_HEIGHT / 2;
        guiGraphics.fill(serverPanelX, serverPanelY, serverPanelX + PANEL_WIDTH, serverPanelY + PANEL_HEIGHT, 0xFF2D2D30);
        drawPanelBorder(guiGraphics, serverPanelX, serverPanelY, PANEL_WIDTH, PANEL_HEIGHT);

        String serverTitle = Component.translatable("yuushya_os.note.server").getString();
        guiGraphics.drawCenteredString(font, serverTitle, serverPanelX + PANEL_WIDTH / 2, serverPanelY + 10, 0xFFFFFFFF);
        serverNoteDisplay.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    /**
     * 绘制面板边框
     */
    private void drawPanelBorder(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + 1, 0xFF555555);           // 上
        guiGraphics.fill(x, y + height - 1, x + width, y + height, 0xFF555555); // 下
        guiGraphics.fill(x, y, x + 1, y + height, 0xFF555555);          // 左
        guiGraphics.fill(x + width - 1, y, x + width, y + height, 0xFF555555); // 右
    }
}
