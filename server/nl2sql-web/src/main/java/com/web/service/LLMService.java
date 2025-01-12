package com.web.service;


import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import com.alibaba.dashscope.utils.JsonUtils;
import com.web.constant.ProjectConstant;
import com.web.vo.MyMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LLMService {
    public MyMessage response(MyMessage question, List<MyMessage> existMessages, String model) throws Exception {
        MyMessage response = new MyMessage();

        List<Message> tongyiMessages = new ArrayList<>();
        tongyiMessages.add(createMessage(Role.SYSTEM, "你是一个智能助手，你需要将我指定的SQL查询需求找出查询的表名关键字和字段名关键字并按照指定格式输出，只能从需求中获取表名和字段名关键字，不可以添加额外的内容。格式如下：" +
                "表名关键字:[表1],[表2]...\n" +
                "字段名关键字:[字段1],[字段2]..."));
        for (MyMessage m : existMessages) {
            if (m.getSender().equals(ProjectConstant.USER_SENDER)) {
                tongyiMessages.add(createMessage(Role.USER, m.getContent()));
            }
            if (m.getSender().equals(ProjectConstant.CLIENT_SENDER)) {
                tongyiMessages.add(createMessage(Role.ASSISTANT, m.getContent()));
            }
        }
        tongyiMessages.add(createMessage(Role.USER, question.getContent()));

        GenerationParam param = createGenerationParam(tongyiMessages, model);
        GenerationResult result = callGenerationWithMessages(param);

        response.setMessageId(question.getMessageId() + 1);
        response.setSender(ProjectConstant.CLIENT_SENDER);
        response.setContent(getContent(result));
        response.setCreateTime(System.currentTimeMillis());
        return response;
    }


    public static GenerationParam createGenerationParam(List<Message> messages, String model) {
        return GenerationParam.builder()
                .model(model)
                .messages(messages)
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .build();
    }

    public static GenerationResult callGenerationWithMessages(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        return gen.call(param);
    }

    public static Message createMessage(Role role, String content) {
        return Message.builder().role(role.getValue()).content(content).build();
    }

    public static String getContent(GenerationResult result) {
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }

    public static String getResultAsJson(GenerationResult result) {
        return JsonUtils.toJson(result);
    }
}
