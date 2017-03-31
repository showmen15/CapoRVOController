package pl.agh.capo.simulation.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.agh.capo.rvo.Vector2;
import pl.agh.capo.utilities.communicationUDP.StatePublisherUDP;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.Point;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;

public class RunSim implements Runnable {

	private StatePublisherUDP statePublisher;
	private SimRVO3 sim;
	Boolean working;
	Map<Integer, State2> AllRobotStates;

	public RunSim(SimRVO3 Sim, Map<Integer, State2> allRobotStates) {
		this.sim = Sim;
		working = true;
		AllRobotStates = allRobotStates;
		
		statePublisher = StatePublisherUDP.createAndEstablishConnection();
	}

	@Override
	public void run() {
		  int robotID = sim.RobotID;
	        int loop = 0;

	        while(working)
	        {
	            if(loop > 40)
	            {
	                int llll = 00;
	            }
	            State2 currentState = AllRobotStates.get(robotID);
	            List<State2> AllState = new ArrayList<State2>();
	            State2 temp;
	            
	            for (State2 item : AllRobotStates.values()) {
	            	AllState.add(item);
				}
	            
	            Vector2 currentVelocity = sim.compute(AllState, currentState.location);

	            currentState.location = Vector2.OpAddition(currentState.location,Vector2.OpMultiply(currentVelocity, sim.timeStep));
	            currentState.velocity = currentVelocity;

	            System.out.println(robotID + "&" + currentVelocity.x() + "&" + currentVelocity.y() + "&" + currentState.location.x() + "&" + currentState.location.y() + "&");
	            
	            //System.out.println(String.format("%d;%d;%d;",  robotID, currentVelocity.x(), currentVelocity.y()));
	            
	           // Console.WriteLine(string.Format("{3}&{0}&{1}&{2}&{4}&{5}&", robotID, currentVelocity.x(), currentVelocity.y(), loop, currentState.location.x(), currentState.location.y()));

	            State state = new State(robotID, new Location(currentState.location.x(), currentState.location.y(), 0), new Velocity(currentVelocity.x(), currentVelocity.y()), new Point(0,0));

	            statePublisher.publishRobotState(state);
	         
	            loop++;
	            try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            // System.Threading.Thread.Sleep(100);
	           // System.Threading.Thread.Sleep(500);
	        }	     
	}
}
