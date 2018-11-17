package com.netease.nim.demo.login;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.UserLoginBean;
import com.netease.nim.demo.common.entity.VersionUpdateBean;
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.CountDownTimerUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.config.preference.UserPreferences;
import com.netease.nim.demo.home.activity.FindPwdActivity;
import com.netease.nim.demo.main.activity.MyMainActivity;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.common.util.string.MD5;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nim.uikit.permission.MPermission;
import com.netease.nim.uikit.permission.annotation.OnMPermissionDenied;
import com.netease.nim.uikit.permission.annotation.OnMPermissionGranted;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import util.UpdateAppUtils;


/**
 * 登录/注册界面
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
public class LoginActivity extends UI implements OnKeyListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String KICK_OUT = "KICK_OUT";
    private final int BASIC_PERMISSION_REQUEST_CODE = 110;

    private TextView rightTopBtn;  // ActionBar完成按钮
    private TextView switchModeBtn;  // 注册/登录切换按钮
    private TextView tv_find_pwd;  // 注册/登录切换按钮
    private TextView button_login;  //登录按钮

    private ClearableEditTextWithIcon loginAccountEdit;
    private ClearableEditTextWithIcon loginPasswordEdit;

    private ClearableEditTextWithIcon registerAccountEdit;
    private ClearableEditTextWithIcon registerNickNameEdit;
    private ClearableEditTextWithIcon registerPasswordEdit;
    private ClearableEditTextWithIcon registerPasswordEdit2;
    private ClearableEditTextWithIcon edit_register_yanzheng;

    private View loginLayout;
    private View registerLayout;

    private AbortableFuture<LoginInfo> loginRequest;
    private boolean registerMode = false; // 注册模式
    private boolean registerPanelInited = false; // 注册面板是否初始化
    private Spinner gradeSpinner;
    private int current_grade;
    private TextView tv_get_yanzheng;
    private int current_grade_position;

    public static void start(Context context) {
        start(context, false);
    }

    public static void start(Context context, boolean kickOut) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(KICK_OUT, kickOut);
        context.startActivity(intent);
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return false;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        ToolBarOptions options = new ToolBarOptions();
        options.isNeedNavigate = false;
        options.logoId = R.drawable.actionbar_white_logo_space;
        setToolBar(R.id.toolbar, options);
        requestBasicPermission();
        onParseIntent();
        initRightTopBtn();
        setupLoginPanel();
        setupRegisterPanel();

    }

    /**
     * 基本权限管理
     */
    private void requestBasicPermission() {
        MPermission.with(LoginActivity.this)
                .addRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
//        Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
//        Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
    }

    private void onParseIntent() {
//        if (getIntent().getBooleanExtra(KICK_OUT, false)) {
//            int type = NIMClient.getService(AuthService.class).getKickedClientType();
//            String client;
//            switch (type) {
//                case ClientType.Web:
//                    client = "网页端";
//                    break;
//                case ClientType.Windows:
//                    client = "电脑端";
//                    break;
//                case ClientType.REST:
//                    client = "服务端";
//                    break;
//                default:
//                    client = "移动端";
//                    break;
//            }
//            EasyAlertDialogHelper.showOneButtonDiolag(LoginActivity.this, getString(R.string.kickout_notify),
//                    String.format(getString(R.string.kickout_content), client), getString(R.string.ok), true, null);
//        }
    }

    /**
     * ActionBar 右上角按钮
     */
    private void initRightTopBtn() {
        rightTopBtn = addRegisterRightTopBtn(this, R.string.login);
        rightTopBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (registerMode) {
                    register();
                } else {
                    //fakeLoginTest(); // 假登录代码示例
                    login();
                }
            }
        });
    }

    /**
     * 登录面板
     */
    private void setupLoginPanel() {
        loginAccountEdit = findView(R.id.edit_login_account);
        loginPasswordEdit = findView(R.id.edit_login_password);
        button_login = findView(R.id.button_login);
        button_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        loginAccountEdit.setIconResource(R.drawable.login_username);
        loginPasswordEdit.setIconResource(R.drawable.login_pwd);

        loginAccountEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        loginPasswordEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        loginAccountEdit.addTextChangedListener(textWatcher);
        loginPasswordEdit.addTextChangedListener(textWatcher);
        loginPasswordEdit.setOnKeyListener(this);

        String account = Preferences.getUserAccount();
        loginAccountEdit.setText(account);
    }

    /**
     * 注册面板
     */
    private void setupRegisterPanel() {
        loginLayout = findView(R.id.login_layout);
        registerLayout = findView(R.id.register_layout);
        switchModeBtn = findView(R.id.register_login_tip);
        tv_find_pwd = findView(R.id.tv_find_pwd);
        tv_find_pwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到找回密码界面
                startActivity(new Intent(LoginActivity.this, FindPwdActivity.class));
            }
        });
        switchModeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMode();
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // 更新右上角按钮状态
            if (!registerMode) {
                // 登录模式
                boolean isEnable = loginAccountEdit.getText().length() > 0
                        && loginPasswordEdit.getText().length() > 0;
                updateRightTopBtn(LoginActivity.this, rightTopBtn, isEnable);
            }
        }
    };

    private void updateRightTopBtn(Context context, TextView rightTopBtn, boolean isEnable) {
        rightTopBtn.setText(R.string.done);
        rightTopBtn.setBackgroundResource(R.drawable.g_white_btn_selector);
        rightTopBtn.setEnabled(isEnable);
        rightTopBtn.setTextColor(context.getResources().getColor(R.color.color_blue_0888ff));
        rightTopBtn.setPadding(ScreenUtil.dip2px(10), 0, ScreenUtil.dip2px(10), 0);
    }

    /**
     * ***************************************** 登录 **************************************
     */

    private void login() {
        DialogMaker.showProgressDialog(this, null, getString(R.string.logining), true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (loginRequest != null) {
                    loginRequest.abort();
                    onLoginDone();
                }
            }
        }).setCanceledOnTouchOutside(false);

        // 云信只提供消息通道，并不包含用户资料逻辑。开发者需要在管理后台或通过服务器接口将用户帐号和token同步到云信服务器。
        // 在这里直接使用同步到云信服务器的帐号和token登录。
        // 这里为了简便起见，demo就直接使用了密码的md5作为token。
        // 如果开发者直接使用这个demo，只更改appkey，然后就登入自己的账户体系的话，需要传入同步到云信服务器的token，而不是用户密码。
        final String account = loginAccountEdit.getEditableText().toString().toLowerCase();
        final String token = tokenFromPassword(loginPasswordEdit.getEditableText().toString());
//        // 登录
//        loginRequest = NimUIKit.doLogin(new LoginInfo(account, token), new RequestCallback<LoginInfo>() {
//            @Override
//            public void onSuccess(LoginInfo param) {
//                Log.i(TAG, "login success");
                ApiUtils.getInstance().user_login(account, token, new ApiListener<UserLoginBean.DataBean.UserinfoBean>() {
                    @Override
                    public void onSuccess(UserLoginBean.DataBean.UserinfoBean userinfoBean) {
                        onLoginDone();
                        DemoCache.setAccount(account);
                        SharedPreferencesUtils.setString(LoginActivity.this, "grade", userinfoBean.getGrade());
                        SharedPreferencesUtils.setInt(LoginActivity.this, "account_id", userinfoBean.getId());
                        saveLoginInfo(account, token);
                        SharedPreferencesUtils.setBoolean(LoginActivity.this,"isLogin",true);
                        // 初始化消息提醒配置
                        initNotificationConfig();
                        // 进入主界面
                        MyMainActivity.start(LoginActivity.this, null);
                        finish();
                    }

                    @Override
                    public void onFailed(String errorMsg) {
                        MyUtils.showToast(LoginActivity.this, errorMsg);
                    }
                });
//                MyUser.loginByAccount(account, token, new LogInListener<MyUser>() {
//                    @Override
//                    public void done(MyUser user, BmobException e) {
//                        if (user != null&&user.getUserType()!=1) {
//                            Log.i("Bmob", "用户登陆成功");
//                            onLoginDone();
//                            //绑定手机号
//                            if(!user.getMobilePhoneNumberVerified()){
//                                user.setMobilePhoneNumber(account);
//                                user.setMobilePhoneNumberVerified(true);
//                                user.update(new UpdateListener() {
//
//                                    @Override
//                                    public void done(BmobException e) {
//                                        if(e==null){
//                                            Log.e(TAG,"手机号码绑定成功");
//                                        }else{
//                                            Log.e(TAG,"失败:" + e.getMessage());
//                                        }
//                                    }
//                                });
//                            }
//
//                            DemoCache.setAccount(account);
//                            SharedPreferencesUtils.setString(LoginActivity.this,"grade",user.getGrade());
//                            saveLoginInfo(account, token);
//
//                            // 初始化消息提醒配置
//                            initNotificationConfig();
//                          // 进入主界面
//                            MyMainActivity.start(LoginActivity.this, null);
//                            finish();
//                        } else {
//                            onLoginDone();
//                            Log.i("Bmob", e.getErrorCode()+e.getMessage());
//                            MyUtils.showToast(LoginActivity.this,"用户不存在");
////                            MyUtils.showToast(LoginActivity.this, e.getErrorCode() + e.getMessage());
//                        }
//                    }
//                });
            }

            //
//            @Override
//            public void onFailed(int code) {
//                onLoginDone();
//                if (code == 302 || code == 404) {
//                    Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(LoginActivity.this, "登录失败: " + code, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//                Toast.makeText(LoginActivity.this, R.string.login_exception, Toast.LENGTH_LONG).show();
//                onLoginDone();
//            }
//        });
//    }

    private void initNotificationConfig() {
        // 初始化消息提醒
        NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

        // 加载状态栏配置
        StatusBarNotificationConfig statusBarNotificationConfig = UserPreferences.getStatusConfig();
        if (statusBarNotificationConfig == null) {
            statusBarNotificationConfig = DemoCache.getNotificationConfig();
            UserPreferences.setStatusConfig(statusBarNotificationConfig);
        }
        // 更新配置
        NIMClient.updateStatusBarNotificationConfig(statusBarNotificationConfig);
    }

    private void onLoginDone() {
        loginRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    private void saveLoginInfo(final String account, final String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }

    //DEMO中使用 username 作为 NIM 的account ，md5(password) 作为 token
    //开发者需要根据自己的实际情况配置自身用户系统和 NIM 用户系统的关系
    private String tokenFromPassword(String password) {
        String appKey = readAppKey(this);
        boolean isDemo = "45c6af3c98409b18a84451215d0bdd6e".equals(appKey)
                || "fe416640c8e8a72734219e1847ad2547".equals(appKey);
        return isDemo ? MD5.getStringMD5(password) : password;
    }

    private static String readAppKey(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null) {
                return appInfo.metaData.getString("com.netease.nim.appKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ***************************************** 注册 **************************************
     */

    private void register() {
        if (!registerMode || !registerPanelInited) {
            return;
        }

        if (!checkRegisterContentValid()) {
            return;
        }

        if (!NetworkUtil.isNetAvailable(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
            return;
        }
        //检验验证码.
        final String yanzheng = edit_register_yanzheng.getText().toString().trim();
        DialogMaker.showProgressDialog(LoginActivity.this, getString(R.string.registering), false);
        // 注册流程
        final String account = registerAccountEdit.getText().toString();
        final String nickName = registerNickNameEdit.getText().toString();
        final String password = registerPasswordEdit.getText().toString();
        // 设置请求的参数
        ApiUtils.getInstance().user_register(account, nickName, password, yanzheng,current_grade+"", new ApiListener<String>() {
            @Override
            public void onSuccess(String s) {
                switchMode();  // 切换回登录
                loginAccountEdit.setText(account);
                loginPasswordEdit.setText(password);
                registerAccountEdit.setText("");
                registerNickNameEdit.setText("");
                registerPasswordEdit.setText("");
                DialogMaker.dismissProgressDialog();
            }

            @Override
            public void onFailed(String errorMsg) {
                MyUtils.showToast(LoginActivity.this, errorMsg);
                DialogMaker.dismissProgressDialog();
            }
        });

    }


    private boolean checkRegisterContentValid() {
        if (!registerMode || !registerPanelInited) {
            return false;
        }
        // 帐号检查
        String account = registerAccountEdit.getText().toString().trim();
//        if (account.length() <= 0 || account.length() > 20) {
//            Toast.makeText(this, R.string.register_account_tip, Toast.LENGTH_SHORT).show();
//
//            return false;
//        }
        if (current_grade_position == 0) {
            MyUtils.showToast(LoginActivity.this, "请选择年级");
            return false;
        }
        if (!MyUtils.isMobileNumber(account)) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 昵称检查
        String nick = registerNickNameEdit.getText().toString().trim();
        if (nick.length() <= 0 || nick.length() > 10) {
            Toast.makeText(this, R.string.register_nick_name_tip, Toast.LENGTH_SHORT).show();
            return false;
        }

        // 密码检查
        String password = registerPasswordEdit.getText().toString().trim();
        if (password.length() < 6 || password.length() > 20) {
            Toast.makeText(this, R.string.register_password_tip, Toast.LENGTH_SHORT).show();
            return false;
        }
        String password2 = registerPasswordEdit2.getText().toString().trim();
        if (!password.equals(password2)) {
            Toast.makeText(this, "俩次密码输入不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * ***************************************** 注册/登录切换 **************************************
     */
    private static final String[] m = {"请选择年级", "四年级", "五年级", "六年级", "初一", "初二", "初三", "高一", "高二", "高三"};
    private static final int[] grade={13,4,5,6,7,8,9,10,11,12};
    private ArrayAdapter<String> spinner_adapter;

    private void switchMode() {
        registerMode = !registerMode;

        if (registerMode && !registerPanelInited) {
            registerAccountEdit = findView(R.id.edit_register_account);
            registerNickNameEdit = findView(R.id.edit_register_nickname);
            registerPasswordEdit = findView(R.id.edit_register_password);
            registerPasswordEdit2 = findView(R.id.edit_register_password2);
            edit_register_yanzheng = findView(R.id.edit_register_yanzheng);
            tv_get_yanzheng = findView(R.id.tv_get_yanzheng);
            tv_get_yanzheng.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //启动倒计时
                    if (checkRegisterContentValid()) {
                        CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(tv_get_yanzheng, 60000, 1000);
                        mCountDownTimerUtils.start();
                        //发送验证码
                        ApiUtils.getInstance().sms_send(registerAccountEdit.getText().toString().trim(), "register", new ApiListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                MyUtils.showToast(LoginActivity.this,"验证码发送成功");
                            }

                            @Override
                            public void onFailed(String errorMsg) {
                                MyUtils.showToast(LoginActivity.this,errorMsg);
                            }
                        });
                    }

                }
            });
            gradeSpinner = findView(R.id.spiner);
            //将可选内容与ArrayAdapter连接起来
//            spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m);
            spinner_adapter = new ArrayAdapter<String>(this, R.layout.my_spinner_item, m);

            //设置下拉列表的风格
            spinner_adapter.setDropDownViewResource(R.layout.my_spinner_dropdown_item);

            //将adapter 添加到spinner中
            gradeSpinner.setAdapter(spinner_adapter);

            //添加事件Spinner事件监听
            gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    current_grade = grade[position];
                    current_grade_position = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //设置默认值
            gradeSpinner.setVisibility(View.VISIBLE);
//            tv_find_pwd.setVisibility(View.GONE);
            registerAccountEdit.setIconResource(R.drawable.login_username);
            registerNickNameEdit.setIconResource(R.drawable.login_nick);
            registerPasswordEdit.setIconResource(R.drawable.login_pwd);
            registerPasswordEdit2.setIconResource(R.drawable.login_pwd);
            edit_register_yanzheng.setIconResource(R.drawable.login_code);

            registerAccountEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            registerNickNameEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
            registerPasswordEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            registerPasswordEdit2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            edit_register_yanzheng.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

            registerAccountEdit.addTextChangedListener(textWatcher);
            registerNickNameEdit.addTextChangedListener(textWatcher);
            registerPasswordEdit.addTextChangedListener(textWatcher);
            registerPasswordEdit2.addTextChangedListener(textWatcher);
            edit_register_yanzheng.addTextChangedListener(textWatcher);

            registerPanelInited = true;
        }

        setTitle(registerMode ? R.string.register : R.string.login);
        loginLayout.setVisibility(registerMode ? View.GONE : View.VISIBLE);
        registerLayout.setVisibility(registerMode ? View.VISIBLE : View.GONE);
        switchModeBtn.setText(registerMode ? R.string.login_has_account : R.string.register);
        if (registerMode) {
            rightTopBtn.setEnabled(true);
        } else {
//            boolean isEnable = loginAccountEdit.getText().length() > 0
//                    && loginPasswordEdit.getText().length() > 0;
//            rightTopBtn.setEnabled(isEnable);
            rightTopBtn.setVisibility(View.GONE);
        }
    }

    public TextView addRegisterRightTopBtn(UI activity, int strResId) {
        String text = activity.getResources().getString(strResId);
        TextView textView = findView(R.id.action_bar_right_clickable_textview);
        textView.setText(text);
        if (textView != null) {
            textView.setBackgroundResource(R.drawable.register_right_top_btn_selector);
            textView.setPadding(ScreenUtil.dip2px(10), 0, ScreenUtil.dip2px(10), 0);
        }
        return textView;
    }

    /**
     * *********** 假登录示例：假登录后，可以查看该用户数据，但向云信发送数据会失败；随后手动登录后可以发数据 **************
     */
    private void fakeLoginTest() {
        // 获取账号、密码；账号用于假登录，密码在手动登录时需要
        final String account = loginAccountEdit.getEditableText().toString().toLowerCase();
        final String token = tokenFromPassword(loginPasswordEdit.getEditableText().toString());

        // 执行假登录
        boolean res = NIMClient.getService(AuthService.class).openLocalCache(account); // SDK会将DB打开，支持查询。
        Log.i("test", "fake login " + (res ? "success" : "start"));

        if (!res) {
            return;
        }

        // Demo缓存当前假登录的账号
        DemoCache.setAccount(account);

        // 初始化消息提醒配置
        initNotificationConfig();

        // 构建缓存
        DataCacheManager.buildDataCacheAsync();

        // 进入主界面，此时可以查询数据（最近联系人列表、本地消息历史、群资料等都可以查询，但当云信服务器发起请求会返回408超时）
        MyMainActivity.start(LoginActivity.this, null);

        // 演示15s后手动登录，登录成功后，可以正常收发数据
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(account, token));
                loginRequest.setCallback(new RequestCallbackWrapper() {
                    @Override
                    public void onResult(int code, Object result, Throwable exception) {
                        Log.i("test", "real login, code=" + code);
                        if (code == ResponseCode.RES_SUCCESS) {
                            saveLoginInfo(account, token);
                            finish();
                        }
                    }
                });
            }
        }, 15 * 1000);
    }
}
