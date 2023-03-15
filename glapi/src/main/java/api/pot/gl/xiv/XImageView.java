package api.pot.gl.xiv;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import api.pot.gl.R;
import api.pot.gl.geo.TextPainter;
import api.pot.gl.tools.Global;
import api.pot.gl.tools.XImage;
import api.pot.gl.xiv.tools.Descriptor;
import api.pot.gl.xiv.tools.DetProgress;
import api.pot.gl.xiv.tools.Forgrounder;
import api.pot.gl.xiv.tools.ImgPainter;
import api.pot.gl.xiv.tools.IndetProgress;
import api.pot.gl.xiv.tools.PaintBundle;
import api.pot.gl.xiv.tools.Pulsation;
import api.pot.gl.xiv.tools.XivListener;
import api.pot.gl.xiv.tools.morphism.Transformer;
import api.pot.gl.xiv.tools.Transitor;
import api.pot.gl.xiv.tools.stars.StarSystem;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("AppCompatCustomView")
@SuppressWarnings("UnusedDeclaration")
public class XImageView extends ImageView {

    public static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    public static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    public static final int COLORDRAWABLE_DIMENSION = 2;

    public static final int DEFAULT_BORDER_WIDTH = 0;
    public static final int DEFAULT_BORDER_COLOR = Global.MAIN_GES_COLOR;
    public static final int DEFAULT_CIRCLE_BACKGROUND_COLOR = Color.TRANSPARENT;
    public static final boolean DEFAULT_BORDER_OVERLAY = false;

    private RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();
    private final Paint mCircleBackgroundPaint = new Paint();







    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxoliverxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private final Paint mBorderPaintSpace = new Paint();

    public static final int DEFAULT_INTERNAL_BORDER_WIDTH = 0;
    public static final int DEFAULT_INTERNAL_BORDER_COLOR = Color.TRANSPARENT;

    private int mInternalBorderWidth = DEFAULT_INTERNAL_BORDER_WIDTH;
    private int mInternalBorderColor = DEFAULT_INTERNAL_BORDER_COLOR;

    public static final int DEFAULT_MAX_BORDER_WIDTH = -1;
    private int mMaxBorderWidth = DEFAULT_MAX_BORDER_WIDTH;


    public static final int DEFAULT_MAX_BORDER_RADIUS = 0;
    private float mMaxBorderRadius = DEFAULT_MAX_BORDER_RADIUS;

    public static final int DEFAULT_BORDER_START_ANGLE = 0;
    public static final int DEFAULT_BORDER_SWEEP_ANGLE = 0;
    public static final Boolean DEFAULT_BORDER_USE_CENTER = false;
    //private static final int DEFAULT_BORDER_COLOR = Color.parseColor("#4b0082");
    List<Arc> myArcs = new ArrayList<Arc>();

    public static final int BORDER_TYPE_NORMAL = 0;
    public static final int BORDER_TYPE_DET_PROGRESS = 1;
    public static final int BORDER_TYPE_UNDET_PROGRESS = 2;
    private int mBorderType = BORDER_TYPE_NORMAL;


    private float mCircleBackgroundRadius = DEFAULT_MAX_BORDER_RADIUS;
    private Boolean isCircleBackgroundRadiusAuto = true;

    public static final int BACK_TYPE_BACKGROUNG = 0;
    public static final int BACK_TYPE_ECHO = 1;
    public static final int BACK_TYPE_SWEEPER = 2;
    private int mBackType = BACK_TYPE_BACKGROUNG;

    private RectF arcBorderRect = new RectF();

    public static final int GRAPH_TYPE_PICTURE = 0;
    public static final int GRAPH_TYPE_LINE = 1;
    public static final int GRAPH_TYPE_TEXT = 2;
    public static final int GRAPH_TYPE_TEXT_BG_CIRCLE = 3;
    public static final int GRAPH_TYPE_GEO_FORM = 4;
    private int graphType = GRAPH_TYPE_PICTURE;

    private int resId=0;
    private String imageAddress = "";

    private Bitmap mainBmp;
    private List<AroundView> aroundViews = new ArrayList<AroundView>();

    private Boolean isMirrorEffect = false;

    private NotRoundForm notRoundForm;

    private Transitor transitor;

    private int alpha = 255;


    //*******************Anim**************************
    private IndetProgress indetProgress;
    private DetProgress detProgress;
    private Pulsation pulsation;
    private Transformer transformer;
    private Descriptor descriptor;
    private Forgrounder forgrounder;




    private boolean useGradientColorsForBorder = false;
    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private List<Integer> mBorderColorList = new ArrayList<>();
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    private int mCircleBackgroundColor = DEFAULT_CIRCLE_BACKGROUND_COLOR;
    private int mBgAlpha = 255;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mDrawableRadius;
    private float mBorderRadius;

    private ColorFilter mColorFilter;

    private boolean mReady;
    private boolean mSetupPending;
    private boolean mBorderOverlay;


    private boolean mForegrounderEnabled = true;





    ///----------------------------------XImageView+++++++++++++++++++++++++++++++++++
    private boolean mBoundWithBitmapDimens = false;//l'image n'est plus en format carré, mais correspond à la taille normal du bitmap
    private boolean mBoundWithViewDimens = false;//l'image n'est plus en format carré, mais correspond à la taille exact de la vue
    private boolean mEnabledCircularTransformation = true;

    private int shadowRad=10, shadowDx=0, shadowDy=10, shadowColor= Color.BLACK;

    private XivListener xivListener;

    ///----------------------------------XImageView+++++++++++++++++++++++++++++++++++





    private boolean must_update = false;
    private Handler handler_for_invalidate = null;
    private Runnable runnable_for_invalidate = null;

    public void invalidater(){
        if(must_update) return;
        must_update = true;
    }

    public void initInvalidater(){
        if(runnable_for_invalidate!=null && handler_for_invalidate!=null) return;
        //
        handler_for_invalidate = new Handler();
        runnable_for_invalidate = new Runnable() {
            @Override
            public void run() {
                View parent = (View)getParent();
                RectF parentBound = null;
                if(parent!=null) {
                    int[] location = new int[2];
                    parent.getLocationOnScreen(location);
                    parentBound = new RectF(location[0], location[1], location[0] + parent.getWidth(), location[1] + parent.getHeight());
                }
                //
                if(must_update && mReady && Global.isViewIntoContainer(XImageView.this, parentBound)) {
                    invalidate();
                    must_update = false;
                }
                //
                handler_for_invalidate.postDelayed(runnable_for_invalidate, Global.invalidate_time);
            }
        };
        handler_for_invalidate.postDelayed(runnable_for_invalidate, Global.invalidate_time);
    }

    public XImageView(Context context) {
        super(context);

        initInvalidater();

        init();
    }

    public XImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initInvalidater();

        resId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "src", 0);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XImageView, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.XImageView_xiv_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.XImageView_xiv_border_color, DEFAULT_BORDER_COLOR);
        mBorderOverlay = a.getBoolean(R.styleable.XImageView_xiv_border_overlay, DEFAULT_BORDER_OVERLAY);
        mCircleBackgroundColor = a.getColor(R.styleable.XImageView_xiv_circle_background_color, DEFAULT_CIRCLE_BACKGROUND_COLOR);

        mInternalBorderWidth = a.getDimensionPixelSize(R.styleable.XImageView_xiv_internal_border_width, DEFAULT_INTERNAL_BORDER_WIDTH)/2;
        mInternalBorderColor = a.getColor(R.styleable.XImageView_xiv_internal_border_color, DEFAULT_INTERNAL_BORDER_COLOR);
        mMaxBorderWidth = a.getDimensionPixelSize(R.styleable.XImageView_xiv_max_border_width, DEFAULT_MAX_BORDER_WIDTH);

        mBorderType = a.getInt(R.styleable.XImageView_xiv_border_type, BORDER_TYPE_NORMAL);
        mInternalBorderWidth *= mBorderType!=BORDER_TYPE_NORMAL?2:1;
        myArcs.add(new Arc(a.getFloat(R.styleable.XImageView_xiv_border_startAngle, DEFAULT_BORDER_START_ANGLE),
                a.getFloat(R.styleable.XImageView_xiv_border_sweepAngle, DEFAULT_BORDER_SWEEP_ANGLE),
                a.getBoolean(R.styleable.XImageView_xiv_border_use_center, DEFAULT_BORDER_USE_CENTER),
                new PaintBundle(a.getInt(R.styleable.XImageView_xiv_border_color, DEFAULT_BORDER_COLOR), null)));

        graphType = a.getInt(R.styleable.XImageView_xiv_graph_type, GRAPH_TYPE_PICTURE);

        if(isCircleBackgroundRadiusAuto)
            mCircleBackgroundRadius = (mMaxBorderRadius>DEFAULT_MAX_BORDER_RADIUS?mMaxBorderRadius:(mBorderWidth>0?mBorderRadius:mDrawableRadius));

        setAlpha(255*a.getFloat(R.styleable.XImageView_xiv_alpha, 1)%256);

        mBgAlpha = (int) (255*a.getFloat(R.styleable.XImageView_xiv_bg_alpha, 1)%256);

        mEnabledCircularTransformation = a.getBoolean(R.styleable.XImageView_xiv_enabled_circular_transformation, true);
        mBoundWithBitmapDimens = a.getBoolean(R.styleable.XImageView_xiv_bound_with_bitmap_dimens, false);
        mBoundWithViewDimens = a.getBoolean(R.styleable.XImageView_xiv_bound_with_view_dimens, false);

        mForegrounderEnabled = a.getBoolean(R.styleable.XImageView_xiv_foregrounder_enabled, mForegrounderEnabled);

        shadowRad = a.getDimensionPixelSize(R.styleable.XImageView_xiv_shadow_rad, 0);
        shadowDx = a.getDimensionPixelSize(R.styleable.XImageView_xiv_shadow_dx, 0);
        shadowDy = a.getDimensionPixelSize(R.styleable.XImageView_xiv_shadow_dy, 0);
        shadowColor = a.getColor(R.styleable.XImageView_xiv_shadow_color, Color.BLACK);


        enabled = a.getBoolean(R.styleable.XImageView_xiv_enabled, true);
        setEnabled(a.getBoolean(R.styleable.XImageView_xiv_enabled, true));


        Global.useContext(context);

        a.recycle();

        init();
    }
    private boolean enabled = true;

    private void init() {
        super.setScaleType(SCALE_TYPE);
        mReady = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new OutlineProvider());
        }

        if(getBitmapFromDrawable(getDrawable())==null){
            setImageDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        }

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
        //
        mBorderColorList.add(Global.FIRST_GES_GRADIENT_COLOR);
        mBorderColorList.add(Global.SECOND_GES_GRADIENT_COLOR);
        mBorderColorList.add(Global.THIRD_GES_GRADIENT_COLOR);
        mBorderColorList.add(Global.FIRST_GES_GRADIENT_COLOR);
        //
        forgrounder = new Forgrounder(this, mForegrounderEnabled);
        descriptor = new Descriptor(this);
        transformer = new Transformer(this);
        notRoundForm = new NotRoundForm();
        starSystem = new StarSystem(this);
        //
        clearAroundViews();
        for(int i=0;i<8;i++)
            addAroundView(i*45);
        //
        indetProgress = new IndetProgress(this);
        detProgress = new DetProgress(this);
        pulsation = new Pulsation(this);
        //
        transitor = new Transitor(this);
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //transitor.update();

        if (mBitmap == null) {
            initializeBitmap();
            return;
        }

        //draw back shadow

        //draw bg
        if (mCircleBackgroundColor != Color.TRANSPARENT && mEnabledCircularTransformation) {
            Log.d("XL", getHeight()+" http://"+mCircleBackgroundRadius);
            canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mCircleBackgroundRadius, mCircleBackgroundPaint);
        }else if(mCircleBackgroundColor != Color.TRANSPARENT && !mEnabledCircularTransformation){
            canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), Math.max(getWidth(), getHeight()),
                    mCircleBackgroundPaint);
        }

        //draw content
        if(graphType==GRAPH_TYPE_PICTURE) {
            //draw bitmap with shadow
            ///shadow
            Paint p = new Paint();
            if(shadowRad>0){
                p.setStyle(Paint.Style.STROKE);
                p.setColor(shadowColor);
                p.setStrokeWidth(Math.max(Math.max(shadowDx, shadowDy), shadowRad));
                p.setShadowLayer(shadowRad,shadowDx, shadowDy, shadowColor);
                p.setMaskFilter(new BlurMaskFilter(Math.max(Math.max(shadowDx, shadowDy), shadowRad), BlurMaskFilter.Blur.NORMAL));
            }
            //
            mBitmapPaint.setAlpha((int) getAlpha());
            //
            if(!mEnabledCircularTransformation){
                if(notRoundForm.rx==-1) notRoundForm.rx = mDrawableRect.width()*notRoundForm.radiusRatio;
                if(notRoundForm.ry==-1) notRoundForm.ry = mDrawableRect.height()*notRoundForm.radiusRatio;
                if(notRoundForm.notRoundForm_drawablePath==null)
                    notRoundForm.notRoundForm_drawablePath =
                            ImgPainter.roundedRectPath(mDrawableRect, notRoundForm.rx, notRoundForm.ry,
                                    notRoundForm.isTopLeftRound, notRoundForm.isTopRightRound, notRoundForm.isBottomRightRound, notRoundForm.isBottomLeftRound);
            }
            ///shadow
            if(shadowRad>0)
                onDrawBitmap(canvas, p, mDrawableRadius, mDrawableRect, notRoundForm.notRoundForm_drawablePath);
            onDrawBitmap(canvas, mBitmapPaint, mDrawableRadius, mDrawableRect, notRoundForm.notRoundForm_drawablePath);
            //
            if(transitor.transition) onDrawBitmap(canvas, transitor.update(), transitor.mDrawableRadius,
                    transitor.mDrawableRect, transitor.notRoundForm_drawablePath);
            //
            if(starSystem.visibility != StarSystem.STAR_SYSTEM_GONE){
                starSystem.update(canvas, new RectF(0, 0, canvas.getWidth(), canvas.getHeight()));
            }
        }
        //
        if(xivListener!=null) xivListener.onDrawContent(canvas);

        //draw fg
        if(mMaxBorderRadius > DEFAULT_MAX_BORDER_RADIUS){
            Paint mMaxBorderPaint = new Paint();
            mMaxBorderPaint.setStyle(Paint.Style.STROKE);
            mMaxBorderPaint.setStrokeWidth(mMaxBorderWidth);
            mMaxBorderPaint.setColor(Color.TRANSPARENT);//Color.argb(80, 0, 0, 255)

            canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mMaxBorderRadius, mMaxBorderPaint);
        }
        if (mBorderWidth > 0) {
            if(mBorderType==BORDER_TYPE_NORMAL){
                if(mEnabledCircularTransformation)
                    canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, mBorderPaint);
                else {
                    if(notRoundForm.border_rx==-1) notRoundForm.border_rx = mBorderRect.width()*notRoundForm.radiusRatio;
                    if(notRoundForm.border_ry==-1) notRoundForm.border_ry = mBorderRect.height()*notRoundForm.radiusRatio;
                    if(notRoundForm.notRoundForm_borderPath==null) notRoundForm.notRoundForm_borderPath =
                            ImgPainter.roundedRectPath(mBorderRect, notRoundForm.border_rx, notRoundForm.border_ry,
                                    notRoundForm.isTopLeftRound, notRoundForm.isTopRightRound, notRoundForm.isBottomRightRound, notRoundForm.isBottomLeftRound);
                    //
                    if(notRoundForm.isRect()) canvas.drawRect(mBorderRect, mBorderPaint);
                    else canvas.drawPath(notRoundForm.notRoundForm_borderPath, mBorderPaint);
                }
            }else {
                arcBorderRect = new RectF();
                arcBorderRect.set(mBorderRect);
                arcBorderRect.left -= mBorderWidth/2;
                arcBorderRect.top -= mBorderWidth/2;
                arcBorderRect.right += mBorderWidth/2;
                arcBorderRect.bottom += mBorderWidth/2;

                if(mBorderType==BORDER_TYPE_DET_PROGRESS && myArcs.size()>0){
                    Paint nowPaint = new Paint();
                    nowPaint.set(mBorderPaint);
                    //
                    if (myArcs.get(0).paint.color != -1)
                        nowPaint.setColor(myArcs.get(0).paint.color);
                    if (myArcs.get(0).paint.shader != null)
                        nowPaint.setShader(myArcs.get(0).paint.shader);
                    //
                    if(mEnabledCircularTransformation)
                        canvas.drawArc(arcBorderRect, myArcs.get(0).startAngle, myArcs.get(0).sweepAngle, myArcs.get(0).useCenter, nowPaint);
                    else {
                        if(notRoundForm.arc_rx==-1) notRoundForm.arc_rx = arcBorderRect.width()*notRoundForm.radiusRatio;
                        if(notRoundForm.arc_ry==-1) notRoundForm.arc_ry = arcBorderRect.height()*notRoundForm.radiusRatio;
                        if(notRoundForm.notRoundForm_arcPathPoints==null) notRoundForm.notRoundForm_arcPathPoints =
                                ImgPainter.getPoints(ImgPainter.listRoundRectPaths(arcBorderRect, notRoundForm.arc_rx, notRoundForm.arc_ry,
                                        notRoundForm.isTopLeftRound, notRoundForm.isTopRightRound,
                                        notRoundForm.isBottomRightRound, notRoundForm.isBottomLeftRound));
                        if(notRoundForm.notRoundForm_arcsPaths==null) {
                            notRoundForm.notRoundForm_arcsPaths = new ArrayList<>();
                            for(Arc elt : myArcs) notRoundForm.notRoundForm_arcsPaths.add(null);
                        }
                        if(notRoundForm.notRoundForm_arcsPaths.get(0)==null)
                            notRoundForm.notRoundForm_arcsPaths.set(0, ImgPainter.arcPath(myArcs.get(0),
                                    notRoundForm.notRoundForm_arcPathPoints, arcBorderRect.centerX(), arcBorderRect.centerY()) );
                        //
                        if(notRoundForm.notRoundForm_arcsPaths.get(0)!=null) canvas.drawPath(notRoundForm.notRoundForm_arcsPaths.get(0), nowPaint);
                    }
                }else if(mBorderType==BORDER_TYPE_UNDET_PROGRESS && myArcs.size()>0){
                    int i=0;
                    for(Arc arc : myArcs){
                        Paint nowPaint = new Paint();
                        nowPaint.set(mBorderPaint);
                        if(arc.paint.color!=-1)nowPaint.setColor(arc.paint.color);
                        if(arc.paint.shader!=null)nowPaint.setShader(arc.paint.shader);
                        //
                        if(mEnabledCircularTransformation)
                            canvas.drawArc(arcBorderRect, arc.startAngle, arc.sweepAngle, arc.useCenter, nowPaint);
                        else {
                            if(notRoundForm.arc_rx==-1) notRoundForm.arc_rx = arcBorderRect.width()*notRoundForm.radiusRatio;
                            if(notRoundForm.arc_ry==-1) notRoundForm.arc_ry = arcBorderRect.height()*notRoundForm.radiusRatio;
                            if(notRoundForm.notRoundForm_arcPathPoints==null) notRoundForm.notRoundForm_arcPathPoints =
                                    ImgPainter.getPoints(ImgPainter.listRoundRectPaths(arcBorderRect, notRoundForm.arc_rx, notRoundForm.arc_ry,
                                            notRoundForm.isTopLeftRound, notRoundForm.isTopRightRound,
                                            notRoundForm.isBottomRightRound, notRoundForm.isBottomLeftRound));
                            if(notRoundForm.notRoundForm_arcsPaths==null) {
                                notRoundForm.notRoundForm_arcsPaths = new ArrayList<>();
                                for(int j=0;j<myArcs.size();j++) notRoundForm.notRoundForm_arcsPaths.add(null);
                            }
                            if(notRoundForm.notRoundForm_arcsPaths.size()<=i) {
                                for(int k=notRoundForm.notRoundForm_arcsPaths.size();k<=i;k++) notRoundForm.notRoundForm_arcsPaths.add(null);
                            }
                            if(notRoundForm.notRoundForm_arcsPaths.size()>i && notRoundForm.notRoundForm_arcsPaths.get(i)==null)
                                notRoundForm.notRoundForm_arcsPaths.set(i, ImgPainter.arcPath(arc,
                                        notRoundForm.notRoundForm_arcPathPoints, arcBorderRect.centerX(), arcBorderRect.centerY()) );
                            //
                            if(notRoundForm.notRoundForm_arcsPaths.size()>i && notRoundForm.notRoundForm_arcsPaths.get(i)!=null) canvas.drawPath(notRoundForm.notRoundForm_arcsPaths.get(i), nowPaint);
                        }
                        //
                        i++;
                    }
                }
            }
        }

        //draw AroundViews
        if(aroundViews!=null && aroundViews.size()!=0){
            for(AroundView aroundView : aroundViews)
                if(aroundView.isVisible){
                    aroundView.init();
                    aroundView.onDraw(canvas);
                }
        }

        //draw description
        if(descriptor.isVisible){
            if(descriptor.isTextDatasReady);
                //TextPainter.drawTextCentered(descriptor.text, null, canvas, descriptor.textBound, true);
        }
        //draw forground
        forgrounder.update(canvas);
    }
/**
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //transitor.update();

        if (mBitmap == null) {
            initializeBitmap();
            return;
        }

        //draw back shadow

        //draw bg
        if (mCircleBackgroundColor != Color.TRANSPARENT && mEnabledCircularTransformation) {
            Log.d("XL", getHeight()+" http://"+mCircleBackgroundRadius);
            canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mCircleBackgroundRadius, mCircleBackgroundPaint);
        }else if(mCircleBackgroundColor != Color.TRANSPARENT && !mEnabledCircularTransformation){
            canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), Math.max(getWidth(), getHeight()),
                    mCircleBackgroundPaint);
        }

        //draw content
        if(graphType==GRAPH_TYPE_PICTURE) {
            //draw bitmap with shadow
            ///shadow
            Paint p = new Paint();
            if(shadowRad>0){
                p.setStyle(Paint.Style.STROKE);
                p.setColor(shadowColor);
                p.setStrokeWidth(Math.max(Math.max(shadowDx, shadowDy), shadowRad));
                p.setShadowLayer(shadowRad,shadowDx, shadowDy, shadowColor);
                p.setMaskFilter(new BlurMaskFilter(Math.max(Math.max(shadowDx, shadowDy), shadowRad), BlurMaskFilter.Blur.NORMAL));
            }
            //
            mBitmapPaint.setAlpha((int) getAlpha());
            //
            if(!mEnabledCircularTransformation){
                if(notRoundForm.rx==-1) notRoundForm.rx = mDrawableRect.width()*notRoundForm.radiusRatio;
                if(notRoundForm.ry==-1) notRoundForm.ry = mDrawableRect.height()*notRoundForm.radiusRatio;
                if(notRoundForm.notRoundForm_drawablePath==null)
                    notRoundForm.notRoundForm_drawablePath =
                            ImgPainter.roundedRectPath(mDrawableRect, notRoundForm.rx, notRoundForm.ry,
                                    notRoundForm.isTopLeftRound, notRoundForm.isTopRightRound, notRoundForm.isBottomRightRound, notRoundForm.isBottomLeftRound);
            }
            ///shadow
            if(shadowRad>0)
                onDrawBitmap(canvas, p, mDrawableRadius, mDrawableRect, notRoundForm.notRoundForm_drawablePath);
            onDrawBitmap(canvas, mBitmapPaint, mDrawableRadius, mDrawableRect, notRoundForm.notRoundForm_drawablePath);
            //
            if(transitor.transition) onDrawBitmap(canvas, transitor.update(), transitor.mDrawableRadius,
                    transitor.mDrawableRect, transitor.notRoundForm_drawablePath);
            //
            if(starSystem.visibility != StarSystem.STAR_SYSTEM_GONE){
                starSystem.update(canvas, new RectF(0, 0, canvas.getWidth(), canvas.getHeight()));
            }
        }
        //
        if(xivListener!=null) xivListener.onDrawContent(canvas);

        //draw fg
        if(mMaxBorderRadius > DEFAULT_MAX_BORDER_RADIUS){
            Paint mMaxBorderPaint = new Paint();
            mMaxBorderPaint.setStyle(Paint.Style.STROKE);
            mMaxBorderPaint.setStrokeWidth(mMaxBorderWidth);
            mMaxBorderPaint.setColor(Color.TRANSPARENT);//Color.argb(80, 0, 0, 255)

            canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mMaxBorderRadius, mMaxBorderPaint);
        }
        if (mBorderWidth > 0) {
            if(mBorderType==BORDER_TYPE_NORMAL){
                if(mEnabledCircularTransformation)
                    canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), mBorderRadius, mBorderPaint);
                else {
                    if(notRoundForm.border_rx==-1) notRoundForm.border_rx = mBorderRect.width()*notRoundForm.radiusRatio;
                    if(notRoundForm.border_ry==-1) notRoundForm.border_ry = mBorderRect.height()*notRoundForm.radiusRatio;
                    if(notRoundForm.notRoundForm_borderPath==null) notRoundForm.notRoundForm_borderPath =
                            ImgPainter.roundedRectPath(mBorderRect, notRoundForm.border_rx, notRoundForm.border_ry,
                                    notRoundForm.isTopLeftRound, notRoundForm.isTopRightRound, notRoundForm.isBottomRightRound, notRoundForm.isBottomLeftRound);
                    //
                    if(notRoundForm.isRect()) canvas.drawRect(mBorderRect, mBorderPaint);
                    else canvas.drawPath(notRoundForm.notRoundForm_borderPath, mBorderPaint);
                }
            }else {
                arcBorderRect = new RectF();
                arcBorderRect.set(mBorderRect);
                arcBorderRect.left -= mBorderWidth/2;
                arcBorderRect.top -= mBorderWidth/2;
                arcBorderRect.right += mBorderWidth/2;
                arcBorderRect.bottom += mBorderWidth/2;

                if(mBorderType==BORDER_TYPE_DET_PROGRESS && myArcs.size()>0){
                    Paint nowPaint = new Paint();
                    nowPaint.set(mBorderPaint);
                    //
                    if (myArcs.get(0).paint.color != -1)
                        nowPaint.setColor(myArcs.get(0).paint.color);
                    if (myArcs.get(0).paint.shader != null)
                        nowPaint.setShader(myArcs.get(0).paint.shader);
                    //
                    if(mEnabledCircularTransformation)
                        canvas.drawArc(arcBorderRect, myArcs.get(0).startAngle, myArcs.get(0).sweepAngle, myArcs.get(0).useCenter, nowPaint);
                    else {
                        if(notRoundForm.arc_rx==-1) notRoundForm.arc_rx = arcBorderRect.width()*notRoundForm.radiusRatio;
                        if(notRoundForm.arc_ry==-1) notRoundForm.arc_ry = arcBorderRect.height()*notRoundForm.radiusRatio;
                        if(notRoundForm.notRoundForm_arcPathPoints==null) notRoundForm.notRoundForm_arcPathPoints =
                                ImgPainter.getPoints(ImgPainter.listRoundRectPaths(arcBorderRect, notRoundForm.arc_rx, notRoundForm.arc_ry,
                                        notRoundForm.isTopLeftRound, notRoundForm.isTopRightRound,
                                        notRoundForm.isBottomRightRound, notRoundForm.isBottomLeftRound));
                        if(notRoundForm.notRoundForm_arcsPaths==null) {
                            notRoundForm.notRoundForm_arcsPaths = new ArrayList<>();
                            for(Arc elt : myArcs) notRoundForm.notRoundForm_arcsPaths.add(null);
                        }
                        if(notRoundForm.notRoundForm_arcsPaths.get(0)==null)
                            notRoundForm.notRoundForm_arcsPaths.set(0, ImgPainter.arcPath(myArcs.get(0),
                                    notRoundForm.notRoundForm_arcPathPoints, arcBorderRect.centerX(), arcBorderRect.centerY()) );
                        //
                        if(notRoundForm.notRoundForm_arcsPaths.get(0)!=null) canvas.drawPath(notRoundForm.notRoundForm_arcsPaths.get(0), nowPaint);
                    }
                }else if(mBorderType==BORDER_TYPE_UNDET_PROGRESS && myArcs.size()>0){
                    int i=0;
                    for(Arc arc : myArcs){
                        Paint nowPaint = new Paint();
                        nowPaint.set(mBorderPaint);
                        if(arc.paint.color!=-1)nowPaint.setColor(arc.paint.color);
                        if(arc.paint.shader!=null)nowPaint.setShader(arc.paint.shader);
                        //
                        if(mEnabledCircularTransformation)
                            canvas.drawArc(arcBorderRect, arc.startAngle, arc.sweepAngle, arc.useCenter, nowPaint);
                        else {
                            if(notRoundForm.arc_rx==-1) notRoundForm.arc_rx = arcBorderRect.width()*notRoundForm.radiusRatio;
                            if(notRoundForm.arc_ry==-1) notRoundForm.arc_ry = arcBorderRect.height()*notRoundForm.radiusRatio;
                            if(notRoundForm.notRoundForm_arcPathPoints==null) notRoundForm.notRoundForm_arcPathPoints =
                                    ImgPainter.getPoints(ImgPainter.listRoundRectPaths(arcBorderRect, notRoundForm.arc_rx, notRoundForm.arc_ry,
                                            notRoundForm.isTopLeftRound, notRoundForm.isTopRightRound,
                                            notRoundForm.isBottomRightRound, notRoundForm.isBottomLeftRound));
                            if(notRoundForm.notRoundForm_arcsPaths==null) {
                                notRoundForm.notRoundForm_arcsPaths = new ArrayList<>();
                                for(int j=0;j<myArcs.size();j++) notRoundForm.notRoundForm_arcsPaths.add(null);
                            }
                            if(notRoundForm.notRoundForm_arcsPaths.size()<=i) {
                                for(int k=notRoundForm.notRoundForm_arcsPaths.size();k<=i;k++) notRoundForm.notRoundForm_arcsPaths.add(null);
                            }
                            if(notRoundForm.notRoundForm_arcsPaths.size()>i && notRoundForm.notRoundForm_arcsPaths.get(i)==null)
                                notRoundForm.notRoundForm_arcsPaths.set(i, ImgPainter.arcPath(arc,
                                        notRoundForm.notRoundForm_arcPathPoints, arcBorderRect.centerX(), arcBorderRect.centerY()) );
                            //
                            if(notRoundForm.notRoundForm_arcsPaths.size()>i && notRoundForm.notRoundForm_arcsPaths.get(i)!=null) canvas.drawPath(notRoundForm.notRoundForm_arcsPaths.get(i), nowPaint);
                        }
                        //
                        i++;
                    }
                }
            }
        }

        //draw AroundViews
        if(aroundViews!=null && aroundViews.size()!=0){
            for(AroundView aroundView : aroundViews)
                if(aroundView.isVisible){
                    aroundView.init();
                    aroundView.onDraw(canvas);
                }
        }

        //draw description
        if(descriptor.isVisible){
            if(descriptor.isTextDatasReady);
                //TextPainter.drawTextCentered(descriptor.text, null, canvas, descriptor.textBound, true);
        }
        //draw forground
        forgrounder.update(canvas);
    }*/

    private int cmpA = 0;

    private void onDrawBitmap(Canvas canvas, Paint paint, float mDrawableRadius, RectF mDrawableRect, Path notRoundForm_drawablePath) {
        if(canvas==null || paint==null || mDrawableRect==null) return;
        //
        if(mEnabledCircularTransformation) {
            if(mDrawableRadius==0) return;
            //
            canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, paint);
        }else {
            if(notRoundForm_drawablePath==null) return;
            //
            if(notRoundForm.isRect()) canvas.drawRect(mDrawableRect, paint);
            else canvas.drawPath(notRoundForm_drawablePath, paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        setup();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        setup();
    }




    //*************************XIV***Methodes******************************************************

    private StarSystem starSystem;
    public StarSystem getStarSystem() {
        return starSystem;
    }

    public void setShadowRad(int shadowRad) {
        this.shadowRad = shadowRad;
        setup();
    }
    public void setShadowDx(int shadowDx) {
        this.shadowDx = shadowDx;
        setup();
    }
    public void setShadowDy(int shadowDy) {
        this.shadowDy = shadowDy;
        setup();
    }
    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
        setup();
    }
    public void setPadding(int pad) {
        setPadding(pad, pad, pad, pad);
    }
    public void setShadowRadRes(int resId) {
        this.shadowRad = (int) getResources().getDimension(resId);
        setup();
    }
    public void setShadowDxRes(int resId) {
        this.shadowDx = (int) getResources().getDimension(resId);
        setup();
    }
    public void setShadowDyRes(int resId) {
        this.shadowDy = (int) getResources().getDimension(resId);
        setup();
    }
    public void setShadowColorRes(int resId) {
        this.shadowColor = getResources().getColor(resId);
        setup();
    }
    public void setPaddingRes(int resId) {
        setPadding((int) getResources().getDimension(resId), (int) getResources().getDimension(resId), (int) getResources().getDimension(resId), (int) getResources().getDimension(resId));
    }
    public void setListener(XivListener xivListener) {
        this.xivListener = xivListener;
    }

    //*************************custom***anim******************************************************
    public IndetProgress getIndetProgress() {
        return indetProgress;
    }

    public DetProgress getDetProgress() {
        return detProgress;
    }

    public Pulsation getPulsation() {
        return pulsation;
    }

    //*************************fonctions****officieuses******************************************

    public void setmBitmap(Bitmap bmp){
        if(bmp==null || bmp.getHeight()==0 || bmp.getWidth()==0) return;
        mBitmap = bmp;

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //mBitmapPaint.setShader(null);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        invalidater();
    }

    public Paint getmBitmapPaint() {
        return mBitmapPaint;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public RectF getmDrawableRect() {
        return mDrawableRect;
    }

    public void setmDrawableRect(RectF mDrawableRect) {
        this.mDrawableRect = mDrawableRect;
        if(!mEnabledCircularTransformation) notRoundForm.updater();
        invalidater();
    }

    //*************************custom***meth******************************************************


    public void setOnFgClickListener(@Nullable Forgrounder.OnClickListener l) {
        forgrounder.setOnClickListener(l);
    }

    public void setDimens(int width, int height){
        try {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            this.setLayoutParams(layoutParams);
        }catch (Exception e){
            try{
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                this.setLayoutParams(layoutParams);
            }catch (Exception e2){}
        }
    }

    @Override
    public float getAlpha() {
        return alpha;
    }

    @Override
    public void setAlpha(float alpha) {
        this.alpha = (int) alpha;
        invalidater();
    }

    public float getmDrawableRadius() {
        return mDrawableRadius;
    }

    public Transitor getTransitor() {
        return transitor;
    }

    public String getImageAddress() {
        return imageAddress;
    }

    public Forgrounder getForgrounder() { return forgrounder; }

    public Descriptor getDescriptor() {
        return descriptor;
    }

    public Transformer getTransformer() {
        return transformer;
    }

    public NotRoundForm getNotRoundForm() {
        return notRoundForm;
    }

    public class NotRoundForm{
        public boolean isTopLeftRound = false, isTopRightRound = false, isBottomRightRound = false, isBottomLeftRound = false;
        public float rx=-1, ry=-1, border_rx=-1, border_ry=-1, arc_rx=-1, arc_ry=-1;//use default ratio
        public float radiusRatio = 1f/2;//
        public Path notRoundForm_drawablePath = null;
        public Path notRoundForm_borderPath = null;
        public List<ImgPainter.FloatPoint[]> notRoundForm_arcPathPoints = null;
        public List<Path> notRoundForm_arcsPaths = null;
        //
        private int dontUpdate = -1;
        //

        public void updater() {
            if(mEnabledCircularTransformation) {
                invalidater();
                return;
            }
            //
            if(dontUpdate!=0) {
                if (notRoundForm.rx != -1) notRoundForm.rx = mDrawableRect.width() * notRoundForm.radiusRatio;
                if (notRoundForm.ry != -1) notRoundForm.ry = mDrawableRect.height() * notRoundForm.radiusRatio;
            }
            if(notRoundForm.notRoundForm_drawablePath!=null)
                notRoundForm.notRoundForm_drawablePath =
                        ImgPainter.roundedRectPath(mDrawableRect, notRoundForm.rx, notRoundForm.ry,
                                notRoundForm.isTopLeftRound, notRoundForm.isTopRightRound, notRoundForm.isBottomRightRound, notRoundForm.isBottomLeftRound);
            if(notRoundForm.notRoundForm_borderPath!=null) notRoundForm.notRoundForm_borderPath =
                    ImgPainter.roundedRectPath(mBorderRect, notRoundForm.border_rx, notRoundForm.border_ry,
                            notRoundForm.isTopLeftRound, notRoundForm.isTopRightRound, notRoundForm.isBottomRightRound, notRoundForm.isBottomLeftRound);
            if(notRoundForm.notRoundForm_arcPathPoints!=null) notRoundForm.notRoundForm_arcPathPoints =
                    ImgPainter.getPoints(ImgPainter.listRoundRectPaths(arcBorderRect, notRoundForm.arc_rx, notRoundForm.arc_ry,
                            notRoundForm.isTopLeftRound, notRoundForm.isTopRightRound,
                            notRoundForm.isBottomRightRound, notRoundForm.isBottomLeftRound));
            if(notRoundForm.notRoundForm_arcsPaths==null) {
                notRoundForm.notRoundForm_arcsPaths = new ArrayList<>();
                for(Arc elt : myArcs) notRoundForm.notRoundForm_arcsPaths.add(null);
            }
            int i = 0;
            for (Arc arc : myArcs) {
                if(notRoundForm.notRoundForm_arcsPaths.size()<=i) {
                    for(int k=notRoundForm.notRoundForm_arcsPaths.size();k<=i;k++) notRoundForm.notRoundForm_arcsPaths.add(null);
                }
                if (notRoundForm.notRoundForm_arcsPaths.get(i) != null)
                    notRoundForm.notRoundForm_arcsPaths.set(i, ImgPainter.arcPath(arc,
                            notRoundForm.notRoundForm_arcPathPoints, arcBorderRect.centerX(), arcBorderRect.centerY()) );
                i++;
            }

            invalidater();
        }

        public void setRoundPeaks(boolean topLeftRound, boolean topRightRound, boolean bottomRightRound, boolean bottomLeftRound){
            isTopLeftRound = topLeftRound;
            isTopRightRound = topRightRound;
            isBottomRightRound = bottomRightRound;
            isBottomLeftRound = bottomLeftRound;
        }

        public void setRx(float rx) {
            if(rx>mDrawableRect.width()/2) return;
            this.rx = rx;
            dontUpdate = 0;
            updater();
        }

        public void setRy(float ry) {
            if(ry>mDrawableRect.height()/2) return;
            this.ry = ry;
            dontUpdate = 0;
            updater();
        }

        public void setCornerRadius(float r) {
            if(r>Math.min(mDrawableRect.height()/2, mDrawableRect.width()/2)) r=mDrawableRect.height()/4;
            this.rx = r;
            this.ry = r;
            dontUpdate = 0;
            updater();
            //Toast.makeText(getContext(), "yes", Toast.LENGTH_SHORT).show();
        }

        public void setRadiusRatio(float radiusRatio) {
            if(this.radiusRatio == radiusRatio) return;
            //
            this.radiusRatio = radiusRatio;
            //
            updater();
        }

        public void setTopLeftRound(boolean topLeftRound) {
            isTopLeftRound = topLeftRound;
        }

        public void setTopRightRound(boolean topRightRound) {
            isTopRightRound = topRightRound;
        }

        public void setBottomRightRound(boolean bottomRightRound) {
            isBottomRightRound = bottomRightRound;
        }

        public void setBottomLeftRound(boolean bottomLeftRound) {
            isBottomLeftRound = bottomLeftRound;
        }

        public void setNotRoundForm_drawablePath(Path notRoundForm_drawablePath) {
            this.notRoundForm_drawablePath = notRoundForm_drawablePath;
        }

        public void setNotRoundForm_borderPath(Path notRoundForm_borderPath) {
            this.notRoundForm_borderPath = notRoundForm_borderPath;
        }

        public boolean isRect() {
            return (!isTopLeftRound && !isTopRightRound && !isBottomRightRound && !isBottomLeftRound);
        }
    }

    public boolean isBoundWithBitmapDimens() {
        return mBoundWithBitmapDimens;
    }

    public void setBoundWithBitmapDimens(boolean mBoundWithBitmapDimens) {
        this.mBoundWithBitmapDimens = mBoundWithBitmapDimens;
        notRoundForm.updater();
        setup();
    }

    public boolean isBoundWithViewDimens() {
        return mBoundWithViewDimens;
    }

    public void setBoundWithViewDimens(boolean mBoundWithViewDimens) {
        this.mBoundWithViewDimens = mBoundWithViewDimens;
        notRoundForm.updater();
        setup();
    }

    public boolean isEnabledCircularTransformation() {
        return mEnabledCircularTransformation;
    }

    public void setEnabledCircularTransformation(boolean mEnabledCircularTransformation) {
        if(this.mEnabledCircularTransformation == mEnabledCircularTransformation) return;
        this.mEnabledCircularTransformation = mEnabledCircularTransformation;
        if(!mEnabledCircularTransformation && notRoundForm!=null) notRoundForm.updater();
        //initializeBitmap();
        setup();
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
        //TextPainter.drawTextCentered(allText, null, cvs, bounds, true);
    }

    private Bitmap getRatioBmp(Bitmap bmp, float ratio) {
        int w = (int) (bmp.getWidth()*ratio),
                h = bmp.getHeight();
        if(w==0 || h==0)return null;
        Bitmap bmpRogned = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasRogned = new Canvas(bmpRogned);
        canvasRogned.drawBitmap(bmp, 0, 0, null);
        return bmpRogned;
    }

    public void drawPolygon(Canvas canvas, Point[] points, Paint polyPaint) {
        // line at minimum...
        if (points.length < 2) {
            return;
        }

        // paint
            /*Paint polyPaint = new Paint();
            polyPaint.setColor(color);
            polyPaint.setStyle(Paint.Style.STROKE);
            polyPaint.setStrokeWidth(10);*/

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

    public int getSmartWidth(){
        int wBmp = (getWidth()!=0?getWidth():getLayoutParams()!=null && getLayoutParams().width!=0?getLayoutParams().width:0);
        return wBmp;
    }

    public int getSmartHeight(){
        int hBmp = (getHeight()!=0?getHeight():getLayoutParams()!=null && getLayoutParams().height!=0?getLayoutParams().height:0);
        if(isMirrorEffect)hBmp/=2;
        return hBmp;
    }

    Bitmap flip(Bitmap src) {
        Matrix m = new Matrix();
        m.preScale(1, -1);//horizontal:-1, 1//vertical:1, -1//
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }

    public int[] listToIntArray(List<Integer> objects){
        int[] tab;
        Object[] obj = objects.toArray();
        if(obj==null || obj.length==0){
            tab = new int[2];
            tab[0] = Color.TRANSPARENT;
            tab[1] = Color.TRANSPARENT;
        }else {
            tab = new int[(obj.length==1)?2:obj.length];
            int i=0;
            for(Object o : obj){
                tab[i]= (int) o;
                i++;
            }
            if(obj.length==1)tab[1]= (int) obj[0];
        }
        return tab;
    }

    public static class Spot{
        public float x;
        public float y;

        public Spot(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Spot(double x, double y) {
            this.x = (float) x;
            this.y = (float) y;
        }

        public float getDistanceTo(Spot spot){
            return (float) Math.sqrt(Math.pow(this.x-spot.x, 2) + Math.pow(this.y-spot.y, 2));
        }
    }

    public interface MainStateListener {

        void onBitmapCreation();

        void onBitmapCreated();
    }

    //testiiiiiiiiiiiiiiiiiiiiiii
    public Bitmap addShadow(final Bitmap bm, final int dstWidth, final int dstHeight, int color, int size, float dx, float dy) {
        final Bitmap mask = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ALPHA_8);

        final Matrix scaleToFit = new Matrix();
        final RectF src = new RectF(0, 0, bm.getWidth(), bm.getHeight());
        final RectF dst = new RectF(0, 0, dstWidth - dx, dstHeight - dy);
        scaleToFit.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

        final Matrix dropShadow = new Matrix(scaleToFit);
        dropShadow.postTranslate(dx, dy);

        final Canvas maskCanvas = new Canvas(mask);
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskCanvas.drawBitmap(bm, scaleToFit, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        maskCanvas.drawBitmap(bm, dropShadow, paint);

        final BlurMaskFilter filter = new BlurMaskFilter(size, BlurMaskFilter.Blur.NORMAL);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setMaskFilter(filter);
        paint.setFilterBitmap(true);

        final Bitmap ret = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        final Canvas retCanvas = new Canvas(ret);
        retCanvas.drawBitmap(mask, 0,  0, paint);
        retCanvas.drawBitmap(bm, scaleToFit, null);
        mask.recycle();
        return ret;
    }

    public static Bitmap doHighlightImage(Bitmap src) {
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth() + 96, src.getHeight() + 96, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmOut);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        Paint ptBlur = new Paint();
        ptBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
        int[] offsetXY = new int[2];
        Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
        Paint ptAlphaColor = new Paint();
        ptAlphaColor.setColor(Color.BLACK);
        canvas.drawBitmap(bmAlpha, offsetXY[0], offsetXY[1], ptAlphaColor);
        bmAlpha.recycle();
        canvas.drawBitmap(src, 0, 0, null);
        return bmOut;
    }
    //testiiiiiiiiiiiiiiiiiiiiiii

    public Bitmap getMainBmp() {
        return mainBmp;
    }

    public List<AroundView> getAroundViews() { return aroundViews; }

    public AroundView getAroundView(int i) {
        if(aroundViews==null || aroundViews.size()<=i) return null;
        return aroundViews.get(i);
    }

    public void clearAroundViews(){
        if(aroundViews==null) aroundViews = new ArrayList<>();
        else aroundViews.clear();
        invalidater();
    }

    public void addAroundView(float position){
        if(aroundViews==null) aroundViews = new ArrayList<>();
        aroundViews.add(new AroundView(position));
        invalidater();
    }

    public class AroundView{
        public XImageView view=null;
        public float position = 0f;
        public boolean isVisible = false;
        public boolean isInit = false;
        public float ratioSize = 1f/7;

        public static final int TYPE_FULL_COLOR = 0;
        public static final int TYPE_TEXT = 1;
        public static final int TYPE_IMAGE = 2;

        public int type = TYPE_FULL_COLOR;
        public String text = "";
        public int imgRes = -1;
        public String imgPath;
        public Bitmap bitmap;

        public float soft = 1;

        public void setText(String text, int... colors_f2b){//From foreground to back
            type = TYPE_TEXT;
            this.text = text;
            setColors(colors_f2b);
            isInit = false;
            invalidater();
        }

        public void setFullColor(int... colors_f2b){
            type = TYPE_FULL_COLOR;
            setColors(colors_f2b);
            isInit = false;
            invalidater();
        }

        public void setImage(String path, int... colors_f2b){
            type = TYPE_IMAGE;
            this.bitmap = null;
            this.imgPath = path;
            this.imgRes = -1;
            setColors(colors_f2b);
            isInit = false;
            invalidater();
        }

        public void setImage(int res, int... colors_f2b){
            type = TYPE_IMAGE;
            this.bitmap = null;
            this.imgPath = null;
            this.imgRes = res;
            setColors(colors_f2b);
            isInit = false;
            invalidater();
        }

        public void setImage(Bitmap bitmap, int... colors_f2b){
            type = TYPE_IMAGE;
            this.bitmap = bitmap;
            setColors(colors_f2b);
            isInit = false;
            invalidater();
        }

        private void setColors(int... colors_f2b) {
            if(colors_f2b==null) return;
            if(colors_f2b.length>0) colorBorder = colors_f2b[0];
            if(colors_f2b.length>1) colorContent = colors_f2b[1];
            if(colors_f2b.length>2) colorBg = colors_f2b[2];
            if(colors_f2b.length>3) colorShadow = colors_f2b[3];
        }

        public int colorContent = -1;
        public int marginContent = 0;
        public int colorBg = -1;
        public int colorBorder = -1;
        public int colorShadow = -1;

        public RectF bound, realBound;
        public Paint paintContent;
        public Paint paintBg;
        public Paint paintBorder;
        public Paint paintShadow;


        //Bitmap bmp;
        //Canvas canvas;

        public void onDraw(Canvas canvas) {
            //bmp = Bitmap.createBitmap((int) bound.width(), (int) bound.height(), Bitmap.Config.ARGB_8888);
            //canvas = new Canvas(bmp);
            //
            int save = canvas.save();
            canvas.clipPath(clPath);
            //
            if(colorBg!=-1)canvas.drawOval(bound, paintBg);
            //
            if(colorContent!=-1){
                switch(type){
                    case TYPE_FULL_COLOR:{
                        canvas.drawOval(new RectF(bound.left+marginContent, bound.top+marginContent,
                                bound.right-marginContent, bound.bottom-marginContent), paintContent);
                        break;
                    }
                    case TYPE_TEXT:{
                        TextPainter.drawTextCentered(text, paintContent, canvas,
                                new RectF(bound.left+marginContent, bound.top+marginContent,
                                        bound.right-marginContent, bound.bottom-marginContent),
                                true);
                        break;
                    }
                    case TYPE_IMAGE:{
                        /*canvas.drawOval(new RectF(bound.left+marginContent, bound.top+marginContent,
                                bound.right-marginContent, bound.bottom-marginContent), paintContent);*/
                        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), bound, null);
                        break;
                    }
                }
            }
            if(colorBorder!=-1)
                canvas.drawOval(new RectF(bound.left-paintBorder.getStrokeWidth()/2, bound.top-paintBorder.getStrokeWidth()/2,
                    bound.right+paintBorder.getStrokeWidth()/2, bound.bottom+paintBorder.getStrokeWidth()/2), paintBorder);
            ///
            //cvs.drawBitmap(bmp, rectFToRect(bound), rectFToRect(realBound), null);
            //
            canvas.restoreToCount(save);
        }

        private Rect rectFToRect(RectF rectF){
            return new Rect((int)rectF.left, (int)rectF.top, (int)rectF.right, (int)rectF.bottom);
        }

        Path clPath;
        public void init() {
            //if(isInit) return;
            float w = getSmartWidth() * ratioSize;
            float h = getSmartHeight() * ratioSize;
            float x = (float) (mDrawableRect.centerX() + mBorderRadius*Math.cos(Math.toRadians(position))- w/2),
                    y = (float) (mDrawableRect.centerY() + mBorderRadius*Math.sin(Math.toRadians(position)) - h/2);
            //
            bound = new RectF(x, y, x+w, y+h);
            //
            clPath = new Path();
            clPath.addOval(bound, Path.Direction.CCW);
            //bound = mBorderRect;
            int shadow = 1;
            marginContent = (int) (w/5);
            if(colorContent!=-1){
                if(paintContent==null)paintContent = new Paint();
                paintContent.setMaskFilter(new BlurMaskFilter(soft, BlurMaskFilter.Blur.NORMAL));
                if(type==TYPE_IMAGE){
                    if(bitmap==null) {
                        if(imgPath!=null) bitmap = XImage.decodeSampledBitmapFromPath(imgPath,
                                (int) bound.width()-marginContent*2, (int) bound.height()-marginContent*2);
                        else if(imgRes!=-1) bitmap = XImage.decodeSampledBitmapFromXMLResource(getResources(), imgRes,
                                (int) bound.width()-marginContent*2, (int) bound.height()-marginContent*2);
                    }
                    try {
                        paintContent.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                    }catch (Exception e){
                        bitmap = null;
                    }
                }else paintContent.setColor(colorContent);
                if(colorShadow!=-1){
                    paintContent.setShadowLayer(shadow,shadow, shadow, colorShadow);
                    paintContent.setMaskFilter(new BlurMaskFilter(soft, BlurMaskFilter.Blur.NORMAL));
                }
            }
            if(colorBg!=-1){
                if(paintBg==null)paintBg = new Paint();
                paintBg.setColor(colorBg);
                if(colorShadow!=-1){
                    paintBg.setShadowLayer(shadow,shadow, shadow, colorShadow);
                    paintBg.setMaskFilter(new BlurMaskFilter(soft, BlurMaskFilter.Blur.NORMAL));
                }
            }
            if(colorBorder!=-1){
                if(paintBorder==null)paintBorder = new Paint();
                paintBorder.setStyle(Paint.Style.STROKE);
                paintBorder.setStrokeWidth(w/5);
                paintBorder.setColor(colorBorder);
                paintBorder.setMaskFilter(new BlurMaskFilter(soft, BlurMaskFilter.Blur.NORMAL));
            }
            //
            isInit = true;
            if(isVisible)invalidater();
        }

        public AroundView(float position) {
            this.position = position;
        }

        public void update(){
            init();
        }

        public void setPosition(float position) {
            if(this.position == position) return;
            this.position = position;
            init();
        }

        public void setRatioSize(float ratioSize) {
            if(this.ratioSize == ratioSize) return;
            this.ratioSize = ratioSize;
            init();
        }

        public void setVisible(Boolean visible) {
            if(isVisible==visible)return;
            isVisible = visible;
            if(isVisible) init();
        }
    }

    public int getGraphType() {
        return graphType;
    }

    public void setGraphType(int graphType) {
        if(this.graphType == graphType)return;
        this.graphType = graphType;
        setup();
    }

    public RectF getArcBorderRect() {
        return arcBorderRect;
    }

    public float getmCircleBackgroundRadius() {
        return mCircleBackgroundRadius;
    }

    public float getMaxCircleBackgroundRadius() {
        return (mMaxBorderRadius>DEFAULT_MAX_BORDER_RADIUS?mMaxBorderRadius:(mBorderWidth>0?mBorderRadius:mDrawableRadius));
    }

    public void setmCircleBackgroundRadius(float mCircleBackgroundRadius) {
        if (mCircleBackgroundRadius == this.mCircleBackgroundRadius) {
            return;
        }

        if( 0<=mCircleBackgroundRadius && mCircleBackgroundRadius<=(mMaxBorderRadius>DEFAULT_MAX_BORDER_RADIUS?mMaxBorderRadius:(mBorderWidth>0?mBorderRadius:mDrawableRadius)) )
            this.mCircleBackgroundRadius = mCircleBackgroundRadius;
        if (mCircleBackgroundColor != Color.TRANSPARENT)
            invalidater();
    }

    public void setCircleBackgroundRadiusAuto(Boolean isCircleBackgroundRadiusAuto) {
        this.isCircleBackgroundRadiusAuto = isCircleBackgroundRadiusAuto;
    }

    public void addArc(float startAngle, float sweepAngle, Boolean useCenter, PaintBundle paint) {
        myArcs.add(new XImageView.Arc(startAngle, sweepAngle, useCenter, paint));
        setup();
    }

    public void clearArcs(){
        myArcs.clear();
        setup();
    }

    public class Arc{
        public float startAngle, sweepAngle;
        public Boolean useCenter;
        public PaintBundle paint;

        public Arc(float startAngle, float sweepAngle, Boolean useCenter, PaintBundle paint) {
            this.startAngle = startAngle;
            this.sweepAngle = sweepAngle;
            this.useCenter = useCenter;
            this.paint = paint;
        }

        public void setStartAngle(float startAngle) {
            this.startAngle = startAngle;
            notRoundForm.updater();
        }

        public void setSweepAngle(float sweepAngle) {
            this.sweepAngle = sweepAngle;
            notRoundForm.updater();
        }

        public void setUseCenter(Boolean useCenter) {
            this.useCenter = useCenter;
            setup();
        }

        public void setPaint(PaintBundle paint) {
            this.paint = paint;
            setup();
        }
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(@ColorInt int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidater();
    }

    public void setBorderAlpha(int borderAlpha) {
        mBorderPaint.setAlpha(borderAlpha);
        invalidater();
    }

    public boolean isUseGradientColorsForBorder() {
        return useGradientColorsForBorder;
    }

    public void setUseGradientColorsForBorder(boolean useGradientColorsForBorder) {
        if(this.useGradientColorsForBorder == useGradientColorsForBorder) return;
        this.useGradientColorsForBorder = useGradientColorsForBorder;
        upDateBorderColors();
    }

    public void upDateBorderColors(){
        mBorderPaint.setShader(null);
        if(this.mBorderColorList.size()!=0 && useGradientColorsForBorder) {
            SweepGradient shader = new SweepGradient(getSmartWidth() / 2, getSmartHeight() / 2,
                    listToIntArray(mBorderColorList), null);
            mBorderPaint.setShader(shader);
        }else {
            mBorderPaint.setColor(mBorderColor);
        }
        invalidater();
    }

    public void setmBorderColorList(List<Integer> mBorderColorList) {
        if(mBorderColorList==null) return;
        this.mBorderColorList = mBorderColorList;
        upDateBorderColors();
    }

    public void addBorderColor(@ColorInt int borderColor){
        if(mBorderColorList==null) mBorderColorList = new ArrayList<>();
        mBorderColorList.add(borderColor);
        upDateBorderColors();
    }

    public void clearBorderColorList(){
        if(mBorderColorList==null) mBorderColorList = new ArrayList<>();
        else mBorderColorList.clear();
        upDateBorderColors();
    }

    public int getCircleBackgroundColor() {
        return mCircleBackgroundColor;
    }

    public void setCircleBackgroundColor(@ColorInt int circleBackgroundColor) {
        if (circleBackgroundColor == mCircleBackgroundColor) {
            return;
        }

        mCircleBackgroundColor = circleBackgroundColor;
        mCircleBackgroundPaint.setColor(circleBackgroundColor);
        invalidater();
    }

    public void setCircleBackgroundColorResource(@ColorRes int circleBackgroundRes) {
        setCircleBackgroundColor(getContext().getResources().getColor(circleBackgroundRes));
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }


    public List<Arc> getArcs() {return myArcs;}

    public void setArcs(List<Arc> myArcs) {
        this.myArcs = myArcs;
        setup();
    }

    public int getmBorderType() {
        return mBorderType;
    }

    public void setmBorderType(int mBorderType) {
        if (mBorderType == this.mBorderType) {
            return;
        }

        this.mBorderType = mBorderType;
        this.mInternalBorderWidth *= this.mBorderType!=BORDER_TYPE_NORMAL?2:1/2;
        setup();
    }

    public int getmInternalBorderWidth() {return mInternalBorderWidth;}

    public void setmInternalBorderWidth(int mInternalBorderWidth) {
        if (mInternalBorderWidth/2 == this.mInternalBorderWidth) {
            return;
        }

        this.mInternalBorderWidth = mInternalBorderWidth/2;
        this.mInternalBorderWidth *= mBorderType!=BORDER_TYPE_NORMAL?2:1;
        setup();
    }

    public int getmMaxBorderWidth() {return mMaxBorderWidth;}

    public void setmMaxBorderWidth(int mMaxBorderWidth) {
        if (mMaxBorderWidth == this.mMaxBorderWidth) {
            return;
        }

        this.mMaxBorderWidth = mMaxBorderWidth;
        setup();
    }

    public boolean isBorderOverlay() {
        return mBorderOverlay;
    }

    public void setBorderOverlay(boolean borderOverlay) {
        if (borderOverlay == mBorderOverlay) {
            return;
        }

        mBorderOverlay = borderOverlay;
        setup();
    }

    public void setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
        cmpA=0;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if(bm==null) return;
        super.setImageBitmap(bm);
        initializeBitmap();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initializeBitmap();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        initializeBitmap();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        initializeBitmap();
    }

    public void setImageFile(File file) {
        setImagePath(file.getAbsolutePath());
    }

    public void setImagePath(final String path) {
        new Thread(new Runnable() {
            Bitmap bitmap;
            @Override
            public void run() {
                bitmap = XImage.decodeSampledBitmapFromPath(path, getSmartWidth(), getSmartHeight());
                post(new Runnable() {
                    @Override
                    public void run() {
                        if(bitmap!=null) setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter) {
            return;
        }

        mColorFilter = cf;
        applyColorFilter();
        invalidater();
    }

    @Override
    public ColorFilter getColorFilter() {
        return mColorFilter;
    }

    private void applyColorFilter() {
        mBitmapPaint.setColorFilter(mColorFilter);
    }

    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initializeBitmap() {
        if(transitor==null) transitor = new Transitor(this);
        transitor.source(mBitmap);
        mBitmap = getBitmapFromDrawable(getDrawable());
            /*if(bmp!=null){
                mBitmap = bmp;
                Toast.makeText(getContext(), "done:"+(bmp==null), Toast.LENGTH_SHORT).show();
            }*/
        //mBitmap = bmp!=null?bmp:mBitmap;
        transitor.target(mBitmap);
        transitor.transite();
        transitor.useDimens(mDrawableRadius, mDrawableRect, notRoundForm==null?null:notRoundForm.notRoundForm_drawablePath);

        setup();
    }

    public void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if ( getSmartWidth() == 0 || getSmartHeight() == 0) {
            return;
        }

        if (mBitmap == null) {
            invalidater();
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        upDateBorderColors();
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mCircleBackgroundPaint.setStyle(Paint.Style.FILL);
        mCircleBackgroundPaint.setAntiAlias(true);
        mCircleBackgroundPaint.setColor(mCircleBackgroundColor);
        mCircleBackgroundPaint.setAlpha(mBgAlpha);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderRect.set(calculateBounds(getSmartWidth(), getSmartHeight(), mBitmap));
        //
        arcBorderRect = new RectF();
        arcBorderRect.set(mBorderRect);
        arcBorderRect.left -= mBorderWidth/2;
        arcBorderRect.top -= mBorderWidth/2;
        arcBorderRect.right += mBorderWidth/2;
        arcBorderRect.bottom += mBorderWidth/2;
        //
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2.0f, (mBorderRect.width() - mBorderWidth) / 2.0f)+mInternalBorderWidth+borderInterval();

        //calculate maxRadius
        if( (mMaxBorderWidth!=DEFAULT_MAX_BORDER_WIDTH) && (mMaxBorderWidth >= mBorderWidth) ){

            mMaxBorderRadius = Math.min((mBorderRect.height() ) / 2.0f , (mBorderRect.width() ) / 2.0f)+mInternalBorderWidth+mMaxBorderWidth;

            mBorderRadius = mMaxBorderRadius-mMaxBorderWidth+mBorderWidth/2;
        }


        mDrawableRect.set(mBorderRect);
        if (!mBorderOverlay && mInternalBorderWidth > 0) {
            mDrawableRect.inset(mInternalBorderWidth - 1.0f, mInternalBorderWidth - 1.0f);
        }
        mDrawableRadius = Math.min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f);


        if(isCircleBackgroundRadiusAuto)
            mCircleBackgroundRadius = (mMaxBorderRadius>DEFAULT_MAX_BORDER_RADIUS?mMaxBorderRadius:(mBorderWidth>0?mBorderRadius:mDrawableRadius));

        applyColorFilter();
        updateShaderMatrix();
        invalidater();
    }

    private float borderInterval() {
        return (mMaxBorderWidth!=DEFAULT_MAX_BORDER_WIDTH) && (mMaxBorderWidth >= mBorderWidth) ? mMaxBorderWidth : mBorderWidth > 0 ? mBorderWidth: 0;
    }

    public float potentialDrawableRadius = 0;

    public RectF calculateBounds(int w, int h, Bitmap bitmap) {
        int availableWidth  = w - getPaddingLeft() - getPaddingRight();
        int availableHeight = h - getPaddingTop() - getPaddingBottom();

        int sideLength = Math.min(availableWidth, availableHeight);

        potentialDrawableRadius = (sideLength-2*mInternalBorderWidth-2*borderInterval())/2;

        float w_adjustment = 0;
        float h_adjustment = 0;

        float left;
        float top;
        if( mEnabledCircularTransformation || (!mBoundWithBitmapDimens && !mBoundWithViewDimens) ){
            left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
            top = getPaddingTop() + (availableHeight - sideLength) / 2f;
        }else if(mBoundWithViewDimens){
            left = getPaddingLeft();
            top = getPaddingTop();
        }else {
            left = getPaddingLeft();
            top = getPaddingTop();

            float newW, newH;
            float lastW = availableWidth-2*mInternalBorderWidth-2*borderInterval();
            float lastH = availableHeight-2*mInternalBorderWidth-2*borderInterval();
            //
            if(availableWidth<availableHeight){
                newW = availableWidth;
                newH = bitmap.getHeight()*availableWidth/bitmap.getWidth();
            }else {
                newH = availableHeight;
                newW = bitmap.getWidth()*availableHeight/bitmap.getHeight();
            }
            //
            w_adjustment = (lastW-newW)/2;
            h_adjustment = (lastH-newH)/2;
            if(w_adjustment<0 || h_adjustment<0){
                float delta = Math.abs(Math.min(w_adjustment, h_adjustment));
                w_adjustment += delta;
                h_adjustment += delta;
            }
        }

        RectF ret_bounds = (mEnabledCircularTransformation || (!mBoundWithBitmapDimens && !mBoundWithViewDimens)) ?
                new RectF(left+mInternalBorderWidth+borderInterval(), top+mInternalBorderWidth+borderInterval(),
                        left + sideLength-mInternalBorderWidth-borderInterval(), top + sideLength-mInternalBorderWidth-borderInterval()) :
                new RectF(left+mInternalBorderWidth+borderInterval()+w_adjustment, top+mInternalBorderWidth+borderInterval()+h_adjustment,
                        left + availableWidth-mInternalBorderWidth-borderInterval()-w_adjustment, top + availableHeight-mInternalBorderWidth-borderInterval()-h_adjustment) ;

        return descriptor.isVisible?descriptor.initBound(ret_bounds):ret_bounds;
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    private boolean manual_manage_touch = false;
    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
        manual_manage_touch = true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()) return false;
        ///
        if(!manual_manage_touch) {
            if (inTouchableArea(event.getX(), event.getY()))
                forgrounder.onTouch(event, (mBorderWidth > 0 ? new RectF(mBorderRect.left - getPaddingLeft() - mBorderWidth, mBorderRect.top - mBorderWidth - getPaddingTop(), mBorderRect.right + mBorderWidth + getPaddingRight(), mBorderRect.bottom + mBorderWidth + getPaddingBottom()) :
                        new RectF(mDrawableRect.left - getPaddingLeft(), mDrawableRect.top - getPaddingTop(), mDrawableRect.right + getPaddingRight(), mDrawableRect.bottom + getPaddingBottom())));
            else
                forgrounder.touchUp = true;
        }
        //
        if(starSystem.isEnabled) starSystem.onTouchEvent(event);
        //
        return (manual_manage_touch && super.onTouchEvent(event)) || !manual_manage_touch && ( inTouchableArea(event.getX(), event.getY()) && ( super.onTouchEvent(event) || forgrounder.isEnabled) );
    }

    private boolean inTouchableArea(float x, float y) {
        if(mEnabledCircularTransformation || (!mBoundWithBitmapDimens && !mBoundWithViewDimens))
            return Math.pow(x - mBorderRect.centerX(), 2) + Math.pow(y - mBorderRect.centerY(), 2) <= Math.pow(mBorderRadius, 2);
        else
            return (mBorderRect.left<=x && x<=mBorderRect.right) && (mBorderRect.top<=y && y<=mBorderRect.bottom);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class OutlineProvider extends ViewOutlineProvider {

        @Override
        public void getOutline(View view, Outline outline) {
            Rect bounds = new Rect();
            mBorderRect.roundOut(bounds);
            outline.setRoundRect(bounds, bounds.width() / 2.0f);
        }

    }

}
