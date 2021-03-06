package pl.agh.capo.configure;

import java.io.IOException;

import pl.agh.capo.controller.collision.velocity.CollisionFreeVelocityType;
import pl.agh.capo.controller.collision.velocity.FearMutationType;
import pl.agh.capo.controller.collision.velocity.PointViaSelectionType;
import pl.agh.capo.utilities.EnvironmentalConfiguration;

public class RunAllgorytmConfigureSimulation {

	public static void RunCommunicationConfigure()
	{
		EnvironmentalConfiguration.SIMULATION = true;
		EnvironmentalConfiguration.ADDRESS_SQL =  "WR-7-BASE-74\\SQLEXPRESS";//"127.0.0.1"; // //"192.168.2.103";//"SZYMON-KOMPUTER";//"192.168.2.101"; // "SZYMON-KOMPUTER"; //"SZSZ\\SQLEXPRESS"; //"WR-7-BASE-74\\SQLEXPRESS";//"SZSZ\\SQLEXPRESS";////"WR-7-BASE-74\\SQLEXPRESS";//// ServerName = "SZYMON-KOMPUTER";
		
		// RabbitMQ connection leads
		EnvironmentalConfiguration.ADDRESS = "127.0.0.1";
		EnvironmentalConfiguration.USERNAME = "guest";
		EnvironmentalConfiguration.PASSWORD =  "guest";
		EnvironmentalConfiguration.CHANNEL_NAME = "capoRobotState";
	}	
	
	public static void RunAllgorytmConfigure(TaskConfig configure) throws Exception {
	
		EnvironmentalConfiguration.SIMULATION_TIME_STEP_IN_MS = 200;

		EnvironmentalConfiguration.SIMULATION_SLEEP_BETWEEN_TIME_STEP_IN_MS = 50; // SIMULATION_TIME_STEP_IN_MS;

		// Empirically selected values to control robots
		// Units consisted with International System of Units
		EnvironmentalConfiguration.ROBOT_DIAMETER = 0.3;
		EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = 0.14;
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
			
			case 6:// Skrzyżowanie Typu 8
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.ALL;
							
				EnvironmentalConfiguration.NEIGHBOR_DIST = 2.4f;
				EnvironmentalConfiguration.MAX_NEIGHBORS = 30;
				EnvironmentalConfiguration.TIME_HORIZON = 2.4f;
				EnvironmentalConfiguration.TIME_HORIZON_OBST = 2.5f;

				EnvironmentalConfiguration.RADIUS = 0.3f;
				EnvironmentalConfiguration.MAX_SPEED = 0.25f;
				EnvironmentalConfiguration.TIME_STEP = 0.2f;
				
				EnvironmentalConfiguration.MAX_OBSERVATION_DISTANCE_FF = 2.0;
				EnvironmentalConfiguration.RL_CP = 1.0;
				break;
			
			case 9: // BenchmarkCircle
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;

				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.NEIGHBOR_DIST = 15.0f;
				EnvironmentalConfiguration.MAX_NEIGHBORS = 10;
				EnvironmentalConfiguration.TIME_HORIZON = 10.0f;
				EnvironmentalConfiguration.TIME_HORIZON_OBST = 10.0f;

				EnvironmentalConfiguration.RADIUS = 1.5f;
				EnvironmentalConfiguration.MAX_SPEED = 2.0f;
				EnvironmentalConfiguration.TIME_STEP = 0.25f;

				EnvironmentalConfiguration.ROBOT_DIAMETER = 1.5;
				EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = 1.5/2;
				EnvironmentalConfiguration.ROBOT_MAX_SPEED = 2.0;
				
				EnvironmentalConfiguration.MAX_OBSERVATION_DISTANCE_FF = 2.0;
				EnvironmentalConfiguration.RL_CP = 1.0;
				break;
					
			case 10: // OtwartaPrzestrzeń7x5m.roson
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;
			
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;

				EnvironmentalConfiguration.NEIGHBOR_DIST = 2.0f;
				EnvironmentalConfiguration.MAX_NEIGHBORS = 10;
				EnvironmentalConfiguration.TIME_HORIZON = 3.0f;
				EnvironmentalConfiguration.TIME_HORIZON_OBST = 5.0f;

				EnvironmentalConfiguration.RADIUS = 0.3f;
				EnvironmentalConfiguration.MAX_SPEED = 0.25f;
				EnvironmentalConfiguration.TIME_STEP = 0.2f;

				EnvironmentalConfiguration.MAX_OBSERVATION_DISTANCE_FF = 2.0;
				EnvironmentalConfiguration.RL_CP = 1.0;
				break;

			case 12: // Pojedyncze Roboty OtwartaPrzestrzeń 7x5m.roson
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;

				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.NEIGHBOR_DIST = 2.0f;
				EnvironmentalConfiguration.MAX_NEIGHBORS = 10;
				EnvironmentalConfiguration.TIME_HORIZON = 3.0f;
				EnvironmentalConfiguration.TIME_HORIZON_OBST = 5.0f;

				EnvironmentalConfiguration.RADIUS = 0.3f;
				EnvironmentalConfiguration.MAX_SPEED = 0.25f;
				EnvironmentalConfiguration.TIME_STEP = 0.2f;

				EnvironmentalConfiguration.MAX_OBSERVATION_DISTANCE_FF = 2.0;
				EnvironmentalConfiguration.RL_CP = 1.0;
				break;

			case 11: // PrzejściePrzezDrzwi7x5.roson
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;

				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.NEIGHBOR_DIST = 2.4f;
				EnvironmentalConfiguration.MAX_NEIGHBORS = 30;
				EnvironmentalConfiguration.TIME_HORIZON = 2.7f;
				EnvironmentalConfiguration.TIME_HORIZON_OBST = 1.5f;

				EnvironmentalConfiguration.RADIUS = 0.3f;
				EnvironmentalConfiguration.MAX_SPEED = 0.25f;
				EnvironmentalConfiguration.TIME_STEP = 0.2f;

				EnvironmentalConfiguration.MAX_OBSERVATION_DISTANCE_FF = 2.0;
				EnvironmentalConfiguration.RL_CP = 1.0;
				break;
				
			case 13: //WąskiKorytarz7x5m.roson
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;
				
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.NEIGHBOR_DIST = 2.4f;
				EnvironmentalConfiguration.MAX_NEIGHBORS = 30;
				EnvironmentalConfiguration.TIME_HORIZON = 2.7f;
				EnvironmentalConfiguration.TIME_HORIZON_OBST = 2.5f;

				EnvironmentalConfiguration.RADIUS = 0.3f;
				EnvironmentalConfiguration.MAX_SPEED = 0.25f;
				EnvironmentalConfiguration.TIME_STEP = 0.2f;

				EnvironmentalConfiguration.MAX_OBSERVATION_DISTANCE_FF = 2.0;
				EnvironmentalConfiguration.RL_CP = 1.0;
				break;
				
			case 18: //WąskiePrzejścieMijankaDuza	
			case 14: //WąskiePrzejścieMijankaNowa
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;
				
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.NEIGHBOR_DIST = 2.4f;
				EnvironmentalConfiguration.MAX_NEIGHBORS = 30;
				EnvironmentalConfiguration.TIME_HORIZON = 2.7f;
				EnvironmentalConfiguration.TIME_HORIZON_OBST = 2.5f;

				EnvironmentalConfiguration.RADIUS = 0.3f;
				EnvironmentalConfiguration.MAX_SPEED = 0.25f;
				EnvironmentalConfiguration.TIME_STEP = 0.2f;

				EnvironmentalConfiguration.MAX_OBSERVATION_DISTANCE_FF = 2.0;
				EnvironmentalConfiguration.RL_CP = 1.0;
				break;
			
			case 15: //SkrzyżowanieRównorzędneNowe
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;
				
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.NEIGHBOR_DIST = 2.4f;
				EnvironmentalConfiguration.MAX_NEIGHBORS = 30;
				EnvironmentalConfiguration.TIME_HORIZON = 2.7f;
				EnvironmentalConfiguration.TIME_HORIZON_OBST = 2.5f;

				EnvironmentalConfiguration.RADIUS = 0.3f;
				EnvironmentalConfiguration.MAX_SPEED = 0.25f;
				EnvironmentalConfiguration.TIME_STEP = 0.2f;

				EnvironmentalConfiguration.MAX_OBSERVATION_DISTANCE_FF = 2.0;
				EnvironmentalConfiguration.RL_CP = 1.0;
				break;	
				
			case 16: //Otwarta Przestrzeń 4 Roboty 4
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.ONLY_ONCE;
				
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.NEIGHBOR_DIST = 15.0f;
				EnvironmentalConfiguration.MAX_NEIGHBORS = 10;
				EnvironmentalConfiguration.TIME_HORIZON = 10.0f;
				EnvironmentalConfiguration.TIME_HORIZON_OBST = 10.0f;

				EnvironmentalConfiguration.RADIUS = 0.3f;
				EnvironmentalConfiguration.MAX_SPEED = 0.25f;
				EnvironmentalConfiguration.TIME_STEP = 0.2f;
			
				EnvironmentalConfiguration.MAX_OBSERVATION_DISTANCE_FF = 2.0;
				EnvironmentalConfiguration.RL_CP = 1.0;
				
				
				break;
				
			default:
				throw new Exception("Algorytm nie skonfigurowany!!!!");

			}
			break;
		case 3: // RVOWithRightHand

			switch (configure.ID_Map) {
			case 6:
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.ALL;
			
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.ROBOT_DIAMETER = 0.3;
				EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = 0.14;
				EnvironmentalConfiguration.ROBOT_MAX_SPEED = 0.5;
				EnvironmentalConfiguration.PREF_ROBOT_SPEED = EnvironmentalConfiguration.ROBOT_MAX_SPEED / 2.0;
				EnvironmentalConfiguration.ANGULAR_VELOCITY_FACTOR = 3.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_OPPOSITE = 5.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_PRIMARY = 1.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_SUBORDINATED = 10.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_BEHIND = 3.0;
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				EnvironmentalConfiguration.VO_ROBOT_RADIUS = 1.6 * EnvironmentalConfiguration.ROBOT_DIAMETER;
				EnvironmentalConfiguration.ACCEPTABLE_RADIUS = 1 * EnvironmentalConfiguration.VO_ROBOT_RADIUS;
				EnvironmentalConfiguration.MIN_SPEED_FACTOR = 0.2;
				break;
				
			case 9: // BenchmarkCircle
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;
			
				EnvironmentalConfiguration.ROBOT_DIAMETER = 1.5;
				EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = 1.5/2;
				EnvironmentalConfiguration.ROBOT_MAX_SPEED = 2.0;
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				break;
				
			case 10: // OtwartaPrzestrzeń7x5m.roson
			case 12: // Pojedyncze Roboty OtwartaPrzestrzeń 7x5m.roson
			case 11: // PrzejściePrzezDrzwi7x5.roson
			case 13: //WąskiKorytarz7x5m.roson
			case 14: //WąskiePrzejścieMijankaNowa
			case 15: //SkrzyżowanieRównorzędneNowe
			case 16: //Otwarta Przestrzeń 4 Roboty 4
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.ONLY_ONCE;
				
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.ROBOT_DIAMETER = 0.3;
				EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = 0.14;
				EnvironmentalConfiguration.ROBOT_MAX_SPEED = 0.5;
				EnvironmentalConfiguration.PREF_ROBOT_SPEED = EnvironmentalConfiguration.ROBOT_MAX_SPEED / 2.0;
				EnvironmentalConfiguration.ANGULAR_VELOCITY_FACTOR = 3.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_OPPOSITE = 5.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_PRIMARY = 1.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_SUBORDINATED = 10.0;
				EnvironmentalConfiguration.RECIPROCITY_FACTOR_BEHIND = 3.0;
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				EnvironmentalConfiguration.VO_ROBOT_RADIUS = 1.6 * EnvironmentalConfiguration.ROBOT_DIAMETER;
				EnvironmentalConfiguration.ACCEPTABLE_RADIUS = 1 * EnvironmentalConfiguration.VO_ROBOT_RADIUS;
				EnvironmentalConfiguration.MIN_SPEED_FACTOR = 0.2;
				break;
				
				
			case 18: //WąskiePrzejścieMijankaDuza
				EnvironmentalConfiguration.POINT_VIA_SELECTION_TYPE = PointViaSelectionType.NONE;
				
				EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR = 1.6;
				
				EnvironmentalConfiguration.ROBOT_DIAMETER = 0.3;
				EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = 0.14;
				EnvironmentalConfiguration.ROBOT_MAX_SPEED = 0.5;
				EnvironmentalConfiguration.PREF_ROBOT_SPEED = EnvironmentalConfiguration.ROBOT_MAX_SPEED / 2.0;
				EnvironmentalConfiguration.ANGULAR_VELOCITY_FACTOR = 3.0;
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