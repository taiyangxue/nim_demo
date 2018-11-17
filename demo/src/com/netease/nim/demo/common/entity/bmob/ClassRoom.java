package com.netease.nim.demo.common.entity.bmob;

import java.io.Serializable;


/**
 * Created by Administrator on 2017/3/3.
 */

public class ClassRoom  implements Serializable{
    private String account;
    private String httpPullUrl;
    private String hlsPullUrl;
    private String rtmpPullUrl;
    private String imageUrl;
    private String name;
    private String pushUrl;
    private String cid;
    private String roomId;
    private String typeId;
    private boolean isOnline;
    private String roomPwd;

    public String getRoomPwd() {
        return roomPwd;
    }

    public void setRoomPwd(String roomPwd) {
        this.roomPwd = roomPwd;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getHttpPullUrl() {
        return httpPullUrl;
    }

    public void setHttpPullUrl(String httpPullUrl) {
        this.httpPullUrl = httpPullUrl;
    }

    public String getHlsPullUrl() {
        return hlsPullUrl;
    }

    public void setHlsPullUrl(String hlsPullUrl) {
        this.hlsPullUrl = hlsPullUrl;
    }

    public String getRtmpPullUrl() {
        return rtmpPullUrl;
    }

    public void setRtmpPullUrl(String rtmpPullUrl) {
        this.rtmpPullUrl = rtmpPullUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @Override
    public String toString() {
        return "ClassRoom{" +
                "account='" + account + '\'' +
                ", httpPullUrl='" + httpPullUrl + '\'' +
                ", hlsPullUrl='" + hlsPullUrl + '\'' +
                ", rtmpPullUrl='" + rtmpPullUrl + '\'' +
                ", name='" + name + '\'' +
                ", pushUrl='" + pushUrl + '\'' +
                ", cid='" + cid + '\'' +
                ", roomId='" + roomId + '\'' +
                ", typeId='" + typeId + '\'' +
                '}';
    }
}
