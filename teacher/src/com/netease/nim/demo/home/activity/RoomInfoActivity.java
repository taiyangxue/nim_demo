package com.netease.nim.demo.home.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.ClassRoom;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.PictureUtil;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class RoomInfoActivity extends UI {
    private static final int SYS_INTENT_REQUEST = 1;
    private static final int CROP_PICTURE = 3;
    private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
    private static final String TAG = "RoomInfoActivity";
    @ViewInject(R.id.tv_name)
    private TextView tv_name;
    @ViewInject(R.id.tv_pwd)
    private TextView tv_pwd;
    @ViewInject(R.id.tv_push_url)
    private TextView tv_push_url;
    @ViewInject(R.id.tv_pull_url)
    private TextView tv_pull_url;
    @ViewInject(R.id.imageView)
    private ImageView imageView;
    private ClassRoom room;
    private BitmapUtils bitmapUtils;
    private Uri imageUri;

    @OnClick({R.id.tv_copy, R.id.tv_edit, R.id.tv_share, R.id.imageView})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_copy:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(tv_push_url.getText().toString());
                Toast.makeText(this, "复制成功", Toast.LENGTH_LONG).show();
                break;
            case R.id.tv_edit:
                if (room != null) {
                    showEditRoomPwd();
                }
                break;
            case R.id.tv_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "这是我的直播间拉流地址:" + tv_pull_url.getText());
                intent.setType("text/plain");
                //设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(intent, "分享到"));
                break;
            case R.id.imageView:
                systemPhoto();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);
        ViewUtils.inject(this);
        bitmapUtils = new BitmapUtils(this);
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "房间信息";
        setToolBar(R.id.toolbar, options);
        imageUri = Uri.parse(IMAGE_FILE_LOCATION);
        room = (ClassRoom) getIntent().getSerializableExtra("room");
        bitmapUtils.display(imageView, room.getImageUrl());

        tv_name.setText(room.getName());
        tv_pwd.setText(room.getRoomPwd());
        tv_push_url.setText(room.getPushUrl());
        tv_pull_url.setText(room.getRtmpPullUrl());
    }

    /**
     * 修改文件夹对话框
     */
    public void showEditRoomPwd() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dView = View.inflate(this, R.layout.dialog_edit_room_pwd, null);
        final android.app.AlertDialog dialog = builder.create();
        final EditText et_pwd = (EditText) dView.findViewById(R.id.et_pwd);
        et_pwd.setText(room.getRoomPwd());
        dialog.setView(dView, 0, 0, 0, 0);
        dialog.show();
        //确认按钮监听
        dView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pwd = et_pwd.getText().toString().trim();
                if (!TextUtils.isEmpty(pwd)) {
                    room.setRoomPwd(pwd);
                    room.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                MyUtils.showToast(RoomInfoActivity.this, "修改密码成功");
                                dialog.dismiss();
                            }else {
                                switch (e.getErrorCode()){
                                    case 100:
                                        MyUtils.showToast(RoomInfoActivity.this, "修改密码成功");
                                        dialog.dismiss();
                                        break;
                                    default:
                                        MyUtils.showToast(RoomInfoActivity.this, "修改密码失败");
                                        break;
                                }
                                Log.e(TAG,e.getErrorCode()+e.getMessage());
                            }
                        }
                    });
                } else {
                    MyUtils.showToast(RoomInfoActivity.this, "房间密码不能为空");
                }
            }
        });
        dView.findViewById(R.id.bt_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 打开系统相册
     */
    private void systemPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SYS_INTENT_REQUEST);
    }

    //调用系图库后执行相关操作
    private void photoCamera(Intent data) {
//        Uri uri = data.getData();
        Uri uri = null;
        if (data != null) {
            uri = data.getData();
            System.out.println("Data");
        } else {
            System.out.println("File");
            String fileName = getSharedPreferences("temp", Context.MODE_WORLD_WRITEABLE).getString("tempName", "");
            uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileName));
        }
        cropImage(uri, 1600, 800, CROP_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SYS_INTENT_REQUEST:
                    photoCamera(data);
                    break;
                case CROP_PICTURE:
                    Bitmap photo = null;
                    Uri photoUri = data.getData();
                    if (photoUri != null) {
                        photo = BitmapFactory.decodeFile(photoUri.getPath());
                        Log.e("myTest", photoUri.getPath());
                    }
                    if (photo == null) {
                        Bundle extra = data.getExtras();
                        if (extra != null) {
                            photo = (Bitmap) extra.get("data");
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            saveBitmap(photo);
                        }
                        Log.e("myTest", photo.getWidth() + "");
                    }
//                    imageView.setImageBitmap(photo);
                    break;
            }
        }
    }

    /**
     * 保存方法
     */
    public void saveBitmap(Bitmap bitmap) {
        File f = new File("/sdcard/", "temp.jpg");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            compressSave(f.getAbsolutePath());
            Log.i(TAG, "已经保存" + f.getAbsolutePath());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //截取图片
    public void cropImage(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, requestCode);
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
                    PictureUtil.getAlbumDir(), "small_room" + f.getName()));
            bm.compress(Bitmap.CompressFormat.JPEG, 40, fos);
//            Toast.makeText(getActivity(), "压缩成功，已保存至" + PictureUtil.getAlbumDir(), Toast.LENGTH_SHORT).show();
            String picPath = PictureUtil.getAlbumDir() + "/small_room" + f.getName();
            final BmobFile bmobFile = new BmobFile(new File(picPath));
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        room.setImageUrl(bmobFile.getFileUrl());
                        room.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    MyUtils.showToast(RoomInfoActivity.this, "上传成功");
                                    bitmapUtils.display(imageView, room.getImageUrl());
                                } else {
                                    MyUtils.showToast(RoomInfoActivity.this, e.getErrorCode() + e.getMessage());
                                }
                            }
                        });
                    } else {
                        MyUtils.showToast(RoomInfoActivity.this, e.getErrorCode() + e.getMessage());
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
}
