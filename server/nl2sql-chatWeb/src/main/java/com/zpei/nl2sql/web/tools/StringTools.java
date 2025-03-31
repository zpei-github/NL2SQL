/*
 *   Copyright (c) 2024 zpei-github
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.zpei.nl2sql.web.tools;
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

        String regex = "\\{(?!:$)([^:]*):([^,}]+(?:,[^,}]+)*)?\\}(?:;\\{(?!:$)([^:]*):([^,}]+(?:,[^,}]+)*)?\\})*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches() && !input.contains("{:}") && !input.contains("{::}");
    }
}
