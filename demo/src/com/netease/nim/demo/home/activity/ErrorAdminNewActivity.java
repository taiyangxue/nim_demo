package com.netease.nim.demo.home.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.ErrorPicResult;
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.PictureUtil;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;

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
                if (item.getPid() != 0) {
                    Intent intent = new Intent(ErrorAdminNewActivity.this, ErrorAdminNewActivity.class);
                    intent.putExtra("pid", item.getId());
                    intent.putExtra("title", item.getName());
                    intent.putExtra("course", course);
//                    if(item.getPic_image()!=null){
                        intent.putExtra("type", 2);
//                    }
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(MyNewErrorPicAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
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
            final BmobFile bmobFile = new BmobFile(new File(picPath));
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        //bmobFile.getFileUrl()--返回的上传文件的完整地址
//                        toast("上传文件成功:" + bmobFile.getFileUrl());
//                        MyUtils.showToast(getActivity(), "上传文件成功:" + bmobFile.getFileUrl());
                        ApiUtils.getInstance().errorpic_add(SharedPreferencesUtils.getInt(ErrorAdminNewActivity.this, "account_id", 0),
                                pid, bmobFile.getFileUrl(), new ApiListener<ErrorPicResult.DataBean>() {
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
                    } else {
                        MyUtils.showToast(ErrorAdminNewActivity.this, e.getErrorCode() + e.getMessage());
                    }
                }

                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                }
            });
        } catch (Exception e) {

        }
    }
    private void refresh(ErrorPicResult.DataBean errorPic) {
        Log.e("TAG","refresh"+adapter);
        adapter.add(0,errorPic);
    }
}
