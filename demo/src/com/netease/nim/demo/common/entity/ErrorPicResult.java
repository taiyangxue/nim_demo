package com.netease.nim.demo.common.entity;

import java.util.List;

/**
 * Created by sunjj on 2018/9/17.
 */
public class ErrorPicResult {

    /**
     * code : 1
     * msg : category
     * time : 1537153731
     * data : [{"id":6249,"pid":6240,"name":"测试一下","image":"","keywords":"sfdsd","description":"","createtime":1537153656,"updatetime":1537153656,"weigh":6249,"create_admin_id":15},{"id":6250,"pid":6240,"name":"继续测试2","image":"","keywords":"","description":"","createtime":1537153714,"updatetime":1537153714,"weigh":6250,"create_admin_id":15}]
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
         * id : 6249
         * pid : 6240
         * name : 测试一下
         * image :
         * keywords : sfdsd
         * description :
         * createtime : 1537153656
         * updatetime : 1537153656
         * weigh : 6249
         * create_admin_id : 15
         */
        private int id;
        private int section_id;
        private String pic_image;
        private int user_id;

        private int pid;
        private String name;
        private String image;
        private String keywords;
        private String description;
        private int createtime;
        private int updatetime;
        private int weigh;
        private int create_admin_id;

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

        public int getSection_id() {
            return section_id;
        }

        public void setSection_id(int section_id) {
            this.section_id = section_id;
        }

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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
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

        public int getCreate_admin_id() {
            return create_admin_id;
        }

        public void setCreate_admin_id(int create_admin_id) {
            this.create_admin_id = create_admin_id;
        }
    }
}
