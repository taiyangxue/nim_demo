package com.netease.nim.demo.home.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.loveplusplus.demo.image.ImagePagerActivity;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.NEVideoPlayerActivity;
import com.netease.nim.demo.common.entity.VideoRet;
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.home.activity.HudongActivity;
import com.netease.nim.uikit.common.ui.imageview.ImageViewEx;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/9.
 */
public class MyHomesAdapter extends BaseQuickAdapter<VideoRet.DataBean, BaseViewHolder> {
    private final static int COUNT_LIMIT = 10000;
    private BitmapUtils utils;
    private int course;
    private int type_id;
    public MyHomesAdapter(BitmapUtils utils, RecyclerView recyclerView, int course) {
        super(recyclerView, R.layout.video_item2, null);
        this.utils=utils;
        this.course=course;
    }

    public MyHomesAdapter(BitmapUtils utils, RecyclerView recyclerView) {
        super(recyclerView, R.layout.video_item2, null);
        this.utils=utils;
//        this.course=course;
//        this.type_id=type_id;
    }
    @Override
    protected void convert(BaseViewHolder holder, final VideoRet.DataBean video, final int position, boolean isScrolling) {
        // bg
//        holder.getConvertView().setBackgroundResource(R.drawable.list_item_bg_selecter);
        // cover
        ImageViewEx coverImage = holder.getView(R.id.cover_image);
        ImageViewEx coverImage_video = holder.getView(R.id.cover_image_video);
        RelativeLayout rl_video = holder.getView(R.id.rl_video);
        RelativeLayout rl_videotype = holder.getView(R.id.rl_videotype);
        ImageView iv_play = holder.getView(R.id.iv_play);
        ImageView iv_open = holder.getView(R.id.iv_open);
        final ImageView iv_shoucang = holder.getView(R.id.iv_shoucang);
        ImageView iv_hudong = holder.getView(R.id.iv_hudong);
        holder.setText(R.id.tv_title,video.getName());

        if(!TextUtils.isEmpty(video.getAnswer_text())||!TextUtils.isEmpty(video.getAnswer_image())){
            iv_open.setImageResource(R.drawable.yitiduojie_press);
        }
        if(video.getVid()==0){
            iv_play.setVisibility(View.GONE);
        }
        if(video.isIscomment()){
            iv_hudong.setImageResource(R.drawable.daan_press);
        }

        if(video.getPid()!=0){
            //文件夹,隐藏视频相关操作
            rl_video.setVisibility(View.GONE);
            coverImage_video.setVisibility(View.GONE);
            if(video.getCreatetime()==0){
                rl_videotype.setVisibility(View.GONE);
            }
        }else {
//            if(type_id==99){
//                iv_shoucang.setImageResource();
//            }
            rl_video.setVisibility(View.VISIBLE);
            rl_videotype.setVisibility(View.GONE);
            coverImage.setVisibility(View.GONE);
            coverImage_video.setVisibility(View.VISIBLE);
            if(TextUtils.isEmpty(video.getOrigUrl())){
               iv_play.setVisibility(View.GONE);
            }
            if(video.isIscollect()){
                iv_shoucang.setImageResource(R.drawable.ali_shoucang2);
            }else {
                iv_shoucang.setImageResource(R.drawable.ali_shoucang);
            }
            if(video.getSnapshotUrl_image().startsWith("http")){
                utils.display(coverImage_video,video.getSnapshotUrl_image());
            }else {
                utils.display(coverImage_video,ApiUtils.STATIC_HOST+video.getSnapshotUrl_image());
            }
        }

        holder.addOnClickListener(R.id.cover_image_video);
        holder.addOnClickListener(R.id.iv_open);
        holder.addOnClickListener(R.id.iv_play);
        holder.addOnClickListener(R.id.iv_shoucang);
        holder.addOnClickListener(R.id.iv_hudong);
        iv_shoucang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video.isIscollect()){
                    ApiUtils.getInstance().video_canclecollect(SharedPreferencesUtils.getInt(mContext, "account_id", 0)+"",
                            video.getId()+"",course, new ApiListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    MyUtils.showToast(mContext,"取消成功");
                                    iv_shoucang.setImageResource(R.drawable.ali_shoucang);
                                }

                                @Override
                                public void onFailed(String errorMsg) {
                                    MyUtils.showToast(mContext,errorMsg);
                                }
                            });
                }else {
                    ApiUtils.getInstance().video_collect(SharedPreferencesUtils.getInt(mContext, "account_id", 0)+"",
                            video.getId()+"",course, new ApiListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    MyUtils.showToast(mContext,"收藏成功");
                                    iv_shoucang.setImageResource(R.drawable.ali_shoucang2);
                                }

                                @Override
                                public void onFailed(String errorMsg) {
                                    MyUtils.showToast(mContext,errorMsg);
                                }
                            });
                }

            }
        });
        coverImage_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(video.getSnapshotUrl_image())) {
                    Intent intent = new Intent(mContext, ImagePagerActivity.class);
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    String imgurl="";
                    String ans_imgurl="";
                    if(video.getSnapshotUrl_image().startsWith("http")){
                        imgurl=video.getSnapshotUrl_image();
                    }else {
                        imgurl=ApiUtils.STATIC_HOST+video.getSnapshotUrl_image();
                    }

                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{imgurl});
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    if(!TextUtils.isEmpty(video.getAnswer_text())){
                        intent.putExtra(ImagePagerActivity.EXTRA_DAAN ,video.getAnswer_text());
                    }
                    if(!TextUtils.isEmpty(video.getAnswer_image())){
                        if(video.getAnswer_image().startsWith("http")){
                            ans_imgurl=video.getAnswer_image();
                        }else {
                            ans_imgurl=ApiUtils.STATIC_HOST+video.getAnswer_image();
                        }
                        intent.putExtra(ImagePagerActivity.EXTRA_DAAN_URL,ans_imgurl);
                    }
                    intent.putExtra(ImagePagerActivity.EXTRA_VIDEO_URL,video.getOrigUrl());
                    intent.putExtra(ImagePagerActivity.EXTRA_IS_VIDEO,true);
                    mContext.startActivity(intent);
                } else {
                    MyUtils.showToast(mContext, "该视频未上传视频封面");
                }

            }
        });
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NEVideoPlayerActivity.class);
                intent.putExtra("media_type", "videoondemand");
                intent.putExtra("decode_type", "software");
                intent.putExtra("videoPath", video.getOrigUrl());
                mContext.startActivity(intent);
            }
        });

        iv_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(video.getAnswer_text())){
                    showDaan(video.getAnswer_text());
                }else {
                    //展示答案
                    if(!TextUtils.isEmpty(video.getAnswer_image())){
                        String ans_imgurl="";
                        if(video.getAnswer_image().startsWith("http")){
                            ans_imgurl=video.getAnswer_image();
                        }else {
                            ans_imgurl=ApiUtils.STATIC_HOST+video.getAnswer_image();
                        }
                        Intent intent = new Intent(mContext, ImagePagerActivity.class);
                        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{ans_imgurl});
                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 1);
                        mContext.startActivity(intent);
                    }else {
                        MyUtils.showToast(mContext,"该视频未上传答案");
                    }
                }
            }
        });
        iv_hudong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,HudongActivity.class);
                intent.putExtra("video_id",video.getId());
                mContext.startActivity(intent);
            }
        });
    }
    public void showDaan(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dView = View.inflate(mContext, R.layout.dialog_daan1, null);
        final AlertDialog dialog = builder.create();
        final TextView tv_content = (TextView) dView.findViewById(R.id.tv_content);
        tv_content.setText(content);
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
    }
}

