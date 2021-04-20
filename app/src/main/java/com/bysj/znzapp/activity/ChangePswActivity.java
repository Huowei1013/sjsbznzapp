package com.bysj.znzapp.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.beagle.component.activity.BaseCompatActivity;
import com.beagle.component.app.AppManager;
import com.beagle.component.app.MvpManager;
import com.beagle.component.logger.LogCat;
import com.bysj.znzapp.R;
import com.bysj.znzapp.persenter.activity.ChangePswActivityPresenter;
import com.bysj.znzapp.persenter.activity.ChangePswActivityPresenter;
import com.thirdsdks.filedeal.ToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePswActivity  extends BaseCompatActivity {

    private EditText et_old_password;
    private EditText et_new_password;
    private EditText et_confirm_password;
    private CheckBox ck;
    private ViewGroup parentView;
    private PopupWindow mPopupWindow;
    private String passwordA = "";
    private String passwordB = "";
    private String passwordC = "";

    String regExp_0 = "[A-Z]{1,}";
    String regExp_1 = "[a-z]{1,}";
    String regExp1 = "[0-9]{1,}";
    String regExp2 = "[A-Za-z]{1,}";
    String regExp3 = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_psw);
        initHead();
        initView();
    }

    @Override
    protected void initHead() {
        getCenterTextView().setText("修改密码");
        getCenterTextView().setTextColor(Color.WHITE);
    }

    @Override
    protected void initView() {
        et_old_password = (EditText) findViewById(R.id.et_old_password);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_confirm_password = (EditText) findViewById(R.id.et_confirm_password);
        ck = (CheckBox) findViewById(R.id.ck);
        ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    et_old_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et_new_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et_confirm_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    et_old_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        parentView = (ViewGroup) findViewById(android.R.id.content);
        Button bt_submit = (Button) findViewById(R.id.bt_submit);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ensure();
            }
        });
    }

    private void ensure() {
        passwordA = et_old_password.getText().toString().trim();
        passwordB = et_new_password.getText().toString().trim();
        passwordC = et_confirm_password.getText().toString().trim();
        if (TextUtils.isEmpty(passwordA)) {
            ToastUtil.showToast(AppManager.currentActivity(), "原密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(passwordB)) {
            ToastUtil.showToast(AppManager.currentActivity(), "新密码不能为空");
            return;
        }
//        if (passwordB.length() >= 8 && passwordB.length() <= 20
//                && hasReg(regExp_0, passwordB) && hasReg(regExp_1, passwordB)
//                && hasReg(regExp1, passwordB) && hasReg(regExp2, passwordB) && hasReg(regExp3, passwordB)) {
//        } else {
//            ToastUtil.showToast(AppManager.currentActivity(), "密码必须是8-20位长度大小写英文字母、数字与符号组成");
//            return;
//        }
//        if (TextUtils.isEmpty(passwordC)) {
//            ToastUtil.showToast(AppManager.currentActivity(), "确认密码不能为空");
//            return;
//        }
//        if (passwordC.length() >= 8 && passwordC.length() <= 20
//                && hasReg(regExp_0, passwordC) && hasReg(regExp_1, passwordC)
//                && hasReg(regExp1, passwordC) && hasReg(regExp2, passwordC) && hasReg(regExp3, passwordC)) {
//        } else {
//            ToastUtil.showToast(AppManager.currentActivity(), "密码必须是8-20位长度大小写英文字母、数字与符号组成");
//            return;
//        }
        if (TextUtils.equals(passwordA, passwordB)) {
            ToastUtil.showToast(context, "原始密码和新密码不能相同");
            return;
        }
        if (!TextUtils.equals(passwordB, passwordC)) {
            View pop = LayoutInflater.from(this).inflate(R.layout.pop_change_psw, parentView, false);
            Button besure = (Button) pop.findViewById(R.id.besure);
            besure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });
            int width = getWindowManager().getDefaultDisplay().getWidth();
            int height = getWindowManager().getDefaultDisplay().getHeight();
            mPopupWindow = new PopupWindow(pop, width - 150, height / 4, true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    darkBackgroundColor(getWindow(), 1f);
                }
            });
            mPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
            darkBackgroundColor(getWindow(), 0.4f);
        }
            findPresenter().ensure(passwordA, passwordB, passwordC);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        findPresenter();
    }

    // 绑定Presenter
    public ChangePswActivityPresenter findPresenter() {
        ChangePswActivityPresenter todoDetailPresenter = (ChangePswActivityPresenter) MvpManager.findPresenter(ChangePswActivity.this);
        if (todoDetailPresenter == null) {
            MvpManager.bindPresenter(ChangePswActivity.this,
                    "com.bysj.znzapp.persenter.activity.ChangePswActivityPresenter");
            todoDetailPresenter = (ChangePswActivityPresenter) MvpManager.findPresenter(ChangePswActivity.this);
        }
        return todoDetailPresenter;
    }
    private void darkBackgroundColor(Window window, Float color) {
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = color;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(lp);
    }

    public boolean hasReg(String reg, String str) {
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        boolean b = m.find();
        LogCat.e(reg + "--------" + b + "----------");
        return b;
    }
}
