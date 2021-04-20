package com.bysj.znzapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.beagle.component.activity.BaseCompatFragment;
import com.beagle.component.app.MvpManager;
import com.bysj.znzapp.R;
import com.bysj.znzapp.activity.InfoActivity;
import com.bysj.znzapp.activity.MainActivity;
import com.bysj.znzapp.activity.SettingActivity;
import com.bysj.znzapp.config.HttpConfig;
import com.bysj.znzapp.persenter.fragment.MyFragmentPresenter;
import com.bysj.znzapp.utils.GlideTransform;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends BaseCompatFragment implements View.OnClickListener {

    @BindView(R.id.iv_my_photo)
    ImageView ivMyPhoto;
    @BindView(R.id.tv_my_name)
    TextView tvMyName;
    @BindView(R.id.tv_my_department)
    TextView tvMyDepartment;
    @BindView(R.id.tv_my_post)
    TextView tvMyPost;
    @BindView(R.id.rl_my_info)
    RelativeLayout rlMyInfo;
    @BindView(R.id.ll_firstLine)
    LinearLayout llFirstLine;
    @BindView(R.id.iv_my_setting)
    ImageView ivMySetting;
    @BindView(R.id.rl_my_setting)
    RelativeLayout rlMySetting;
    @BindView(R.id.ll_secondLine)
    LinearLayout llSecondLine;
    private final static int REQUEST_CODE_IMAGE = 111;
    private String photoUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_IMAGE: {// 头像
                    findPresenter().getUserInfo();
                    break;
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        findPresenter();
    }

    // 设置名称
    public void setName(String str) {
        tvMyName.setText(str);
    }

    // 设置头像
    public void setPhoto(String url) {
        this.photoUrl = url;
        Glide.with(context)
                .load(HttpConfig.fileDownloadServer + url)
                .placeholder(R.mipmap.my_photo)
                .thumbnail(0.2f)
                .centerCrop()
                .transform(new GlideTransform.GlideCircleTransform(getContext()))
                .error(R.mipmap.my_photo)
                .into(ivMyPhoto);
    }

    // 设置单位
    public void setDepartment(String str) {
        tvMyDepartment.setText(str + ":");
    }

    // 设置岗位
    public void setPost(String str) {
        tvMyPost.setText(str);
    }

    // 刷新信息
    public void refreshInfo() {
        findPresenter().getUserInfo();
    }

    // 绑定Presenter
    public MyFragmentPresenter findPresenter() {
        MyFragmentPresenter myFragmentPresenter = (MyFragmentPresenter) MvpManager.findPresenter(this);
        if (myFragmentPresenter == null) {
            MvpManager.bindPresenter(this, "com.bysj.znzapp.persenter.fragment.MyFragmentPresenter");
            myFragmentPresenter = (MyFragmentPresenter) MvpManager.findPresenter(this);
        }
        return myFragmentPresenter;
    }

    @OnClick({R.id.rl_my_setting,R.id.ll_firstLine})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_firstLine:// 头像
                Intent intent = new Intent(context, InfoActivity.class);
                intent.putExtra("photo", photoUrl);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
                break;
            case R.id.rl_my_setting:// 设置
                ((MainActivity) context).isRefreshPersonInfo = true;
                Intent set = new Intent(context, SettingActivity.class);
                startActivity(set);
                break;
        }
    }
}
