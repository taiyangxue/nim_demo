package com.netease.nim.demo.common.entity;

/**
 * Created by sun on 2018/9/16.
 */
public class AddVideocomment {

    /**
     * code : 1
     * msg : 提交成功
     * time : 1537107244
     * data : {"image":"12355","video_id":"23005","user_id":"10","createtime":1537107244,"updatetime":1537107244,"id":"2","user_name":"嘟"}
     */

    private int code;
    private String msg;
    private String time;
    private Videocomment.DataBean data;

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

    public Videocomment.DataBean getData() {
        return data;
    }

    public void setData(Videocomment.DataBean data) {
        this.data = data;
    }
}
