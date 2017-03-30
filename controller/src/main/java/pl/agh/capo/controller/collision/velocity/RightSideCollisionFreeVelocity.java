package pl.agh.capo.controller.collision.velocity;

import com.vividsolutions.jts.math.Vector2D;
import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;
import pl.agh.capo.controller.collision.WallCollisionDetector;
import pl.agh.capo.velocityobstales.VelocityObstacles;

import java.util.ArrayList;
import java.util.Map;

public class RightSideCollisionFreeVelocity extends AbstractCollisionFreeVelocity {

    public RightSideCollisionFreeVelocity(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, int robotId) {
        super(states, wallCollisionDetector,robotId);
    }
    
    @Override
    protected void buildVelocityObstacles() {
        velocityObstaclesList = new ArrayList<>();
        states.values().stream().filter(this::collisionDanger).forEach(state -> {
            VelocityObstacles vo = buildVelocityObstacles(state);
            if (vo != null) {
                velocityObstaclesList.add(vo);
            }
        });
    }

    @Override
	protected void buildVelocityObstacles(double currentRobotFearFactor) {
    	
    }
    
    @Override
    protected Velocity findBestCollisionFreeVelocity() {
        double alpha = Math.atan2(velocity.getY(), velocity.getX());
        double cos = Math.cos(alpha);
        double sin = Math.sin(alpha);
        double step = findStep();

        Velocity v = findVelocityRightForward(sin, cos, step);
        if (v == null){
            v = findVelocityRightBack(sin, cos, Math.sqrt(2.0) * step);
        }
        if (v == null) {
            v = findVelocityLeftForward(sin, cos, 2.0 * step);
        }
        if (v == null) {
            v = findVelocityLeftBack(sin, cos, 2.0 * step);
        }
        if (v != null){
            return v;
        }
        logger.debug("DANGER STOP");
        return new Velocity(0.0, 0.0);
    }

    private Velocity findVelocityRightForward(double sin, double cos, double step) {
        Velocity best = null;
        double bestDist = Double.MAX_VALUE;
        for (double y = EnvironmentalConfiguration.PREF_ROBOT_SPEED; y >= 0.0; y -= step) {
            double maxX = Math.sqrt((EnvironmentalConfiguration.PREF_ROBOT_SPEED * EnvironmentalConfiguration.PREF_ROBOT_SPEED) - (y * y));
            for (double x = 0.0; x <= maxX; x += step) {
                Velocity v = rotateVelocity(x, y, sin, cos);
                double tmpDist = v.distance(velocity);
                if (isVelocityCollisionFree(v) && isVelocityBetter(bestDist, tmpDist, v.getSpeed())) {
                    best = v;
                    bestDist = tmpDist;
                }
            }
        }
        return best;
    }

    private Velocity findVelocityRightBack(double sin, double cos, double step) {
        Velocity best = null;
        double bestDist = Double.MAX_VALUE;
        for (double y = EnvironmentalConfiguration.PREF_ROBOT_SPEED; y >= 0.0; y -= step) {
            double maxX = Math.sqrt((EnvironmentalConfiguration.PREF_ROBOT_SPEED * EnvironmentalConfiguration.PREF_ROBOT_SPEED) - (y * y));
            for (double x = 0.0; x >= -maxX; x -= step) {
                Velocity v = rotateVelocity(x, y, sin, cos);
                double tmpDist = v.distance(velocity);
                if (isVelocityCollisionFree(v) && isVelocityBetter(bestDist, tmpDist, v.getSpeed())) {
                    best = v;
                    bestDist = tmpDist;
                }
            }
        }
        return best;
    }

    private Velocity findVelocityLeftForward(double sin, double cos, double step) {
        Velocity best = null;
        double bestDist = Double.MAX_VALUE;
        for (double y = 0.0; y >= -EnvironmentalConfiguration.PREF_ROBOT_SPEED; y -= step) {
            double maxX = Math.sqrt((EnvironmentalConfiguration.PREF_ROBOT_SPEED * EnvironmentalConfiguration.PREF_ROBOT_SPEED) - (y * y));
            for (double x = 0.0; x <= maxX; x += step) {
                Velocity v = rotateVelocity(x, y, sin, cos);
                double tmpDist = v.distance(velocity);
                if (isVelocityCollisionFree(v) && isVelocityBetter(bestDist, tmpDist, v.getSpeed())) {
                    best = v;
                    bestDist = tmpDist;
                }
            }
        }
        return best;
    }

    private Velocity findVelocityLeftBack(double sin, double cos, double step) {
        Velocity best = null;
        double bestDist = Double.MAX_VALUE;
        for (double y = 0.0; y >= -EnvironmentalConfiguration.PREF_ROBOT_SPEED; y -= step) {
            double maxX = Math.sqrt((EnvironmentalConfiguration.PREF_ROBOT_SPEED * EnvironmentalConfiguration.PREF_ROBOT_SPEED) - (y * y));
            for (double x = 0.0; x >= -maxX; x -= step) {
                Velocity v = rotateVelocity(x, y, sin, cos);
                double tmpDist = v.distance(velocity);
                if (isVelocityCollisionFree(v) && isVelocityBetter(bestDist, tmpDist, v.getSpeed())) {
                    best = v;
                    bestDist = tmpDist;
                }
            }
        }
        return best;
    }

    private double findStep(){
        return (Math.sqrt(Math.PI) * EnvironmentalConfiguration.PREF_ROBOT_SPEED) / 20.0;
    }

    private Velocity rotateVelocity(double x, double y, double sin, double cos) {
        return new Velocity(x * cos - y * sin, x * sin + y * cos);
    }

    private boolean collisionDanger(State state) {
        double distance = state.getLocation().distance(location);
        Reciprocity reciprocity = reciprocity(state);
        return reciprocity.danger(distance);
    }

    private Reciprocity reciprocity(State state) {
        Vector2D currentVector = velocity.toVector2D();
        Vector2D foreignVector = state.getVelocity().toVector2D();
        double resultant = getNormalizeAngleTo(currentVector, foreignVector);

        if (resultant > 355.0) {
            return Reciprocity.BEHIND;
        } else if (resultant > 195.0) {
            return Reciprocity.SUBORDINATED;
        } else if (resultant > 175.0) {
            return Reciprocity.OPPOSITE;
        } else if (resultant > 5.0) {
            return Reciprocity.PRIMARY;
        } else {
            return Reciprocity.BEHIND;
        }
    }

    private double getNormalizeAngleTo(Vector2D currentVector, Vector2D foreignVector) {
        double resultant = Math.toDegrees(foreignVector.angle() - currentVector.angle());
        if (resultant < 0.0) {
            resultant += 360.0;
        }
        return resultant;
    }

    private enum Reciprocity {
        OPPOSITE(EnvironmentalConfiguration.RECIPROCITY_FACTOR_OPPOSITE * EnvironmentalConfiguration.ROBOT_MAX_SPEED),
        PRIMARY(EnvironmentalConfiguration.RECIPROCITY_FACTOR_PRIMARY * EnvironmentalConfiguration.ROBOT_MAX_SPEED),
        SUBORDINATED(EnvironmentalConfiguration.RECIPROCITY_FACTOR_SUBORDINATED * EnvironmentalConfiguration.ROBOT_MAX_SPEED),
        BEHIND(EnvironmentalConfiguration.RECIPROCITY_FACTOR_BEHIND * EnvironmentalConfiguration.ROBOT_MAX_SPEED);

        private double collisionDangerZoneRadius;

        Reciprocity(double collisionDangerZoneRadius) {
            this.collisionDangerZoneRadius = collisionDangerZoneRadius;
        }

        public boolean danger(double distance) {
            return distance < collisionDangerZoneRadius;
        }
    }
}
