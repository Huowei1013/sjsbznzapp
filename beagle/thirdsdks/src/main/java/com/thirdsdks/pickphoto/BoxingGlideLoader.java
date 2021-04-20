package com.thirdsdks.pickphoto;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bilibili.boxing.loader.IBoxingCallback;
import com.bilibili.boxing.loader.IBoxingMediaLoader;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.thirdsdks.R;

/**
 * Created by zhaotao on 2021/01/04 20:52.
 */

public class BoxingGlideLoader implements IBoxingMediaLoader {
    @Override
    public void displayThumbnail(@NonNull ImageView img, @NonNull String absPath, int width, int height) {
         String path;
        if (!TextUtils.isEmpty(absPath) && (absPath.startsWith("http") || absPath.startsWith("https"))) {
            path = absPath;
        } else {
            path = "file://" + absPath;
        }
        try {
            Glide.with(img.getContext())
                    .load(path)
                    .placeholder(R.drawable.picker_placeholder)
                    .crossFade()
                    .centerCrop()
                    .override(width, height)
                    .into(img);
        } catch (IllegalArgumentException ignore) {
        }

    }

    @Override
    public void displayRaw(@NonNull final ImageView img, @NonNull String absPath, int width, int height, final IBoxingCallback callback) {
        String path;
        if (!TextUtils.isEmpty(absPath) && (absPath.startsWith("http") || absPath.startsWith("https"))) {
            path = absPath;
        } else {
            path = "file://" + absPath;
        }
        BitmapTypeRequest<String> request = Glide.with(img.getContext())
                .load(path)
                .asBitmap();
        if (width > 0 && height > 0) {
            request.override(width, height);
        }
        request.listener(new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                if (callback != null) {
                    callback.onFail(e);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (resource != null && callback != null) {
                    img.setImageBitmap(resource);
                    callback.onSuccess();
                    return true;
                }
                return false;
            }
        }).into(img);
    }
}
