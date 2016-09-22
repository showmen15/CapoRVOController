package pl.agh.capo.utilities.state;

import com.vividsolutions.jts.math.Vector2D;

import java.io.Serializable;

public class Location extends Point implements Serializable {

    private double direction;

    public Location(double x, double y, double direction) {
        super(x, y);
        this.direction = direction;
    }

    public double getDirection() {
        return direction;
    }

    public Vector2D getUnitVector(){
        return new Vector2D(Math.cos(direction), Math.sin(direction));
    }
}
