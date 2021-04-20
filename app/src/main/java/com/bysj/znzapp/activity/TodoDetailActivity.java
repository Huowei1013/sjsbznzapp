package com.bysj.znzapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beagle.component.activity.BaseCompatActivity;
import com.beagle.component.app.MvpManager;
import com.bysj.znzapp.R;
import com.bysj.znzapp.bean.response.MyReportInfo;
import com.bysj.znzapp.bean.response.TodoDetailInfo;
import com.bysj.znzapp.persenter.activity.TodoDetailActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoDetailActivity extends BaseCompatActivity {

    @BindView(R.id.tv_tv_no_hint)
    TextView tvTvNoHint;
    @BindView(R.id.tv_no)
    TextView tvNo;
    @BindView(R.id.btn_former_deals)
    Button btnFormerDeals;
    @BindView(R.id.prl_former_deals)
    LinearLayout prlFormerDeals;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_grid)
    TextView tvGrid;
    @BindView(R.id.tv_fact_person)
    TextView tvFactPerson;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_place)
    TextView tvPlace;
    @BindView(R.id.tv_content)
    EditText tvContent;
    @BindView(R.id.ll)
    LinearLayout ll;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);
        ButterKnife.bind(this);
        initHead();
        initView();
        findPresenter().getTodoDetail(eventId);
    }

    @Override
    protected void initHead() {
        getCenterTextView().setText("事件详情");
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
        eventId = getIntent().getIntExtra("event_num",0);
    }

    /**
     * 带上ID参数启动
     *
     * @param context
     * @param id
     */
    public static void startActivity(Context context, int id) {
        Intent intent = new Intent(context, TodoDetailActivity.class);
        intent.putExtra("event_num", id);
        context.startActivity(intent);
    }

    /**
     * 展示数据
     *
     * @param todoDetailInfo
     */
    public void setEvent(MyReportInfo.RowsBean todoDetailInfo) {
        tvNo.setText(todoDetailInfo.getEventId());
        tvType.setText(todoDetailInfo.getEventType());
        tvFactPerson.setText(todoDetailInfo.getEventFactPerson());
        tvDate.setText(todoDetailInfo.getEventTime());
        tvPlace.setText(todoDetailInfo.getEventAddress());
        tvContent.setText(todoDetailInfo.getEventContent());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        findPresenter();
    }

    // 绑定Presenter
    public TodoDetailActivityPresenter findPresenter() {
        TodoDetailActivityPresenter todoDetailPresenter = (TodoDetailActivityPresenter) MvpManager.findPresenter(TodoDetailActivity.this);
        if (todoDetailPresenter == null) {
            MvpManager.bindPresenter(TodoDetailActivity.this,
                    "com.bysj.znzapp.persenter.activity.TodoDetailActivityPresenter");
            todoDetailPresenter = (TodoDetailActivityPresenter) MvpManager.findPresenter(TodoDetailActivity.this);
        }
        return todoDetailPresenter;
    }
}