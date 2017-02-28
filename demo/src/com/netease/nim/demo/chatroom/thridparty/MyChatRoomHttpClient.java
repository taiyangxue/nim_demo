package com.netease.nim.demo.chatroom.thridparty;

import android.os.AsyncTask;

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
 * 获取频道列表的服务
 * <p/>
 * Created by huangjun on 2015/3/6.
 */
public class MyChatRoomHttpClient extends AsyncTask<Map, Float, String> {
    private static final String TAG ="MyChatRoomHttpClient" ;
    private List<NameValuePair> list;
    private HttpEntity entity;

    public MyChatRoomHttpClient(List<NameValuePair> list) {
        this.list=list;
    }

    @Override
    protected String doInBackground(Map... params) {
//
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = "https://vcloud.163.com/app/channel/create";
        HttpPost httpPost = new HttpPost(url);

        String appKey = "94kid09c9ig9k1loimjg012345123456";
        String appSecret = "123456789012";
        String nonce =  "1";
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", appKey);
        httpPost.addHeader("Nonce", nonce);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/json;charset=utf-8");

//        // 设置请求的参数
//        StringEntity params = new StringEntity("{\"name\":\"netease_vcloud\", \"type\":0}",Consts.UTF_8);
//        httpPost.setEntity(params);
//
//        // 执行请求
//        HttpResponse response = httpClient.execute(httpPost);
//
//        // 打印执行结果
//        System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(this.list, "utf-8"));
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
