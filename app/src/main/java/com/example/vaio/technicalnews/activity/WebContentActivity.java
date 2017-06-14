package com.example.vaio.technicalnews.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vaio.technicalnews.R;
import com.example.vaio.technicalnews.fragment.NewsFragment;

public class WebContentActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {
    private WebView wvContent;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_content);
        try {
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() throws Exception{
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wvContent = (WebView) findViewById(R.id.wvContent);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        link = getIntent().getExtras().getString(NewsFragment.LINK_CONTENT);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web_content, menu);
        MenuItem actionShare = menu.findItem(R.id.action_share);
        actionShare.setOnMenuItemClickListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, link);
                startActivity(Intent.createChooser(share, "Bạn muốn chia sẻ trên..."));
                break;

        }
        return false;
    }
}
