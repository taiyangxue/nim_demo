package com.netease.nim.demo.home.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.model.ToolBarOptions;

import java.io.UnsupportedEncodingException;


public class HtmlActivity extends AppCompatActivity {

    private static final String TAG = "HtmlActivity";
    private WebView webview;
    private WebSettings settings;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        ToolBarOptions options = new ToolBarOptions();
        webview = (WebView) findViewById(R.id.webview);
        url = getIntent().getStringExtra("url");
//        Log.e(TAG,url);
//        Log.i(TAG,url);
        settings = webview.getSettings();
        settings.setDefaultTextEncodingName("UTF-8");
        webview.setInitialScale(265);//为25%，最小缩放等级
        // 为webView添加js支持
        settings.setJavaScriptEnabled(true);
        // 添加页面缩放
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);// 双击缩放
//        //自适应屏幕
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        settings.setLoadWithOverviewMode(true);
//        Log.i(TAG,html);
        if(!TextUtils.isEmpty(url)){
            webview.loadUrl(url);
            Log.e(TAG,webview+url);
        }
        webview.setWebViewClient(new WebViewClient() {
            /**
             * 网页加载时回调
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            /**
             * 网页加载结束回调
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
    /**
     * 转换字符串编码
     */
    public static String convertEncodingFormat(String str, String formatFrom, String FormatTo) {
        String result = null;
        if (!(str == null || str.length() == 0)) {
            try {
                result = new String(str.getBytes(formatFrom), FormatTo);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
