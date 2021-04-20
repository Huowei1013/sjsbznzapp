package com.thirdsdks.pickphoto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.bilibili.boxing.AbsBoxingActivity;
import com.bilibili.boxing.AbsBoxingViewFragment;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.thirdsdks.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaotao on 2021/01/04 20:52.
 */

public class BoxingNewActivity extends AbsBoxingActivity {

    private BoxingNewFragment mPickerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boxing);
        createToolbar();
        setTitleTxt(getBoxingConfig());
    }

    @NonNull
    @Override
    public AbsBoxingViewFragment onCreateBoxingView(ArrayList<BaseMedia> medias) {
        mPickerFragment = (BoxingNewFragment) getSupportFragmentManager().findFragmentByTag(BoxingNewFragment.TAG);
        if (mPickerFragment == null) {
            mPickerFragment = (BoxingNewFragment) BoxingNewFragment.newInstance().setSelectedBundle(medias);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, mPickerFragment, BoxingNewFragment.TAG).commit();
        }
        return mPickerFragment;
    }

    private void createToolbar() {
        Toolbar bar = (Toolbar) findViewById(R.id.nav_top_bar);
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setTitleTxt(BoxingConfig config) {
        TextView titleTxt = (TextView) findViewById(R.id.pick_album_txt);
        if (config.getMode() == BoxingConfig.Mode.VIDEO) {
            titleTxt.setText(R.string.boxing_video_title);
            titleTxt.setCompoundDrawables(null, null, null, null);
            return;
        }
        mPickerFragment.setTitleTxt(titleTxt);
    }

    @Override
    public void onBoxingFinish(Intent intent, @Nullable List<BaseMedia> medias) {
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
