package api.pot.gl.geo;

import android.graphics.Canvas;

public class Spot {
    public float x;
    public float y;

    public Spot(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean isInto(Canvas canvas) {
        return (0<x && x<canvas.getWidth()) && (0<y && y<canvas.getHeight());
    }
}
