package com.netease.nim.demo.common.entity.bmob;

import cn.bmob.v3.BmobObject;

/**
 * Created by sunjj on 2017/10/20.
 */

public class Fankui extends BmobObject {
    private String videoName;
    private String msg;
    private Video video;

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }
}
