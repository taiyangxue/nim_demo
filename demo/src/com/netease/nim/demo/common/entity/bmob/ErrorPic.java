package com.netease.nim.demo.common.entity.bmob;


/**
 * Created by Administrator on 2017/3/9.
 */

public class ErrorPic {
    //图片文件的存储地址
    private String picUrl;
    //所属用户
    private String account;
    //课程
    private String course;
    //章节
    private String section;
    private Section sectionId;

    public Section getSectionId() {
        return sectionId;
    }

    public void setSectionId(Section sectionId) {
        this.sectionId = sectionId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    @Override
    public String toString() {
        return "ErrorPic{" +
                "picUrl='" + picUrl + '\'' +
                ", account='" + account + '\'' +
                ", course='" + course + '\'' +
                ", section='" + section + '\'' +
                '}';
    }
}
