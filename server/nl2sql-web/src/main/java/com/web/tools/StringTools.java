package com.web.tools;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class StringTools {

    /**
     * 判断输入字符串是否符合指定格式
     *
     * @param input 输入的字符串
     * @return 是否符合格式（true/false）
     * @author zpei
     * @create 2025/2/25
     **/
    public static boolean isValidFormat(String input) {
        // 更新后的正则表达式，确保字段名之间只能使用逗号分隔
        String regex = "\\{(?!:$)([^:]*):([^,}]+(?:,[^,}]+)*)?\\}(?:;\\{(?!:$)([^:]*):([^,}]+(?:,[^,}]+)*)?\\})*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches() && !input.contains("{:}") && !input.contains("{::}");
    }
}
