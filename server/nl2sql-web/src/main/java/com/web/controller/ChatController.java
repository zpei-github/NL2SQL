package com.web.controller;

import com.web.constant.ProjectConstant;
import com.web.data.ResultData;
import com.web.service.LLMService;
import com.web.service.SolveService;
import com.web.vo.MyMessage;
import com.web.vo.SendMessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private LLMService llmService;

    @Autowired
    private SolveService solveService;


    @PostMapping("/sendMessage")
    public ResultData sendMessage(@RequestBody SendMessageVO messages) throws Exception {
        MyMessage question = messages.getQuestion();
        /**
        MyMessage question = messages.getQuestion();
        question.setCreateTime(System.currentTimeMillis());
        List<MyMessage> history = messages.getExistMessages();

        MyMessage response = new MyMessage();
        response.setMessageId(question.getMessageId() + 1);
        response.setContent("这是客户端的回复");
        response.setSender(ProjectConstant.CLIENT_SENDER);
        response = llmService.response(question, history, "qwen-plus");
        */
        Map<String, List<String>> map = new HashMap<>();
        map.put("table", new ArrayList<>());
        map.put("column", new ArrayList<>());

        map.get("table").add("审查");
        map.get("table").add("分类");

        map.get("column").add("产品ID");
        map.get("column").add("评论者");
        map.get("column").add("类别名称");

        return ResultData.success(solveService.getMSTree(map));
    }
}
