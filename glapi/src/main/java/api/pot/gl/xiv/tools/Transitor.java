package api.pot.gl.xiv.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import api.pot.gl.xiv.XImageView;

public class Transitor {
    private XImageView main;

    private Bitmap newBmp, oldBmp, bmp;

    private Canvas canvas;
    private Paint paint = new Paint();
    Bitmap duo;

    private Bitmap source, target;

    public Transitor(XImageView main) {
        this.main = main;
    }

    public void source(Bitmap source){
        this.source = source;
    }

    public void target(Bitmap target){
        this.target = target;
    }

    public void source2target(Bitmap source, Bitmap target){
        this.source = source;
        this.target = target;
    }

    public void transite(){
        if(!useTransition) return;
        //
        if(source==null || target==null || transition) return;
        //
        try{
            defaultAlpha = (int) main.getAlpha();
        }catch (Exception e){}
        main.setAlpha(defaultAlpha);
        //
        paint.set(main.getmBitmapPaint());
        //
        if(progresser!=null && progresser.isRunning()){
            progresser.cancel();
            progresser.removeAllUpdateListeners();
        }
        //
        progresser = ValueAnimator.ofFloat(0, 1);
        progresser.setDuration(duration);
        progresser.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                evolution = (float) valueAnimator.getAnimatedValue();
            }
        });
        progresser.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                transition = false;
                main.setAlpha(defaultAlpha);
                main.invalidater();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                transition = false;
                main.setAlpha(defaultAlpha);
                main.invalidater();
            }
        });
        progresser.start();
        //
        transition = true;
    }

    int cmp=0;

    public Paint update(){
        if(!useTransition) return null;
        //
        if(!transition) return null;
        //
        main.setAlpha(255/*defaultAlpha*evolution*/);
        paint.setAlpha((int) (defaultAlpha*(1-evolution)));
        //
        return paint;
    }

    public boolean isFirstDraw(){
        if(!useTransition) return false;
        //
        if(!transition) return false;
        //
        if (!isFirstDraw) {
            isFirstDraw = true;
            if(transition) main.invalidater();
            return false;
        }
        isFirstDraw = false;
        return false;//true;
    }

    public void useTransition(boolean useTransition){
        this.useTransition = useTransition;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void useDimens(float mDrawableRadius, RectF mDrawableRect, Path notRoundForm_drawablePath){
        this.mDrawableRadius = mDrawableRadius;
        if(mDrawableRect==null) this.mDrawableRect = mDrawableRect;
        else this.mDrawableRect = new RectF(mDrawableRect);
        if(notRoundForm_drawablePath==null) this.notRoundForm_drawablePath = notRoundForm_drawablePath;
        else this.notRoundForm_drawablePath = new Path(notRoundForm_drawablePath);
    }

    public float mDrawableRadius = 0;
    public RectF mDrawableRect;
    public Path notRoundForm_drawablePath;
    //
    private int defaultAlpha = 255;
    private ValueAnimator progresser;
    private float evolution = 0;
    private boolean isFirstDraw = true;
    //
    public boolean useTransition = true;
    public boolean transition = false;
    public long duration = 500;

    public void transite(Bitmap new_bmp, Bitmap old_bmp) {
        if(1!=2) {
            old_bmp = new_bmp;
            return;
        }
        //
        this.oldBmp = old_bmp;
        this.newBmp = new_bmp;
        //
        if(oldBmp==null){
            oldBmp = newBmp;
            return;
        }
        if(newBmp==null) return;
        //
        bmp = Bitmap.createBitmap(oldBmp.getWidth(), oldBmp.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bmp);
        paint.setAlpha(127);
        canvas.drawBitmap(oldBmp, 0, 0, paint);
        canvas.drawBitmap(newBmp, oldBmp.getWidth()/2-newBmp.getWidth()/2, oldBmp.getHeight()/2-newBmp.getHeight()/2, paint);
        main.setmBitmap(bmp);
    }

    private void transiting(float evolution){
        transiting(evolution);
    }

}