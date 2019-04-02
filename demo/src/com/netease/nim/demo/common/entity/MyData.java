package com.netease.nim.demo.common.entity;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by sunjj on 2019/4/2.
 */
public class MyData implements Serializable{
    public MyData(int pid,String title,int position){
        this.pid=pid;
        this.title=title;
        this.position=position;
    }
    public int pid;
    public int position;
    public String title;


    protected MyData(Parcel in) {
        pid = in.readInt();
        title = in.readString();
    }

}
