package pl.agh.capo.velocityobstales;

import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.Point;
import pl.agh.capo.utilities.state.Velocity;

public class VelocityObstaclesBuilder {


    private Location firstRobotLocation;
    public VelocityObstaclesBuilder setFirstRobotCenter(Location location){
        firstRobotLocation = location;
        return this;
    }

    private Location secondRobotLocation;
    public VelocityObstaclesBuilder setSecondRobotCenter(Location location){
        secondRobotLocation = location;
        return this;
    }

    private Velocity firstRobotVelocity;
    public VelocityObstaclesBuilder setFirstRobotVelocity(Velocity velocity){
        firstRobotVelocity = velocity;
        return this;
    }

    private Velocity secondRobotVelocity;
    public VelocityObstaclesBuilder setSecondRobotVelocity(Velocity velocity){
        secondRobotVelocity = velocity;
        return this;
    }

    public VelocityObstacles buildReciprocalVelocityObstacles() throws CollisionException {
        double vx = (firstRobotVelocity.getX() + secondRobotVelocity.getX()) / 2;
        double vy = (firstRobotVelocity.getY() + secondRobotVelocity.getY()) / 2;

        return new VelocityObstacles(relativeLocation(), new Point(vx, vy), findRadius());
    }

    public VelocityObstacles build() throws CollisionException {
        return new VelocityObstacles(relativeLocation(), secondRobotVelocity, findRadius());
    }

    private double findRadius() throws CollisionException {
        double distance = firstRobotLocation.distance(secondRobotLocation);
        if (distance > EnvironmentalConfiguration.VO_ROBOT_RADIUS){
            return EnvironmentalConfiguration.VO_ROBOT_RADIUS;
        }
        if (distance < EnvironmentalConfiguration.ROBOT_DIAMETER){
            throw new CollisionException();
        }
        return distance;
    }

    private Location relativeLocation(){
        double x = secondRobotLocation.getX() - firstRobotLocation.getX();
        double y = secondRobotLocation.getY() - firstRobotLocation.getY();
        return new Location(x, y, secondRobotLocation.getDirection());
    }
}
