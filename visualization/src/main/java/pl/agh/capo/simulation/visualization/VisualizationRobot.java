package pl.agh.capo.simulation.visualization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.util.Date;

import pl.agh.capo.configure.ConnectMSSQLServer;
import pl.agh.capo.configure.TaskConfig;
import pl.agh.capo.utilities.communication.StateCollector;
import pl.agh.capo.utilities.state.State;



public class VisualizationRobot {

	public static void main(String[] args) {
	
		if (args.length != 1)
			return;

		String mapPath = "./MapVisualizerRVO.json";

		ConnectMSSQLServer log = new ConnectMSSQLServer();

		TaskConfig configure = log.GetTaskConfigVisualization(Integer.parseInt(args[0]));
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
		
		State[] tempLog = log.GetVisualizeRobotCaseSimulation(configure.ID_Case);
		
		StateCollectorVisualizer vis = new StateCollectorVisualizer(mazeVisualizer,tempLog);
		//System.exit(1);		
		
		//StateCollector stateCollector = StateCollector.createAndEstablishConnection(mazeVisualizer);
		//if (!stateCollector.isConnectionEstablished()) {
		//	System.exit(1);
		//}
	}

}
