package com.jiubai.jiubaijz;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.umeng.socialize.PlatformConfig;

/**
 * Created by Larry Howell on 2016/9/26.
 */

public class App extends Application {
    public static SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();

        sp = getApplicationContext().getSharedPreferences("sp", Context.MODE_PRIVATE);

        initService();
    }

    public void initService() {
        VolleyUtil.initRequestQueue(getApplicationContext());

        PlatformConfig.setWeixin("wx698954241d0efc96", "022a1078d40881525588b4bf39867072");
        PlatformConfig.setQQZone("1105686136", "h09hT7GutWorykbk");
    }
}
