package com.netease.nim.demo.common.util;

import android.os.AsyncTask;

import com.netease.nim.demo.main.helper.CheckSumBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.Date;
import java.util.Map;

/**
 * 获取频道列表的服务
 * <p/>
 * Created by huangjun on 2015/3/6.
 */
public class MyHttpClient extends AsyncTask<Map, Float, String> {
    private static final String TAG = "MyHttpClient";
    private String  json;
    private String  mUrl;
    public MyHttpClient(String url,String  json) {
        this.json=json;
        this.mUrl=url;
    }
    @Override
    protected String doInBackground(Map... params) {
//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = mUrl;
        HttpPost httpPost = new HttpPost(url);
        String appKey = "9a69511ec44840b9b9dff9d0e57d5449";
        String appSecret = "16ba19d26ee84dbb82d4cc9c34bc208f";
        String nonce = "12345";
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime);//参考 计算CheckSum的java代码
        // 设置请求的header
        httpPost.addHeader("AppKey", appKey);
        httpPost.addHeader("Nonce", nonce);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
        try {
//          // 设置请求的参数
            httpPost.setEntity(new StringEntity(json,"utf-8"));
            // 执行请求
            HttpResponse response = httpClient.execute(httpPost);
            // 打印执行结果
            String s=EntityUtils.toString(response.getEntity(), "utf-8");
            return s;
        } catch (Exception e) {
            return "{\"msg\":\"Exception\",\"code\":400}";
        }
    }
}
