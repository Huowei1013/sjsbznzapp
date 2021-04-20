package com.thirdsdks.filedeal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaotao on 2020/12/30 19:04.
 */

public class FileCategoryBean implements Serializable {

    List<FilesBean> photos;
    List<FilesBean> videos;
    List<FilesBean> voices;

    public FileCategoryBean() {
        photos = new ArrayList<>();
        videos = new ArrayList<>();
        voices = new ArrayList<>();
    }

    public List<FilesBean> getPhotos() {
        return photos;
    }

    public void setPhotos(List<FilesBean> photos) {
        this.photos = photos;
    }

    public List<FilesBean> getVideos() {
        return videos;
    }

    public void setVideos(List<FilesBean> videos) {
        this.videos = videos;
    }

    public List<FilesBean> getVoices() {
        return voices;
    }

    public void setVoices(List<FilesBean> voices) {
        this.voices = voices;
    }

    public List<String> getPhotosPath() {
        List<String> result = new ArrayList<>();
        if (photos != null && photos.size() > 0) {
            for (FilesBean filesBean : photos) {
                result.add(filesBean.getVisitPath());
            }
        }
        return result;
    }

    public List<String> getVoicePath() {
        List<String> result = new ArrayList<>();
        if (voices != null && voices.size() > 0) {
            for (FilesBean filesBean : voices) {
                result.add(filesBean.getVisitPath());
            }
        }
        return result;
    }

    public List<String> getVideoPath() {
        List<String> result = new ArrayList<>();
        if (videos != null && videos.size() > 0) {
            for (FilesBean filesBean : videos) {
                result.add(filesBean.getVisitPath());
            }
        }
        return result;
    }

    public List<String> getVideoCoverPath() {
        List<String> result = new ArrayList<>();
        if (videos != null && videos.size() > 0) {
            for (FilesBean filesBean : videos) {
                if (filesBean.getThumbnail() != null) {
                    result.add(filesBean.getThumbnail().getVisitPath());
                }
            }
        }
        return result;
    }
}
