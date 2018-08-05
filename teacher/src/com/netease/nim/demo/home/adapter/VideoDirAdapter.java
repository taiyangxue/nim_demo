package com.netease.nim.demo.home.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.loveplusplus.demo.image.ImagePagerActivity;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.Video;
import com.netease.nim.demo.common.entity.bmob.VideoDir;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.home.activity.VideoDirActivity;
import com.netease.nim.demo.login.MyUser;
import com.netease.nim.uikit.common.media.picker.PickImageHelper;
import com.netease.nim.uikit.common.ui.imageview.ImageViewEx;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.common.util.storage.StorageType;
import com.netease.nim.uikit.common.util.storage.StorageUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static com.netease.nim.uikit.session.constant.RequestCode.PICK_IMAGE;


/**
 * Created by huangjun on 2016/12/9.
 */
public class VideoDirAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {
    private final static int COUNT_LIMIT = 10000;
    private BitmapUtils utils;

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
        final ImageView iv_subscribe = holder.getView(R.id.iv_subscribe);
        final ImageView iv_open = holder.getView(R.id.iv_open);
//        utils.display(coverImage,room.getSnapshotUrl());
//        ChatRoomHelper.setCoverImage(room.getRoomId(), coverImage);
        // name
        holder.addOnClickListener(R.id.iv_open);
        holder.addOnClickListener(R.id.iv_subscribe);
        holder.addOnClickListener(R.id.cover_image);
        holder.addOnLongClickListener(R.id.iv_open);
        if (videoDir instanceof VideoDir) {
            holder.setText(R.id.tv_name, ((VideoDir) videoDir).getName());
            holder.setText(R.id.tv_creat_time, ((VideoDir) videoDir).getCreatedAt());
            coverImage.setImageResource(R.drawable.directory3);
            if (((VideoDir) videoDir).getParent() == null) {
                if (((VideoDir) videoDir).isDingYue()) {
                    iv_open.setVisibility(View.VISIBLE);
                    iv_open.setImageResource(R.drawable.ic_menu_attachment_select);
                } else {
                    iv_open.setVisibility(View.VISIBLE);
                    iv_open.setImageResource(R.drawable.ic_menu_attachment);
                }
            } else {

            }
            if (((VideoDir) videoDir).getCreatAccount().equals("88888888")) {
                iv_open.setVisibility(View.VISIBLE);
                iv_open.setImageResource(R.drawable.btn_star_on_focused_holo_dark);
            } else if (((VideoDir) videoDir).getCreatAccount().equals(DemoCache.getAccount())) {
                iv_open.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                MyUtils.showToast(mContext,"open");
                        ((VideoDir) videoDir).setDingYue(!((VideoDir) videoDir).isDingYue());
                        ((VideoDir) videoDir).update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    if (((VideoDir) videoDir).isDingYue()) {
                                        MyUtils.showToast(mContext, "设置订阅文件夹");
                                        iv_open.setImageResource(R.drawable.ic_menu_attachment_select);
                                    } else {
                                        MyUtils.showToast(mContext, "取消订阅文件夹");
                                        iv_open.setImageResource(R.drawable.ic_menu_attachment);
                                    }
                                }
                                remove(position);
                            }
                        });
                    }
                });
            } else {
                iv_open.setVisibility(View.INVISIBLE);
            }
//            iv_subscribe.setVisibility(View.GONE);
        } else if (videoDir instanceof Video) {
            if (!TextUtils.isEmpty(((Video) videoDir).getSnapshotUrl())) {
                coverImage.load(((Video) videoDir).getSnapshotUrl());
            } else {
                coverImage.setImageResource(R.drawable.video_default);
            }
            holder.setText(R.id.tv_name, ((Video) videoDir).getVideoName());
            holder.setText(R.id.tv_creat_time, ((Video) videoDir).getCreatedAt());
            MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
            if (userInfo.getUserType() == 0) {
                iv_open.setVisibility(View.VISIBLE);
                iv_subscribe.setVisibility(View.VISIBLE);
                iv_subscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            showDaan(((Video) videoDir));
                    }
                });
                if (((Video) videoDir).getAnswer() != null) {
                    iv_open.setImageResource(R.drawable.icon_daan);
                    iv_open.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //展示答案
                            Intent intent = new Intent(mContext, ImagePagerActivity.class);
                            // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{((Video) videoDir).getAnswer().getUrl()});
                            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 1);
                            mContext.startActivity(intent);
                        }
                    });
                    iv_open.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            ((VideoDirActivity) mContext).setCurrent_videoDir(videoDir);
                            showSelector(R.string.app_name, PICK_IMAGE, multiSelect, tempFile());
                            return true;
                        }
                    });
                } else {
                    iv_open.setImageResource(R.drawable.icon_update);
                    iv_open.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //上传图片
                            ((VideoDirActivity) mContext).setCurrent_videoDir(videoDir);
                            showSelector(R.string.app_name, PICK_IMAGE, multiSelect, tempFile());
                        }
                    });
                }
            } else {
                iv_open.setVisibility(View.INVISIBLE);
            }
//            iv_subscribe.setVisibility(View.VISIBLE);
            coverImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(((Video) videoDir).getSnapshotUrl())) {
                        Intent intent = new Intent(mContext, ImagePagerActivity.class);
                        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{((Video) videoDir).getSnapshotUrl()});
                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                        mContext.startActivity(intent);
                    } else {
                        MyUtils.showToast(mContext, "该视频未上传视频封面");
                    }
                }
            });

//            iv_subscribe.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ((Video) videoDir).setSubscribe(!((Video) videoDir).isSubscribe());
//                    ((Video) videoDir).update(new UpdateListener() {
//                        @Override
//                        public void done(BmobException e) {
//                            if (e == null) {
//                                if (((Video) videoDir).isSubscribe()) {
//                                    MyUtils.showToast(mContext, "设置订阅课");
//                                    iv_subscribe.setImageResource(R.drawable.ic_menu_attachment_select);
//                                } else {
//                                    MyUtils.showToast(mContext, "取消订阅课");
//                                    iv_subscribe.setImageResource(ic_menu_attachment);
//                                }
//                            }
//                        }
//                    });
//                }
//            });
//            if (((Video) ((Video) videoDir)).isOpen()) {
//                iv_open.setImageResource(R.drawable.btn_star_on_focused_holo_dark);
//            } else {
//                iv_open.setImageResource(R.drawable.btn_star_off_normal_holo_dark);
//            }
//            if (((Video) videoDir).isSubscribe()) {
//                iv_subscribe.setImageResource(R.drawable.ic_menu_attachment_select);
//            } else {
//                iv_subscribe.setImageResource(ic_menu_attachment);
//            }
        }
        // online count
//        TextView onlineCountText = holder.getView(R.id.tv_status);
    }

    //        onlineCountText.setText(String.valueOf(room.getFilename()));
    private static final int PICK_IMAGE_COUNT = 9;
    private static final int PORTRAIT_IMAGE_WIDTH = 720;

    public static final String MIME_JPEG = "image/jpeg";
    public static final String JPG = ".jpg";

    private boolean multiSelect = true;
    private boolean crop = false;

    private String tempFile() {
        String filename = StringUtil.get32UUID() + JPG;
        return StorageUtil.getWritePath(filename, StorageType.TYPE_TEMP);
    }

    /**
     * 打开图片选择器
     */
    private void showSelector(int titleId, final int requestCode, final boolean multiSelect, final String outPath) {
        PickImageHelper.PickImageOption option = new PickImageHelper.PickImageOption();
        option.titleResId = titleId;
        option.multiSelect = multiSelect;
        option.multiSelectMaxCount = PICK_IMAGE_COUNT;
        option.crop = crop;
        option.cropOutputImageWidth = PORTRAIT_IMAGE_WIDTH;
        option.cropOutputImageHeight = PORTRAIT_IMAGE_WIDTH;
        option.outputPath = outPath;
        PickImageHelper.pickImage(mContext, requestCode, option);
    }
    public void showDaan(final Video video) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dView = View.inflate(mContext, R.layout.dialog_daan1, null);
        final AlertDialog dialog = builder.create();
        final EditText tv_content = (EditText) dView.findViewById(R.id.et_content);
        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(tv_content.getText().toString())){
                    video.setDescription(tv_content.getText().toString());
                    video.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                MyUtils.showToast(mContext,"提交成功");
                                dialog.dismiss();
                            }else {
                                MyUtils.showToast(mContext,"提交失败，请重试！");
                            }
                        }
                    });
                }else {
                    MyUtils.showToast(mContext,"答案不能为空！");
                }
            }
        });
        if(video.getDescription()!=null){
            tv_content.setText(video.getDescription());
        }
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
    }
}

