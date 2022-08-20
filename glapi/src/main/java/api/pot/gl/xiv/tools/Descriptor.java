package api.pot.gl.xiv.tools;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import api.pot.gl.tools.Global;
import api.pot.gl.xiv.XImageView;

public class Descriptor {
    private XImageView main;
    public String text = "cloud";
    private float text_w, text_h, main_w;
    private int textSize = Global.GES_DESCRIPTOR_TEXT_SIZE;
    private Paint textPaint;
    private float shift = 0;
    //
    public boolean isVisible = false;
    public RectF textBound;
    public boolean isTextDatasReady = false;
    //
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 1;
    private int align = ALIGN_LEFT;

    public Descriptor(XImageView main) {
        this.main = main;
        //
        init();
    }


    public void init(){
        initPaint();
        generateMeasure();
    }

    private void generateMeasure() {
        Rect now_textBound = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), now_textBound);
        text_w = now_textBound.width();
        text_h = now_textBound.height();
    }

    private void initPaint() {
        textPaint = new Paint();
        textPaint.setTextSize(textSize);
    }

    public void onVisibilityChange(){
        isVisible = !isVisible;
        if(isVisible) show();
        else hide();
    }

    private void show() {
        if(isVisible) return;
        main_w = main.getmDrawableRect().width();
        if(main.getSmartWidth()-main_w<text_w){
            //shift
            return;
        }else {
            //
        }
        shift = text_w/2;
    }

    private void hide() {
        if(!isVisible) return;
    }

    public RectF initBound(RectF main_bounds) {
        RectF ret=null;
        if(align==ALIGN_LEFT) {
            ret = new RectF(main_bounds.left - shift, main_bounds.top,
                    main_bounds.right - shift, main_bounds.bottom);
            //
            textBound = new RectF(ret.right, main_bounds.centerY() - text_h / 2,
                    ret.right + text_w, main_bounds.centerY() + text_h / 2);
        }else if(align==ALIGN_RIGHT){
            ret = new RectF(main_bounds.left + shift, main_bounds.top,
                    main_bounds.right + shift, main_bounds.bottom);
            //
            textBound = new RectF(ret.left - text_w, main_bounds.centerY() - text_h / 2,
                    ret.left, main_bounds.centerY() + text_h / 2);
        }
        isTextDatasReady = true;
        //
        return ret;
    }

    public void setVisibility(boolean isVisible) {
        show();
        this.isVisible = isVisible;
        main.setup();
    }

    public void setText(String text) {
        if(align==ALIGN_LEFT && text.charAt(0)!=' ') {
            text = " "+text;
        }else if(align==ALIGN_RIGHT && text.charAt(text.length()-1)!=' '){
            text += "";
        }
        this.text = text;
        //
        init();
    }
}
