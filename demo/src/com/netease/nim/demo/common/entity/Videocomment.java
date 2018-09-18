package com.netease.nim.demo.common.entity;

import java.util.List;

/**
 * Created by sun on 2018/9/16.
 */
public class Videocomment {

    /**
     * code : 1
     * msg : 获取成功
     * time : 1537104372
     * data : [{"id":1,"content":"123","image":"/uploads/20180713/978cbe0947b8af4688b96ba029733fed.png","video_id":23005,"user_id":24,"createtime":1537104311,"updatetime":1537104311,"user_name":"jay"}]
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
         * content : 123
         * image : /uploads/20180713/978cbe0947b8af4688b96ba029733fed.png
         * video_id : 23005
         * user_id : 24
         * createtime : 1537104311
         * updatetime : 1537104311
         * user_name : jay
         */

        private int id;
        private String content;
        private String image;
        private int video_id;
        private int user_id;
        private int createtime;
        private int updatetime;
        private String user_name;
        private String user_mobile;
        private int upcount;

        public int getUpcount() {
            return upcount;
        }

        public void setUpcount(int upcount) {
            this.upcount = upcount;
        }

        public String getUser_mobile() {
            return user_mobile;
        }

        public void setUser_mobile(String user_mobile) {
            this.user_mobile = user_mobile;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getVideo_id() {
            return video_id;
        }

        public void setVideo_id(int video_id) {
            this.video_id = video_id;
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

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }
    }
}
