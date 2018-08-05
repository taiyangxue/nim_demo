package com.netease.nim.demo.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/3/27.
 * 适配器的基类
 */

public abstract class CommentAdpter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mData;
    protected LayoutInflater layoutInflater;
    private int layoutId;
    public CommentAdpter(Context context, List<T> data,int layoutId) {
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
        mData = data;
        this.layoutId=layoutId;
    }

    public CommentAdpter() {
        super();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.getViewHolder(mContext,convertView,parent, layoutId,position);
        convert(viewHolder,getItem(position));
        return viewHolder.getConvertView();
    }

    protected abstract void convert(ViewHolder viewHolder, T item);

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
