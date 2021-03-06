package com.asd412id.ujianqapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class UjianWebViewActivity extends AppCompatActivity {
    WebView webView;
    String url;
    SwipeRefreshLayout refreshLayout;
    Boolean loaded = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ujian_web_view);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        url = bundle.getString("url");
        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                refreshLayout.setRefreshing(false);
                loaded = true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                refreshLayout.setRefreshing(true);
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.setLongClickable(true);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");

        refreshLayout = findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkUrl(true);
            }
        });

        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        checkUrl(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Konfirmasi")
                .setMessage("Pastikan ujian telah selesai!\nYakin akan keluar?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(UjianWebViewActivity.this,MainActivity.class));
                        finishAffinity();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
            return true;
        }
        return false;
    }

    private void checkUrl(final Boolean reload) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (reload && loaded){
                    webView.reload();
                }else {
                    webView.loadUrl(url);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError){
                    Toast.makeText(UjianWebViewActivity.this,"Jaringan bermasalah! Silahkan periksa jaringan perangkat!",Toast.LENGTH_SHORT).show();
                }else if (error.networkResponse!=null && error.networkResponse.data!=null){
                    Toast.makeText(UjianWebViewActivity.this,"Server tidak ditemukan!",Toast.LENGTH_SHORT).show();
                }
                refreshLayout.setRefreshing(false);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    @Override
    public void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        assert activityManager != null;
        activityManager.moveTaskToFront(getTaskId(), 0);
    }
}
