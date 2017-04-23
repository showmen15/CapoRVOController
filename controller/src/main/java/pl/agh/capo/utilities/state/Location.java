package pl.agh.capo.utilities.state;

import com.vividsolutions.jts.math.Vector2D;

import java.io.Serializable;

public class Location extends Point implements Serializable {

    private double direction;

    public Location(double x, double y, double direction) {
        super(x, y);
        
        
        this.direction = normalizeAngle(direction);
    }

    public double getDirection() {
        return direction;
    }
    
    public static double normalizeAngle(double angle) {
        return Math.atan2(Math.sin(angle), Math.cos(angle));
    }   
    
    //Tylko do testow RVO - ruch w dowolnym kierunku
    public void setDirection(double x,double y) {
    	direction = Math.atan2(y,x);
    }
    
    public void setDirection(double Direction) {
        direction = Direction;
    }

    public Vector2D getUnitVector(){
        return new Vector2D(Math.cos(direction), Math.sin(direction));
    }
    
    public double getDistance(Location l)
	{
		return Math.sqrt((this.getX() - l.getX()) * (this.getX() - l.getX()) + (this.getY() - l.getY()) * (this.getY() - l.getY()));
	}
    
    public void setX(double X)
    {
      super.setX(X);
    }
    
    public void setY(double Y)
    {
    	super.setY(Y);
    }
}
