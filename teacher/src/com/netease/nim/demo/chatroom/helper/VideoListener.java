package com.netease.nim.demo.chatroom.helper;

import java.util.Map;

/**
 * Created by hzxuwen on 2016/4/25.
 */
public interface VideoListener {
    void onVideoOn(String account);
    void onVideoOff(String account);
    void onTabChange(boolean notify);
    void onKickOutSuccess(String account);
    void onUserLeave(String account);
    void onReportSpeaker(Map<String, Integer> map);

    /**
     * 主持人同意观众申请发言，观众选择后确认
     */
    void onAcceptConfirm();
}
