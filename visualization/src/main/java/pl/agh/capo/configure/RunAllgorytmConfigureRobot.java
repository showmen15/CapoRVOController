package pl.agh.capo.configure;

import java.io.IOException;

import pl.agh.capo.controller.collision.velocity.CollisionFreeVelocityType;
import pl.agh.capo.controller.collision.velocity.FearMutationType;
import pl.agh.capo.controller.collision.velocity.PointViaSelectionType;
import pl.agh.capo.utilities.EnvironmentalConfiguration;

public class RunAllgorytmConfigureRobot {

	
	public static void RunCommunicationConfigure()
	{
		EnvironmentalConfiguration.SIMULATION = true;
		EnvironmentalConfiguration.ADDRESS_SQL = EnvironmentalConfiguration.SIMULATION ? "127.0.0.1" : "192.168.2.101";  //"192.168.2.103";//"SZYMON-KOMPUTER";//"192.168.2.101"; // "SZYMON-KOMPUTER"; //"SZSZ\\SQLEXPRESS"; //"WR-7-BASE-74\\SQLEXPRESS";//"SZSZ\\SQLEXPRESS";////"WR-7-BASE-74\\SQLEXPRESS";//// ServerName = "SZYMON-KOMPUTER";
		
		// RabbitMQ connection leads
		EnvironmentalConfiguration.ADDRESS = EnvironmentalConfiguration.SIMULATION ? "127.0.0.1" : "192.168.2.100";
		EnvironmentalConfiguration.USERNAME =  EnvironmentalConfiguration.SIMULATION ? "guest" : "panda";
		EnvironmentalConfiguration.PASSWORD =  EnvironmentalConfiguration.SIMULATION ? "guest" : "panda";
		EnvironmentalConfiguration.CHANNEL_NAME = "capoRobotState";
	}
	
	public static void RunAllgorytmConfigure(TaskConfig configure) throws Exception {

		EnvironmentalConfiguration.SIMULATION_TIME_STEP_IN_MS = 200;

		EnvironmentalConfiguration.SIMULATION_SLEEP_BETWEEN_TIME_STEP_IN_MS = 200; //80; // SIMULATION_TIME_STEP_IN_MS;

		// Empirically selected values to control robots
		// Units consisted with International System of Units
		EnvironmentalConfiguration.ROBOT_DIAMETER = 0.4;
		EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = 0.2;
		EnvironmentalConfiguration.ROBOT_MAX_SPEED = 0.5;
		EnvironmentalConfiguration.PREF_ROBOT_SPEED = EnvironmentalConfiguration.ROBOT_MAX_SPEED / 2.0;
		
		// Multiplier of angle to desired velocity (used to perform turning)
		EnvironmentalConfiguration.ANGULAR_VELOCITY_FACTOR = 3.0; 

		// Max speed multiplier determining collision free zone in Free right side
		// solution
		EnvironmentalConfiguration.RECIPROCITY_FACTOR_OPPOSITE = 5.0;
		EnvironmentalConfiguration.RECIPROCITY_FACTOR_PRIMARY = 1.0;
		EnvironmentalConfiguration.RECIPROCITY_FACTOR_SUBORDINATED = 10.0;
		EnvironmentalConfiguration.RECIPROCITY_FACTOR_BEHIND = 3.0;

		// Multiplier of half robot diameter
		EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;

		// Radius used to find VO
		EnvironmentalConfiguration.VO_ROBOT_RADIUS = 1.6 * EnvironmentalConfiguration.ROBOT_DIAMETER; // 1.6 *
																			// ROBOT_DIAMETER;
		// Minimum distance which allow robots to turn near each other,
		// Additional area of VO - not quite consistent with definition
		EnvironmentalConfiguration.ACCEPTABLE_RADIUS = 1 * EnvironmentalConfiguration.VO_ROBOT_RADIUS;

		// Multiplier of max speed during selecting velocity in pure VO solution
		EnvironmentalConfiguration.MIN_SPEED_FACTOR = 0.2;
		
		
		switch (configure.ID_Program) {

		case 0: // FearFactorBase
			EnvironmentalConfiguration.COLLISIONFREEVELOCITYMETHOD = CollisionFreeVelocityType.RECIPROCAL_VELOCITY_OBSTACLES_FEAR_FACTOR;
			EnvironmentalConfiguration.ALGORITHM_FEAR_IMPLEMENTATION = FearMutationType.FEAR_ORIGINAL;
			EnvironmentalConfiguration.ACTIVEFEARFACTORGATE = false;
			break;

		case 1: // FearFactorWithPassageThroughTheDoor
			EnvironmentalConfiguration.COLLISIONFREEVELOCITYMETHOD = CollisionFreeVelocityType.RECIPROCAL_VELOCITY_OBSTACLES_FEAR_FACTOR;
			EnvironmentalConfiguration.ALGORITHM_FEAR_IMPLEMENTATION = FearMutationType.FEAR_ORIGINAL;
			EnvironmentalConfiguration.ACTIVEFEARFACTORGATE = true;
			break;

		case 2: // RVOBase
			EnvironmentalConfiguration.COLLISIONFREEVELOCITYMETHOD = CollisionFreeVelocityType.RECIPROCAL_VELOCITY_OBSTACLES;
			EnvironmentalConfiguration.ALGORITHM_FEAR_IMPLEMENTATION = FearMutationType.NONE;
			EnvironmentalConfiguration.ACTIVEFEARFACTORGATE = false;
			break;

		case 3: // RVOWithRightHand
			EnvironmentalConfiguration.COLLISIONFREEVELOCITYMETHOD = CollisionFreeVelocityType.RIGHT_HAND;
			EnvironmentalConfiguration.ALGORITHM_FEAR_IMPLEMENTATION = FearMutationType.NONE;
			EnvironmentalConfiguration.ACTIVEFEARFACTORGATE = false;
			break;
		case 4:// FearFactorBaseWithSingleFirst
			
			EnvironmentalConfiguration.COLLISIONFREEVELOCITYMETHOD = CollisionFreeVelocityType.RECIPROCAL_VELOCITY_OBSTACLES_FEAR_FACTOR;
			EnvironmentalConfiguration.ALGORITHM_FEAR_IMPLEMENTATION = FearMutationType.FEAR_SINGLE_FIRST;
			EnvironmentalConfiguration.ACTIVEFEARFACTORGATE = false;
			break;
			
		case 5: //FearFactorWithPassageThroughTheDoorWithSingleFirst
			EnvironmentalConfiguration.COLLISIONFREEVELOCITYMETHOD = CollisionFreeVelocityType.RECIPROCAL_VELOCITY_OBSTACLES_FEAR_FACTOR;
			EnvironmentalConfiguration.ALGORITHM_FEAR_IMPLEMENTATION = FearMutationType.FEAR_SINGLE_FIRST;
			EnvironmentalConfiguration.ACTIVEFEARFACTORGATE = true;
			break;


		default:
			throw new IOException("Nieprawidlowe dane wejsciowe");
		}

		switch (configure.ID_Program) // Config AlgoritmSettings
		{

		case 0: // FearFactorBase
		case 1: // FearFactorWithPassageThroughTheDoor
		case 2: // RVOBase
		case 4: // FearFactorBaseWithSingleFirst
		case 5: //FearFactorWithPassageThroughTheDoorWithSingleFirst
			switch (configure.ID_Map) {
								
			case 19: //Lab OtwartaPrzestrzeñ
				EnvironmentalConfiguration.ROBOT_MAX_SPEED = 0.4;
				EnvironmentalConfiguration.PREF_ROBOT_SPEED = EnvironmentalConfiguration.ROBOT_MAX_SPEED / 2.0;
				
				
				EnvironmentalConfiguration.ROBOT_DIAMETER = 0.3;
				EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = EnvironmentalConfiguration.ROBOT_DIAMETER / 2;
				
				
				
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;
			
				EnvironmentalConfiguration.ANGULAR_VELOCITY_FACTOR = 3.55;
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;

				EnvironmentalConfiguration.NEIGHBOR_DIST = 2.0f; //2.0
				EnvironmentalConfiguration.MAX_NEIGHBORS = 10;
				EnvironmentalConfiguration.TIME_HORIZON = 2.1f; //3.0
				EnvironmentalConfiguration.TIME_HORIZON_OBST = 2.7f;

				EnvironmentalConfiguration.RADIUS = 0.28f;
				EnvironmentalConfiguration.MAX_SPEED = 0.25f;
				EnvironmentalConfiguration.TIME_STEP = 0.08f;

				EnvironmentalConfiguration.MAX_OBSERVATION_DISTANCE_FF = 2.0;
				EnvironmentalConfiguration.RL_CP = 1.5;
				break;

			case 17: //Lab PrzejœciePrzezDrzwi
				
				EnvironmentalConfiguration.ROBOT_MAX_SPEED = 0.4;
				EnvironmentalConfiguration.PREF_ROBOT_SPEED = EnvironmentalConfiguration.ROBOT_MAX_SPEED / 2.0;
				
				
				EnvironmentalConfiguration.ROBOT_DIAMETER = 0.3;
				EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = 0.2;//EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE / 2;
				
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;

				EnvironmentalConfiguration.ANGULAR_VELOCITY_FACTOR = 3.55;
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.NEIGHBOR_DIST = 2.7f;
				EnvironmentalConfiguration.MAX_NEIGHBORS = 30;
				EnvironmentalConfiguration.TIME_HORIZON = 3.0f;
				EnvironmentalConfiguration.TIME_HORIZON_OBST = 2.7f;

				EnvironmentalConfiguration.RADIUS = 0.26f; //0.25
				EnvironmentalConfiguration.MAX_SPEED = 0.25f; //0.25
				EnvironmentalConfiguration.TIME_STEP = 0.03;//0.03f; //0.2

				EnvironmentalConfiguration.MAX_OBSERVATION_DISTANCE_FF = 0.8;
				EnvironmentalConfiguration.RL_CP = 1.0;
				break;
				
			case 20: //Lab  Mijanka 
				EnvironmentalConfiguration.ROBOT_MAX_SPEED = 0.4;
				EnvironmentalConfiguration.PREF_ROBOT_SPEED = EnvironmentalConfiguration.ROBOT_MAX_SPEED / 2.0;
				
				
				EnvironmentalConfiguration.ROBOT_DIAMETER = 0.3;
				EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = 0.2;//EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE / 2;
				
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;

				EnvironmentalConfiguration.ANGULAR_VELOCITY_FACTOR = 3.55;
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.NEIGHBOR_DIST = 2.7f;
				EnvironmentalConfiguration.MAX_NEIGHBORS = 30;
				EnvironmentalConfiguration.TIME_HORIZON = 3.0f;
				EnvironmentalConfiguration.TIME_HORIZON_OBST = 2.7f;

				EnvironmentalConfiguration.RADIUS = 0.26f; //0.25
				EnvironmentalConfiguration.MAX_SPEED = 0.25f; //0.25
				EnvironmentalConfiguration.TIME_STEP = 0.03;//0.03f; //0.2

				EnvironmentalConfiguration.MAX_OBSERVATION_DISTANCE_FF = 2.0;
				EnvironmentalConfiguration.RL_CP = 1.5;
				break;
				
			case 21: //Lab Otwarta Przestrzeñ 4 Roboty 4
				EnvironmentalConfiguration.ROBOT_MAX_SPEED = 0.4;
				EnvironmentalConfiguration.PREF_ROBOT_SPEED = EnvironmentalConfiguration.ROBOT_MAX_SPEED / 2.0;
				
				
				EnvironmentalConfiguration.ROBOT_DIAMETER = 0.3;
				EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = EnvironmentalConfiguration.ROBOT_DIAMETER / 2;
				
				
				
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.ONLY_ONCE;
			
				EnvironmentalConfiguration.ANGULAR_VELOCITY_FACTOR = 3.55;
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;

				EnvironmentalConfiguration.NEIGHBOR_DIST = 2.0f; //2.0
				EnvironmentalConfiguration.MAX_NEIGHBORS = 10;
				EnvironmentalConfiguration.TIME_HORIZON = 2.1f; //3.0
				EnvironmentalConfiguration.TIME_HORIZON_OBST = 2.7f;

				EnvironmentalConfiguration.RADIUS = 0.28f;
				EnvironmentalConfiguration.MAX_SPEED = 0.25f;
				EnvironmentalConfiguration.TIME_STEP = 0.08f;

				EnvironmentalConfiguration.MAX_OBSERVATION_DISTANCE_FF = 2.0;
				EnvironmentalConfiguration.RL_CP = 1.5;
				break;
				
			default:
				throw new Exception("Algorytm nie skonfigurowany!!!!");

			}
			break;
		case 3: // RVOWithRightHand

			switch (configure.ID_Map) {
				
			
			case 19: // Lab OtwartaPrzestrzeñ
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;
				
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.ROBOT_DIAMETER = 0.4;
				EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = EnvironmentalConfiguration.ROBOT_DIAMETER / 2;
				EnvironmentalConfiguration.ROBOT_MAX_SPEED = 0.5;
				EnvironmentalConfiguration.PREF_ROBOT_SPEED = EnvironmentalConfiguration.ROBOT_MAX_SPEED / 2.0;
				EnvironmentalConfiguration.ANGULAR_VELOCITY_FACTOR = 3.55;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_OPPOSITE = 5.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_PRIMARY = 1.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_SUBORDINATED = 10.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_BEHIND = 3.0;
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				EnvironmentalConfiguration.VO_ROBOT_RADIUS = 1.6 * EnvironmentalConfiguration.ROBOT_DIAMETER;
				EnvironmentalConfiguration.ACCEPTABLE_RADIUS = 1 * EnvironmentalConfiguration.VO_ROBOT_RADIUS;
				EnvironmentalConfiguration.MIN_SPEED_FACTOR = 0.2;
				break;
				
				
			case 17: //Lab PrzejœciePrzezDrzwi
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;
				
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.ROBOT_DIAMETER = 0.35;
				EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = EnvironmentalConfiguration.ROBOT_DIAMETER / 2;
				EnvironmentalConfiguration.ROBOT_MAX_SPEED = 0.5;
				EnvironmentalConfiguration.PREF_ROBOT_SPEED = EnvironmentalConfiguration.ROBOT_MAX_SPEED / 2.0;
				EnvironmentalConfiguration.ANGULAR_VELOCITY_FACTOR = 3.55;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_OPPOSITE = 5.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_PRIMARY = 1.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_SUBORDINATED = 10.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_BEHIND = 3.0;
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				EnvironmentalConfiguration.VO_ROBOT_RADIUS = 1.6 * EnvironmentalConfiguration.ROBOT_DIAMETER;
				EnvironmentalConfiguration.ACCEPTABLE_RADIUS = 1 * EnvironmentalConfiguration.VO_ROBOT_RADIUS;
				EnvironmentalConfiguration.MIN_SPEED_FACTOR = 0.2;
				break;
				
			case 20: //Lab  Mijanka 
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;
				
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.ROBOT_DIAMETER = 0.35;
				EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = EnvironmentalConfiguration.ROBOT_DIAMETER / 2;
				EnvironmentalConfiguration.ROBOT_MAX_SPEED = 0.5;
				EnvironmentalConfiguration.PREF_ROBOT_SPEED = EnvironmentalConfiguration.ROBOT_MAX_SPEED / 2.0;
				EnvironmentalConfiguration.ANGULAR_VELOCITY_FACTOR = 3.55;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_OPPOSITE = 5.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_PRIMARY = 1.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_SUBORDINATED = 10.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_BEHIND = 3.0;
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				EnvironmentalConfiguration.VO_ROBOT_RADIUS = 1.6 * EnvironmentalConfiguration.ROBOT_DIAMETER;
				EnvironmentalConfiguration.ACCEPTABLE_RADIUS = 1 * EnvironmentalConfiguration.VO_ROBOT_RADIUS;
				EnvironmentalConfiguration.MIN_SPEED_FACTOR = 0.2;
				break;
				
			case 21: //Lab Otwarta Przestrzeñ 4 Roboty 4
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.ONLY_ONCE;
				
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.ROBOT_DIAMETER = 0.4;
				EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = EnvironmentalConfiguration.ROBOT_DIAMETER / 2;
				EnvironmentalConfiguration.ROBOT_MAX_SPEED = 0.5;
				EnvironmentalConfiguration.PREF_ROBOT_SPEED = EnvironmentalConfiguration.ROBOT_MAX_SPEED / 2.0;
				EnvironmentalConfiguration.ANGULAR_VELOCITY_FACTOR = 3.55;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_OPPOSITE = 5.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_PRIMARY = 1.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_SUBORDINATED = 10.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_BEHIND = 3.0;
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				EnvironmentalConfiguration.VO_ROBOT_RADIUS = 1.6 * EnvironmentalConfiguration.ROBOT_DIAMETER;
				EnvironmentalConfiguration.ACCEPTABLE_RADIUS = 1 * EnvironmentalConfiguration.VO_ROBOT_RADIUS;
				EnvironmentalConfiguration.MIN_SPEED_FACTOR = 0.2;
				break;
				
			default:
				throw new Exception("Algorytm nie skonfigurowany!!!!");

			}
			break;
		default:
			throw new IOException("Nieprawidlowe dane wejsciowe");

		}
	}
	
}
