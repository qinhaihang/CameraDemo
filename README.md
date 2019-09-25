# CameraDemo
Android Camera 

## CameraX
在取得数据回调的时候，是 Image 类，通过下边的方法可以转化为 byte 数组，方便进一步的处理数据
```java

    public byte[] dealImage(Image image){

        if(imageData == null)
            return null;

        if(image!=null&&image.getPlanes().length>=3) {
            Image.Plane Y = image.getPlanes()[0];
            Image.Plane U = image.getPlanes()[1];
            Image.Plane V = image.getPlanes()[2];

            int Yb = Y.getBuffer().remaining();
            int Ub = U.getBuffer().remaining();
            int Vb = V.getBuffer().remaining();
            // 每次分配内存 耗费资源
            //byte[] imageData = new byte[Yb+Ub+Vb];
            Y.getBuffer().get(imageData, 0, Yb);
            U.getBuffer().get(imageData, Yb, Ub);
            V.getBuffer().get(imageData, Yb + Ub, Vb);
            return imageData;
        }else{
            return null;
        }
    }

```
