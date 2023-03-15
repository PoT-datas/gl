package api.pot.gl.xiv.tools.stars;

public class Point {
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
