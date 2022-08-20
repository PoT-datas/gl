package api.pot.gl.xiv.tools;

import android.graphics.SweepGradient;

public class PaintBundle {
    public int color;
    public SweepGradient shader;

    public PaintBundle(int color, SweepGradient shader) {
        this.color = color;
        this.shader = shader;
    }
}
