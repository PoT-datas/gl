package api.pot.gl.xiv.tools;

import android.graphics.SweepGradient;
import android.os.Handler;
import android.view.ViewTreeObserver;

import api.pot.gl.tools.Global;
import api.pot.gl.xiv.XImageView;

public class DetProgress {
    private XImageView main;
    public int[] colors = {Global.FIRST_GES_GRADIENT_COLOR, Global.SECOND_GES_GRADIENT_COLOR,
            Global.THIRD_GES_GRADIENT_COLOR, Global.FIRST_GES_GRADIENT_COLOR};
    public float[] positions = null;
    public float value = 0;//[0.00, 1.00]
    public float startAngle = 0;
    //
    Handler handler_for_start_angle = null;
    Runnable runnable_for_start_angle = null;
    boolean isStartAnglePlaying = false;
    float startAngleSpeed = 2f;//tr/sec//this val must be negative if mvt is negative sense
    long startAngleFluidity = 50/*ms*/;

    private int lastBorderWidth = 0,
        lastInternalBorderWidth = 0;

    private boolean isInit = false;
    boolean isViewReady = false;

    public DetProgress(XImageView main) {
        this.main = main;
    }

    public void isViewReady(){
        if(isViewReady) return;
        //
        ViewTreeObserver viewTreeObserver = main.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                main.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                isViewReady = true;
                init();
            }
        });
    }

    public void init() {
        if(isInit) return;

        isViewReady();
        if(!isViewReady) return;

        int border = (int) (main.getmCircleBackgroundRadius()/20);
        lastBorderWidth = main.getBorderWidth();
        lastInternalBorderWidth = main.getmInternalBorderWidth();
        main.setmBorderType(XImageView.BORDER_TYPE_DET_PROGRESS);
        if(main.getBorderWidth()==XImageView.DEFAULT_BORDER_WIDTH)
            main.setBorderWidth(border);
        if(main.getmInternalBorderWidth()==XImageView.DEFAULT_INTERNAL_BORDER_WIDTH);
            main.setmInternalBorderWidth(border);

        SweepGradient sweepGradient = new SweepGradient(main.getArcBorderRect().centerX(), main.getArcBorderRect().centerY(), colors, positions);

        main.clearArcs();
        main.addArc(startAngle, startAngle+360*value, false, new PaintBundle(-1, sweepGradient));

        main.getArcs().get(0).setSweepAngle(360*value);

        initAnimStartAngle();

        main.setUseGradientColorsForBorder(true);

        isInit = true;

        if(mustStartAnimStartAngle) startAnimStartAngle();
    }

    public void initAnimStartAngle(){
        handler_for_start_angle = new Handler();
        runnable_for_start_angle = new Runnable() {
            @Override
            public void run() {
                setStartAngle(startAngle+360*startAngleSpeed/startAngleFluidity);

                if(isStartAnglePlaying) handler_for_start_angle.postDelayed(runnable_for_start_angle, startAngleFluidity);
            }
        };
    }

    private boolean mustStartAnimStartAngle = false;
    public void startAnimStartAngle(){
        if(!isInit) {
            mustStartAnimStartAngle = true;
            return;
        }
        mustStartAnimStartAngle = false;
        if(isStartAnglePlaying) return;
        isStartAnglePlaying = true;
        handler_for_start_angle.post(runnable_for_start_angle);
    }

    public void stopAnimStartAngle(){
        if(!isStartAnglePlaying && !isInit) return;
        isStartAnglePlaying = false;
        main.setBorderWidth(lastBorderWidth);
        main.setmInternalBorderWidth(lastInternalBorderWidth);
        isInit = false;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
        main.getArcs().get(0).setPaint(new PaintBundle(-1, new SweepGradient(main.getArcBorderRect().centerX(), main.getArcBorderRect().centerY(),
                this.colors, this.positions)));
    }

    public void setPositions(float[] positions) {
        this.positions = positions;
        main.getArcs().get(0).setPaint(new PaintBundle(-1, new SweepGradient(main.getArcBorderRect().centerX(), main.getArcBorderRect().centerY(),
                this.colors, this.positions)));
    }

    public void setValue(float value) {
        if(!isInit) return;
        if(!(0<=value && value<=1 && main.getArcs().size()!=0))return;
        this.value = value;
        main.getArcs().get(0).setSweepAngle(360*value);
    }

    public void setStartAngle(float startAngle) {
        if(main.getArcs().size()==0)return;
        this.startAngle = startAngle;
        main.getArcs().get(0).setStartAngle(startAngle);
    }
}
