package com.jiubai.jiubaijz;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Larry Howell on 2016/9/23.
 *
 * 主acitivty
 */

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.webView)
    WebView mWebView;

    @Bind(R.id.menu_button)
    ImageButton mImageButton;

    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;

    private PopupWindow popupWindow;
    private boolean isAnimStart = false;
    private int currentProgress;

    public static final String URL = "file:///android_asset/test.html";
    //public static final String URL = "http://888.jiubaiwang.cn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        initToolbar();

        initWebView();

        initPopupWindow();
    }

    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");

        Log.i("agent", mWebView.getSettings().getUserAgentString());

        mWebView.getSettings().setUserAgentString(
                mWebView.getSettings().getUserAgentString() + "\\JiubaiwangLanren");

        Log.i("agent", mWebView.getSettings().getUserAgentString());

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setAlpha(1.0f);

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                CookieManager cookieManager = CookieManager.getInstance();
                String cookie = cookieManager.getCookie(url);

                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    url = URLDecoder.decode(url, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.i("url", url);

                if (url.contains("app:::")) {
                    String[] cmds = url.split(":::");

                    if (cmds.length > 1) {
                        for (int i = 1; i < cmds.length; i++) {
                            execute(cmds[i]);
                        }
                    }

                    return false;
                } else {
                    view.loadUrl(url);
                    return true;
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                Config.title = title;

                mToolbar.setTitle(title);

                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                currentProgress = mProgressBar.getProgress();
                if (newProgress >= 100 && !isAnimStart) {
                    // 防止调用多次动画
                    isAnimStart = true;
                    mProgressBar.setProgress(newProgress);
                    // 开启属性动画让进度条平滑消失
                    startDismissAnimation(mProgressBar.getProgress());
                } else {
                    // 开启属性动画让进度条平滑递增
                    startProgressAnimation(newProgress);
                }

                super.onProgressChanged(view, newProgress);
            }
        });

        mWebView.loadUrl(URL);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }
            }
        });
    }

    private void initPopupWindow() {
        popupWindow = new PopupWindow(this);
        popupWindow.setElevation(8);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setWidth(400);
    }

    @OnClick({R.id.menu_button})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.menu_button:
                popupWindow.showAsDropDown(mImageButton);
                break;
        }
    }

    private void execute(String command) {
        final String[] parameters = command.split("::");

        if (parameters.length >= 1) {
            switch (parameters[0]) {
                case "title":
                    mToolbar.setTitle(parameters[1]);
                    break;

                case "titleColor":
                    Config.titleColor = Color.argb(
                            (int)(Double.valueOf(parameters[4]) * 255),
                            Integer.valueOf(parameters[1]),
                            Integer.valueOf(parameters[2]),
                            Integer.valueOf(parameters[3]));
                    mToolbar.setTitleTextColor(Config.titleColor);
                    break;

                case "barColor":
                    Config.barColor = Color.argb(
                            (int)(Double.valueOf(parameters[4]) * 255),
                            Integer.valueOf(parameters[1]),
                            Integer.valueOf(parameters[2]),
                            Integer.valueOf(parameters[3]));
                    mToolbar.setBackgroundColor(Config.barColor);
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Config.barColor));
                    break;

                case "rightBtnUrl":

                    break;

                case "rightBtnStyle":
                    setupMenuPopup(parameters);
                    break;

                case "showLeftBtn":
                    if ("true".equals(parameters[1])) {
                        mToolbar.setNavigationIcon(R.drawable.back);
                    } else {
                        mToolbar.setNavigationIcon(null);
                    }
                    break;

                case "qrScan":
                    break;

                case "share":
                    share("玖佰记账", parameters[1], parameters[2]);
                    break;

                case "feedback":
                    feedback();
                    break;

                case "refresh":
                    refresh();
                    break;
            }
        }
    }

    private void feedback() {
        Log.i("info", "feedback");
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
    }

    private void refresh() {
        mWebView.loadUrl(URL);
    }

    private void setupMenuPopup(String[] parameters) {
        final ArrayList<Integer> imageIds = new ArrayList<>();
        final ArrayList<String> titles = new ArrayList<>();
        final ArrayList<String> actions = new ArrayList<>();

        final int length = Integer.valueOf(parameters[1]);

        for (int i = 1; i <= length; i++) {
            titles.add(parameters[i * 3 - 1]);
            if ("qrScan".equals(parameters[i * 3])) {
                imageIds.add(getResources().getIdentifier("qr_scan", "drawable", getPackageName()));
            } else {
                imageIds.add(getResources().getIdentifier(parameters[i * 3], "drawable", getPackageName()));
            }
            actions.add(parameters[i * 3 + 1]);
        }

        ListView listView = new ListView(this);

        listView.setBackgroundColor(0);
        MenuListAdapter adapter = new MenuListAdapter(this, imageIds, titles);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                popupWindow.dismiss();

                mWebView.loadUrl("javascript:" + actions.get(i) + "()");
            }
        });
        listView.setAdapter(adapter);

        popupWindow.setContentView(listView);
    }

    private void share(String title, String text, String url) {
        if(Build.VERSION.SDK_INT >= 23){
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CALL_PHONE,Manifest.permission.READ_LOGS,Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.SET_DEBUG_APP,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.GET_ACCOUNTS,Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this,mPermissionList,123);
        }

        new ShareAction(this)
                .withTitle(title)
                .withText(text)
                .withTargetUrl(url)
                .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .open();
    }

    /**
     * progressBar递增动画
     */
    private void startProgressAnimation(int newProgress) {
        ObjectAnimator animator = ObjectAnimator.ofInt(mProgressBar, "progress", currentProgress, newProgress);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * progressBar消失动画
     */
    private void startDismissAnimation(final int progress) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mProgressBar, "alpha", 1.0f, 0.0f);
        anim.setDuration(500);  // 动画时长
        anim.setInterpolator(new DecelerateInterpolator());     // 减速
        // 关键, 添加动画进度监听器
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();      // 0.0f ~ 1.0f
                int offset = 100 - progress;
                mProgressBar.setProgress((int) (progress + offset * fraction));
            }
        });

        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
                isAnimStart = false;
            }
        });
        anim.start();
    }

    /**
     * 监听back键
     * 在WebView中回退导航
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {  // 返回键的KEYCODE
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;  // 拦截
            } else {
                return super.onKeyDown(keyCode, event);   //  放行
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
