package pl.agh.capo.utilities.state;

import com.vividsolutions.jts.math.Vector2D;

import java.io.Serializable;

public class Velocity extends Point implements Serializable {

    public Velocity(double x, double y) {
        super(x, y);
    }

    public double getSpeed(){
        return super.distanceFromOrigin();
    }

    public Vector2D toVector2D(){
        return super.toVectorFromOrigin();
    }

    public static Velocity fromVector2D(Vector2D vector2D){
        return new Velocity(vector2D.getX(), vector2D.getY());
    }
}
