package pl.agh.capo.simulation.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pl.agh.capo.rvo.Vector2;
import pl.agh.capo.utilities.communication.StateReceivedCallback;
import pl.agh.capo.utilities.communicationUDP.StateCollectorUDP;
import pl.agh.capo.utilities.communicationUDP.StatePublisherUDP;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.Point;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;

public class RunSim implements Runnable, StateReceivedCallback {

	private StatePublisherUDP statePublisher;
	private StateCollectorUDP stateCollector;
	private SimRVO3 sim;
	Boolean working;
	Map<Integer, State> states;
	
	Map<Integer, State2> AllRobotStates;
	Vector2 golePosioton;
	
	int RobotID;

	public RunSim(SimRVO3 Sim, Map<Integer, State2> allRobotStates, Vector2 gole) {
		this.sim = Sim;
		working = true;
		AllRobotStates = new ConcurrentHashMap<>(); //allRobotStates;
		
		states = new ConcurrentHashMap<>();
		golePosioton = gole;
		
		RobotID = sim.RobotID;
		
		statePublisher = StatePublisherUDP.createAndEstablishConnection();
		stateCollector = StateCollectorUDP.createAndEstablishConnection(this);
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
	            
	            Vector2 currentVelocity = sim.compute2(golePosioton,states, sim.location);

	            sim.location = Vector2.OpAddition(sim.location,Vector2.OpMultiply(currentVelocity, sim.timeStep));
	            sim.velocity1 = currentVelocity;

	            System.out.println(robotID + "&" + currentVelocity.x() + "&" + currentVelocity.y() + "&" + sim.location.x() + "&" + sim.location.y() + "&");
	            
	            //System.out.println(String.format("%d;%d;%d;",  robotID, currentVelocity.x(), currentVelocity.y()));
	            
	           // Console.WriteLine(string.Format("{3}&{0}&{1}&{2}&{4}&{5}&", robotID, currentVelocity.x(), currentVelocity.y(), loop, currentState.location.x(), currentState.location.y()));

	            State state = new State(robotID, new Location(sim.location.x(), sim.location.y(), 0), new Velocity(currentVelocity.x(), currentVelocity.y()), new Point(0,0));

	            statePublisher.publishRobotState(state);
	         
	            loop++;
	            try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            // System.Threading.Thread.Sleep(100);
	           // System.Threading.Thread.Sleep(500);
	        }	     
	}

	@Override
	public void handle(State state) {
		 if (state.getRobotId() == RobotID) 
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
}
