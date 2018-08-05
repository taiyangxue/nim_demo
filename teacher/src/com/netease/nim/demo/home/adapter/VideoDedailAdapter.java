package com.netease.nim.demo.home.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.loveplusplus.demo.image.ImagePagerActivity;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.Video;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.uikit.common.ui.imageview.ImageViewEx;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by huangjun on 2016/12/9.
 */
public class VideoDedailAdapter extends BaseQuickAdapter<Video, BaseViewHolder> {
    private final static int COUNT_LIMIT = 10000;
    private BitmapUtils utils;
    private boolean isOpen;
    private boolean isSubscribe;
    public VideoDedailAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.video_dir_item, null);
    }

    public VideoDedailAdapter(boolean isOpen, boolean isSubscribe, BitmapUtils utils, RecyclerView recyclerView) {
        super(recyclerView, R.layout.video_dir_item, null);
        this.utils = utils;
        this.isOpen=isOpen;
        this.isSubscribe=isSubscribe;
    }

    @Override
    protected void convert(BaseViewHolder holder, final Video video, final int position, boolean isScrolling) {
        // bg
        holder.getConvertView().setBackgroundResource(R.drawable.list_item_bg_selecter);
        // cover
        ImageViewEx coverImage = holder.getView(R.id.cover_image);
//        final ImageView iv_subscribe = holder.getView(R.id.iv_subscribe);
        final ImageView iv_open = holder.getView(R.id.iv_open);
        utils.display(coverImage, video.getSnapshotUrl());
        holder.setText(R.id.tv_name, video.getVideoName());
        holder.setText(R.id.tv_creat_time, video.getCreatedAt());
        iv_open.setVisibility(View.VISIBLE);
//        iv_subscribe.setVisibility(View.VISIBLE);
        holder.addOnClickListener(R.id.iv_open);
//        holder.addOnClickListener(R.id.iv_subscribe);
        holder.addOnClickListener(R.id.cover_image);
        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(video.getSnapshotUrl())){
                    Intent intent = new Intent(mContext, ImagePagerActivity.class);
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{video.getSnapshotUrl()});
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    mContext.startActivity(intent);
                }else {
                    MyUtils.showToast(mContext,"该视频未上传视频封面");
                }
            }
        });
        iv_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MyUtils.showToast(mContext,"open");
                video.setOpen(!video.isOpen());
                video.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            if(video.isOpen()){
                                MyUtils.showToast(mContext,"设置公开课");
                                iv_open.setImageResource(R.drawable.btn_star_on_focused_holo_dark);
                            }else {
                                MyUtils.showToast(mContext,"取消公开课");
                                if(isOpen){
                                    VideoDedailAdapter.this.remove(position);
                                }
                                iv_open.setImageResource(R.drawable.btn_star_off_normal_holo_dark);
                            }
                        }
                    }
                });
            }
        });
//        iv_subscribe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                video.setSubscribe(!video.isSubscribe());
//                video.update(new UpdateListener() {
//                    @Override
//                    public void done(BmobException e) {
//                        if(e==null){
//                            if(video.isSubscribe()){
//                                MyUtils.showToast(mContext,"设置订阅课");
//                                iv_subscribe.setImageResource(R.drawable.ic_menu_attachment_select);
//                            }else {
//                                MyUtils.showToast(mContext,"取消订阅课");
//                                if(isSubscribe){
//                                    VideoDedailAdapter.this.remove(position);
//                                }
//                                iv_subscribe.setImageResource(R.drawable.ic_menu_attachment);
//                            }
//                        }
//                    }
//                });
//            }
//        });
        if (video.isOpen()) {
            iv_open.setImageResource(R.drawable.btn_star_on_focused_holo_dark);
        } else {
            iv_open.setImageResource(R.drawable.btn_star_off_normal_holo_dark);
        }
//        if (video.isSubscribe()) {
//            iv_subscribe.setImageResource(R.drawable.ic_menu_attachment_select);
//        } else {
//            iv_subscribe.setImageResource(R.drawable.ic_menu_attachment);
//        }
    }
}

