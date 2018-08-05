package com.netease.nim.demo.common.entity;

/**
 * Created by Administrator on 2017/3/17.
 * 创建视频分类返回结果实体类
 */

public class CreatTypeResult {

    /**
     * typeId : 1
     */

    private RetBean ret;
    /**
     * ret : {"typeId":1}
     * code : 200
    */

    private int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg;

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
        private int typeId;

        public int getTypeId() {
            return typeId;
        }

        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }
    }
}
