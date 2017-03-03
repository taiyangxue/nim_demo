package com.netease.nim.demo.common.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/2/28.
 * 获取直播间列表的返回实体
 */

public class ChannelListResult {

    /**
     * pnum : 1
     * list : [{"needRecord":0,"uid":69517,"duration":120,"status":0,"name":"netease_vcloud","filename":"netease_vcloud","format":1,"type":0,"ctime":1488253247038,"cid":"e1f3a464831c45b6bb3dd18d6a762993","recordStatus":null},{"needRecord":0,"uid":69517,"duration":120,"status":0,"name":"测试","filename":"测试","format":1,"type":0,"ctime":1488253080868,"cid":"69e39bd0c4d74ccba75fa9801c2d9f1a","recordStatus":null}]
     * totalRecords : 2
     * totalPnum : 1
     * records : 10
     */

    private RetBean ret;
    /**
     * ret : {"pnum":1,"list":[{"needRecord":0,"uid":69517,"duration":120,"status":0,"name":"netease_vcloud","filename":"netease_vcloud","format":1,"type":0,"ctime":1488253247038,"cid":"e1f3a464831c45b6bb3dd18d6a762993","recordStatus":null},{"needRecord":0,"uid":69517,"duration":120,"status":0,"name":"测试","filename":"测试","format":1,"type":0,"ctime":1488253080868,"cid":"69e39bd0c4d74ccba75fa9801c2d9f1a","recordStatus":null}],"totalRecords":2,"totalPnum":1,"records":10}
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
        private int pnum;
        private int totalRecords;
        private int totalPnum;
        private int records;
        /**
         * needRecord : 0
         * uid : 69517
         * duration : 120
         * status : 0
         * name : netease_vcloud
         * filename : netease_vcloud
         * format : 1
         * type : 0
         * ctime : 1488253247038
         * cid : e1f3a464831c45b6bb3dd18d6a762993
         * recordStatus : null
         */

        private List<ListBean> list;

        public int getPnum() {
            return pnum;
        }

        public void setPnum(int pnum) {
            this.pnum = pnum;
        }

        public int getTotalRecords() {
            return totalRecords;
        }

        public void setTotalRecords(int totalRecords) {
            this.totalRecords = totalRecords;
        }

        public int getTotalPnum() {
            return totalPnum;
        }

        public void setTotalPnum(int totalPnum) {
            this.totalPnum = totalPnum;
        }

        public int getRecords() {
            return records;
        }

        public void setRecords(int records) {
            this.records = records;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            private int needRecord;
            private int uid;
            private int duration;
            private int status;
            private String name;
            private String filename;
            private int format;
            private int type;
            private long ctime;
            private String cid;
            private Object recordStatus;

            public int getNeedRecord() {
                return needRecord;
            }

            public void setNeedRecord(int needRecord) {
                this.needRecord = needRecord;
            }

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getFilename() {
                return filename;
            }

            public void setFilename(String filename) {
                this.filename = filename;
            }

            public int getFormat() {
                return format;
            }

            public void setFormat(int format) {
                this.format = format;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
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

            public Object getRecordStatus() {
                return recordStatus;
            }

            public void setRecordStatus(Object recordStatus) {
                this.recordStatus = recordStatus;
            }
        }
    }
}
