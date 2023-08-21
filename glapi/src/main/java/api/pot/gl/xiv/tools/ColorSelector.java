package api.pot.gl.xiv.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.widget.Toast;

import api.pot.gl.xiv.XImageView;

public class ColorSelector {
    public static final int COLOR_SELECTOR_NONE = -1;
    public static final int COLOR_SELECTOR_LINEAR = 0;
    public static final int COLOR_SELECTOR_LINEAR_H = 1;
    public static final int COLOR_SELECTOR_LINEAR_V = 2;
    public static final int COLOR_SELECTOR_CIRCULAR = 3;
    //
    public int type = COLOR_SELECTOR_NONE;
    public int linear_type = COLOR_SELECTOR_LINEAR_H;
    private XImageView main;
    private boolean isInit = false;
    private float cx, cy;
    private Paint paint = new Paint(), paint_border = new Paint();
    public boolean isEnabled = false, isStrokeWidthSet = false;
    public float selector_ratio = 1f/8;
    public int selected_color = Color.WHITE;
    private long time=0;
    //
    private OnColorChangeListener onColorChangeListener = null;

    private float c_radius = 0, c_cx=0, c_cy=0;
    private XImageView.Spot spot;
    Bitmap mixBmp;
    Canvas mixCanvas;
    Paint mixPaint = new Paint();

    private int brightness = 255, maxBrightness = 255;
    private float brightness_c_cx, brightness_c_cy, brightness_radius;
    private RectF brightness_bound;
    private boolean isBrightnessing = false, touching = false;
    private Paint brightnessPaint = new Paint();

    private RectF linear_selector_bound;
    private Bitmap linear_selector_bitmap = null;
    private float linear_selector_cx, linear_selector_cy, linear_selector_radius;
    private Canvas linear_selector_canvas;
    private Paint linear_selector_paint = new Paint();

    public ColorSelector(final XImageView main) {
        this.main = main;
        paint.setStyle(Paint.Style.FILL);
        paint_border.setStyle(Paint.Style.STROKE);
        paint_border.setColor(Color.WHITE);
    }

    public void setType(int type) {
        this.type = type;
        init(type);
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public void init(int type) {
        if (isInit) return;
        //
        this.type = type;/**
        //
        if(type==COLOR_SELECTOR_CIRCULAR) {
            main.getMain().addListener(new XImageView.MainStateListener() {
                @Override
                public void onBitmapCreation() {
                    ///main.getIndetProgress().initType(IndetProgress.INDET_PROGRESS_STATIC_TURN);
                    ///main.getIndetProgress().play();
                }

                @Override
                public void onBitmapCreated() {
                    cx = main.getmDrawableRect().centerX();
                    cy = main.getmDrawableRect().centerY();
                    //
                    main.getIndetProgress().stop();
                    main.setBorderWidth(0);
                    main.setmInternalBorderWidth(0);
                    //
                    int radius = (int) Math.min(main.getmDrawableRect().width() * selector_ratio, main.getmDrawableRect().height() * selector_ratio);
                    if (Math.max(main.getmMaxBorderWidth(), main.getBorderWidth()) < radius) {
                        main.setmMaxBorderWidth(radius);
                    } else main.invalidater();
                    //
                    isInit = true;
                    //
                    isEnabled = true;
                    c_radius = Math.min(main.getmDrawableRect().width(), main.getmDrawableRect().height()) / 2;
                    c_cx = main.getMain().getMain_bound().centerX();
                    c_cy = main.getMain().getMain_bound().centerY();
                    brightness_bound = new RectF(c_cx + c_radius - radius / 3, c_cy - c_radius, c_cx + c_radius, c_cy + c_radius);
                    brightness_radius = radius / 2;
                }
            });
            //
            main.setDisableCircularTransformation(false);
            main.getMain().clearColors();
            main.getMain().setMainType(XImageView.Main.MAIN_COLOR_SELECTOR);
            main.getMain().addColor(Color.TRANSPARENT);
            main.getForgrounder().setEnabled(false);
        }if(type==COLOR_SELECTOR_LINEAR){
            if(main.getSmartWidth()>main.getSmartHeight()){
                linear_type = COLOR_SELECTOR_LINEAR_H;
            }else {
                linear_type = COLOR_SELECTOR_LINEAR_V;
            }
            main.setDisableCircularTransformation(true);
            main.getMain().clearColors();
            main.getMain().setMainType(XImageView.Main.MAIN_GRADIENT_LINEAR);
            main.getMain().addColor(Color.TRANSPARENT);
            main.getForgrounder().setEnabled(false);
            //
            isEnabled = true;
            //
            isInit = true;
        }*/
    }

    public void onTouch(MotionEvent event) {
        if(type==COLOR_SELECTOR_NONE || !isInit || !isEnabled) {
            Toast.makeText(main.getContext(), (type==COLOR_SELECTOR_NONE)+"/yo/"+(isInit), Toast.LENGTH_SHORT).show();
            return;
        }
        else if(type==COLOR_SELECTOR_CIRCULAR) {/**
            if (main.getMain().mainType != MAIN_COLOR_SELECTOR) return;*/
            //
            spot = new XImageView.Spot(event.getX(), event.getY());
            //
            touching = true;
            //
            if (event.getAction() == MotionEvent.ACTION_DOWN && (
                    Math.pow(spot.x - brightness_c_cx, 2) + Math.pow(spot.y - brightness_c_cy, 2) <= Math.pow(brightness_radius, 2)
                    || (brightness_bound.left<=spot.x && spot.x<=brightness_bound.right) && (brightness_bound.top<=spot.y && spot.y<=brightness_bound.bottom))    ) {
                isBrightnessing = true;
            }
            if (!isBrightnessing) {
                if (Math.pow(spot.x - c_cx, 2) + Math.pow(spot.y - c_cy, 2) <= Math.pow(c_radius, 2)) {
                    cx = spot.x;
                    cy = spot.y;
                } else {
                    cx = c_cx - (c_radius / spot.getDistanceTo(new XImageView.Spot(c_cx, c_cy))) * (c_cx - spot.x);
                    cy = c_cy - (c_radius / spot.getDistanceTo(new XImageView.Spot(c_cx, c_cy))) * (c_cy - spot.y);
                }
            } else if (brightness_bound.top <= spot.y && spot.y <= brightness_bound.bottom) {
                brightness = (int) (255 * (brightness_bound.bottom - spot.y < 0 ? brightness_bound.bottom :
                        brightness_bound.bottom - spot.y > brightness_bound.height() ? brightness_bound.height() : brightness_bound.bottom - spot.y) /
                        brightness_bound.height());
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isBrightnessing = false;
                touching = false;
            }
            main.invalidater();
        }else if(type==COLOR_SELECTOR_LINEAR){
            //
            spot = new XImageView.Spot(event.getX(), event.getY());
            //
            if(linear_type==COLOR_SELECTOR_LINEAR_H && linear_selector_bound.left <= spot.x && spot.x <= linear_selector_bound.right){
                linear_selector_cx = spot.x;
                main.invalidater();
            }else if(linear_type==COLOR_SELECTOR_LINEAR_V && linear_selector_bound.top <= spot.y && spot.y <= linear_selector_bound.bottom){
                linear_selector_cy = spot.y;
                main.invalidater();
            }
        }
    }

    public void update(Canvas canvas) {
        if(type==COLOR_SELECTOR_NONE || !isInit) return;
        else if(type==COLOR_SELECTOR_CIRCULAR) {/**
            if (main.getMain().mainType != MAIN_COLOR_SELECTOR) return;*/
            //
            mixBmp = Bitmap.createBitmap(main.getmBitmap().getWidth(), main.getmBitmap().getHeight(), Bitmap.Config.ARGB_8888);
            mixCanvas = new Canvas(mixBmp);
            mixCanvas.drawBitmap(main.getmBitmap(), 0, 0, null);
            mixCanvas.drawColor(Color.argb(255 - brightness, 0, 0, 0));
            selected_color = mixBmp.getPixel((int) cx, (int) cy);
            if (onColorChangeListener != null) onColorChangeListener.onColorChange(selected_color);
            //
            mixPaint.setColor(Color.argb(255 - brightness, 0, 0, 0));
            canvas.drawCircle(c_cx, c_cy, c_radius, mixPaint);
            //
            paint.setColor(selected_color);
            if (!isStrokeWidthSet) {
                paint_border.setStrokeWidth(Math.min(main.getmDrawableRect().width() * selector_ratio, main.getmDrawableRect().height() * selector_ratio) / 4);
                isStrokeWidthSet = true;
            }
            canvas.drawCircle(cx, cy, Math.min(main.getmDrawableRect().width() * selector_ratio, main.getmDrawableRect().height() * selector_ratio), paint_border);
            canvas.drawCircle(cx, cy, Math.min(main.getmDrawableRect().width() * selector_ratio, main.getmDrawableRect().height() * selector_ratio), paint);

            if (brightness_bound != null && (!touching || (touching && isBrightnessing))) {
                LinearGradient shader = new LinearGradient(0, brightness_bound.top, 0, brightness_bound.bottom,
                        main.getmBitmap().getPixel((int) cx, (int) cy), Color.BLACK, Shader.TileMode.CLAMP);
                brightnessPaint.setShader(shader);
                //
                brightness_c_cx = brightness_bound.centerX();
                brightness_c_cy = brightness_bound.bottom - brightness_bound.height() * Float.valueOf(brightness) / maxBrightness;
                canvas.drawRoundRect(brightness_bound, brightness_bound.width() / 2, brightness_bound.width() / 2, brightnessPaint);
                canvas.drawCircle(brightness_c_cx, brightness_c_cy, brightness_radius, paint_border);
                canvas.drawCircle(brightness_c_cx, brightness_c_cy, brightness_radius, paint);
            }
            touching = false;
        }else if(type==COLOR_SELECTOR_LINEAR){
            if(linear_selector_bitmap==null){
                /*justification des decalage:
                * main.getmDrawableRect().height()/2 || main.getmDrawableRect().width()/2 (padding for scroller radius)
                * main.getmDrawableRect().height()/2/8 || main.getmDrawableRect().width()/2/8 (padding for scroller stroke width)
                * */
                if(main.getSmartWidth()>main.getSmartHeight()){
                    linear_type = COLOR_SELECTOR_LINEAR_H;
                    linear_selector_bound = new RectF(main.getmDrawableRect().left+main.getmDrawableRect().height()/2, main.getmDrawableRect().top+main.getmDrawableRect().height()/16,
                            main.getmDrawableRect().right-main.getmDrawableRect().height()/2, main.getmDrawableRect().bottom-main.getmDrawableRect().height()/16);
                }else {
                    linear_type = COLOR_SELECTOR_LINEAR_V;
                    linear_selector_bound = new RectF(main.getmDrawableRect().left+main.getmDrawableRect().width()/16, main.getmDrawableRect().top+main.getmDrawableRect().width()/2,
                            main.getmDrawableRect().right-main.getmDrawableRect().width()/16, main.getmDrawableRect().bottom-main.getmDrawableRect().width()/2);
                }
                //
                linear_selector_bitmap = Bitmap.createBitmap((int) linear_selector_bound.width(), (int)linear_selector_bound.height(), Bitmap.Config.ARGB_8888);
                linear_selector_canvas = new Canvas(linear_selector_bitmap);
                if(linear_type==COLOR_SELECTOR_LINEAR_H){
                    LinearGradient shader = new LinearGradient(0, 0, linear_selector_bitmap.getWidth(), 0,
                            new int[]{Color.BLACK, Color.RED, Color.GREEN, Color.TRANSPARENT, Color.BLUE, Color.RED, Color.WHITE}, null, Shader.TileMode.CLAMP);
                    linear_selector_paint.setShader(shader);
                    linear_selector_canvas.drawRoundRect(new RectF(0, 0, linear_selector_bitmap.getWidth(), linear_selector_bitmap.getHeight()),
                            linear_selector_bitmap.getHeight()/2, linear_selector_bitmap.getHeight()/2, linear_selector_paint);
                    //
                    linear_selector_radius = linear_selector_bitmap.getHeight()/2;
                }else if(linear_type==COLOR_SELECTOR_LINEAR_V){
                    LinearGradient shader = new LinearGradient(0, linear_selector_bitmap.getHeight(), 0, 0,
                            new int[]{Color.BLACK, Color.RED, Color.GREEN, Color.TRANSPARENT, Color.BLUE, Color.RED, Color.WHITE}, null, Shader.TileMode.CLAMP);
                    linear_selector_paint.setShader(shader);
                    linear_selector_canvas.drawRoundRect(new RectF(0, 0, linear_selector_bitmap.getWidth(), linear_selector_bitmap.getHeight()),
                            linear_selector_bitmap.getWidth()/2, linear_selector_bitmap.getWidth()/2, linear_selector_paint);
                    //
                    linear_selector_radius = linear_selector_bitmap.getWidth()/2;
                }
                //
                linear_selector_cx = linear_selector_bound.centerX();
                linear_selector_cy = linear_selector_bound.centerY();
            }
            //
            selected_color = linear_selector_bitmap.getPixel((int) (linear_selector_cx-linear_selector_bound.left), (int) (linear_selector_cy-linear_selector_bound.top));
            if (onColorChangeListener != null) onColorChangeListener.onColorChange(selected_color);
            //
            paint.setColor(selected_color);
            if (!isStrokeWidthSet) {
                paint_border.setStrokeWidth(linear_selector_radius / 4);
                isStrokeWidthSet = true;
            }
            //
            canvas.drawBitmap(linear_selector_bitmap, linear_selector_bound.left, linear_selector_bound.top, null);
            //
            canvas.drawCircle(linear_selector_cx, linear_selector_cy, linear_selector_radius, paint_border);
            canvas.drawCircle(linear_selector_cx, linear_selector_cy, linear_selector_radius, paint);
        }
    }

    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.onColorChangeListener = onColorChangeListener;
    }

    public interface OnColorChangeListener {

        void onColorChange(int color);
    }
}
