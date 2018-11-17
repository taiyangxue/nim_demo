package com.netease.nim.demo.common.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.netease.nim.demo.common.entity.AddVideocomment;
import com.netease.nim.demo.common.entity.CommonBean;
import com.netease.nim.demo.common.entity.ErrorPicAdd;
import com.netease.nim.demo.common.entity.ErrorPicResult;
import com.netease.nim.demo.common.entity.ErrorPicRet;
import com.netease.nim.demo.common.entity.NodeRet;
import com.netease.nim.demo.common.entity.SectionRet;
import com.netease.nim.demo.common.entity.UserLoginBean;
import com.netease.nim.demo.common.entity.VersionUpdateBean;
import com.netease.nim.demo.common.entity.VideoRet;
import com.netease.nim.demo.common.entity.Videocomment;

import java.util.List;


/**
 * Created by sun on 2017/5/26.
 */

public class ApiUtils {
    private static final String TAG = "ApiUtils";
    private static HttpUtils httpUtils;
    private static Gson gson;
    private static ApiUtils instance;
    private static final String HOST = "http://47.105.59.105/api";
    public static final String STATIC_HOST = "http://47.105.59.105/";

    public static synchronized ApiUtils getInstance() {
        if (instance == null) {
            instance = new ApiUtils();
        }
        gson = new Gson();
        httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(1000);
        return instance;
    }
    /**
     * 获取版本信息
     *
     * @param listener
     */
    public void index_versionupdate(final ApiListener<VersionUpdateBean.DataBean> listener) {
        String path = "/index/versionupdate";
        httpUtils.send(HttpRequest.HttpMethod.GET, HOST + path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                VersionUpdateBean result = gson.fromJson(responseInfo.result, VersionUpdateBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    public void sms_send(String mobile, String event, final ApiListener<String> listener) {
        String path = "/sms/send";
        RequestParams params = new RequestParams();
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("event", event);
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                CommonBean result = gson.fromJson(responseInfo.result, CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getMsg());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }

    public void sms_check(String mobile, String captcha, final ApiListener<String> listener) {
        String path = "/sms/check";
        RequestParams params = new RequestParams();
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("captcha", captcha);
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                CommonBean result = gson.fromJson(responseInfo.result, CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getMsg());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }

    public void user_login(String account, String password, final ApiListener<UserLoginBean.DataBean.UserinfoBean> listener) {
        String path = "/user/login";
        RequestParams params = new RequestParams();
        params.addBodyParameter("account", account);
        params.addBodyParameter("password", password);
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                UserLoginBean result = gson.fromJson(responseInfo.result, UserLoginBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData().getUserinfo());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }

    /**
     * 重置密码
     *
     * @param mobile
     * @param password
     * @param captcha
     * @param listener
     */
    public void user_resetpwd(String mobile, String password, String captcha, final ApiListener<String> listener) {
        String path = "/user/resetpwd";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("password", password);
        params.addBodyParameter("captcha", captcha);
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                Log.e(TAG, gson.toJson(params));
                CommonBean result = gson.fromJson(responseInfo.result, CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getMsg());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }

    /**
     * 重置密码
     *
     * @param oldpwd
     * @param newpwd
     * @param listener
     */
    public void user_changepwd(String account_id, String oldpwd, String newpwd, final ApiListener<String> listener) {
        String path = "/user/changepwd";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("account_id", account_id);
        params.addBodyParameter("oldpwd", oldpwd);
        params.addBodyParameter("newpwd", newpwd);
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                Log.e(TAG, gson.toJson(params));
                CommonBean result = gson.fromJson(responseInfo.result, CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getMsg());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }

    /**
     * 用户注册
     *
     * @param mobile
     * @param nickname
     * @param password
     * @param captcha
     * @param listener
     */
    public void user_register(String mobile, String nickname, String password, String captcha, String grade, final ApiListener<String> listener) {
        String path = "/user/register";
        RequestParams params = new RequestParams();
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("nickname", nickname);
        params.addBodyParameter("password", password);
        params.addBodyParameter("captcha", captcha);
        params.addBodyParameter("grade", grade);
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                CommonBean result = gson.fromJson(responseInfo.result, CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getMsg());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }

    /**
     * 获取轮播图
     *
     * @param listener
     */
    public void index_banner(final ApiListener<String> listener) {
        String path = "/index/banner";
        httpUtils.send(HttpRequest.HttpMethod.GET, HOST + path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                CommonBean result = gson.fromJson(responseInfo.result, CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }

    /**
     * 获取轮播图
     *
     * @param listener
     */
    public void index_push(String user_account, String type, final ApiListener<String> listener) {
        String path = "/index/push";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("type", type);
        params.addBodyParameter("user_account", user_account);
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                CommonBean result = gson.fromJson(responseInfo.result, CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }


    public void video_getpushvideo(String user_id, String course, String keywords,  final ApiListener<List<VideoRet.DataBean>> listener) {
        String path = "/video/getpushvideo";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("user_id", user_id);
        params.addBodyParameter("course", course);
        params.addBodyParameter("keywords", keywords);
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                VideoRet result = gson.fromJson(responseInfo.result, VideoRet.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    public void video_getvideotype(String user_id, String course, String keywords, int pid, final ApiListener<List<VideoRet.DataBean>> listener) {
        String path = "/video/getvideotype";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("user_id", user_id);
        params.addBodyParameter("course", course);
        params.addBodyParameter("keywords", keywords);
        params.addBodyParameter("pid", pid + "");
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                VideoRet result = gson.fromJson(responseInfo.result, VideoRet.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    public void video_getvideos(String grade, String course, String video, int offset, int limit, String time, String search, final ApiListener<List<VideoRet.DataBean>> listener) {
        String path = "/video/getvideos";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("grade", grade);
        params.addBodyParameter("course", course);
        params.addBodyParameter("video", video);
        params.addBodyParameter("offset", offset + "");
        params.addBodyParameter("limit", limit + "");
        if (!TextUtils.isEmpty(time)) {
            params.addBodyParameter("time", time);
        }
        if (!TextUtils.isEmpty(search)) {
            params.addBodyParameter("search", search);
        }
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                VideoRet result = gson.fromJson(responseInfo.result, VideoRet.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }

    public void video_collect(String user_id, String video_id, int course, final ApiListener<String> listener) {
        String path = "/video/collect";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("user_id", user_id);
        params.addBodyParameter("video_id", video_id);
        params.addBodyParameter("course_id", course + "");
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                CommonBean result = gson.fromJson(responseInfo.result, CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    public void video_canclecollect(String user_id, String video_id, int course, final ApiListener<String> listener) {
        String path = "/video/canclecollect";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("user_id", user_id);
        params.addBodyParameter("video_id", video_id);
        params.addBodyParameter("course_id", course + "");
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                CommonBean result = gson.fromJson(responseInfo.result, CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }

    public void video_getcollects(String user_id, int course, int offset, int limit, String time, String search, final ApiListener<List<VideoRet.DataBean>> listener) {
        String path = "/video/getcollects";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("user_id", user_id);
        params.addBodyParameter("course_id", course + "");
        params.addBodyParameter("offset", offset + "");
        params.addBodyParameter("limit", limit + "");
        if (!TextUtils.isEmpty(time)) {
            params.addBodyParameter("time", time);
        }
        if (!TextUtils.isEmpty(search)) {
            params.addBodyParameter("search", search);
        }
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                VideoRet result = gson.fromJson(responseInfo.result, VideoRet.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }

    /**
     * 获取一题多解内容
     * @param offset
     * @param limit
     * @param video_id
     * @param listener
     */
    public void videocomment_select(String video_id,  int offset, int limit, final ApiListener<List<Videocomment.DataBean>> listener) {
        String path = "/videocomment/select";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("video_id", video_id);
        params.addBodyParameter("offset", offset + "");
        params.addBodyParameter("limit", limit + "");
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                Videocomment result = gson.fromJson(responseInfo.result, Videocomment.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    /**
     * 提交一题多解内容
     */
    public void videocomment_add(String video_id, String user_id,String image,String content,  final ApiListener<Videocomment.DataBean> listener) {
        String path = "/videocomment/add";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("video_id", video_id);
        params.addBodyParameter("user_id", user_id);
        params.addBodyParameter("image", image);
        params.addBodyParameter("content", content);
//        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                AddVideocomment result = gson.fromJson(responseInfo.result, AddVideocomment.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    /**
     * 点赞接口
     */
    public void videocomment_upcount(String id,String user_id, final ApiListener<String> listener) {
        String path = "/videocomment/upcount";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("id", id);
        params.addBodyParameter("user_id", user_id);
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                CommonBean result = gson.fromJson(responseInfo.result, CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getMsg());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    public void errorpic_geterrorpictype(String user_id, String course,  int pid, final ApiListener<List<ErrorPicResult.DataBean>> listener) {
        String path = "/errorpic/geterrorpictype";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("user_id", user_id);
        params.addBodyParameter("course", course);
        params.addBodyParameter("pid", pid + "");
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                ErrorPicResult result = gson.fromJson(responseInfo.result, ErrorPicResult.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    public void errorpic_getsection(int course, String grade,  final ApiListener<List<SectionRet.DataBean>> listener) {
        String path = "/errorpic/getsection";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("course", course+"");
        params.addBodyParameter("grade", grade);
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                SectionRet result = gson.fromJson(responseInfo.result, SectionRet.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    public void errorpic_select(int user_id, int section_id,  final ApiListener<List<ErrorPicRet.DataBean>> listener) {
        String path = "/errorpic/select";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("user_id", user_id+"");
        params.addBodyParameter("section_id", section_id+"");
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                ErrorPicRet result = gson.fromJson(responseInfo.result, ErrorPicRet.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    public void errorpic_add(int user_id,int category_id,String pic_image, final ApiListener<ErrorPicResult.DataBean> listener) {
        String path = "/errorpic/add";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("user_id", user_id+"");
//        params.addBodyParameter("section_id", section_id+"");
        params.addBodyParameter("category_id", category_id+"");
        params.addBodyParameter("pic_image", pic_image);
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                ErrorPicAdd result = gson.fromJson(responseInfo.result,ErrorPicAdd.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    public void errorpic_update(int id,int section_id, final ApiListener<String> listener) {
        String path = "/errorpic/update";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("id", id+"");
        params.addBodyParameter("section_id", section_id+"");
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                CommonBean result = gson.fromJson(responseInfo.result,CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    public void errorpic_delete(int id, final ApiListener<String> listener) {
        String path = "/errorpic/delete";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("id", id+"");
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                CommonBean result = gson.fromJson(responseInfo.result, CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    public void node_getnodes(String user_id, int course,String type, int offset, int limit, String time, String search, final ApiListener<List<NodeRet.DataBean>> listener) {
        String path = "/node/getnodes";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("user_id", user_id);
        params.addBodyParameter("course_id", course + "");
        params.addBodyParameter("offset", offset + "");
        params.addBodyParameter("limit", limit + "");
        params.addBodyParameter("type", type);
        if (!TextUtils.isEmpty(time)) {
            params.addBodyParameter("time", time);
        }
        if (!TextUtils.isEmpty(search)) {
            params.addBodyParameter("search", search);
        }
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                NodeRet result = gson.fromJson(responseInfo.result, NodeRet.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }

    public void node_add(String user_id, int course,  String title, String content_image, final ApiListener<String> listener) {
        String path = "/node/add";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("user_id", user_id);
        params.addBodyParameter("course_id", course + "");
        params.addBodyParameter("title", title);
        params.addBodyParameter("content_image", content_image);
        params.addBodyParameter("type", "1");
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                CommonBean result = gson.fromJson(responseInfo.result, CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    public void node_update(String id,  String title, String content, final ApiListener<String> listener) {
        String path = "/node/add";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("id", id);
        params.addBodyParameter("title", title);
        params.addBodyParameter("content", content);
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                CommonBean result = gson.fromJson(responseInfo.result, CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
    public void user_changegrade(String user_id, int grade,  final ApiListener<String> listener) {
        String path = "/user/changegrade";
        final RequestParams params = new RequestParams();
        params.addBodyParameter("user_id", user_id);
        params.addBodyParameter("grade", grade + "");
        Log.e(TAG, gson.toJson(params));
        httpUtils.send(HttpRequest.HttpMethod.POST, HOST + path, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e(TAG, responseInfo.result);
                CommonBean result = gson.fromJson(responseInfo.result, CommonBean.class);
                if (result.getCode() == 1) {
                    listener.onSuccess(result.getData());
                } else {
                    listener.onFailed(result.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, s);
                listener.onFailed(s);
            }
        });
    }
}
