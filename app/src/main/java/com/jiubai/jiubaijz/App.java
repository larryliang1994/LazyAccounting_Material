package com.jiubai.jiubaijz;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.jiubai.jiubaijz.common.Config;
import com.jiubai.jiubaijz.net.VolleyUtil;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UmengTool;

/**
 * Created by Larry Howell on 2016/9/26.
 * <p>
 * 自定义application
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

        // 签名895049402aee7b7712ff06d84a94ed10
        UMShareAPI.get(this);
        PlatformConfig.setWeixin("wx698954241d0efc96", "022a1078d40881525588b4bf39867072");
        PlatformConfig.setQQZone("1105758610", "8TDWIwvUdHO5UCRC");
    }
}