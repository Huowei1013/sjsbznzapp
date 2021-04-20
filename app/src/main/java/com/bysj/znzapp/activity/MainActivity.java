package com.bysj.znzapp.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.ShapeBadgeItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.beagle.component.activity.BaseCompatActivity;
import com.beagle.component.app.MvpManager;
import com.beagle.component.logger.LogCat;
import com.bysj.znzapp.BuildConfig;
import com.bysj.znzapp.R;
import com.bysj.znzapp.base.BaseApp;
import com.bysj.znzapp.bean.response.ConfigInfo;
import com.bysj.znzapp.bean.response.VersionInfo;
import com.bysj.znzapp.fragment.ApplicationFragment;
import com.bysj.znzapp.fragment.MessageFragment;
import com.bysj.znzapp.fragment.MyFragment;
import com.bysj.znzapp.fragment.ServiceFragment;
import com.bysj.znzapp.persenter.activity.MainActivityPresenter;
import com.bysj.znzapp.views.AnimationUtils;
import com.bysj.znzapp.views.DownLoadManage;
import com.bysj.znzapp.views.VersionUtil;
import com.thirdsdks.filedeal.ToastUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseCompatActivity implements BottomNavigationBar.OnTabSelectedListener {

    private ViewGroup parentView;
    @BindView(R.id.bnb_bottom_navibar)
    BottomNavigationBar bnbBottomNavibar;
    @BindView(R.id.layout_fragment)
    LinearLayout layoutFragment;
    private int tabSelectedIndex;
    private String mainApplicationTitle;
    private ApplicationFragment applicationFragment;
    private String mainServiceTitle;
    private ServiceFragment serviceFragment;
    private String mainMessageTitle;
    private MessageFragment messageFragment;
    private Button btnTopRight;
    private TextBadgeItem badgeItem;
    private String mainMyTitle;
    public MyFragment myFragment;
    public boolean isRefreshPersonInfo = false;// 是否刷新我的信息
    private VersionInfo version;
    private PopupWindow mPopupWindow;
    private ConfigInfo config;

    public ConfigInfo getConfig() {
        return config;
    }

    public void setConfig(ConfigInfo config) {
        this.config = config;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initHead();
        initView();
        initDefaultFragment(savedInstanceState);
    }

    // 初始化默认Fragment
    private void initDefaultFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (applicationFragment == null) applicationFragment = new ApplicationFragment();
            ft.add(R.id.layout_fragment, applicationFragment, applicationFragment.getClass().getName());
            ft.commit();
        } else {
            int selectIndex = savedInstanceState.getInt("index", 0);
            bnbBottomNavibar.selectTab(tabSelectedIndex);
            onTabSelected(selectIndex);
        }
    }

    @Override
    protected void initHead() {
        setLeftNavigationGone();
        mainApplicationTitle = (String) getResources().getText(R.string.mainApplicationTitle);
        mainServiceTitle = (String) getResources().getText(R.string.mainServiceTitle);
        mainMessageTitle = (String) getResources().getText(R.string.mainMessageTitle);

        mainMyTitle = (String) getResources().getText(R.string.mainMyTitle);
        getCenterTextView().setText(mainApplicationTitle);
        getCenterTextView().setTextColor(Color.WHITE);
        btnTopRight = getRightButton1();
        btnTopRight.setTextSize(12);
        btnTopRight.setVisibility(View.GONE);
        btnTopRight.setTextColor(Color.WHITE);
        btnTopRight.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void initView() {
        parentView = (ViewGroup) findViewById(android.R.id.content).getRootView();
        bnbBottomNavibar.setMode(BottomNavigationBar.MODE_FIXED);
        bnbBottomNavibar.setTabSelectedListener(this);
        bnbBottomNavibar.setActiveColor(R.color.blue_click);
        bnbBottomNavibar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bnbBottomNavibar.clearAll();
        badgeItem = new TextBadgeItem()
                .setBorderWidth(1)
                .setBackgroundColorResource(R.color.colorAccent)
                .setBorderColor(R.color.colorAccent)
                .setHideOnSelect(false);
        badgeItem.hide();
        ShapeBadgeItem circularBadgeItem = new ShapeBadgeItem()
                .setShapeColorResource(R.color.colorAccent)
                .setSizeInDp(this, 8, 8)
                .setShape(ShapeBadgeItem.SHAPE_OVAL)
                .setHideOnSelect(true);
        bnbBottomNavibar
                .addItem(new BottomNavigationItem(R.mipmap.main_home_selected, mainApplicationTitle).setInactiveIconResource(R.mipmap.main_home))
//                .addItem(new BottomNavigationItem(R.mipmap.main_service_selected, mainServiceTitle).setInactiveIconResource(R.mipmap.main_service))
                .addItem(new BottomNavigationItem(R.drawable.transparent, "事件"))
//                .addItem(new BottomNavigationItem(R.mipmap.main_message_selected, mainMessageTitle).setInactiveIconResource(R.mipmap.main_message).setBadgeItem(badgeItem))
                .addItem(new BottomNavigationItem(R.mipmap.main_my_selected, mainMyTitle).setInactiveIconResource(R.mipmap.main_my))
                .setFirstSelectedPosition(0)
                .initialise();
        ImageView img_addEvent = (ImageView) findViewById(R.id.img_addEvent);
        ViewCompat.setTranslationZ(img_addEvent, 120);
    }


    @Override
    public void onTabSelected(int position) {
        // 事件上报
        if (position == 1) {
            startActivityForResult(new Intent(MainActivity.this, FactActivity.class), 222);
            return;
        }
        tabSelectedIndex = position;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        List<Fragment> fragmentList = fragmentManager.getFragments();
        if (fragmentList != null && !fragmentList.isEmpty()) {
            for (Fragment fragment : fragmentList) {
                fragmentTransaction.hide(fragment);
            }
        }
        switch (position) {
            case 0: {// 热点应用
                btnTopRight.setVisibility(View.INVISIBLE);
                getCenterTextView().setText(mainApplicationTitle);
                if (applicationFragment == null) {
                    applicationFragment = (ApplicationFragment) fragmentManager.findFragmentByTag(ApplicationFragment.class.getName());
                    if (applicationFragment == null)
                        applicationFragment = new ApplicationFragment();
                    if (!applicationFragment.isAdded())
                        fragmentTransaction.add(R.id.layout_fragment, applicationFragment, applicationFragment.getClass().getName());
                }
                fragmentTransaction.show(applicationFragment);
                break;
            }
//            case 1: {// 服务概况
//                btnTopRight.setVisibility(View.INVISIBLE);
//                getCenterTextView().setText(mainServiceTitle);
//                if (serviceFragment == null) {
//                    serviceFragment = (ServiceFragment) fragmentManager.findFragmentByTag(ServiceFragment.class.getName());
//                    if (serviceFragment == null)
//                        serviceFragment = new ServiceFragment();
//                    if (!serviceFragment.isAdded())
//                        fragmentTransaction.add(R.id.layout_fragment, serviceFragment, serviceFragment.getClass().getName());
//                }
//                fragmentTransaction.show(serviceFragment);
//                serviceFragment.refreshData();

//                break;
//            }
            case 3: {// 我的消息
                btnTopRight.setVisibility(View.VISIBLE);
                getCenterTextView().setText(mainMessageTitle);
                if (messageFragment == null) {
                    messageFragment = (MessageFragment) fragmentManager.findFragmentByTag(MessageFragment.class.getName());
                    if (messageFragment == null)
                        messageFragment = new MessageFragment();
                    if (!messageFragment.isAdded())
                        fragmentTransaction.add(R.id.layout_fragment, messageFragment, messageFragment.getClass().getName());
                }
                fragmentTransaction.show(messageFragment);
                btnTopRight.setVisibility(View.VISIBLE);// 消息显示右上角按钮
                btnTopRight.setText("全部已读");
                btnTopRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {// 按钮功能事件绑定
                        if (messageFragment != null) messageFragment.markAll();
                    }
                });
                break;
            }
            case 2: {// 个人中心
                btnTopRight.setVisibility(View.INVISIBLE);
                getCenterTextView().setText(mainMyTitle);
                if (myFragment == null) {
                    myFragment = (MyFragment) fragmentManager.findFragmentByTag(MyFragment.class.getName());
                    if (myFragment == null)
                        myFragment = new MyFragment();
                    if (!myFragment.isAdded())
                        fragmentTransaction.add(R.id.layout_fragment, myFragment, myFragment.getClass().getName());
                }
                fragmentTransaction.show(myFragment);
                break;
            }
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myFragment != null && isRefreshPersonInfo) {
            myFragment.refreshInfo();
        }
        updateMessage();
    }

    // 更新未读消息
    public void updateMessage() {
        findPresenter().navisCount();
    }

    // 更新未读消息数
    public void setNavisCount(int navisCount) {
        if (navisCount != 0) {
            badgeItem.show();
            badgeItem.show();
            badgeItem.setHideOnSelect(false);
            if (navisCount > 99) {
                badgeItem.setText("99+");
            } else {
                badgeItem.setText(navisCount + "");
            }
        } else {
            badgeItem.hide();
        }
    }
    private long firstTime = 0;
    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime < 2000) {
            super.onBackPressed();
            BaseApp.getApp().exitApp();
        } else {
            ToastUtil.showToast(MainActivity.this, "再按一次退出程序");
            firstTime = System.currentTimeMillis();
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        findPresenter();
    }

    // 检查版本
    public void checkVersion(final VersionInfo version) {
        this.version = version;
        try {
            String latestVersion = version.getLatestVersion();
            String currentVersion = VersionUtil.getVersion(this);
            if (VersionUtil.compareVersion(latestVersion, currentVersion) == 1) {
                versionPopupWindow();// 版本不同，强制升级
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 新版本提示
    private void versionPopupWindow() {
        View popView = LayoutInflater.from(MainActivity.this).inflate(R.layout.check_version, null, false);
        ButterKnife.bind(this, popView);
        TextView tvVersion = (TextView) popView.findViewById(R.id.tv_version);
        TextView tvVersionHint = (TextView) popView.findViewById(R.id.tv_content);
        TextView tvContent = (TextView) popView.findViewById(R.id.tv_version_hint);
        Button btnVersionUpdate = (Button) popView.findViewById(R.id.btn_version_update);
        btnVersionUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadNewVersion(context, BuildConfig.APPLICATION_ID);
            }
        });
        tvVersion.setText(version.getNextVersion());
        tvContent.setText(version.getDescription());
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = getWindowManager().getDefaultDisplay().getHeight();
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        mPopupWindow = new PopupWindow(popView, (int) (width * 0.9), (int) (height * 0.7), true);
        if (version.isNeedUpdate()) {
            mPopupWindow.setFocusable(false);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOutsideTouchable(false);
        } else {
            tvVersionHint.setVisibility(View.GONE);
            mPopupWindow.setFocusable(true);
            popView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    AnimationUtils.darkBackgroundColor(getWindow(), 1f);
                }
            });
        }
        if (parentView != null) {
            LogCat.e("-----------------------showAtLocation-----------------------");
            if (!isFinishing()) {
                parentView.post(new Runnable() {
                    @Override
                    public void run() {
                        mPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
                    }
                });
            }
        }
        AnimationUtils.darkBackgroundColor(getWindow(), 0.4f);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 222) {
            onTabSelected(tabSelectedIndex);
            bnbBottomNavibar.selectTab(tabSelectedIndex);
        }
    }
    // 下载新版本
    public void downloadNewVersion(Context context, String packageName) {
        if (version != null && !TextUtils.isEmpty(version.getAddress())) {
            if (version.getAddress().contains("#") && version.getAddress().endsWith("#apk")) {
                String downloadFilepath = DownLoadManage.downloadFilepath;
                String address = version.getAddress();
                address = address.substring(0, address.length() - 4);
                if (downloadFilepath != null) {
                    File file = new File(downloadFilepath);
                    if (file.exists()) {
                        DownLoadManage.install(downloadFilepath);
                    } else {
                        DownLoadManage.downApk(address, "grid.apk", true);
                    }
                } else {
                    DownLoadManage.downApk(address, "grid.apk", false);
                }
            } else {
                Uri uri = Uri.parse(version.getAddress());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    context.startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 绑定Presenter
    public MainActivityPresenter findPresenter() {
        MainActivityPresenter mainActivityPresenter = (MainActivityPresenter) MvpManager.findPresenter(MainActivity.this);
        if (mainActivityPresenter == null) {
            MvpManager.bindPresenter(MainActivity.this,
                    "com.bysj.znzapp.persenter.activity.MainActivityPresenter");
            mainActivityPresenter = (MainActivityPresenter) MvpManager.findPresenter(MainActivity.this);
        }
        return mainActivityPresenter;
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }
}