package com.bysj.znzapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bysj.znzapp.R;
import com.bysj.znzapp.utils.SharedPreferencesUtil;

/**
 * 广告页
 */
public class SplashActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        Intent ticketIntent = getIntent();
//        String ticket = "";
//        if (ticketIntent != null) {
//            ticket = ticketIntent.getStringExtra("ticket");
//            String errorCode = ticketIntent.getStringExtra("errorCode");
//            LogCat.e("ticket------------------>" + ticket);
//            LogCat.e("errorCode--------------->" + errorCode);
//            if (!TextUtils.isEmpty(ticket) && !TextUtils.equals("null", ticket)) {
//                // 拿到外部应用启动的ticket
//                BaseApp.getApp().clearCookie();
//            } else if (TextUtils.equals("null", ticket)) {// 外部应用启动掌心时，ticket无效
//                ToastUtil.showToast(this, "ticker无效，请重新启动");
//            }
//        }
//        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
//            if (!TextUtils.isEmpty(ticket) && !TextUtils.equals("null", ticket)) {
//                Intent intent = new Intent(this, LoginActivity.class);
//                intent.putExtra("ticket", ticket);
//                startActivity(intent);
//                overridePendingTransition(0, 0);
//            }
//            finish();
//            return;
//        }
        boolean isFirst = SharedPreferencesUtil.getBoolean(this, "isFirst", true);
        if (isFirst) {// 第一次打开，需要显示Splash的界面
//            setContentView(R.layout.activity_splash);
            SharedPreferencesUtil.putBoolean(this, "isFirst", false);
            final Intent intent = new Intent(this, LoginActivity.class);
//            intent.putExtra("ticket", ticket);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }, 2000);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
//            intent.putExtra("ticket", ticket);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }
}