package com.yuushya.yuushya_os.gui.widget;

import lombok.Setter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/**
 * 多行文本显示组件，用于在非编辑状态下显示多行文本
 */
public class MultiLineTextDisplay extends AbstractWidget {
    private final Font font;
    @Setter
    private String text = "";
    private static final int LINE_HEIGHT = 9;
    private static final int TEXT_COLOR = 0xFFDDDDDD;  // 浅灰色文本
    private static final int PADDING = 5;

    public MultiLineTextDisplay(int x, int y, int width, int height, Font font) {
        super(x, y, width, height, Component.empty());
        this.font = font;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (text.isEmpty()) {
            // 显示占位符
            String placeholder = "...";
            guiGraphics.drawString(font, placeholder,
                this.getX() + PADDING,
                this.getY() + PADDING,
                0xFF666666, false);
            return;
        }

        // 渲染多行文本
        int y = this.getY() + PADDING;
        int maxWidth = this.width - PADDING * 2;

        // 按换行符分割文本
        String[] lines = text.split("\\n", -1);

        for (String line : lines) {
            // 如果单行超过最大宽度，进行换行处理
            String remaining = line;
            while (!remaining.isEmpty() && y < this.getY() + this.height - PADDING) {
                // 查找能在最大宽度内显示的最大字符数
                int fitChars = findFitChars(remaining, maxWidth);

                String toDraw = remaining.substring(0, fitChars);
                guiGraphics.drawString(font, toDraw,
                    this.getX() + PADDING,
                    y,
                    TEXT_COLOR, false);
                y += LINE_HEIGHT;
                remaining = remaining.substring(fitChars);
            }

            if (y >= this.getY() + this.height - PADDING) {
                // 文本过长，显示省略号
                guiGraphics.drawString(font, "...",
                    this.getX() + PADDING,
                    y - LINE_HEIGHT,
                    TEXT_COLOR, false);
                break;
            }
        }
    }

    /**
     * 查找能在指定宽度内显示的最大字符数
     */
    private int findFitChars(String text, int maxWidth) {
        if (font.width(text) <= maxWidth) {
            return text.length();
        }

        // 二分查找最大合适长度
        int left = 0;
        int right = text.length();

        while (left < right) {
            int mid = (left + right + 1) / 2;
            String substring = text.substring(0, mid);
            if (font.width(substring) <= maxWidth) {
                left = mid;
            } else {
                right = mid - 1;
            }
        }

        return left;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
