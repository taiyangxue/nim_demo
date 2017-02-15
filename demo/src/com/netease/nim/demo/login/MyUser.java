package com.netease.nim.demo.login;

import cn.bmob.v3.BmobUser;

public class MyUser extends BmobUser {

    private Boolean sex;
    private String nick;
    //年级4、5、6、7、8、9、年级
    // 11、12、13高一，高二，高三
    private int grade;

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
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