package com.bysj.znzapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.beagle.component.activity.BaseCompatActivity;
import com.bysj.znzapp.R;

public class VersionUpdateActivity extends BaseCompatActivity {

    private boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_update);
        initHead();
        initView();
    }

    @Override
    protected void initHead() {
        isUpdate = getIntent().getBooleanExtra("isUpdate",false);
    }

    @Override
    protected void initView() {
        if (isUpdate) {
            getCenterTextView().setText("版本更新记录");
        }else {
            getCenterTextView().setText("关于智能针");
        }
    }
}