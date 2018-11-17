package com.netease.nim.demo.login;

//import cn.bmob.v3.BmobUser;

public class MyUser {

    private Boolean sex;
    private String nick;
    //年级4、5、6、7、8、9、年级
    // 11、12、13高一，高二，高三
    private String grade;
    private int userType;
    private boolean isOnline;
    private boolean isFree;

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    //专属视频分类id
    private String typeId;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public boolean getSex() {
        return this.sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getNick() {
        return this.nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

}