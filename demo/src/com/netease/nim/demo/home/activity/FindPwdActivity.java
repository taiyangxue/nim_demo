package com.netease.nim.demo.home.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.demo.R;
import com.netease.nim.demo.common.util.ApiListener;
import com.netease.nim.demo.common.util.ApiUtils;
import com.netease.nim.demo.common.util.CountDownTimerUtils;
import com.netease.nim.demo.common.util.MyUtils;
import com.netease.nim.demo.contact.MyResetPwdContactHttpClient;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nim.uikit.model.ToolBarOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class FindPwdActivity extends UI {
    private static final String TAG = "FindPwdActivity";
    private ClearableEditTextWithIcon edit_find_account;
    private ClearableEditTextWithIcon edit_find_password;
    private ClearableEditTextWithIcon edit_find_password2;
    private ClearableEditTextWithIcon edit_find_yanzheng;
    private TextView tv_get_yanzheng;
    private TextView tv_find_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);
        ToolBarOptions options = new ToolBarOptions();
        options.titleString = "找回密码";
        setToolBar(R.id.toolbar, options);
        findViews();
    }

    private void findViews() {
        edit_find_account = findView(R.id.edit_find_account);
        edit_find_password = findView(R.id.edit_find_password);
        edit_find_password2 = findView(R.id.edit_find_password2);
        edit_find_yanzheng = findView(R.id.edit_find_yanzheng);

        edit_find_account.setIconResource(R.drawable.login_username);
        edit_find_password.setIconResource(R.drawable.login_pwd);
        edit_find_password2.setIconResource(R.drawable.login_pwd);
        edit_find_yanzheng.setIconResource(R.drawable.login_code);


        tv_get_yanzheng = findView(R.id.tv_get_yanzheng);
        tv_find_ok = findView(R.id.tv_find_ok);
        tv_find_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkRegisterContentValid()) {
                    return;
                }
                if (!NetworkUtil.isNetAvailable(FindPwdActivity.this)) {
                    Toast.makeText(FindPwdActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                    return;
                }
//                updatePwd(edit_find_account.getText().toString().trim(),
//                        edit_find_password.getText().toString().trim());
                //检验验证码.
                String yanzheng = edit_find_yanzheng.getText().toString().trim();
                final String account=edit_find_account.getText().toString().trim();
                final String pwd=edit_find_password.getText().toString().trim();
                ApiUtils.getInstance().user_resetpwd(account, pwd, yanzheng, new ApiListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                            MyUtils.showToast(FindPwdActivity.this, "密码修改成功");
                            //返回登陆界面
                            finish();
                    }

                    @Override
                    public void onFailed(String errorMsg) {
                        MyUtils.showToast(FindPwdActivity.this,errorMsg);
                        DialogMaker.dismissProgressDialog();
                    }
                });
            }
        });
        tv_get_yanzheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动倒计时
                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(tv_get_yanzheng, 60000, 1000);
                mCountDownTimerUtils.start();
                //发送验证码
                ApiUtils.getInstance().sms_send(edit_find_account.getText().toString().trim(), "resetpwd", new ApiListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        MyUtils.showToast(FindPwdActivity.this,"验证码发送成功");
                    }

                    @Override
                    public void onFailed(String errorMsg) {
                        MyUtils.showToast(FindPwdActivity.this,errorMsg);
                    }
                });
            }
        });
    }

    private void updatePwd(final String username, final String pwd) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("accid", username));
        nvps.add(new BasicNameValuePair("token", pwd));
        new MyResetPwdContactHttpClient(nvps) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(TAG, s);
                try {
                    JSONObject jsonObj = new JSONObject(s);
                    if (200 == jsonObj.getInt("code")) {
                        MyUtils.showToast(FindPwdActivity.this, "密码修改成功");
                        //返回登陆界面
                        finish();
                    } else {
                        Toast.makeText(FindPwdActivity.this, jsonObj.getString("desc"), Toast.LENGTH_SHORT).show();
                        DialogMaker.dismissProgressDialog();
                    }
                } catch (JSONException e) {
                    Toast.makeText(FindPwdActivity.this, "JSON解析异常", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private boolean checkRegisterContentValid() {
        // 帐号检查
        String account = edit_find_account.getText().toString().trim();
        if (!MyUtils.isMobileNumber(account)) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 密码检查
        String password = edit_find_password.getText().toString().trim();
        if (password.length() < 6 || password.length() > 20) {
            Toast.makeText(this, R.string.register_password_tip, Toast.LENGTH_SHORT).show();
            return false;
        }
        String password2 = edit_find_password.getText().toString().trim();
        if (!password.equals(password2)) {
            Toast.makeText(this, "俩次密码输入不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
