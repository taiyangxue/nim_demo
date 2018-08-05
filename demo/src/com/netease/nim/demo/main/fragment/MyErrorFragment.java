package com.netease.nim.demo.main.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.loveplusplus.demo.image.ImagePagerActivity;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.ErrorPicRet;
import com.netease.nim.demo.common.entity.SectionRet;
import com.netease.nim.demo.common.entity.bmob.ErrorPic;
import com.netease.nim.demo.common.entity.bmob.Section;
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.PictureUtil;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.home.activity.ErrorAdminActivity;
import com.netease.nim.demo.home.activity.SectionSelectActivity;
import com.netease.nim.demo.home.adapter.MyErrorPicAdapter;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.uikit.common.media.picker.PickImageHelper;
import com.netease.nim.uikit.common.media.picker.activity.PickImageActivity;
import com.netease.nim.uikit.common.media.picker.activity.PreviewImageFromCameraActivity;
import com.netease.nim.uikit.common.media.picker.model.PhotoInfo;
import com.netease.nim.uikit.common.media.picker.model.PickerContract;
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
import com.netease.nim.uikit.session.constant.Extras;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static com.netease.nim.uikit.session.constant.RequestCode.PICK_IMAGE;
import static com.netease.nim.uikit.session.constant.RequestCode.PREVIEW_IMAGE_FROM_CAMERA;

/**
 * Created by Administrator on 2017/2/6.
 */

public class MyErrorFragment extends MainTabFragment {
    private static final String TAG = "MyErrorFragment";
    private static final int SELECT_SETION = 0;
    private static final int AREA_SELECT = 1;
    private static final int CAMERA_INTENT_REQUEST = 3;
    private static final int SYS_INTENT_REQUEST = 4;

    private String mCurrentPhotoPath;// 图片路径
    private GridView gridView;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    private MyErrorPicAdapter adapter;
    private RecyclerView recyclerView;
    // 图片封装为一个数组
    private int[] icon = {R.drawable.t_ying, R.drawable.t_yu, R.drawable.t_li, R.drawable.t_shu,
            R.drawable.t_wu, R.drawable.t_hua, R.drawable.t_di, R.drawable.t_zheng,
            R.drawable.t_sheng};
//    private String[] iconName = {"语文", "数学", "英语", "物理",
//            "化学", "生物", "地理", "历史",
//            "政治"};
    private String[] iconName = {"英语", "语文", "历史", "数学", "物理", "化学", "地理", "政治", "生物"};
    private Button btn_push;
    private BitmapUtils bitmapUtils;
    private String[] urls;
    private ErrorPicRet.DataBean select_pic;
    private int current_position;
    private String fileName;
    public MyErrorFragment() {
        setContainerId(MainTab.CHAT_ROOM.fragmentId);
    }

    @Override
    protected void onInit() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_error_admin, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bitmapUtils = new BitmapUtils(getActivity());
        findViews();
        initData();
        Log.e("TAG","onActivityCreated");
//        gson = new Gson();
    }

    private void findViews() {
        gridView = findView(R.id.gridview);
        btn_push = findView(R.id.btn_push);
        btn_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPicDialog();
                showSelector(R.string.app_name, PICK_IMAGE, multiSelect, tempFile());
            }
        });
        // recyclerView
        recyclerView = findView(R.id.recycler_view);
        if(adapter==null){
            adapter = new MyErrorPicAdapter(bitmapUtils, recyclerView);
        }
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), true));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<MyErrorPicAdapter>() {
            @Override
            public void onItemClick(MyErrorPicAdapter adapter, View view, int position) {
                adapter.getItem(position);
                Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
                // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                urls=new String[adapter.getData().size()];
                for (int i=0;i<adapter.getData().size();i++) {
                    urls[i]=adapter.getData().get(i).getPic_image();
                }
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                getActivity().startActivity(intent);
            }
            @Override
            public void onItemLongClick(MyErrorPicAdapter adapter, View view, int position) {
                super.onItemLongClick(adapter, view, position);
                select_pic=adapter.getItem(position);
                current_position=position;
                showSelectDialog();
            }
        });
    }

    private void initData() {
        //新建List
        data_list = new ArrayList<Map<String, Object>>();
        //获取数据
        getData();
        //新建适配器
        String[] from = {"image", "text"};
        int[] to = {R.id.image, R.id.text};
        sim_adapter = new SimpleAdapter(getActivity(), data_list, R.layout.item_gridview_home, from, to);
        //配置适配器
        gridView.setAdapter(sim_adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), ErrorAdminActivity.class);
                intent.putExtra("course_id",position+1);
                getActivity().startActivity(intent);
            }
        });


        refreshData();
    }

    public List<Map<String, Object>> getData() {
        //cion和iconName的长度是相同的，这里任选其一都可以
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }
        return data_list;
    }

    private void showPicDialog() {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(getActivity()).create();
        alertDialog.show();
        Window win = alertDialog.getWindow();

        WindowManager.LayoutParams lp = win.getAttributes();
        win.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        win.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置弹出的动画效果
        win.setWindowAnimations(R.style.AnimBottom);
        lp.alpha = 0.7f;
        win.setAttributes(lp);
        win.setContentView(R.layout.dialog_pic);

        Button cancelBtn = (Button) win.findViewById(R.id.camera_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        Button camera_phone = (Button) win.findViewById(R.id.camera_phone);
        camera_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                systemPhoto();
                alertDialog.cancel();
            }

        });
        Button camera_camera = (Button) win.findViewById(R.id.camera_camera);
        camera_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPhoto();
                alertDialog.cancel();
            }

        });

    }

    /**
     * 调用相机拍照
     */
    private void cameraPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            // 指定存放拍摄照片的位置
            File f = createImageFile();
            takePictureIntent
                    .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            startActivityForResult(takePictureIntent, CAMERA_INTENT_REQUEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把程序拍摄的照片放到 SD卡的 Pictures目录中 shangxuntong 文件夹中
     * 照片的命名规则为：sxt_20160125_173729.jpg
     *
     * @return File
     * @throws IOException
     */
    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = format.format(new Date());
        String imageFileName = "sxt_" + timeStamp + ".jpg";

        File image = new File(PictureUtil.getAlbumDir(), imageFileName);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * 打开系统相册
     */
    private void systemPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
//        if (Build.VERSION.SDK_INT <19) {
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//        }else {
//            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//        }
        startActivityForResult(intent, SYS_INTENT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"requestCode"+requestCode+"requestCode"+requestCode);
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
                                        MyUtils.showToast(getActivity(),"提交分类失败，请重新提交");

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
//                                    MyUtils.showToast(getActivity(),"提交分类失败，请重新提交");
//                                }
//                            }
//                        });
                    }
                    break;
            }
        }
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case CAMERA_INTENT_REQUEST:
//                    //在图库中展示
//                    PictureUtil.galleryAddPic(getActivity(), mCurrentPhotoPath);
//                    //将拍摄的照片压缩保存
//                    compressSave(mCurrentPhotoPath);
//                    break;
//                case SYS_INTENT_REQUEST:
//                    photoCamera(data);
//                    break;
//            }
//
//        }
    }

    //调用系图库后执行相关操作
    private void photoCamera(Intent data) {
        Uri uri = data.getData();
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null,
                null, null);
        cursor.moveToFirst();
        String imageFilePath = cursor.getString(1);
        compressSave(imageFilePath);
        cursor.close();
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

            bm.compress(Bitmap.CompressFormat.JPEG, 40, fos);

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
                        ApiUtils.getInstance().errorpic_add(SharedPreferencesUtils.getInt(getActivity(), "account_id", 0), 0,
                                bmobFile.getFileUrl(), new ApiListener<ErrorPicRet.DataBean>() {
                                    @Override
                                    public void onSuccess(ErrorPicRet.DataBean dataBean) {
                                        refresh(dataBean);
                                        MyUtils.showToast(getActivity(), "上传成功");
                                    }

                                    @Override
                                    public void onFailed(String errorMsg) {
                                    MyUtils.showToast(getActivity(), errorMsg);
                                    }
                                });
//                        final ErrorPic errorPic=new ErrorPic();
//                        errorPic.setPicUrl(bmobFile.getFileUrl());
//                        errorPic.setAccount(DemoCache.getAccount());
//                        errorPic.setCourse("未整理");
//                        errorPic.setSection("未整理");
//                        errorPic.save(new SaveListener<String>() {
//                            @Override
//                            public void done(String s, BmobException e) {
//                                if(e==null){
//                                    refresh(errorPic);
//                                    MyUtils.showToast(getActivity(), "上传成功");
//                                    Log.e(TAG,errorPic.getPicUrl());
////                                    refreshData();
//                                }else {
//                                    MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
//                                }
//                            }
//                        });
                    } else {
//                        toast("上传文件失败：" + e.getMessage());
                        MyUtils.showToast(getActivity(), e.getErrorCode() + e.getMessage());
                    }
                }

                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                }
            });
//            FileBean filebean = new FileBean();
//            filebean.setFileName(f.getName());
//            filebean.setFileContent(PictureUtil.bitmapToString(photoPath));
//            new MyService(this).uploadFile(filebean);//上传身份证的保存路径
//            idcard_url="fileUpload/"+filebean.getFileName();
//            System.out.println(filebean.getFileName());
        } catch (Exception e) {

        }
    }
    private void refresh(ErrorPicRet.DataBean errorPic) {
        Log.e("TAG","refresh"+adapter);
        adapter.add(0,errorPic);
    }

    private void refreshData() {
//        adapter.clearData();
//        BmobQuery<ErrorPic> query=new BmobQuery<>();
//        query.addWhereEqualTo("course","未整理");
//        query.addWhereEqualTo("account",DemoCache.getAccount());
//        query.order("-updatedAt");
//        query.findObjects(new FindListener<ErrorPic>() {
//            @Override
//            public void done(List<ErrorPic> list, BmobException e) {
//                if(e==null&&list!=null&&list.size()>0){
//                    adapter.setNewData(list); // 刷新数源
//                    Log.e("TAG","数据刷新"+adapter.toString());
//                    postRunnable(new Runnable() {
//                        @Override
//                        public void run() {
//                            adapter.closeLoadAnimation();
//                        }
//                    });
//
//                }
//            }
//        });
//        ApiUtils.getInstance().errorpic_select(SharedPreferencesUtils.getInt(getActivity(), "account_id", 0),
//                0, new ApiListener<List<ErrorPicRet.DataBean>>() {
//                    @Override
//                    public void onSuccess(List<ErrorPicRet.DataBean> dataBeans) {
//                        adapter.setNewData(dataBeans); // 刷新数源
//                        Log.e("TAG","数据刷新"+adapter.toString());
//                        postRunnable(new Runnable() {
//                            @Override
//                            public void run() {
//                                adapter.closeLoadAnimation();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFailed(String errorMsg) {
//                        MyUtils.showToast(getActivity(), errorMsg);
//                    }
//                });
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
        PickImageHelper.pickImage(getActivity(), requestCode, option);
    }
    /**
     * 图片选取回调
     */
    private void onPickImageActivityResult(int requestCode, Intent data) {
        if (data == null) {
            Toast.makeText(getActivity(), com.netease.nim.uikit.R.string.picker_image_error, Toast.LENGTH_LONG).show();
            return;
        }
        boolean local = data.getBooleanExtra(Extras.EXTRA_FROM_LOCAL, false);
        if (local) {
            // 本地相册
            List<PhotoInfo> photos = PickerContract.getPhotos(data);
            if(photos == null) {
                Toast.makeText(getActivity(), com.netease.nim.uikit.R.string.picker_image_error, Toast.LENGTH_LONG).show();
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
            intent.setClass(getActivity(), PreviewImageFromCameraActivity.class);
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
                PickImageActivity.start(getActivity(), PICK_IMAGE, PickImageActivity.FROM_CAMERA, path);
            }
        }
    }
    /**
     * 是否可以获取图片
     */
    private boolean handleImagePath(Intent intent, Intent data) {
        String photoPath = data.getStringExtra(Extras.EXTRA_FILE_PATH);
        if (TextUtils.isEmpty(photoPath)) {
            Toast.makeText(getActivity(), com.netease.nim.uikit.R.string.picker_image_error, Toast.LENGTH_LONG).show();
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
            Toast.makeText(getActivity(), com.netease.nim.uikit.R.string.picker_image_error, Toast.LENGTH_LONG).show();
            return false;
        } else {
            ImageUtil.makeThumbnail(getActivity(), scaledImageFile);
        }
        intent.putExtra("ImageFilePath", scaledImageFile.getAbsolutePath());
        return true;
    }
    /**
     * 显示操作对话框
     *
     */
    private void showSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("选择操作");
        //    指定下拉列表的显示数据
        //    设置一个下拉的列表选择项
        builder.setItems(new String[]{"分类", "下载","删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent=new Intent(getActivity(), SectionSelectActivity.class);
                        intent.putExtra("user_id",select_pic.getUser_id());
                        intent.putExtra("course_id",0);
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("提醒");
        builder.setMessage("确定要删除该图片吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
//                select_pic.delete(new UpdateListener() {
//                    @Override
//                    public void done(BmobException e) {
//                        if (e == null) {
//                            //删除下面的子文件
//                            MyUtils.showToast(getActivity(), "删除成功");
//                            adapter.remove(current_position);
//                            dialog.dismiss();
//                        } else {
//                            MyUtils.showToast(getActivity(), "删除失败");
//                            LogUtil.e(TAG, e.getErrorCode() + e.getMessage());
//                        }
//                    }
//                });
                ApiUtils.getInstance().errorpic_delete(select_pic.getId(), new ApiListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        MyUtils.showToast(getActivity(), "删除成功");
                        adapter.remove(current_position);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailed(String errorMsg) {
                        MyUtils.showToast(getActivity(), "删除失败");
                    }
                });

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
                new AlertDialog.Builder(getActivity());
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
                            Toast.makeText(getActivity(),
                                    "下载失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(ResponseInfo<File> file) {
                            Log.e(TAG,"下载完成");
                            Toast.makeText(getActivity(), "下载完成", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLoading(long total, long current,
                                              boolean isUploading) {
                            super.onLoading(total, current, isUploading);
                            //进度条效果
//                            System.out.println(isUploading);
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
                                Toast.makeText(getActivity(), "下载完成", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
//                            Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "未检测到sd卡", Toast.LENGTH_SHORT);
        }
    }
}