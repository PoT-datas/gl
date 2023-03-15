package api.pot.gl.xiv.tools.stars;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import api.pot.gl.geo.TextPainter;
import api.pot.gl.tools.Global;
import api.pot.gl.xiv.XImageView;
import api.pot.gl.xiv.tools.StarSystemMng;

import static api.pot.gl.xiv.tools.stars.StarMng.drawPolygon;

public class StarSystem{
    private XImageView main;
    private ImgPlayer imgPlayer;


    public static final int STAR_SYSTEM_NORMAL = 0;
    public static final int STAR_SYSTEM_STAT = 1;
    public static final int STAR_SYSTEM_GONE = 2;


    private OnStarNomberChangeListener onStarNomberChangeListener = null;

    public StarSystem(XImageView main) {
        this.main = main;
        imgPlayer = new ImgPlayer(main);
        //setVisibility(STAR_SYSTEM_NORMAL);
        visibility = STAR_SYSTEM_GONE;
    }

    public void setOnStarNomberChangeListener(OnStarNomberChangeListener onStarNomberChangeListener) {
        this.onStarNomberChangeListener = onStarNomberChangeListener;
    }

    public void removeOnStarNomberChangeListener() {
        this.onStarNomberChangeListener = null;
    }
    /*
     * ceci est pour un cas general de fonctionnement
     * tout est parfait comme ca
     * */
    public int nbrStarToShow = 5;
    public int nbrStarSelected =0;
    public StatStarDatas statStarDatas = null, normalStatStarDatas = null;
    public Boolean isStarting = true;
    public int visibility = STAR_SYSTEM_GONE;
    private float evolRatio = 0;
    //
    private List<RectF> starsBounds = new ArrayList<RectF>();
    private List<Integer> allStar = new ArrayList<Integer>();
    //
    public final int DEFAULT_STAR_BORDER_COLOR = Color.parseColor("#4b0082");

    public Boolean isEnabled = false;
    public RectF bounds = null;
    public Star starModel = new Star();
    public List<Integer> colors = new ArrayList<Integer>();
    public float ratioPadding = 1.0f/4.0f, padding=0, maxWidth=0;
    public Boolean usingColorMoving = false;
    public long startAnimDuration = 6000;
    private long startTime=0, realTime=0, now=0;
    private float ratioTime = 0, lastRatioTime = -1;

    //
    private Paint touchPaint = new Paint();
    private TouchStarDatas touchStarDatas = new TouchStarDatas();
    private Boolean isStarMade = false;
    private List<Bitmap> starBmpList = new ArrayList<Bitmap>();
    //
    private float ratioMargin = 1.0f/4f;

    public int getSelectedStar() {
        return this.nbrStarSelected;
    }

    public void setTransparenceBg(){
        main.setEnabledCircularTransformation(false);/*
        main.clearColors();
        main.setMainType(ComplexImageView.Main.MAIN_GRADIENT_LINEAR);
        main.addColor(Color.TRANSPARENT);*/
    }

    private class TouchStarDatas{
        int onTouchStar = 0;
        int c_alpha = Global.MAIN_GES_FG_TOUCH_START_ALPHA;
        float evol_ratio = 0;
        ValueAnimator alphaAnim;

        public TouchStarDatas() {
            init();
        }

        public void start(){
            if(alphaAnim!=null && !alphaAnim.isRunning())
                alphaAnim.start();
        }

        public void init(){
            alphaAnim = ValueAnimator.ofInt(0, 100);
            alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    //
                    evol_ratio = Float.valueOf(val)/100;
                    c_alpha = (int) (Global.MAIN_GES_FG_TOUCH_START_ALPHA +
                            (Global.MAIN_GES_FG_TOUCH_END_ALPHA-Global.MAIN_GES_FG_TOUCH_START_ALPHA) * evol_ratio);
                    //
                    main.invalidater();
                }
            });
            alphaAnim.setDuration(Global.MAIN_GES_FG_TOUCH_DURATION);
            alphaAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    onTouchStar = 0;
                }
            });
            alphaAnim.setInterpolator(new AccelerateInterpolator());
        }

        public void setOnTouchStar(int onTouchStar) {
            if(alphaAnim!=null && alphaAnim.isRunning()){
                alphaAnim.removeAllUpdateListeners();
                alphaAnim.cancel();
                init();
            }else if(alphaAnim==null)
                init();
            this.onTouchStar = onTouchStar;
        }
    }

    public StarSystem() {
        touchPaint.setColor(Global.MAIN_GES_FG_TOUCH_COLOR);
    }

    public void setEnabled(Boolean isEnabled) {
        if(this.isEnabled == isEnabled) return;
        this.isEnabled = isEnabled;
        //
        main.invalidater();
    }

    public void onTouchEvent(MotionEvent event) {
        if(!isEnabled || event.getAction()!=MotionEvent.ACTION_DOWN) return;

        int i = 0, nbr = 0;
        float x=event.getX(), y=event.getY();
        for(RectF bound : starsBounds){
            if(bound.left<=x && x<=bound.right && bound.top<=y && y<=bound.bottom)
                nbr = i+1;
            i++;
        }
        //
        if(nbr!=0){
            setNbrStarSelected( (nbrStarSelected==1 && nbr==1) ? 0 : nbr );
            touchStarDatas.setOnTouchStar(nbr);
        }
        //
        main.invalidater();
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
        if(this.visibility!=STAR_SYSTEM_GONE) {
            setTransparenceBg();
            if(statStarDatas == null && normalStatStarDatas == null){
                final StarSystemMng starSystemMng = new StarSystemMng();
                setStarting(false);
                setStarInfos(starSystemMng);
            }
        }
        //
        main.invalidater();
    }

    public void setNbrStarToShow(int nbrStarToShow) {
        if(this.nbrStarToShow == nbrStarToShow) return;
        //
        this.nbrStarToShow = nbrStarToShow;
        //
        main.invalidater();
    }

    public void setStarInfos(StarSystemMng starSystemMng) {
        starSystemMng.generateStatDatas();
        setStatStarDatas(starSystemMng.getNbrStarer(), starSystemMng.getListStarInfos());
        main.invalidater();
    }

    private void setStatStarDatas(int nbrStarer, List<Float> values) {
        StatStarDatas statStarDatas = new StatStarDatas();
        statStarDatas.nbrStarer = nbrStarer;
        statStarDatas.values = values;
        statStarDatas.generateNote();
        //
        if(normalStatStarDatas==null) this.normalStatStarDatas = statStarDatas;
        else this.statStarDatas = statStarDatas;
    }

    public void setStarting(Boolean isStarting) {
        this.isStarting = isStarting;
    }

    public void setNbrStarSelected(float nbrStarSelected) {
        if(nbrStarSelected>=Float.valueOf(nbrStarToShow)/2) usingColorMoving=true;
        else usingColorMoving=false;
        //
        this.nbrStarSelected = (int) Math.floor(nbrStarSelected);
        this.evolRatio = nbrStarSelected - this.nbrStarSelected;
        if(this.nbrStarSelected<this.nbrStarToShow && this.evolRatio>0) this.nbrStarSelected++;
        //
        //if(!isStarting) invalidater();
        //
        if(onStarNomberChangeListener!=null) onStarNomberChangeListener.onStarNomberChange(this.nbrStarSelected);
    }

    public void update(Canvas cvs, RectF bounds){
        if(visibility == STAR_SYSTEM_GONE || cvs==null || bounds==null) return;
        //
        if(visibility==STAR_SYSTEM_STAT) {
            if(normalStatStarDatas==null) return;
            //
            if(isStarting)
                updateStartAnim();
            else if(statStarDatas==null)
                statStarDatas=normalStatStarDatas;
            //
            getSatStar(cvs, bounds);
        }else if(visibility==STAR_SYSTEM_NORMAL){
            if(nbrStarSelected<0 && nbrStarToShow<1) return;
            getAllStars(cvs, bounds);
        }
    }

    public void updateStartAnim(){
        if(now == -1) {
            isStarting = false;
            return;
        }else {
            now = android.os.SystemClock.uptimeMillis();
            if (startTime == 0) {
                startTime = now;
            }
            //
            if((now - startTime)>startAnimDuration){
                statStarDatas = normalStatStarDatas;
                now = -1;
                return;
            }
            //
            realTime = (int) ((now - startTime) % startAnimDuration);
            ratioTime = ((float) realTime) / startAnimDuration;
            lastRatioTime = ratioTime;
        }
        //
        if(statStarDatas==null) statStarDatas = new StatStarDatas();
        statStarDatas.nbrStarer = (int) (ratioTime*normalStatStarDatas.nbrStarer);
        int i=0;
        for(float elt : normalStatStarDatas.values){
            if(statStarDatas.values.size()!=normalStatStarDatas.values.size())
                statStarDatas.values.add(ratioTime*elt);
            else
                statStarDatas.values.set(i, ratioTime*elt);
            i++;
        }
        statStarDatas.generateNote();
    }

    public void getSatStar(Canvas cvs, RectF bounds){

        /*Paint p = new Paint();
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5);
        cvs.drawRect(bounds, p);*/

        //note numerique
        RectF numTextBound = new RectF(bounds.left+ratioMargin*bounds.width()/4, bounds.top,
                bounds.left+bounds.width()/2, bounds.top+bounds.height()/2);
        Paint textPaint = new Paint();
        textPaint.setColor(Global.TEXT_COLOR_ON_MAIN_GES_TEXT_COLOR);
        textPaint.setTextSize(TextPainter.getTextSizeFromContainer("0.00", textPaint, numTextBound.width(), numTextBound.height()));
        TextPainter.drawTextCentered(Global.numberFormat.format(statStarDatas.note), textPaint, cvs, numTextBound, true);
        //note starique
        setNbrStarSelected(statStarDatas.note);
        ratioPadding = 0;
        getAllStars(cvs, new RectF(bounds.left+ratioMargin*bounds.width()/4, bounds.centerY(), bounds.left+bounds.width()/2, bounds.centerY()+bounds.height()/4));
        //nbr avis
        textPaint.setTextSize(0);
        TextPainter.drawTextCentered(Global.getRightDivisionNumber(statStarDatas.nbrStarer), textPaint, cvs,
                new RectF(bounds.left+ratioMargin*bounds.width()/4, bounds.bottom-bounds.height()/4, bounds.left+bounds.width()/2, bounds.bottom), true);
        //proportion
        float c_top, c_bottom;
        RectF nowBound;
        Paint statPaint = new Paint();
        statPaint.setColor(DEFAULT_STAR_BORDER_COLOR);
        float e_h = bounds.height()/statStarDatas.values.size();
        bounds = new RectF(bounds.left, bounds.top-e_h/3, bounds.right, bounds.bottom-e_h);
        e_h = bounds.height()/statStarDatas.values.size();
        for(int i=0;i<statStarDatas.values.size();i++){
            c_top = (statStarDatas.values.size()-1)*e_h - (bounds.top+i*e_h);
            c_bottom = c_top+e_h;
            nowBound = new RectF(bounds.centerX()+ratioMargin*bounds.width()/4, c_top + ratioMargin*e_h,
                    bounds.right-ratioMargin*bounds.width()/4, c_bottom - ratioMargin*e_h);
            progressRect(cvs, nowBound, statStarDatas.values.get(i), i, statPaint);
        }
    }

    public void progressRect(Canvas cvs, RectF bounds, float evolution, int value, Paint paint){
        if(bounds==null || 0>=bounds.width() || 0>=bounds.height()) return;
        evolution = evolution>1?1:evolution;
        //
        Paint alphaPaint = new Paint(paint);
        alphaPaint.setAlpha(70);
        //
        cvs.drawRoundRect(bounds, bounds.height()/2, bounds.height()/2, alphaPaint);
        //Log.d("ges_zerro", +" ********************* "+);
        if(evolution>0) {
            if (evolution * bounds.width() > bounds.height())
                cvs.drawRoundRect(new RectF(bounds.left, bounds.top, bounds.left + evolution * bounds.width(), bounds.bottom),
                        bounds.height() / 2, bounds.height() / 2, paint);
            else if(evolution> 0){
                int w = (int) (evolution * bounds.width() / 2),
                        h = (int) bounds.height();
                if(!(w==0 || h==0)) {
                    Bitmap bmpRogned = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    Canvas canvasRogned = new Canvas(bmpRogned);
                    canvasRogned.drawOval(new RectF(0, 0, bounds.height(), bounds.height()), paint);
                    cvs.drawBitmap(bmpRogned, bounds.left, bounds.top, null);
                    //
                    Bitmap bmpRogned2 = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    Canvas canvasRogned2 = new Canvas(bmpRogned2);
                    canvasRogned2.drawOval(new RectF(-(bounds.height() - evolution * bounds.width() / 2), 0,
                            -(bounds.height() - evolution * bounds.width() / 2) + bounds.height(), bounds.height()), paint);
                    cvs.drawBitmap(bmpRogned2, bounds.left + bmpRogned.getWidth(), bounds.top, null);
                }
            }
        }
        //
        NumberFormat nf = new DecimalFormat("0.##");
        String troncVal = nf.format((evolution)*100),
                allText = troncVal+"%"+(value>=0?" ("+value+")":"");
        TextPainter.drawTextCentered(allText, null, cvs, bounds, true);
    }

    public class StatStarDatas{
        public int maxStar = 5;
        public float note=0;
        public long nbrAllStar;
        public List<Float> values = new ArrayList<Float>();
        public int nbrStarer;

        public void generateNote(){
            if(values.size()>0){
                float note = 0;
                int i=0;
                for(Float value : values) {
                    note+=value*i;
                    i++;
                }

                this.note = note;
            }
            //
            main.invalidater();
        }

        public StatStarDatas(float note, long nbrAllStar, List<Float> values) {
            this.note = note;
            this.nbrAllStar = nbrAllStar;
            this.values = values;
            //
            main.invalidater();
        }

        public StatStarDatas() {
        }
    }

    /*public void getAllStars(Canvas cvs, final RectF bounds){
        if(cvs==null || bounds==null)return;
        //
        maxWidth = bounds.width()/nbrStarToShow;
        padding = ratioPadding*maxWidth;
        if(starModel.paint==null || true){
            starModel.paint = new Paint();
            if(usingColorMoving){
                imgPlayer.bound = bounds;
                imgPlayer.onInit();
                imgPlayer.update();
                Shader fillShader = new BitmapShader((imgPlayer.cBmp!=null?imgPlayer.cBmp:((BitmapDrawable)main.getDrawable()).getBitmap()),
                        Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                starModel.paint.setShader(fillShader);
            }else starModel.paint.setColor(DEFAULT_STAR_BORDER_COLOR);
            starModel.paint.setStyle(Paint.Style.FILL);
        }

        starBmpList.clear();
        float starWidth = maxWidth-2*padding;

        for(int i=0;i<nbrStarSelected;i++){///solve by draw real bitmat with XIV on draw interface
            try {
                starModel.bound = new RectF(bounds.left+padding+i*maxWidth, bounds.top,
                    bounds.left+padding+i*maxWidth + starWidth, bounds.height());
                starBmpList.add(starModel.getStar());
            }catch (Exception e){
                Toast.makeText(main.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if(nbrStarToShow!=0){
            int i=0;
            if(starModel.bound==null) starModel.bound = new RectF(bounds.left+padding+i*maxWidth, 0,
                    bounds.left+padding+i*maxWidth + starWidth, bounds.height());
            starModel.paint.setShader(null);
            starModel.paint.setStyle(Paint.Style.STROKE);
            starModel.paint.setColor(DEFAULT_STAR_BORDER_COLOR);
            starBmpList.add(starModel.getStar());
        }

        if(starBmpList.size()>0) {
            for(int i=0;i<nbrStarToShow;i++){
                //save location
                if(starsBounds.size()<nbrStarToShow){
                    float left = bounds.left + padding + i * maxWidth,
                            top = bounds.top + bounds.height() / 2 - starModel.bound.height() / 2;
                    starsBounds.add(new RectF(left-padding, top-padding,
                            left+starModel.bound.width()+padding, top+starModel.bound.height()+padding));
                }
                //
                if((i==nbrStarSelected-1) && (0<evolRatio && evolRatio<1)) {
                    Paint paintAlpha = new Paint();
                    paintAlpha.setAlpha(70);
                    cvs.drawBitmap(starBmpList.get(nbrStarSelected), bounds.left + padding + i * maxWidth,
                            bounds.top + bounds.height() / 2 - starModel.bound.height() / 2, paintAlpha);
                    //
                    Bitmap partStar = getRatioBmp(starBmpList.get(i), evolRatio);
                    if(partStar!=null)
                        cvs.drawBitmap(partStar, bounds.left + padding + i * maxWidth,
                                bounds.top + bounds.height() / 2 - starModel.bound.height() / 2, null);
                }else if(starModel.bound!=null){
                    cvs.drawBitmap(starBmpList.get(i<nbrStarSelected?i:nbrStarSelected), bounds.left+padding+i*maxWidth,
                            bounds.top + bounds.height() / 2 - starModel.bound.height() / 2, null);
                }if(i+1==touchStarDatas.onTouchStar){
                    touchStarDatas.start();
                    touchPaint.setAlpha(touchStarDatas.c_alpha);
                    float radius = (starsBounds.get(i).width()+padding*2)/2>bounds.height()/2?
                            bounds.height()/2:(starsBounds.get(i).width()+padding*2)/2;
                    cvs.drawCircle(starsBounds.get(i).centerX(), starsBounds.get(i).centerY(),
                            radius, touchPaint);
                }
            }
        }
    }*/

    private Bitmap getRatioBmp(Bitmap bmp, float ratio) {
        int w = (int) (bmp.getWidth()*ratio),
                h = bmp.getHeight();
        if(w==0 || h==0)return null;
        Bitmap bmpRogned = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasRogned = new Canvas(bmpRogned);
        canvasRogned.drawBitmap(bmp, 0, 0, null);
        return bmpRogned;
    }

    /*public static class Star{
        public Boolean isStarBorder = true;
        public Paint paint = null;
        public RectF bound = null;
        //
        private PentagonStarDatas psDatas = null;
        private float widthRatio = 10.0f/75.0f;
        private Point[] points = null;

        public Bitmap getStar(){
            if(bound==null || paint==null || 0>=bound.width() || 0>=bound.height())return null;

            Bitmap star = Bitmap.createBitmap((int) bound.width(),(int) bound.height(), Bitmap.Config.ARGB_8888);
            Canvas starCvs = new Canvas(star);

            if(bound.height()>bound.width()) bound = new RectF(bound.left,bound.top,bound.right,bound.top+bound.width());

            points = getPointsForStar(bound);
            starCvs.translate(-bound.left, -bound.top);
            drawPolygon(starCvs, points, paint);

            return star;
        }

        public void drawPolygon(Canvas canvas, Point[] points, Paint polyPaint) {
            // line at minimum...
            if (points.length < 2) {
                return;
            }

            // path
            Path polyPath = new Path();
            polyPath.moveTo(points[0].x, points[0].y);
            int i, len;
            len = points.length;
            for (i = 0; i < len; i++) {
                polyPath.lineTo(points[i].x, points[i].y);
            }
            //polyPath.lineTo(points[0].x, points[0].y);

            // draw
            canvas.drawPath(polyPath, polyPaint);
        }

        private class Point {
            public float x = 0;
            public float y = 0;
            public Point(float x, float y) {
                this.x = x;
                this.y = y;
            }

            public Point(double x, double y) {
                this.x = (float) x;
                this.y = (float) y;
            }

            public Point() {}
        }

        private Point[] getPointsForStar(RectF bound) {
            //if(psDatas==null){
            psDatas = new PentagonStarDatas();
            float radius = bound.width()>bound.height()?bound.height()/2:bound.width()/2;
            if(paint.getStyle()!= Paint.Style.FILL){
                if(0>=paint.getStrokeWidth())paint.setStrokeWidth(radius*widthRatio);
                if(paint.getStrokeWidth()>radius/2)paint.setStrokeWidth(radius/2);
                radius -= 1.5*paint.getStrokeWidth();
            }
            psDatas.init(new Point(bound.centerX(), bound.centerY()), radius);
            //}

            Point[] points = new Point[]{
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(-18))*psDatas.starBigSide/2, psDatas.first.y+Math.cos(Math.toRadians(-18))*psDatas.starBigSide/2),
                    //new Point(psDatas.first.x, psDatas.first.y),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(-18))*psDatas.starBigSide, psDatas.first.y+Math.cos(Math.toRadians(-18))*psDatas.starBigSide),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(-54))*psDatas.bigSide, psDatas.first.y+Math.cos(Math.toRadians(-54))*psDatas.bigSide),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(-18))*(psDatas.starBigSide+psDatas.littleSide), psDatas.first.y+Math.cos(Math.toRadians(-18))*(psDatas.starBigSide+psDatas.littleSide)),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(-18))*(2*psDatas.starBigSide+psDatas.littleSide), psDatas.first.y+Math.cos(Math.toRadians(-18))*(2*psDatas.starBigSide+psDatas.littleSide)),
                    //
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(0))*(psDatas.bigRadius+psDatas.littleRadius), psDatas.first.y+Math.cos(Math.toRadians(0))*(psDatas.bigRadius+psDatas.littleRadius)),
                    //
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(18))*(2*psDatas.starBigSide+psDatas.littleSide), psDatas.first.y+Math.cos(Math.toRadians(18))*(2*psDatas.starBigSide+psDatas.littleSide)),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(18))*(psDatas.starBigSide+psDatas.littleSide), psDatas.first.y+Math.cos(Math.toRadians(18))*(psDatas.starBigSide+psDatas.littleSide)),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(54))*psDatas.bigSide, psDatas.first.y+Math.cos(Math.toRadians(54))*psDatas.bigSide),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(18))*psDatas.starBigSide, psDatas.first.y+Math.cos(Math.toRadians(18))*psDatas.starBigSide),
                    new Point(psDatas.first.x, psDatas.first.y),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(-18))*psDatas.starBigSide/2, psDatas.first.y+Math.cos(Math.toRadians(-18))*psDatas.starBigSide/2)};

            return points;
        }

        private class PentagonStarDatas{
            public Point first;
            public float bigRadius, bigSide, littleRadius, littleSide, starBigSide, starHeight;

            public void init(Point central, float input_bigRadius){
                bigRadius = input_bigRadius;
                bigSide = (float) (2*bigRadius*Math.sin(Math.toRadians(36)));
                littleRadius = (float) (bigRadius*Math.cos(Math.toRadians(36)) - bigSide/(2*Math.tan(Math.toRadians(54))));
                littleSide = (float) (2*littleRadius*Math.sin(Math.toRadians(36)));
                starBigSide = (float) (2*littleRadius*Math.cos(Math.toRadians(18)));
                //
                starHeight = (float) Math.sqrt( Math.pow( (starBigSide*2+littleSide), 2) - Math.pow( (bigSide/2), 2) );
                first = new Point(central.x, central.y-starHeight/2+((paint.getStyle()!= Paint.Style.FILL)?paint.getStrokeWidth()/8:0));
            }
        }
    }*/

    public void getAllStars(Canvas cvs, RectF bounds){
        if(cvs==null || bounds==null)return;
        //
        maxWidth = bounds.width()/nbrStarToShow;
        padding = ratioPadding*maxWidth;
        if(starModel.paint==null || true){
            starModel.paint = new Paint();
            if(usingColorMoving/*imgPlayer!=null && imgPlayer.isPlaying*/){
                imgPlayer.bound = bounds;
                imgPlayer.onInit();
                //imgPlayer.setBound(bounds);
                imgPlayer.update();
                Shader fillShader = new BitmapShader((imgPlayer.cBmp!=null?imgPlayer.cBmp:((BitmapDrawable)main.getDrawable()).getBitmap()),
                        Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                starModel.paint.setShader(fillShader);
            }else starModel.paint.setColor(DEFAULT_STAR_BORDER_COLOR);
            starModel.paint.setStyle(Paint.Style.FILL);
        }

        starBmpList.clear();
        float starWidth = maxWidth-2*padding;
        //bounds = new RectF(bounds.left, bounds.centerY()-starWidth/2, bounds.right, bounds.centerY()+starWidth/2);

        for(int i=0;i<nbrStarSelected;i++){
            starModel.bound = new RectF(bounds.left+padding+i*maxWidth, bounds.top,
                    bounds.left+padding+i*maxWidth + starWidth, bounds.bottom);
            starBmpList.add(starModel.getStar());
        }
        if(nbrStarToShow!=0){
            int i=0;
            if(starModel.bound==null) starModel.bound = new RectF(bounds.left+padding+i*maxWidth, bounds.top,
                    bounds.left+padding+i*maxWidth + starWidth, bounds.bottom);
            starModel.paint.setShader(null);
            starModel.paint.setStyle(Paint.Style.STROKE);
            starModel.paint.setColor(DEFAULT_STAR_BORDER_COLOR);
            starBmpList.add(starModel.getStar());
        }

        if(starBmpList.size()>0) {
            for(int i=0;i<nbrStarToShow;i++){
                //save location
                if(starsBounds.size()<nbrStarToShow){
                    float left = bounds.left + padding + i * maxWidth,
                            top = bounds.top + bounds.height() / 2 - starModel.bound.height() / 2;
                    starsBounds.add(new RectF(left-padding, top-padding,
                            left+starModel.bound.width()+padding, top+starModel.bound.height()+padding));
                }
                //
                if((i==nbrStarSelected-1) && (0<evolRatio && evolRatio<1)) {
                    Paint paintAlpha = new Paint();
                    paintAlpha.setAlpha(70);
                    cvs.drawBitmap(starBmpList.get(nbrStarSelected), bounds.left + padding + i * maxWidth,
                            bounds.top + bounds.height() / 2 - starModel.bound.height() / 2, paintAlpha);
                    //
                    Bitmap partStar = getRatioBmp(starBmpList.get(i), evolRatio);
                    if(partStar!=null)
                        cvs.drawBitmap(partStar, bounds.left + padding + i * maxWidth,
                                bounds.top + bounds.height() / 2 - starModel.bound.height() / 2, null);
                }else if(starModel.bound!=null)
                    cvs.drawBitmap(starBmpList.get(i<nbrStarSelected?i:nbrStarSelected), bounds.left+padding+i*maxWidth,
                            bounds.top + bounds.height() / 2 - starModel.bound.height() / 2, null);
                if(i+1==touchStarDatas.onTouchStar){
                    touchStarDatas.start();
                    touchPaint.setAlpha(touchStarDatas.c_alpha);
                    float radius = (starsBounds.get(i).width()+padding*2)/2>bounds.height()/2?
                            bounds.height()/2:(starsBounds.get(i).width()+padding*2)/2;
                    cvs.drawCircle(starsBounds.get(i).centerX(), starsBounds.get(i).centerY(),
                            radius, touchPaint);
                }
            }
        }
    }

    public static class Star{
        public Boolean isStarBorder = true;
        public Paint paint = null;
        public RectF bound = null;
        //
        private PentagonStarDatas psDatas = null;
        private float widthRatio = 10.0f/75.0f;
        private Point[] points = null;

        public Bitmap getStar(){
            if(bound==null || paint==null || 0>=bound.width() || 0>=bound.height())return null;

            Bitmap star = Bitmap.createBitmap((int) bound.width(),(int) bound.height(), Bitmap.Config.ARGB_8888);
            Canvas starCvs = new Canvas(star);

            /*if(points==null)*/points = getPointsForStar(bound);

            starCvs.translate(-bound.left, -bound.top);


            /*Paint p = new Paint();
            p.setColor(Color.RED);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(5);
            starCvs.drawRect(bound, p);*/


            paint.setShadowLayer(1,1, 1, Color.BLACK);
            paint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.INNER));


            drawPolygon(starCvs, points, paint);

            return star;
        }

        private Point[] getPointsForStar(RectF bound) {
            /*float r = Math.min(bound.width(), bound.height())/2;
            bound = new RectF(bound.centerX()-r, bound.centerY()-r, bound.centerX()+4, bound.centerY()+r);*/

            //if(psDatas==null){
            psDatas = new PentagonStarDatas();
            float radius = bound.width()>bound.height()?bound.height()/2:bound.width()/2;
            if(paint.getStyle()!= Paint.Style.FILL){
                if(0>=paint.getStrokeWidth())paint.setStrokeWidth(radius*widthRatio);
                if(paint.getStrokeWidth()>radius/2)paint.setStrokeWidth(radius/2);
                radius -= 1.5*paint.getStrokeWidth();
            }
            psDatas.init(new Point(bound.centerX(), bound.centerY()), radius);
            //}

            /*psDatas.first
             * n'est pas uiliser en premier, car il y-aura un problem de jointure.
             * du coup on utilisera le point millieu entre le vrai first et le vrai second*/
            Point[] points = new Point[]{
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(-18))*psDatas.starBigSide/2, psDatas.first.y+Math.cos(Math.toRadians(-18))*psDatas.starBigSide/2),
                    //new Point(psDatas.first.x, psDatas.first.y),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(-18))*psDatas.starBigSide, psDatas.first.y+Math.cos(Math.toRadians(-18))*psDatas.starBigSide),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(-54))*psDatas.bigSide, psDatas.first.y+Math.cos(Math.toRadians(-54))*psDatas.bigSide),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(-18))*(psDatas.starBigSide+psDatas.littleSide), psDatas.first.y+Math.cos(Math.toRadians(-18))*(psDatas.starBigSide+psDatas.littleSide)),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(-18))*(2*psDatas.starBigSide+psDatas.littleSide), psDatas.first.y+Math.cos(Math.toRadians(-18))*(2*psDatas.starBigSide+psDatas.littleSide)),
                    //
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(0))*(psDatas.bigRadius+psDatas.littleRadius), psDatas.first.y+Math.cos(Math.toRadians(0))*(psDatas.bigRadius+psDatas.littleRadius)),
                    //
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(18))*(2*psDatas.starBigSide+psDatas.littleSide), psDatas.first.y+Math.cos(Math.toRadians(18))*(2*psDatas.starBigSide+psDatas.littleSide)),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(18))*(psDatas.starBigSide+psDatas.littleSide), psDatas.first.y+Math.cos(Math.toRadians(18))*(psDatas.starBigSide+psDatas.littleSide)),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(54))*psDatas.bigSide, psDatas.first.y+Math.cos(Math.toRadians(54))*psDatas.bigSide),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(18))*psDatas.starBigSide, psDatas.first.y+Math.cos(Math.toRadians(18))*psDatas.starBigSide),
                    new Point(psDatas.first.x, psDatas.first.y),
                    new Point(psDatas.first.x+Math.sin(Math.toRadians(-18))*psDatas.starBigSide/2, psDatas.first.y+Math.cos(Math.toRadians(-18))*psDatas.starBigSide/2)};

            return points;
        }

        private class PentagonStarDatas{
            public Point first;
            public float bigRadius, bigSide, littleRadius, littleSide, starBigSide, starHeight;

            public void init(Point central, float input_bigRadius){
                bigRadius = input_bigRadius;
                bigSide = (float) (2*bigRadius*Math.sin(Math.toRadians(36)));
                littleRadius = (float) (bigRadius*Math.cos(Math.toRadians(36)) - bigSide/(2*Math.tan(Math.toRadians(54))));
                littleSide = (float) (2*littleRadius*Math.sin(Math.toRadians(36)));
                starBigSide = (float) (2*littleRadius*Math.cos(Math.toRadians(18)));
                //
                starHeight = (float) Math.sqrt( Math.pow( (starBigSide*2+littleSide), 2) - Math.pow( (bigSide/2), 2) );
                first = new Point(central.x, central.y-starHeight/2+((paint.getStyle()!= Paint.Style.FILL)?paint.getStrokeWidth()/8:0));
            }
        }
    }

}
