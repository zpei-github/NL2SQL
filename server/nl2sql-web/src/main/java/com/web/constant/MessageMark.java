package com.web.constant;


public enum MessageMark {
    MM10001(10001, "数据库表结构信息和连接信息等"),
    MM10002(10002,"查询需求关键字提取要求"),
    MM10003(10003,"查询需求关键字提取结果"),
    MM10004(10004,"SQL生成要求"),
    MM10005(10005,"SQL生成结果"),
    MM10007(10007,""),
    MM10008(10008, ""),
    MM10000(10000, "异常消息"),;

    /**自定义状态码**/
    private final int code;
    /**自定义描述**/
    private final String mark;

    MessageMark(int code, String mark){
        this.code = code;
        this.mark = mark;
    }

    public int getCode() {
        return code;
    }

    public String getMark() {
        return mark;
    }

    @Override
    public String toString() {
        return this.code + " " + this.mark;
    }

}
