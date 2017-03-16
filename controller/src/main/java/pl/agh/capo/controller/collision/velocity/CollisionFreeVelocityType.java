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

		@Override
		public AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity, int robotId, double currentRobotFearFactor) {
			return null;
		}
    },
    VELOCITY_OBSTACLES {
        @Override
        public AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity,int robotId) {
            return new VelocityObstaclesCollisionFreeVelocity(states, wallCollisionDetector, location, velocity,robotId);
        }
        
		@Override
		public AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity, int robotId, double currentRobotFearFactor) {
			return null;
		}
    },
    	
	RECIPROCAL_VELOCITY_OBSTACLES{
    	   @Override
           public AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity,int robotId) {
               return new ReciprocalVelocityObstaclesCollisionFreeVelocity(states, wallCollisionDetector, location, velocity,robotId);
           }
    	   
   		@Override
   		public AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity, int robotId, double currentRobotFearFactor) {
   			return null;
   		}
    },
           
    RECIPROCAL_VELOCITY_OBSTACLES_FEAR_FACTOR{
   		
    	@Override
		public AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity, int robotId) {
			return null;
		}
    	
    	@Override
               public AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity,int robotId, double currentRobotFearFactor) 
        	   {
                   return new ReciprocalVelocityObstaclesCollisionFreeVelocity(states, wallCollisionDetector, location, velocity,robotId, currentRobotFearFactor);
               }
           };
    	   

    public abstract AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity,int robotId);

    public abstract AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity,int robotId, double currentRobotFearFactor);
}
