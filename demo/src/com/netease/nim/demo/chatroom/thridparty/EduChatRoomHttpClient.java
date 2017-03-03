package com.netease.nim.demo.chatroom.thridparty;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.common.entity.AddressResult;
import com.netease.nim.demo.common.entity.ChannelCreat;
import com.netease.nim.demo.common.entity.ChannelListResult;
import com.netease.nim.demo.common.entity.ChatroomCreateResult;
import com.netease.nim.demo.config.DemoServers;
import com.netease.nim.demo.main.helper.CheckSumBuilder;
import com.netease.nim.uikit.common.http.NimHttpClient;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网易云信Demo聊天室Http客户端。第三方开发者请连接自己的应用服务器。
 * <p/>
 * Created by huangjun on 2016/2/22.
 */
public class EduChatRoomHttpClient {

    private String errorMsg = "error";
    private static Gson gson;

    public class EnterRoomParam {
        /**
         * 创建房间成功返回的房间号
         */
        private String roomId;
        /**
         * 创建房间成功返回的推流地址
         */
        private String url;

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    private static final String TAG = EduChatRoomHttpClient.class.getSimpleName();

    // code
    private static final int RESULT_CODE_SUCCESS = 200;

    // api
    private static final String API_NAME_CREATE_ROOM = "create.action";
    private static final String API_NAME_FETCH_ADDRESS = "getAddress";
    private static final String API_NAME_CLOSE_ROOM = "close";

    // header
    private static final String HEADER_KEY_APP_KEY = "appkey";
    private static final String HEADER_KEY_CONTENT_TYPE = "Content-type";
    private static final String APP_SECRET = "16ba19d26ee84dbb82d4cc9c34bc208f";
    private static final String NONCE = "12345";
    // result
    private static final String RESULT_KEY_ERROR_MSG = "errmsg";
    private static final String RESULT_KEY_RES = "res";
    private static final String RESULT_KEY_MSG = "msg";
    private static final String RESULT_KEY_TOTAL = "total";
    private static final String RESULT_KEY_LIST = "list";
    private static final String RESULT_KEY_NAME = "name";
    private static final String RESULT_KEY_CREATOR = "creator";
    private static final String RESULT_KEY_STATUS = "status";
    private static final String RESULT_KEY_ANNOUNCEMENT = "announcement";
    private static final String RESULT_KEY_EXT = "ext";
    private static final String RESULT_KEY_ROOM_ID = "roomid";
    private static final String RESULT_KEY_BROADCAST_URL = "broadcasturl";
    private static final String RESULT_KEY_ONLINE_USER_COUNT = "onlineusercount";
    private static final String RESULT_KEY_ADDR = "addr";

    private static final String RESULT_KEY_LIVE = "live";
    private static final String RESULT_KEY_PUSH_URL = "pushUrl";
    private static final String RESULT_KEY_PULL_URL = "rtmpPullUrl";

    // request
    private static final String REQUEST_USER_UID = "uid"; // 用户id
    private static final String REQUEST_ROOM_NAME = "name"; // 直播间名称
    private static final String REQUEST_STREAM_TYPE = "type"; // 推流类型（0:rtmp；1:hls；2:http），默认为0
    private static final String REQUEST_ROOM_ID = "roomid"; // 直播间id
    private static final String REQUEST_CREATOR = "creator"; // 聊天室属主的账号accid

    public interface ChatRoomHttpCallback<T> {
        void onSuccess(T t);

        void onFailed(int code, String errorMsg);
    }

    private static EduChatRoomHttpClient instance;

    public static synchronized EduChatRoomHttpClient getInstance() {
        if (instance == null) {
            instance = new EduChatRoomHttpClient();
        }
        gson = new Gson();
        return instance;
    }

    private EduChatRoomHttpClient() {
        NimHttpClient.getInstance().init(DemoCache.getContext());
    }

    /**
     * 教师创建教室，服务器创建直播频道
     * @param roomName 教室名称
     * @param callback 回调
     */
    public void createClassRoom(String roomName, final ChatRoomHttpCallback<ChannelCreat.RetBean> callback) {
        String url = DemoServers.API_SERVER2 + "channel/create";
        Map<String, String> headers = new HashMap<>(2);
        String appKey = readAppKey();
        String appSecret = "16ba19d26ee84dbb82d4cc9c34bc208f";
        String nonce = "12345";
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime);//参考 计算CheckSum的java代码
        // 设置请求的header
        headers.put("AppKey", appKey);
        headers.put("Nonce", nonce);
        headers.put("CurTime", curTime);
        headers.put("CheckSum", checkSum);
        headers.put("Content-Type", "application/json;charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", roomName);
        jsonObject.put("type", 0);
        NimHttpClient.getInstance().execute(url, headers, jsonObject.toString(), new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable e) {
                ChannelCreat channelCreat=gson.fromJson(response,ChannelCreat.class);
                LogUtil.e(TAG, response);
                if (channelCreat.getCode()!= 200) {
                    errorMsg=channelCreat.getMsg();
                    LogUtil.e(TAG, "create room failed : code = " + code + ", errorMsg = " + errorMsg);
                    if (callback != null) {
                        callback.onFailed(code, errorMsg);
                    }
                    return;
                }
                try {
                    if (channelCreat.getCode() == RESULT_CODE_SUCCESS) {
                        callback.onSuccess(channelCreat.getRet());
                    } else {
                        LogUtil.e(TAG, "create room failed : code = " + code + ", errorMsg = " + channelCreat.getMsg());
                       callback.onFailed(code, channelCreat.getMsg());
                    }
                } catch (Exception e2) {
                    callback.onFailed(-1, e2.getMessage());
                }
            }
        });
    }
    /**
     * 主播创建房间
     * @param account  主播accid
     * @param roomName 房间名称
     * @param callback 回调
     */
    public void createRoom(String account, String roomName, final ChatRoomHttpCallback<String> callback) {
        String url = DemoServers.chatRoomAPIServer() + API_NAME_CREATE_ROOM;
        Map<String, String> headers = new HashMap<>(2);
        String appKey = readAppKey();
//        headers.put(HEADER_KEY_APP_KEY, appKey);
//        headers.put(HEADER_KEY_CONTENT_TYPE, "application/json; charset=utf-8");
        String appSecret = "16ba19d26ee84dbb82d4cc9c34bc208f";
        String nonce = "12345";
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime);//参考 计算CheckSum的java代码
        // 设置请求的header
        headers.put("AppKey", appKey);
        headers.put("Nonce", nonce);
        headers.put("CurTime", curTime);
        headers.put("CheckSum", checkSum);
        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        String params = "name="+roomName+"&announcement=&broadcasturl=xxxxxx&creator="+account;
        NimHttpClient.getInstance().execute(url, headers, params, new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable e) {
                LogUtil.e(TAG, response);
                ChatroomCreateResult chatroomCreateResult=gson.fromJson(response,ChatroomCreateResult.class);
                if (code != 200) {
                    errorMsg=chatroomCreateResult.getDesc();
                    LogUtil.e(TAG, "create room failed : code = " + code + ", errorMsg = " + errorMsg);
                    if (callback != null) {
                        callback.onFailed(code, errorMsg);
                    }
                    return;
                }
                try {
                    int resCode = chatroomCreateResult.getCode();
                    if (resCode == RESULT_CODE_SUCCESS) {
                        callback.onSuccess(chatroomCreateResult.getChatroom().getRoomid()+"");
                    } else {
                        LogUtil.e(TAG, "create room failed : code = " + code + ", errorMsg = " + chatroomCreateResult.getDesc());
                       callback.onFailed(resCode, chatroomCreateResult.getDesc());
                    }
                } catch (Exception e2) {
                    callback.onFailed(-1, e2.getMessage());
                }
            }

        });
    }
    /**
     * 向服务器请求直播间信息
     */
    public void fetchChatRoomPullAddress(String cid,final ChatRoomHttpCallback<AddressResult> callback) {
        String url = DemoServers.API_SERVER2 + "address";
        Map<String, String> headers = new HashMap<>(2);
        String appKey = readAppKey();
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(APP_SECRET, NONCE, curTime);//参考 计算CheckSum的java代码
        // 设置请求的header
        headers.put("AppKey", appKey);
        headers.put("Nonce", NONCE);
        headers.put("CurTime", curTime);
        headers.put("CheckSum", checkSum);
        headers.put("Content-Type", "application/json;charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cid", cid);
        NimHttpClient.getInstance().execute(url, headers, jsonObject.toString(), new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable e) {
                Log.e(TAG,response);
                AddressResult addressResult = gson.fromJson(response, AddressResult.class);
                if (code != 200) {
                    LogUtil.e(TAG, "fetchChatRoomAddress failed : code = " + code + ", errorMsg = " + errorMsg);
                    if (callback != null) {
                        callback.onFailed(code, addressResult.getMsg());
                    }
                    return;
                }
                try {
                    if (code == RESULT_CODE_SUCCESS) {
                        // reply
                        callback.onSuccess(addressResult);
                    } else {
                        callback.onFailed(code, null);
                    }
                } catch (JSONException e1) {
                    callback.onFailed(-1, e1.getMessage());
                }
            }
        });
    }
    /**
     * 向服务器请求直播拉流地址
     * @param callback
     */
    public void fetchChatRoomList(final ChatRoomHttpClient.ChatRoomHttpCallback<List<ChannelListResult.RetBean.ListBean>> callback) {
        String url = DemoServers.API_SERVER2 + "channellist";
        Map<String, String> headers = new HashMap<>(2);
        String appKey = readAppKey();
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(APP_SECRET, NONCE, curTime);//参考 计算CheckSum的java代码
        // 设置请求的header
        headers.put("AppKey", appKey);
        headers.put("Nonce", NONCE);
        headers.put("CurTime", curTime);
        headers.put("CheckSum", checkSum);
        headers.put("Content-Type", "application/json;charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("records", 100);
        jsonObject.put("pnum", 1);
        jsonObject.put("ofield", "ctime");
        jsonObject.put("sort", 0);
        NimHttpClient.getInstance().execute(url, headers, jsonObject.toString(), new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable e) {
                Log.e(TAG,response);
                ChannelListResult channelListResult = gson.fromJson(response, ChannelListResult.class);
                if (code != 200) {
                    errorMsg=channelListResult.getMsg();
                    LogUtil.e(TAG, "fetchChatRoomAddress failed : code = " + code + ", errorMsg = " + errorMsg);
                    if (callback != null) {
                        callback.onFailed(code, errorMsg);
                    }
                    return;
                }
                try {
                    if (code == RESULT_CODE_SUCCESS) {
                        // reply
                        callback.onSuccess(channelListResult.getRet().getList());
                    } else {
                        callback.onFailed(code, null);
                    }
                } catch (JSONException e1) {
                    callback.onFailed(-1, e1.getMessage());
                }
            }
        });
    }
    /**
     * 向网易云信Demo应用服务器请求聊天室地址
     */
    public void fetchChatRoomAddress(String roomId, String account, final ChatRoomHttpCallback<List<String>> callback) {
        String url = DemoServers.chatRoomAPIServer() + API_NAME_FETCH_ADDRESS + "?roomid=" + roomId + "&uid=" + account;

        Map<String, String> headers = new HashMap<>(2);
        String appKey = readAppKey();
        headers.put(HEADER_KEY_APP_KEY, appKey);

        NimHttpClient.getInstance().execute(url, headers, null, new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable e) {
                if (code != 0) {
                    LogUtil.e(TAG, "fetchChatRoomAddress failed : code = " + code + ", errorMsg = " + errorMsg);
                    if (callback != null) {
                        callback.onFailed(code, errorMsg);
                    }
                    return;
                }

                try {
                    // ret 0
                    JSONObject res = JSONObject.parseObject(response);
                    // res 1
                    int resCode = res.getIntValue(RESULT_KEY_RES);
                    if (resCode == RESULT_CODE_SUCCESS) {
                        // msg 1
                        JSONObject msg = res.getJSONObject(RESULT_KEY_MSG);
                        List<String> roomAddrs = new ArrayList<String>(2);
                        if (msg != null) {
                            // list 2
                            JSONArray addrs = msg.getJSONArray(RESULT_KEY_ADDR);
                            for (int i = 0; i < addrs.size(); i++) {
                                roomAddrs.add(addrs.get(i).toString());
                            }
                        }
                        // reply
//                        callback.onSuccess(roomAddrs);
                    } else {
                        callback.onFailed(resCode, null);
                    }
                } catch (JSONException e1) {
                    callback.onFailed(-1, e1.getMessage());
                }
            }
        });
    }

    /**
     * 关闭聊天室
     *
     * @param roomId   聊天室的roomid
     * @param account  主播accid
     * @param callback 回调
     */
    public void closeRoom(String roomId, String account, final ChatRoomHttpCallback<String> callback) {
        String url = DemoServers.chatRoomAPIServer() + API_NAME_CLOSE_ROOM;

        Map<String, String> headers = new HashMap<>(2);
        String appKey = readAppKey();
        headers.put(HEADER_KEY_APP_KEY, appKey);
        headers.put(HEADER_KEY_CONTENT_TYPE, "application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(REQUEST_ROOM_ID, roomId);
        jsonObject.put(REQUEST_USER_UID, account);

        NimHttpClient.getInstance().execute(url, headers, jsonObject.toString(), new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable e) {
                if (code != 0) {
                    LogUtil.e(TAG, "close room failed : code = " + code + ", errorMsg = " + errorMsg);
                    if (callback != null) {
                        callback.onFailed(code, errorMsg);
                    }
                    return;
                }

                try {
                    // ret 0
                    JSONObject res = JSONObject.parseObject(response);
                    // res 1
                    int resCode = res.getIntValue(RESULT_KEY_RES);
                    if (resCode == RESULT_CODE_SUCCESS) {
                        // msg 1
                        String msg = res.getString(RESULT_KEY_MSG);
                        // reply
                        callback.onSuccess(msg);
                    } else {
                        LogUtil.e(TAG, "close room failed : code = " + code + ", errorMsg = " + res.getString(RESULT_KEY_ERROR_MSG));
                        callback.onFailed(resCode, res.getString(RESULT_KEY_ERROR_MSG));
                    }
                } catch (JSONException e1) {
                    callback.onFailed(-1, e1.getMessage());
                }
            }
        });
    }

    public String readAppKey() {
        try {
            ApplicationInfo appInfo = DemoCache.getContext().getPackageManager()
                    .getApplicationInfo(DemoCache.getContext().getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null) {
                return appInfo.metaData.getString("com.netease.nim.appKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
