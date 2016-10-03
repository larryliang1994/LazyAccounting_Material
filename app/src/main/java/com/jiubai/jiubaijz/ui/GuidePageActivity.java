package com.jiubai.jiubaijz.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.jiubai.jiubaijz.App;
import com.jiubai.jiubaijz.R;
import com.jiubai.jiubaijz.adapter.ViewPagerAdapter;
import com.jiubai.jiubaijz.common.UtilBox;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Larry Howell on 2016/9/30.
 */
public class GuidePageActivity extends AppCompatActivity {

    @Bind(R.id.viewpager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide);

        ButterKnife.bind(this);

        initView();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(this, new ViewPagerAdapter.Callback() {
            @Override
            public void onStart() {
                SharedPreferences.Editor editor = App.sp.edit();
                editor.putInt("version", UtilBox.getPackageInfo(GuidePageActivity.this).versionCode);
                editor.apply();

                startActivity(new Intent(GuidePageActivity.this, MainActivity.class));
                finish();
            }
        });

        mViewPager.setAdapter(vpAdapter);
    }
}