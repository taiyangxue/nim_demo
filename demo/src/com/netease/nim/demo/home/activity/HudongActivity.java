package com.netease.nim.demo.home.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.lidroid.xutils.BitmapUtils;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.Common;
import com.netease.nim.demo.common.entity.Videocomment;
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.PictureUtil;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.home.adapter.VideocommentAdapter;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.media.picker.PickImageHelper;
import com.netease.nim.uikit.common.media.picker.activity.PickImageActivity;
import com.netease.nim.uikit.common.media.picker.activity.PreviewImageFromCameraActivity;
import com.netease.nim.uikit.common.media.picker.model.PhotoInfo;
import com.netease.nim.uikit.common.media.picker.model.PickerContract;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.util.file.AttachmentStore;
import com.netease.nim.uikit.common.util.media.ImageUtil;
import com.netease.nim.uikit.common.util.storage.StorageType;
import com.netease.nim.uikit.common.util.storage.StorageUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nim.uikit.session.constant.Extras;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


import static com.netease.nim.demo.common.entity.Common.OOS_HOST_MY;
import static com.netease.nim.uikit.session.constant.RequestCode.PICK_IMAGE;
import static com.netease.nim.uikit.session.constant.RequestCode.PREVIEW_IMAGE_FROM_CAMERA;

public class HudongActivity extends UI {
    private static final String TAG = "HudongActivity";
    private VideocommentAdapter adapter;
    private RecyclerView recyclerView;
    private PullToRefreshLayout swipeRefreshLayout;
    private List<Videocomment.DataBean> videolist;
    private int limit;
    private int offset;
    private BitmapUtils bitmapUtil;
    private int video_id;
    private Button btn_push;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hudong);
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "一题多解";
        setToolBar(R.id.toolbar, options);
        video_id = getIntent().getIntExtra("video_id", 0);
        bitmapUtil = new BitmapUtils(this);
        findViews();
        limit = 20;
        fetchData(true, false);
    }

    private void findViews() {
//        setTitle(getIntent().getStringExtra("title"));
        btn_push = findView(R.id.btn_push);
        btn_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPicDialog();
                showSelector(R.string.app_name, PICK_IMAGE, multiSelect, tempFile());
            }
        });
        swipeRefreshLayout = findView(R.id.swipe_refresh);
        swipeRefreshLayout.setPullUpEnable(false);
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                fetchData(true, false);
            }

            @Override
            public void onPullUpToRefresh() {
                fetchData(false, true);
            }
        });
        recyclerView = findView(R.id.recycler_view);
        adapter = new VideocommentAdapter(bitmapUtil, recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
//        recyclerView.addOnItemTouchListener(new OnItemClickListener<CollectionAdapter>() {
//            @Override
//            public void onItemClick(CollectionAdapter adapter, View vew, int position) {
////                final Object videoDir = adapter.getItem(position);
////                startVideo(adapter.getItem(position));
//            }
//
//            @Override
//            public void onItemLongClick(CollectionAdapter adapter, View view, int position) {
//                super.onItemLongClick(adapter, view, position);
//            }
//        });
    }

    private void fetchData(boolean isRefresh, final boolean isLoadMore) {
        if (isRefresh) {
            offset = 0;
            adapter.clearData();
        }
        if (isLoadMore) {
            offset += limit;
        }
        ApiUtils.getInstance().videocomment_select(video_id + "", offset, limit, new ApiListener<List<Videocomment.DataBean>>() {
            @Override
            public void onSuccess(List<Videocomment.DataBean> dataBeans) {
                videolist = dataBeans;
                onFetchDataDone(true, isLoadMore, videolist);
            }

            @Override
            public void onFailed(String errorMsg) {
                onFetchDataDone(false, isLoadMore, null);
                MyUtils.showToast(HudongActivity.this, errorMsg);
            }
        });
    }


    private void onFetchDataDone(final boolean success, final boolean isLoadMore, final List<Videocomment.DataBean> data) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false); // 刷新结束
                swipeRefreshLayout.setLoadMore(false);
//                Log.e("123","isLoadMore:"+isLoadMore);
                if (success) {
                    if (isLoadMore) {
                        adapter.addData(data);
                    } else {
                        adapter.setNewData(data); // 刷新数据源
                    }
                    adapter.closeLoadAnimation();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE:
                onPickImageActivityResult(requestCode, data);
                break;
            case PREVIEW_IMAGE_FROM_CAMERA:
                onPreviewImageActivityResult(requestCode, data);
                break;
        }
    }

    private static final int PICK_IMAGE_COUNT = 9;
    private static final int PORTRAIT_IMAGE_WIDTH = 800;
    private static final int PORTRAIT_IMAGE_HEIGHT = 600;
    public static final String MIME_JPEG = "image/jpeg";
    public static final String JPG = ".jpg";

    private boolean multiSelect = true;
    private boolean crop = true;

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
        option.cropOutputImageHeight = PORTRAIT_IMAGE_HEIGHT;

        option.outputPath = outPath;
        PickImageHelper.pickImage(this, requestCode, option);
    }

    /**
     * 图片选取回调
     */
    private void onPickImageActivityResult(int requestCode, Intent data) {
        if (data == null) {
            Toast.makeText(this, com.netease.nim.uikit.R.string.picker_image_error, Toast.LENGTH_LONG).show();
            return;
        }
        boolean local = data.getBooleanExtra(Extras.EXTRA_FROM_LOCAL, false);
        if (local) {
            // 本地相册
            List<PhotoInfo> photos = PickerContract.getPhotos(data);
            if (photos == null) {
                Toast.makeText(this, com.netease.nim.uikit.R.string.picker_image_error, Toast.LENGTH_LONG).show();
                return;
            }
            for (PhotoInfo photoInfo : photos) {
                compressSave(photoInfo.getAbsolutePath());
            }
//            sendImageAfterSelfImagePicker(data);
        } else {
            // 拍照
            Intent intent = new Intent();
            if (!handleImagePath(intent, data)) {
                return;
            }
            intent.setClass(this, PreviewImageFromCameraActivity.class);
            startActivityForResult(intent, PREVIEW_IMAGE_FROM_CAMERA);
        }
    }

    /**
     * 拍摄回调
     */
    private void onPreviewImageActivityResult(int requestCode, Intent data) {
        if (data.getBooleanExtra(PreviewImageFromCameraActivity.RESULT_SEND, false)) {
//            sendImageAfterPreviewPhotoActivityResult(data);
            final ArrayList<String> selectedImageFileList = data.getStringArrayListExtra(Extras.EXTRA_SCALED_IMAGE_LIST);
            final ArrayList<String> origSelectedImageFileList = data.getStringArrayListExtra(Extras.EXTRA_ORIG_IMAGE_LIST);
//            Log.e("TAG","拍摄回调"+selectedImageFileList.size()+origSelectedImageFileList.size());

            boolean isOrig = data.getBooleanExtra(Extras.EXTRA_IS_ORIGINAL, false);
            for (int i = 0; i < selectedImageFileList.size(); i++) {
                String imageFilepath = selectedImageFileList.get(i);
//                Log.e("TAG","拍摄回调"+imageFilepath);
                compressSave(imageFilepath);
                File imageFile = new File(imageFilepath);
                String origImageFilePath = origSelectedImageFileList.get(i);
            }
        } else if (data.getBooleanExtra(PreviewImageFromCameraActivity.RESULT_RETAKE, false)) {
            String filename = StringUtil.get32UUID() + JPG;
            String path = StorageUtil.getWritePath(filename, StorageType.TYPE_TEMP);

            if (requestCode == PREVIEW_IMAGE_FROM_CAMERA) {
                PickImageActivity.start(this, PICK_IMAGE, PickImageActivity.FROM_CAMERA, path);
            }
        }
    }

    /**
     * 是否可以获取图片
     */
    private boolean handleImagePath(Intent intent, Intent data) {
        String photoPath = data.getStringExtra(Extras.EXTRA_FILE_PATH);
        if (TextUtils.isEmpty(photoPath)) {
            Toast.makeText(this, com.netease.nim.uikit.R.string.picker_image_error, Toast.LENGTH_LONG).show();
            return false;
        }

        File imageFile = new File(photoPath);
        intent.putExtra("OrigImageFilePath", photoPath);
        File scaledImageFile = ImageUtil.getScaledImageFileWithMD5(imageFile, MIME_JPEG);

        boolean local = data.getExtras().getBoolean(Extras.EXTRA_FROM_LOCAL, true);
        if (!local) {
            // 删除拍照生成的临时文件
            AttachmentStore.delete(photoPath);
        }

        if (scaledImageFile == null) {
            Toast.makeText(this, com.netease.nim.uikit.R.string.picker_image_error, Toast.LENGTH_LONG).show();
            return false;
        } else {
            ImageUtil.makeThumbnail(this, scaledImageFile);
        }
        intent.putExtra("ImageFilePath", scaledImageFile.getAbsolutePath());
        return true;
    }

    /**
     * 将获取的图片压缩后上传
     *
     * @param photoPath//图片存放路径
     */
    private void compressSave(String photoPath) {
        try {
            File f = new File(photoPath);
            Bitmap bm = PictureUtil.getSmallBitmap(photoPath);
            FileOutputStream fos = new FileOutputStream(new File(
                    PictureUtil.getAlbumDir(), "small_" + f.getName()));

            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);

//            Toast.makeText(getActivity(), "压缩成功，已保存至" + PictureUtil.getAlbumDir(), Toast.LENGTH_SHORT).show();
            String picPath = PictureUtil.getAlbumDir() + "/small_" + f.getName();
            oosUpfile(HudongActivity.this,new File(picPath));
//            final BmobFile bmobFile = new BmobFile(new File(picPath));
//            bmobFile.uploadblock(new UploadFileListener() {
//
//                @Override
//                public void done(BmobException e) {
//                    if (e == null) {
//                        showAddContent(bmobFile.getFileUrl());
//                    } else {
////                        toast("上传文件失败：" + e.getMessage());
//                        MyUtils.showToast(HudongActivity.this, e.getErrorCode() + e.getMessage());
//                    }
//                }
//
//                @Override
//                public void onProgress(Integer value) {
//                    // 返回的上传进度（百分比）
//                }
//            });
        } catch (Exception e) {

        }
    }

    private void refresh(Videocomment.DataBean item) {
        Log.e("TAG", "refresh" + adapter);
        adapter.add(0, item);
    }

    /**
     * 创建目录对话框
     * @param fileUrl
     */
    public void showAddContent(final String fileUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dView = View.inflate(this, R.layout.dialog_content, null);
        final AlertDialog dialog = builder.create();
        final EditText et_content = (EditText) dView.findViewById(R.id.et_content);
        dialog.setView(dView, 0, 0, 0, 0);
        //确认按钮监听
        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = et_content.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {

                    ApiUtils.getInstance().videocomment_add(video_id + "", SharedPreferencesUtils.getInt(HudongActivity.this, "account_id", 0) + ""
                            ,fileUrl,content, new ApiListener<Videocomment.DataBean>() {
                                @Override
                                public void onSuccess(Videocomment.DataBean s) {
                                    dialog.dismiss();
                                    refresh(s);
                                    MyUtils.showToast(HudongActivity.this, "上传成功");
                                }

                                @Override
                                public void onFailed(String errorMsg) {
                                    MyUtils.showToast(HudongActivity.this, errorMsg);
                                }
                            });
                } else {
                    MyUtils.showToast(HudongActivity.this, "内容不能为空");
                }
            }
        });
        dView.findViewById(R.id.bt_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Log.e(TAG,"show");
        dialog.show();

    }
    private OSSCustomSignerCredentialProvider credentialProvider;

    private void oosUpfile(Context context, final File file) {
        credentialProvider = new OSSCustomSignerCredentialProvider() {
            @Override
            public String signContent(String content) {
                String signature = hmac_sha1(Common.OOS_ACCESS_KEY_SECRET, content);
                return "OSS " + Common.OOS_ACCESS_KEY_ID + ":" + signature;
            }
        };
        OSS oss = new OSSClient(context, Common.OOS_HOST, credentialProvider);
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest("yidu-app", "opendoor_img/" + file.getName(), file.getAbsolutePath());
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, final long currentSize, final long totalSize) {
//                Log.e("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                //进度百分比
//                int progress = (int) (current * 100 / totalSize);
//                tv_update.setVisibility(View.VISIBLE);
//                tv_update.setText(progress
//                        + "%");
            }
        });
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("PutObject", "UploadSuccess");
                        showAddContent(OOS_HOST_MY+file.getName());
                    }
                });

            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    private String hmac_sha1(String key, String datas) {
        String reString = "";

        try {
            byte[] data = key.getBytes("UTF-8");
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance("HmacSHA1");
            //用给定密钥初始化 Mac 对象
            mac.init(secretKey);

            byte[] text = datas.getBytes("UTF-8");
            //完成 Mac 操作
            byte[] text1 = mac.doFinal(text);
            return new String(Base64.encodeBase64(text1), "UTF-8");
        } catch (Exception e) {
            // TODO: handle exception
        }
        return reString;
    }
}
