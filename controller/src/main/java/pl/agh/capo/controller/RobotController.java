package pl.agh.capo.controller;

import com.vividsolutions.jts.math.Vector2D;
import org.apache.log4j.Logger;
import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.communication.StateCollector;
import pl.agh.capo.utilities.communication.StatePublisher;
import pl.agh.capo.utilities.communication.StateReceivedCallback;
import pl.agh.capo.utilities.communicationUDP.StateCollectorUDP;
import pl.agh.capo.utilities.communicationUDP.StatePublisherUDP;
import pl.agh.capo.utilities.maze.Gate;
import pl.agh.capo.utilities.maze.MazeMap;
import pl.agh.capo.utilities.state.Destination;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.Point;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;
import pl.agh.capo.controller.collision.CollisionFreeVelocityGenerator;
import pl.agh.capo.controller.collision.WallCollisionDetector;
import pl.agh.capo.controller.collision.velocity.AbstractCollisionFreeVelocity;
import pl.agh.capo.controller.collision.velocity.CollisionFreeVelocityType;
import pl.agh.capo.controller.collision.velocity.FearMutationType;
import pl.agh.capo.controller.collision.velocity.ReciprocalVelocityObstaclesCollisionFreeVelocity;
import pl.agh.capo.fear.Fear;
import pl.agh.capo.utilities.RobotMotionModel;
import pl.agh.capo.robot.IRobot;
import pl.agh.capo.robot.IRobotManager;
import pl.agh.capo.rvo.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RobotController implements Runnable {

	private final Logger logger = Logger.getLogger(RobotController.class);

	public static final int MOVE_ROBOT_SIMULATION_IN_MS = EnvironmentalConfiguration.SIMULATION_TIME_STEP_IN_MS;
	public static final int MOVE_ROBOT_PERIOD_IN_MS = EnvironmentalConfiguration.SIMULATION_SLEEP_BETWEEN_TIME_STEP_IN_MS;
	private static final int MONITOR_SENSOR_PERIOD_IN_MS = (int) (MOVE_ROBOT_PERIOD_IN_MS * 1.5);

	private final IRobot robot;
	private final RobotMotionModel motionModel;
	private final StatePublisherUDP statePublisher;
	private final StateCollectorUDP stateCollector;
	private final CollisionFreeVelocityGenerator collisionFreeVelocityGenerator;
	private final WallCollisionDetector wallCollisionDetector;
	private final ScheduledExecutorService controlScheduler = Executors.newScheduledThreadPool(1);
	private final ScheduledExecutorService sensorMonitor = Executors.newScheduledThreadPool(1);

	private final int robotId;
	private final IRobotManager manager;

	private List<Destination> path;
	private Destination destination;
	private Destination lastReachDestination;
	private int sensorReadCounter = 0;
	private StringBuilder positionRobot;

	private boolean isSensorWorking = true;

	private Fear fear;
	private AbstractCollisionFreeVelocity collisionFreeVelocity;

	public RobotController(int robotId, List<Destination> destinationList, MazeMap mazeMap, IRobot robot, IRobotManager manager, CollisionFreeVelocityType collisionFreeVelocityType) {
		this.robotId = robotId;
		this.robot = robot;
		this.manager = manager;
		motionModel = new RobotMotionModel(EnvironmentalConfiguration.ROBOT_MAX_SPEED);

		wallCollisionDetector = new WallCollisionDetector(mazeMap);
		collisionFreeVelocityGenerator = new CollisionFreeVelocityGenerator(collisionFreeVelocityType, robotId, wallCollisionDetector);

		// stateCollector =
		// StateCollector.createAndEstablishConnection(collisionFreeVelocityGenerator);
		// statePublisher = StatePublisher.createAndEstablishConnection();

		stateCollector = StateCollectorUDP.createAndEstablishConnection(collisionFreeVelocityGenerator);
		statePublisher = StatePublisherUDP.createAndEstablishConnection();

		setPath(destinationList);

		try {
			fear = new Fear(robotId, mazeMap.getGates());
		} catch (Exception e) {
			e.printStackTrace();
		}

		collisionFreeVelocity = collisionFreeVelocityGenerator.createCollisionFreeState();

		publishState(false, new State(robotId, robot.getRobotLocation(), new Velocity(0, 0), destination));

		positionRobot = new StringBuilder();
	}

	public void setPath(List<Destination> destinationList) {
		path = destinationList;
		lastReachDestination = path.remove(0);
		destination = path.get(0);
	}

	public void run() {
		
		controlScheduler.scheduleAtFixedRate(this::controlRobot, MOVE_ROBOT_PERIOD_IN_MS, MOVE_ROBOT_PERIOD_IN_MS, TimeUnit.MILLISECONDS);

		// Test Algorytmu dzialajacy
		// controlScheduler.scheduleAtFixedRate(this::rvoTest, MOVE_ROBOT_PERIOD_IN_MS, MOVE_ROBOT_PERIOD_IN_MS, TimeUnit.MILLISECONDS);

		// Test Algorytmu dzialajacy
		// controlScheduler.scheduleAtFixedRate(this::rvoAlgorithmImplementationTest, MOVE_ROBOT_PERIOD_IN_MS, MOVE_ROBOT_PERIOD_IN_MS, TimeUnit.MILLISECONDS);

		// sensorMonitor.scheduleAtFixedRate(this::monitorSensor, MONITOR_SENSOR_PERIOD_IN_MS, MONITOR_SENSOR_PERIOD_IN_MS,TimeUnit.MILLISECONDS);
	}

	private void controlRobot() {
		try {
			sensorReadCounter++;
			Location robotLocation = robot.getRobotLocation();
			if (robotLocation == null) {
				robot.setVelocity(0.0, 0.0);
				return;
			}
			motionModel.setLocation(robotLocation);
			double destinationDistance = destination.distance(motionModel.getLocation());
			
			if (!findDestination(destinationDistance)) {
				stop();
				return;
			}

			Velocity optimalVelocity = findOptimalToDestinationVelocity();
			setToDestinationVelocity(optimalVelocity, destinationDistance);

			boolean collide = false;
			State collisionFreeState;
			double fearfactor = 0.0;
			boolean stopRobot = false;
			boolean stopWall = false;
			
			
			if (EnvironmentalConfiguration.ALGORITHM_FEAR_IMPLEMENTATION == FearMutationType.FEAR_ORIGINAL) {

				fearfactor = fear.CalculateFearFactor(collisionFreeVelocityGenerator.GetStates(), robotLocation);

				List<Integer> listRobotIDBiggerFearFactor = fear.GetRobotIDBiggerFearFactor(robotId, fearfactor);
				
				collisionFreeVelocity.buildVelocityObstacles(motionModel.getLocation(), optimalVelocity, listRobotIDBiggerFearFactor);
				collide = !collisionFreeVelocity.isCurrentVelocityCollisionFree();

				optimalVelocity = collisionFreeVelocity.get();
				setVelocity(optimalVelocity);

				optimalVelocity = wallCollisionDetector.collisionFreeVelocity(new Point(robotLocation.getX(),robotLocation.getY()), optimalVelocity);
				setVelocity(optimalVelocity);
				
				//stopWall = !wallCollisionDetector.collisionFree(new Point(robotLocation.getX(),robotLocation.getY()), optimalVelocity);
				
				
				//if(stopWall)
				//{
					//optimalVelocity.setX(optimalVelocity.getX() * -1);
					//optimalVelocity.setY(optimalVelocity.getY() * -1);
									
				//}
				
				stopRobot = fear.EmergencyStop(collisionFreeVelocityGenerator.GetStates(), robotLocation, optimalVelocity, fearfactor); 
				
				
				
				if(stopRobot)
				{
					optimalVelocity = new Velocity(0, 0);
					setVelocity(optimalVelocity);
				}
			} else if (EnvironmentalConfiguration.ALGORITHM_FEAR_IMPLEMENTATION == FearMutationType.FEAR_SINGLE_FIRST) {

				collisionFreeVelocity.buildVelocityObstacles(motionModel.getLocation(), optimalVelocity);
				collide = !collisionFreeVelocity.isCurrentVelocityCollisionFree();

				fearfactor = fear.CalculateFearFactor(collisionFreeVelocityGenerator.GetStates(), robotLocation);
				boolean avoidCollision = false;

				if (collide)
					avoidCollision = fear.HaveAvoidCollision(collisionFreeVelocityGenerator.GetStates(), fearfactor);

				if (avoidCollision) {
					collide = true;

					optimalVelocity = collisionFreeVelocity.get();
					setVelocity(optimalVelocity);
				} else
					collide = false;
			} else if (EnvironmentalConfiguration.ALGORITHM_FEAR_IMPLEMENTATION == FearMutationType.NONE) {

				collisionFreeVelocity.buildVelocityObstacles(motionModel.getLocation(), optimalVelocity);
				collide = !collisionFreeVelocity.isCurrentVelocityCollisionFree();

				optimalVelocity = collisionFreeVelocity.get();
				setVelocity(optimalVelocity);				
			}

//			if(stopRobot || stopWall)
//				robot.setVelocity(0,0);
//			else
//				robot.setVelocity(motionModel.getVelocityLeft(), motionModel.getVelocityRight());

			robot.setVelocity(motionModel.getVelocityLeft(), motionModel.getVelocityRight());
			
			collisionFreeState = createCollisionFreeState(optimalVelocity);
			collisionFreeState.setRobotFearFactor(fearfactor);

			publishState(collide, collisionFreeState);
			loggRobotPostition(collisionFreeState);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void rvoAlgorithmImplementationTest() {

		try {

			sensorReadCounter++;
			Location robotLocation = robot.getRobotLocation();
			if (robotLocation == null) {
				robot.setVelocity(0.0, 0.0);
				return;
			}
			motionModel.setLocation(robotLocation);
			double destinationDistance = destination.distance(motionModel.getLocation());
			
			if (!findDestination(destinationDistance)) {
				stop();
				return;
			}

			// robotLocation = robot.getRobotLocation();

			Velocity optimalVelocity = findOptimalToDestinationVelocity(); // new Velocity(destination.getX() - robotLocation.getX(), destination.getY() - robotLocation.getY());

			collisionFreeVelocity.buildVelocityObstacles(motionModel.getLocation(), optimalVelocity);

			optimalVelocity = collisionFreeVelocity.get();

			robotLocation.setX((float) robotLocation.getX() + ((float) optimalVelocity.getX()) * 0.2);
			robotLocation.setY((float) robotLocation.getY() + ((float) optimalVelocity.getY()) * 0.2);

			State state = new State(robotId, new Location(robotLocation.getX(), robotLocation.getY(), 0), new Velocity(optimalVelocity.getX(), optimalVelocity.getY()), destination);

			publishState(false, state);
			
			loggRobotPostition(state);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void rvoTest() {

		try {

			sensorReadCounter++;
			Location robotLocation = robot.getRobotLocation();
			if (robotLocation == null) {
				robot.setVelocity(0.0, 0.0);
				return;
			}
			motionModel.setLocation(robotLocation);
			double destinationDistance = destination.distance(motionModel.getLocation());
			
			/*if (!findDestination(destinationDistance)) {
				stop();
				return;
			}*/

			// robotLocation = robot.getRobotLocation();

			Velocity optimalVelocity = findOptimalToDestinationVelocity(); // new Velocity(destination.getX() - robotLocation.getX(), destination.getY() - robotLocation.getY());

			collisionFreeVelocity.buildVelocityObstacles(motionModel.getLocation(), optimalVelocity);

			optimalVelocity = collisionFreeVelocity.get();

			// Location loc = new Location((float) robotLocation.getX() + ((float) optimalVelocity.getX()) * 0.2, (float) robotLocation.getY() + ((float) optimalVelocity.getY()) * 0.2, robotLocation.getDirection());

			// robotLocation.setX(loc.getX());
			// robotLocation.setY(loc.getY());

			setVelocity(optimalVelocity);

			motionModel.getLocation().setDirection(optimalVelocity.getX(), optimalVelocity.getY()); // robotLocation.setDirection(optimalVelocity.getX(),optimalVelocity.getY());

			// robot.setVelocity(motionModel.getVelocityLeft(), motionModel.getVelocityRight());

			double R = Math.sqrt(Math.pow(optimalVelocity.getX(), 2) + Math.pow(optimalVelocity.getY(), 2));
			robot.setVelocity(R, R);

			State collisionFreeState = createCollisionFreeState(optimalVelocity);

			// State state = new State(robotId, new Location(loc.getX(), loc.getY(), 0), new Velocity(optimalVelocity.getX(), optimalVelocity.getY()), destination);

			publishState(false, collisionFreeState);
			
			loggRobotPostition(collisionFreeState);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * 
	 * //AbstractCollisionFreeVelocity collisionFreeVelocity = collisionFreeVelocityGenerator.createCollisionFreeState(motionModel. getLocation(), optimalVelocity);
	 * 
	 * //collisionFreeVelocity.buildVelocityObstacles(motionModel.getLocation(), optimalVelocity);
	 * 
	 * 
	 * //Vector2 opt = //rvo.compute(states.values(), new Vector2((float) motionModel.getLocation().getX(),(float) motionModel.getLocation().getY()), optimalVelocity);
	 * 
	 * 
	 * //new Velocity(opt.x(), opt.y()); // //new Velocity(0.0,-0.25); //
	 * 
	 * //float x = (float) optimalVelocity.getX(); //float y = (float) optimalVelocity.getY();
	 * 
	 * if(Math.abs(x) < 0.0001) x = 0.0f;
	 * 
	 * if(Math.abs(y) < 0.0001) y = 0.0f;
	 * 
	 * optimalVelocity.setX(x); optimalVelocity.setY(y);
	 * 
	 * // string.Format("ID: {0} X: {1} Y: {2}", robotID, currentVelocity.x(), currentVelocity.y()) // System.out.println(loop + "&" + robotId + "&" + optimalVelocity.getX() + "&" + optimalVelocity.getY() + "&");
	 * 
	 * 
	 * // float Lx = (float) robotLocation.getX(); // float Ly = (float) robotLocation.getY();
	 * 
	 * //System.out.println(loop + "&" + robotId + "&" + x + "&" + y + "&" + Lx + "&" + Ly + "&" );
	 * 
	 * 
	 * // statePublisher.publishRobotState(state);
	 */
	/*
	 * State collisionFreeState; collisionFreeState = createCollisionFreeState(optimalVelocity); publishState(false, collisionFreeState); loggRobotPostition(collisionFreeState); loop++;
	 */

	// robotLocation = new Location(robotLocation.getX() +0.2,
	// robotLocation.getY() +0.2, 0);
	// robot.getRobotLocation().setX(robotLocation.getX() +0.2);
	// robot.getRobotLocation().setY(robotLocation.getY() +0.2);

	// motionModel.setLocation(robotLocation);

	/*
	 * Velocity optimalVelocity = new Velocity(0,1);
	 * 
	 * double x = robotLocation.getX(); double y = robotLocation.getY(); double x1 = robotLocation.getX() + optimalVelocity.getX(); double y1 = robotLocation.getY() + optimalVelocity.getY();
	 * 
	 * double direction = calcOrientationRVO(x,y,x1,y1);
	 * 
	 * 
	 * robotLocation = new Location(robotLocation.getX() + 0.3, robotLocation.getY() +0.3, direction); motionModel.setLocation(robotLocation);
	 * 
	 * setToDestinationVelocity(optimalVelocity, destinationDistance);
	 * 
	 * robot.setVelocity(motionModel.getVelocityLeft(), motionModel.getVelocityRight());
	 * 
	 * State collisionFreeState; collisionFreeState = createCollisionFreeState(optimalVelocity);
	 * 
	 * publishState(false, collisionFreeState);
	 */

	/*
	 * 
	 * // setToDestinationVelocity(optimalVelocity, destinationDistance); robotLocation = new Location(robotLocation.getX() + 0.2, robotLocation.getY() + 0.2, 0); motionModel.setLocation(robotLocation);
	 * 
	 * State collisionFreeState;
	 * 
	 * Velocity optimalVelocity = new Velocity(1, 1); collisionFreeState = createCollisionFreeState(optimalVelocity);
	 * 
	 * robot.setVelocity(motionModel.getVelocityLeft(), motionModel.getVelocityRight());
	 * 
	 * 
	 * 
	 * publishState(false, collisionFreeState);
	 */

	/*
	 * Velocity optimalVelocity = new Velocity(destination.getX() - robotLocation.getX(), destination.getY() - robotLocation.getY());
	 * 
	 * AbstractCollisionFreeVelocity collisionFreeVelocity = collisionFreeVelocityGenerator.createCollisionFreeState(motionModel. getLocation(), optimalVelocity);
	 * 
	 * optimalVelocity = collisionFreeVelocity.get();
	 * 
	 * double x = robotLocation.getX(); double y = robotLocation.getY(); double x1 = robotLocation.getX() + optimalVelocity.getX(); double y1 = robotLocation.getY() + optimalVelocity.getY();
	 * 
	 * double direction = calcOrientationRVO(x,y,x1,y1);
	 * 
	 * robotLocation = new Location(robotLocation.getX() + optimalVelocity.getX() * 0.2, robotLocation.getY() + optimalVelocity.getY() * 0.2, direction);
	 * 
	 * motionModel.setLocation(robotLocation);
	 * 
	 * 
	 * if(robotId == 1) System.out.println("Robot: " + Integer.toString(robotId) + "X: " + robotLocation.getX() + "Y: " + robotLocation.getY());
	 * 
	 * robot.setVelocity(motionModel.getVelocityLeft(), motionModel.getVelocityRight());
	 * 
	 * State collisionFreeState;
	 * 
	 * collisionFreeState = createCollisionFreeState(optimalVelocity);
	 * 
	 * publishState(false, collisionFreeState);
	 * 
	 * 
	 */
	// Velocity optimalVelocity = findOptimalToDestinationVelocity();
	// setToDestinationVelocity(optimalVelocity, destinationDistance);

	// Velocity optimalVelocity = findOptimalToDestinationVelocity();
	// setToDestinationVelocity(optimalVelocity, destinationDistance);

	// State collisionFreeState;
	//
	// robotLocation = new Location(7, 4, 0);
	//
	// robotLocation.setDirection(-1.57);
	//
	// motionModel.setLocation(robotLocation);
	//
	//
	// //optimalVelocity = new Velocity(0, 0);
	// setVelocity(optimalVelocity); //ustawienie wektora jak ma jechac
	//
	//
	// robot.setVelocity(motionModel.getVelocityLeft(),
	// motionModel.getVelocityRight()); //wykonanie przez robota
	//
	// collisionFreeState = createCollisionFreeState(optimalVelocity);
	// //wizualizacja
	// publishState(false, collisionFreeState); //wizualizacja
	//

	// boolean collide = false;
	// State collisionFreeState;
	// double fearfactor = 0.0;

	// AbstractCollisionFreeVelocity collisionFreeVelocity =
	// collisionFreeVelocityGenerator.createCollisionFreeState(motionModel.getLocation(),
	// optimalVelocity);
	// collide = !collisionFreeVelocity.isCurrentVelocityCollisionFree();

	// optimalVelocity = collisionFreeVelocity.get();
	// setVelocity(optimalVelocity);

	// State st = createCollisionFreeState(optimalVelocity);

	// double x = st.getLocation().getX();
	// double y = st.getLocation().getY();
	// double x1 = st.getLocation().getX() + st.getVelocity().getX();
	// double y1 = st.getLocation().getY() + st.getVelocity().getY();

	// double optimalDirection = calcOrientationRVO(x,y,x1,y1);

	// st.getLocation().setDirection(optimalDirection);

	// Location moveLocation = new Location(robotLocation.getX() +
	// optimalVelocity.getX() * 0.2, robotLocation.getY() +
	// optimalVelocity.getY() * 0.2,0.0);

	// State sst1 = new State(robotId, moveLocation, optimalVelocity ,
	// optimalVelocity);

	// robot.setVelocity(leftVelocity, rightVelocity);
	// robot.setVelocity(optimalVelocity.getX(),robotLocation.getY());
	// publishState(false, sst1);

	// robot.setVelocity(motionModel.getVelocityLeft(),
	// motionModel.getVelocityRight());

	// collisionFreeState = createCollisionFreeState(optimalVelocity);
	// collisionFreeState.setRobotFearFactor(fearfactor);

	// publishState(collide, collisionFreeState);

	// setVelocity(optimalVelocity);
	// test

	// robot.setVelocity(motionModel.getVelocityLeft(),
	// motionModel.getVelocityRight());

	// collisionFreeState = createCollisionFreeState(optimalVelocity);
	// collisionFreeState.setRobotFearFactor(fearfactor);

	// publishState(collide, collisionFreeState);

	// Location st = robotLocation;
	//
	// double x = st.getX();
	// double y = st.getY();
	// double x1 = st.getX() + optimalVelocity.getX();
	// double y1 = st.getY() + optimalVelocity.getY();
	//
	// double optimalDirection = calcOrientationRVO(x,y,x1,y1);
	//
	// st.setDirection(optimalDirection);
	//
	// publishState(collide, collisionFreeState);

	// System.out.println("Robot: " + robotId + " X: " + String.format("%.5f",
	// motionModel.getVelocityLeft()) + "Y: " +
	// String.format("%.5f",motionModel.getVelocityRight()));

	// System.out.println("Robot: " + robotId + " X: " + String.format("%.5f",
	// optimalVelocity.getX()) + "Y: " +
	// String.format("%.5f",optimalVelocity.getY()));

	// Velocity optimalVelocity = findOptimalToDestinationVelocity();
	//
	//
	//
	// setToDestinationVelocity(optimalVelocity, destinationDistance);
	//
	//
	//
	// //System.out.println(optimalVelocity.toString());
	//
	// boolean collide = false;
	// boolean avoidCollision = false;
	// State st;
	//

	// /* if(robotId == 8)
	// {
	// optimalVelocity = rvRobot0.GetVelocity();
	// setVelocity(optimalVelocity);
	// }
	//
	// if(robotId == 0)
	// {
	// optimalVelocity = rvRobot8.GetVelocity();
	// setVelocity(optimalVelocity);
	// }*/
	//
	// AbstractCollisionFreeVelocity collisionFreeVelocity =
	// collisionFreeVelocityGenerator.createCollisionFreeState(motionModel.getLocation(),
	// optimalVelocity);
	//
	// collide = !collisionFreeVelocity.isCurrentVelocityCollisionFree();
	//
	//
	// if(collide)
	// {
	// optimalVelocity = collisionFreeVelocity.get();
	// setVelocity(optimalVelocity);
	//
	// }
	//
	//// if (collide)
	//// {
	////
	//// optimalVelocity = new Velocity(-0.99, 0);
	//// setVelocity(optimalVelocity);
	////
	//// }
	//
	// // Velocity velo = collisionFreeVelocity.get();
	//
	// // optimalVelocity = velo;
	// // setVelocity(optimalVelocity);
	//
	// //System.out.println( "Robot: " + robotId + " X: " + velo.getX() + " Y: "
	// + velo.getY());
	//
	// // }
	// //if(robotId == 0)
	// // System.out.print( optimalVelocity.getX() + ";" +
	// optimalVelocity.getY() + "&");
	//
	//
	// st = createCollisionFreeState(optimalVelocity);
	//
	// robot.setVelocity(motionModel.getVelocityLeft(),
	// motionModel.getVelocityRight());
	//
	// publishState(collide, st);
	//
	//
	// if (collide)
	// {
	// optimalVelocity = collisionFreeVelocity.get();
	// setVelocity(optimalVelocity);
	// }
	//
	// st = createCollisionFreeState(optimalVelocity);
	//
	// System.out.println(" Robot " + robotId + " X: " + optimalVelocity.getX()
	// + " Y: " + optimalVelocity.getY());
	//

	// double x = st.getLocation().getX();
	// double y = st.getLocation().getY();
	// double x1 = st.getLocation().getX() + st.getVelocity().getX();
	// double y1 = st.getLocation().getY() + st.getVelocity().getY();
	//
	// double optimalDirection = calcOrientationRVO(x,y,x1,y1);

	// st.getLocation().setDirection(optimalDirection);

	// robot.setVelocity(motionModel.getVelocityLeft(),
	// motionModel.getVelocityRight());

	// publishState(collide, st);

	// }
	// if(EnvironmentalConfiguration.FEAR)
	// {
	// double fearfactor =
	// fear.CalculateFearFactor(collisionFreeVelocityGenerator.GetStates(),robotLocation);
	//
	// if (collide)
	// avoidCollision =
	// fear.HaveAvoidCollision(collisionFreeVelocityGenerator.GetStates(),
	// fearfactor);
	//
	// collide = false;
	//
	// if (avoidCollision) {
	// optimalVelocity = collisionFreeVelocity.get();
	// // System.out.println("OP V: X " + optimalVelocity.getX() + ", Y
	// // " + optimalVelocity.getY());
	// setVelocity(optimalVelocity);
	//
	// collide = true;
	// }
	//
	// robot.setVelocity(motionModel.getVelocityLeft(),
	// motionModel.getVelocityRight());
	// st = createCollisionFreeState(optimalVelocity);
	// st.setRobotFearFactor(fearfactor);
	// }
	// else
	// {
	//
	// if (collide)
	// {
	// optimalVelocity = collisionFreeVelocity.get();
	// setVelocity(optimalVelocity);
	// }
	//
	// st = createCollisionFreeState(optimalVelocity);
	//
	// System.out.println(" Robot " + robotId + " X: " + optimalVelocity.getX()
	// + " Y: " + optimalVelocity.getY());
	//
	//
	// double x = st.getLocation().getX();
	// double y = st.getLocation().getY();
	// double x1 = st.getLocation().getX() + st.getVelocity().getX();
	// double y1 = st.getLocation().getY() + st.getVelocity().getY();
	//
	// double optimalDirection = calcOrientationRVO(x,y,x1,y1);
	//
	// st.getLocation().setDirection(optimalDirection);
	//
	//
	// robot.setVelocity(motionModel.getVelocityLeft(),
	// motionModel.getVelocityRight());
	//
	// }
	//
	// publishState(collide, st);
	// }

	private void loggRobotPostition(State currentRobotState) {
		
	//	System.out.println(robotId + ";" + sensorReadCounter + ";" + currentRobotState.getLocation().getX() + ";" + currentRobotState.getLocation().getY() + ";" + currentRobotState.getLocation().getDirection() + ";" + currentRobotState.getVelocity().getX() + ";" + currentRobotState.getVelocity().getY() + ";" + currentRobotState.getRobotFearFactor() + ";" + "\n");
		
		positionRobot.append(robotId + ";" + sensorReadCounter + ";" + currentRobotState.getLocation().getX() + ";" + currentRobotState.getLocation().getY() + ";" + currentRobotState.getLocation().getDirection() + ";" + currentRobotState.getVelocity().getX() + ";" + currentRobotState.getVelocity().getY() + ";" + currentRobotState.getRobotFearFactor() + ";" + "\n");
	}

	private double calcOrientationRVO(double x, double y, double x1, double y1) {
		return Math.atan2(y1 - y, x1 - x);
	}

	private void stop() {
		controlScheduler.shutdownNow();
		sensorMonitor.shutdownNow();
		publishState(false, State.createFinished(robotId));
		manager.onFinish(robotId, sensorReadCounter, positionRobot.toString());
		robot.setVelocity(0.0, 0.0);
	}

	private void setToDestinationVelocity(Velocity velocity, double destinationDistance) {
		double angleToTarget = getToVelocityAngle(velocity);
		if (Double.isNaN(angleToTarget)) {
			motionModel.setVelocity(0.0, 0.0);
		} else {
			motionModel.setLinearAndAngularVelocities(findLinearVelocity(angleToTarget, destinationDistance), calculateAngularVelocity(angleToTarget));
		}
	}

	private double findLinearVelocity(double angleToTarget, double destinationDistance) {
		// if (destination.isFinal()) {
		// return Math.cos(angleToTarget / 2.0) * Math.min(1.0,
		// destinationDistance) * motionModel.getMaxLinearVelocity() / 2.0;
		// } else {
		return Math.cos(angleToTarget / 2.0) * motionModel.getMaxLinearVelocity() / 2.0;
		// }
	}

	private void setVelocity(Velocity velocity) {
		double angleToTarget = getToVelocityAngle(velocity);
		if (Double.isNaN(angleToTarget)) {
			motionModel.setVelocity(0.0, 0.0);
			return;
		}
		double speed = velocity.getSpeed();
		if (speed == 0.0) {
			motionModel.setLinearAndAngularVelocities(0.0, 0.0);
		} else {
			motionModel.setLinearAndAngularVelocities(velocity.getSpeed(), calculateAngularVelocity(angleToTarget));
		}
	}

	private double getToVelocityAngle(Velocity velocity) {
		if (velocity.getX() == 0.0 && velocity.getY() == 0.0) {
			return Double.NaN;
		}
		Vector2D robotUnitVector = motionModel.getUnitVector();
		return robotUnitVector.angleTo(velocity.toVector2D());
	}

	// Dodawany jest margines 0.15
	private Velocity findOptimalToDestinationVelocity() {
		double deltaX = destination.getX() - motionModel.getLocation().getX();
		double deltaY = destination.getY() - motionModel.getLocation().getY();
		double distance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
		if (distance == 0.0) {
			return new Velocity(0.0, 0.0);
		}
		double factor = EnvironmentalConfiguration.PREF_ROBOT_SPEED / distance;
		return new Velocity(deltaX * factor, deltaY * factor);
	}

	private State createCollisionFreeState(Velocity optimalVelocity) {
		return new State(robotId, motionModel.getLocation(), optimalVelocity, destination);
	}

	private void publishState(boolean collide, State state) {
		isSensorWorking = true;
		statePublisher.publishRobotState(state);
		manager.onNewState(robotId, collide, state);
	}

	private boolean findDestination(double destinationDistance) {
		if (!wallCollisionDetector.isDestinationVisible(motionModel.getLocation(), destination)) {
			path.add(0, lastReachDestination);
			destination = path.get(0);
			return true;
		}
		if (destinationDistance <= destination.getMargin()) {
			lastReachDestination = path.remove(0);
			if (path.size() > 0) {
				destination = path.get(0);
			} else {
				return false;
			}
		}
		return true;
	}

	private void monitorSensor() {
		if (isSensorWorking) {
			isSensorWorking = false;
		} else {
			reduceSpeedDueToSensorReadingTimeout();
		}
	}

	private void reduceSpeedDueToSensorReadingTimeout() {
		logger.debug("-> reduceSpeedDueToSensorReadingTimeout\n");
		robot.setVelocity(motionModel.getVelocityLeft() / 2, motionModel.getVelocityRight() / 2);
	}

	private double calculateAngularVelocity(double angleToTarget) {
		return EnvironmentalConfiguration.ANGULAR_VELOCITY_FACTOR * angleToTarget;
	}
}
