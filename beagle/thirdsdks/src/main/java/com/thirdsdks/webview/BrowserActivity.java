package com.thirdsdks.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.beagle.component.activity.BaseCompatActivity;
import com.beagle.component.app.IApplication;
import com.beagle.component.app.ModuleApplicaiton;
import com.beagle.component.logger.LogCat;
import com.beagle.component.utils.ScreenUtil;
import com.beagle.okhttp.OkHttpApplication;
import com.tencent.sonic.sdk.SonicCacheInterceptor;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicConstants;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;
import com.tencent.sonic.sdk.SonicSessionConnection;
import com.tencent.sonic.sdk.SonicSessionConnectionInterceptor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thirdsdks.R;

/**
 * Created by zhaotao on 2020/12/20 21:02.
 */

public class BrowserActivity extends BaseCompatActivity {

    public final static String PARAM_URL = "param_url";
    public final static String PARAM_MODE = "param_mode";
    public static final int MODE_DEFAULT = 0;
    public static final int MODE_SONIC = 1;
    public static final int MODE_SONIC_WITH_OFFLINE_CACHE = 2;
    private WebView webView;
    private SonicSession sonicSession;
    private Object javascriptInterface;
    private String javascriptInterfaceName;
    private WebProgressView webProgressView;
    private boolean isStopdThread;//停止线程
    private String cookie = "";

    public static void startBrowserActivity(Context context, String headTitle, String url, int mode, boolean isBack) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(BrowserActivity.PARAM_URL, url);
        if (mode > 2 || mode < 0) mode = MODE_SONIC;
        intent.putExtra(BrowserActivity.PARAM_MODE, mode);
        intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
        intent.putExtra("headTitle", headTitle);
        intent.putExtra("isBack", isBack);
        context.startActivity(intent);
    }

    public static void startBrowserActivity(Context context, String headTitle, String url, int mode, Serializable javascriptInterface, String javascriptInterfaceName) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(BrowserActivity.PARAM_URL, url);
        if (mode > 2 || mode < 0) mode = 0;
        intent.putExtra(BrowserActivity.PARAM_MODE, mode);
        intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
        intent.putExtra("headTitle", headTitle);
        intent.putExtra("jsObject", javascriptInterface);
        intent.putExtra("jsName", javascriptInterfaceName);
        context.startActivity(intent);
    }

    public static void startBrowserActivity(Context context, String headTitle, String url, int mode, Serializable javascriptInterface, String javascriptInterfaceName,
                                            Serializable javascriptInterface1, String javascriptInterfaceName1) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(BrowserActivity.PARAM_URL, url);
        if (mode > 2 || mode < 0) mode = 0;
        intent.putExtra(BrowserActivity.PARAM_MODE, mode);
        intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
        intent.putExtra("headTitle", headTitle);
        intent.putExtra("jsObject", javascriptInterface);
        intent.putExtra("jsName", javascriptInterfaceName);
        intent.putExtra("jsObject1", javascriptInterface1);
        intent.putExtra("jsName1", javascriptInterfaceName1);
        context.startActivity(intent);
    }

    @Override
    protected void initHead() {
        Intent intent = getIntent();
        String headTitle = intent.getStringExtra("headTitle");
        boolean isBack = intent.getBooleanExtra("isBack", false);
        getCenterTextView().setText(headTitle);
        if (isBack) {
            Button button = getRightButton1();
            int width = (int) TypedValue.applyDimension(1, 48.0F, this.getResources().getDisplayMetrics());
            Toolbar.LayoutParams params = new Toolbar.LayoutParams((int) (width * 2.2), width);
            params.gravity = Gravity.RIGHT;
            params.rightMargin = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            button.setGravity(Gravity.CENTER);
            button.setLayoutParams(params);
            button.setText("返回上页");
            button.setTextColor(Color.WHITE);
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (webView != null) webView.goBack();
                }
            });
        }
    }

    @Override
    protected void initView() {

    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initHead();
        Intent intent = getIntent();
        String url = intent.getStringExtra(PARAM_URL);
        int mode = intent.getIntExtra(PARAM_MODE, -1);
        if (TextUtils.isEmpty(url) || -1 == mode) {
            finish();
            return;
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        // init sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
        }
        SonicSessionClientImpl sonicSessionClient = null;
        // if it's sonic mode , startup sonic session at first time
        if (MODE_DEFAULT != mode) { // sonic mode
            SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();
            sessionConfigBuilder.setSupportLocalServer(true);

            // if it's offline pkg mode, we need to intercept the session connection
            if (MODE_SONIC_WITH_OFFLINE_CACHE == mode) {
                sessionConfigBuilder.setCacheInterceptor(new SonicCacheInterceptor(null) {
                    @Override
                    public String getCacheData(SonicSession session) {
                        return null; // offline pkg does not need cache
                    }
                });

                sessionConfigBuilder.setConnectionInterceptor(new SonicSessionConnectionInterceptor() {
                    @Override
                    public SonicSessionConnection getConnection(SonicSession session, Intent intent) {
                        return new OfflinePkgSessionConnection(BrowserActivity.this, session, intent);
                    }
                });
            }

            // create sonic session and run sonic flow
            sonicSession = SonicEngine.getInstance().createSession(url, sessionConfigBuilder.build());
            if (null != sonicSession) {
                sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
            } else {
                // this only happen when a same sonic session is already running,
                // u can comment following codes to feedback as a default mode.
//                throw new UnknownError("create session fail!");
//                Toast.makeText(this, "create sonic session fail!", Toast.LENGTH_SHORT).show();
                LogCat.e("create sonic session fail!");
            }
        }
        // start init flow ...
        // in the real world, the init flow may cost a long time as startup
        // runtime、init configs....
        setContentView(R.layout.activity_browser);
        // init webview
        webView = (WebView) findViewById(R.id.webview);
        // 添加进度条
        webProgressView = new WebProgressView(context);
        webProgressView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(context, 4)));
        webProgressView.setColor(Color.RED);
        webProgressView.setProgress(10);
        // 把进度条加到Webview中
        webView.addView(webProgressView);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, final String message, JsResult result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                });
                result.confirm();
                return true;
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                LogCat.e("jsConsole--------------" + cm.message());
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 加载完毕进度条消失
                    webProgressView.setVisibility(View.GONE);
                } else {
                    //更新进度
                    webProgressView.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (sonicSession != null) {
                    sonicSession.getSessionClient().pageFinish(url);
                }
                if (webProgressView.isShown()) webProgressView.setVisibility(View.GONE);
            }

            @TargetApi(21)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, final String url) {
                if (sonicSession != null) {
                    return (WebResourceResponse) sonicSession.getSessionClient().requestResource(url);
                }
                return super.shouldInterceptRequest(view, url);
            }

        });
        WebSettings webSettings = webView.getSettings();
        // add java script interface
        // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
        // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
        // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
        webSettings.setJavaScriptEnabled(true);
        webView.removeJavascriptInterface("searchBoxJavaBridge_");
        webView.removeJavascriptInterface("accessibility");
        webView.removeJavascriptInterface("accessibilityTraversal");
        intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
        webView.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient, intent), "sonic");
        Serializable jsObject = intent.getSerializableExtra("jsObject");
        String jsName = intent.getStringExtra("jsName");
        if (jsObject != null && !TextUtils.isEmpty(jsName)) {
            LogCat.e("-----------------------配置js调用本地方法");
            Object object = jsObject;
            webView.addJavascriptInterface(object, jsName);
        }
        Serializable jsObject1 = intent.getSerializableExtra("jsObject1");
        String jsName1 = intent.getStringExtra("jsName1");
        if (jsObject1 != null && !TextUtils.isEmpty(jsName1)) {
            LogCat.e("-----------------------配置js调用本地方法2");
            Object object1 = jsObject1;
            webView.addJavascriptInterface(object1, jsName1);
        }
        // init webview settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Android5.0开始，默认用MIXED_CONTENT_NEVER_ALLOW模式，即总是不允许WebView同时加载Https和Http。
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setAllowContentAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 跨域访问
        try {
            if (Build.VERSION.SDK_INT >= 16) {
                Class<?> clazz = webSettings.getClass();
                Method method = clazz.getMethod(
                        "setAllowUniversalAccessFromFileURLs", boolean.class);//利用反射机制去修改设置对象
                if (method != null) {
                    method.invoke(webSettings, true);//修改设置
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            URL url1 = new URL(url);
            String host = url1.getHost();
            IApplication iApplication = ModuleApplicaiton.getInstance().findIApplication("com.zhy.http.okhttp.OkHttpApplication");
            if (iApplication != null) {
                OkHttpApplication okHttpApplication = (OkHttpApplication) iApplication;
                String token = okHttpApplication.getToken("bgToken");
                if (!TextUtils.isEmpty(host) && (host.contains("http:") || host.contains("https:"))) {
                    synCookies(host, token);
                } else {
                    SharedPreferences settings = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                    String _host = settings.getString("host", "");
                    synCookies(_host, token);
                }

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient.bindWebView(webView);
            sonicSessionClient.clientReady();
        } else { // default mode
            webView.loadUrl(url);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (null != sonicSession) {
            sonicSession.destroy();
            sonicSession = null;
        }
        isStopdThread = true;
        if (webView != null) {
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }

            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearView();
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    private static class OfflinePkgSessionConnection extends SonicSessionConnection {

        private final WeakReference<Context> context;

        public OfflinePkgSessionConnection(Context context, SonicSession session, Intent intent) {
            super(session, intent);
            this.context = new WeakReference<Context>(context);
        }

        @Override
        protected int internalConnect() {
            Context ctx = context.get();
            if (null != ctx) {
                try {
                    InputStream offlineHtmlInputStream = ctx.getAssets().open("sonic-demo-index.html");
                    responseStream = new BufferedInputStream(offlineHtmlInputStream);
                    return SonicConstants.ERROR_CODE_SUCCESS;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return SonicConstants.ERROR_CODE_UNKNOWN;
        }

        @Override
        protected BufferedInputStream internalGetResponseStream() {
            return responseStream;
        }

        @Override
        public void disconnect() {
            if (null != responseStream) {
                try {
                    responseStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getResponseCode() {
            return 200;
        }

        @Override
        public Map<String, List<String>> getResponseHeaderFields() {
            return new HashMap<>(0);
        }

        @Override
        public String getResponseHeaderField(String key) {
            return "";
        }
    }

    public void synCookies(String cookie) {
        synCookies("", cookie);
    }

    public void synCookies(String host, String cookie) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(host, "bgToken=" + cookie);// cookies是在HttpClient中获得的cookie
        LogCat.e("-------------bgToken=" + cookie);
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.createInstance(getApplicationContext());
            CookieSyncManager.getInstance().sync();
        } else {
            cookieManager.setAcceptThirdPartyCookies(webView, true);//5.0以上系统不用这个方法就不行
            cookieManager.flush();
        }
    }
}
