package com.pot.gl.Test;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pot.gl.R;
import api.pot.gl.others.ColorSelector;
import api.pot.gl.xiv.XImageView;
import api.pot.gl.xiv.tools.Forgrounder;
import api.pot.gl.xiv.tools.IndetProgress;

public class MainActivity extends AppCompatActivity {
    private XImageView first, second, third, fourth, fifth, sixth, seventh, eighth, nineth, tenth, line_progress;

    private XImageView tryer, tryer2, tryer3, tryer4, tryer5;

    private XImageView test2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        init2();

        init3();

        init4();
    }

    private XImageView test3;
    private void init4() {
        test3 = (XImageView) findViewById(R.id.test3);
        test3.setOnFgClickListener(new Forgrounder.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Click", Toast.LENGTH_LONG).show();
            }
        });
    }

    boolean isFirst = true;
    private void init3() {
        test2 = (XImageView) findViewById(R.id.test2);
        test2.setDisableCircularTransformation(true);
        //test2.getTransitor().useTransition(false);
        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFirst = !isFirst;
                if(isFirst) test2.setImageDrawable(getResources().getDrawable(R.drawable.gost));
                else test2.setImageDrawable(getResources().getDrawable(R.drawable.wizz));
            }
        });
    }

    private void init2() {
        tryer = (XImageView) findViewById(R.id.tryer);
        //tryer4.setImgNormalSize(false);
        tryer.setDisableCircularTransformation(true);
        tryer.getNotRoundForm().setRoundPeaks(true, true, true, false);
        tryer.setUseGradientColorsForBorder(true);
        //

        tryer2 = (XImageView) findViewById(R.id.tryer2);
        //tryer4.setImgNormalSize(false);
        tryer2.setDisableCircularTransformation(true);
        tryer2.getNotRoundForm().setRoundPeaks(true, true, true, false);
        //
        tryer2.getDetProgress().init();
        tryer2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                MainActivity.initDet(tryer2);
                return false;
            }
        });

        tryer3 = (XImageView) findViewById(R.id.tryer3);
        //tryer4.setImgNormalSize(false);
        tryer3.setDisableCircularTransformation(true);
        tryer3.getNotRoundForm().setRoundPeaks(true, true, true, false);
        //
        tryer3.getIndetProgress().initType(IndetProgress.INDET_PROGRESS_STATIC_TURN);
        tryer3.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!tryer3.getIndetProgress().isPlaying)tryer3.getIndetProgress().play();
                else tryer3.getIndetProgress().stop();
                return false;
            }
        });

        tryer4 = (XImageView) findViewById(R.id.tryer4);
        tryer4.setImgNormalSize(false);
        tryer4.setDisableCircularTransformation(true);
        tryer4.getNotRoundForm().setRoundPeaks(true, true, true, false);
        //
        tryer4.getIndetProgress().initType(IndetProgress.INDET_PROGRESS_DYN_PENDULUM);
        tryer4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!tryer4.getIndetProgress().isPlaying)tryer4.getIndetProgress().play();
                else tryer4.getIndetProgress().stop();
                return false;
            }
        });

        final AnimatorSet mMorphingAnimatorSet = new AnimatorSet();
        tryer5 = (XImageView) findViewById(R.id.tryer5);
        //tryer5.setImgNormalSize(false);
        //tryer5.setDisableCircularTransformation(true);
        tryer5.getNotRoundForm().setRoundPeaks(true, true, true, true);
        tryer5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                tryer5.getTransformer().migrateIntoParent();
                return false;
            }
        });
        tryer5.getNotRoundForm().setRadiusRatio(0f/8);
        //
        final RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        ViewTreeObserver viewTreeObserver = container.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                container.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //
                final View view = tryer5;
                //
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                RelativeLayout.LayoutParams new_lParams = new RelativeLayout.LayoutParams(lParams.width, lParams.height);
                new_lParams.topMargin = view.getTop();
                new_lParams.leftMargin = view.getLeft();
                view.setLayoutParams(new_lParams);
                //
                final RelativeLayout.LayoutParams  tryer_layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                ValueAnimator progresser_left = ValueAnimator.ofInt(view.getLeft(), container.getMeasuredWidth()/2-tryer_layoutParams.width/2);
                progresser_left.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        tryer_layoutParams.leftMargin = val;
                        view.setLayoutParams(tryer_layoutParams);
                    }
                });
                ValueAnimator progresser_top = ValueAnimator.ofInt(view.getTop(), container.getHeight()/2-tryer_layoutParams.height/2);
                progresser_top.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        tryer_layoutParams.topMargin = val;
                        view.setLayoutParams(tryer_layoutParams);
                    }
                });
                mMorphingAnimatorSet.setDuration(1000);
                mMorphingAnimatorSet.playTogether(progresser_left, progresser_top);
            }
        });

    }

    private void init() {
        sixth = (XImageView) findViewById(R.id.sixth);
        sixth.getIndetProgress().initType(IndetProgress.INDET_PROGRESS_STATIC_TURN);
        sixth.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!sixth.getIndetProgress().isPlaying)sixth.getIndetProgress().play();
                else sixth.getIndetProgress().stop();
                return false;
            }
        });

        third = (XImageView) findViewById(R.id.third);
        third.getIndetProgress().initType(IndetProgress.INDET_PROGRESS_DYN_PENDULUM);
        third.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!third.getIndetProgress().isPlaying)third.getIndetProgress().play();
                else third.getIndetProgress().stop();
                return false;
            }
        });

        second = (XImageView) findViewById(R.id.second);
        second.getDetProgress().init();
        second.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                initDet(second);
                return false;
            }
        });

        first = (XImageView) findViewById(R.id.first);
        first.getPulsation().init();
        first.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!first.getPulsation().isPlaying)first.getPulsation().play();
                else first.getPulsation().stop();
                return false;
            }
        });

        tenth = (XImageView) findViewById(R.id.tenth);
        /*tenth.getAroundViews().get(1).setVisible(true);
        tenth.getAroundViews().get(1).setText("$");
        tenth.getAroundViews().get(3).setVisible(true);
        tenth.getAroundViews().get(3).setText("€");
        tenth.getAroundViews().get(5).setVisible(true);
        tenth.getAroundViews().get(5).setText("7");
        tenth.getAroundViews().get(7).setVisible(true);
        tenth.getAroundViews().get(7).setText("13");
        tenth.setUseGradientColorsForBorder(true);*/
    }

    public static void initDet(final XImageView main) {
        main.getDetProgress().init();

        ValueAnimator border_sweep_angle_anim = ValueAnimator.ofInt(0, 360);
        border_sweep_angle_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                main.getDetProgress().setValue(((float) val)/360);
            }
        });
        border_sweep_angle_anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                main.getDetProgress().startAnimStartAngle();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                main.getDetProgress().stopAnimStartAngle();
            }
        });
        //border_sweep_angle_anim.setRepeatCount(ObjectAnimator.INFINITE);

        AnimatorSet mMorphingAnimatorSet = new AnimatorSet();
        mMorphingAnimatorSet.setDuration(3000);
        mMorphingAnimatorSet.playTogether(border_sweep_angle_anim);
        mMorphingAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        mMorphingAnimatorSet.start();
    }
}
