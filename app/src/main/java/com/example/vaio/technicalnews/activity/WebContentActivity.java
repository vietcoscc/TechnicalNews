package com.example.vaio.technicalnews.activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.fragment.HomeFragment;
import com.example.vaio.technicalnews.fragment.NewsFragment;

public class WebContentActivity extends AppCompatActivity {
    private WebView wvContent;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_content);
        initViews();
    }

    private void initViews() {
        wvContent = (WebView) findViewById(R.id.wvContent);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final String link = getIntent().getExtras().getString(NewsFragment.LINK_CONTENT);
        wvContent.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(WebContentActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        wvContent.getSettings().setAllowContentAccess(false);
        wvContent.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        wvContent.getSettings().setJavaScriptEnabled(false);
        wvContent.loadUrl(link);
    }

}
