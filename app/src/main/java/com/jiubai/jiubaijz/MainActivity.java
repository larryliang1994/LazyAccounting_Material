package com.jiubai.jiubaijz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.reflect.Method;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.webView)
    WebView mWebView;

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        MenuItem item = menu.findItem(R.id.action_refresh);
////        SpannableStringBuilder builder = new SpannableStringBuilder("* Login");
////        // replace "*" with icon
////        builder.setSpan(new ImageSpan(this, R.drawable.refresh), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
////        item.setTitle(builder);
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        if(menu.getClass().getSimpleName().equals("MenuBuilder")){
//            try{
//                Method m = menu.getClass().getDeclaredMethod(
//                        "setOptionalIconsVisible", Boolean.TYPE);
//                m.setAccessible(true);
//                m.invoke(menu, true);
//            }
//            catch(NoSuchMethodException e){
//                Log.e("info", "onMenuOpened", e);
//            }
//            catch(Exception e){
//                throw new RuntimeException(e);
//            }
//        }
//
//        return true;
//    }
}
