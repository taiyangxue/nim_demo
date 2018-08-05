package com.netease.nim.demo.common.entity;

/**
 * Created by Administrator on 2017/3/3.
 */

public class ChannelCreat {

    /**
     * httpPullUrl : http://flv2220e357.live.126.net/live/cbf2cfcb943b47f0be690e553a8d27b5.flv?netease=flv2220e357.live.126.net
     * hlsPullUrl : http://pullhls2220e357.live.126.net/live/cbf2cfcb943b47f0be690e553a8d27b5/playlist.m3u8
     * rtmpPullUrl : rtmp://v2220e357.live.126.net/live/cbf2cfcb943b47f0be690e553a8d27b5
     * name : 测试2
     * pushUrl : rtmp://p2220e357.live.126.net/live/cbf2cfcb943b47f0be690e553a8d27b5?wsSecret=3db2170807ba136722cb3552e8a19ccd&wsTime=1488536764
     * ctime : 1488536764536
     * cid : cbf2cfcb943b47f0be690e553a8d27b5
     */

    private RetBean ret;
    /**
     * ret : {"httpPullUrl":"http://flv2220e357.live.126.net/live/cbf2cfcb943b47f0be690e553a8d27b5.flv?netease=flv2220e357.live.126.net","hlsPullUrl":"http://pullhls2220e357.live.126.net/live/cbf2cfcb943b47f0be690e553a8d27b5/playlist.m3u8","rtmpPullUrl":"rtmp://v2220e357.live.126.net/live/cbf2cfcb943b47f0be690e553a8d27b5","name":"测试2","pushUrl":"rtmp://p2220e357.live.126.net/live/cbf2cfcb943b47f0be690e553a8d27b5?wsSecret=3db2170807ba136722cb3552e8a19ccd&wsTime=1488536764","ctime":1488536764536,"cid":"cbf2cfcb943b47f0be690e553a8d27b5"}
     * code : 200
     */

    private int code;

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public RetBean getRet() {
        return ret;
    }

    public void setRet(RetBean ret) {
        this.ret = ret;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class RetBean {
        private String httpPullUrl;
        private String hlsPullUrl;
        private String rtmpPullUrl;
        private String name;
        private String pushUrl;
        private long ctime;
        private String cid;

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

        public long getCtime() {
            return ctime;
        }

        public void setCtime(long ctime) {
            this.ctime = ctime;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }
    }
}
