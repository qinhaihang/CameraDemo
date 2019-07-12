package com.qhh.camerademo.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.DisplayMetrics;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLFrameRenderer implements Renderer {

    private GLSurfaceView mTargetSurface;
    private GLProgram prog;
    private int mScreenWidth, mScreenHeight;
    private int mVideoWidth, mVideoHeight;
    private ByteBuffer mImgDatas;
    int imgwidth;
    int imgheight;
    Context context;



    public GLFrameRenderer(Context context, GLSurfaceView surface, DisplayMetrics dm) {
        this.context = context;
        mTargetSurface = surface;
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Utils.LOGD("GLFrameRenderer :: onSurfaceCreated");

        if (prog == null)
        {
            prog = new GLProgram(context);
        }
        if (!prog.isProgramBuilt()) {
            prog.buildProgram();
            Utils.LOGD("GLFrameRenderer :: buildProgram done");
        }
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Utils.LOGD("GLFrameRenderer :: onSurfaceChanged");
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {
            if (mImgDatas != null) {
                mImgDatas.position(0);
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                prog.buildTextures(mImgDatas, mVideoWidth, mVideoHeight);
                prog.drawFrame();
            }
        }
    }


    public void update(int w, int h) {

        imgwidth = w;
        imgheight = h;

        Utils.LOGD("INIT E");
        if (w > 0 && h > 0) {
            // 调整比例
            if (mScreenWidth > 0 && mScreenHeight > 0) {
                float f1 = 1f * mScreenHeight / mScreenWidth;
                float f2 = 1f * h / w;
                if (f1 == f2) {
                    prog.createBuffers(GLProgram.squareVertices);
                } else if (f1 < f2) {
                    float widScale = f1 / f2;
                    prog.createBuffers(new float[] { -widScale, -1.0f, widScale, -1.0f, -widScale, 1.0f, widScale,
                            1.0f, });
                } else {
                    float heightScale = f2 / f1;
                    prog.createBuffers(new float[] { -1.0f, -heightScale, 1.0f, -heightScale, -1.0f, heightScale, 1.0f,
                            heightScale, });
                }
            }
            // 初始化容器
            if (w != mVideoWidth && h != mVideoHeight) {
                this.mVideoWidth = w;
                this.mVideoHeight = h;
            }
        }
    }


    public void update(byte[] data) {
        synchronized (this) {
            mImgDatas = ByteBuffer.wrap(data);
        }
        mTargetSurface.requestRender();
    }


}
