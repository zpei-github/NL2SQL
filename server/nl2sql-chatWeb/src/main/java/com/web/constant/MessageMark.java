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

package com.web.constant;
import lombok.Getter;

@Getter
public enum MessageMark {
    MM10000(1, "异常消息"),
    MM10001(1 << 1, "数据库表结构信息和连接信息等"),
    MM10002(1 << 2,"查询需求关键字提取要求"),
    MM10003(1 << 3,"查询需求关键字提取结果"),
    MM10004(1 << 4,"SQL生成要求"),
    MM10005(1 << 5,"SQL生成结果"),
    MM10006(1 << 6,""),
    MM10007(1 << 7, ""),
    MM10008(1 << 8, ""),
    MM10009(1 << 9, ""),
    MM10010(1 << 10, ""),
    MM10011(1 << 11, ""),
    MM10012(1 << 12, ""),
    MM10013(1 << 13, ""),
    MM10014(1 << 14, ""),
    MM10015(1 << 15, ""),
    MM10016(1 << 16, ""),
    MM10017(1 << 17, ""),
    MM10018(1 << 18, ""),
    MM10019(1 << 19, ""),
    MM10020(1 << 20, ""),
    MM10021(1 << 21, ""),
    MM10022(1 << 22, ""),
    MM10023(1 << 23, ""),
    MM10024(1 << 24, ""),
    MM10025(1 << 25, ""),
    MM10026(1 << 26, ""),
    MM10027(1 << 27, ""),
    MM10028(1 << 28, ""),
    MM10029(1 << 29, ""),
    MM10030(1 << 30, ""),;
    /**自定义状态码**/
    private final int code;
    /**自定义描述**/
    private final String mark;

    MessageMark(int code, String mark){
        this.code = code;
        this.mark = mark;
    }

    @Override
    public String toString() {
        return this.code + " " + this.mark;
    }

}
