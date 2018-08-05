package com.netease.nim.demo.common.entity;

/**
 * Created by sun on 2018/3/22.
 */

public class UserLoginBean {

    /**
     * code : 1
     * msg : 登录成功
     * time : 1521722358
     * data : {"userinfo":{"id":1,"group_id":1,"username":"admin","nickname":"admin","mobile":"13888888888","avatar":"/assets/img/avatar.png","score":5,"token":"5aeb837d-dd30-4c6a-bad2-c5871278a344"}}
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
         * userinfo : {"id":1,"group_id":1,"username":"admin","nickname":"admin","mobile":"13888888888","avatar":"/assets/img/avatar.png","score":5,"token":"5aeb837d-dd30-4c6a-bad2-c5871278a344"}
         */

        private UserinfoBean userinfo;

        public UserinfoBean getUserinfo() {
            return userinfo;
        }

        public void setUserinfo(UserinfoBean userinfo) {
            this.userinfo = userinfo;
        }

        public static class UserinfoBean {
            /**
             * id : 1
             * group_id : 1
             * username : admin
             * nickname : admin
             * mobile : 13888888888
             * avatar : /assets/img/avatar.png
             * score : 5
             * token : 5aeb837d-dd30-4c6a-bad2-c5871278a344
             */

            private int id;
            private int group_id;
            private String username;
            private String nickname;
            private String mobile;
            private String avatar;
            private int score;
            private String token;
            private String grade;

            public String getGrade() {
                return grade;
            }

            public void setGrade(String grade) {
                this.grade = grade;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getGroup_id() {
                return group_id;
            }

            public void setGroup_id(int group_id) {
                this.group_id = group_id;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public int getScore() {
                return score;
            }

            public void setScore(int score) {
                this.score = score;
            }

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }
        }
    }
}
