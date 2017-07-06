package pl.agh.capo.utilities;

import com.vividsolutions.jts.math.Vector2D;
import pl.agh.capo.utilities.state.Location;

public class RobotMotionModel {

    public static final double ROBOT_HALF_DIAMETER = EnvironmentalConfiguration.ROBOT_DIAMETER / 2;

    private static final double VERY_SMALL_DOUBLE = 0.0001;  //	nedded in some calculations; 0.1 milimeter is assumed smaller than accuracy

    private final double maxLinearVelocity;

    private Location location = new Location(0, 0, 0);
    private double velocityLeft;
    private double velocityRight;

    public RobotMotionModel(double maxLinearVelocity) {
        this.maxLinearVelocity = maxLinearVelocity;
    }

    public double getMaxLinearVelocity() {
        return maxLinearVelocity;
    }

    public Location getLocation() {
        return location;
    }

    public double getVelocityLeft() {
        return velocityLeft;
    }

    public double getVelocityRight() {
        return velocityRight;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean setVelocity(double velocityLeft, double velocityRight) {
        this.velocityLeft = velocityLeft;
        this.velocityRight = velocityRight;
        return checkFeasibility();
    }

    public Vector2D getUnitVector() {
        return location.getUnitVector();
    }

    public double getLinearVelocity() {
        return (velocityLeft + velocityRight) / 2;
    }

    public double getAngularVelocity() {
        return (velocityLeft - velocityRight) / (2 * EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE);
    }
    
    public double getSumVelocityLeftRight()
    {
    	return 0.2; //(velocityLeft + velocityRight);
    }
    
    public double getSubVelocityLeftRight()
    {
    	return 0.2; //(velocityLeft - velocityRight);
    }

    public double getArcRadius() {
        if (velocityLeft == velocityRight) {
            return Double.NaN;
        }
        return EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE * (velocityLeft + velocityRight) / (velocityLeft - velocityRight);
    }

    public boolean setAngularVelocity(double angularVelocity) {
        velocityLeft = angularVelocity * EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE;
        velocityRight = -angularVelocity * EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE;
        return checkFeasibility();
    }

    public boolean setLinearAndAngularVelocities(double linearVelocity, double angularVelocity) {
        if (Math.abs(linearVelocity) < VERY_SMALL_DOUBLE) {
            return setAngularVelocity(angularVelocity);
        } else {
            //velocityLeft = linearVelocity * (1 + ((angularVelocity * WHEELS_HALF_DISTANCE) / linearVelocity));
            velocityLeft = linearVelocity + angularVelocity * EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE;
            //velocityRight = linearVelocity * (1 - ((angularVelocity * WHEELS_HALF_DISTANCE) / linearVelocity));
            velocityRight = linearVelocity - angularVelocity * EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE;
        }
        return checkFeasibility();
    }
    
    public boolean spinAround(double direction,double angleToTarget)
    {
    	System.out.println("AngleTo target: " + angleToTarget);
    	System.out.println("Directorion: " + direction);
    	
    	
    	if(angleToTarget < 0)
    	{
    	velocityLeft = - EnvironmentalConfiguration.ROBOT_MAX_SPEED;
    	velocityRight =   EnvironmentalConfiguration.ROBOT_MAX_SPEED;
    	}
    	else
    	{
    		velocityLeft =  EnvironmentalConfiguration.ROBOT_MAX_SPEED;
    		velocityRight = -  EnvironmentalConfiguration.ROBOT_MAX_SPEED;
    	}
    	
    	return true;
    }

    public void performMoveByTimeInMilliseconds(int deltaTime) {
        location = calculateLocationAfterTimeInMillisecond(deltaTime);
    }

    public Location calculateLocationAfterTimeInMillisecond(int deltaTime) {
    	 
    	double radius = getArcRadius();
        if (Double.isNaN(radius)) {
            return calculateLocationAfterTimeMovingStraight(deltaTime / 1000.0);
        }
        return calculateLocationAfterTimeTurning(radius, deltaTime / 1000.0);
    	
    	/*double radius = getArcRadius();
         if (Double.isNaN(radius)) {
             return calculateLocationAfterTimeMovingStraight(deltaTime / 1000.0);
         }
         else
         {      	 
        	 Location loc = calculateLocationAfterTimeTurning(radius, deltaTime / 1000.0);        	 
        	 return loc;
         }*/
    	
    	//return new Location(0, 0 , 0);
    	
    	/*double X, Y, alfaNew, Alfa;
       double wheelTrack = 0.2;
       
       Alfa = location.getDirection();
       X = location.getX();
       Y = location.getY();
       
       alfaNew = (((getSubVelocityLeftRight()) * deltaTime) / wheelTrack); 
    	
		X += (wheelTrack * (getSumVelocityLeftRight())) / (2*(getSubVelocityLeftRight())) * (Math.sin(alfaNew + Alfa) - Math.sin(Alfa));
		Y -= (wheelTrack * (getSumVelocityLeftRight())) / (2*(getSubVelocityLeftRight())) * (Math.cos(alfaNew + Alfa) - Math.cos(Alfa));

		Alfa += alfaNew;
    	
    	
    	return new Location(X, Y , Alfa );
         */
    	/* double radius = getArcRadius();
        if (Double.isNaN(radius)) {
            return calculateLocationAfterTimeMovingStraight(deltaTime / 1000.0);
        }
        return calculateLocationAfterTimeTurning(radius, deltaTime / 1000.0);
        */
    }

    private Location calculateLocationAfterTimeTurning(double radius, double deltaTime) {
        double arcCenterX = location.getX() - radius * Math.sin(location.getDirection());
        double arcCenterY = location.getY() + radius * Math.cos(location.getDirection());

        double angularVelocityDeltaTime = getAngularVelocity() * deltaTime;

        double newX = Math.cos(angularVelocityDeltaTime) * (location.getX() - arcCenterX)
                - Math.sin(angularVelocityDeltaTime) * (location.getY() - arcCenterY)
                + arcCenterX;
        double newY = Math.sin(angularVelocityDeltaTime) * (location.getX() - arcCenterX)
                + Math.cos(angularVelocityDeltaTime) * (location.getY() - arcCenterY)
                + arcCenterY;

        return new Location(newX, newY, location.getDirection() + angularVelocityDeltaTime);
    }

    private Location calculateLocationAfterTimeMovingStraight(double deltaTime) {
        double x = location.getX() + getLinearVelocity() * Math.cos(location.getDirection()) * deltaTime;
        double y = location.getY() + getLinearVelocity() * Math.sin(location.getDirection()) * deltaTime;
        double direction = location.getDirection();
        return new Location(x, y, direction);
    }

    private boolean checkFeasibility() {
        if (Math.abs(velocityLeft) < maxLinearVelocity && Math.abs(velocityRight) < maxLinearVelocity) {
            return true;
        }
        double divider = Math.max(Math.abs(velocityLeft), Math.abs(velocityRight)) / maxLinearVelocity;
        velocityLeft /= divider;
        velocityRight /= divider;
        return false;
    }

    public boolean setLinearVelocity(double linearVelocity) {
        velocityLeft = linearVelocity;
        velocityRight = linearVelocity;
        return checkFeasibility();
    }




    public boolean setAngularVelocityAndArcRadiusToMoveOn(double arcRadius, double angularVelocity) {
        if (Math.abs(arcRadius) < VERY_SMALL_DOUBLE) {
            return setAngularVelocity(angularVelocity);
        } else {
            //	velocityLeft = (arcRadius * angularVelocity) * (1 + (WHEELS_HALF_DISTANCE / arcRadius));
            velocityLeft = angularVelocity * (arcRadius + EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE);
            //	velocityRight = (arcRadius * angularVelocity) * (1 - (WHEELS_HALF_DISTANCE / arcRadius));
            velocityRight = angularVelocity * (arcRadius - EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE);
        }
        return checkFeasibility();
    }

    public boolean setLinearVelocityAndArcRadiusToMoveOn(double linearVelocity, double arcRadius) {
        if (Math.abs(arcRadius) < VERY_SMALL_DOUBLE) {
            arcRadius = Math.signum(arcRadius) * VERY_SMALL_DOUBLE;
        }
        velocityLeft = linearVelocity * (1 + (EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE / arcRadius));
        velocityRight = linearVelocity * (1 - (EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE / arcRadius));
        return checkFeasibility();
    }
}