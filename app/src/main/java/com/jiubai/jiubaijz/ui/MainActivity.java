package com.jiubai.jiubaijz.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jiubai.jiubaijz.R;
import com.jiubai.jiubaijz.adapter.MenuListAdapter;
import com.jiubai.jiubaijz.common.Config;
import com.jiubai.jiubaijz.common.UtilBox;
import com.jiubai.jiubaijz.net.VolleyUtil;
import com.jiubai.jiubaijz.zxing.activity.CaptureActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Larry Howell on 2016/9/23.
 * <p>
 * 主activity
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
    private long doubleClickTime = 0;
    private boolean isAnimStart = false;
    private int currentProgress;
    private String currentUrl = "";
    private boolean canGoBack = false;
    private ListView mListView;

    //public static final String URL = "file:///android_asset/test.html";
    public static final String URL = "http://888.jiubaiwang.cn";
    public static final int CODE_QR_SCAN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initView();

        PushAgent.getInstance(this).onAppStart();
    }

    private void initView() {
        initToolbar();

        initWebView();

        initPopupWindow();

        initMenuButton();

        checkVersion();
    }

    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");

        mWebView.getSettings().setUserAgentString(
                mWebView.getSettings().getUserAgentString() + "\\JiubaiwangLanren");

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

                if (cookie != null && !"".equals(cookie)) {
                    String[] cookies = cookie.replace(" ", "").split(";");

                    Config.cookie = new HashMap<>();

                    for(String c: cookies) {
                        String[] cc = c.split("=");
                        Config.cookie.put(cc[0], cc[1]);
                    }
                }

                if (!"".equals(Config.shouldLoadUrl)) {
                    mWebView.loadUrl(Config.shouldLoadUrl);

                    Config.shouldLoadUrl = "";
                }

                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);

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
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.contains("app:::")) {
                    mWebView.loadUrl(url);

                    if (!currentUrl.equals(url)) {
                        currentUrl = url;
                    }
                }

                return true;
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

        mToolbar.setNavigationIcon(R.drawable.home);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.canGoBack() && canGoBack) {
                    mWebView.goBack();

                    while(mWebView.getUrl().equals(currentUrl) && mWebView.canGoBack()) {
                        mWebView.goBack();
                    }
                    currentUrl = mWebView.getUrl();
                }
            }
        });
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (popupWindow != null) {
            if (!popupWindow.isShowing()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    popupWindow.showAsDropDown(mImageButton, UtilBox.dip2px(this, -12), 0, Gravity.END);
                } else {
                    int x = UtilBox.dip2px(this, 12);
                    int titleHeight = UtilBox.dip2px(this, 56);
                    Rect frame = new Rect();
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                    popupWindow.showAtLocation(mListView, Gravity.TOP + Gravity.END, x, frame.top + titleHeight);
                }
            }
        }
        return false;
    }

    private void initPopupWindow() {
        popupWindow = new PopupWindow(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(UtilBox.dip2px(this, 16));
        }
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setWidth(UtilBox.dip2px(this, 150));
        popupWindow.setHeight(android.view.WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void initMenuButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mImageButton.setElevation(UtilBox.dip2px(this, 8));
        }
    }

    @OnClick({R.id.menu_button})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.menu_button:
                if (popupWindow == null) {
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    popupWindow.showAsDropDown(mImageButton, UtilBox.dip2px(this, -12), 0, Gravity.END);
                } else {
                    int x = UtilBox.dip2px(this, 12);
                    int titleHeight = UtilBox.dip2px(this, 56);
                    Rect frame = new Rect();
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                    popupWindow.showAtLocation(mListView, Gravity.TOP + Gravity.END, x, frame.top + titleHeight);
                }
                mImageButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mImageButton.setBackgroundColor(0);
                    }
                }, 100);
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
                            (int) (Double.valueOf(parameters[4]) * 255),
                            Integer.valueOf(parameters[1]),
                            Integer.valueOf(parameters[2]),
                            Integer.valueOf(parameters[3]));
                    mToolbar.setTitleTextColor(Config.titleColor);
                    break;

                case "barColor":
                    Config.barColor = Color.argb(
                            (int) (Double.valueOf(parameters[4]) * 255),
                            Integer.valueOf(parameters[1]),
                            Integer.valueOf(parameters[2]),
                            Integer.valueOf(parameters[3]));
                    mToolbar.setBackgroundColor(Config.barColor);
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Config.barColor));
                    break;

                case "rightBtnUrl":
                    setupMenuButton(parameters);
                    break;

                case "rightBtnStyle":
                    setupMenuPopup(parameters);
                    break;

                case "showLeftBtn":
                    if ("true".equals(parameters[1])) {
                        canGoBack = true;
                        mToolbar.setNavigationIcon(R.drawable.back);
                    } else {
                        canGoBack = false;
                        mToolbar.setNavigationIcon(R.drawable.home);
                    }
                    break;

                case "qrScan":
                    qrScan();
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

    private void qrScan() {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, CODE_QR_SCAN);
    }

    private void feedback() {
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
    }

    private void refresh() {
        mWebView.loadUrl(URL);
    }

    private void share(String title, String text, String url) {
        new ShareAction(this)
                .withTitle(title)
                .withText(text)
                .withTargetUrl(url)
                .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .open();
    }

    private void checkVersion() {
        String[] keys = {"a"};
        String[] values = {"a"};

        VolleyUtil.request("http://888.jiubaiwang.cn/androidversion.php", keys, values,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            //String response = "{\"version\":\"lanrenjizhangV2\", \"info\": \"您好，懒人记账推出了新版本，我们做了如下更新。1,这是第一个版本 2,这是第一个版本 3,这是第一个版本\", \"url\": \"http://20055.jiubai.cc/uploadfile/webeditor2/android/TaskMoment_Material.apk\"}";

                            JSONObject jsonObject = new JSONObject(s);

                            int newVersion = Integer.valueOf(
                                    jsonObject.getString("version").replace("lanrenjizhangV", ""));

                            if (newVersion > UtilBox.getPackageInfo(MainActivity.this).versionCode) {
                                final String desc = jsonObject.getString("info");

                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage(desc)
                                        .setTitle("新版本")
                                        .setMessage(desc)
                                        .setCancelable(true)
                                        .setNegativeButton("取消", null)
                                        .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Uri uri = Uri.parse("http://20055.jiubai.cc/uploadfile/webeditor2/android/LazyAccounting_Material.apk");
                                                startActivity(new Intent(Intent.ACTION_VIEW,uri));
                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.setCancelable(true);
                                dialog.setCanceledOnTouchOutside(true);
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CODE_QR_SCAN:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("result");
                    if (result != null && !"".equals(result)) {
                        if (result.contains("http")) {
                            mWebView.loadUrl(result);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            AlertDialog dialog = builder.setMessage(result)
                                    .setCancelable(true)
                                    .setPositiveButton("关闭", null)
                                    .create();
                            dialog.setCancelable(true);
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        AlertDialog dialog = builder.setMessage("无内容")
                                .setCancelable(true)
                                .setPositiveButton("关闭", null)
                                .create();
                        dialog.setCancelable(true);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();
                    }
                }
                break;
        }
    }

    private void setupMenuButton(String[] parameters) {
        if ("null".equals(parameters[1])) {
            mImageButton.setVisibility(View.GONE);
        } else {
            mImageButton.setVisibility(View.VISIBLE);
        }
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

        mListView = new ListView(this);

        mListView.setBackgroundColor(0);
        MenuListAdapter adapter = new MenuListAdapter(this, imageIds, titles);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                popupWindow.dismiss();

                mWebView.loadUrl("javascript:" + actions.get(i) + "()");
            }
        });
        mListView.setAdapter(adapter);

        popupWindow.setContentView(mListView);
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
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mWebView.canGoBack() && mWebView.getUrl().equals(currentUrl) && canGoBack) {
                mWebView.goBack();

                while(mWebView.getUrl().equals(currentUrl) && mWebView.canGoBack() && canGoBack) {
                    mWebView.goBack();
                }
                currentUrl = mWebView.getUrl();
                return true;
            } else if ((System.currentTimeMillis() - doubleClickTime) > 2000) {
                UtilBox.showSnackbar(this, "再按一次退出程序");
                doubleClickTime = System.currentTimeMillis();
                return true;
            } else {
                finish();
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}