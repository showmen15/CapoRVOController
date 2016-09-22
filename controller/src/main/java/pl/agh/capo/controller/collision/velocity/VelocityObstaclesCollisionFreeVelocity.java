package pl.agh.capo.controller.collision.velocity;

import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;
import pl.agh.capo.controller.collision.WallCollisionDetector;
import pl.agh.capo.velocityobstales.VelocityObstacles;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class VelocityObstaclesCollisionFreeVelocity extends AbstractCollisionFreeVelocity {

    public VelocityObstaclesCollisionFreeVelocity(Map<Integer, State> states, WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity) {
        super(states, wallCollisionDetector, location, velocity);
    }

    @Override
    protected void buildVelocityObstacles() {
        velocityObstaclesList = new ArrayList<>();
        states.values().stream().forEach(state -> {
            VelocityObstacles vo = buildVelocityObstacles(state);
            if (vo != null) {
                velocityObstaclesList.add(vo);
            }
        });
    }

    /*@Override
    protected Velocity findBestCollisionFreeVelocity() {
        Velocity bestVelocity = null;
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < EnvironmentalConfiguration.TRIES_COUNT; i++){
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
        for (double y = EnvironmentalConfiguration.PREF_ROBOT_SPEED; y >= -EnvironmentalConfiguration.PREF_ROBOT_SPEED; y -= step) {
            double maxX = Math.sqrt((EnvironmentalConfiguration.PREF_ROBOT_SPEED * EnvironmentalConfiguration.PREF_ROBOT_SPEED) - (y * y));
            for (double x = maxX; x >= -maxX; x -= step) {
                Velocity v = new Velocity(x, y);
                double tmpDist = v.distance(velocity);
                if (isVelocityCollisionFree(v) && isVelocityBetter(minDist, tmpDist, v.getSpeed())){
                    bestVelocity = v;
                    minDist = tmpDist;
                }
            }
        }
        if (bestVelocity == null){
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
