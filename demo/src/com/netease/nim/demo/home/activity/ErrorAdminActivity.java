package com.netease.nim.demo.home.activity;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loveplusplus.demo.image.ImagePagerActivity;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.ErrorPicRet;
import com.netease.nim.demo.common.entity.SectionRet;
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.PictureUtil;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.home.adapter.MyErrorPicAdapter;
import com.netease.nim.demo.home.adapter.MySpinnerAdapter;
import com.netease.nim.demo.login.MyUser;
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
import com.netease.nim.uikit.common.util.log.LogUtil;
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


import static com.netease.nim.uikit.session.constant.RequestCode.PICK_IMAGE;
import static com.netease.nim.uikit.session.constant.RequestCode.PREVIEW_IMAGE_FROM_CAMERA;

public class ErrorAdminActivity extends UI {
    private static final int SELECT_SETION = 0;
    private static final String TAG = "ErrorAdminActivity";
    private String[] urls;
    private MyErrorPicAdapter adapter;
    private BitmapUtils bitmapUtils;
    private int select_course;
    @ViewInject(R.id.swipe_refresh)
    private PullToRefreshLayout swipeRefreshLayout;
    @ViewInject(R.id.recycler_view)
    private RecyclerView recyclerView;
    @ViewInject(R.id.spinner)
    private Spinner spinner;
    @ViewInject(R.id.btn_push)
    private Button btn_push;
    private MySpinnerAdapter spinner_adapter;
    private List<SectionRet.DataBean> sections = new ArrayList<SectionRet.DataBean>();
    private SectionRet.DataBean current_section;
    private MyUser current_user;
    private ErrorPicRet.DataBean select_pic;
    private int current_position;
    private String fileName;
    private String[] iconName = {"英语", "语文", "历史", "数学", "物理", "化学", "地理", "政治", "生物"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_admin);
        ViewUtils.inject(this);
        select_course = getIntent().getIntExtra("course_id",0);
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = iconName[select_course-1];
        setToolBar(R.id.toolbar, options);
        findViews();
        getSectionData();

    }
    private void findViews() {
        swipeRefreshLayout = findView(R.id.swipe_refresh);
        swipeRefreshLayout.setPullUpEnable(false);
        swipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                refreshData();
            }

            @Override
            public void onPullUpToRefresh() {

            }
        });
        btn_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPicDialog();
                showSelector(R.string.app_name, PICK_IMAGE, multiSelect, tempFile());
            }
        });
        // recyclerView
        bitmapUtils = new BitmapUtils(this);
        adapter = new MyErrorPicAdapter(bitmapUtils, recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<MyErrorPicAdapter>() {
            @Override
            public void onItemClick(MyErrorPicAdapter adapter, View view, int position) {
                adapter.getItem(position);
                Intent intent = new Intent(ErrorAdminActivity.this, ImagePagerActivity.class);
                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                urls=new String[adapter.getData().size()];
                for (int i=0;i<adapter.getData().size();i++) {
                    urls[i]=adapter.getData().get(i).getPic_image();
                }
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                startActivity(intent);
            }
            @Override
            public void onItemLongClick(MyErrorPicAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
                select_pic=adapter.getItem(position);
                current_position=position;
                showSelectDialog();
            }
        });
        initSpinner();
    }

    private void initSpinner() {
        //将可选内容与ArrayAdapter连接起来
        spinner_adapter = new MySpinnerAdapter(this, sections);
        //将adapter 添加到spinner中
        spinner.setAdapter(spinner_adapter);

        //设置默认值
        spinner.setVisibility(View.VISIBLE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_section = sections.get(position);
                refreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void refreshData() {
        adapter.clearData();
        ApiUtils.getInstance().errorpic_select(SharedPreferencesUtils.getInt(this, "account_id", 0),
                current_section.getId(), new ApiListener<List<ErrorPicRet.DataBean>>() {
                    @Override
                    public void onSuccess(List<ErrorPicRet.DataBean> dataBeans) {
                        onFetchDataDone(true, dataBeans);
                    }

                    @Override
                    public void onFailed(String errorMsg) {
                        onFetchDataDone(false, null);
                        MyUtils.showToast(ErrorAdminActivity.this, "该分类暂无数据");
                    }
                });
//        BmobQuery<ErrorPic> query = new BmobQuery<>();
//        query.addWhereEqualTo("course", select_course);
//        if (current_section != null) {
//            query.addWhereEqualTo("sectionId", current_section);
//        }
//        query.addWhereEqualTo("account", DemoCache.getAccount());
//        query.order("-updatedAt");
//        query.findObjects(new FindListener<ErrorPic>() {
//            @Override
//            public void done(List<ErrorPic> list, BmobException e) {
//                if (e == null && list != null && list.size() > 0) {
//                    onFetchDataDone(true, list);
//                } else {
//                    onFetchDataDone(false, null);
//                    MyUtils.showToast(ErrorAdminActivity.this, "该分类暂无数据");
//                }
//            }
//        });
    }

    private void onFetchDataDone(final boolean success, final List<ErrorPicRet.DataBean> list) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false); // 刷新结束

                if (success) {
                    adapter.setNewData(list); // 刷新数据源
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
        if (resultCode == 0) {
            switch (requestCode) {
                case SELECT_SETION:
                    if(data!=null){
                        LogUtil.e(TAG,data.getStringExtra("course")+data.getStringExtra("section"));
                        ApiUtils.getInstance().errorpic_update(select_pic.getId(), data.getIntExtra("section_id",0),
                                new ApiListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        adapter.remove(current_position);
                                    }

                                    @Override
                                    public void onFailed(String errorMsg) {
                                        MyUtils.showToast(ErrorAdminActivity.this,"提交分类失败，请重新提交");

                                    }
                                });
//                        select_pic.setCourse(data.getStringExtra("course"));
//                        select_pic.setSectionId((Section) data.getSerializableExtra("section"));
//                        select_pic.update(new UpdateListener() {
//                            @Override
//                            public void done(BmobException e) {
//                                if(e==null){
//                                    adapter.remove(current_position);
//                                }else {
//                                    MyUtils.showToast(ErrorAdminActivity.this,"提交分类失败，请重新提交");
//                                }
//                            }
//                        });
                    }
                    break;
            }
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
        } catch (Exception e) {

        }
    }
    private void refresh(ErrorPicRet.DataBean errorPic) {
        Log.e("TAG","refresh"+adapter);
        adapter.add(0,errorPic);
    }
    /**
     * 获取对应科目的章节
     *
     */
    private void getSectionData() {
        ApiUtils.getInstance().errorpic_getsection(select_course,
                SharedPreferencesUtils.getString(this, "grade", ""), new ApiListener<List<SectionRet.DataBean>>() {
                    @Override
                    public void onSuccess(List<SectionRet.DataBean> dataBeans) {
                        sections.clear();
                        sections.addAll(dataBeans);
                        if(current_section==null){
                            current_section=sections.get(0);
                        }
                        spinner_adapter.notifyDataSetChanged();
                        refreshData();
                    }

                    @Override
                    public void onFailed(String errorMsg) {
                        MyUtils.showToast(ErrorAdminActivity.this, "该科目暂无章节分类");
                    }
                });
//        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
//        BmobQuery<Section> query = new BmobQuery<>();
////        query.addWhereEqualTo("grade", SharedPreferencesUtils.getString(ErrorAdminActivity.this, "grade", ""));
//        query.addWhereEqualTo("grade", userInfo.getGrade());
//        query.addWhereEqualTo("course", current_course);
////        query.order("");
//        query.order("updatedAt");
//        query.findObjects(new FindListener<Section>() {
//            @Override
//            public void done(List<Section> list, BmobException e) {
//                if (e == null) {
//                    if (list != null && list.size() > 0) {
//                        sections.clear();
//                        sections.addAll(list);
//                        if(current_section==null){
//                            current_section=sections.get(0);
//                        }
//                        spinner_adapter.notifyDataSetChanged();
//                    } else {
//                        MyUtils.showToast(ErrorAdminActivity.this, "该科目暂无章节分类");
//                    }
//                } else {
//                    LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
//                }
//            }
//        });
    }
    /**
     * 显示操作对话框
     *
     */
    private void showSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ErrorAdminActivity.this);
        builder.setTitle("选择操作");
        //    指定下拉列表的显示数据
        //    设置一个下拉的列表选择项
        builder.setItems(new String[]{"重新分类", "下载","删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent=new Intent(ErrorAdminActivity.this, SectionSelectActivity.class);
                        intent.putExtra("user_id",select_pic.getUser_id());
                        intent.putExtra("course_id",select_course);
                        startActivityForResult(intent,SELECT_SETION);
                        break;
                    case 1:
                        showDownloadDialog(select_pic.getPic_image());
                        break;
                    case 2:
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ErrorAdminActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("提醒");
        builder.setMessage("确定要删除该图片吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                ApiUtils.getInstance().errorpic_delete(select_pic.getId(), new ApiListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        MyUtils.showToast(ErrorAdminActivity.this, "删除成功");
                        adapter.remove(current_position);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailed(String errorMsg) {
                        MyUtils.showToast(ErrorAdminActivity.this, "删除失败");
                    }
                });
//                select_pic.delete(new UpdateListener() {
//                    @Override
//                    public void done(BmobException e) {
//                        if (e == null) {
//                            //删除下面的子文件
//
//                        } else {
//                            MyUtils.showToast(ErrorAdminActivity.this, "删除失败");
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
                new AlertDialog.Builder(ErrorAdminActivity.this);
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
                            Toast.makeText(ErrorAdminActivity.this,
                                    "下载失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(ResponseInfo<File> file) {
                            Log.e(TAG,"下载完成");
                            Toast.makeText(ErrorAdminActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ErrorAdminActivity.this, "下载完成", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
//                            Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(ErrorAdminActivity.this, "未检测到sd卡", Toast.LENGTH_SHORT);
        }
    }
}
