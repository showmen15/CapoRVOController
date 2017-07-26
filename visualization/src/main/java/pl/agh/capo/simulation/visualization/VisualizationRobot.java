package pl.agh.capo.simulation.visualization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.util.Date;

import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

import pl.agh.capo.configure.ConnectMSSQLServer;
import pl.agh.capo.configure.RunAllgorytmConfigureRobot;
import pl.agh.capo.configure.RunAllgorytmConfigureSimulation;
import pl.agh.capo.configure.TaskConfig;
import pl.agh.capo.utilities.communication.StateCollector;
import pl.agh.capo.utilities.state.State;



public class VisualizationRobot {

	public static void main(String[] args) throws IOException {
	
		if (args.length != 2)
			return;

		String mapPath = "./MapVisualizerRVO.json";

		RunAllgorytmConfigureRobot.RunCommunicationConfigure();
		//RunAllgorytmConfigureSimulation.RunCommunicationConfigure();
		
		ConnectMSSQLServer log = new ConnectMSSQLServer();

		TaskConfig configure = log.GetTaskConfigVisualization(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
		String sCaseName = "Algorytm: " + configure.Name_Program + " Mapa: " + configure.Name_Map
				+ " Konfiguracja: " + configure.Name_Config;

		try {
		
		RunAllgorytmConfigureRobot.RunAllgorytmConfigure(configure);
		//RunAllgorytmConfigureSimulation.RunAllgorytmConfigure(configure);
		
		
			Files.write(Paths.get(mapPath), configure.Map.getBytes(), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		MazeVisualizer mazeVisualizer = MazeVisualizer.getInstance();
		mazeVisualizer.open(mapPath, sCaseName);
		
		State[] tempLog = log.GetVisualizeRobotCaseSimulation(configure.ID_Case,configure.ID_Trials);
		
		StateCollectorVisualizer vis = new StateCollectorVisualizer(mazeVisualizer,tempLog);
		//System.exit(1);		
		
		//StateCollector stateCollector = StateCollector.createAndEstablishConnection(mazeVisualizer);
		//if (!stateCollector.isConnectionEstablished()) {
		//	System.exit(1);
		//}
	}
	
	//poprawka danych zle zalogownych
	public static void mainFix(String[] args) throws IOException {
			
		RunAllgorytmConfigureRobot.RunCommunicationConfigure();
		//RunAllgorytmConfigureSimulation.RunCommunicationConfigure();
		
		ConnectMSSQLServer log = new ConnectMSSQLServer();
		
		int[] tab = new int[] {1119,1120,1121,1122,1123,1124,1125,1126,1127,1128,1129,1130,1131,1132,1133,1134,1135,1136,1137,1138,1139,1141,1142,1143,1145,1146,1147,1148,1149,1150};
		
		for (int i : tab) 
		{
			String temp = log.FixLogResult(i);
			log.UpdateLogResult(i, temp);
		}
		
		int i = 222;
		i++;
	}
}
