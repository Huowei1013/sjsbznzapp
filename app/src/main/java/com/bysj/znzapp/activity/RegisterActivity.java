package com.bysj.znzapp.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatButton;

import com.beagle.component.activity.BaseCompatActivity;
import com.beagle.component.app.MvpManager;
import com.bysj.znzapp.R;
import com.bysj.znzapp.persenter.activity.RegisterActivityPresenter;
import com.thirdsdks.filedeal.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bysj.znzapp.activity.TelephoneMailActivity.isEmail;
import static com.bysj.znzapp.activity.TelephoneMailActivity.isMobileNO;

public class RegisterActivity extends BaseCompatActivity {

    @BindView(R.id.tv_image)
    ImageView tvImage;
    @BindView(R.id.tv_name)
    EditText tvName;
    @BindView(R.id.tv_password)
    EditText tvPassword;
    @BindView(R.id.tv_phone)
    EditText tvPhone;
    @BindView(R.id.tv_email)
    EditText tvEmail;
    @BindView(R.id.btn_register)
    AppCompatButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiter);
        ButterKnife.bind(this);
        initHead();
        initView();
    }

    @Override
    protected void initHead() {
        getCenterTextView().setText("注册");
        getCenterTextView().setTextColor(Color.WHITE);
        setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    @Override
    protected void initView() {
        tvPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
        tvPhone.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(11)
        });
        tvEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        tvEmail.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(30)
        });
    }

    @OnClick(R.id.btn_register)
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_register:
                if (TextUtils.isEmpty(tvName.getText().toString())){
                    ToastUtil.showToast(context,"请输入用户名");
                    return;
                }
                if (TextUtils.isEmpty(tvPassword.getText().toString())){
                    ToastUtil.showToast(context,"请输入密码");
                    return;
                }
                if (isMobileNO(tvPhone.getText().toString())){
                    ToastUtil.showToast(context,"请输入正确手机号");
                    return;
                }
                if (TextUtils.isEmpty(tvEmail.getText().toString())){
                    ToastUtil.showToast(context,"请输入正确邮箱");
                    return;
                }
                findPresenter().setRegister(tvName.getText().toString(),tvPassword.getText().toString(),tvPhone.getText().toString(),tvEmail.getText().toString());
                break;
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        findPresenter();
    }

    // 绑定Presenter
    public RegisterActivityPresenter findPresenter() {
        RegisterActivityPresenter todoDetailPresenter = (RegisterActivityPresenter) MvpManager.findPresenter(RegisterActivity.this);
        if (todoDetailPresenter == null) {
            MvpManager.bindPresenter(RegisterActivity.this,
                    "com.bysj.znzapp.persenter.activity.RegisterActivityPresenter");
            todoDetailPresenter = (RegisterActivityPresenter) MvpManager.findPresenter(RegisterActivity.this);
        }
        return todoDetailPresenter;
    }
}