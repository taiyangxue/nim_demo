package com.netease.nim.demo.common.entity;

/**
 * Created by Administrator on 2017/2/28.
 */

public class ChatroomCreateResult {

    /**
     * roomid : 66
     * valid : true
     * announcement : null
     * name : mychatroom
     * broadcasturl : xxxxxx
     * ext :
     * creator : zhangsan
     */

    private ChatroomBean chatroom;
    /**
     * chatroom : {"roomid":66,"valid":true,"announcement":null,"name":"mychatroom","broadcasturl":"xxxxxx","ext":"","creator":"zhangsan"}
     * code : 200
     */

    private int code;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ChatroomBean getChatroom() {
        return chatroom;
    }

    public void setChatroom(ChatroomBean chatroom) {
        this.chatroom = chatroom;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class ChatroomBean {
        private int roomid;
        private boolean valid;
        private Object announcement;
        private String name;
        private String broadcasturl;
        private String ext;
        private String creator;

        public int getRoomid() {
            return roomid;
        }

        public void setRoomid(int roomid) {
            this.roomid = roomid;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public Object getAnnouncement() {
            return announcement;
        }

        public void setAnnouncement(Object announcement) {
            this.announcement = announcement;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBroadcasturl() {
            return broadcasturl;
        }

        public void setBroadcasturl(String broadcasturl) {
            this.broadcasturl = broadcasturl;
        }

        public String getExt() {
            return ext;
        }

        public void setExt(String ext) {
            this.ext = ext;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }
    }
}
