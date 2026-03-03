package com.wdbyte.bytepage.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class ChineseToPinyinClean {

    private static final HanyuPinyinOutputFormat PINYIN_FORMAT = new HanyuPinyinOutputFormat();

    static {
        PINYIN_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        PINYIN_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    private static boolean isAlphanumeric(char c) {
        return (c >= 'a' && c <= 'z') ||
            (c >= 'A' && c <= 'Z') ||
            (c >= '0' && c <= '9');
    }

    private static boolean isEmoji(char c) {
        return (c >= 0x1F600 && c <= 0x1F64F) ||
            (c >= 0x1F300 && c <= 0x1F5FF) ||
            (c >= 0x1F680 && c <= 0x1F6FF) ||
            (c >= 0x1F1E0 && c <= 0x1F1FF) ||
            (c >= 0x2600 && c <= 0x26FF) ||
            (c >= 0x2700 && c <= 0x27BF) ||
            Character.isSurrogate(c);
    }

    public static String toCleanPinyin(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        boolean lastWasChinese = false; // 标记上一个输出是否来自汉字

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // 1. 英文字母或数字：直接保留
            if (isAlphanumeric(c)) {
                // 如果前面是汉字，这里不需要加 -（因为字母/数字本身是分隔符）
                result.append(Character.toLowerCase(c));
                lastWasChinese = false;
                continue;
            }

            // 2. Emoji：跳过（包括代理对）
            if (isEmoji(c)) {
                if (Character.isHighSurrogate(c) && i + 1 < input.length()) {
                    char low = input.charAt(i + 1);
                    if (Character.isLowSurrogate(low)) {
                        i++; // 跳过低代理
                    }
                }
                // 不改变 lastWasChinese，因为 emoji 被丢弃，不影响上下文
                continue;
            }

            // 3. 汉字：转拼音
            if (c >= '\u4e00' && c <= '\u9fff') {
                String pinyin = "-";
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, PINYIN_FORMAT);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        pinyin = pinyinArray[0];
                    }
                } catch (BadHanyuPinyinOutputFormatCombination ignored) {
                    // fallback to '-'
                }

                if (lastWasChinese) {
                    // 前一个是汉字，当前也是汉字 → 加横杠分隔
                    result.append("-").append(pinyin);
                } else {
                    result.append(pinyin);
                }
                lastWasChinese = true;
                continue;
            }

            // 4. 其他字符（标点、符号、空格等）→ 视为分隔符，输出 '-'，并重置状态
            // 但注意：我们不立即输出 '-'，而是让后续逻辑通过合并处理
            // 这里我们先输出一个 '-' 占位，并标记 lastWasChinese = false
            result.append("-");
            lastWasChinese = false;
        }

        // 清理：合并多个连续 '-'，去除首尾 '-'
        String clean = result.toString()
            .replaceAll("-+", "-")      // 多个横杠变一个
            .replaceAll("^-|-$", "");   // 去掉开头和结尾

        return clean.isEmpty() ? "unnamed" : clean;
    }

    // 测试
    public static void main(String[] args) {
        String[] tests = {
            "你好World123！😊",
            "北京欢迎你",
            "价格：¥99.99（含税）🚀",
            "Java编程实战",
            "🎉新年快乐🎊2024！",
            "abc123",
            "，。？！@#￥%……&*（）",
            "Hello世界！🌍"
        };

        for (String test : tests) {
            System.out.println("原文: " + test);
            System.out.println("结果: " + toCleanPinyin(test));
            System.out.println("---");
        }
    }
}
