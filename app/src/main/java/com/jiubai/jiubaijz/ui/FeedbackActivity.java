package com.jiubai.jiubaijz.ui;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jiubai.jiubaijz.R;
import com.jiubai.jiubaijz.common.Config;
import com.jiubai.jiubaijz.common.UtilBox;
import com.jiubai.jiubaijz.net.VolleyUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Larry Howell on 2016/9/26.
 *
 * 意见反馈activity
 */

public class FeedbackActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.editText)
    EditText mEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        ButterKnife.bind(this);

        initView();

        PushAgent.getInstance(this).onAppStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_done:
                UtilBox.toggleSoftInput(mEditText, false);
                if ("".equals(mEditText.getText().toString())) {
                    UtilBox.showSnackbar(this, "请输入反馈内容");
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("正在上传反馈内容");
                    progressDialog.show();

                    String deviceInfo = Build.VERSION.SDK_INT + "##"
                            + UtilBox.getPackageInfo(this).versionCode + "##"
                            + android.os.Build.MODEL;

                    Log.i("info", deviceInfo);

                    String[] keys = {"a", "content", "equipment"};
                    String[] values = {"app_feedback", mEditText.getText().toString(), deviceInfo};

                    VolleyUtil.request("http://ucenter.jiubaiwang.cn/app_api.php", keys, values,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                        }
                                    }, 500);

                                    UtilBox.showSnackbar(FeedbackActivity.this, "上传成功");

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 1500);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    progressDialog.dismiss();

                                    Log.i("info", volleyError.getMessage());
                                }
                            });
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setBackgroundColor(Config.barColor);
        mToolbar.setTitleTextColor(Config.titleColor);
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
