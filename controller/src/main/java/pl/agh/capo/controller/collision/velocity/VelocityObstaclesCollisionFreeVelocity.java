package pl.agh.capo.controller.collision.velocity;

import pl.agh.capo.controller.collision.WallCollisionDetector;
import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;
import pl.agh.capo.velocityobstales.VelocityObstacles;

import java.util.ArrayList;
import java.util.Map;

public class VelocityObstaclesCollisionFreeVelocity extends AbstractCollisionFreeVelocity {

    public VelocityObstaclesCollisionFreeVelocity(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity) {
        super(states, wallCollisionDetector, location, velocity);
    }

    @Override
    protected void buildVelocityObstacles() {
        velocityObstaclesList = new ArrayList<>();
        states.values().forEach(state -> {
            VelocityObstacles vo = buildVelocityObstacles(state);
            if (vo != null) {
                velocityObstaclesList.add(vo);
            }
        });
    }

   /* @Override
    protected Velocity findBestCollisionFreeVelocity() {
        Velocity bestVelocity = null;
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < 500; i++){
            Velocity tmp = generateRandomVelocity();
            double tmpDist = velocity.distance(tmp);
            if (isVelocityCollisionFree(tmp) && isVelocityBetter(minDist, tmpDist, tmp.getSpeed())){
                bestVelocity = tmp;
                minDist = tmpDist;
            }
        }
        if (bestVelocity == null){
            return new Velocity(-velocity.getX(), -velocity.getY());
        }
        return bestVelocity;
    }*/

    @Override
    protected Velocity findBestCollisionFreeVelocity() {
        double step = EnvironmentalConfiguration.PREF_ROBOT_SPEED / 10;
        Velocity bestVelocity = null;
        double minDist = Double.MAX_VALUE;
        double minAngleTo = Double.MAX_VALUE;
        for (double y = EnvironmentalConfiguration.PREF_ROBOT_SPEED / 2; y >= -EnvironmentalConfiguration.PREF_ROBOT_SPEED / 2; y -= step) {
            double maxX = Math.sqrt((EnvironmentalConfiguration.PREF_ROBOT_SPEED * EnvironmentalConfiguration.PREF_ROBOT_SPEED) - (y * y)) / 2;
            for (double x = maxX; x >= -maxX; x -= step) {
                Velocity v = new Velocity(x + velocity.getX(), y + velocity.getY());
                double tmpDist = v.distance(velocity);
                double angleTo = v.toVector2D().angleTo(velocity.toVector2D());
                double speed = v.getSpeed();
                if (isVelocityCollisionFree(v)) {
                    if (isVelocityBetter(minDist, tmpDist, speed)) {
                        bestVelocity = v;
                        minDist = tmpDist;
                        minAngleTo = angleTo;
                    } else if (tmpDist - minDist < 0.01 && minAngleTo > angleTo) {
                        bestVelocity = v;
                        minDist = tmpDist;
                        minAngleTo = angleTo;
                    }
                }
            }
        }
        if (bestVelocity == null) {
            return new Velocity(-velocity.getX(), -velocity.getY());
        }
        return bestVelocity;
    }

/*
    private Velocity generateRandomVelocity(){
        Random random = new Random();
        double x =  (2.0 * EnvironmentalConfiguration.PREF_ROBOT_SPEED * random.nextDouble()) - EnvironmentalConfiguration.PREF_ROBOT_SPEED;
        double maxY = Math.sqrt((EnvironmentalConfiguration.PREF_ROBOT_SPEED * EnvironmentalConfiguration.PREF_ROBOT_SPEED) - (x * x));
        double y = (2.0 * maxY * random.nextDouble()) - maxY;
        return new Velocity(x, y);
    }*/
}