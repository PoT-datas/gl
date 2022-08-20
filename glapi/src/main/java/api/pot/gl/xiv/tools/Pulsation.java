package api.pot.gl.xiv.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.animation.AccelerateInterpolator;

import api.pot.gl.xiv.XImageView;

import java.util.Random;

public class Pulsation {
  private XImageView main;
  private RGB_MAKER rgb_maker;
  private AnimatorSet mMorphingAnimatorSet;
  public Boolean isPlaying = false;
  public float speed = 2;//mvt/sec

  public Pulsation(XImageView main) {
    this.main = main;
  }

  public static class RGB_MAKER{
    public int r, g, b;

    public RGB_MAKER(int r, int g, int b) {
      this.r = r;
      this.g = g;
      this.b = b;
    }
  }

  public void play(){
    if(isPlaying)return;
    isPlaying = true;
    mMorphingAnimatorSet.start();
  }

  public void stop(){
    mMorphingAnimatorSet.removeAllListeners();
    mMorphingAnimatorSet.cancel();
    main.setBorderColor(Color.argb(0, rgb_maker.r, rgb_maker.g, rgb_maker.b));
    isPlaying = false;
  }

  public void setSpeed(float nbr_mvt_per_sec) {
    if(this.speed==speed)return;
    this.speed = speed;
  }

  public void init() {
    main.getArcs().clear();
    main.setmBorderType(XImageView.BORDER_TYPE_NORMAL);
    if(main.getmMaxBorderWidth()==XImageView.DEFAULT_MAX_BORDER_WIDTH || main.getmMaxBorderWidth()<main.getBorderWidth())
      main.setmMaxBorderWidth(70);
    if(main.getBorderWidth()==XImageView.DEFAULT_BORDER_WIDTH)
      main.setBorderWidth(0);
    if(main.getmInternalBorderWidth()==XImageView.DEFAULT_INTERNAL_BORDER_WIDTH)
      main.setmInternalBorderWidth(10);

    final Random rnd = new Random();
    rgb_maker = new RGB_MAKER(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

    ValueAnimator border_color_anim = ValueAnimator.ofInt(150, 0);
    border_color_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int val = (Integer) valueAnimator.getAnimatedValue();
        main.setBorderColor(Color.argb(val, rgb_maker.r, rgb_maker.g, rgb_maker.b));
      }
    });
    border_color_anim.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationRepeat(Animator animation) {
        super.onAnimationRepeat(animation);
        rgb_maker = new RGB_MAKER(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
      }
    });
    border_color_anim.setRepeatCount(ObjectAnimator.INFINITE);

    ValueAnimator border_width_anim = ValueAnimator.ofInt(0, main.getmMaxBorderWidth());
    border_width_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int val = (Integer) valueAnimator.getAnimatedValue();
        main.setBorderWidth(val);
      }
    });
    border_width_anim.setRepeatCount(ObjectAnimator.INFINITE);

    mMorphingAnimatorSet = new AnimatorSet();
    mMorphingAnimatorSet.setDuration((long) (1000/speed));
    mMorphingAnimatorSet.playTogether(border_color_anim, border_width_anim);
    mMorphingAnimatorSet.setInterpolator(new AccelerateInterpolator());
  }
}
