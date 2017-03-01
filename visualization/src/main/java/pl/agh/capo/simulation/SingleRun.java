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

	public static void RunAllgorytmConfigure(int runAllgorytm) throws IOException {
		switch (runAllgorytm) {
		case 0: // FearFactorBase
			EnvironmentalConfiguration.COLLISIONFREEVELOCITYMETHOD = CollisionFreeVelocityType.RECIPROCAL_VELOCITY_OBSTACLES;
			EnvironmentalConfiguration.FEAR = true;
			EnvironmentalConfiguration.ACTIVEFEARFACTORGATE = false;
			break;
		case 1: // FearFactorWithPassageThroughTheDoor
			EnvironmentalConfiguration.COLLISIONFREEVELOCITYMETHOD = CollisionFreeVelocityType.RECIPROCAL_VELOCITY_OBSTACLES;
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
	}

	public static void main(String[] args) throws FileNotFoundException {

		if (args.length != 2)
			return;

		try {
			String robotConfigPath = "./Config.csv";
			String mapPath = "./Map.json";
			MazeMap mazeMap;

			ConnectMSSQLServer log = new ConnectMSSQLServer();

			configure = log.GetTaskConfig(Integer.parseInt(args[0]));
			RunAllgorytmConfigure(Integer.parseInt(args[1]));

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
