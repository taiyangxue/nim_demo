package com.netease.nim.demo.common.entity.bmob;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by sunjj on 2017/5/12.
 */

public class UserRealtion extends BmobObject{
    private String account;
    private BmobRelation student;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public BmobRelation getStudent() {
        return student;
    }

    public void setStudent(BmobRelation student) {
        this.student = student;
    }
}