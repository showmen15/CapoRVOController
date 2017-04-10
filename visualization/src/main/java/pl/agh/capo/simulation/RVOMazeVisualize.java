package pl.agh.capo.simulation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import pl.agh.capo.simulation.visualization.MazeVisualizer;
import pl.agh.capo.utilities.communication.StateCollector;
import pl.agh.capo.utilities.communicationUDP.StateCollectorUDP;

public class RVOMazeVisualize {

	public static void main(String[] args) {

		if (args.length != 1)
			return;

		String mapPath = "./MapVisualizerRVO.json";

		ConnectMSSQLServer log = new ConnectMSSQLServer();

		TaskConfig configure = log.GetTaskConfig(Integer.parseInt(args[0]));
		String sCaseName = "Algorytm: " + configure.Name_Program + " Mapa: " + configure.Name_Map
				+ " Konfiguracja: " + configure.Name_Config;

		try {
			Files.write(Paths.get(mapPath), configure.Map.getBytes(), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		MazeVisualizer mazeVisualizer = MazeVisualizer.getInstance();
		mazeVisualizer.open(mapPath, sCaseName);
		StateCollectorUDP stateCollector = StateCollectorUDP.createAndEstablishConnection(mazeVisualizer);
		if (!stateCollector.isConnectionEstablished()) {
			System.exit(1);
		}
	}

}
