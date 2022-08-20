package api.pot.gl.xiv.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Handler;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import api.pot.gl.tools.Global;
import api.pot.gl.xiv.XImageView;

public class IndetProgress {
    private XImageView main;
    private float fourth_border0_first_val = 0, fourth_border1_first_val = 0, fourth_border2_first_val = 0;
    private float border0_first_val = 0, border1_first_val = 0, border2_first_val = 0;
    private TimeInterpolator di = new DecelerateInterpolator(), ai = new AccelerateInterpolator();
    private int periode = 1;
    private int type = INDET_PROGRESS_NONE;
    public float speed = 1;//tr/sec
    public Boolean isPlaying = false;
    private AnimatorSet indetProgress_2AnimatorSet;
    private ValueAnimator border0_start_angle_anim, border1_start_angle_anim, border2_start_angle_anim,
            border00_start_angle_anim, border11_start_angle_anim, border22_start_angle_anim;
    public AllColor ac = new IndetProgress.AllColor(Global.FIRST_GES_GRADIENT_COLOR, Global.SECOND_GES_GRADIENT_COLOR,
            Global.THIRD_GES_GRADIENT_COLOR);

    public static final int INDET_PROGRESS_NONE = -1;
    public static final int INDET_PROGRESS_STATIC_TURN = 0;
    public static final int INDET_PROGRESS_DYN_PENDULUM = 1;

    private boolean init=false, onPlay=false;

    public static class AllColor{
        public int f, s, t;

        public AllColor(int f, int s, int t) {
            this.f = f;
            this.s = s;
            this.t = t;
        }
    }

    public IndetProgress(XImageView main) {
        this.main = main;
    }

    private int defaultBorderWidth = 0, defaultInternalBorderWidth = 0;

    private boolean isViewReady = false;
    public void isViewReady(final int type){
        if(isViewReady) return;
        //
        ViewTreeObserver viewTreeObserver = main.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int new_type = type;
            @Override
            public void onGlobalLayout() {
                main.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                isViewReady = true;
                initType(new_type);
                if(onPlay){
                    play();
                    onPlay = false;
                }
            }
        });
    }

    private boolean gradient = true;
    public void usingGradient(boolean gradient) {
        this.gradient = gradient;
    }

    public void usingColors(int color1){
        usingGradient(false);
        usingColors(color1, color1,color1);
    }

    public void usingColors(int color1, int color2){
        usingColors(color1, color2,color1);
    }

    public void usingColors(int color1, int color2, int color3){
        ac = new IndetProgress.AllColor(color1, color2,color3);
    }

    public void initType(int type) {
        if (this.type == type) return;
        //
        isViewReady(type);
        if(!isViewReady) return;
        //
        defaultBorderWidth = main.getmMaxBorderWidth();
        defaultInternalBorderWidth = main.getmInternalBorderWidth();
        int border = (int) (main.getmCircleBackgroundRadius()/20);
        //
        main.setmBorderType(XImageView.BORDER_TYPE_UNDET_PROGRESS);
        if(main.getBorderWidth()==XImageView.DEFAULT_BORDER_WIDTH)
            main.setBorderWidth(border);
        if(main.getmInternalBorderWidth()==XImageView.DEFAULT_INTERNAL_BORDER_WIDTH)
            main.setmInternalBorderWidth(border*2);
        //
        this.type = type;
        //
        if (isPlaying) stop();
        //
        if (this.type == INDET_PROGRESS_DYN_PENDULUM)
            indetProgress_1();
        else if (this.type == INDET_PROGRESS_STATIC_TURN)
            indetProgress_2();
        //Colors
        if(gradient){
            main.clearBorderColorList();
            main.addBorderColor(ac.f);
            main.addBorderColor(ac.s);
            main.addBorderColor(ac.t);
            main.setUseGradientColorsForBorder(true);
        }else main.setUseGradientColorsForBorder(false);
        //
        init = true;
    }

    public void setSpeed(float tr_per_sec) {
        if(this.speed==speed)return;
        this.speed = speed;
    }

    public void setAc(AllColor ac) {
        this.ac = ac;
    }

    public void play() {
        if (isPlaying) return;
        if(!init){
            onPlay = true;
            return;
        }
        isPlaying = true;
        if (this.type == INDET_PROGRESS_DYN_PENDULUM) {
            addListeners_indetProgress_1();
            border0_start_angle_anim.start();
        } else if (this.type == INDET_PROGRESS_STATIC_TURN) {
            handler_for_static_indP.post(runnable_for_static_indP);
        }
        main.getArcs().get(0).setPaint(new PaintBundle(ac.f, null));
        main.getArcs().get(1).setPaint(new PaintBundle(ac.s, null));
        main.getArcs().get(2).setPaint(new PaintBundle(ac.t, null));
    }

    public void stop() {
        if(!isPlaying) return;
        if (this.type == INDET_PROGRESS_STATIC_TURN) {
        } else if (this.type == INDET_PROGRESS_DYN_PENDULUM && border0_start_angle_anim != null) {
            border0_start_angle_anim.removeAllListeners();
            border1_start_angle_anim.removeAllListeners();
            border2_start_angle_anim.removeAllListeners();
            border00_start_angle_anim.removeAllListeners();
            border11_start_angle_anim.removeAllListeners();
            border22_start_angle_anim.removeAllListeners();
            border0_start_angle_anim.cancel();
            border1_start_angle_anim.cancel();
            border2_start_angle_anim.cancel();
            border00_start_angle_anim.cancel();
            border11_start_angle_anim.cancel();
            border22_start_angle_anim.cancel();
        } else return;
        main.getArcs().get(0).setPaint(new PaintBundle(Color.TRANSPARENT, null));
        main.getArcs().get(1).setPaint(new PaintBundle(Color.TRANSPARENT, null));
        main.getArcs().get(2).setPaint(new PaintBundle(Color.TRANSPARENT, null));
        //
        main.setmMaxBorderWidth(defaultBorderWidth);
        main.setmInternalBorderWidth(defaultInternalBorderWidth);
        //
        isPlaying = false;
    }

    private void indetProgress_1() {
        main.getArcs().clear();
        main.addArc(240, 60, false, new PaintBundle(Color.TRANSPARENT, null));
        main.addArc(60, 60, false, new PaintBundle(Color.TRANSPARENT, null));
        main.addArc(240, 60, false, new PaintBundle(Color.TRANSPARENT, null));

        final int t = (int) (200/speed), T = 2 * t;

        border0_start_angle_anim = ValueAnimator.ofInt(0, 120);
        border0_start_angle_anim.setDuration(t);
        border0_start_angle_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (int) valueAnimator.getAnimatedValue();
                if (val % periode == 0)
                    main.getArcs().get(0).setStartAngle(border0_first_val + val);
            }
        });

        border1_start_angle_anim = ValueAnimator.ofInt(0, 120);
        border1_start_angle_anim.setDuration(t);
        border1_start_angle_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (int) valueAnimator.getAnimatedValue();
                if (val % periode == 0)
                    main.getArcs().get(1).setStartAngle(border1_first_val + val);
            }
        });

        border2_start_angle_anim = ValueAnimator.ofInt(0, 120);
        border2_start_angle_anim.setDuration(t);
        border2_start_angle_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (int) valueAnimator.getAnimatedValue();
                if (val % periode == 0)
                    main.getArcs().get(2).setStartAngle(border2_first_val + val);
            }
        });

        border00_start_angle_anim = ValueAnimator.ofInt(0, 60);
        border00_start_angle_anim.setInterpolator(new DecelerateInterpolator());
        border00_start_angle_anim.setDuration(T);
        border00_start_angle_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (int) valueAnimator.getAnimatedValue();
                if (val % periode == 0)
                    main.getArcs().get(0).setStartAngle(border0_first_val + val);
            }
        });

        border11_start_angle_anim = ValueAnimator.ofInt(0, 60);
        border11_start_angle_anim.setInterpolator(new DecelerateInterpolator());
        border11_start_angle_anim.setDuration(T);
        border11_start_angle_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (int) valueAnimator.getAnimatedValue();
                if (val % periode == 0)
                    main.getArcs().get(1).setStartAngle(border1_first_val + val);
            }
        });

        border22_start_angle_anim = ValueAnimator.ofInt(0, 60);
        border22_start_angle_anim.setInterpolator(new DecelerateInterpolator());
        border22_start_angle_anim.setDuration(T);
        border22_start_angle_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (int) valueAnimator.getAnimatedValue();
                if (val % periode == 0)
                    main.getArcs().get(2).setStartAngle(border2_first_val + val);
            }
        });
    }

    private void addListeners_indetProgress_1() {
        if(!main.getArcs().isEmpty()){
            main.getArcs().clear();
            main.addArc(240, 60, false, new PaintBundle(ac.f, null));
            main.addArc(60, 60, false, new PaintBundle(ac.s, null));
            main.addArc(240, 60, false, new PaintBundle(ac.t, null));
        }

        border0_start_angle_anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                border0_first_val = main.getArcs().get(0).startAngle;
                border1_first_val = main.getArcs().get(1).startAngle;
                border00_start_angle_anim.start();
                border1_start_angle_anim.start();
                border1_start_angle_anim.setInterpolator((border1_first_val % 240 == 0) ? ai : di);
            }
        });
        border1_start_angle_anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                border1_first_val = main.getArcs().get(1).startAngle;
                border2_first_val = main.getArcs().get(2).startAngle;
                border11_start_angle_anim.start();
                border2_start_angle_anim.start();
                border2_start_angle_anim.setInterpolator((border2_first_val % 240 == 0) ? ai : di);
            }
        });
        border2_start_angle_anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                border2_first_val = main.getArcs().get(2).startAngle;
                border0_first_val = main.getArcs().get(0).startAngle;
                border22_start_angle_anim.clone().start();
                border0_start_angle_anim.start();
                border0_start_angle_anim.setInterpolator((border0_first_val % 240 == 0) ? ai : di);
            }
        });

        border0_first_val = main.getArcs().get(0).startAngle;
        border0_start_angle_anim.setInterpolator((border0_first_val % 240 == 0) ? ai : di);
    }

    //
    public Handler handler_for_static_indP = null;
    public Runnable runnable_for_static_indP = null;
    public float static_indPSpeed = 0.5f;//tr/sec//this val must be negative if mvt is negative sense
    public long static_indPFluidity = 30/*ms*/;
    //
    public void setStatic_indPSpeed(float static_indPSpeed) {
        this.static_indPSpeed = static_indPSpeed;
    }
    //
    private void indetProgress_2() {
        main.getArcs().clear();
        main.addArc(0, 60, false, new PaintBundle(Color.TRANSPARENT, null));
        main.addArc(120, 60, false, new PaintBundle(Color.TRANSPARENT, null));
        main.addArc(240, 60, false, new PaintBundle(Color.TRANSPARENT, null));

        handler_for_static_indP = new Handler();
        runnable_for_static_indP = new Runnable() {
            @Override
            public void run() {
                main.getArcs().get(0).setStartAngle(main.getArcs().get(0).startAngle+360*static_indPSpeed/static_indPFluidity);
                main.getArcs().get(1).setStartAngle(main.getArcs().get(1).startAngle+360*static_indPSpeed/static_indPFluidity);
                main.getArcs().get(2).setStartAngle(main.getArcs().get(2).startAngle+360*static_indPSpeed/static_indPFluidity);

                if(isPlaying) handler_for_static_indP.postDelayed(runnable_for_static_indP, static_indPFluidity);
            }
        };
    }
}
