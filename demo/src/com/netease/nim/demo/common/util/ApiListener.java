package com.netease.nim.demo.common.util;

/**
 * Created by sun on 2017/5/26.
 */

public abstract class ApiListener<T> {
    public abstract void onSuccess(T t);
    public abstract void onFailed(String errorMsg);
}
