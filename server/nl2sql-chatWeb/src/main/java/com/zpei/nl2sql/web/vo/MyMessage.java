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
package com.zpei.nl2sql.web.vo;

import com.zpei.nl2sql.web.constant.MessageMark;
import lombok.Data;

@Data
public class MyMessage {
    private Integer messageId;
    private String sender;
    private MessageMark messageMark;
    private String uuid;
    private String content;
    private Long createTime;

    public MyMessage() {}

    public MyMessage(Integer messageId, String sender, MessageMark messageMark, String content) {
        this(messageId, sender, messageMark, null, content, System.currentTimeMillis());
    }

    public MyMessage(Integer messageId, String sender, MessageMark messageMark, String uuid, String content, Long createTime) {
        this.messageId = messageId;
        this.sender = sender;
        this.messageMark = messageMark;
        this.uuid = uuid;
        this.content = content;
        this.createTime = createTime;
    }
}
