package pl.agh.capo.simulation.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.agh.capo.rvo.Agent;
import pl.agh.capo.rvo.KdTree;
import pl.agh.capo.rvo.Obstacle;
import pl.agh.capo.rvo.RVOMath;
import pl.agh.capo.rvo.Vector2;

public class SimRVO3 {

	 private float neighborDist = 5.0f; //
     private int maxNeighbors = 10; //
     private float timeHorizon = 1.5f;
     private float timeHorizonObst = 1.5f;
     private float radius = 0.3f;
     private float maxSpeed = 0.25f;
     private Vector2 velocity = new Vector2(0.0f, 0.0f);
     public float timeStep = 0.2f;
     
     private List<Agent> agents_;
     private List<Obstacle> obstacles_;
     private KdTree kdTree_;

     private Agent CurrentAgent;
     private Vector2 GoalAgent;

     private Random random = new Random();

     public int RobotID;

     public SimRVO3(Vector2 goalAgent, int robotID, List<List<Vector2>> obst)
     {
         GoalAgent = goalAgent;
         CurrentAgent = createAgent(robotID);
         RobotID = robotID;
         obstacles_ = new ArrayList<Obstacle>();
                   
         
//         for (List<Vector2> item : obst) {
//        	 addObstacle(item); 
//		}
     }
     
     public Vector2 compute(List<State2> sate, Vector2 currentPosition)
     {
    	 List<Agent> agents = getAgents(sate);

         Agent tempAgent = createAgent(CurrentAgent);
         CurrentAgent = tempAgent;
         CurrentAgent.position_ = currentPosition;

         agents.add(CurrentAgent);

         agents_ = agents;
         kdTree_ = new KdTree();
         kdTree_.buildAgentTree(agents_);
         kdTree_.buildObstacleTree(obstacles_);

         setAgentPrefVelocity();

         CurrentAgent.computeNeighbors(kdTree_); //Simulator.Instance.agents_[agentNo].computeNeighbors();
         CurrentAgent.computeNewVelocity(); //Simulator.Instance.agents_[agentNo].computeNewVelocity();

         CurrentAgent.update();

         if(RobotID == 0 && CurrentAgent.velocity_.y_ > 0)
         {
             int stop = 3232;
         }

         //Console.WriteLine(string.Format("{0}&{1}&{2}&", RobotID, CurrentAgent.velocity_.x_, CurrentAgent.velocity_.y_));

         return CurrentAgent.velocity_;
     }
     
     private void setAgentPrefVelocity()
     {
         Vector2 goalVector =  Vector2.OpSubtraction(GoalAgent,CurrentAgent.position_); // Simulator.Instance.getAgentPosition(i);

         if (RVOMath.absSq(goalVector) > 1.0f)
         {
             goalVector = RVOMath.normalize(goalVector);
         }

         CurrentAgent.prefVelocity_ = goalVector; // Simulator.Instance.setAgentPrefVelocity(i, goalVector);

         /* Perturb a little to avoid deadlocks due to perfect symmetry. */
         float angle = (float)random.nextDouble() * 2.0f * (float)Math.PI;
         float dist = (float)random.nextDouble() * 0.0001f;

         CurrentAgent.prefVelocity_ = Vector2.OpAddition(CurrentAgent.prefVelocity_,Vector2.OpMultiply(dist,new Vector2((float)Math.cos(angle), (float)Math.sin(angle))));
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
