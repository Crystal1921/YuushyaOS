package com.yuushya.yuushya_os.gui.screen;

import com.yuushya.yuushya_os.gui.widget.CalendarWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen.BUTTON_HEIGHT;
import static com.yuushya.yuushya_os.gui.screen.CreativeWorkshopScreen.HEIGHT;

public class CalenderScreen extends LayerScreen {
    private static final int CELL_SIZE = 24;
    private static final int CALENDAR_WIDTH = CELL_SIZE * 7;

    private CalendarWidget calendarWidget;

    protected CalenderScreen(Screen parent) {
        super(Component.translatable("yuushya_os.calendar.screen.title"), parent);
    }

    @Override
    protected void init() {
        super.init();

        int widthCenter = this.width / 2;
        int heightCenter = this.height / 2;

        // 计算日历位置
        int calendarX = widthCenter - CALENDAR_WIDTH / 2 - 65;
        int calendarY = heightCenter - HEIGHT / 2 - 10;

        // 创建日历组件
        calendarWidget = new CalendarWidget(calendarX, calendarY, CELL_SIZE, CELL_SIZE);

        // 创建月份导航按钮
        int buttonY = calendarY - BUTTON_HEIGHT - 10;

        Button prevMonthButton = Button.builder(Component.translatable("yuushya_os.calendar.button.previous"), button -> calendarWidget.previousMonth())
                .bounds(calendarX + CELL_SIZE, buttonY + 10, CELL_SIZE, BUTTON_HEIGHT)
                .build();

        Button nextMonthButton = Button.builder(Component.translatable("yuushya_os.calendar.button.next"), button -> calendarWidget.nextMonth())
                .bounds(calendarX + CELL_SIZE * 5, buttonY + 10, CELL_SIZE, BUTTON_HEIGHT)
                .build();

        Button todayButton = Button.builder(Component.translatable("yuushya_os.calendar.button.today"), button -> calendarWidget.goToToday())
                .bounds(calendarX + (int) (CELL_SIZE * 2.5), buttonY + 10, CELL_SIZE * 2, BUTTON_HEIGHT)
                .build();

        // 确认按钮，显示选中日期
        Button confirmButton = Button.builder(Component.translatable("yuushya_os.calendar.button.confirm"), button -> {
                    minecraft.setScreen(new NoteScreen(this, calendarWidget.getSelectedDate()));
                })
                .bounds(calendarX + (int) (CELL_SIZE * 2.5), calendarY + CELL_SIZE * 7 + 25, CELL_SIZE * 2, BUTTON_HEIGHT)
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
        String selectedDateText = Component.translatable("yuushya_os.calendar.text.selected", calendarWidget.getSelectedDateString()).getString();
        int textWidth = font.width(selectedDateText);
        guiGraphics.drawString(font, selectedDateText,
                widthCenter - textWidth / 2,
                calendarWidget.getY() + CELL_SIZE * 7 + CELL_SIZE + 10,
                0xFFFFFFFF, false);
    }
}
