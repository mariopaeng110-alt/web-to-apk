package com.webtoapk.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);

        setContentView(webView);

        setupWebView();

        loadWebsite();
    }

    private void setupWebView() {

        WebSettings settings =
                webView.getSettings();

        /*
         * JAVASCRIPT
         */
        settings.setJavaScriptEnabled(true);

        /*
         * LOCAL STORAGE
         */
        settings.setDomStorageEnabled(true);

        /*
         * DATABASE
         */
        settings.setDatabaseEnabled(true);

        /*
         * COOKIES
         */
        CookieManager cookies =
                CookieManager.getInstance();

        cookies.setAcceptCookie(true);

        cookies.setAcceptThirdPartyCookies(
                webView,
                true
        );

        /*
         * WEBSITE VIEW
         */
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        /*
         * CACHE
         */
        settings.setCacheMode(
                WebSettings.LOAD_DEFAULT
        );

        /*
         * FILE ACCESS
         */
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        /*
         * MEDIA
         */
        settings.setMediaPlaybackRequiresUserGesture(
                false
        );

        /*
         * ZOOM
         */
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        /*
         * USER AGENT
         *
         * Dibuat seperti Chrome Android
         * supaya website tidak salah mendeteksi
         * WebView sebagai browser lama.
         */
        String chromeUA =
                settings.getUserAgentString();

        if (!chromeUA.contains("Chrome")) {

            settings.setUserAgentString(
                    chromeUA +
                    " Chrome/131.0.0.0 Mobile"
            );
        }


        /*
         * WEBVIEW CLIENT
         *
         * Semua link tetap di dalam APK.
         */
        webView.setWebViewClient(
                new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(
                            WebView view,
                            WebResourceRequest request
                    ) {

                        view.loadUrl(
                                request.getUrl().toString()
                        );

                        return true;
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(
                            WebView view,
                            String url
                    ) {

                        view.loadUrl(url);

                        return true;
                    }
                }
        );


        /*
         * CHROME CLIENT
         *
         * Menangani alert(), confirm(),
         * prompt(), popup, fullscreen, dll.
         */
        webView.setWebChromeClient(
                new WebChromeClient()
        );


        /*
         * DOWNLOAD
         */
        webView.setDownloadListener(
                new DownloadListener() {

                    @Override
                    public void onDownloadStart(
                            String url,
                            String userAgent,
                            String contentDisposition,
                            String mimeType,
                            long contentLength
                    ) {

                        try {

                            Intent intent =
                                    new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(url)
                                    );

                            startActivity(intent);

                        } catch (Exception ignored) {
                        }
                    }
                }
        );


        /*
         * Jangan block mixed content.
         */
        if (
                android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.LOLLIPOP
        ) {

            settings.setMixedContentMode(
                    WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            );
        }
    }


    /*
     * Ambil URL dari config.json
     */
    private void loadWebsite() {

        String url =
                "https://example.com";

        try {

            InputStream input =
                    getAssets().open(
                            "config.json"
                    );

            byte[] data =
                    new byte[input.available()];

            input.read(data);

            input.close();

            JSONObject config =
                    new JSONObject(
                            new String(
                                    data,
                                    StandardCharsets.UTF_8
                            )
                    );

            url =
                    config.getString("url");

        } catch (Exception ignored) {
        }

        webView.loadUrl(url);
    }


    /*
     * Tombol BACK Android
     */
    @Override
    public void onBackPressed() {

        if (
                webView != null &&
                webView.canGoBack()
        ) {

            webView.goBack();

        } else {

            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {

        if (webView != null) {

            webView.stopLoading();

            webView.destroy();

            webView = null;
        }

        super.onDestroy();
    }
}
