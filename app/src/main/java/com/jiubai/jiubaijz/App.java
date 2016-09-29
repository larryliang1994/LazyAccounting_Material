package com.jiubai.jiubaijz;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.jiubai.jiubaijz.common.Config;
import com.jiubai.jiubaijz.net.VolleyUtil;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;

/**
 * Created by Larry Howell on 2016/9/26.
 *
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

        PlatformConfig.setWeixin("wx698954241d0efc96", "022a1078d40881525588b4bf39867072");
        PlatformConfig.setQQZone("1105686136", "h09hT7GutWorykbk");

        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {}

            @Override
            public void onFailure(String s, String s1) {}
        });
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void handleMessage(Context context, UMessage uMessage) {
                super.handleMessage(context, uMessage);

                String url = uMessage.extra.get("url");

                if (url != null && !"".equals(url)) {
                    Config.shouldLoadUrl = url;
                }
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        mPushAgent.setDebugMode(true);
    }
}