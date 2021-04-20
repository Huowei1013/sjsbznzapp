package com.thirdsdks.pickphoto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.beagle.component.activity.BaseCompatFragment;

import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.loader.IBoxingMediaLoader;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.model.entity.impl.ImageMedia;

import com.thirdsdks.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by zhaotao on 2021/01/04 20:52.
 */

public class PickPhotoFragment extends BaseCompatFragment {
    private Context context;
    private final static int REQUEST_CODE_CHOOSE = 233;
    private PhotoAdapter photoAdapter;
    private RecyclerView recyclerView;
    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private int spanCount = -1;
    private int selectMax = -1;
    private List<BaseMedia> selectedMedias = new ArrayList<>();

    private PickEnd pickEnd;

    private boolean isSingleHideAdd = false;//设置是否一张图片时，当选择过后隐藏+号图片
    private boolean needFliterTime = false;

    public interface PickEnd {
        void end();
    }

    public static PickPhotoFragment newFragment(int spanCount, int selectMax) {
        if (BoxingMediaLoader.getInstance().getLoader() == null) {
            IBoxingMediaLoader loader = new BoxingGlideLoader();
            BoxingMediaLoader.getInstance().init(loader);
        }

        PickPhotoFragment pickPhotoFragment = new PickPhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("spanCount", spanCount);
        bundle.putInt("selectMax", selectMax);
        pickPhotoFragment.setArguments(bundle);
        return pickPhotoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pick_photo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(getContext(), selectedPhotos, selectedMedias);
        photoAdapter.setSingleHideAdd(isSingleHideAdd);
        Bundle bundle = getArguments();
        spanCount = bundle.getInt("spanCount", -1);
        selectMax = bundle.getInt("selectMax", -1);
        if (spanCount != -1) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(spanCount, OrientationHelper.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        }
        recyclerView.setAdapter(photoAdapter);
        photoAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int max = (selectMax == -1) ? PhotoAdapter.MAX : selectMax;
                BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.MULTI_IMG);
                config.needGif().needCamera(R.mipmap.picker_ic_camera)
                        .withMediaPlaceHolderRes(R.drawable.picker_placeholder) // 设置默认图片占位图，默认无
                        .withAlbumPlaceHolderRes(R.mipmap.error).withMaxCount(max);
                Boxing boxing = Boxing.of(config)
                        .withIntent(context, BoxingNewActivity.class, (ArrayList<? extends BaseMedia>) selectedMedias);
                boxing.getIntent().putExtra("needFliterTime", needFliterTime);
                boxing.start(PickPhotoFragment.this, REQUEST_CODE_CHOOSE);

            }
        });
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
                        Log.e("zhaotao", media.getPath() + "------");
                    }
                    selectedMedias.addAll(medias);
                }
            }
            selectedPhotos.clear();
            selectedPhotos.addAll(photos);
            photoAdapter.notifyDataSetChanged();
            if (pickEnd != null) {
                pickEnd.end();
            }
        }
    }

    public void setImage(List<String> path) {
        selectedMedias.clear();
        selectedPhotos.clear();
        selectedPhotos.addAll(path);
        if (path != null && !path.isEmpty()) {
            for (String s : path) {
                ImageMedia media = new ImageMedia("", s);
                media.setPath(s);
                selectedMedias.add(media);
            }
        }
    }

    public void setSinglePicHideAdd(boolean singlePicHideAdd) {
        this.isSingleHideAdd = singlePicHideAdd;
        if (photoAdapter != null) {
            photoAdapter.setSingleHideAdd(true);
        }
    }

    public void refresh() {
        if (photoAdapter != null) {
            photoAdapter.notifyDataSetChanged();
        }
    }

    public void setNeedFliterTime(boolean needFliterTime) {
        this.needFliterTime = needFliterTime;
    }

    public void setPickEnd(PickEnd pickEnd) {
        this.pickEnd = pickEnd;
    }

    public ArrayList<String> getSelectedPhotos() {
        return selectedPhotos;
    }

    public void cleanAll() {
        selectedMedias.clear();
        selectedPhotos.clear();
        photoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
