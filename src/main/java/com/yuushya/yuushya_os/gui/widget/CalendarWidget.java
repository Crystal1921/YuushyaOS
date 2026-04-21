package com.yuushya.yuushya_os.gui.widget;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Windows 风格的可点击日历组件
 */
public class CalendarWidget {
    // 星期标题翻译键
    private static final String[] WEEK_HEADER_KEYS = {
        "yuushya_os.calendar.sunday",
        "yuushya_os.calendar.monday",
        "yuushya_os.calendar.tuesday",
        "yuushya_os.calendar.wednesday",
        "yuushya_os.calendar.thursday",
        "yuushya_os.calendar.friday",
        "yuushya_os.calendar.saturday"
    };

    // 月份翻译键
    private static final String[] MONTH_KEYS = {
        "yuushya_os.calendar.month january",
        "yuushya_os.calendar.month february",
        "yuushya_os.calendar.month march",
        "yuushya_os.calendar.month april",
        "yuushya_os.calendar.month may",
        "yuushya_os.calendar.month june",
        "yuushya_os.calendar.month july",
        "yuushya_os.calendar.month august",
        "yuushya_os.calendar.month september",
        "yuushya_os.calendar.month october",
        "yuushya_os.calendar.month november",
        "yuushya_os.calendar.month december"
    };

    private static final String MONTH_YEAR_FORMAT_KEY = "yuushya_os.calendar.month_year_format";

    // 颜色定义
    private static final int BG_COLOR = 0xFF2D2D30;      // 背景色
    private static final int HEADER_BG_COLOR = 0xFF3C3C3C; // 标题背景色
    private static final int BORDER_COLOR = 0xFF555555;  // 边框颜色
    private static final int TEXT_COLOR = 0xFFFFFFFF;    // 文本颜色
    private static final int TODAY_BG_COLOR = 0xFF0078D7; // 今天背景色（蓝色）
    private static final int SELECTED_BG_COLOR = 0xFFD9D9D9; // 选中背景色
    private static final int OTHER_MONTH_TEXT_COLOR = 0xFF666666; // 其他月份文本颜色
    private static final int WEEKEND_COLOR = 0xFFFF6B6B; // 周末颜色（淡红）
    private final Font font;
    /**
     * -- GETTER --
     * 获取日历的X坐标
     */
    @Getter
    private final int x;
    /**
     * -- GETTER --
     * 获取日历的Y坐标
     */
    @Getter
    private final int y;
    private final int cellWidth;
    private final int cellHeight;
    private final int headerHeight;
    /**
     * -- GETTER --
     * 获取当前显示的月份
     */
    @Getter
    private YearMonth currentMonth;
    /**
     * -- GETTER --
     * 获取当前选中的日期
     */
    @Getter
    private LocalDate selectedDate;
    private final LocalDate today;

    public CalendarWidget(int x, int y, int cellWidth, int cellHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        this.font = minecraft.font;
        this.x = x;
        this.y = y;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.headerHeight = cellHeight;
        this.currentMonth = YearMonth.now();
        this.today = LocalDate.now();
        this.selectedDate = today;
    }

    /**
     * 渲染日历
     */
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 绘制背景
        int totalWidth = cellWidth * 7;
        int totalHeight = headerHeight + cellHeight * 7; // 标题 + 星期头 + 6周日期
        guiGraphics.fill(x, y, x + totalWidth, y + totalHeight, BG_COLOR);
        guiGraphics.fill(x, y, x + totalWidth, y + headerHeight, HEADER_BG_COLOR);

        // 绘制边框
        drawBorder(guiGraphics, x, y, totalWidth, totalHeight);

        // 绘制年月标题
        String monthName = Component.translatable(MONTH_KEYS[currentMonth.getMonthValue() - 1]).getString();
        String year = String.valueOf(currentMonth.getYear());
        String format = Component.translatable(MONTH_YEAR_FORMAT_KEY).getString();
        String monthTitle = String.format(format, monthName, year);
        int titleWidth = font.width(monthTitle);
        guiGraphics.drawString(font, monthTitle,
                x + (totalWidth - titleWidth) / 2,
                y + (headerHeight - font.lineHeight) / 2,
                TEXT_COLOR, false);

        // 绘制星期标题
        for (int i = 0; i < 7; i++) {
            int weekX = x + i * cellWidth;
            int weekY = y + headerHeight;
            String weekHeaderText = Component.translatable(WEEK_HEADER_KEYS[i]).getString();
            drawCenteredText(guiGraphics, weekHeaderText, weekX, weekY, WEEKEND_COLOR, i == 0 || i == 6);
        }

        // 绘制分隔线
        guiGraphics.fill(x, y + headerHeight, x + totalWidth, y + headerHeight + 1, BORDER_COLOR);
        guiGraphics.fill(x, y + headerHeight + cellHeight, x + totalWidth, y + headerHeight + cellHeight + 1, BORDER_COLOR);

        // 获取当月第一天
        LocalDate firstOfMonth = currentMonth.atDay(1);
        // 获取第一天是星期几
        DayOfWeek firstDayOfWeek = firstOfMonth.getDayOfWeek();
        int firstDayIndex = firstDayOfWeek.getValue() % 7; // 让周日为0

        // 计算需要显示的周数（包括上个月和下个月的部分日期）
        LocalDate date = firstOfMonth.minusDays(firstDayIndex);

        // 绘制6周的日期
        for (int week = 0; week < 6; week++) {
            for (int day = 0; day < 7; day++) {
                int cellX = x + day * cellWidth;
                int cellY = y + headerHeight + cellHeight + week * cellHeight;

                LocalDate currentDate = date.plusDays(week * 7 + day);
                boolean isCurrentMonth = currentDate.getMonth() == currentMonth.getMonth();
                boolean isToday = currentDate.equals(today);
                boolean isSelected = currentDate.equals(selectedDate);
                boolean isWeekend = day == 0 || day == 6;

                // 绘制单元格背景
                if (isSelected) {
                    guiGraphics.fill(cellX + 2, cellY + 2, cellX + cellWidth - 2, cellY + cellHeight - 2, SELECTED_BG_COLOR);
                } else if (isToday) {
                    guiGraphics.fill(cellX + 2, cellY + 2, cellX + cellWidth - 2, cellY + cellHeight - 2, TODAY_BG_COLOR);
                }

                // 绘制日期数字
                String dayText = String.valueOf(currentDate.getDayOfMonth());
                int textColor = isCurrentMonth ? TEXT_COLOR : OTHER_MONTH_TEXT_COLOR;
                if (isSelected) {
                    textColor = 0xFF000000; // 选中时文字为黑色
                }
                drawCenteredText(guiGraphics, dayText, cellX, cellY, textColor, isWeekend);

                // 绘制单元格边框
                drawCellBorder(guiGraphics, cellX, cellY);
            }
        }
    }

    /**
     * 处理鼠标点击
     */
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false; // 只处理左键

        // 检查是否点击在日期区域
        int totalWidth = cellWidth * 7;
        int dateAreaY = y + headerHeight + cellHeight;

        if (mouseX < x || mouseX > x + totalWidth ||
                mouseY < dateAreaY || mouseY > dateAreaY + cellHeight * 6) {
            return false;
        }

        // 计算点击的日期
        int col = (int) ((mouseX - x) / cellWidth);
        int row = (int) ((mouseY - dateAreaY) / cellHeight);

        // 获取当月第一天
        LocalDate firstOfMonth = currentMonth.atDay(1);
        DayOfWeek firstDayOfWeek = firstOfMonth.getDayOfWeek();
        int firstDayIndex = firstDayOfWeek.getValue() % 7;

        LocalDate clickedDate = firstOfMonth.minusDays(firstDayIndex).plusDays(row * 7L + col);

        // 更新选中的日期
        selectedDate = clickedDate;

        // 如果点击的是其他月份的日期，切换月份
        if (clickedDate.getMonth() != currentMonth.getMonth()) {
            currentMonth = YearMonth.from(clickedDate);
        }

        return true;
    }

    /**
     * 上一月
     */
    public void previousMonth() {
        currentMonth = currentMonth.minusMonths(1);
    }

    /**
     * 下一月
     */
    public void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
    }

    /**
     * 跳转到今天
     */
    public void goToToday() {
        currentMonth = YearMonth.now();
        selectedDate = today;
    }

    /**
     * 绘制居中文本
     */
    private void drawCenteredText(GuiGraphics guiGraphics, String text, int x, int y, int defaultColor, boolean isWeekend) {
        int textWidth = font.width(text);
        int textX = x + (cellWidth - textWidth) / 2;
        int textY = y + (cellHeight - font.lineHeight) / 2;
        int color = isWeekend ? WEEKEND_COLOR : defaultColor;
        guiGraphics.drawString(font, text, textX, textY, color, false);
    }

    /**
     * 绘制边框
     */
    private void drawBorder(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + 1, BORDER_COLOR);           // 上
        guiGraphics.fill(x, y + height - 1, x + width, y + height, BORDER_COLOR); // 下
        guiGraphics.fill(x, y, x + 1, y + height, BORDER_COLOR);          // 左
        guiGraphics.fill(x + width - 1, y, x + width, y + height, BORDER_COLOR); // 右
    }

    /**
     * 绘制单元格边框
     */
    private void drawCellBorder(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + cellWidth, y + 1, BORDER_COLOR);           // 上
        guiGraphics.fill(x, y + cellHeight - 1, x + cellWidth, y + cellHeight, BORDER_COLOR); // 下
        guiGraphics.fill(x, y, x + 1, y + cellHeight, BORDER_COLOR);          // 左
        guiGraphics.fill(x + cellWidth - 1, y, x + cellWidth, y + cellHeight, BORDER_COLOR); // 右
    }

    /**
     * 获取格式化的选中日期字符串
     */
    public String getSelectedDateString() {
        return String.format("%d-%02d-%02d",
                selectedDate.getYear(),
                selectedDate.getMonthValue(),
                selectedDate.getDayOfMonth());
    }

}
