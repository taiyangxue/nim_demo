package com.netease.nim.demo.common.entity.bmob;

import cn.bmob.v3.BmobObject;

/**
 * Created by sunjj on 2017/8/17.
 */

public class Collection extends BmobObject{
    private String origUrl;
    private String downloadOrigUrl;
    private String description;
    private String snapshotUrl;
    private String videoName;
    private String answerUrl;
    private String user;

    public String getOrigUrl() {
        return origUrl;
    }

    public void setOrigUrl(String origUrl) {
        this.origUrl = origUrl;
    }

    public String getDownloadOrigUrl() {
        return downloadOrigUrl;
    }

    public void setDownloadOrigUrl(String downloadOrigUrl) {
        this.downloadOrigUrl = downloadOrigUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSnapshotUrl() {
        return snapshotUrl;
    }

    public void setSnapshotUrl(String snapshotUrl) {
        this.snapshotUrl = snapshotUrl;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getAnswerUrl() {
        return answerUrl;
    }

    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
