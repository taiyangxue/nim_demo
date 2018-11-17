package com.netease.nim.demo.common.entity.bmob;


/**
 * Created by Administrator on 2017/3/17.
 */

public class Video  {
    private String origUrl;
    private String downloadOrigUrl;
    private String description;
    private String snapshotUrl;
    private String videoName;
    private boolean isOpen;
    private boolean isSubscribe;
    private String typeId;
    private VideoDir videoDir;
    private VideoDir videoDirTea;



    public VideoDir getVideoDirTea() {
        return videoDirTea;
    }

    public void setVideoDirTea(VideoDir videoDirTea) {
        this.videoDirTea = videoDirTea;
    }



    public boolean isSubscribe() {
        return isSubscribe;
    }

    public void setSubscribe(boolean subscribe) {
        isSubscribe = subscribe;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public VideoDir getVideoDir() {
        return videoDir;
    }

    public void setVideoDir(VideoDir videoDir) {
        this.videoDir = videoDir;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

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


}
