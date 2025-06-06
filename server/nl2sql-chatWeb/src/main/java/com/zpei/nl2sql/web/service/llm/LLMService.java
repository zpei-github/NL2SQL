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
package com.zpei.nl2sql.web.service.llm;


import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.zpei.nl2sql.web.constant.LLMConstant;
import com.zpei.nl2sql.web.vo.MyMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LLMService {

    @Value("${llm.apikey}")
    private String apikey;

    @Value("${llm.model}")
    private String model;

    public MyMessage response(MyMessage question, List<MyMessage> existMessages) throws Exception {
        MyMessage response = new MyMessage();

        List<Message> tongyiMessages = new ArrayList<>();

        for (MyMessage m : existMessages) {
            switch (m.getSender()) {
                case LLMConstant.USER_SENDER -> tongyiMessages.add(createMessage(Role.USER, m.getContent()));
                case LLMConstant.CLIENT_SENDER -> tongyiMessages.add(createMessage(Role.ASSISTANT, m.getContent()));
                case LLMConstant.SYSTEM_SENDER -> tongyiMessages.add(createMessage(Role.SYSTEM, m.getContent()));
            }
        }
        tongyiMessages.add(createMessage(Role.USER, question.getContent()));

        GenerationParam param = createGenerationParam(tongyiMessages, this.model);
        GenerationResult result = callGenerationWithMessages(param);

        response.setMessageId(question.getMessageId() + 1);
        response.setSender(LLMConstant.CLIENT_SENDER);
        response.setContent(getContent(result));
        response.setCreateTime(System.currentTimeMillis());
        return response;
    }


    public GenerationParam createGenerationParam(List<Message> messages, String model) {
        return GenerationParam.builder()
                .model(model)
                .messages(messages)
                .apiKey(this.apikey)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .build();
    }

    public GenerationResult callGenerationWithMessages(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        return gen.call(param);
    }

    public Message createMessage(Role role, String content) {
        return Message.builder().role(role.getValue()).content(content).build();
    }

    public static String getContent(GenerationResult result) {
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }

    public static String getResultAsJson(GenerationResult result) {
        return JsonUtils.toJson(result);
    }
}
