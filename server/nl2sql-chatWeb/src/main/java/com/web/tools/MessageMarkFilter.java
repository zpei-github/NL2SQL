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
package com.web.tools;


import com.web.constant.MessageMark;
import com.web.vo.MyMessage;

import java.util.Iterator;
import java.util.List;

public class MessageMarkFilter {

    /** 存在过滤器
     * messages中message的MessageMark出现在filters中，就会被删除
     * @param messages 消息列表
     * @param filters 过滤器，也就是MessageMark的组合
     * @return
     * @author zpei
     * @create 2025/3/29
     **/
    public static void existFilter(List<MyMessage> messages, MessageMark[] filters) {
        int filterPart = 0;
        for (MessageMark filter : filters) {
            filterPart = filterPart | filter.getCode();
        }

        Iterator<MyMessage> iterator = messages.iterator();
        while (iterator.hasNext()) {
            MyMessage message = iterator.next();
            if(message.getMessageMark() == null) continue;

            int markCode = message.getMessageMark().getCode();
            if ((markCode & filterPart) == markCode) iterator.remove();
        }
    }


    /** 不属于过滤器
     * messages中message的MessageMark不属于filters，就会被删除
     * @param messages 消息列表
     * @param filters 过滤器，也就是MessageMark的组合
     * @return
     * @author zpei
     * @create 2025/3/29
     **/
    public static void notExistFilter(List<MyMessage> messages, MessageMark[] filters) {
        int filterPart = 0;
        for (MessageMark filter : filters) {
            filterPart = filterPart | filter.getCode();
        }
        Iterator<MyMessage> iterator = messages.iterator();
        while (iterator.hasNext()) {
            MyMessage message = iterator.next();
            if(message.getMessageMark() == null) continue;
            int markCode = message.getMessageMark().getCode();
            if ((markCode & filterPart) != markCode) iterator.remove();
        }
    }
}
