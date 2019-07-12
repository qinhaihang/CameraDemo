package com.qhh.camerademo.opengl;

import android.opengl.GLES30;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glShaderSource;

public class ShaderHelper {



    public static int compileVertexShader(String shaderCode){
        return compileShader(GL_VERTEX_SHADER,shaderCode);
    }

    public static int compileFragmentShader(String shaderCode){
        return compileShader(GL_FRAGMENT_SHADER,shaderCode);
    }


    private static int compileShader(int type,String shaderCode){
        final int shaderObjectId = glCreateShader(type);
        if (shaderObjectId == 0){
            return 0;
        }
        glShaderSource(shaderObjectId,shaderCode);
        glCompileShader(shaderObjectId);
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId,GL_COMPILE_STATUS,compileStatus,0);
        if (compileStatus[0] == 0){
            glDeleteShader(shaderObjectId);
            return 0;
        }
        return shaderObjectId;
    }


    //把着色器链接进OpenGL程序
    public static int linkProgram(int vertexShaderId,int fragmentShaderId){
        int program = GLES30.glCreateProgram();
        if (program != 0) {
            GLES30.glAttachShader(program, vertexShaderId);
            GLES30.glAttachShader(program, fragmentShaderId);
            GLES30.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES30.GL_TRUE) {
                Utils.LOGE("Could not link program: ");
                Utils.LOGE(GLES30.glGetProgramInfoLog(program));
                GLES30.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }





}
