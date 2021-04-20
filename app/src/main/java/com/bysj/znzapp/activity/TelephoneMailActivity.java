package com.bysj.znzapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.beagle.component.activity.BaseCompatActivity;
import com.bysj.znzapp.R;
import com.thirdsdks.filedeal.ToastUtil;
import com.thirdsdks.videoplay.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelephoneMailActivity extends BaseCompatActivity {

    private String flag = "";
    private EditText editText;
    private Button button;
    String num = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telephone_mail);
        initHead();
        initView();
    }

    @Override
    protected void initHead() {
        flag = getIntent().getStringExtra("flag");
        num = getIntent().getStringExtra("num");
        if (flag.equals("num")){
            getCenterTextView().setText("联系电话");
        }else if (flag.equals("mail")){
            getCenterTextView().setText("电子邮箱");
        }else {
            getCenterTextView().setText("用户姓名");
        }
        getCenterTextView().setTextColor(Color.WHITE);
    }

    @Override
    protected void initView() {
        editText = (EditText) findViewById(R.id.ed_telMail);
        button = (Button) findViewById(R.id.btn_sure);
        editText.setText(StringUtil.notNull(num));
        if (flag.equals("num")){
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(11)
            });
        }else if (flag.equals("mail")){
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
            editText.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(30)
            });
        }else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(10)
            });
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    // 提交
    private void submit(){
        if (flag.equals("num")){
            if (isMobileNO(editText.getText().toString())){
                Intent intent = new Intent(context,InfoActivity.class);
                intent.putExtra("num",editText.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }else {
                ToastUtil.showToast(context,"请输入正确的手机号码");
            }
        }else if (flag.equals("mail")){
            if (isEmail(editText.getText().toString())){
                Intent intent = new Intent(context,InfoActivity.class);
                intent.putExtra("mail",editText.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }else {
                ToastUtil.showToast(context,"请输入正确的邮箱");
            }
        }else {
            Intent intent = new Intent(context,InfoActivity.class);
            intent.putExtra("name",editText.getText().toString());
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    // 判断电话号码是否合法
    public static boolean isMobileNO(String mobiles) {
        boolean isValid = false;
        String expression = "(^(0[0-9]{2,3})+([2-9][0-9]{6,7})+([0-9]{1,4})?$)";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(mobiles);

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        if ( m.matches() || matcher.matches()){
            isValid = true;
        }
        return isValid;
    }

    // 判断邮箱是否合法
    public static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
