package com.yuushya.yuushya_os.gui.screen;

import com.yuushya.yuushya_os.gui.widget.CalendarWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen.*;

public class CalenderScreen extends LayerScreen {
    private static final int CELL_SIZE = 24;
    private static final int CALENDAR_WIDTH = CELL_SIZE * 7;

    private CalendarWidget calendarWidget;

    protected CalenderScreen() {
        super(Component.literal("Calendar Screen"));
    }

    @Override
    protected void init() {
        super.init();

        int widthCenter = this.width / 2;
        int heightCenter = this.height / 2;

        // 计算日历位置（居中）
        int calendarX = widthCenter - CALENDAR_WIDTH / 2;
        int calendarY = heightCenter - HEIGHT / 2 - 10;

        // 创建日历组件
        calendarWidget = new CalendarWidget(calendarX, calendarY, CELL_SIZE, CELL_SIZE);

        // 创建月份导航按钮
        int buttonY = calendarY - BUTTON_HEIGHT - 10;

        Button prevMonthButton = Button.builder(Component.literal("<"), button -> calendarWidget.previousMonth())
                .bounds(widthCenter - CALENDAR_WIDTH / 2 - BUTTON_HEIGHT * 2, buttonY, BUTTON_HEIGHT, BUTTON_HEIGHT)
                .build();

        Button nextMonthButton = Button.builder(Component.literal(">"), button -> calendarWidget.nextMonth())
                .bounds(widthCenter + CALENDAR_WIDTH / 2 + BUTTON_HEIGHT, buttonY, BUTTON_HEIGHT, BUTTON_HEIGHT)
                .build();

        Button todayButton = Button.builder(Component.literal("今天"), button -> calendarWidget.goToToday())
                .bounds(widthCenter - BUTTON_WIDTH / 2, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();

        // 确认按钮，显示选中日期
        Button confirmButton = Button.builder(Component.literal("确认"), button -> {
                    String selectedDate = calendarWidget.getSelectedDateString();
                    // 这里可以添加处理选中日期的逻辑
                    minecraft.player.sendSystemMessage(Component.literal("选中的日期: " + selectedDate));
                    onClose();
                })
                .bounds(widthCenter - BUTTON_WIDTH, calendarY + CELL_SIZE * 7 + 20, BUTTON_WIDTH * 2, BUTTON_HEIGHT)
                .build();

        this.addRenderableWidget(prevMonthButton);
        this.addRenderableWidget(nextMonthButton);
        this.addRenderableWidget(todayButton);
        this.addRenderableWidget(confirmButton);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 先处理日历的点击事件
        if (calendarWidget.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (minecraft == null) {
            return;
        }

        int widthCenter = this.width / 2;
        int heightCenter = this.height / 2;

        // 绘制日历
        calendarWidget.render(guiGraphics, mouseX, mouseY);

        // 绘制选中日期提示
        String selectedDateText = "选中: " + calendarWidget.getSelectedDateString();
        int textWidth = font.width(selectedDateText);
        guiGraphics.drawString(font, selectedDateText,
                widthCenter - textWidth / 2,
                calendarWidget.getY() + CELL_SIZE * 7 + CELL_SIZE + 10,
                0xFFFFFFFF, false);
    }
}
