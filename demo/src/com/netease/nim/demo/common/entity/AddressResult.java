package com.netease.nim.demo.common.entity;

/**
 * Created by Administrator on 2017/2/28.
 */

public class AddressResult {

    /**
     * code : XXX
     * msg : XXX
     * ret : {"pushUrl":"XXX","httpPullUrl":"XXX","hlsPullUrl":"XXX","rtmpPullUrl":"XXX"}
     */

    private String code;
    private String msg;
    /**
     * pushUrl : XXX
     * httpPullUrl : XXX
     * hlsPullUrl : XXX
     * rtmpPullUrl : XXX
     */

    private RetBean ret;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public static class RetBean {
        private String pushUrl;
        private String httpPullUrl;
        private String hlsPullUrl;
        private String rtmpPullUrl;

        public String getPushUrl() {
            return pushUrl;
        }

        public void setPushUrl(String pushUrl) {
            this.pushUrl = pushUrl;
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
    }
}
