package api.pot.gl.xiv.tools;

import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;

import api.pot.gl.xiv.XImageView;

import java.util.ArrayList;
import java.util.List;

public class ImgPainter {

    public static Path arcPath(XImageView.Arc arc, List<FloatPoint[]> pointsList, float cx, float cy){
        Path arcPath = new Path();
        int j=0;
        FloatPoint[] points = null;
        boolean isNewPath = true;
        for(int i=0;i<pointsList.size();i++) {
            if(j==pointsList.size()-1) points = pointsList.get(0);
            else points = pointsList.get(i);
            //
            for (FloatPoint fp : points) {
                if (isPointBelongToArc(fp, arc, cx, cy)) {
                    if (arcPath.isEmpty() || isNewPath) {
                        arcPath.moveTo(fp.x, fp.y);
                    } else arcPath.lineTo(fp.x, fp.y);
                    //
                    if(isNewPath)isNewPath = false;
                }else isNewPath = true;
            }
            if(j==pointsList.size()-1) break;
            j = i;
            if(i==pointsList.size()-1) i--;
        }
        return arcPath;
    }

    private static boolean isPointBelongToArc(FloatPoint fp, XImageView.Arc arc, float cx, float cy) {
        boolean isGrowing = (arc.startAngle+arc.sweepAngle>=arc.startAngle);
        boolean isCrossing2Pi = ((arc.startAngle+arc.sweepAngle)%360!=0) && (arc.startAngle%360!=0) && Math.floor((arc.startAngle+arc.sweepAngle)/360)!=Math.floor(arc.startAngle/360);
        float startAngle=get_2Pi_radiant(arc.startAngle), endAngle=get_2Pi_radiant(arc.startAngle+arc.sweepAngle),
                c_angle=0, tan_c_angle=0;
        if(startAngle==(float)(2*Math.PI)) startAngle = 0;
        //
        if(cx-fp.x!=0){
            tan_c_angle = (fp.y-cy)/(fp.x-cx);
            if( (fp.y-cy)/Math.abs(fp.y-cy)<0 && (fp.x-cx)/Math.abs(fp.x-cx)<0 )
                c_angle = (float) (Math.atan(tan_c_angle) + Math.PI);
            else if( (fp.y-cy)/Math.abs(fp.y-cy)<0 && (fp.x-cx)/Math.abs(fp.x-cx)>0 )
                c_angle = (float) (Math.atan(tan_c_angle) + 2*Math.PI);
            else if( (fp.y-cy)/Math.abs(fp.y-cy)>0 && (fp.x-cx)/Math.abs(fp.x-cx)<0 )
                c_angle = (float) (Math.atan(tan_c_angle) + Math.PI);
            else if( (fp.y-cy)==0 && (fp.x-cx)/Math.abs(fp.x-cx)<0 )
                c_angle = (float) Math.PI;
            else
                c_angle = (float) Math.atan(tan_c_angle);
            c_angle = get_2Pi_radiant((float) Math.toDegrees(c_angle));
        }else {
            if((fp.y-cy)>0) c_angle = get_2Pi_radiant(90);
            else c_angle = get_2Pi_radiant(270);
        }
        //
        return  (isGrowing && ( (!isCrossing2Pi && startAngle<=c_angle && c_angle<=endAngle) ||
                    (isCrossing2Pi && (startAngle<=c_angle && c_angle<=2*Math.PI || 0<=c_angle && c_angle<=endAngle) ) ) ) ||
                (!isGrowing && ( (!isCrossing2Pi && endAngle<=c_angle && c_angle<=startAngle) ||
                        (isCrossing2Pi && (endAngle<=c_angle && c_angle<=2*Math.PI || 0<=c_angle && c_angle<=startAngle) ) ) ) ;
    }

    public static float get_2Pi_radiant(float degres){
        if(degres!=0 && degres%360==0) return (float) (2*Math.PI);
        return (float) ( (Math.toRadians(degres)+2*Math.PI)%(2*Math.PI));
    }

    public static List<FloatPoint[]> getPoints(List<Path> paths) {
        List<FloatPoint[]> pointArrayList = new ArrayList<>();
        //
        for(Path path0 : paths) {
            PathMeasure pm = new PathMeasure(path0, false);
            float length = pm.getLength();
            final int nbrPoints = (int) length;
            FloatPoint[] pointArray = new FloatPoint[nbrPoints];
            float distance = 0f;
            float speed = length / nbrPoints;
            int counter = 0;
            float[] aCoordinates = new float[2];

            while ((distance < length) && (counter < nbrPoints)) {
                // get point from the path
                pm.getPosTan(distance, aCoordinates, null);
                pointArray[counter] = new FloatPoint(aCoordinates[0],
                        aCoordinates[1]);
                counter++;
                distance = distance + speed;
            }

            pointArrayList.add(pointArray);
        }
        //
        return pointArrayList;
    }

    public static class FloatPoint {
        float x, y;

        public FloatPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }

    public static Path listToPath(List<Path> paths){
        Path path = new Path();
        for (Path p : paths)
            path.addPath(p);
        path.close();
        return path;
    }

    public static List<Path> listRoundRectPaths(
            RectF bound, float rx, float ry,
            boolean tl, boolean tr, boolean br, boolean bl
    ){ return roundedRectPaths(bound.left, bound.top, bound.right, bound.bottom, rx, ry, tl, tr, br, bl);}

    public static Path roundedRectPaths(
            RectF bound, float rx, float ry,
            boolean tl, boolean tr, boolean br, boolean bl
    ){ return listToPath( roundedRectPaths(bound.left, bound.top, bound.right, bound.bottom, rx, ry, tl, tr, br, bl) );}

    public static List<Path> roundedRectPaths(
            float left, float top, float right, float bottom, float rx, float ry,
            boolean tl, boolean tr, boolean br, boolean bl
    ){
        List<Path> path = new ArrayList<Path>();
        int i=0;
        while (i<8){
            path.add(new Path());
            i++;
        }
        //
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        if(rx==0 && ry==0){
            tl = false; tr = false; br = false; bl = false;
        }
        float width = right - left;
        float height = bottom - top;
        if (rx > width / 2) rx = width / 2;
        if (ry > height / 2) ry = height / 2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        i=0;
        path.get(i).moveTo(right, top + ry);
        if (tr) {
            path.get(i).rQuadTo(0, -ry, -rx, -ry);//top-right corner
        }else{
            path.get(i).rLineTo(0, -ry);
            path.get(i).rLineTo(-rx,0);
        }
        //
        i++;
        path.get(i).moveTo(right-rx, top);
        path.get(i).rLineTo(-widthMinusCorners, 0);
        //
        i++;
        path.get(i).moveTo(left+rx, top);
        if (tl)
            path.get(i).rQuadTo(-rx, 0, -rx, ry); //top-left corner
        else{
            path.get(i).rLineTo(-rx, 0);
            path.get(i).rLineTo(0,ry);
        }
        //
        i++;
        path.get(i).moveTo(left, top+ry);
        path.get(i).rLineTo(0, heightMinusCorners);
        //
        i++;
        path.get(i).moveTo(left, bottom-ry);
        if (bl)
            path.get(i).rQuadTo(0, ry, rx, ry);//bottom-left corner
        else{
            path.get(i).rLineTo(0, ry);
            path.get(i).rLineTo(rx,0);
        }
        //
        i++;
        path.get(i).moveTo(left+rx, bottom);
        path.get(i).rLineTo(widthMinusCorners, 0);
        //
        i++;
        path.get(i).moveTo(right-rx, bottom);
        if (br)
            path.get(i).rQuadTo(rx, 0, rx, -ry); //bottom-right corner
        else{
            path.get(i).rLineTo(rx,0);
            path.get(i).rLineTo(0, -ry);
        }
        //
        i++;
        path.get(i).moveTo(right, bottom-ry);
        path.get(i).rLineTo(0, -heightMinusCorners);
        //
        //path.close();//Given close, last lineto can be removed.

        return path;
    }

    public static Path roundedRectPath(
            RectF bound, float rx, float ry,
            boolean tl, boolean tr, boolean br, boolean bl
    ){ return roundedRectPath(bound.left, bound.top, bound.right, bound.bottom, rx, ry, tl, tr, br, bl);}

    public static Path roundedRectPath(
            float left, float top, float right, float bottom, float rx, float ry,
            boolean tl, boolean tr, boolean br, boolean bl
    ){
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width / 2) rx = width / 2;
        if (ry > height / 2) ry = height / 2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        if (tr)
            path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
        else{
            path.rLineTo(0, -ry);
            path.rLineTo(-rx,0);
        }
        path.rLineTo(-widthMinusCorners, 0);
        if (tl)
            path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
        else{
            path.rLineTo(-rx, 0);
            path.rLineTo(0,ry);
        }
        path.rLineTo(0, heightMinusCorners);

        if (bl)
            path.rQuadTo(0, ry, rx, ry);//bottom-left corner
        else{
            path.rLineTo(0, ry);
            path.rLineTo(rx,0);
        }

        path.rLineTo(widthMinusCorners, 0);
        if (br)
            path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner
        else{
            path.rLineTo(rx,0);
            path.rLineTo(0, -ry);
        }

        path.rLineTo(0, -heightMinusCorners);

        path.close();//Given close, last lineto can be removed.

        return path;
    }

    public static Bitmap getResizedBitmap(Bitmap originalBitmap, int newWidth, int newHeight){
        if(originalBitmap==null) return null;
        //
        if(newWidth<=0 && newHeight>0){
            newWidth = originalBitmap.getWidth() * newHeight / originalBitmap.getHeight();
        }else if(newWidth>0 && newHeight<=0){
            newHeight = originalBitmap.getHeight() * newWidth / originalBitmap.getWidth();
        } else if(newWidth<=0 && newHeight<=0) return null;
        //
        return Bitmap.createScaledBitmap(
                originalBitmap, newWidth, newHeight, false);
    }
}
