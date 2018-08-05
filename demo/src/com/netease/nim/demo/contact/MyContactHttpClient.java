package com.netease.nim.demo.contact;

import android.os.AsyncTask;
import android.util.Log;

import com.netease.nim.demo.main.helper.CheckSumBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 通讯录数据获取协议的实现
 * <p/>
 * Created by huangjun on 2015/3/6.
 */
public class MyContactHttpClient extends AsyncTask<Map, Float, String> {
    private static final String TAG ="MyChatRoomHttpClient" ;
    private List<NameValuePair> list;
    private HttpEntity entity;

    public MyContactHttpClient(List<NameValuePair> list) {
        this.list=list;
    }

    @Override
    protected String doInBackground(Map... params) {
//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = "https://api.netease.im/nimserver/user/create.action";
//        String url = "https://api.netease.im/nimserver/chatroom/create.action";
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
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(this.list, "utf-8"));
            for(NameValuePair nameValuePair:list){
                Log.e(TAG,nameValuePair.getName()+nameValuePair.getValue());
            }
            HttpResponse response = httpClient.execute(httpPost);
            // 打印执行结果
//            System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
            String s=EntityUtils.toString(response.getEntity(), "utf-8");

//                    {"code":200,"info":{"token":"346351","accid":"15518784041","name":"sun2"}}
            return s;
        } catch (Exception e) {
//            e.printStackTrace();
            return "{\"desc\":\"Exception\",\"code\":400}";
        }
    }
}
