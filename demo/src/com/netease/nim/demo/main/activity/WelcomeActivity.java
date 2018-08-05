package com.netease.nim.demo.main.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.avchat.activity.AVChatActivity;
import com.netease.nim.demo.common.entity.bmob.Update;
import com.netease.nim.demo.common.util.sys.SysInfoUtil;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.login.LoginActivity;
import com.netease.nim.demo.main.model.Extras;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * 欢迎/导航页（app启动Activity）
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
public class WelcomeActivity extends UI {

    private static final String TAG = "WelcomeActivity";
    private String pkName;
    private String versionName;
    private boolean customSplash = false;
    private TextView tv_version;
    private static boolean firstEnter = true; // 是否首次进入
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        tv_version = findView(R.id.tv_version);
        progressBar = findView(R.id.progressBar);
        if (savedInstanceState != null) {
            setIntent(new Intent()); // 从堆栈恢复，不再重复解析之前的intent
        }
        Log.e(TAG,"firstEnter"+firstEnter);
        if (!firstEnter) {
            onIntent();
        } else {
            showSplashView();
        }
        myInit();
    }

    private void myInit() {
        try {
            pkName = this.getPackageName();
            versionName = this.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            Log.e(TAG, pkName + versionName);
            tv_version.setText("v" + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        BmobQuery<Update> query = new BmobQuery<>();
        query.addWhereEqualTo("packageName", pkName);
        query.findObjects(new FindListener<Update>() {
            @Override
            public void done(List<Update> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        Update update = list.get(0);
                        if (update.isUpdate()) {
                            showUpdateDialog(update);
                        } else {
                            //进入主页
                            enterHome();
                        }
                    } else {
                        creatVersionUpdate(pkName, versionName);
                    }
                } else {
                    enterHome();

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (firstEnter) {
            firstEnter = false;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (canAutoLogin()) {
                        onIntent();
                    } else {
                        LoginActivity.start(WelcomeActivity.this);
                        finish();
                    }
                }
            };
            if (customSplash) {
                new Handler().postDelayed(runnable, 1000);
            } else {
                runnable.run();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        /**
         * 如果Activity在，不会走到onCreate，而是onNewIntent，这时候需要setIntent
         * 场景：点击通知栏跳转到此，会收到Intent
         */
        setIntent(intent);
        onIntent();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }

    // 处理收到的Intent
    private void onIntent() {
        LogUtil.i(TAG, "onIntent...");

        if (TextUtils.isEmpty(DemoCache.getAccount())) {
            // 判断当前app是否正在运行
            if (!SysInfoUtil.stackResumed(this)) {
                LoginActivity.start(this);
            }
            finish();
        } else {
            // 已经登录过了，处理过来的请求
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                    parseNotifyIntent(intent);
                    return;
                } else if (intent.hasExtra(Extras.EXTRA_JUMP_P2P) || intent.hasExtra(AVChatActivity.INTENT_ACTION_AVCHAT)) {
                    parseNormalIntent(intent);
                }
            }

            if (!firstEnter && intent == null) {
                finish();
            } else {
                showMainActivity();
            }
        }
    }

    /**
     * 已经登陆过，自动登陆
     */
    private boolean canAutoLogin() {
        String account = Preferences.getUserAccount();
        String token = Preferences.getUserToken();

        Log.i(TAG, "get local sdk token =" + token);
//        return !TextUtils.isEmpty(account) && !TextUtils.isEmpty(token);
        return false;
    }

    private void parseNotifyIntent(Intent intent) {
        ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        if (messages == null || messages.size() > 1) {
            showMainActivity(null);
        } else {
            showMainActivity(new Intent().putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, messages.get(0)));
        }
    }

    private void parseNormalIntent(Intent intent) {
        showMainActivity(intent);
    }

    /**
     * 首次进入，打开欢迎界面
     */
    private void showSplashView() {
        getWindow().setBackgroundDrawableResource(R.drawable.splash_bg);
        customSplash = true;
    }

    private void showMainActivity() {
        showMainActivity(null);
    }

    private void showMainActivity(Intent intent) {
//        MainActivity.start(WelcomeActivity.this, intent);
        MyMainActivity.start(WelcomeActivity.this, intent);
        finish();
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
                    BmobFile file = update.getFile();
                    final File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
                    file.download(saveFile, new DownloadFileListener() {

                        @Override
                        public void onStart() {
//                            toast("开始下载...");
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void done(String savePath, BmobException e) {
                            if (e == null) {
                                Log.e(TAG, "下载成功,保存路径:" + savePath);
//                                下载成功,保存路径:/data/data/com.sst.admin/cache/bmob/app-debug.apk
                                install(saveFile);
                                finish();
                            } else {
                                Log.e(TAG, "下载失败：" + e.getErrorCode() + "," + e.getMessage());
                            }
                        }

                        @Override
                        public void onProgress(Integer value, long newworkSpeed) {
//                            Log.i("bmob","下载进度："+value+","+newworkSpeed);
                            progressBar.setMax(100);
                            progressBar.setProgress(value);
                        }
                    });
                } else {
                    Toast.makeText(WelcomeActivity.this, "未检测到sd卡", Toast.LENGTH_SHORT);
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
//        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
//        finish();
        LoginActivity.start(WelcomeActivity.this);
    }

    private void install(File t) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(t),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void creatVersionUpdate(String pkName, String versionName) {
        Update update = new Update();
        update.setPackageName(pkName);
        update.setVersion(versionName);
        update.setUpdate(false);
        update.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    enterHome();
                } else {
                    Log.e(TAG, e.getErrorCode() + e.getMessage());
                }
            }
        });
    }
}
