package pl.agh.capo.utilities.state;

import com.vividsolutions.jts.math.Vector2D;

import java.io.Serializable;

public class Point implements Serializable {

    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector2D toVectorFromOrigin() {
        return new Vector2D(x, y);
    }

    public double distance(Point point) {
        double deltaX = x - point.getX();
        double deltaY = y - point.getY();
        return getDistance(deltaX, deltaY);
    }

    public double distanceFromOrigin(){
        return getDistance(x, y);
    }

    private double getDistance(double xDiff, double yDiff){
        return Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (Double.compare(point.x, x) != 0) return false;
        if (Double.compare(point.y, y) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
