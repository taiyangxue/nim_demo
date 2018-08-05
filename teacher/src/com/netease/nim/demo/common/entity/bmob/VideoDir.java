package com.netease.nim.demo.common.entity.bmob;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Administrator on 2017/4/4.
 */

public class VideoDir extends BmobObject implements Serializable{
    private String name;
    private String creatAccount;
    private VideoDir parent;
    private boolean isDingYue;
    private BmobRelation dingYueUser;

    public BmobRelation getDingYueUser() {
        return dingYueUser;
    }

    public void setDingYueUser(BmobRelation dingYueUser) {
        this.dingYueUser = dingYueUser;
    }

    public boolean isDingYue() {
        return isDingYue;
    }

    public void setDingYue(boolean dingYue) {
        isDingYue = dingYue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatAccount() {
        return creatAccount;
    }

    public void setCreatAccount(String creatAccount) {
        this.creatAccount = creatAccount;
    }

    public VideoDir getParent() {
        return parent;
    }

    public void setParent(VideoDir parent) {
        this.parent = parent;
    }
}
