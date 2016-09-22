package pl.agh.capo.utilities.state;

import java.io.Serializable;

public class State implements Serializable {

    private int robotId;
    private Location location;
    private Velocity velocity;
    private boolean finished;

    // To pl.agh.capo.simulation.visualization only:
    private Point destination;

    private State(){}

    public State(int robotId, Location location, Velocity velocity, Point destination) {
        this.robotId = robotId;
        this.location = location;
        this.velocity = velocity;
        this.destination = destination;
        this.finished = false;
    }

    public static State createFinished(int robotId){
        State state = new State();
        state.setRobotId(robotId);
        state.setFinished();
        return state;
    }

    public int getRobotId() {
        return robotId;
    }

    public Location getLocation() {
        return location;
    }

    public Velocity getVelocity() {
        return velocity;
    }

    public Point getDestination() {
        return destination;
    }

    public boolean isFinished(){
        return finished;
    }

    private void setFinished(){
        finished = true;
    }

    private void setRobotId(int robotId) {
        this.robotId = robotId;
    }
}
