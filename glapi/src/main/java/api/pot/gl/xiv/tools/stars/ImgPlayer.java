package api.pot.gl.xiv.tools.stars;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import java.util.ArrayList;
import java.util.List;

import api.pot.gl.tools.Global;
import api.pot.gl.xiv.XImageView;

public class ImgPlayer {
    private XImageView main;

    public ImgPlayer(XImageView main) {
        this.main = main;
    }

    public long duration=0;
    public List<Integer> colors = new ArrayList<>();
    public float nbrVisibleColors = 0;
    public RectF bound = null;
    public Boolean isInit = false, isPlaying = false;

    private long movieStart=0, now, relTime;
    public Bitmap cBmp;
    private Bitmap allBmp;
    private Canvas cCanvas, allCanvas;
    private Paint cPaint, allPaint;
    private Shader allShader;
    public List<Integer> allColors = new ArrayList<>();
    private float x=0, y=0, ratio;

    public void onInit(){
        if(/*imgPlayer.isPlaying &&*/ !isInit) {
            bound = bound==null?main.getmDrawableRect():bound;
            duration = duration==0?6000:duration;
            if(colors.size()==0) {
                colors.add(Global.FIRST_GES_GRADIENT_COLOR);
                colors.add(Global.SECOND_GES_GRADIENT_COLOR);
                colors.add(Global.THIRD_GES_GRADIENT_COLOR);
            }
            nbrVisibleColors = nbrVisibleColors==0?2f:nbrVisibleColors;
            init();
        }
    }

    public void init(){
        if(bound==null || nbrVisibleColors==0 || colors.size()==0 || duration==0)return;

        allColors.clear();
        for(int i=0;i<2*colors.size();i++){
            allColors.add(colors.get(i%colors.size()));
        }

        allBmp = Bitmap.createBitmap((int) (2*(colors.size()/nbrVisibleColors)*bound.width()), (int) bound.height(), Bitmap.Config.ARGB_8888);
        allCanvas = new Canvas(allBmp);
        allPaint = new Paint();
        allShader = new LinearGradient(0, 0, allBmp.getWidth(), 0, listToIntArray(allColors),
                null, Shader.TileMode.CLAMP);
        allPaint.setShader(allShader);
        allCanvas.drawRect(new RectF(0, 0, allBmp.getWidth(), allBmp.getHeight()), allPaint);

        movieStart = 0;
        isInit = true;
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

    public void update(){
        //if(!isInit || !isPlaying)return;

        cBmp = Bitmap.createBitmap((int)bound.width(), (int) bound.height(), Bitmap.Config.ARGB_8888);
        cCanvas = new Canvas(cBmp);

        now = android.os.SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }
        relTime = (int) ((now - movieStart) % duration);

        ratio = ((float) relTime)/duration;
        //x = ratio*(allBmp.getWidth()-cBmp.getWidth());
        //x = ratio*((colors.size()/nbrVisibleColors)*bound.width());
        x = (1-ratio)*(colors.size()*allBmp.getWidth()/(2*colors.size()-1));//(1-ratio)=>pour le sens du mvt(g->d)
        y=0;

        cCanvas.drawBitmap(allBmp, -x, y, null);

        main.invalidater();
    }

    public void setNbrVisibleColors(int nbrVisibleColors) {
        if (nbrVisibleColors>colors.size())return;
        this.nbrVisibleColors = nbrVisibleColors;
        isInit = false;
    }

    public void setBound(RectF bound) {
        this.bound = bound;
        isInit = false;
    }
}