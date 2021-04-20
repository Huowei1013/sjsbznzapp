package com.thirdsdks.filedeal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaotao on 2020/12/30 19:04.
 */

public class FilesBean implements Serializable {

    private static final long serialVersionUID = 4318794529340253507L;
    /**
     * id : 3ebdb797-ab49-42c1-a15e-5d1bc598ec2e
     * fileName : 23.png
     * category : 图片
     * visitPath : /zhzlBackend/common/getImages?id=3ebdb797-ab49-42c1-a15e-5d1bc598ec2e
     * thumbnail : {"id":"e1b2822c-00ec-43d2-89b3-2d4b144f6441","fileName":"23.png","category":"图片缩略图","visitPath":"/zhzlBackend/common/getImages?id=e1b2822c-00ec-43d2-89b3-2d4b144f6441"}
     */

    private String id;
    private String fileName;
    private String category;
    private String fileType;
    private String visitPath;
    private String code;
    private ThumbnailBean thumbnail;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getVisitPath() {
        return visitPath;
    }

    public void setVisitPath(String visitPath) {
        this.visitPath = visitPath;
    }

    public ThumbnailBean getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ThumbnailBean thumbnail) {
        this.thumbnail = thumbnail;
    }

    public static class ThumbnailBean implements Serializable {
        private static final long serialVersionUID = 8860032085391638626L;
        /**
         * id : e1b2822c-00ec-43d2-89b3-2d4b144f6441
         * fileName : 23.png
         * category : 图片缩略图
         * visitPath : /zhzlBackend/common/getImages?id=e1b2822c-00ec-43d2-89b3-2d4b144f6441
         */

        private String id;
        private String fileName;
        private String category;
        private String visitPath;
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getVisitPath() {
            return visitPath;
        }

        public void setVisitPath(String visitPath) {
            this.visitPath = visitPath;
        }
    }

    public static FilesBean getSinglePhotoFilesBean(List<String> paths, String code) {
        if (paths == null || paths.size() == 0) {
            return null;
        }
        FilesBean filesBean = new FilesBean();
        filesBean.setCategory("图片");
        filesBean.setVisitPath(paths.get(0));
        filesBean.setCode(code);
        return filesBean;
    }


    public static List<FilesBean> getPhotoFilesBean(List<String> paths, String code) {
        List<FilesBean> filesBeans = new ArrayList<>();
        if (paths == null || paths.size() == 0) {
            return filesBeans;
        }
        for (int i = 0; i < paths.size(); i++) {
            FilesBean filesBean = new FilesBean();
            filesBean.setCategory("图片");
            filesBean.setVisitPath(paths.get(i));
            filesBean.setCode(code);
            filesBeans.add(filesBean);
        }
        return filesBeans;
    }

    public static List<FilesBean> getVoiceFilesBean(List<String> paths, String code) {
        List<FilesBean> filesBeans = new ArrayList<>();
        if (paths == null || paths.size() == 0) {
            return filesBeans;
        }
        for (int i = 0; i < paths.size(); i++) {
            FilesBean filesBean = new FilesBean();
            filesBean.setCategory("语音");
            filesBean.setVisitPath(paths.get(i));
            filesBean.setCode(code);
            filesBeans.add(filesBean);
        }
        return filesBeans;
    }

    public static List<FilesBean> getVideoFilesBean(List<String> videoPath, List<String> videoCoverPath, String code) {
        List<FilesBean> filesBeans = new ArrayList<>();
        if (videoPath == null || videoPath.size() == 0) {
            return filesBeans;
        }
        for (int i = 0; i < videoPath.size(); i++) {
            FilesBean filesBean = new FilesBean();
            filesBean.setCategory("视频");
            filesBean.setVisitPath(videoPath.get(i));
            filesBean.setCode(code);
            ThumbnailBean thumbnailBean = new ThumbnailBean();
            thumbnailBean.setCategory("视频缩略图");
            thumbnailBean.setVisitPath(videoCoverPath.get(i));
            thumbnailBean.setCode(code);
            filesBean.setThumbnail(thumbnailBean);
            filesBeans.add(filesBean);
        }
        return filesBeans;
    }

    @Override
    public String toString() {
        return "FilesBean{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", category='" + category + '\'' +
                ", fileType='" + fileType + '\'' +
                ", visitPath='" + visitPath + '\'' +
                ", code='" + code + '\'' +
                ", thumbnail=" + thumbnail +
                '}';
    }
}
