package com.netease.nim.demo.home.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.PictureUtil;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.media.picker.PickImageHelper;
import com.netease.nim.uikit.common.media.picker.activity.PickImageActivity;
import com.netease.nim.uikit.common.media.picker.activity.PreviewImageFromCameraActivity;
import com.netease.nim.uikit.common.media.picker.model.PhotoInfo;
import com.netease.nim.uikit.common.media.picker.model.PickerContract;
import com.netease.nim.uikit.common.util.file.AttachmentStore;
import com.netease.nim.uikit.common.util.media.ImageUtil;
import com.netease.nim.uikit.common.util.storage.StorageType;
import com.netease.nim.uikit.common.util.storage.StorageUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
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

public class NodeDetailActivity2 extends UI {
    private static final String TAG ="NodeDetailActivity2" ;
    private String title;
    private EditText et_title;
    private String current_image;
    private BitmapUtils bitmapUtil;
    private ImageView iv_content;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_detail2);
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = getIntent().getStringExtra("title");
        setToolBar(R.id.toolbar, options);
        bitmapUtil=new BitmapUtils(this);
        et_title = (EditText) findViewById(R.id.et_title);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        current_image=getIntent().getStringExtra("content_image");
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(TextUtils.isEmpty(current_image)){
                   MyUtils.showToast(NodeDetailActivity2.this,"请上传图片后再保存");
                   return;
               }
               title= et_title.getText().toString();
                ApiUtils.getInstance().node_add(SharedPreferencesUtils.getInt(NodeDetailActivity2.this, "account_id", 0) + "",
                        getIntent().getIntExtra("course", 0) , title, current_image, new ApiListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                finish();
                                MyUtils.showToast(NodeDetailActivity2.this,"添加成功");
                            }

                            @Override
                            public void onFailed(String errorMsg) {
                                MyUtils.showToast(NodeDetailActivity2.this,errorMsg);
                            }
                        });
            }
        });
        iv_content = (ImageView) findViewById(R.id.iv_content);
        iv_content.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showSelector(R.string.app_name, PICK_IMAGE, multiSelect, tempFile());

            }
        });

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

            bm.compress(Bitmap.CompressFormat.JPEG, 40, fos);

            String picPath = PictureUtil.getAlbumDir() + "/small_" + f.getName();
            bitmapUtil.display(iv_content,photoPath);
            oosUpfile(NodeDetailActivity2.this,new File(picPath));
//            final BmobFile bmobFile = new BmobFile(new File(picPath));
//            bmobFile.uploadblock(new UploadFileListener() {
//
//                @Override
//                public void done(BmobException e) {
//                    if (e == null) {
//                        //bmobFile.getFileUrl()--返回的上传文件的完整地址
////                        toast("上传文件成功:" + bmobFile.getFileUrl());
////                        MyUtils.showToast(getActivity(), "上传文件成功:" + bmobFile.getFileUrl());
////                        mEditor.insertImage(bmobFile.getFileUrl(),
////                                "file");
//                        current_image=bmobFile.getFileUrl();
//
//                    } else {
////                        toast("上传文件失败：" + e.getMessage());
//                        MyUtils.showToast(NodeDetailActivity2.this, e.getErrorCode() + e.getMessage());
//                    }
//                }
//
//                @Override
//                public void onProgress(Integer value) {
//                    // 返回的上传进度（百分比）
//                    Log.e(TAG,"上传进度"+value);
//                    progressBar.setProgress(value);
//                }
//            });
        } catch (Exception e) {

        }
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
                //删除本地文件
                current_image=OOS_HOST_MY+file.getName();
//                mEditor.insertImage(OOS_HOST_MY+file.getName(),
//                        "file");
//                if (file.exists() && file.isFile()) {
//                    file.delete();
//                    Log.e("TAG","文件删除成功");
//                }
//                //修改记录的图片
//                if(pushrecord_ids.length()>=10){
//                    ApiUtils.getInstance().openrecord_add(pushrecord_ids, OOS_HOST_MY + device_id + "/" + file.getName(), new ApiListener<String>() {
//                        @Override
//                        public void onSuccess(String msg) {
//                            MyUtils.showToast(MainActivity.this, msg);
//                        }
//
//                        @Override
//                        public void onFailed(String errorMsg) {
//                            MyUtils.showToast(MainActivity.this, errorMsg);
//                        }
//                    });
//                }else {
//
//                    ApiUtils.getInstance().pushrecord_update(pushrecord_ids, OOS_HOST_MY + device_id + "/" + file.getName(), new ApiListener<String>() {
//                        @Override
//                        public void onSuccess(String s) {
//                            Log.e(TAG, s);
//                        }
//
//                        @Override
//                        public void onFailed(String errorMsg) {
//                            Log.e(TAG, errorMsg);
//                        }
//                    });
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
}
