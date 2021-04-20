package com.bysj.znzapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.Guideline;

import com.beagle.component.activity.BaseCompatActivity;
import com.beagle.component.app.MvpManager;
import com.bysj.znzapp.R;
import com.bysj.znzapp.base.BaseApp;
import com.bysj.znzapp.persenter.activity.LoginActivityPresenter;
import com.bysj.znzapp.utils.SharedPreferencesUtil;
import com.bysj.znzapp.utils.TokenUtils;
import com.bysj.znzapp.views.UnderlineTextView;
import com.thirdsdks.filedeal.ToastUtil;
import com.thirdsdks.videoplay.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class LoginActivity extends BaseCompatActivity {

    @BindView(R.id.img_background)
    ImageView imgBackground;
    @BindView(R.id.txt_username)
    AppCompatEditText txtUsername;
    @BindView(R.id.txt_password)
    AppCompatEditText txtPassword;
    @BindView(R.id.btn_login)
    AppCompatButton btnLogin;
    @BindView(R.id.guideline)
    Guideline guideline;
    @BindView(R.id.login_icon)
    ImageView loginIcon;
    @BindView(R.id.img_username)
    ImageView imgUsername;
    @BindView(R.id.img_password)
    ImageView imgPassword;
    @BindView(R.id.txt_sendMessage)
    TextView txtSendMessage;
    @BindView(R.id.tab_account)
    UnderlineTextView tabAccount;
    @BindView(R.id.tab_phone)
    UnderlineTextView tabPhone;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    public final static int ACCOUNT = 0;
    public final static int PHONE = 1;
    private int loginFlag;
    private int clickCount;
    private String strUsername;
    private String strPassword;
    private boolean isCheckDeviceCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getSupportActionBar().hide();// 去掉ActionBar
        initView();
        checkOutLogin();
    }

    // 获取外部的传入的用户名和密码
    private void checkOutLogin() {
        strUsername = getIntent().getStringExtra("username");
        strPassword = getIntent().getStringExtra("password");
        String token = getIntent().getStringExtra("token");
        String phone = getIntent().getStringExtra("phone");
        if (!TextUtils.isEmpty(strUsername) && !TextUtils.isEmpty(strPassword)) {
            isCheckDeviceCode = true;
            TokenUtils.setToken(this, token);
            txtUsername.setText(strUsername);
            txtPassword.setText(strPassword);
            SharedPreferencesUtil.putString(LoginActivity.this, "zz2zx", "zz2zxlogin");
        }
    }

    @Override
    protected void initHead() {

    }

    @Override
    protected void initView() {
        loginFlag = SharedPreferencesUtil.getInt(LoginActivity.this, "loginFlag", ACCOUNT);
        setLoginFlag(loginFlag);
        String beforeStr = getString(R.string.null_user_name);
        SpannableStringBuilder style = new SpannableStringBuilder();
        style.append(beforeStr).append(getString(R.string.register));
        style.setSpan(registerSpan, beforeStr.length(), style.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置部分文字颜色
        ForegroundColorSpan foregroundColorSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.shop_login_color));
        style.setSpan(foregroundColorSpan1, beforeStr.length(), style.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置光标如何移动计量的方法
        tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
        //配置给TextView
        tvRegister.setText(style);

    }
    //注册
    private final ClickableSpan registerSpan = new ClickableSpan() {
        @Override
        public void onClick(@NonNull View widget) {
            startActivity(new Intent(context, RegisterActivity.class));
        }

        //去除连接下划线
        @Override
        public void updateDrawState(TextPaint ds) {
            /**set textColor**/
            ds.setColor(ds.linkColor);
            /**Remove the underline**/
            ds.setUnderlineText(false);
        }
    };
    @OnFocusChange({R.id.txt_username, R.id.txt_password})
    public void onFocusChange(View view,boolean hasFocus){
        switch (view.getId()) {
            case R.id.txt_username:
                if (hasFocus) {
                    imgUsername.setImageResource(R.mipmap.login_name_selected);
                    btnLogin.setBackgroundColor(getResources().getColor(R.color.blue_click));
                } else {
                    imgUsername.setImageResource(R.mipmap.login_name);
                }
                break;
            case R.id.txt_password:
                if (hasFocus) {
                    imgPassword.setImageResource(R.mipmap.login_password_selected);
                    btnLogin.setBackgroundColor(getResources().getColor(R.color.blue_click));
                } else {
                    imgPassword.setImageResource(R.mipmap.login_password);
                }
                break;
        }
    }
    @OnClick({R.id.btn_login, R.id.tab_account, R.id.tab_phone,R.id.txt_username, R.id.txt_password})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (loginFlag == ACCOUNT) {
                    loginByAccount();
                } else if (loginFlag == PHONE) {
                    loginByCode();
                }
                break;
            case R.id.tab_account:
                // 账号登录
                setLoginFlag(ACCOUNT);
                break;
            case R.id.tab_phone:
                // 手机登录
                setLoginFlag(PHONE);
                break;
        }
    }

    // 设置登陆类型
    public void setLoginFlag(int type) {
        loginFlag = type;
        SharedPreferencesUtil.putInt(LoginActivity.this, "loginFlag", type);
        if (loginFlag == PHONE) {
            txtSendMessage.setVisibility(View.VISIBLE);
            txtUsername.setText("");
            txtPassword.setText("");
            txtUsername.setHint("手机号");
            txtPassword.setHint("验证码");
            txtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            tabAccount.setUnderlineVisible(false);
            tabPhone.setUnderlineVisible(true);
        } else if (loginFlag == ACCOUNT) {
            txtSendMessage.setVisibility(View.GONE);
            txtUsername.setText("");
            txtPassword.setText("");
            txtUsername.setHint("用户名");
            txtPassword.setHint("密码");
            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            tabAccount.setUnderlineVisible(true);
            tabPhone.setUnderlineVisible(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String ticket = "";
        Intent intent = getIntent();
        if (intent != null) {
            ticket = intent.getStringExtra("ticket");
            if (TextUtils.isEmpty(ticket)) {
                Uri uri = intent.getData();
                if (uri != null) {
                    ticket = uri.getQueryParameter("ticket");
                }
            }
        }
        // 单点登录中有ticket有值
        if (!TextUtils.isEmpty(ticket) && !TextUtils.equals("null", ticket)) {
//            findPresenter().getTicketToken(ticket);
        } else {
            // 走正常的逻辑
            if (clickCount <= 0) { //没有点击过登陆
                autoLogin();
            }
        }
    }

    // 自动登录
    private void autoLogin() {
        String username = SharedPreferencesUtil.getString(LoginActivity.this, "username", "");
        String password = SharedPreferencesUtil.getString(LoginActivity.this, "password", "");
        if (!TextUtils.isEmpty(username)) {
            txtUsername.setText(username);
            Intent intent = getIntent();
            if (intent != null
                    && !intent.hasExtra("exit")
                    && !TextUtils.isEmpty(password)) {// 自动登陆，不是来自从设置里退出的
                txtPassword.setText(password);
                loginByAccount();
            }
        }
    }

    // 账号登陆
    void loginByAccount() {
        String username = txtUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            ToastUtil.showToast(LoginActivity.this, "请输入账号");
            return;
        }
        String password = txtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(LoginActivity.this, "请输入密码");
            return;
        }
        findPresenter().loginByAccount(username, password);
    }

    // 手机号登陆
    void loginByCode() {
        String username = txtUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            ToastUtil.showToast(LoginActivity.this, "请输入手机号");
            return;
        }
        String password = txtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(LoginActivity.this, "请输入验证码");
            return;
        }
        if (!StringUtil.isNumRic(username) || username.length() < 8) {
            ToastUtil.showToast(LoginActivity.this, "请输入正确的手机号格式");
            return;
        }
        findPresenter().loginByCode(username, password);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        findPresenter();
    }

    // 绑定Presenter
    public LoginActivityPresenter findPresenter() {
        LoginActivityPresenter loginActivityPresenter = (LoginActivityPresenter) MvpManager.findPresenter(LoginActivity.this);
        if (loginActivityPresenter == null) {
            MvpManager.bindPresenter(LoginActivity.this,
                    "com.bysj.znzapp.persenter.activity.LoginActivityPresenter");
            loginActivityPresenter = (LoginActivityPresenter) MvpManager.findPresenter(LoginActivity.this);
        }
        return loginActivityPresenter;
    }

    // 切换到主页面
    public void goToMainActivity() {
//        if (BaseApp.getApp().getTrack() != null
//                && UserInfoProvide.getUserInfo() != null) {
//            BaseApp.getApp().getTrack().tryStartService(UserInfoProvide.getUserInfo().getAccount());
//        }
        // 保证有用户信息
//        UserOnlineService.startAllService(LoginActivity.this);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//        MeetingFunc.onAccountLogin(UserInfoProvide.getUserInfo().getAccount());
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("exit")) {
            BaseApp.getApp().exitApp();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            clickCount = 1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}