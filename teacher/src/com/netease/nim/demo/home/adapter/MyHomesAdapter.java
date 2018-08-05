package com.netease.nim.demo.home.adapter;

import android.support.v7.widget.RecyclerView;

import com.lidroid.xutils.BitmapUtils;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.VideoListResult;
import com.netease.nim.uikit.common.ui.imageview.ImageViewEx;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/9.
 */
public class MyHomesAdapter extends BaseQuickAdapter<VideoListResult.RetBean.ListBean, BaseViewHolder> {
    private final static int COUNT_LIMIT = 10000;
    private BitmapUtils utils;
    public MyHomesAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.chat_room_item, null);
    }

    public MyHomesAdapter(BitmapUtils utils, RecyclerView recyclerView) {
        super(recyclerView, R.layout.chat_room_item, null);
        this.utils=utils;
    }
    @Override
    protected void convert(BaseViewHolder holder, VideoListResult.RetBean.ListBean room, int position, boolean isScrolling) {
        // bg
        holder.getConvertView().setBackgroundResource(R.drawable.list_item_bg_selecter);
        // cover
        ImageViewEx coverImage = holder.getView(R.id.cover_image);
        utils.display(coverImage,room.getSnapshotUrl());
//        ChatRoomHelper.setCoverImage(room.getRoomId(), coverImage);
        // name
        holder.setText(R.id.tv_name, room.getVideoName());
        // online count
//        TextView onlineCountText = holder.getView(R.id.tv_status);
        holder.setText(R.id.tv_status, room.getDescription());
    }
//        onlineCountText.setText(String.valueOf(room.getFilename()));
}

