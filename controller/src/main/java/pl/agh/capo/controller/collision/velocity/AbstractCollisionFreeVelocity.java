package pl.agh.capo.controller.collision.velocity;

import org.apache.log4j.Logger;
import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;
import pl.agh.capo.controller.collision.WallCollisionDetector;
import pl.agh.capo.velocityobstales.CollisionException;
import pl.agh.capo.velocityobstales.VelocityObstacles;
import pl.agh.capo.velocityobstales.VelocityObstaclesBuilder;

import java.util.List;
import java.util.Map;

public abstract class AbstractCollisionFreeVelocity {

    protected final Logger logger = Logger.getLogger(AbstractCollisionFreeVelocity.class);

    protected Map<Integer, State> states;
    protected WallCollisionDetector wallCollisionDetector;
    protected Location location;
    protected Velocity velocity;
    protected int RobotID; 

    protected List<VelocityObstacles> velocityObstaclesList;

    public AbstractCollisionFreeVelocity(Map<Integer, State> states,WallCollisionDetector wallCollisionDetector, int robotId)
    {
    	 this.states = states;
         this.wallCollisionDetector = wallCollisionDetector;
         this.RobotID = robotId;
    }
    
    public void buildVelocityObstacles(Location location, Velocity velocity)
    {
        this.location = location;
        this.velocity = velocity;  
        buildVelocityObstacles();
    }
       
    public void buildVelocityObstacles(Location location, Velocity velocity, double currentRobotFearFactor)
    {
        this.location = location;
        this.velocity = velocity;  
        buildVelocityObstacles(currentRobotFearFactor);
    }

    public boolean isCurrentVelocityCollisionFree() {
        return isVelocityCollisionFree(velocity);
    }

    public Velocity get() {
        return findBestCollisionFreeVelocity();
    }

    protected abstract void buildVelocityObstacles();
    
    protected abstract void buildVelocityObstacles(double currentRobotFearFactor);

    protected abstract Velocity findBestCollisionFreeVelocity();

    protected VelocityObstacles buildVelocityObstacles(State state) {
        try {
            return new VelocityObstaclesBuilder()
                    .setFirstRobotCenter(location)
                    .setSecondRobotCenter(state.getLocation())
                    .setFirstRobotVelocity(velocity)
                    .setSecondRobotVelocity(state.getVelocity())
                    .buildReciprocalVelocityObstacles();
        } catch (CollisionException e) {
            logger.warn("COLLISION[!][!][!]");
            return null;
        }
    }

    protected boolean isVelocityCollisionFree(Velocity velocity) {
        for (VelocityObstacles vo : velocityObstaclesList) 
        {
            if (vo.inside(velocity)) 
            {
                return false;
            }
        }
        
        return wallCollisionDetector.collisionFree(location, velocity);
    }

    protected boolean isVelocityBetter(double bestDist, double tmpDist, double speed){
        if (speed < EnvironmentalConfiguration.MIN_SPEED_FACTOR * EnvironmentalConfiguration.PREF_ROBOT_SPEED){
            return false;
        }
        return bestDist > tmpDist;
    }
}
