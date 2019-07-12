package com.qhh.camerademo.opengl;

import android.content.Context;
import android.opengl.GLES30;

import com.qhh.camerademo.R;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES30.GL_PIXEL_UNPACK_BUFFER;


public class GLProgram {

    // program id
    private int mProgram;
    // texture id
    private int mTexture;
    // texture index in gles
    private int mIindex;
    private int mPositionHandle = -1;
    private int mCoordHandle = -1;
    private int mUVHandle = -1;
    private int mUVid = -1;
    // vertices buffer
    private ByteBuffer mVerticeBuffer;
    private ByteBuffer mCoordBuffer;
    // video width and height
    private int mVideoWidth = -1;
    private int mVideoHeight = -1;
    // flow control
    private boolean isProgBuilt = false;
    private int mPixelBuffer = 0;
    private Context context;
    static float[] squareVertices = { -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, };
    private static float[] coordVertices = { 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, };

    public GLProgram(Context context) {
        this.context = context;
        mTexture = GLES30.GL_TEXTURE0;
        mIindex = 0;
    }

    public boolean isProgramBuilt() {
        return isProgBuilt;
    }

    public void buildProgram() {
        if (mProgram <= 0) {

            String vertexShaderSource = TextResourceReader
                    .readTextFileFromResource(context, R.raw.vertex_shader);
            String fragmentShaderSource = TextResourceReader
                    .readTextFileFromResource(context,R.raw.fragment_shader);

            //生成着色器对象
            int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
            int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
            mProgram = ShaderHelper.linkProgram(vertexShader,fragmentShader);
        }



        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        Utils.LOGD("mPositionHandle = " + mPositionHandle);
        checkGlError("glGetAttribLocation vPosition");
        if (mPositionHandle == -1) {
            throw new RuntimeException("Could not get attribute location for vPosition");
        }
        mCoordHandle = GLES30.glGetAttribLocation(mProgram, "a_texCoord");
        Utils.LOGD("mCoordHandle = " + mCoordHandle);
        checkGlError("glGetAttribLocation a_texCoord");
        if (mCoordHandle == -1) {
            throw new RuntimeException("Could not get attribute location for a_texCoord");
        }

        mUVHandle = GLES30.glGetUniformLocation(mProgram, "tex_yuv");
        Utils.LOGD("_uhandle = " + mUVHandle);
        checkGlError("glGetUniformLocation tex_u");
        if (mUVHandle == -1) {
            throw new RuntimeException("Could not get uniform location for tex_uv");
        }

        isProgBuilt = true;
    }


    public void buildTextures(Buffer y, int width, int height) {
        boolean videoSizeChanged = (width != mVideoWidth || height != mVideoHeight);
        if (videoSizeChanged)
        {
            mVideoWidth = width;
            mVideoHeight = height;
            Utils.LOGD("buildTextures videoSizeChanged: w=" + mVideoWidth + " h=" + mVideoHeight);
        }

        y.position(0);
        // building texture for U data
        if (mUVid < 0 || videoSizeChanged)
        {
            if (mUVid >= 0) {
                Utils.LOGD("glDeleteTextures U");
                GLES30.glDeleteTextures(1, new int[] { mUVid }, 0);
                checkGlError("glDeleteTextures");
            }
            int[] textures = new int[1];
            GLES30.glGenTextures(1, textures, 0);
            checkGlError("glGenTextures");
            mUVid = textures[0];
            Utils.LOGD("glGenTextures U = " + mUVid);

            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mUVid);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, mVideoWidth / 2, mVideoHeight , 0,
                    GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
        }
        if(0 == mPixelBuffer)
        {
            int[] hPixelHandle = new int[1];
            GLES30.glGenBuffers(1, hPixelHandle, 0);
            mPixelBuffer = hPixelHandle[0];
            GLES30.glBindBuffer(GL_PIXEL_UNPACK_BUFFER, mPixelBuffer);
            GLES30.glBufferData(GL_PIXEL_UNPACK_BUFFER, mVideoWidth*mVideoHeight*2, null, GL_STATIC_DRAW);
        }
        GLES30.glBindBuffer(GL_PIXEL_UNPACK_BUFFER, mPixelBuffer);
        GLES30.glBufferSubData(GL_PIXEL_UNPACK_BUFFER,  0, mVideoWidth*mVideoHeight*2, y);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mUVid);
        GLES30.glTexSubImage2D(GLES30.GL_TEXTURE_2D, 0, 0, 0, mVideoWidth / 2,  mVideoHeight ,
                GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);



    }



    /**
     * render the frame
     * the YUV data will be converted to RGB by shader.
     */
    public void drawFrame() {
        GLES30.glUseProgram(mProgram);
        checkGlError("glUseProgram");
        GLES30.glVertexAttribPointer(mPositionHandle, 2, GLES30.GL_FLOAT, false, 8, mVerticeBuffer);
        checkGlError("glVertexAttribPointer mPositionHandle");
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glVertexAttribPointer(mCoordHandle, 2, GLES30.GL_FLOAT, false, 8, mCoordBuffer);
        checkGlError("glVertexAttribPointer maTextureHandle");
        GLES30.glEnableVertexAttribArray(mCoordHandle);

        GLES30.glActiveTexture(mTexture);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mUVid);
        GLES30.glUniform1i(mUVHandle, mIindex);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        GLES30.glFlush();

        GLES30.glDisableVertexAttribArray(mPositionHandle);
        GLES30.glDisableVertexAttribArray(mCoordHandle);
    }
    /**
     * these two buffers are used for holding vertices, screen vertices and texture vertices.
     */
    void createBuffers(float[] vert) {
        mVerticeBuffer = ByteBuffer.allocateDirect(vert.length * 4);
        mVerticeBuffer.order(ByteOrder.nativeOrder());
        mVerticeBuffer.asFloatBuffer().put(vert);
        mVerticeBuffer.position(0);

        if (mCoordBuffer == null) {
            mCoordBuffer = ByteBuffer.allocateDirect(coordVertices.length * 4);
            mCoordBuffer.order(ByteOrder.nativeOrder());
            mCoordBuffer.asFloatBuffer().put(coordVertices);
            mCoordBuffer.position(0);
        }
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Utils.LOGE("***** " + op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }




}
