package com.sensetime.cameralibrary;

import android.graphics.ImageFormat;

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

    private int previwFormat = ImageFormat.NV21;
    private int orientation = 90;

    public CameraConfig(int cameraType,int previwFormat,int orientation) {
        this.cameraType = cameraType;
        this.cameraType = previwFormat;
        this.orientation = orientation;
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

    public static class Builder {

        private int cameraType;
        private int previwFormat;
        private int orientation;

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

        public CameraConfig builer(){
            return new CameraConfig(cameraType,previwFormat,orientation);
        }
    }
}
