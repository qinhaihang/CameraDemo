package com.sensetime.cameralibrary;

import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;

/**
 * @author qinhaihang_vendor
 * @version $Rev$
 * @time 2019/6/13 17:02
 * @des
 * @packgename com.sensetime.cameralibrary
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes
 */
public class CameraConfig {

    public static final int BACK_CAMERA = 0;
    public static final int FRONT_CAMERA = 1;

    private int cameraType;

    private int previwFormat;
    private int orientation = 90;

    private int previewWidth;
    private int previewHeight;

    private int pictureWidth;
    private int pictureHeight;

    private String focusMode;

    private SurfaceTexture surfaceTexture;
    private SurfaceHolder surfaceHolder;

    public CameraConfig(int cameraType, int previwFormat, int orientation, int previewWidth, int previewHeight,
                        int pictureWidth,int pictureHeight, SurfaceTexture surfaceTexture,SurfaceHolder surfaceHolder,
                        String focusMode) {
        this.cameraType = cameraType;
        this.previwFormat = previwFormat;
        this.orientation = orientation;
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
        this.surfaceTexture = surfaceTexture;
        this.surfaceHolder = surfaceHolder;
        this.pictureWidth = pictureWidth;
        this.pictureHeight = pictureHeight;
        this.focusMode = focusMode;
    }

    public int getCameraType() {
        return cameraType;
    }

    public int getPreviwFormat() {
        return previwFormat;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public int getPreviewHeight(){
        return previewHeight;
    }

    public int getPictureWidth() {
        return pictureWidth;
    }

    public int getPictureHeight() {
        return pictureHeight;
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }

    public String getFocusMode() {
        return focusMode;
    }

    public static class Builder {

        private int cameraType;
        private int previwFormat;
        private int orientation;

        private int previewWidth;
        private int previewHeight;

        private int pictureWidth;
        private int pictureHeight;

        private SurfaceTexture surfaceTexture;
        private SurfaceHolder surfaceHolder;

        private String focusMode;

        public Builder setCameraType(int cameraType) {
            this.cameraType = cameraType;
            return this;
        }

        public Builder setPreviwFormat(int previwFormat) {
            this.previwFormat = previwFormat;
            return this;
        }

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setPreviewWidth(int previewWidth) {
            this.previewWidth = previewWidth;
            return this;
        }

        public Builder setPreviewHeight(int previewHeight) {
            this.previewHeight = previewHeight;
            return this;
        }

        public Builder setPictureSize(int pictureWidth,int pictureHeight) {
            this.pictureWidth = pictureWidth;
            this.pictureHeight = pictureHeight;
            return this;
        }

        public Builder setSurfaceTexture(SurfaceTexture surfaceTexture) {
            this.surfaceTexture = surfaceTexture;
            return this;
        }

        public Builder setSurfaceHolder(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
            return this;
        }

        public Builder setFocusMode(String focusMode) {
            this.focusMode = focusMode;
            return this;
        }

        public CameraConfig builer(){
            return new CameraConfig(cameraType, previwFormat, orientation, previewWidth, previewHeight,
                    pictureWidth, pictureHeight, surfaceTexture, surfaceHolder,focusMode);
        }
    }
}
