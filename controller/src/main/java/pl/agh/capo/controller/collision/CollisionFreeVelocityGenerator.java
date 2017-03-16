package pl.agh.capo.controller.collision;

import pl.agh.capo.utilities.communication.StateReceivedCallback;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;
import pl.agh.capo.controller.collision.velocity.AbstractCollisionFreeVelocity;
import pl.agh.capo.controller.collision.velocity.CollisionFreeVelocityType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CollisionFreeVelocityGenerator implements StateReceivedCallback {

    private final Map<Integer, State> states = new ConcurrentHashMap<>();

    private final int robotId;
    private final WallCollisionDetector wallCollisionDetector;
    private final CollisionFreeVelocityType collisionFreeVelocityType;

    public CollisionFreeVelocityGenerator(CollisionFreeVelocityType collisionFreeVelocityType, int robotId, WallCollisionDetector wallCollisionDetector) {
        this.robotId = robotId;
        this.wallCollisionDetector = wallCollisionDetector;
        this.collisionFreeVelocityType = collisionFreeVelocityType;
    }

    public AbstractCollisionFreeVelocity createCollisionFreeState(Location location, Velocity velocity){
        return collisionFreeVelocityType.create(states, wallCollisionDetector, location, velocity,robotId);
    }

    public AbstractCollisionFreeVelocity createCollisionFreeState(Location location, Velocity velocity,double currentRobotFearFactor){
        return collisionFreeVelocityType.create(states, wallCollisionDetector, location, velocity,robotId,currentRobotFearFactor);
    }
    
    @Override
    public void handle(State state) {
        if (state.getRobotId() == robotId) 
        {
            return;
        }
        if (state.isFinished())
        {
            states.remove(state.getRobotId());
        } 
        else 
        {
            states.put(state.getRobotId(), state);
        }
    }
    
    public Map<Integer, State> GetStates()
    {
    	return states;
    }
}
