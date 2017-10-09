package pl.agh.capo.utilities.state;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;

public class State implements Serializable {

    private int robotId;
    private Location location;
    private Velocity velocity;
    private boolean finished;
    private double robotFearFactor;
    private Timestamp timeStemp;

    // To pl.agh.capo.simulation.visualization only:
    private Point destination;

    private State(){}

    public State(int robotId, Location location, Velocity velocity, Point destination) {
        this.robotId = robotId;
        this.location = location;
        this.velocity = velocity;
        this.destination = destination;
        this.finished = false;
        this.robotFearFactor = 0.0;
        this.timeStemp = new Timestamp(System.currentTimeMillis());
    }

    public State(String sLogState)
    {
    	destination = new Point(0, 0);
    	
    	String[] sSplitedState =  sLogState.split(";");
    	
    	robotId = Integer.parseInt(sSplitedState[0]); //robotId
    	//sSplitedState[1] sensorReadCounter
    	 	
    	location = new Location(Double.parseDouble(sSplitedState[2]),Double.parseDouble(sSplitedState[3]), Double.parseDouble(sSplitedState[4])); //currentRobotState.getLocation().getX(),currentRobotState.getLocation().getY(), currentRobotState.getLocation().getDirection()
    	velocity = new Velocity(Double.parseDouble(sSplitedState[5]), Double.parseDouble(sSplitedState[6])); //currentRobotState.getVelocity().getX(), currentRobotState.getVelocity().getY()
    	robotFearFactor = Double.parseDouble(sSplitedState[7]); //currentRobotState.getRobotFearFactor()
    	
    	if(sSplitedState.length > 8)
    	{
    		destination = new Point(Double.parseDouble(sSplitedState[8]), Double.parseDouble(sSplitedState[9])); //currentRobotState.getDestination().getX(), currentRobotState.getDestination().getY()
    		finished = Boolean.parseBoolean(sSplitedState[10]); //currentRobotState.isFinished()
    			
    		timeStemp = new Timestamp(Long.parseLong(sSplitedState[11])); //currentRobotState.getTimeStemp()    				
    	}
    	else
    	{
    		timeStemp = null;
    	}
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

    public void setFinished(){
        finished = true;
    }

    private void setRobotId(int robotId) {
        this.robotId = robotId;
    }
    
    public double getRobotFearFactor(){
    	return robotFearFactor;
    }
    
    public void setRobotFearFactor(double robotFearFactor){
    	this.robotFearFactor =  robotFearFactor;
    }
    
    public long getTimeStemp()
    {
    	return timeStemp.getTime();
    }
    
    public void setTimeStemp(long time)
    {
    	if(timeStemp == null)
    		timeStemp = new Timestamp(time);
    }
}

