package pl.agh.capo.simulation.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import pl.agh.capo.controller.collision.velocity.ReciprocalVelocityObstaclesCollisionFreeVelocity;
import pl.agh.capo.rvo.Agent;
import pl.agh.capo.rvo.KdTree;
import pl.agh.capo.rvo.Obstacle;
import pl.agh.capo.rvo.RVOMath;
import pl.agh.capo.rvo.Vector2;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.Point;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;

public class SimRVO3 {

	 private float neighborDist = 5.0f; //
     private int maxNeighbors = 10; //
     private float timeHorizon = 1.5f;
     private float timeHorizonObst = 1.5f;
     private float radius = 0.3f;
     private float maxSpeed = 0.25f;
     private Vector2 velocity = new Vector2(0.0f, 0.0f);
     public float timeStep = 0.2f;
     
//     private List<Agent> agents_;
//     private List<Obstacle> obstacles_;
//     private KdTree kdTree_;

     private Agent CurrentAgent;
     //private Vector2 GoalAgent;

     private Random random = new Random();

     public int RobotID;
     
     public Vector2 location;
     public Vector2 velocity1;
     
     private ReciprocalVelocityObstaclesCollisionFreeVelocity rvo;

     public SimRVO3(int robotID, List<List<Vector2>> obst)
     {
         
        CurrentAgent = createAgent(robotID);
         RobotID = robotID;
         //obstacles_ = new ArrayList<Obstacle>();
                   
         rvo = new ReciprocalVelocityObstaclesCollisionFreeVelocity(null, null, RobotID);
        
         
//         for (List<Vector2> item : obst) {
//        	 addObstacle(item); 
//		}
     }
     
     public Vector2 compute2(Vector2 goalAgent,Map<Integer, State> st, Vector2 currentPosition)
     {
    	 
 	 Velocity goalVector = new Velocity(goalAgent.x() - currentPosition.x(), goalAgent.y() - currentPosition.y());  
    	 
    	 Vector2 rvo1 = rvo.compute(st.values(), currentPosition,goalVector);
    	
    	 
    	 return rvo1;
    	 
//    	List<State2> copy = new ArrayList<>();
//    	State2 tt;
//    	
//    	for (State2 state2 : sate) {
//    		tt = new State2();
//    		tt.finished = state2.finished;
//    		tt.location = state2.location;
//    		tt.robotFearFactor = state2.robotFearFactor;
//    		tt.robotId = state2.robotId;
//    		tt.velocity = state2.velocity;
//    		
//    		copy.add(tt);
//    	}
//    	
//    	sate = copy;
//    	 
//    	 Map<Integer, State> st = new ConcurrentHashMap<Integer, State>();
//    	// List<State> list = new ArrayList<>();
//    	 
//    	 for (State2 state2 : sate) {
//    		 State temp = new State(state2.robotId, new Location(state2.location.x(), state2.location.y(), 0),
//    				      new Velocity(state2.velocity.x(), state2.velocity.y()),new Point(goalAgent.x(), goalAgent.y()));
//    	
//			st.put(state2.robotId, temp);
//			//list.add(temp);
//		}
    	 
    	 
    	// rvo = new ReciprocalVelocityObstaclesCollisionFreeVelocity(st, null, RobotID);
    	 
    	 //goalVector =  Vector2.OpSubtraction(GoalAgent,CurrentAgent.position_);
    	 
   
     }
     
     public Vector2 compute(Vector2 goalAgent,List<State2> sate, Vector2 currentPosition)
     {
    	List<State2> copy = new ArrayList<>();
    	State2 tt;
    	
    	for (State2 state2 : sate) {
    		tt = new State2();
    		tt.finished = state2.finished;
    		tt.location = state2.location;
    		tt.robotFearFactor = state2.robotFearFactor;
    		tt.robotId = state2.robotId;
    		tt.velocity = state2.velocity;
    		
    		copy.add(tt);
    	}
    	
    	sate = copy;
    	 
    	 Map<Integer, State> st = new ConcurrentHashMap<Integer, State>();
    	// List<State> list = new ArrayList<>();
    	 
    	 for (State2 state2 : sate) {
    		 State temp = new State(state2.robotId, new Location(state2.location.x(), state2.location.y(), 0),
    				      new Velocity(state2.velocity.x(), state2.velocity.y()),new Point(goalAgent.x(), goalAgent.y()));
    	
			st.put(state2.robotId, temp);
			//list.add(temp);
		}
    	 
    	 
    	// rvo = new ReciprocalVelocityObstaclesCollisionFreeVelocity(st, null, RobotID);
    	 
    	 //goalVector =  Vector2.OpSubtraction(GoalAgent,CurrentAgent.position_);
    	 
    	 Velocity goalVector = new Velocity(goalAgent.x() - currentPosition.x(), goalAgent.y() - currentPosition.y());  
    	 
    	 Vector2 rvo1 = rvo.compute(st.values(), currentPosition,goalVector);
    	  
    	  
    	 
    	/*   List<Agent> agents_;
    	 List<Obstacle> obstacles_;
    	     KdTree kdTree_;

//    	     Agent CurrentAgent;
    	     Vector2 GoalAgent;
    	 
//         CurrentAgent = createAgent(RobotID);
         obstacles_ = new ArrayList<Obstacle>();
    	 
    	 
    	 GoalAgent = goalAgent;
    	 List<Agent> agents = getAgents(sate);

         //Agent tempAgent = createAgent(CurrentAgent);
         //CurrentAgent = tempAgent;
         CurrentAgent.position_ = currentPosition;

         agents.add(CurrentAgent);

         agents_ = agents;
         kdTree_ = new KdTree();
         kdTree_.buildAgentTree(agents_);
         kdTree_.buildObstacleTree(obstacles_);

         setAgentPrefVelocity(CurrentAgent,GoalAgent);

         CurrentAgent.computeNeighbors(kdTree_); //Simulator.Instance.agents_[agentNo].computeNeighbors();
         CurrentAgent.computeNewVelocity(); //Simulator.Instance.agents_[agentNo].computeNewVelocity();

         CurrentAgent.update();

         if(RobotID == 0 && CurrentAgent.velocity_.y_ > 0)
         {
             int stop = 3232;
         }

         //Console.WriteLine(string.Format("{0}&{1}&{2}&", RobotID, CurrentAgent.velocity_.x_, CurrentAgent.velocity_.y_));

         Vector2 rvo2 = CurrentAgent.velocity_;
         
         
         if(rvo1.x_ != rvo2.x() || rvo1.y_ != rvo2.y())
         {
        	 int tr = 0;
        	 
        	 tr+= 1312;
        	 
         }
         
         */
         return rvo1;
     }
     
     private void setAgentPrefVelocity(Agent CurrentAgent, Vector2 GoalAgent)
     {
         Vector2 goalVector =  Vector2.OpSubtraction(GoalAgent,CurrentAgent.position_); // Simulator.Instance.getAgentPosition(i);

         if (RVOMath.absSq(goalVector) > 1.0f)
         {
             goalVector = RVOMath.normalize(goalVector);
         }

         CurrentAgent.prefVelocity_ = goalVector; // Simulator.Instance.setAgentPrefVelocity(i, goalVector);

         /* Perturb a little to avoid deadlocks due to perfect symmetry. */
//         float angle = (float)random.nextDouble() * 2.0f * (float)Math.PI;
//         float dist = (float)random.nextDouble() * 0.0001f;
//
//         CurrentAgent.prefVelocity_ = Vector2.OpAddition(CurrentAgent.prefVelocity_,Vector2.OpMultiply(dist,new Vector2((float)Math.cos(angle), (float)Math.sin(angle))));
     }
     
     private Agent createAgent(int id)
     {
         Agent agent = new Agent();
         agent.id_ = id;

         agent.position_ = new Vector2(0, 0);

         agent.maxNeighbors_ = maxNeighbors;
         agent.maxSpeed_ = maxSpeed;
         agent.neighborDist_ = neighborDist;
         agent.radius_ = radius;
         agent.timeHorizon_ = timeHorizon;
         agent.timeHorizonObst_ = timeHorizonObst;
         agent.velocity_ = velocity;

         agent.timeStep_ = timeStep;

         return agent;
     } //
     
     
     private List<Agent> getAgents(List<State2> sates)//
     {
         List<Agent> temp = new ArrayList<Agent>();//
         
         for(int i = 0; i <sates.size();i++)
         {
        	 State2 item = sates.get(i);
        	 
             if (item.robotId == RobotID)
                 continue;
             else
             temp.add(createAgent(item));//
         }         

         return temp;//
     }
     
     private Agent createAgent(State2 state)//
     {
         Agent temp = createAgent(state.robotId);//

         temp.position_ = state.location;//
         temp.velocity_ = state.velocity;//

         return temp;//
     }
     
     private Agent createAgent(Agent agent)
     {
         Agent temp = createAgent(agent.id_);

         temp.position_ = agent.position_;
         temp.velocity_ = agent.velocity_;

         return temp;
     }
	
}
