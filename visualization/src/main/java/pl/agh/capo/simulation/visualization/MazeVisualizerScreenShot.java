package pl.agh.capo.simulation.visualization;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import pl.agh.capo.configure.ConnectMSSQLServer;
import pl.agh.capo.configure.RunAllgorytmConfigureSimulation;
import pl.agh.capo.configure.TaskConfig;
import pl.agh.capo.simulation.robot.RobotManager;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.Point;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;

public class MazeVisualizerScreenShot {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String mapPath = "./MapVisualizerRVO.json";

		//RunAllgorytmConfigureRobot.RunCommunicationConfigure();
		RunAllgorytmConfigureSimulation.RunCommunicationConfigure();
				
		ConnectMSSQLServer log = new ConnectMSSQLServer();

		TaskConfig configure = log.GetSimulationConfig(Integer.parseInt(args[0]));
		//String sCaseName = "Algorithm: " + configure.Name_Program + " Map: " + configure.Name_Map + " Set: " + configure.Name_Config;
				
		String sCaseName = "Simulator"; //"Visualiser";
				
		try {
				
			//RunAllgorytmConfigureRobot.RunAllgorytmConfigure(configure);
			RunAllgorytmConfigureSimulation.RunAllgorytmConfigure(configure);
				
				
				Files.write(Paths.get(mapPath), configure.Map.getBytes(), StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				MazeVisualizer mazeVisualizer = MazeVisualizer.getInstance();
				mazeVisualizer.IS_MAX_MODE = true;
				mazeVisualizer.open(mapPath, sCaseName);
				
				ArrayList<State>  result = getConfigurationList(configure.ConfigFile);
				
				for (int i = 0; i < result.size(); i++) {
					mazeVisualizer.handle(result.get(i));
				}
						
				
				
			//	RobotManager robotManager = new RobotManager(robotConfig, mazeMap);
				
			//	State[] tempLog = log.GetVisualizeRobotCaseSimulation(configure.ID_Case,configure.ID_Trials);
				
			//	StateCollectorVisualizer vis = new StateCollectorVisualizer(mazeVisualizer,tempLog);
				//System.exit(1);		
		
	}
	
	public static ArrayList<State> getConfigurationList(String sRobotConfig)
	{
		ArrayList<State> result = new ArrayList<State>();
		
	    String[] robotData = sRobotConfig.split("\n");
	    
	    for (int i = 0; i < robotData.length; i++) 
	    {
			String[] temp = robotData[i].split(";");
	    	int idRobot = Integer.parseInt(temp[0]);	
	    	double locX = Double.parseDouble(temp[3]) ;
	    	double locY = Double.parseDouble(temp[4]);
	    	double locAlfa = Double.parseDouble(temp[1]);
			
			
			State item = new State(idRobot,new Location(locX, locY, locAlfa),new Velocity(0, 0),new Point(-1, -1));
			
			result.add(item);
		}
		
		
		
		return  result;
	}

}
