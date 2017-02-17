package pl.agh.capo.controller.collision.velocity;

import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;
import pl.agh.capo.controller.collision.WallCollisionDetector;

import java.util.Map;

public enum CollisionFreeVelocityType {

    RIGHT_HAND {
        @Override
        public AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity,int robotId) {
            return new RightSideCollisionFreeVelocity(states, wallCollisionDetector, location, velocity,robotId);
        }
    },
    VELOCITY_OBSTACLES {
        @Override
        public AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity,int robotId) {
            return new VelocityObstaclesCollisionFreeVelocity(states, wallCollisionDetector, location, velocity,robotId);
        }
    },
    	
	RECIPROCAL_VELOCITY_OBSTACLES{
    	   @Override
           public AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity,int robotId) {
               return new ReciprocalVelocityObstaclesCollisionFreeVelocity(states, wallCollisionDetector, location, velocity,robotId);
           }
	};

    public abstract AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity,int robotId);
}
