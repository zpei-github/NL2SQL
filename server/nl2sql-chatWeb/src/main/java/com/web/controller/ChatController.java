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
package com.web.controller;

import com.web.constant.MessageMark;
import com.web.constant.LLMConstant;
import com.web.constant.Prompts;
import com.web.constant.ServerConstant;
import com.web.data.ResultData;
import com.web.entity.milvus.StandardColumnSchema;
import com.web.entity.milvus.StandardTableSchema;
import com.web.entity.mysql.standard_database.StandardColumn;
import com.web.entity.mysql.standard_database.StandardTable;
import com.web.service.llm.LLMService;
import com.web.service.graph.GraphComponent;
import com.web.service.milvus.ColumnMilvusService;
import com.web.service.milvus.TableMilvusService;
import com.web.service.mysql_database.ColumnService;
import com.web.service.mysql_database.TableService;
import com.web.tools.MessageMarkFilter;
import com.web.tools.StringTools;
import com.web.vo.MyMessage;
import com.web.vo.SendMessageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private TableService tableService;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private TableMilvusService tMilService;

    @Autowired
    private ColumnMilvusService cMilService;

    @Autowired
    private GraphComponent gComponent;


    private static final Logger logger = LoggerFactory.getLogger(GraphComponent.class);


    @PostMapping("/sendMessage")
    public ResultData sendMessage(@RequestBody SendMessageVO messages) throws Exception {
        MyMessage question = messages.getQuestion();
        question.setCreateTime(System.currentTimeMillis());
        question.setMessageMark(MessageMark.MM10002);
        List<MyMessage> history = messages.getExistMessages();

        // 关键字
        MyMessage keywords = null;

        // 返回消息
        MyMessage answer = new MyMessage();

        // 返回的聊天记录
        List<MyMessage> returnData = new LinkedList<>();

        //设置prompt
        MyMessage prompt = new MyMessage(0, LLMConstant.SYSTEM_SENDER, MessageMark.MM10002, Prompts.SYSTEM_SENDER_KEYWORD_ANALYSE_PROMPT);

        // 删除历史聊天记录中无关的信息，防止无关信息的干扰，减少token使用量
        history.add(0, prompt);

        // 只过滤出MessageMark.MM10002和MessageMark.MM10003的消息
        MessageMarkFilter.notExistFilter(history, new MessageMark[]{MessageMark.MM10002, MessageMark.MM10003});

        keywords = llmService.response(question, history);
        keywords.setMessageId(question.getMessageId() + 1);
        keywords.setMessageMark(MessageMark.MM10003);

        returnData.add(question);

        history.add(question);
        history.add(keywords);

        // 格式重复解析
        int retryTimes = 0;
        MyMessage correct = new MyMessage(question.getMessageId(), LLMConstant.USER_SENDER, null, Prompts.LLM_EXPORT_WRONG_FORMAT_PROMPT);
        while (!StringTools.isValidFormat(keywords.getContent())) {
            keywords = llmService.response(correct, history);
            correct.setMessageId(keywords.getMessageId() - 1);
            correct.setCreateTime(System.currentTimeMillis());

            history.add(correct);
            history.add(keywords);

            retryTimes++;
            if (retryTimes > 3) {
                correct.setMessageId(question.getMessageId()+1);
                correct.setCreateTime(System.currentTimeMillis());
                correct.setMessageMark(MessageMark.MM10000);
                correct.setSender(LLMConstant.CLIENT_SENDER);
                correct.setContent(Prompts.SERVICE_RETRY_PROMPT);

                returnData.add(correct);
                return ResultData.success(returnData);
            }
        }

        returnData.add(keywords);

        // 组成输出结果
        answer.setCreateTime(System.currentTimeMillis());
        answer.setSender(LLMConstant.CLIENT_SENDER);
        answer.setMessageId(keywords.getMessageId() + 1);

        answer.setContent(getDatabaseInfo(keywords.getContent(), true, true, true));

        if(answer.getContent().equals(ServerConstant.STEINER_TREE_SERVICE_ERROR) || answer.getContent().equals(ServerConstant.DATABASE_SERVICE_ERROR)) {
            answer.setContent("本地数据库服务异常，请重新尝试");
            answer.setMessageMark(MessageMark.MM10000);
        }else {
            answer.setMessageMark(MessageMark.MM10001);
        }

        returnData.add(answer);

        return ResultData.success(returnData);
    }

    @PostMapping("/get_sql")
    public ResultData generateSQL(@RequestBody SendMessageVO messages) throws Exception {
        // 用户提问消息
        MyMessage question = messages.getQuestion();
        question.setMessageMark(MessageMark.MM10004);
        question.setCreateTime(System.currentTimeMillis());

        // 聊天历史记录
        LinkedList<MyMessage> history = messages.getExistMessages();

        // 返回值
        List<MyMessage> returnData = new LinkedList<>();

        // 大模型返回结果
        MyMessage response = null;


        // prompt
        MyMessage prompt = new MyMessage(0, LLMConstant.SYSTEM_SENDER, MessageMark.MM10004, Prompts.SQL_GENERATE_PROMPT);

        // 缺省消息
        MyMessage localQuestion = new MyMessage(question.getMessageId(), LLMConstant.USER_SENDER, MessageMark.MM10004, question.getContent() + "\n现在需要根据我的需求和上下文给出SQL查询语句");

        // 删除历史聊天记录中无关的信息，防止无关信息的干扰，减少token使用量
        boolean mark = false;
        for(int i = history.size() - 1; i >= 0; i--){
            if(MessageMark.MM10000.equals(history.get(i).getMessageMark()) || (mark && MessageMark.MM10001.equals(history.get(i).getMessageMark()))){
                history.remove(i);
            } else if (MessageMark.MM10001.equals(history.get(i).getMessageMark())) {
                mark = true;
            }
        }

        returnData.add(question);
        response = llmService.response(localQuestion, history);
        response.setMessageMark(MessageMark.MM10005);
        returnData.add(response);

        return ResultData.success(returnData);
    }


    /**
     * 将关键字信息转换为表结构信息和表连接信息
     * @param keywords 关键字
     * @param isAddDDL 是否添加表结构信息
     * @param isAddTableConnectInfo 是否添加表连接信息
     * @param isAddKeywordMap 是否添加关键字映射字段信息
     * @return  信息
     * @author zpei
     * @create 2025/2/25
     **/
    public String getDatabaseInfo(String keywords, boolean isAddDDL, boolean isAddTableConnectInfo,boolean isAddKeywordMap) {
        StringBuilder content = new StringBuilder();

        // 表关键字
        List<StandardTable> tables = new ArrayList<>();

        // 用于聚合的字段关键字
        List<StandardColumn> columns = new ArrayList<>();

        // 表关键字与源表名映射记录[关键字, 源表名]
        List<String[]> tableNameMap = new ArrayList<>();

        // 字段关键字与源字段名映射记录[关键字, 源字段名]
        List<String[]> columnNameMap = new ArrayList<>();

        // 关联关系提取
        String[] relations = keywords.split(";");

        try{
            for(String relation : relations){
                String[] keyword = relation.substring(1, relation.length() - 1).split(":");
                String tableKeyword = keyword[0].trim();
                String[] columnKeyword = keyword[1].split(",");

                // 关键字引擎匹配到的表列表
                List<StandardTableSchema> stTable = null;

                // 关键字引擎匹配到的字段列表
                List<StandardColumnSchema> stColumn = null;

                if (!tableKeyword.equals("")
                        // 开始匹配表关键字
                        && (stTable = tMilService.hybridSearch(tableKeyword, 3)) != null
                        && !stTable.isEmpty()) {
                    StandardTable stt = tableService.getTableByStandardTableId(stTable.get(0).getStandard_table_id());
                    tables.add(stt);
                    tableNameMap.add(new String[]{tableKeyword,stt.getOriginal_table_name()});

                    for(String column : columnKeyword){
                        if (!column.equals("")
                                // 开始匹配字段关键字
                                && (stColumn = cMilService.hybridSearch(column, 3)) != null
                                && !stColumn.isEmpty()){
                            StandardColumn stc = columnService.getStandardColumnByStandardColumnId(stColumn.get(0).getStandard_column_id());
                            columnNameMap.add(new String[]{column,stc.getOriginal_column_name()});
                        }
                    }
                    continue;
                }

                for(String column : columnKeyword){
                    if (!column.equals("")
                            && (stColumn = cMilService.hybridSearch(column, 3)) != null
                            && !stColumn.isEmpty()){
                        StandardColumn stc = columnService.getStandardColumnByStandardColumnId(stColumn.get(0).getStandard_column_id());
                        columns.add(stc);
                        columnNameMap.add(new String[]{column,stc.getOriginal_column_name()});
                    }
                }
            }
        }catch (Exception e){

            // 设置返回消息
            content.append(ServerConstant.DATABASE_SERVICE_ERROR);
            logger.error(e.getMessage());
            return content.toString();
        }

        Map<List<String>, List<String>> tree = gComponent.getMSTree(tables, columns);

        Set<String> tableSet = new HashSet<>();

        if (tree.isEmpty()) {

            // 设置返回消息
            content.append(ServerConstant.STEINER_TREE_SERVICE_ERROR);
        } else {
            content.append("根据关键字结合本地数据库数据得出以下信息: ");

            if(isAddTableConnectInfo){
                content.append("\n表连接条件如下:\n");
                for (Map.Entry<List<String>, List<String>> entry : tree.entrySet()) {
                    content.append("表");
                    for (String table : entry.getValue()) {
                        content.append(table).append(", ");
                        tableSet.add(table);
                    }
                    content.append(" 通过以下字段进行连接 ");

                    for (String column : entry.getKey()) {
                        content.append(column).append(",");
                    }
                    content.append(";\n\n");
                }
            }

            if(isAddDDL){
                content.append("\n各表的表结构如下:\n");
                for (String table : tableSet) {
                    content.append(tableService.getTableByOriginalTableName(table).getOriginal_table_ddl()).append("\n\n");
                }
            }

            // 判断是否添加关键字映射
            if(isAddKeywordMap){
                content.append("\n\n表关键字映射如下:\n");
                for(String[] tMap: tableNameMap){
                    content.append(tMap[0]).append(":").append(tMap[1]).append(",\n");
                }
                content.append("\n\n字段关键字映射如下:\n");
                for(String[] tMap: columnNameMap){
                    content.append(tMap[0]).append(":").append(tMap[1]).append(",\n");
                }
            }
        }
        return content.toString();
    }
}
