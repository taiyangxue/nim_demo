package com.netease.nim.demo.common.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/3/8.
 */

public class TypeListResult {

    /**
     * pageSize : 2
     * totalRecords : 4
     * list : [{"typeName":"默认分类","createTime":1464229521940,"isDel":0,"desc":"默认分类","number":47,"typeId":38},{"typeName":"直播录制","createTime":1464229521940,"isDel":0,"desc":"直播录制","number":1,"typeId":39}]
     * currentPage : 1
     * pageNum : 2
     */

    private RetBean ret;
    /**
     * ret : {"pageSize":2,"totalRecords":4,"list":[{"typeName":"默认分类","createTime":1464229521940,"isDel":0,"desc":"默认分类","number":47,"typeId":38},{"typeName":"直播录制","createTime":1464229521940,"isDel":0,"desc":"直播录制","number":1,"typeId":39}],"currentPage":1,"pageNum":2}
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
        private int pageSize;
        private int totalRecords;
        private int currentPage;
        private int pageNum;
        /**
         * typeName : 默认分类
         * createTime : 1464229521940
         * isDel : 0
         * desc : 默认分类
         * number : 47
         * typeId : 38
         */

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
            private String typeName;
            private long createTime;
            private int isDel;
            private String desc;
            private int number;
            private int typeId;

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

            public int getIsDel() {
                return isDel;
            }

            public void setIsDel(int isDel) {
                this.isDel = isDel;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public int getNumber() {
                return number;
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public int getTypeId() {
                return typeId;
            }

            public void setTypeId(int typeId) {
                this.typeId = typeId;
            }
        }
    }
}
