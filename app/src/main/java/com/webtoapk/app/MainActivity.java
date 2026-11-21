package com.webtoapk.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.graphics.Color;

import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.WHITE);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient());

        String url = "https://example.com";

        try {
            InputStream input = getAssets().open("config.json");
            byte[] data = new byte[input.available()];
            input.read(data);
            input.close();

            JSONObject config = new JSONObject(
                new String(data, StandardCharsets.UTF_8)
            );

            url = config.getString("url");

        } catch (Exception e) {
            Toast.makeText(this, "Config tidak ditemukan", Toast.LENGTH_LONG).show();
        }

        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
