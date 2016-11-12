package com.jiubai.jiubaijz.wxapi;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Log.e("ard", "WXEntryActivity onCreate"); // 日志中从未出现过，忽略
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("ard", "WXEntryActivity onStart"); // 日志中从未出现过，忽略
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("ard", "WXEntryActivity onResume"); // 日志中从未出现过，忽略
    }

    @Override
    public void onResp(BaseResp resp) {
        super.onResp(resp); // 必须的。

        // 以下就是我们查找问题所在用到的... log而已
        Log.e("ard",
                "ERR_OK:" + BaseResp.ErrCode.ERR_OK // 0
                        + ", ERR_COMM:" + BaseResp.ErrCode.ERR_COMM // 1
                        + ", ERR_USER_CANCEL:" + BaseResp.ErrCode.ERR_USER_CANCEL // 2
                        + ", ERR_SENT_FAILED:" + BaseResp.ErrCode.ERR_SENT_FAILED // 3
                        + ", ERR_AUTH_DENIED:" + BaseResp.ErrCode.ERR_AUTH_DENIED // 4
                        + ", ERR_UNSUPPORT:" + BaseResp.ErrCode.ERR_UNSUPPORT // 5
        );

        Log.e("ard", "WXEntryActivity onResp errcode:" + resp.errCode);
        Log.e("ard", "WXEntryActivity onResp errstr:" + resp.errStr);
        Log.e("ard", "WXEntryActivity onResp transaction:" + resp.transaction);
    }

    @Override
    public void onReq(BaseReq req) {
        super.onReq(req);

        Log.e("ard", "WXEntryActivity onReq");
    }

}
