package pl.agh.capo.utilities;

import pl.agh.capo.controller.collision.velocity.CollisionFreeVelocityType;
import pl.agh.capo.controller.collision.velocity.FearMutationType;
import pl.agh.capo.controller.collision.velocity.PointViaSelectionType;

public class EnvironmentalConfiguration {

	public static boolean SIMULATION = false;

	public static String ADDRESS_SQL =  SIMULATION ? "127.0.0.1" : "192.168.2.101";
	
	// RabbitMQ connection leads
	public static  String ADDRESS = SIMULATION ? "127.0.0.1" : "192.168.2.100";
	public static  String USERNAME = SIMULATION ? "guest" : "panda";
	public static  String PASSWORD = SIMULATION ? "guest" : "panda";
	public static  String CHANNEL_NAME = "capoRobotState";

	public static  int SIMULATION_TIME_STEP_IN_MS = 200;

	public static  int SIMULATION_SLEEP_BETWEEN_TIME_STEP_IN_MS = 30; // SIMULATION_TIME_STEP_IN_MS;

	// Empirically selected values to control robots
	// Units consisted with International System of Units
	public static  double ROBOT_DIAMETER = 0.4;
	public static  double ROBOT_WHEELS_HALF_DISTANCE = 0.2;
	public static  double ROBOT_MAX_SPEED = 1.0;
	public static  double PREF_ROBOT_SPEED =  ROBOT_MAX_SPEED / 2.0;

	// Multiplier of angle to desired velocity (used to perform turning)
	public static  double ANGULAR_VELOCITY_FACTOR = 3.0; 

	// Max speed multiplier determining collision free zone in Free right side
	// solution
	public static  double RECIPROCITY_FACTOR_OPPOSITE = 5.0;
	public static  double RECIPROCITY_FACTOR_PRIMARY = 1.0;
	public static  double RECIPROCITY_FACTOR_SUBORDINATED = 10.0;
	public static  double RECIPROCITY_FACTOR_BEHIND = 3.0;

	// Multiplier of half robot diameter
	public static  double WALL_COLLISION_MARGIN_FACTOR = 1.6;

	// Radius used to find VO
	public static  double VO_ROBOT_RADIUS = 1.6 * ROBOT_DIAMETER; // 1.6 *
																		// ROBOT_DIAMETER;

	// Minimum distance which allow robots to turn near each other,
	// Additional area of VO - not quite consistent with definition
	public static  double ACCEPTABLE_RADIUS = 1 * VO_ROBOT_RADIUS;

	// Multiplier of max speed during selecting velocity in pure VO solution
	public static  double MIN_SPEED_FACTOR = 0.2;

	public static CollisionFreeVelocityType COLLISIONFREEVELOCITYMETHOD = CollisionFreeVelocityType.RECIPROCAL_VELOCITY_OBSTACLES_FEAR_FACTOR;

	public static FearMutationType ALGORITHM_FEAR_IMPLEMENTATION = FearMutationType.FEAR_ORIGINAL;

	public static PointViaSelectionType POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;
	
	public static boolean ACTIVEFEARFACTORGATE = false;

	// Tries of selecting velocity in pure VO solution
	// public static final int TRIES_COUNT = 500;

	////// Settings HRVO ////////////

//	//CAPO dla PrzejœciePrzezDrzwi
//	public static double NEIGHBOR_DIST = 2.3f;
//	public static int MAX_NEIGHBORS = 30;
//	public static double TIME_HORIZON = 2.7f;
//	public static double TIME_HORIZON_OBST = 1.5f;
//
//	public static double RADIUS = 0.3f;
//	public static double MAX_SPEED = 0.25f;
//	public static double TIME_STEP = 0.2f;


////	//CAPO dla Pojedycze Roboty dó³ 2
//	public static double NEIGHBOR_DIST = 2.3f; //2.2 //1.8   // 1.1 ok // 0.6 dobre do obok siebie ale nie dla otwatej  //0.8 dla 1 na 1 (0.4 dla  
//	public static int MAX_NEIGHBORS = 50;
//	public static double TIME_HORIZON = 3.0f; //5.0  //5.0
//	public static double TIME_HORIZON_OBST = 5.0f;
//
//	public static double RADIUS = 0.3f;
//	public static double MAX_SPEED = 0.25f;
//	public static double TIME_STEP = 0.2f;
//	
//	public static double MAX_OBSERVATION_DISTANCE_FF =  2.0;  //do sprawdzenia //Zasieg obserwacji
//	public static double RL_CP = 1.0; //Zasieg dzialania przejscia 

	
	//OtwartaPrzestrzeñ7x5m.roson
//	public static double NEIGHBOR_DIST = 2.4f; //2.4f;  //2.0  
//	public static int MAX_NEIGHBORS = 30;
//	public static double TIME_HORIZON = 2.7f; 
//	public static double TIME_HORIZON_OBST = 2.5f;
//
//	public static double RADIUS = 0.3f;
//	public static double MAX_SPEED = 0.25f;
//	public static double TIME_STEP = 0.2f;
//	
//	public static double MAX_OBSERVATION_DISTANCE_FF =  2.0; 
//	public static double RL_CP = 1.0;  
	
	//Circle CAPO 
//	public static double NEIGHBOR_DIST = 2.4;//1.0f; //2.4f;  //2.0  
//	public static int MAX_NEIGHBORS = 30;
//	public static double TIME_HORIZON = 2.7f; 
//	public static double TIME_HORIZON_OBST = 2.25f;//1.5f;
//
//	public static double RADIUS = 0.3f;
//	public static double MAX_SPEED = 0.25f;
//	public static double TIME_STEP = 0.2f;
//	
//	public static double MAX_OBSERVATION_DISTANCE_FF =  2.0; 
//	public static double RL_CP = 1.0;  
	
	
//	//Circle CAPO 
//	public static double NEIGHBOR_DIST = 15.0f;//1.0f; //2.4f;  //2.0  
//	public static int MAX_NEIGHBORS = 10;
//	public static double TIME_HORIZON = 10.0f; 
//	public static double TIME_HORIZON_OBST = 10.0f;//1.5f;
//
//	public static double RADIUS = 0.3f;
//	public static double MAX_SPEED = 0.25f;
//	public static double TIME_STEP = 0.2f;
//	
//	public static double MAX_OBSERVATION_DISTANCE_FF =  2.0; 
//	public static double RL_CP = 1.0; 
	
////	//Skrzy¿owanie typ 8 
//	public static double NEIGHBOR_DIST = 2.4f;//1.0f; //2.4f;  //2.0  
//	public static int MAX_NEIGHBORS = 30;
//	public static double TIME_HORIZON = 2.4f; 
//	public static double TIME_HORIZON_OBST = 2.5f;//1.5f;
//
//	public static double RADIUS = 0.3f;
//	public static double MAX_SPEED = 0.25f;
//	public static double TIME_STEP = 0.2f;
//	
//	public static double MAX_OBSERVATION_DISTANCE_FF =  2.0; 
//	public static double RL_CP = 1.0;	
	
	//mijanka
	public static double NEIGHBOR_DIST = 2.4f;//1.0f; //2.4f;  //2.0  
	public static int MAX_NEIGHBORS = 30;
	public static double TIME_HORIZON = 2.7f; 
	public static double TIME_HORIZON_OBST = 2.5f;//1.5f;

	public static double RADIUS = 0.3f;
	public static double MAX_SPEED = 0.25f;
	public static double TIME_STEP = 0.2f;
	
	public static double MAX_OBSERVATION_DISTANCE_FF =  2.0; 
	public static double RL_CP = 1.0;	
			
	//mijanka duza
//	public static double NEIGHBOR_DIST = 3.4f;//1.0f; //2.4f;  //2.0  
//	public static int MAX_NEIGHBORS = 30;
//	public static double TIME_HORIZON = 3.7f; 
//	public static double TIME_HORIZON_OBST = 2.5f;//1.5f;
//
//	public static double RADIUS = 0.3f;
//	public static double MAX_SPEED = 0.25f;
//	public static double TIME_STEP = 0.2f;
//	
//	public static double MAX_OBSERVATION_DISTANCE_FF =  3.0; 
//	public static double RL_CP = 1.0;	
//	
}
