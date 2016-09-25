package com.jiubai.jiubaijz;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.lang.reflect.Method;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.webView)
    WebView mWebView;

    @Bind(R.id.menu_button)
    ImageButton imageButton;

    private PopupWindow popupWindow;
    private int barColor;
    private int titleColor;

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

        test();
    }

    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("info", url);
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.loadUrl("http://888.jiubaiwang.cn");
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
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
                popupWindow.showAsDropDown(imageButton);
                break;
        }
    }

    private void execute(String command) {
        final String[] parameters = command.split("::");

        if (parameters.length > 1) {
            switch (parameters[0]) {
                case "title":
                    mToolbar.setTitle(parameters[1]);
                    break;

                case "titleColor":
                    titleColor = Color.argb(
                            Integer.valueOf(parameters[4]),
                            Integer.valueOf(parameters[1]),
                            Integer.valueOf(parameters[2]),
                            Integer.valueOf(parameters[3]));
                    mToolbar.setTitleTextColor(titleColor);
                    break;

                case "barColor":
                    barColor = Color.argb(
                            Integer.valueOf(parameters[4]),
                            Integer.valueOf(parameters[1]),
                            Integer.valueOf(parameters[2]),
                            Integer.valueOf(parameters[3]));
                    mToolbar.setBackgroundColor(barColor);
                    popupWindow.setBackgroundDrawable(new ColorDrawable(barColor));

                    break;

                case "rightBtnUrl":

                    break;

                case "rightBtnStyle":
                    setupMenuPopup(parameters);
                    break;

                case "showLeftBtn":
                    if ("true".equals(parameters[1])) {
                        //mToolbar.setNavigationIcon(R.drawable.back);
                    } else {
                        mToolbar.setNavigationIcon(null);
                    }
                    break;

                case "qrScan":
                    break;

                case "share":
                    break;

                case "feedback":
                    break;
            }
        }
    }

    private void setupMenuPopup(String[] parameters) {
        final ArrayList<Integer> imageIds = new ArrayList<>();
        final ArrayList<String> titles = new ArrayList<>();
        final ArrayList<String> actions = new ArrayList<>();

        final int length = Integer.valueOf(parameters[1]);

        for (int i = 1; i <= length; i++) {
            titles.add(parameters[i * 3 - 1]);
            imageIds.add(getResources().getIdentifier(parameters[i * 3], "drawable", getPackageName()));
            actions.add(parameters[i * 3 + 1]);
        }

        ListView listView = new ListView(this);

        listView.setBackgroundColor(0);
        MenuListAdapter adapter = new MenuListAdapter(this, imageIds, titles);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                popupWindow.dismiss();
                String cmd = "showLeftBtn::true";
                execute(cmd);
            }
        });
        listView.setAdapter(adapter);

        popupWindow.setContentView(listView);
    }

    private void test() {
        String  cmd = "title::标题";
        execute(cmd);

        cmd = "barColor::55::55::55::255";
        execute(cmd);

        cmd = "titleColor::255::255::255::255";
        execute(cmd);

        cmd = "showLeftBtn::false";
        execute(cmd);

        cmd = "rightBtnStyle::4::分享::share::share::意见反馈::feedback::feedback::扫一扫::qr_scan::qrScan::刷新::refresh::refresh";
        execute(cmd);
    }
}
