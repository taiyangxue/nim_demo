package com.netease.nim.demo.common.entity.bmob;


/**
 * Created by Administrator on 2017/3/27.
 * 错题分类章节实体类
 */

public class Section {
    private String name;
    private String desc;
    private String grade;
    private String course;
    private String addAccount;

    public String getAddAccount() {
        return addAccount;
    }

    public void setAddAccount(String addAccount) {
        this.addAccount = addAccount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
