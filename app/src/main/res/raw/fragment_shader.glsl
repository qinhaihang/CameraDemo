varying lowp vec2 tc;
uniform sampler2D tex_yuv;
bool nFlag = false;
void main(void) {
    mediump float r,g,b,y,u,v;
    if (nFlag == false){
        y=texture2D(tex_yuv, tc).r ;
        u=texture2D(tex_yuv, tc).g ;
        v=texture2D(tex_yuv, tc).a ;
    } else {
        y=texture2D(tex_yuv, tc).b ;
        u=texture2D(tex_yuv, tc).g ;
        v=texture2D(tex_yuv, tc).a ;
    }
    nFlag = !nFlag;
    u=u-0.5;
    v=v-0.5;

    r=y+1.370705*v ;
    g=y-0.337633*u-0.698001*v;
    b=y+1.732446*u;
    gl_FragColor=vec4(r,g,b,1.0);
}
