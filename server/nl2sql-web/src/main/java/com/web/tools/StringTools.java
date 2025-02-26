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

    public static void main(String[] args) {
        // 测试用例
        String test1 = "{table1,table2};{field1,field2}"; // 符合格式
        String test2 = "{table1};{field1}";               // 符合格式
        String test3 = "{table1,table2}{field1,field2}";  // 不符合格式（缺少分号）
        String test4 = "{table1,table2;field1,field2}";  // 不符合格式（分号位置错误）
        String test5 = "{};{}";                          // 不符合格式（至少需要一个关键字）

        System.out.println(isValidFormat(test1)); // true
        System.out.println(isValidFormat(test2)); // true
        System.out.println(isValidFormat(test3)); // false
        System.out.println(isValidFormat(test4)); // false
        System.out.println(isValidFormat(test5)); // false
    }
}
