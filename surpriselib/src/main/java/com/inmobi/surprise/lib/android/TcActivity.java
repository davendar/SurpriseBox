package com.inmobi.surprise.lib.android;

import com.inmobi.surprise.lib.R;
import com.inmobi.surprise.lib.util.Constants;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class TcActivity extends Activity {

    private ProgressBar progressBar;
    private RelativeLayout backButtonParent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms);
        progressBar = (ProgressBar) findViewById(R.id.webProgress);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
        backButtonParent = (RelativeLayout) findViewById(R.id.rlBackParent);
        backButtonParent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
        loadUrl(webView);
    }


    private void loadUrl(WebView view) {
        view.loadUrl(Constants.EULA);
    }

}