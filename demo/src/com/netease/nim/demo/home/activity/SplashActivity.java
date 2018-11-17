package com.netease.nim.demo.home.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.Update;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.login.LoginActivity;
import com.netease.nim.demo.main.activity.MyMainActivity;

import java.io.File;
import java.util.List;



public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private String pkName;
    private String versionName;
    @ViewInject(R.id.tv_version)
    private TextView tv_version;
    @ViewInject(R.id.progressBar)
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        ViewUtils.inject(this);
        try {
            pkName = this.getPackageName();
            versionName = this.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            Log.e(TAG, pkName + versionName);
            tv_version.setText("v"+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        enterHome();
    }

    protected void showUpdateDialog(final Update update) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("版本更新");
        builder.setMessage(update.getDesc());
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("更新升级", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    //下载文件
//                    BmobFile file = update.getFile();
//                    final File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
//                    file.download(saveFile, new DownloadFileListener() {
//
//                        @Override
//                        public void onStart() {
////                            toast("开始下载...");
//                            progressBar.setVisibility(View.VISIBLE);
//                        }
//
//                        @Override
//                        public void done(String savePath, BmobException e) {
//                            if (e == null) {
//                                Log.e(TAG, "下载成功,保存路径:" + savePath);
////                                下载成功,保存路径:/data/data/com.sst.admin/cache/bmob/app-debug.apk
//                                install(saveFile);
//                                finish();
//                            } else {
//                                Log.e(TAG, "下载失败：" + e.getErrorCode() + "," + e.getMessage());
//                            }
//                        }
//
//                        @Override
//                        public void onProgress(Integer value, long newworkSpeed) {
////                            Log.i("bmob","下载进度："+value+","+newworkSpeed);
//                            progressBar.setMax(100);
//                            progressBar.setProgress(value);
//                        }
//                    });
                } else {
                    Toast.makeText(SplashActivity.this, "未检测到sd卡", Toast.LENGTH_SHORT);
                }
            }
        });
        builder.setNegativeButton("忽略本次", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void enterHome() {
        if(SharedPreferencesUtils.getBoolean(SplashActivity.this,"isLogin",false)){
//            DemoCache.setAccount(account);
            MyMainActivity.start(SplashActivity.this, null);
            finish();
        }else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
//        DemoCache.setAccount(account);
//        SharedPreferencesUtils.setString(LoginActivity.this, "grade", userinfoBean.getGrade());
//        SharedPreferencesUtils.setInt(LoginActivity.this, "account_id", userinfoBean.getId());
//        saveLoginInfo(account, token);
//        SharedPreferencesUtils.setBoolean(LoginActivity.this,"isLogin",true);
//        // 初始化消息提醒配置
//        initNotificationConfig();
//        // 进入主界面
//        MyMainActivity.start(LoginActivity.this, null);
    }

    private void install(File t) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(t),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

//    private void creatVersionUpdate(String pkName, String versionName) {
//        Update update = new Update();
//        update.setPackageName(pkName);
//        update.setVersion(versionName);
//        update.setUpdate(false);
//        update.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                if (e == null) {
//                    enterHome();
//                } else {
//                    Log.e(TAG, e.getErrorCode() + e.getMessage());
//                }
//            }
//        });
//    }
//    private String getAppInfo() {
//        try {
//            String pkName = this.getPackageName();
//            String versionName = this.getPackageManager().getPackageInfo(
//                    pkName, 0).versionName;
//            int versionCode = this.getPackageManager()
//                    .getPackageInfo(pkName, 0).versionCode;
//            return pkName + "   " + versionName + "  " + versionCode;
//        } catch (Exception e) {
//        }
//        return null;
//    }
}
