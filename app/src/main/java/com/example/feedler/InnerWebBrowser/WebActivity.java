package com.example.feedler.InnerWebBrowser;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


import com.example.feedler.R;

public class WebActivity extends Activity {

    public static final String URLKEY="URLKEY";
    private WebView webView;
    String url;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);
        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());

        Bundle argumentsFromAnotherActivity = getIntent().getExtras();
        if (argumentsFromAnotherActivity!=null){
            url=  argumentsFromAnotherActivity.getString(URLKEY);
        }
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
