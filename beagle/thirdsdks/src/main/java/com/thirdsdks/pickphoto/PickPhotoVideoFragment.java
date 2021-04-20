package com.thirdsdks.pickphoto;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.beagle.component.activity.BaseCompatFragment;
import com.beagle.component.logger.LogCat;
import com.beagle.component.utils.ScreenUtil;

import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.loader.IBoxingMediaLoader;
import com.bilibili.boxing.model.BoxingBuilderConfig;
import com.bilibili.boxing.model.BoxingManager;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.ImageMedia;
import com.bilibili.boxing.utils.BoxingFileHelper;
import com.bilibili.boxing.utils.CameraPickerHelper;

import com.thirdsdks.filedeal.FileDownUtil;
import com.thirdsdks.filedeal.ToastUtil;
import com.thirdsdks.R;
import com.thirdsdks.rtmp.TCConstants;
import com.thirdsdks.rtmp.TCVideoPreviewActivity;
import com.thirdsdks.rtmp.TCVideoRecordActivity;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by zhaotao on 2021/01/04 20:52.
 */

public class PickPhotoVideoFragment extends BaseCompatFragment {

    private Context context;
    private final static int REQUEST_CODE_CHOOSE = 233;
    private final static int REQUEST_VIDEO_CHOOSE = 333;
    private final static int REQUEST_VIDEO_PREW = 433;
    private PhotoAdapter photoAdapter;
    private RecyclerView recyclerView;
    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private int spanCount = -1;
    private int selectMax = -1;
    private List<BaseMedia> selectedMedias = new ArrayList<>();
    private final String[] items = {"添加照片", "添加视频"};
    private AlertDialog alertDialog;
    //------------------------------视频属性-------------------------------
    private int widthBitmap;
    private ImageView iv_play_video;
    private ImageView iv_add_video;
    private ImageView select;
    private String videPath;
    private String coverPath;
    private int retCode;
    private String descMsg;
    private int typePublish;
    private boolean isDelVideo;
    private ArrayList<String> videos = new ArrayList<>();
    private ArrayList<String> videoCovers = new ArrayList<>();
    private boolean s = false;
    private boolean flag = false;
    private boolean isVideo = false;
    private boolean onlyPicturesFromCamera = false;
    private CameraPickerHelper cameraPickerHelper = null;
    public static final String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String[] CAMERA_PERMISSIONS = {Manifest.permission.CAMERA};
    private static final int REQUEST_CODE_PERMISSION = 2330;
    private Map<String, String> localNetPathMap = new HashMap<>();
    private boolean needFliterTime = false;

    public static PickPhotoVideoFragment newFragment(int spanCount, int selectMax) {
        if (BoxingMediaLoader.getInstance().getLoader() == null) {
            IBoxingMediaLoader loader = new BoxingGlideLoader();
            BoxingMediaLoader.getInstance().init(loader);
        }



        PickPhotoVideoFragment pickPhotoFragment = new PickPhotoVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("spanCount", spanCount);
        bundle.putInt("selectMax", selectMax);
        pickPhotoFragment.setArguments(bundle);
        return pickPhotoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (onlyPicturesFromCamera) {
            checkPermissionAndLoad();
            cameraPickerHelper = new CameraPickerHelper(savedInstanceState);
        }
        return inflater.inflate(R.layout.fragment_pick_photo_video, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iv_play_video = (ImageView) view.findViewById(R.id.iv_play_video);
        iv_add_video = (ImageView) view.findViewById(R.id.iv_add_video);
        select = (ImageView) view.findViewById(R.id.v_selected);

        iv_play_video.setVisibility(View.GONE);
        iv_add_video.setVisibility(View.GONE);
        select.setVisibility(View.GONE);

        widthBitmap = (int) (ScreenUtil.getScreenWidth(context) / 4.3);
        iv_add_video.setLayoutParams(new RelativeLayout.LayoutParams(widthBitmap, widthBitmap));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(widthBitmap / 2, widthBitmap / 2);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        iv_play_video.setLayoutParams(params);
        iv_add_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(videPath)) {
                    Intent intent = new Intent(context, TCVideoPreviewActivity.class);
                    intent.putExtra(TCConstants.VIDEO_RECORD_TYPE, typePublish);
                    intent.putExtra(TCConstants.VIDEO_RECORD_RESULT, retCode);
                    intent.putExtra(TCConstants.VIDEO_RECORD_DESCMSG, descMsg);
                    intent.putExtra(TCConstants.VIDEO_RECORD_VIDEPATH, videPath);
                    intent.putExtra(TCConstants.VIDEO_RECORD_COVERPATH, coverPath);
                    intent.putExtra("isDelVideo", isDelVideo); //控制是否删除拍摄的视频
                    startActivityForResult(intent, REQUEST_VIDEO_PREW);//预览视频
                }
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_add_video.setVisibility(View.GONE);
                iv_play_video.setVisibility(View.GONE);
                select.setVisibility(View.GONE);
                videPath = "";
                coverPath = "";
                videos.clear();
                videoCovers.clear();
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(getContext(), selectedPhotos, selectedMedias);
        final Bundle bundle = getArguments();
        spanCount = bundle.getInt("spanCount", -1);
        selectMax = bundle.getInt("selectMax", -1);
        if (spanCount != -1) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(spanCount, OrientationHelper.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        }
        photoAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!s) {
                    if (isVideo) {
                        Intent intent = new Intent(context, TCVideoRecordActivity.class);
                        startActivityForResult(intent, REQUEST_VIDEO_CHOOSE);//拍摄视频
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {// 添加照片
                                    int max = (selectMax == -1) ? PhotoAdapter.MAX : selectMax;
                                    updateData();
                                    if (!onlyPicturesFromCamera) {
                                        BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.MULTI_IMG);
                                        config.needGif().needCamera(R.mipmap.picker_ic_camera)
                                                .withMediaPlaceHolderRes(R.drawable.picker_placeholder) // 设置默认图片占位图，默认无
                                                .withAlbumPlaceHolderRes(R.mipmap.error).withMaxCount(max);
                                        Boxing boxing = Boxing.of(config).withIntent(context, BoxingNewActivity.class, (ArrayList<? extends BaseMedia>) selectedMedias);
                                        boxing.getIntent().putExtra("needFliterTime", needFliterTime);
                                        boxing.start(com.thirdsdks.pickphoto.PickPhotoVideoFragment.this, REQUEST_CODE_CHOOSE);
                                    } else {
                                        BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.MULTI_IMG);
                                        config.needGif().needCamera(R.mipmap.picker_ic_camera)
                                                .withMediaPlaceHolderRes(R.drawable.picker_placeholder) // 设置默认图片占位图，默认无
                                                .withAlbumPlaceHolderRes(R.mipmap.error).withMaxCount(max);
                                        Boxing.of(config);
                                        startCamera(getActivity(), com.thirdsdks.pickphoto.PickPhotoVideoFragment.this, BoxingFileHelper.DEFAULT_SUB_DIR);
                                    }
                                } else if (which == 1) {// 添加视频
                                    startActivityForResult(new Intent(context, TCVideoRecordActivity.class), REQUEST_VIDEO_CHOOSE);// 拍摄视频
                                }
                            }
                        });
                        alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }
        });
        recyclerView.setAdapter(photoAdapter);
        if (flag) {
            iv_add_video.setVisibility(View.VISIBLE);
            iv_play_video.setVisibility(View.VISIBLE);
            select.setVisibility(View.VISIBLE);
            Glide.with(context).load(coverPath)
                    .placeholder(R.drawable.picker_placeholder)
                    .error(R.mipmap.error)
                    .centerCrop()
                    .into(iv_add_video);
            iv_add_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogCat.e("----------------------->log");
                    Intent intent = new Intent(context, TCVideoPreviewActivity.class);
                    intent.putExtra(TCConstants.VIDEO_RECORD_COVERPATH, coverPath);
                    intent.putExtra(TCConstants.VIDEO_RECORD_VIDEPATH, videPath);
                    intent.putExtra("isDelVideo", isDelVideo);
                    startActivity(intent);//预览视频
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && requestCode == REQUEST_CODE_CHOOSE) {
            List<String> photos = new ArrayList<>();
            if (data != null) {
                ArrayList<BaseMedia> medias = Boxing.getResult(data);
                selectedMedias.clear();
                if (medias != null && !medias.isEmpty()) {
                    for (BaseMedia media : medias) {
                        photos.add(media.getPath());
                        Log.e("zhoujun", media.getPath() + "------");
                    }
                    selectedMedias.addAll(medias);
                }
            }
            selectedPhotos.clear();
            selectedPhotos.addAll(photos);
            photoAdapter.notifyDataSetChanged();
        } else if (resultCode == RESULT_OK
                && requestCode == REQUEST_VIDEO_CHOOSE) {
            videPath = data.getStringExtra(TCConstants.VIDEO_RECORD_VIDEPATH);
            coverPath = data.getStringExtra(TCConstants.VIDEO_RECORD_COVERPATH);
            descMsg = data.getStringExtra(TCConstants.VIDEO_RECORD_DESCMSG);
            retCode = data.getIntExtra(TCConstants.VIDEO_RECORD_RESULT, -1);
            typePublish = data.getIntExtra(TCConstants.VIDEO_RECORD_TYPE, -1);
            iv_add_video.setVisibility(View.VISIBLE);
            Glide.with(context).load(coverPath)
                    .placeholder(R.drawable.picker_placeholder)
                    .error(R.mipmap.error)
                    .centerCrop()
                    .into(iv_add_video);
            select.setVisibility(View.VISIBLE);
            iv_play_video.setVisibility(View.VISIBLE);
            videos.clear();
            videoCovers.clear();
            videos.add(videPath);
            videoCovers.add(coverPath);
        } else if (resultCode == RESULT_OK &&
                requestCode == REQUEST_VIDEO_PREW) {
            videPath = data.getStringExtra(TCConstants.VIDEO_RECORD_VIDEPATH);
            coverPath = data.getStringExtra(TCConstants.VIDEO_RECORD_COVERPATH);
            if (TextUtils.isEmpty(videPath)) {
                videos.clear();
                videoCovers.clear();
                iv_add_video.setVisibility(View.GONE);
                iv_play_video.setVisibility(View.GONE);
                select.setVisibility(View.GONE);
            }
        } else if (cameraPickerHelper != null && requestCode == CameraPickerHelper.REQ_CODE_CAMERA) {
            File file = new File(cameraPickerHelper.getSourceFilePath());
            if (!file.exists()) {
                Toast.makeText(getContext(), "照片获取失败", Toast.LENGTH_SHORT).show();
                return;
            }
            ImageMedia cameraMedia = new ImageMedia(file);
            cameraMedia.saveMediaStore(com.thirdsdks.pickphoto.PickPhotoVideoFragment.this.getContext().getContentResolver());
            if (selectedMedias != null) {
                selectedMedias.add(cameraMedia);
            }
            if (selectedPhotos != null) {
                selectedPhotos.add(cameraMedia.getPath());
            }
            photoAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<String> getSelectedPhotos() {
        return selectedPhotos;
    }

    public ArrayList<String> getVideos() {
        return videos;
    }

    public ArrayList<String> getVideoCovers() {
        return videoCovers;
    }

    public void cleanAll() {
        selectedMedias.clear();
        selectedPhotos.clear();
        videoCovers.clear();
        videos.clear();
        iv_add_video.setVisibility(View.GONE);
        iv_play_video.setVisibility(View.GONE);
        select.setVisibility(View.GONE);
        photoAdapter.notifyDataSetChanged();
    }

    public void setImage(List<String> path) {
        selectedMedias.clear();
        selectedPhotos.clear();
        selectedPhotos.addAll(path);
        if (path != null && !path.isEmpty()) {
            for (String s : path) {
                ImageMedia media = new ImageMedia(System.currentTimeMillis() + "", s);
                media.setPath(s);
                selectedMedias.add(media);
            }
        }
        if (photoAdapter != null) {
            photoAdapter.notifyDataSetChanged();
        }
    }

    public void addImage(String path) {
        ImageMedia media = new ImageMedia(System.currentTimeMillis() + "", path);
        media.setPath(path);
        selectedMedias.add(media);
        selectedPhotos.add(path);
        photoAdapter.notifyDataSetChanged();
    }

    public void setVideo(final String videoC, final String video, boolean tag) {
        videoCovers.clear();
        videos.clear();
        if (!TextUtils.isEmpty(video)) {
            coverPath = videoC;
            videPath = video;
            if (tag) {
                iv_add_video.setVisibility(View.VISIBLE);
                iv_play_video.setVisibility(View.VISIBLE);
                select.setVisibility(View.VISIBLE);
                Glide.with(context).load(coverPath)
                        .placeholder(R.drawable.picker_placeholder)
                        .error(R.mipmap.error)
                        .centerCrop()
                        .into(iv_add_video);
                // 网络图片处理 start
                if (videPath.startsWith("http") || videPath.startsWith("https")) {
                    downloadVideo(videPath);
                }
                // 网络图片处理 end
                iv_add_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogCat.e("----------------------->log");
                        // 网络图片处理 start
                        if (videPath.startsWith("http") || videPath.startsWith("https")) {
                            if (TextUtils.isEmpty(localNetPathMap.get(video))) {
                                ToastUtil.showToast(context, "请等待视频下载");
                            } else {
                                Intent intent = new Intent(context, TCVideoPreviewActivity.class);
                                intent.putExtra(TCConstants.VIDEO_RECORD_COVERPATH, coverPath);
                                intent.putExtra(TCConstants.VIDEO_RECORD_VIDEPATH, localNetPathMap.get(video));
                                startActivity(intent);//预览视频
                            }
                            // 网络图片处理 end
                        } else {
                            Intent intent = new Intent(context, TCVideoPreviewActivity.class);
                            intent.putExtra(TCConstants.VIDEO_RECORD_COVERPATH, coverPath);
                            intent.putExtra(TCConstants.VIDEO_RECORD_VIDEPATH, videPath);
                            startActivity(intent);//预览视频
                        }
                    }
                });
            } else {
                flag = true;
            }
        }
        videoCovers.add(coverPath);
        videos.add(videPath);
        if (photoAdapter != null) {
            photoAdapter.notifyDataSetChanged();
        }
    }

    public void downloadVideo(final String url) {
        FileDownUtil fileDownUtil = new FileDownUtil(context);
        fileDownUtil.url(url)
                .fileName(System.currentTimeMillis() + ".mp4")
                .download(new FileDownUtil.SimpleResponse() {
                    @Override
                    public void onResponse(File resource) {
                        localNetPathMap.put(url, resource.getPath());
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (alertDialog != null)
            alertDialog.dismiss();
    }

    public void setDelVideo(boolean delVideo) {
        isDelVideo = delVideo;
    }

    public void setEnable(boolean flag) {
        s = flag;
        if (flag) {
            iv_play_video.setEnabled(true);
            iv_play_video.setClickable(true);
            iv_add_video.setEnabled(true);
            iv_add_video.setClickable(true);
        } else {
            iv_play_video.setEnabled(false);
            iv_play_video.setClickable(false);
            iv_add_video.setEnabled(false);
            iv_add_video.setClickable(false);
        }
    }

    public void setVideoFlag() {
        isVideo = true;
    }

    public boolean isOnlyPicturesFromCamera() {
        return onlyPicturesFromCamera;
    }

    public void setOnlyPicturesFromCamera(boolean onlyPicturesFromCamera) {
        this.onlyPicturesFromCamera = onlyPicturesFromCamera;
    }

    private void updateData() {
        List<BaseMedia> medias = new ArrayList<>();
        if (photoAdapter != null) {
            selectedPhotos = photoAdapter.getPhotoes();
            selectedMedias = photoAdapter.getSelectedMedias();
        }
    }

    public void notifyDataSetChanged() {
        if (photoAdapter != null) {
            photoAdapter.notifyDataSetChanged();
        }
    }

    public final void startCamera(Activity activity, Fragment fragment, String subFolderPath) {
        try {
            if (!BoxingBuilderConfig.TESTING && ContextCompat.checkSelfPermission(getActivity(), CAMERA_PERMISSIONS[0]) != PermissionChecker.PERMISSION_GRANTED) {
                requestPermissions(CAMERA_PERMISSIONS, REQUEST_CODE_PERMISSION);
            } else {
                if (!BoxingManager.getInstance().getBoxingConfig().isVideoMode()) {
                    cameraPickerHelper.startCamera(activity, fragment, subFolderPath);
                }
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            onRequestPermissionError(CAMERA_PERMISSIONS, e);
        }
    }

    public boolean isNeedFliterTime() {
        return needFliterTime;
    }

    public void setNeedFliterTime(boolean needFliterTime) {
        this.needFliterTime = needFliterTime;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (cameraPickerHelper != null) {
            cameraPickerHelper.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraPickerHelper != null) {
            cameraPickerHelper.release();
        }
    }

    public void onRequestPermissionError(String[] permissions, Exception e) {
        if (permissions.length > 0) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getContext(), R.string.boxing_storage_permission_deny, Toast.LENGTH_SHORT).show();
            } else if (permissions[0].equals(Manifest.permission.CAMERA)) {
                Toast.makeText(getContext(), R.string.boxing_camera_permission_deny, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onRequestPermissionSuc(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions[0].equals(STORAGE_PERMISSIONS[0])) {
        } else if (permissions[0].equals(CAMERA_PERMISSIONS[0])) {
            startCamera(getActivity(), com.thirdsdks.pickphoto.PickPhotoVideoFragment.this, null);
        }
    }

    private void checkPermissionAndLoad() {
        try {
            if (!BoxingBuilderConfig.TESTING && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && ContextCompat.checkSelfPermission(getActivity(), STORAGE_PERMISSIONS[0]) != PermissionChecker.PERMISSION_GRANTED) {
                requestPermissions(STORAGE_PERMISSIONS, REQUEST_CODE_PERMISSION);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            onRequestPermissionError(STORAGE_PERMISSIONS, e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE_PERMISSION == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onRequestPermissionSuc(requestCode, permissions, grantResults);
            } else {
                onRequestPermissionError(permissions, new SecurityException("request " + permissions[0] + " error."));
            }
        }
    }
}
