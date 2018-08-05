package com.netease.nim.demo.common.entity;

import java.util.List;

/**
 * Created by sun on 2018/7/7.
 */
public class ErrorPicRet {

    /**
     * code : 1
     * msg : success
     * time : 1530974998
     * data : [{"id":1,"section_id":275,"pic_image":"","user_id":8,"createtime":0,"updatetime":0}]
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

    public static class DataBean {
        /**
         * id : 1
         * section_id : 275
         * pic_image :
         * user_id : 8
         * createtime : 0
         * updatetime : 0
         */

        private int id;
        private int section_id;
        private String pic_image;
        private int user_id;
        private int createtime;
        private int updatetime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSection_id() {
            return section_id;
        }

        public void setSection_id(int section_id) {
            this.section_id = section_id;
        }

        public String getPic_image() {
            return pic_image;
        }

        public void setPic_image(String pic_image) {
            this.pic_image = pic_image;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
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
    }
}
