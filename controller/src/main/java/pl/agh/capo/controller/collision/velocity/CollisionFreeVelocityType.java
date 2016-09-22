package pl.agh.capo.controller.collision.velocity;

import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;
import pl.agh.capo.controller.collision.WallCollisionDetector;

import java.util.Map;

public enum CollisionFreeVelocityType {

    RIGHT_HAND {
        @Override
        public AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity) {
            return new RightSideCollisionFreeVelocity(states, wallCollisionDetector, location, velocity);
        }
    },
    VELOCITY_OBSTACLES {
        @Override
        public AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity) {
            return new VelocityObstaclesCollisionFreeVelocity(states, wallCollisionDetector, location, velocity);
        }
    };

    public abstract AbstractCollisionFreeVelocity create(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity);
}
