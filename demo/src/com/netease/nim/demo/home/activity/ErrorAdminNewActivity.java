package com.netease.nim.demo.home.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.loveplusplus.demo.image.ImagePagerActivity;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.Common;
import com.netease.nim.demo.common.entity.ErrorPicResult;
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.PictureUtil;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.home.adapter.MyErrorPicAdapter;
import com.netease.nim.demo.home.adapter.MyNewErrorPicAdapter;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.media.picker.PickImageHelper;
import com.netease.nim.uikit.common.media.picker.activity.PickImageActivity;
import com.netease.nim.uikit.common.media.picker.activity.PreviewImageFromCameraActivity;
import com.netease.nim.uikit.common.media.picker.model.PhotoInfo;
import com.netease.nim.uikit.common.media.picker.model.PickerContract;
import com.netease.nim.uikit.common.ui.ptr2.PullToRefreshLayout;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.ui.recyclerview.listener.OnItemClickListener;
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

public class ErrorAdminNewActivity extends UI {
    private static final String TAG = "ErrorAdminNewActivity";
    private static final int SELECT_VIDEO_DIR2 = 1;
    private MyNewErrorPicAdapter adapter;
    private RecyclerView recyclerView;
    private BitmapUtils bitmapUtils;
    private PullToRefreshLayout swipeRefreshLayout;
    /**
     * 上级传过来的文件id
     */
    private int type_id;
    private int pid;
    private String grade;
    private int course;
    private List<ErrorPicResult.DataBean> videolist;
    private int limit;
    private int offset;
    private Button btn_push;
    private String[] urls;
    private ErrorPicResult.DataBean select_pic;
    private int current_position;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_admin_new);
        type_id = getIntent().getIntExtra("type_id", 0);
        pid = getIntent().getIntExtra("pid", 0);
        course = getIntent().getIntExtra("course", 0);
        grade = SharedPreferencesUtils.getString(this, "grade", "");
//        title = getIntent().getStringExtra("title");
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = getIntent().getStringExtra("title");
        setToolBar(R.id.toolbar, options);
        findViews();
        limit = 20;
        Log.e(TAG,getIntent().getIntExtra("type",1)+"");
        fetchData(true, false);
    }

    private void findViews() {
        btn_push= findView(R.id.btn_push);
        if(getIntent().getIntExtra("type",1)==2){
            btn_push.setVisibility(View.VISIBLE);
            btn_push.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSelector(R.string.app_name, PICK_IMAGE, multiSelect, tempFile());
                }
            });
        }

        swipeRefreshLayout = findView(R.id.swipe_refresh);
        swipeRefreshLayout.setPullUpEnable(true);
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
        // recyclerView
        recyclerView = findView(R.id.recycler_view);
        bitmapUtils = new BitmapUtils(this);
        adapter = new MyNewErrorPicAdapter(bitmapUtils, recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, getIntent().getIntExtra("type",1)));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(2), ScreenUtil.dip2px(2), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<MyNewErrorPicAdapter>() {
            @Override
            public void onItemClick(MyNewErrorPicAdapter adapter, View vew, int position) {
                ErrorPicResult.DataBean item = adapter.getItem(position);
                if (!TextUtils.isEmpty(item.getPic_image())) {
//                    Intent intent = new Intent(ErrorAdminNewActivity.this, ErrorAdminNewActivity.class);
//                    intent.putExtra("pid", item.getId());
//                    intent.putExtra("title", item.getName());
//                    intent.putExtra("course", course);
////                    if(item.getPic_image()!=null){
//                        intent.putExtra("type", 2);
////                    }
//                    startActivity(intent);
                    Intent intent = new Intent(ErrorAdminNewActivity.this, ImagePagerActivity.class);
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    urls=new String[adapter.getData().size()];
                    for (int i=0;i<adapter.getData().size();i++) {
                        urls[i]=adapter.getData().get(i).getPic_image();
                    }
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(ErrorAdminNewActivity.this, ErrorAdminNewActivity.class);
                    intent.putExtra("pid", item.getId());
                    intent.putExtra("title", item.getName());
                    intent.putExtra("course", course);
//                    intent.putExtra("type", 1);
                    startActivity(intent);
                }
            }
//            @Override
//            public void onItemLongClick(MyErrorPicAdapter adapter, View view, int position) {
//                super.onItemLongClick(adapter, view, position);
//                select_pic=adapter.getItem(position);
//                current_position=position;
//                showSelectDialog();
//            }
            @Override
            public void onItemLongClick(MyNewErrorPicAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
                select_pic=adapter.getItem(position);
                if (!TextUtils.isEmpty(select_pic.getPic_image())) {
                    current_position=position;
                    showSelectDialog();
                }

            }
        });
    }


    private void fetchData(boolean isRefresh, final boolean isLoadMore) {
        if (isRefresh) {
            offset = 0;
            adapter.clearData();
        }
        if (isLoadMore) {
            offset += limit;
        }
        ApiUtils.getInstance().errorpic_geterrorpictype(
                SharedPreferencesUtils.getInt(ErrorAdminNewActivity.this, "account_id", 0) + "",
                course + "",  pid, new ApiListener<List<ErrorPicResult.DataBean>>() {
                    @Override
                    public void onSuccess(List<ErrorPicResult.DataBean> dataBeans) {
                        videolist = dataBeans;
                        onFetchDataDone(true, isLoadMore, videolist);
                    }

                    @Override
                    public void onFailed(String errorMsg) {
                        onFetchDataDone(false, isLoadMore, null);
                        MyUtils.showToast(ErrorAdminNewActivity.this, errorMsg);
                        recyclerView.setLayoutManager(new GridLayoutManager(ErrorAdminNewActivity.this, 2));
                        btn_push.setVisibility(View.VISIBLE);
                        btn_push.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showSelector(R.string.app_name, PICK_IMAGE, multiSelect, tempFile());
                            }
                        });
                    }
                });
    }


    private void onFetchDataDone(final boolean success, final boolean isLoadMore, final List<ErrorPicResult.DataBean> data) {
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
                        if(data!=null&&!TextUtils.isEmpty(data.get(0).getPic_image())){
                            recyclerView.setLayoutManager(new GridLayoutManager(ErrorAdminNewActivity.this, 2));
                            btn_push.setVisibility(View.VISIBLE);
                            btn_push.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showSelector(R.string.app_name, PICK_IMAGE, multiSelect, tempFile());
                                }
                            });
                        }
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

    private boolean multiSelect=true;
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
            if(photos == null) {
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
            oosUpfile(ErrorAdminNewActivity.this,new File(picPath));
//            final BmobFile bmobFile = new BmobFile(new File(picPath));
//            bmobFile.uploadblock(new UploadFileListener() {
//
//                @Override
//                public void done(BmobException e) {
//                    if (e == null) {
//                        //bmobFile.getFileUrl()--返回的上传文件的完整地址
////                        toast("上传文件成功:" + bmobFile.getFileUrl());
////                        MyUtils.showToast(getActivity(), "上传文件成功:" + bmobFile.getFileUrl());
//                        ApiUtils.getInstance().errorpic_add(SharedPreferencesUtils.getInt(ErrorAdminNewActivity.this, "account_id", 0),
//                                pid, bmobFile.getFileUrl(), new ApiListener<ErrorPicResult.DataBean>() {
//                                    @Override
//                                    public void onSuccess(ErrorPicResult.DataBean s) {
//                                        refresh(s);
//                                        MyUtils.showToast(ErrorAdminNewActivity.this, "上传成功");
//                                    }
//
//                                    @Override
//                                    public void onFailed(String errorMsg) {
//                                        MyUtils.showToast(ErrorAdminNewActivity.this, errorMsg);
//
//                                    }
//                                });
//                    } else {
//                        MyUtils.showToast(ErrorAdminNewActivity.this, e.getErrorCode() + e.getMessage());
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
    private void refresh(ErrorPicResult.DataBean errorPic) {
        Log.e("TAG","refresh"+adapter);
        adapter.add(0,errorPic);
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
                Log.e("PutObject", "UploadSuccess");
                ApiUtils.getInstance().errorpic_add(SharedPreferencesUtils.getInt(ErrorAdminNewActivity.this, "account_id", 0),
                        pid, OOS_HOST_MY+file.getName(), new ApiListener<ErrorPicResult.DataBean>() {
                            @Override
                            public void onSuccess(ErrorPicResult.DataBean s) {
                                refresh(s);
                                MyUtils.showToast(ErrorAdminNewActivity.this, "上传成功");
                            }

                            @Override
                            public void onFailed(String errorMsg) {
                                MyUtils.showToast(ErrorAdminNewActivity.this, errorMsg);

                            }
                        });
                //删除本地文件
//                if (file.exists() && file.isFile()) {
//                    file.delete();
//                    Log.e(TAG,"文件删除成功");
//                    //文件删除成功
//                }
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
    /**
     * 显示操作对话框
     *
     */
    private void showSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ErrorAdminNewActivity.this);
        builder.setTitle("选择操作");
        //    指定下拉列表的显示数据
        //    设置一个下拉的列表选择项
        builder.setItems(new String[]{ "下载","删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
//                    case 0:
//                        Intent intent=new Intent(ErrorAdminNewActivity.this, SectionSelectActivity.class);
//                        intent.putExtra("user_id",select_pic.getUser_id());
//                        intent.putExtra("course_id",select_course);
//                        startActivityForResult(intent,SELECT_SETION);
//                        break;
                    case 0:
                        showDownloadDialog(select_pic.getPic_image());
                        break;
                    case 1:
                        showDelete();
                        break;
                }
            }
        });
        builder.show();
    }
    /**
     * 删除章节
     *
     */
    public void showDelete() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ErrorAdminNewActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("提醒");
        builder.setMessage("确定要删除该图片吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                ApiUtils.getInstance().errorpic_delete(select_pic.getId(), new ApiListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        MyUtils.showToast(ErrorAdminNewActivity.this, "删除成功");
                        adapter.remove(current_position);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailed(String errorMsg) {
                        MyUtils.showToast(ErrorAdminNewActivity.this, "删除失败");
                    }
                });
//                select_pic.delete(new UpdateListener() {
//                    @Override
//                    public void done(BmobException e) {
//                        if (e == null) {
//                            //删除下面的子文件
//
//                        } else {
//                            MyUtils.showToast(ErrorAdminNewActivity.this, "删除失败");
//                            LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
//                        }
//                    }
//                });

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showDownloadDialog(final String snapshotUrl){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ErrorAdminNewActivity.this);
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
                            Toast.makeText(ErrorAdminNewActivity.this,
                                    "下载失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(ResponseInfo<File> file) {
                            Log.e(TAG,"下载完成");
                            Toast.makeText(ErrorAdminNewActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ErrorAdminNewActivity.this, "下载完成", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
//                            Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(ErrorAdminNewActivity.this, "未检测到sd卡", Toast.LENGTH_SHORT);
        }
    }
}
