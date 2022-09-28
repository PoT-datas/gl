package api.pot.gl.geo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.List;

public class XLandmark {
    public Spot o = new Spot(0, 0);
    public Vector i = new Vector(1, 0);
    public Vector j = new Vector(0, 1);

    private int width = 5;
    private int axisColor = Color.GRAY;

    public Spot maxi = new Spot(0, 0);
    public Spot maxj = new Spot(0, 0);

    public Spot label = new Spot(0, 0);

    private Paint paint;

    public XLandmark(Spot o, Vector i, Vector j) {
        this.o = o;
        this.i = i;
        this.j = j;
    }

    public XLandmark(Canvas canvas, Spot o, Vector i, Vector j) {
        this.o = o;
        this.i = i;
        this.j = j;
        //
        setMax(canvas);
        //
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
        paint.setColor(axisColor);
    }

    public void draw(Canvas canvas) {
        paint.setColor(axisColor);
        //
        canvas.drawLine(o.x, o.y, maxi.x, maxi.y, paint);
        TextPainter.drawTextCentered("Zone", null, canvas,
                new RectF(maxi.x-100,maxi.y,maxi.x,maxi.y+20), true);
        //
        canvas.drawLine(o.x, o.y, maxj.x, maxj.y, paint);
        TextPainter.drawTextCentered("Vitesse", null, canvas,
                new RectF(maxj.x,maxj.y,maxj.x+100,maxj.y+20), true);
        //
        label = new Spot(maxj.x+100, maxj.y+50);
    }

    public void drawLines(Canvas canvas, List<Spot> spots, int color, String l) {
        paint.setColor(color);
        canvas.drawCircle(label.x, label.y, width, paint);
        TextPainter.drawTextCentered(l, null, canvas,
                new RectF(label.x+width, label.y-width,label.x+100, label.y+30), true);
        label = new Spot(label.x, label.y+50);
        //
        for (int i=0;i<spots.size()-1;i++)
            drawLine(canvas, spots.get(i), spots.get(i+1), color);
    }

    private void drawLine(Canvas canvas, Spot spot1, Spot spot2, int color) {
        canvas.drawLines(nomalyze(spot1, spot2), paint);
    }

    private float[] nomalyze(Spot spot1, Spot spot2) {
        float[] points = new float[4];
        Spot spot;
        //
        spot = getRightSpotMark(o, i, j, spot1);
        points[0] = spot.x;
        points[1] = spot.y;
        //
        spot = getRightSpotMark(o, i, j, spot2);
        points[2] = spot.x;
        points[3] = spot.y;
        //
        return points;
    }

    private Spot getRightSpotMark(Spot o, Vector i, Vector j, Spot distance) {
        return new Spot(o.x+distance.x*i.dx,
                o.y+distance.y*j.dy);
    }

    private float getLength(Vector v){
        return (float) Math.sqrt(Math.pow(v.dx, 2)+Math.pow(v.dy, 2));
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setAxisColor(int axisColor) {
        this.axisColor = axisColor;
    }

    public void setMax(Canvas canvas) {
        maxi = o;
        Spot spot;
        int cmp = 0;
        while ( (spot=next(o, i, cmp)).isInto(canvas) ){
            maxi = spot;
            cmp++;
        }
        cmp = 0;
        while ( (spot=next(o, j, cmp)).isInto(canvas) ){
            maxj = spot;
            cmp++;
        }
    }

    private Spot next(Spot o, Vector v, int cmp) {
        return new Spot(o.x+cmp*v.dx, o.y+cmp*v.dy);
    }
}
