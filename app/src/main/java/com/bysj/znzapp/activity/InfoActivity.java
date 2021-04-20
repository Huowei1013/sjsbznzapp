package com.bysj.znzapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beagle.component.activity.BaseCompatActivity;
import com.beagle.component.app.MvpManager;
import com.beagle.component.logger.LogCat;
import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.loader.IBoxingMediaLoader;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bumptech.glide.Glide;
import com.bysj.znzapp.R;
import com.bysj.znzapp.config.HttpConfig;
import com.bysj.znzapp.data.UserInfoProvide;
import com.bysj.znzapp.persenter.activity.InfoActivityPresenter;
import com.bysj.znzapp.utils.GlideTransform;
import com.thirdsdks.pickphoto.BoxingGlideLoader;
import com.thirdsdks.pickphoto.BoxingNewActivity;
import com.thirdsdks.videoplay.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends BaseCompatActivity implements View.OnClickListener {

    private ImageView image, iv_right;
    private TextView name;
    private TextView grid;
    private TextView num;
    private TextView mail;
    private final String[] items = {"选择照片", "取消"};
    private AlertDialog alertDialog;
    private final static int REQUEST_CODE_CHOOSE = 233;
    private final List<BaseMedia> selectedMedias = new ArrayList<>();
    private final ArrayList<String> selectedPhotos = new ArrayList<>();
    private Button btn;
    private String photoUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initHead();
        initView();
    }

    @Override
    protected void initHead() {
        getCenterTextView().setText("个人信息");
        getCenterTextView().setTextColor(Color.WHITE);
        btn = getRightButton1();
        btn.setText("保存");
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setVisibility(View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoActivityPresenter presenter = (InfoActivityPresenter) MvpManager.findPresenter(InfoActivity.this);
                if (presenter != null) {
                    if (selectedPhotos == null || selectedPhotos.isEmpty()) {
                        presenter.submit_noPhoto(num.getText().toString(), mail.getText().toString(), name.getText().toString());
                    } else {
                        presenter.submit(num.getText().toString(), mail.getText().toString(), selectedPhotos, name.getText().toString());
                    }
                }
            }
        });
    }

    @Override
    protected void initView() {
        if (BoxingMediaLoader.getInstance().getLoader() == null) {
            IBoxingMediaLoader loader = new BoxingGlideLoader();
            BoxingMediaLoader.getInstance().init(loader);
        }
        photoUrl = getIntent().getStringExtra("photo");
        image = (ImageView) findViewById(R.id.tv_image);
        name = (TextView) findViewById(R.id.tv_name);
        grid = (TextView) findViewById(R.id.tv_grid);
        num = (TextView) findViewById(R.id.tv_telephone);
        mail = (TextView) findViewById(R.id.tv_mail);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        getData();
        image.setOnClickListener(this);
        num.setOnClickListener(this);
        mail.setOnClickListener(this);
    }

    public void getData() {
        Glide.with(context)
                .load("HttpConfig.fileServer + this.photoUrl")
                .placeholder(R.drawable.placeholder)
                .thumbnail(0.2f)
                .centerCrop()
                .transform(new GlideTransform.GlideCircleTransform(context))
                .error(R.mipmap.my_photo)
                .into(image);
        name.setText(UserInfoProvide.getUserInfo().getUserName());
        grid.setText(UserInfoProvide.getUserInfo().getDisplayDeptName());
        num.setText(StringUtil.notNull(UserInfoProvide.getUserInfo().getPhone()));
        mail.setText(StringUtil.notNull(UserInfoProvide.getUserInfo().getEmail()));
        if (UserInfoProvide.getUserInfo().getAccountUsers() != null
                && UserInfoProvide.getUserInfo().getAccountUsers().size() > 1) {
            iv_right.setVisibility(View.VISIBLE);
            findViewById(R.id.layout_grid).setOnClickListener(this);
        } else {
            iv_right.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_image:
                showHeaderImage();
                break;
            case R.id.tv_telephone:
                Intent intent = new Intent(context, TelephoneMailActivity.class);
                intent.putExtra("flag", "num");
                intent.putExtra("num", num.getText());
                startActivityForResult(intent, 111);
                break;
            case R.id.tv_mail:
                Intent mail = new Intent(context, TelephoneMailActivity.class);
                mail.putExtra("flag", "mail");
                mail.putExtra("num", this.mail.getText());
                startActivityForResult(mail, 222);
                break;
            case R.id.layout_grid:
                break;
            default:
        }
    }

    // 显示头像
    private void showHeaderImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) { // 添加照片
                    BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.MULTI_IMG);
                    config.needGif().needCamera(R.mipmap.picker_ic_camera)
                            .withMediaPlaceHolderRes(R.drawable.placeholder) // 设置默认图片占位图，默认无
                            .withAlbumPlaceHolderRes(R.mipmap.error).withMaxCount(1);
                    Boxing.of(config)
                            .withIntent(context, BoxingNewActivity.class, (ArrayList<? extends BaseMedia>) selectedMedias)
                            .start(InfoActivity.this, REQUEST_CODE_CHOOSE);
                } else if (which == 1) {
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK) {
            num.setText(data.getStringExtra("num"));
            if (btn.getVisibility() == View.GONE) {
                btn.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == 222 && resultCode == RESULT_OK) {
            mail.setText(data.getStringExtra("mail"));
            if (btn.getVisibility() == View.GONE) {
                btn.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == 333 && resultCode == RESULT_OK) {
            name.setText(data.getStringExtra("name"));
            if (btn.getVisibility() == View.GONE) {
                btn.setVisibility(View.VISIBLE);
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE) {
            if (btn.getVisibility() == View.GONE) {
                btn.setVisibility(View.VISIBLE);
            }
            List<String> photos = new ArrayList<>();
            if (data != null) {
                ArrayList<BaseMedia> medias = Boxing.getResult(data);
                selectedMedias.clear();
                if (medias != null && !medias.isEmpty()) {
                    for (BaseMedia media : medias) {
                        photos.add(media.getPath());
                    }
                    selectedMedias.addAll(medias);
                }
            }
            selectedPhotos.clear();
            selectedPhotos.addAll(photos);
            Uri uri = Uri.fromFile(new File(selectedPhotos.get(0)));
            Glide.with(context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder)
                    .thumbnail(0.2f)
                    .centerCrop()
                    .transform(new GlideTransform.GlideCircleTransform(context))
                    .error(R.mipmap.error)
                    .into(image);
        }
    }

    public void close() {
        Intent intent = new Intent(context, MainActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MvpManager.bindPresenter(this,
                "com.bysj.znzapp.presenter.activity.InfoActivityPresenter");
    }
}
