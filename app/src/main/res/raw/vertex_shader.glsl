attribute vec4 vPosition;
attribute vec2 a_texCoord;
varying lowp vec2 tc;
void main() {
    gl_Position = vPosition;
    tc = a_texCoord;
}