package pl.agh.capo.controller.collision.velocity;

import pl.agh.capo.controller.collision.WallCollisionDetector;
import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;

import pl.agh.capo.rvo.*;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import com.vividsolutions.jts.geom.LineSegment;

public class ReciprocalVelocityObstaclesCollisionFreeVelocity extends AbstractCollisionFreeVelocity {

	/////////////// Blocks //////////////////////// Nie testowane
//	private static double neighborDist = 15.0f;
//	private static int maxNeighbors = 10;
//	private static double timeHorizon = 5.0f; // 2.0 //2.5
//	private static double timeHorizonObst = 5.0f;
//	private static double radius = 2.0f;
//	private static double maxSpeed = 2.0f; // 0.25 //0.2
//	private static Vector2 velocityRVO = new Vector2(0.0f, 0.0f);
//	public static double timeStep = 0.25f;

	//////////////////// CAPO ///////////////////////// Nie testowane
//	 private static double neighborDist = 2.3f;
//	 private static int maxNeighbors = 30;
//	 private static double timeHorizon = 2.7f; //2.0 //2.5
//	 private static double timeHorizonObst = 0.5f; //0.0
//	 private static double radius = 0.3f;
//	 private static double maxSpeed = 0.25f; //0.25 //0.2
//	 private static Vector2 velocityRVO = new Vector2(0.0f, 0.0f);
//	 public static double timeStep = 0.2f;

	
	///////////// PrzypadkiDlaOkregu12 ////////////////////////
//	private static double neighborDist = 15.0f;
//	private static int maxNeighbors = 10;
//	private static double timeHorizon = 5.0f; // 2.0 //2.5
//	private static double timeHorizonObst = 5.0f;
//	private static double radius = 0.3f;
//	private static double maxSpeed = 0.2f; // 0.25 //0.2
//	private static Vector2 velocityRVO = new Vector2(0.0f, 0.0f);
//	public static double timeStep = 0.25f;
	
	
	//////////////////// CAPO dla PrzejœciePrzezDrzwi  /////////////////////////
	private static double neighborDist = 2.3f;
	private static int maxNeighbors = 30;
	private static double timeHorizon = 2.7f; 
	private static double timeHorizonObst = 1.5f;
	
	 private static double radius = 0.3f;
	 private static double maxSpeed = 0.25f; 
	 private static Vector2 velocityRVO = new Vector2(0.0f, 0.0f);
	 public static double timeStep = 0.2f;

	 
//		//////////////////// CAPO dla otwarta przestrzen 7x5 /////////////////////////
//	private static double neighborDist = 15.0f;
//	private static int maxNeighbors = 10;
//	private static double timeHorizon = 5.0f; // 2.0 //2.5
//	private static double timeHorizonObst = 5.0f;
//	 private static double radius = 0.3f;
//	 private static double maxSpeed = 0.25f; //0.25 //0.2
//	 private static Vector2 velocityRVO = new Vector2(0.0f, 0.0f);
//	 public static double timeStep = 0.2f;
//	 
	//////////////////// Circle //////////////////// Nie testowane
	// private static double neighborDist = 15.0f;
	// private static int maxNeighbors = 10;
	// private static double timeHorizon = 10.0f;
	// private static double timeHorizonObst = 10.0f;
	// private static double radius = 1.5f;
	// private static double maxSpeed = 2.0f;
	// private static Vector2 velocityRVO = new Vector2(0.0f, 0.0f);
	// public static double timeStep = 0.25f;

	private List<Agent> agents_;
	private List<Obstacle> obstacles_;
	private KdTree kdTree_;

	private Agent CurrentAgent;

	private Random random;

	public ReciprocalVelocityObstaclesCollisionFreeVelocity(Map<Integer, State> states,
			WallCollisionDetector wallCollisionDetector, Location location, Velocity velocity, int robotId) {

		super(states, wallCollisionDetector, location, velocity, robotId);
	}

	@Override
	protected void buildVelocityObstacles() {

		initObstacle();
		List<Agent> agents = getAgents();
		random = new Random();
		CurrentAgent = createAgent(RobotID);
		
		if (agents.size() > 0) {

			CurrentAgent.position_ = new Vector2(location.getX(), location.getY());
			setAgentPrefVelocity(new Vector2(velocity.getX(), velocity.getY()));
			
			agents_ = agents;
			kdTree_ = new KdTree();
			kdTree_.buildAgentTree(agents_);
			kdTree_.buildObstacleTree(obstacles_);

			CurrentAgent.computeNeighbors(kdTree_); // Simulator.Instance.agents_[agentNo].computeNeighbors();
			CurrentAgent.computeNewVelocity(); // Simulator.Instance.agents_[agentNo].computeNewVelocity();	
			
			CurrentAgent.update();
		}
		else
			CurrentAgent.velocity_ = new Vector2(velocity.getX(), velocity.getY());
		
		
		// currentAgent = createAgent(RobotID);
		//
		// Vector2 currentLocation = new Vector2(location.getX(),
		// location.getY());
		// Vector2 currentVelocity = new
		// Vector2(velocity.getX(),velocity.getY());
		//
		// List<Agent> tmpAgents = getAgents();
		//
		// double rangeSq = RVOMath.sqr(currentAgent.timeHorizonObst_ *
		// currentAgent.maxSpeed_ + currentAgent.radius_);
		// List<Obstacle> tmpObstacle = new ArrayList<Obstacle>();
		// //getObstacles(currentLocation,rangeSq);
		//
		// currentAgent.position_ = currentLocation;
		// currentAgent.prefVelocity_ = currentVelocity;
		//
		// currentAgent.computeNeighbors(tmpAgents, tmpObstacle);
		// currentAgent.computeNewVelocity();
	}

	private void setAgentPrefVelocity(Vector2 currentVelocity) {
		CurrentAgent.prefVelocity_ = currentVelocity;

		/* Perturb a little to avoid deadlocks due to perfect symmetry. */
		double angle = (double) random.nextDouble() * 2.0f * (double) Math.PI;
		double dist = (double) random.nextDouble() * 0.0001f;

		CurrentAgent.prefVelocity_ = Vector2.OpAddition(CurrentAgent.prefVelocity_,
				Vector2.OpMultiply(dist, new Vector2((double) Math.cos(angle), (double) Math.sin(angle))));
	}

	private void initObstacle() {
		obstacles_ = new ArrayList<Obstacle>();
		List<Vector2> vert;

		for (LineSegment wall : this.wallCollisionDetector.getWallLineSegments()) 
		{
			vert = createObstacle(wall.p0.x,wall.p0.y,wall.p1.x,wall.p1.y);
			addObstacle(vert);
		}
	}

	   private  List<Vector2> createObstacle(double x_begin,double y_begin,double x_end, double y_end)
       {
           double x_max = Math.max(x_begin, x_end);
           double y_max = Math.max(y_begin, y_end);

           double x_min = Math.min(x_begin, x_end);
           double y_min = Math.min(y_begin, y_end);

           List<Vector2> temp = new ArrayList<Vector2>();
           
           temp.add(new Vector2(x_min, y_max));
           temp.add(new Vector2(x_min, y_min));
           temp.add(new Vector2(x_max, y_min));
           temp.add(new Vector2(x_max, y_max));
           
           return temp;
       }
	
	@Override
	public boolean isCurrentVelocityCollisionFree() {

		return !CurrentAgent.IsCollide();
	}

	@Override
	protected Velocity findBestCollisionFreeVelocity() {

		return new Velocity(CurrentAgent.velocity_.x(), CurrentAgent.velocity_.y());
	}

	private Agent createAgent(int id) {
		Agent agent = new Agent();
		agent.id_ = id;

		agent.position_ = new Vector2(0, 0);

		agent.maxNeighbors_ = maxNeighbors;
		agent.maxSpeed_ = maxSpeed;
		agent.neighborDist_ = neighborDist;
		agent.radius_ = radius;
		agent.timeHorizon_ = timeHorizon;
		agent.timeHorizonObst_ = timeHorizonObst;
		agent.velocity_ = velocityRVO;

		agent.timeStep_ = timeStep;

		return agent;
	}

	private List<Agent> getAgents() {
		List<Agent> temp = new ArrayList<Agent>();

		for (State state : states.values())
			temp.add(createAgent(state));

		return temp;
	}

	private Agent createAgent(State state) {
		Agent temp = createAgent(state.getRobotId());

		temp.position_ = new Vector2(state.getLocation().getX(), state.getLocation().getY());
		temp.velocity_ = new Vector2(state.getVelocity().getX(), state.getVelocity().getY());

		return temp;
	}

	/**
	 * <summary>Adds a new obstacle to the simulation.</summary>
	 *
	 * <returns>The number of the first vertex of the obstacle, or -1 when the
	 * number of vertices is less than two.</returns>
	 *
	 * <param name="vertices">List of the vertices of the polygonal obstacle in
	 * counterclockwise order.</param>
	 *
	 * <remarks>To add a "negative" obstacle, e.g. a bounding polygon around the
	 * environment, the vertices should be listed in clockwise order. </remarks>
	 */
	public int addObstacle(List<Vector2> vertices) {
		if (vertices.size() < 2) {
			return -1;
		}

		int obstacleNo = obstacles_.size();

		for (int i = 0; i < vertices.size(); ++i) {
			Obstacle obstacle = new Obstacle();
			obstacle.point_ = vertices.get(i);

			if (i != 0) {
				obstacle.previous_ = obstacles_.get(obstacles_.size() - 1);
				obstacle.previous_.next_ = obstacle;
			}

			if (i == vertices.size() - 1) {
				obstacle.next_ = obstacles_.get(obstacleNo);
				obstacle.next_.previous_ = obstacle;
			}

			obstacle.direction_ = RVOMath.normalize(
					Vector2.OpSubtraction(vertices.get((i == vertices.size() - 1 ? 0 : i + 1)), vertices.get(i)));

			if (vertices.size() == 2) {
				obstacle.convex_ = true;
			} else {
				obstacle.convex_ = (RVOMath.leftOf(vertices.get((i == 0 ? vertices.size() - 1 : i - 1)),
						vertices.get(i), vertices.get((i == vertices.size() - 1 ? 0 : i + 1))) >= 0.0f);
			}

			obstacle.id_ = obstacles_.size();
			obstacles_.add(obstacle);
		}

		return obstacleNo;
	}

	// /*public int addObstacle(List<Vector2> vertices)
	// {
	//
	//
	// if (vertices.size() < 2)
	// {
	// return -1;
	// }
	//
	// int obstacleNo = walls.Count;
	//
	// for (int i = 0; i < vertices.Count; ++i)
	// {
	// Obstacle obstacle = new Obstacle();
	// obstacle.point_ = vertices[i];
	//
	// if (i != 0)
	// {
	// obstacle.previous_ = walls[walls.Count - 1];
	// obstacle.previous_.next_ = obstacle;
	// }
	//
	// if (i == vertices.Count - 1)
	// {
	// obstacle.next_ = walls[obstacleNo];
	// obstacle.next_.previous_ = obstacle;
	// }
	//
	// obstacle.direction_ = RVOMath.normalize(vertices[(i == vertices.Count - 1
	// ? 0 : i + 1)] - vertices[i]);
	//
	// if (vertices.Count == 2)
	// {
	// obstacle.convex_ = true;
	// }
	// else
	// {
	// obstacle.convex_ = (RVOMath.leftOf(vertices[(i == 0 ? vertices.Count - 1
	// : i - 1)], vertices[i], vertices[(i == vertices.Count - 1 ? 0 : i + 1)])
	// >= 0.0f);
	// }
	//
	// obstacle.id_ = walls.Count;
	// walls.Add(obstacle);
	//
	// if (i == 0)
	// obstacles.Add(obstacle);
	// }
	//
	// return obstacleNo;
	// }*/
	//
	// private List<Obstacle> getObstacles(Vector2 currentlocation, double
	// rangeSq)
	// {
	// List<Obstacle> temp = new ArrayList<Obstacle>();
	// List<Obstacle> walls = getWalls();
	//
	// for (int i = 0; i < walls.size(); i++)
	//
	// {
	// Obstacle obstacle1 = walls.get(i);
	// Obstacle obstacle2 = obstacle1.next_;
	//
	// double agentLeftOfLine = RVOMath.leftOf(obstacle1.point_,
	// obstacle2.point_, currentlocation);
	//
	//
	// double distSqLine = RVOMath.sqr(agentLeftOfLine) /
	// RVOMath.absSq(Vector2.OpSubtraction(obstacle2.point_, obstacle1.point_));
	//
	// if (distSqLine < rangeSq)
	// {
	// if (agentLeftOfLine < 0.0f)
	// {
	// temp.add(walls.get(i));
	//
	// }
	// }
	//
	// }
	//
	// return temp;
	// }
	//
	// private List<Obstacle> getWalls()
	// {
	// return null;
	// }
	//
	//// private double round(double r)
	//// {
	//// return r;
	//// /*if(Math.abs(r) < 0.000001)
	//// return 0;
	//// else
	//// return Math.round(r * 10000000.0) / 10000000.0;*/
	//// }

}
