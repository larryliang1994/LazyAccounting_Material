package com.jiubai.jiubaijz.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jiubai.jiubaijz.App;
import com.jiubai.jiubaijz.common.UtilBox;

/**
 * Created by Larry Howell on 2016/9/30.
 */

public class EntryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int storedVersion = App.sp.getInt("version", -1);
        int currentVersion = UtilBox.getPackageInfo(this).versionCode;

        if (storedVersion < currentVersion) {
            startActivity(new Intent(this, GuidePageActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }

        finish();
    }
}
