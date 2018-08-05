package com.netease.nim.demo.home.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.NodeRet;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

public class NodePlayActivity extends UI {
    @ViewInject(R.id.webView)
    private WebView webView;
    public final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;}pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;}</style>";
    public final static String CSS_STYLE ="<style>* {font-size:25px;line-height:30px;}* {color:#FFFFFF;} p {color:#FFFFFF;}</style>";
    /**
     * 用来控制字体大小
     */
    private WebSettings settings;
    private NodeRet.DataBean node;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ViewUtils.inject(this);
        ToolBarOptions options = new ToolBarOptions();
        node = (NodeRet.DataBean) getIntent().getSerializableExtra("node");
        options.titleString = node.getTitle();
        setToolBar(R.id.toolbar, options);
        initData();
    }

    private void initData() {
        settings = webView.getSettings();
        settings.setSupportZoom(true);
//        settings.setDefaultFontSize(50);
        settings.setTextSize(WebSettings.TextSize.NORMAL);
        webView.loadDataWithBaseURL(null, node.getContent(), "text/html", "utf-8", null);//加载定义的代码，并设定编码格式和字符集。

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return false;
    }
}
