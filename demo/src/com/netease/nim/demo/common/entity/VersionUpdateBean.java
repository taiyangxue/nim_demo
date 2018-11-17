package com.netease.nim.demo.common.entity;

/**
 * Created by sunjj on 2018/4/3.
 */

public class VersionUpdateBean {

    /**
     * code : 1
     * msg : 请求成功
     * time : 1522716930
     * data : {"id":2,"version":"1.2","version_code":2,"upload_file":"/uploads/20180331/e223767a0f8ce21168a6cd076a602c5b.apk","description":"测试","createtime":1522509028}
     */

    private int code;
    private String msg;
    private String time;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 2
         * version : 1.2
         * version_code : 2
         * upload_file : /uploads/20180331/e223767a0f8ce21168a6cd076a602c5b.apk
         * description : 测试
         * createtime : 1522509028
         */

        private int id;
        private String version;
        private int version_code;
        private String upload_file;
        private String description;
        private int createtime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getVersion_code() {
            return version_code;
        }

        public void setVersion_code(int version_code) {
            this.version_code = version_code;
        }

        public String getUpload_file() {
            return upload_file;
        }

        public void setUpload_file(String upload_file) {
            this.upload_file = upload_file;
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
    }
}
