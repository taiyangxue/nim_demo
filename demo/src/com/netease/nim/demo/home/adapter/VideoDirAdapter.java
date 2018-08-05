package com.netease.nim.demo.home.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.loveplusplus.demo.image.ImagePagerActivity;
import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.activity.NEVideoPlayerActivity;
import com.netease.nim.demo.common.entity.bmob.Video;
import com.netease.nim.demo.common.entity.bmob.VideoDir;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.uikit.common.ui.imageview.ImageViewEx;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

import java.io.File;

/**
 * Created by huangjun on 2016/12/9.
 */
public class VideoDirAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {
    private final static int COUNT_LIMIT = 10000;
    private BitmapUtils utils;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private String fileName;

    public VideoDirAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.video_dir_item, null);
    }

    public VideoDirAdapter(BitmapUtils utils, RecyclerView recyclerView) {
        super(recyclerView, R.layout.video_dir_item, null);
        this.utils = utils;
    }

    @Override
    protected void convert(BaseViewHolder holder, final Object videoDir, final int position, boolean isScrolling) {
        // bg
        holder.getConvertView().setBackgroundResource(R.drawable.list_item_bg_selecter);
        // cover
        ImageViewEx coverImage = holder.getView(R.id.cover_image);
        final ImageView iv_play = holder.getView(R.id.iv_play);
        final ImageView iv_open = holder.getView(R.id.iv_open);
        progressBar = holder.getView(R.id.progressBar);
//        utils.display(coverImage,room.getSnapshotUrl());
//        ChatRoomHelper.setCoverImage(room.getRoomId(), coverImage);
        // name
        holder.addOnClickListener(R.id.iv_open);
        holder.addOnClickListener(R.id.cover_image);
        holder.addOnClickListener(R.id.iv_play);
        holder.addOnLongClickListener(R.id.cover_image);
        if (videoDir instanceof VideoDir) {
            holder.setText(R.id.tv_name, ((VideoDir) videoDir).getName());
            holder.setText(R.id.tv_creat_time, ((VideoDir) videoDir).getCreatedAt());
            coverImage.setImageResource(R.drawable.directory3);
            iv_open.setVisibility(View.GONE);
            iv_play.setVisibility(View.GONE);
        } else if (videoDir instanceof Video) {
            if (!TextUtils.isEmpty(((Video) videoDir).getSnapshotUrl())) {
                coverImage.load(((Video) videoDir).getSnapshotUrl());
            } else {
                coverImage.setImageResource(R.drawable.video_default);
//                coverImage.setBackgroundResource(R.drawable.video_default);
            }
            holder.setText(R.id.tv_name, ((Video) videoDir).getVideoName());
            holder.setText(R.id.tv_creat_time, ((Video) videoDir).getCreatedAt());
            iv_open.setVisibility(View.VISIBLE);
            iv_play.setVisibility(View.VISIBLE);
            coverImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(((Video) videoDir).getSnapshotUrl())) {
                        Intent intent = new Intent(mContext, ImagePagerActivity.class);
                        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{((Video) videoDir).getSnapshotUrl()});
                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                        if(!TextUtils.isEmpty(((Video) videoDir).getDescription())){
                            intent.putExtra(ImagePagerActivity.EXTRA_DAAN ,((Video) videoDir).getDescription());
                        }
                        if(((Video) videoDir).getAnswer()!=null){
                            intent.putExtra(ImagePagerActivity.EXTRA_DAAN_URL,((Video) videoDir).getAnswer().getUrl());
                        }
                        intent.putExtra(ImagePagerActivity.EXTRA_VIDEO_URL,((Video) videoDir).getOrigUrl());
                        intent.putExtra(ImagePagerActivity.EXTRA_IS_VIDEO,true);
                        mContext.startActivity(intent);
                    } else {
                        MyUtils.showToast(mContext, "该视频未上传视频封面");
                    }

                }
            });
            coverImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!TextUtils.isEmpty(((Video) videoDir).getSnapshotUrl())) {
//                        MyUtils.showToast(mContext, "下载");
                        showNormalDialog(((Video) videoDir).getSnapshotUrl());
                    } else {
                        MyUtils.showToast(mContext, "该视频未上传视频封面");
                    }
                    return true;
                }
            });
            iv_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NEVideoPlayerActivity.class);
                    intent.putExtra("media_type", "videoondemand");
                    intent.putExtra("decode_type", "software");
                    intent.putExtra("videoPath", ((Video) videoDir).getOrigUrl());
                    mContext.startActivity(intent);
                }
            });
            iv_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!TextUtils.isEmpty(((Video) videoDir).getDescription())){
                        showDaan(((Video) videoDir).getDescription());
                    }else {
                        //展示答案
                        if(((Video) videoDir).getAnswer()!=null&&((Video) videoDir).getAnswer().getUrl()!=null){
                            Intent intent = new Intent(mContext, ImagePagerActivity.class);
                            // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{((Video) videoDir).getAnswer().getUrl()});
                            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 1);
                            mContext.startActivity(intent);
                        }else {
                            MyUtils.showToast(mContext,"该视频未上传答案");
                        }
                    }
                }
            });
//            if (((Video) videoDir).isSubscribe()) {
//                iv_subscribe.setImageResource(R.drawable.ic_menu_attachment_select);
//            } else {
//                iv_subscribe.setImageResource(R.drawable.ic_menu_attachment);
//            }
        }
        // online count
//        TextView onlineCountText = holder.getView(R.id.tv_status);
    }

    private void downLoad(final String snapshotUrl) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            HttpUtils http = new HttpUtils();
            if(snapshotUrl.substring(snapshotUrl.lastIndexOf("/") + 1).contains(".jpg")){
                fileName=snapshotUrl.substring(snapshotUrl.lastIndexOf("/") + 1);
            }else {
                fileName=snapshotUrl.substring(snapshotUrl.lastIndexOf("/") + 1)+".jpg";
            }
            http.download(snapshotUrl, "/sdcard/yidu/" + System.currentTimeMillis() + fileName, true, true,
                    new RequestCallBack<File>() {

                        @Override
                        public void onFailure(HttpException arg0,
                                              String arg1) {
                            Log.e(TAG, snapshotUrl + ">>>>>>" + arg1);
                            Toast.makeText(mContext,
                                    "下载失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(ResponseInfo<File> file) {
                            Log.e(TAG,"下载完成");
                            Toast.makeText(mContext, "下载完成", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLoading(long total, long current,
                                              boolean isUploading) {
                            super.onLoading(total, current, isUploading);
                            //进度条效果
                            System.out.println(isUploading);
                            //进度条效果
//                            progressBar.setVisibility(View.VISIBLE);
//                            progressBar.setMax((int) total);
//                            progressBar.setProgress((int) current);
//                            if(current==0){
//                                showProgressDialog(total);
//                            }
//                            showProgressDialog(total);
//                            progressDialog.setProgress((int) current);
                            if (total == current) {
//                                progressDialog.cancel();
                                Toast.makeText(mContext, "下载完成", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
//                            Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(mContext, "未检测到sd卡", Toast.LENGTH_SHORT);
        }
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

    //        onlineCountText.setText(String.valueOf(room.getFilename()));
    private void showProgressDialog(long total) {
    /* @setProgress 设置初始进度
     * @setProgressStyle 设置样式（水平进度条）
     * @setMax 设置进度最大值
     */
        progressDialog = new ProgressDialog(mContext);
//        progressDialog.setProgress(0);
        progressDialog.setTitle("下载文件");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax((int) total);
        progressDialog.show();
    /* 模拟进度增加的过程
     * 新开一个线程，每个100ms，进度增加1
     */
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int progress = 0;
//                while (progress < MAX_PROGRESS) {
//                    try {
//                        Thread.sleep(100);
//                        progress++;
//                        progressDialog.setProgress(progress);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                // 进度达到最大值后，窗口消失
//                progressDialog.cancel();
//            }
//        }).start();
    }
    private void showNormalDialog(final String snapshotUrl){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
//        normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setTitle("下载文件");
        normalDialog.setMessage("确定要下载该文件?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       //...To-do
                        downLoad(snapshotUrl);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }
}

