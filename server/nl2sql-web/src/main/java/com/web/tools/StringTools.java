package com.web.tools;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class StringTools {

    /**
     * 判断输入字符串是否符合指定格式
     * @param input 输入的字符串
     * @return 是否符合格式（true/false）
     * @author zpei
     * @create 2025/2/25
     **/
    public static boolean isValidFormat(String input) {
        // 正则表达式匹配格式：{表名关键字1,表名关键字2...};{字段名关键字1,字段名关键字2...}
        String regex = "^\\{([^,{}]+(,[^,{}]+)*)\\};\\{([^,{}]+(,[^,{}]+)*)\\}$";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 匹配输入字符串
        Matcher matcher = pattern.matcher(input);

        // 返回是否匹配成功
        return matcher.matches();
    }
}
