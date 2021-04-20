package com.bysj.znzapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.beagle.component.activity.BaseCompatActivity;
import com.beagle.component.app.MvpManager;
import com.beagle.okhttp.OkHttpUtils;
import com.beagle.okhttp.callback.Response;
import com.beagle.okhttp.callback.ResponseCallBack;
import com.bysj.znzapp.R;
import com.bysj.znzapp.base.BaseApp;
import com.bysj.znzapp.bean.UserInfo;
import com.bysj.znzapp.config.HttpConfig;
import com.bysj.znzapp.data.UserInfoProvide;
import com.bysj.znzapp.persenter.activity.LoginActivityPresenter;
import com.bysj.znzapp.utils.DateUtil;
import com.bigkoo.pickerview.TimePickerView;
import com.bysj.znzapp.utils.SharedPreferencesUtil;
import com.thirdsdks.filedeal.ToastUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class FactActivity extends BaseCompatActivity {

    @BindView(R.id.tv_event_address_hint_back)
    TextView tvEventAddressHintBack;
    @BindView(R.id.ed_address)
    EditText edAddress;
    @BindView(R.id.tv_event_type_hint)
    TextView tvEventTypeHint;
    @BindView(R.id.ed_type)
    EditText edType;
    @BindView(R.id.tv_happen_time_hint)
    TextView tvHappenTimeHint;
    @BindView(R.id.tv_happen_time)
    TextView tvHappenTime;
    @BindView(R.id.ll_happen_time)
    LinearLayout llHappenTime;
    @BindView(R.id.tv_event_scope_hint)
    TextView tvEventScopeHint;
    @BindView(R.id.tv_event_scope)
    TextView tvEventScope;
    @BindView(R.id.tv_involve_number_hint)
    TextView tvInvolveNumberHint;
    @BindView(R.id.ed_involve_number)
    EditText edInvolveNumber;
    @BindView(R.id.tv_involve_place_hint)
    TextView tvInvolvePlaceHint;
    @BindView(R.id.ed_involve_place)
    EditText edInvolvePlace;
    @BindView(R.id.tv_involve_person_hint)
    TextView tvInvolvePersonHint;
    @BindView(R.id.ed_involve_person)
    EditText edInvolvePerson;
    @BindView(R.id.tv_event_content_hint)
    TextView tvEventContentHint;
    @BindView(R.id.ed_event_content)
    EditText edEventContent;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fact);
        ButterKnife.bind(this);
        initHead();
        initView();
    }

    @Override
    protected void initHead() {
        getCenterTextView().setText("事件上报");
        getCenterTextView().setTextColor(Color.WHITE);
        setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initView() {
        tvHappenTime.setText(DateUtil.getCurrentDateYYMMDDHHMMSS());
    }

    @OnClick({R.id.btn_report,R.id.tv_happen_time})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_report:
                if (TextUtils.isEmpty(edAddress.getText().toString())){
                    ToastUtil.showToast(context,"请输入事件地址");
                    return;
                }
                if (TextUtils.isEmpty(edType.getText().toString())){
                    ToastUtil.showToast(context,"请输入事件类型");
                    return;
                }
                if (TextUtils.isEmpty(tvHappenTime.getText().toString())){
                    ToastUtil.showToast(context,"请输入事件时间");
                    return;
                }
                if (TextUtils.isEmpty(edInvolveNumber.getText().toString())){
                    ToastUtil.showToast(context,"请输入事件人数");
                    return;
                }
                if (TextUtils.isEmpty(edInvolvePlace.getText().toString())){
                    ToastUtil.showToast(context,"请输入事件场所");
                    return;
                }
                if (TextUtils.isEmpty(edInvolvePerson.getText().toString())){
                    ToastUtil.showToast(context,"请输入事件人物");
                    return;
                }
                if (TextUtils.isEmpty(edEventContent.getText().toString())){
                    ToastUtil.showToast(context,"请输入事件描述");
                    return;
                }
                eventReport(edAddress.getText().toString(),edType.getText().toString(),tvHappenTime.getText().toString(),edInvolveNumber.getText().toString(),
                        edInvolvePlace.getText().toString(),edInvolvePerson.getText().toString(),edEventContent.getText().toString());

                break;
            case R.id.tv_happen_time:
                showTimePickerView(tvHappenTime);
                break;
        }

    }
    // 事件上报
    public void eventReport(String address, String eventType, String time, String number, String place, String person, String content) {
        BaseApp.getApp().clearCookie();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("eventAddress", address);
            jsonObject.put("eventType", eventType);
            jsonObject.put("eventTime", time);
            jsonObject.put("eventNumber", number);
            jsonObject.put("eventOrg", place);
            jsonObject.put("eventPerson", person);
            jsonObject.put("eventContent", content);
            jsonObject.put("eventFactPerson", UserInfoProvide.getUserInfo().getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        showMyDialog();
        OkHttpUtils.postString()
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .url(HttpConfig.saveEvent)
                .content(jsonObject.toString())
                .build()
                .execute(new ResponseCallBack<UserInfo>(UserInfo.class) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        closeMyDialog();
                    }

                    @Override
                    public void onResponse(Response<UserInfo> response, int id) {
                        if (TextUtils.equals(response.getSuccess(), "1")) {
                            ToastUtil.showToast(context, "上报成功");
                            onBackPressed();
                            finish();
                        }
                        closeMyDialog();
                    }
                });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
    // 设置时间
    public void showTimePickerView(final View view) {
        final TextView tv = (TextView) view;
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date2, View v) {// 选中事件回调
                String time = getTime(date2);
                tv.setText(time);
                //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                //tv.setTag(format.format(date2));
            }
        })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY_HOUR_MIN)// 默认全部显示
                .setCancelText("取消")// 取消按钮文字
                .setSubmitText("确定")// 确认按钮文字
                .setContentSize(20)// 滚轮文字大小
                .setTitleSize(20)// 标题文字大小
//              .setTitleText("请选择时间")// 标题文字
                .setOutSideCancelable(true)// 点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)// 是否循环滚动
                .setTextColorCenter(Color.BLACK)// 设置选中项的颜色
                .setTitleColor(Color.BLACK)// 标题文字颜色
                .setSubmitColor(Color.RED)// 确定按钮文字颜色
                .setCancelColor(Color.GRAY)// 取消按钮文字颜色
//              .setTitleBgColor(0xFF666666)// 标题背景颜色 Night mode
//              .setBgColor(0xFF333333)// 滚轮背景颜色 Night mode
//              .setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR) + 20)// 默认是1900-2100年
//              .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
//              .setRangDate(startDate,endDate)// 起始终止年月日设定
//              .setLabel("年","月","日","时","分","秒")
                .setLabel("年", "月", "日", "时", "分", "秒")
                .isCenterLabel(false) // 是否只显示中间选中项的label文字，false则每项item全部都带有label。
//              .isDialog(true)// 是否显示为对话框样式
                .build();
        pvTime.setDate(Calendar.getInstance());// 注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show();
    }
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 设置时间
    public String getTime(Date date) {// 可根据需要自行截取数据显示
        //2017-08-02 07:23:00
        return format.format(date);
    }
}