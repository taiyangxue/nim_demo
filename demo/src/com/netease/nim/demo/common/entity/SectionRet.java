package com.netease.nim.demo.common.entity;

import java.util.List;

/**
 * Created by sun on 2018/7/7.
 */
public class SectionRet {

    /**
     * code : 1
     * msg : success
     * time : 1530974213
     * data : [{"id":275,"pid":226,"name":"测试1","keywords":"","description":"","createtime":1530893678,"updatetime":1530893678,"weigh":275},{"id":276,"pid":226,"name":"测试2","keywords":"","description":"","createtime":1530893693,"updatetime":1530893693,"weigh":276}]
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
         * id : 275
         * pid : 226
         * name : 测试1
         * keywords :
         * description :
         * createtime : 1530893678
         * updatetime : 1530893678
         * weigh : 275
         */

        private int id;
        private int pid;
        private String name;
        private String keywords;
        private String description;
        private int createtime;
        private int updatetime;
        private int weigh;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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

        public int getWeigh() {
            return weigh;
        }

        public void setWeigh(int weigh) {
            this.weigh = weigh;
        }
    }
}
