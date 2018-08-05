package com.netease.nim.demo.common.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sunjj on 2018/5/24.
 */
public class NodeRet {

    /**
     * code : 1
     * msg : success
     * time : 1527135353
     * data : [{"id":1,"type":"1","user_id":19,"title":"CAD","content":"妈妈早啊","createtime":1527135246,"updatetime":1527135246,"course_id":8,"type_text":"Type 1"}]
     */

    private int code;
    private String msg;
    private String time;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * id : 1
         * type : 1
         * user_id : 19
         * title : CAD
         * content : 妈妈早啊
         * createtime : 1527135246
         * updatetime : 1527135246
         * course_id : 8
         * type_text : Type 1
         */

        private int id;
        private String type;
        private int user_id;
        private String title;
        private String content;
        private String content_image;
        private int createtime;
        private int updatetime;
        private int course_id;
        private String type_text;

        public String getContent_image() {
            return content_image;
        }

        public void setContent_image(String content_image) {
            this.content_image = content_image;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getCreatetime() {
            return createtime;
        }

        public void setCreatetime(int createtime) {
            this.createtime = createtime;
        }

        public int getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(int updatetime) {
            this.updatetime = updatetime;
        }

        public int getCourse_id() {
            return course_id;
        }

        public void setCourse_id(int course_id) {
            this.course_id = course_id;
        }

        public String getType_text() {
            return type_text;
        }

        public void setType_text(String type_text) {
            this.type_text = type_text;
        }
    }
}
