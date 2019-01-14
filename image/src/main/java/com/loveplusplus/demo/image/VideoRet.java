package com.loveplusplus.demo.image;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sunjj on 2018/5/22.
 */
public class VideoRet {

    /**
     * code : 1
     * msg : success
     * time : 1526978676
     * data : [{"id":31142,"bmob_id":"4da5048e0b","vid":94840530,"name":"11知识点2","answer_image":"","answer_text":"","downloadOrigUrl":"http://vodocz6ec70.nosdn.127.net/2a340cd9-2143-4556-985e-17b5b4f5f29e.mp4?download=11%E7%9F%A5%E8%AF%86%E7%82%B92.mp4","origUrl":"http://vodocz6ec70.vod.126.net/vodocz6ec70/2a340cd9-2143-4556-985e-17b5b4f5f29e.mp4","snapshotUrl_image":"http://vodocz6ec70.nosdn.127.net/2a340cd9-2143-4556-985e-17b5b4f5f29e/d0eb196d-83f1-403d-ad46-54aad42eaafd","createtime":1524111597,"updatetime":1524111597,"category_id":6105,"new_category_id":6104}]
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

    public static class DataBean implements Serializable{
        /**
         * id : 31142
         * bmob_id : 4da5048e0b
         * vid : 94840530
         * name : 11知识点2
         * answer_image :
         * answer_text :
         * downloadOrigUrl : http://vodocz6ec70.nosdn.127.net/2a340cd9-2143-4556-985e-17b5b4f5f29e.mp4?download=11%E7%9F%A5%E8%AF%86%E7%82%B92.mp4
         * origUrl : http://vodocz6ec70.vod.126.net/vodocz6ec70/2a340cd9-2143-4556-985e-17b5b4f5f29e.mp4
         * snapshotUrl_image : http://vodocz6ec70.nosdn.127.net/2a340cd9-2143-4556-985e-17b5b4f5f29e/d0eb196d-83f1-403d-ad46-54aad42eaafd
         * createtime : 1524111597
         * updatetime : 1524111597
         * category_id : 6105
         * new_category_id : 6104
         *
         */

        private int id;
        private String bmob_id;
        private int vid;
        private String name;
        private String answer_image;
        private String answer_text;
        private String downloadOrigUrl;
        private String origUrl;
        private String snapshotUrl_image;
        private int createtime;
        private int updatetime;
        private int category_id;
        private int new_category_id;
        private boolean iscollect;
        private boolean iscomment;
        /**
         * pid : 6182
         * image :
         * keywords : 1
         * description :
         * weigh : 6185
         * create_admin_id : 8
         */

        private int pid;
        private String image;
        private String keywords;
        private String description;
        private int weigh;
        private int create_admin_id;

        public boolean isIscomment() {
            return iscomment;
        }

        public void setIscomment(boolean iscomment) {
            this.iscomment = iscomment;
        }

        public boolean isIscollect() {
            return iscollect;
        }

        public void setIscollect(boolean iscollect) {
            this.iscollect = iscollect;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getBmob_id() {
            return bmob_id;
        }

        public void setBmob_id(String bmob_id) {
            this.bmob_id = bmob_id;
        }

        public int getVid() {
            return vid;
        }

        public void setVid(int vid) {
            this.vid = vid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAnswer_image() {
            return answer_image;
        }

        public void setAnswer_image(String answer_image) {
            this.answer_image = answer_image;
        }

        public String getAnswer_text() {
            return answer_text;
        }

        public void setAnswer_text(String answer_text) {
            this.answer_text = answer_text;
        }

        public String getDownloadOrigUrl() {
            return downloadOrigUrl;
        }

        public void setDownloadOrigUrl(String downloadOrigUrl) {
            this.downloadOrigUrl = downloadOrigUrl;
        }

        public String getOrigUrl() {
            return origUrl;
        }

        public void setOrigUrl(String origUrl) {
            this.origUrl = origUrl;
        }

        public String getSnapshotUrl_image() {
            return snapshotUrl_image;
        }

        public void setSnapshotUrl_image(String snapshotUrl_image) {
            this.snapshotUrl_image = snapshotUrl_image;
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

        public int getCategory_id() {
            return category_id;
        }

        public void setCategory_id(int category_id) {
            this.category_id = category_id;
        }

        public int getNew_category_id() {
            return new_category_id;
        }

        public void setNew_category_id(int new_category_id) {
            this.new_category_id = new_category_id;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
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
