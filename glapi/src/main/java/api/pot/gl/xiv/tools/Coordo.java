package api.pot.gl.xiv.tools;

import android.util.Log;

import static api.pot.gl.xiv.tools.ImgPainter.get_2Pi_radiant;
import static java.lang.Float.NaN;

public class Coordo {
    public float x, y;

    public Coordo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float generateAngle(Coordo from_spot, Coordo to_spot) {
        return generateAngle(this, from_spot, to_spot);
    }

    public static float generateAngle(Coordo org_spot, Coordo from_spot, Coordo to_spot) {
        float angle = 0;
        try {
            Coordo delta_org_from = new Coordo(from_spot.x - org_spot.x, from_spot.y - org_spot.y);
            Coordo delta_org_to = new Coordo(to_spot.x - org_spot.x, to_spot.y - org_spot.y);
            float angle_org_from = (float) (Math.atan(delta_org_from.y / delta_org_from.x));
            float angle_org_to = (float) (Math.atan(delta_org_to.y / delta_org_to.x));
            angle_org_from = get_2Pi_radiant((float) Math.toDegrees(angle_org_from));
            angle_org_to = get_2Pi_radiant((float) Math.toDegrees(angle_org_to));
            Log.d("GES_GESTURE", " //////////////// "+angle_org_from+" //////////////// "+angle_org_to);
            angle = angle_org_to - angle_org_from;
        }catch (Exception e){ angle = 0;}
        return angle;
    }

    public float argument(){
        return argument(new Coordo(0, 0), this);
    }

    public static float argument(Coordo spot1, Coordo spot2){
        float arg = 0;
        arg = (float) (Math.atan((spot2.y-spot1.y) / (spot2.x-spot1.x)));
        arg = (float) (Math.toDegrees(arg)%90);
        return arg;
    }

    public void setLast_arg_is_set(boolean last_arg_is_set) {
        this.last_arg_is_set = last_arg_is_set;
    }

    public void setLast_arg(float last_arg) {
        this.last_arg = last_arg;
    }

    //calcul le decalage angulaire generer par ces 2 point lors de leur mvt
    public float angularOffset(Coordo spot1, Coordo spot2){
        float arg = argument(spot1, spot2);
        if(!last_arg_is_set) {
            last_arg = arg;
            last_arg_is_set = true;
        }
        float angularOffset = arg - last_arg;
        last_arg = arg;
        return angularOffset==NaN ? 0 : angularOffset;
    }
    private boolean last_arg_is_set = false;
    private float last_arg = NaN;

    public float distanceTo(Coordo spot2) {
        return (float) Math.sqrt(Math.pow(spot2.x-this.x, 2) + Math.pow(spot2.y-this.y, 2));
    }
}
