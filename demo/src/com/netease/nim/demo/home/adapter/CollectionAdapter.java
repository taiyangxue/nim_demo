package com.netease.nim.demo.home.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loveplusplus.demo.image.ImagePagerActivity;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.NEVideoPlayerActivity;
import com.netease.nim.demo.common.entity.bmob.Collection;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.uikit.common.ui.imageview.ImageViewEx;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 * Created by huangjun on 2016/12/9.
 */
public class CollectionAdapter extends BaseQuickAdapter<Collection, BaseViewHolder> {
    private final static int COUNT_LIMIT = 10000;
    public CollectionAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.collection_item, null);
    }

    @Override
    protected void convert(BaseViewHolder holder, final Collection collection, final int position, boolean isScrolling) {
        // bg
        holder.getConvertView().setBackgroundResource(R.drawable.list_item_bg_selecter);
        // cover
        ImageViewEx coverImage = holder.getView(R.id.cover_image);
        final ImageView iv_play = holder.getView(R.id.iv_play);
        final ImageView iv_open = holder.getView(R.id.iv_open);
//        utils.display(coverImage,room.getSnapshotUrl());
//        ChatRoomHelper.setCoverImage(room.getRoomId(), coverImage);
        // name
        coverImage.load(collection.getSnapshotUrl());
//        holder.setText(R.id.tv_creat_time,"收藏时间："+collection.getCreatedAt());
        holder.setText(R.id.tv_name,collection.getVideoName());
        holder.addOnClickListener(R.id.iv_open);
        holder.addOnClickListener(R.id.cover_image);
        holder.addOnClickListener(R.id.iv_play);
        holder.addOnLongClickListener(R.id.cover_image);
//        new ImagePagerActivity().setMyClick(new ImagePagerActivity.OnClickCallback<Integer>() {
//            @Override
//            public void myClick(Integer integer) {
//                switch (integer){
//                    case 1:
//                        MyUtils.showToast(mContext, "HHHHHHH");
//                        break;
//                }
//            }
//        });
        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(collection.getSnapshotUrl())) {
                    Intent intent = new Intent(mContext, ImagePagerActivity.class);
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{collection.getSnapshotUrl()});
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    if(!TextUtils.isEmpty(collection.getDescription())){
                        intent.putExtra(ImagePagerActivity.EXTRA_DAAN ,collection.getDescription());
                    }
                    if(collection.getAnswerUrl()!=null){
                        intent.putExtra(ImagePagerActivity.EXTRA_DAAN_URL,collection.getAnswerUrl());
                    }
                    intent.putExtra(ImagePagerActivity.EXTRA_VIDEO_URL,collection.getOrigUrl());
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
                intent.putExtra("videoPath", collection.getOrigUrl());
                mContext.startActivity(intent);
            }
        });
        iv_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(collection.getDescription())) {
                    showDaan(collection.getDescription());
                } else {
                    //展示答案
                    if (collection.getAnswerUrl() != null) {
                        Intent intent = new Intent(mContext, ImagePagerActivity.class);
                        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{collection.getAnswerUrl()});
                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 1);
                        mContext.startActivity(intent);
                    } else {
                        MyUtils.showToast(mContext, "该视频未上传答案");
                    }
                }
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

