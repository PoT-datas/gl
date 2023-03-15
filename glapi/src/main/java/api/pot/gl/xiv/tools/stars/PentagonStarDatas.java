package api.pot.gl.xiv.tools.stars;

import android.graphics.Paint;

public class PentagonStarDatas {
    public Point first;
    public float bigRadius, bigSide, littleRadius, littleSide, starBigSide, starHeight;

    public void init(Point central, float input_bigRadius, Paint paint){
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
