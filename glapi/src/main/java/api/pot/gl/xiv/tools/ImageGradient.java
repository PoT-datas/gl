package api.pot.gl.xiv.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

public class ImageGradient {
  public static final int LEFT = 0;
  public static final int TOP = 1;
  public static final int RIGHT = 2;
  public static final int BOTTOM = 3;
  Bitmap bitmap;

  public ImageGradient(Bitmap bitmap) {
    this.bitmap = bitmap;
  }

  public ImageGradient() {
  }

  public Bitmap sideGradient(float report, int side){return sideGradient(bitmap, report, side); }

  public static Bitmap sideGradient(Bitmap bmp, float report, int side){
    Bitmap bmpRet = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bmpRet);

    Bitmap bmpRogned = Bitmap.createBitmap(bmp.getWidth(), (int)(bmp.getHeight()-report*bmp.getHeight()), Bitmap.Config.ARGB_8888);
    Canvas canvasRogned = new Canvas(bmpRogned);
    canvasRogned.drawBitmap(bmp, 0, 0, null);

    canvas.drawBitmap(bmpRogned, 0, 0, null);

    Paint paint = new Paint();
    paint.setStyle(Paint.Style.FILL);
    paint.setShader(new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

    int left, top, right, bottom;
    int height_e = (int) (report*bmp.getHeight());
    if( height_e>255 ){
      float delta_h = height_e/255;
      float delta = delta_h;
      for (int a = 0; a<=255; a++) {
        paint.setAlpha(255-a);
        left = 0;
        top = (int) (bmpRogned.getHeight()+delta*a-delta);
        right = left+bmp.getWidth();
        bottom = (int) (top+delta);
        canvas.drawRect(left, top, right, bottom, paint);
      }
    }else {
      float delta_h = 255/height_e;
      float delta = delta_h;
      for (float a = 0; a<=height_e; a+=delta) {
        paint.setAlpha(255-(int)(255*a/height_e));
        left = 0;
        top = (int) (bmpRogned.getHeight()+a-delta);
        right = left+bmp.getWidth();
        bottom = (int) (top+delta);
        canvas.drawRect(left, top, right, bottom, paint);
      }
    }

    return bmpRet;
  }

  public Bitmap roundRectGradient(float report){
    return roundRectGradient(bitmap, report);
  }

  public static Bitmap roundRectGradient(Bitmap bmp, float report){
    if(report>=0.5)return null;

    Bitmap bmpRet = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bmpRet);

    Bitmap bmpRogned = Bitmap.createBitmap((int)(bmp.getWidth()-2*report*bmp.getWidth()), (int)(bmp.getHeight()-2*report*bmp.getHeight()), Bitmap.Config.ARGB_8888);
    Canvas canvasRogned = new Canvas(bmpRogned);
    canvasRogned.drawBitmap(bmp, canvasRogned.getWidth()/2-bmp.getWidth()/2, canvasRogned.getHeight()/2-bmp.getHeight()/2, null);

    canvas.drawBitmap(bmpRogned, canvas.getWidth()/2-bmpRogned.getWidth()/2, canvas.getHeight()/2-bmpRogned.getHeight()/2, null);

    Paint paint = new Paint();
    paint.setStyle(Paint.Style.STROKE);
    paint.setShader(new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

    int left, top, right, bottom;
    int width_e = (int) (report*bmp.getWidth()), height_e = (int) (report*bmp.getHeight());
    if( (width_e>255 && bmp.getWidth()<bmp.getHeight()) || (height_e>255 && bmp.getWidth()>bmp.getHeight()) ){
      float delta_w = width_e/255, delta_h = height_e/255;
      float delta = delta_w > delta_h ? delta_h : delta_w;
      paint.setStrokeWidth(delta);
      for (int a = 0; a<=255; a++) {
        paint.setAlpha(255-a);
        left = canvas.getWidth()/2-bmpRogned.getWidth()/2;
        top = canvas.getHeight()/2-bmpRogned.getHeight()/2;
        right = left+bmpRogned.getWidth();
        bottom = top+bmpRogned.getHeight();
        canvas.drawRect(left-delta*a+delta/2, top-delta*a+delta/2, right+delta*a-delta/2, bottom+delta*a-delta/2, paint);
      }
    }else {
      float delta_w = 255/width_e, delta_h = 255/height_e;
      float delta = delta_w > delta_h ? delta_h : delta_w;
      paint.setStrokeWidth(delta);
      for (float a = 0; a<=(width_e > height_e ? height_e : width_e); a+=delta) {
        paint.setAlpha(255-(int)(255*a/(width_e > height_e ? height_e : width_e)));//(a*delta)
        left = canvas.getWidth()/2-bmpRogned.getWidth()/2;
        top = canvas.getHeight()/2-bmpRogned.getHeight()/2;
        right = left+bmpRogned.getWidth();
        bottom = top+bmpRogned.getHeight();
        canvas.drawRect(left-a+delta/2, top-a+delta/2, right+a-delta/2, bottom+a-delta/2, paint);
      }
    }

    return bmpRet;
  }
}
