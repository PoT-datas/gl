package api.pot.gl.xiv.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import api.pot.gl.xiv.XImageView;

public class Transformer {
    private XImageView main;
    private boolean backup_isImgNormalSize = true;
    private float backup_radiusRatio = -1;
    private RectF backup_dRect = null;
    //
    private ValueAnimator progresser, progresser_rect;
    private AnimatorSet mMorphingAnimatorSet = null;
    //private boolean isTransforming = false;
    private float evolution = 0;
    private float min=0, max=100;
    private boolean isToRound = true;
    //
    public long time = 500;
    //
    private boolean isMarginSaved = false;
    private int normalMarginLeft = 0, normalMarginTop = 0, normalWidth = 0, normalHeight = 0;
    public boolean isToMatch = true;

    public Transformer(final XImageView main){
        this.main = main;
    }

    public void transformRadius(){
        //
        if(backup_radiusRatio==-1) {
            backup_radiusRatio = main.getNotRoundForm().radiusRatio;
            backup_isImgNormalSize = main.isImgNormalSize();
            isToRound = main.isDisableCircularTransformation();
        }
        if(!main.isDisableCircularTransformation()) main.getNotRoundForm().setRadiusRatio(1f/2);
        //
        if(isToRound){
            progresser = ValueAnimator.ofFloat(main.getNotRoundForm().radiusRatio, 1f/2);
            progresser.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float val = (Float) valueAnimator.getAnimatedValue();
                    main.getNotRoundForm().setRadiusRatio(val);
                }
            });
        }else {
            progresser = ValueAnimator.ofFloat(main.getNotRoundForm().radiusRatio, backup_radiusRatio);
            progresser.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float val = (Float) valueAnimator.getAnimatedValue();
                    main.getNotRoundForm().setRadiusRatio(val);
                }
            });
        }
        isToRound = !isToRound;
        //
        mMorphingAnimatorSet = new AnimatorSet();
        mMorphingAnimatorSet.setDuration(time);
        mMorphingAnimatorSet.playTogether(progresser);
        mMorphingAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                main.setDisableCircularTransformation(true);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                main.setDisableCircularTransformation(isToRound);
            }
        });
        mMorphingAnimatorSet.setInterpolator(new AccelerateInterpolator());
        mMorphingAnimatorSet.start();
    }

    public void migrateIntoParent(){
        View container = null;
        try{
            container = (View) main.getParent();
        }catch (Exception e){return;}

        transformRadius();


        final XImageView view = main;
        //
        int cx=0, cy=0, w=0, h=0;
        //
        RelativeLayout.LayoutParams rightLparams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        RelativeLayout.LayoutParams new_lParams = new RelativeLayout.LayoutParams(rightLparams.width, rightLparams.height);
        new_lParams.topMargin = view.getTop();
        new_lParams.leftMargin = view.getLeft();
        view.setLayoutParams(new_lParams);
        if(!isMarginSaved){
            normalMarginLeft = view.getLeft();
            normalMarginTop = view.getTop();
            normalWidth = rightLparams.width;
            normalHeight = rightLparams.height;
            isMarginSaved = true;
        }
        //
        if(isToMatch){
            //
            /*int lwidth = ;
            int lheight = ;*/
            /*String s = new_lParams.width+"/"+new_lParams.height;
            new_lParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            new_lParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            view.setLayoutParams(new_lParams);
            s+=" $ "+((CircleImageView) view).getSmartWidth()+"/"+((CircleImageView) view).getSmartHeight();
            Toast.makeText(main.getContext(), s, Toast.LENGTH_LONG).show();*/
            //
            w = container.getWidth();
            h = container.getHeight();
            cx = 0;/*container.getWidth()/2-rightLparams.width/2;*/
            cy = 0;/*container.getHeight()/2-rightLparams.height/2;*/
        }else {
            view.setLayoutParams(rightLparams);
            w = normalWidth;
            h = normalHeight;
            cx = normalMarginLeft;
            cy = normalMarginTop;
        }
        //
        final RelativeLayout.LayoutParams  tryer_layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        ValueAnimator progresser_left = ValueAnimator.ofInt(view.getLeft(), cx);
        progresser_left.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                tryer_layoutParams.leftMargin = val;
                view.setLayoutParams(tryer_layoutParams);
            }
        });
        ValueAnimator progresser_top = ValueAnimator.ofInt(view.getTop(), cy);
        progresser_top.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                tryer_layoutParams.topMargin = val;
                view.setLayoutParams(tryer_layoutParams);
            }
        });
        ValueAnimator progresser_width = ValueAnimator.ofInt(view.getSmartWidth(), w);
        progresser_width.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                tryer_layoutParams.width = val;
                view.setLayoutParams(tryer_layoutParams);
            }
        });
        ValueAnimator progresser_height = ValueAnimator.ofInt(view.getSmartHeight(), h);
        progresser_height.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                tryer_layoutParams.height = val;
                view.setLayoutParams(tryer_layoutParams);
            }
        });
        AnimatorSet mMorphingAnimatorSet = new AnimatorSet();
        mMorphingAnimatorSet.setDuration(time);
        mMorphingAnimatorSet.playTogether(progresser_left, progresser_top, progresser_width, progresser_height);
        mMorphingAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mMorphingAnimatorSet.start();
        //
        isToMatch = !isToMatch;
    }
}
