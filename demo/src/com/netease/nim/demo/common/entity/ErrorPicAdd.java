package com.netease.nim.demo.common.entity;

/**
 * Created by sun on 2018/7/7.
 */
public class ErrorPicAdd {

    /**
     * code : 1
     * msg : 添加成功
     * time : 1530976393
     * data : {"user_id":"8","section_id":"275$pic_image=fsdfsf","pic_image":null,"createtime":1530976393,"updatetime":1530976393,"id":"2"}
     */

    private int code;
    private String msg;
    private String time;
    private ErrorPicRet.DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ErrorPicRet.DataBean getData() {
        return data;
    }

    public void setData(ErrorPicRet.DataBean data) {
        this.data = data;
    }

}
