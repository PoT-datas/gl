package api.pot.gl.xiv.tools.stars;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class StarMng {
    private static float widthRatio = 10.0f/75.0f;

    public static void drawStar(Canvas canvas, RectF bound, int color){

        StarSystem.Star starModel = new StarSystem.Star();
        starModel.paint = new Paint();
        starModel.paint.setShadowLayer(1,1, 1, Color.BLACK);
        starModel.paint.setColor(color);
        starModel.paint.setStyle(Paint.Style.FILL);
        starModel.bound = bound;
        starModel.paint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.INNER));
        StarMng.drawPolygon(canvas, StarMng.getPointsForStar(starModel.bound, starModel.paint),
                starModel.paint);
    }

    public static Point[] getPointsForStar(RectF bound, Paint paint) {
        //if(psDatas==null){
        PentagonStarDatas psDatas = new PentagonStarDatas();
        float radius = bound.width()>bound.height()?bound.height()/2:bound.width()/2;
        if(paint.getStyle()!= Paint.Style.FILL){
            if(0>=paint.getStrokeWidth())paint.setStrokeWidth(radius*widthRatio);
            if(paint.getStrokeWidth()>radius/2)paint.setStrokeWidth(radius/2);
            radius -= 1.5*paint.getStrokeWidth();
        }
        psDatas.init(new Point(bound.centerX(), bound.centerY()), radius, paint);
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

    public static void drawPolygon(Canvas canvas, Point[] points, Paint polyPaint) {
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
}
