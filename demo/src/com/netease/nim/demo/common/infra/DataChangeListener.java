package com.netease.nim.demo.common.infra;

/**
 * Created by sun on 2018/5/24.
 */
public abstract class DataChangeListener {
    public abstract void onDataSearchChange(String search);
    public abstract void onDataTimeChange(String time);
}
