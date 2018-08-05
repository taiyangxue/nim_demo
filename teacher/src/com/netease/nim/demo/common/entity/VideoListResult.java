package com.netease.nim.demo.common.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/3/8.
 */

public class VideoListResult {

    /**
     * ret : {"pageSize":2,"totalRecords":28,"list":[{"typeName":"默认分类","createTime":1467007983618,"duration":0,"origUrl":"http://vodk32ywxdf.vod.126.net/vodk32ywxdf/02a32b58-39fa-4d04-aeca-d7defa7e8972.jpg","downloadOrigUrl":"http://vodk32ywxdf.nosdn.127.net/02a32b58-39fa-4d04-aeca-d7defa7e8972.jpg?NOSAccessKeyId=ab1856bb39044591939d7b94e1b8e5ee&Expires=1498558273&download=qwqwqw.jpg&Signature=0b5yMclktt/pDBQIZU8bLB6suouXLMfGZhqECFDp8+w=","status":40,"updateTime":1467007983618,"description":null,"snapshotUrl":null,"initialSize":6354,"videoName":"qwqwqw","typeId":38,"completeTime":null,"vid":39},{"typeName":"默认分类","createTime":1467007653656,"duration":0,"origUrl":"http://vodk32ywxdf.vod.126.net/vodk32ywxdf/dcffdc0d-0735-41e1-8d30-d1d19450137f.jpg","downloadOrigUrl":"http://vodk32ywxdf.nosdn.127.net/dcffdc0d-0735-41e1-8d30-d1d19450137f.jpg?NOSAccessKeyId=ab1856bb39044591939d7b94e1b8e5ee&Expires=1498558273&download=Chrysanthemum.jpg&Signature=oxa5/60xl8dmhtLoS21p97J+rnsRWFP12c30oA6Sh3o=","status":40,"updateTime":1467007653656,"description":null,"snapshotUrl":null,"initialSize":879394,"videoName":"Chrysanthemum","typeId":38,"completeTime":null,"vid":38}],"currentPage":1,"pageNum":14}
     * code : 200
     */

    private RetBean ret;
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
        /**
         * pageSize : 2
         * totalRecords : 28
         * list : [{"typeName":"默认分类","createTime":1467007983618,"duration":0,"origUrl":"http://vodk32ywxdf.vod.126.net/vodk32ywxdf/02a32b58-39fa-4d04-aeca-d7defa7e8972.jpg","downloadOrigUrl":"http://vodk32ywxdf.nosdn.127.net/02a32b58-39fa-4d04-aeca-d7defa7e8972.jpg?NOSAccessKeyId=ab1856bb39044591939d7b94e1b8e5ee&Expires=1498558273&download=qwqwqw.jpg&Signature=0b5yMclktt/pDBQIZU8bLB6suouXLMfGZhqECFDp8+w=","status":40,"updateTime":1467007983618,"description":null,"snapshotUrl":null,"initialSize":6354,"videoName":"qwqwqw","typeId":38,"completeTime":null,"vid":39},{"typeName":"默认分类","createTime":1467007653656,"duration":0,"origUrl":"http://vodk32ywxdf.vod.126.net/vodk32ywxdf/dcffdc0d-0735-41e1-8d30-d1d19450137f.jpg","downloadOrigUrl":"http://vodk32ywxdf.nosdn.127.net/dcffdc0d-0735-41e1-8d30-d1d19450137f.jpg?NOSAccessKeyId=ab1856bb39044591939d7b94e1b8e5ee&Expires=1498558273&download=Chrysanthemum.jpg&Signature=oxa5/60xl8dmhtLoS21p97J+rnsRWFP12c30oA6Sh3o=","status":40,"updateTime":1467007653656,"description":null,"snapshotUrl":null,"initialSize":879394,"videoName":"Chrysanthemum","typeId":38,"completeTime":null,"vid":38}]
         * currentPage : 1
         * pageNum : 14
         */

        private int pageSize;
        private int totalRecords;
        private int currentPage;
        private int pageNum;
        private List<ListBean> list;

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalRecords() {
            return totalRecords;
        }

        public void setTotalRecords(int totalRecords) {
            this.totalRecords = totalRecords;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * typeName : 默认分类
             * createTime : 1467007983618
             * duration : 0
             * origUrl : http://vodk32ywxdf.vod.126.net/vodk32ywxdf/02a32b58-39fa-4d04-aeca-d7defa7e8972.jpg
             * downloadOrigUrl : http://vodk32ywxdf.nosdn.127.net/02a32b58-39fa-4d04-aeca-d7defa7e8972.jpg?NOSAccessKeyId=ab1856bb39044591939d7b94e1b8e5ee&Expires=1498558273&download=qwqwqw.jpg&Signature=0b5yMclktt/pDBQIZU8bLB6suouXLMfGZhqECFDp8+w=
             * status : 40
             * updateTime : 1467007983618
             * description : null
             * snapshotUrl : null
             * initialSize : 6354
             * videoName : qwqwqw
             * typeId : 38
             * completeTime : null
             * vid : 39
             */

            private String typeName;
            private long createTime;
            private int duration;
            private String origUrl;
            private String downloadOrigUrl;
            private int status;
            private long updateTime;
            private String description;
            private String snapshotUrl;
            private int initialSize;
            private String videoName;
            private int typeId;
            private long  completeTime;
            private int vid;

            public String getTypeName() {
                return typeName;
            }

            public void setTypeName(String typeName) {
                this.typeName = typeName;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
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

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public long getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(long updateTime) {
                this.updateTime = updateTime;
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

            public int getInitialSize() {
                return initialSize;
            }

            public void setInitialSize(int initialSize) {
                this.initialSize = initialSize;
            }

            public String getVideoName() {
                return videoName;
            }

            public void setVideoName(String videoName) {
                this.videoName = videoName;
            }

            public int getTypeId() {
                return typeId;
            }

            public void setTypeId(int typeId) {
                this.typeId = typeId;
            }

            public long getCompleteTime() {
                return completeTime;
            }

            public void setCompleteTime(long completeTime) {
                this.completeTime = completeTime;
            }

            public int getVid() {
                return vid;
            }

            public void setVid(int vid) {
                this.vid = vid;
            }
        }
    }
}
