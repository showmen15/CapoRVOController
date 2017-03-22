package pl.agh.capo.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import pl.agh.capo.controller.collision.velocity.CollisionFreeVelocityType;
import pl.agh.capo.simulation.robot.RobotManager;
import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.maze.MazeMap;

public class SingleRun {

	public static TaskConfig configure;

	public static void RunAllgorytmConfigure(TaskConfig configure) throws Exception {

		switch (configure.ID_Program) {

		case 0: // FearFactorBase
			EnvironmentalConfiguration.COLLISIONFREEVELOCITYMETHOD = CollisionFreeVelocityType.RECIPROCAL_VELOCITY_OBSTACLES_FEAR_FACTOR;
			EnvironmentalConfiguration.FEAR = true;
			EnvironmentalConfiguration.ACTIVEFEARFACTORGATE = false;
			break;

		case 1: // FearFactorWithPassageThroughTheDoor
			EnvironmentalConfiguration.COLLISIONFREEVELOCITYMETHOD = CollisionFreeVelocityType.RECIPROCAL_VELOCITY_OBSTACLES_FEAR_FACTOR;
			EnvironmentalConfiguration.FEAR = true;
			EnvironmentalConfiguration.ACTIVEFEARFACTORGATE = true;
			break;

		case 2: // RVOBase
			EnvironmentalConfiguration.COLLISIONFREEVELOCITYMETHOD = CollisionFreeVelocityType.RECIPROCAL_VELOCITY_OBSTACLES;
			EnvironmentalConfiguration.FEAR = false;
			EnvironmentalConfiguration.ACTIVEFEARFACTORGATE = false;
			break;

		case 3: // RVOWithRightHand
			EnvironmentalConfiguration.COLLISIONFREEVELOCITYMETHOD = CollisionFreeVelocityType.RIGHT_HAND;
			EnvironmentalConfiguration.FEAR = false;
			EnvironmentalConfiguration.ACTIVEFEARFACTORGATE = false;
			break;

		default:
			throw new IOException("Nieprawidlowe dane wejsciowe");
		}

		switch (configure.ID_Program) // Config AlgoritmSettings
		{

		case 0: // FearFactorBase
		case 1: // FearFactorWithPassageThroughTheDoor
		case 2: // RVOBase
			switch (configure.ID_Map) {
			case 9: // BenchmarkCircle

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
					
			case 10: // OtwartaPrzestrze�7x5m.roson

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

			case 12: // Pojedyncze Roboty OtwartaPrzestrze� 7x5m.roson

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

			case 11: // Przej�ciePrzezDrzwi7x5.roson

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
				
			case 13: //W�skiKorytarz7x5m.roson
				
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
				
			case 14: //W�skiePrzej�cieMijankaNowa
				
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
			
			case 15: //Skrzy�owanieR�wnorz�dneNowe
				
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
				
			default:
				throw new Exception("Algorytm nie skonfigurowany!!!!");

			}
			break;
		case 3: // RVOWithRightHand

			switch (configure.ID_Map) {
			case 9: // BenchmarkCircle
				EnvironmentalConfiguration.ROBOT_DIAMETER = 1.5;
				EnvironmentalConfiguration.ROBOT_WHEELS_HALF_DISTANCE = 1.5/2;
				EnvironmentalConfiguration.ROBOT_MAX_SPEED = 2.0;
				break;
				
			case 10: // OtwartaPrzestrze�7x5m.roson
			case 12: // Pojedyncze Roboty OtwartaPrzestrze� 7x5m.roson
			case 11: // Przej�ciePrzezDrzwi7x5.roson
			case 13: //W�skiKorytarz7x5m.roson
			case 14: //W�skiePrzej�cieMijankaNowa
			case 15: //Skrzy�owanieR�wnorz�dneNowe
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

	public static void main(String[] args) throws Exception {

		if (args.length != 2)
			return;

		try {
			String robotConfigPath = "./Config.csv";
			String mapPath = "./Map.json";
			MazeMap mazeMap;

			ConnectMSSQLServer log = new ConnectMSSQLServer();

			configure = log.GetTaskConfig(Integer.parseInt(args[0]));
			RunAllgorytmConfigure(configure);

			Files.write(Paths.get(robotConfigPath), configure.ConfigFile.getBytes(), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);

			Files.write(Paths.get(mapPath), configure.Map.getBytes(), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);

			mazeMap = new Gson().fromJson(new FileReader(new File(mapPath)), MazeMap.class);

			File robotConfig = new File(robotConfigPath);
			RobotManager robotManager = new RobotManager(robotConfig, mazeMap);

		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
