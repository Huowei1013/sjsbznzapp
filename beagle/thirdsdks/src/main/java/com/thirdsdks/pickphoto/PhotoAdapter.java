package com.thirdsdks.pickphoto;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.model.entity.BaseMedia;

import com.bumptech.glide.Glide;
import com.thirdsdks.R;

/**
 * Created by zhaotao on 2021/01/04 20:52.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private ArrayList<String> photoPaths = new ArrayList<String>();
    private LayoutInflater inflater;
    private Context mContext;
    public final static int TYPE_ADD = 1;
    final static int TYPE_PHOTO = 2;
    public final static int MAX = 9;
    private View.OnClickListener mClickListener;
    private List<BaseMedia> selectedMedias = new ArrayList<>();
    private boolean isSingleHideAdd = false;// 设置是否一张图片时，当选择过后隐藏+号图片

    public PhotoAdapter(Context mContext, ArrayList<String> photoPaths, List<BaseMedia> medias) {
        this.photoPaths = photoPaths;
        this.mContext = mContext;
        this.selectedMedias = medias;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case TYPE_ADD:
                itemView = inflater.inflate(R.layout.picker_item_add, parent, false);
                break;
            case TYPE_PHOTO:
                itemView = inflater.inflate(R.layout.picker_item_photo, parent, false);
                break;
        }
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_PHOTO) {
            if (photoPaths.get(position).startsWith("https") || photoPaths.get(position).startsWith("http")) {
                Glide.with(mContext)
                        .load(photoPaths.get(position))
                        .centerCrop()
                        .thumbnail(0.1f)
                        .placeholder(R.drawable.picker_placeholder)
                        .error(R.mipmap.error)
                        .into(holder.ivPhoto);
            } else {
                Uri uri = Uri.fromFile(new File(photoPaths.get(position)));
                Glide.with(mContext)
                        .load(uri)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .placeholder(R.drawable.picker_placeholder)
                        .error(R.mipmap.error)
                        .into(holder.ivPhoto);
            }
            holder.vSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photoPaths.remove(position);
                    selectedMedias.remove(position);
                    notifyDataSetChanged();
                }
            });
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boxing.of()
                            .withIntent(mContext, PreViewActivity.class, (ArrayList<? extends BaseMedia>) selectedMedias, position)
                            .start((Activity) mContext);
                }
            });
        } else {
            holder.itemView.setOnClickListener(mClickListener);
        }
    }


    @Override
    public int getItemCount() {
        int count = photoPaths.size() + 1;
        if (count > MAX) {
            count = MAX;
        }
        // 设置是否最多只有一张图片时选择完毕后隐藏+号图片
        if (isSingleHideAdd) {
            if (photoPaths.size() == 1) {
                count = 1;
            }
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == photoPaths.size() && position != MAX) ? TYPE_ADD : TYPE_PHOTO;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivPhoto;
        private ImageView vSelected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            vSelected = (ImageView) itemView.findViewById(R.id.v_selected);
            if (vSelected != null) {
                vSelected.setImageResource(R.drawable.edit_clear_image);
                vSelected.setVisibility(View.VISIBLE);
            }
        }
    }

    public ArrayList<String> getPhotoes() {
        return photoPaths;
    }

    public List<BaseMedia> getSelectedMedias() {
        return selectedMedias;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mClickListener = onClickListener;
    }

    public boolean isSingleHideAdd() {
        return isSingleHideAdd;
    }

    public void setSingleHideAdd(boolean singleHideAdd) {
        isSingleHideAdd = singleHideAdd;
    }
}
