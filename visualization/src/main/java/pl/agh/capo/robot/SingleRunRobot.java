package pl.agh.capo.robot;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.RobotMotionModel;
import pl.agh.capo.utilities.maze.MazeMap;
import pl.agh.capo.utilities.state.Destination;
import pl.agh.capo.Main;
import pl.agh.capo.configure.ConnectMSSQLServer;
import pl.agh.capo.configure.RunAllgorytmConfigureRobot;
import pl.agh.capo.configure.RunAllgorytmConfigureSimulation;
import pl.agh.capo.configure.TaskConfig;
import pl.agh.capo.controller.RobotController;
import pl.agh.capo.controller.collision.velocity.CollisionFreeVelocityType;
import pl.agh.capo.robot.Robot;
import pl.agh.capo.robot.RobotManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.ToIntFunction;

public class SingleRunRobot {

	public TaskConfig configure;
	public  RobotController controller;
	
		// build command: gradle clean jar
		//java -jar controller.jar <ID_Case>
	
	    public static void main(String[] args) {
	        
	    	SingleRunRobot temp0 = new SingleRunRobot(args);
	    	
	    	try {
	    		System.out.println("Start??");
				int s = System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	temp0.Run();
	    	
	    /*	SingleRunRobot temp0 = new SingleRunRobot(new String[] {"0", "192.168.2.200"});
	    	SingleRunRobot temp1 = new SingleRunRobot(new String[] {"1", "192.168.2.201"});
	    	
	    	temp0.Run();
	    	temp1.Run();*/
	    }

	    public SingleRunRobot(String[] args)
	    {
	    	String RobotIP = "127.0.0.1";
	    	
	    	if (args.length < 1)
				return;
	    	
	    	if(args.length == 2)
	    		RobotIP = args[1];
	    	
	    	try {
		    	
	    		//RobotIP = "127.0.0.1";//"192.168.2.20" + args[0];  // "192.168.2.200"; // SingleRunRobot.getRobotIP(); //"192.168.2.200"; //TODO do sprawdzenia
	    		int RobotID = Integer.parseInt(args[0]);//SingleRunRobot.getRobotID(RobotIP); 

				ConnectMSSQLServer log = new ConnectMSSQLServer();
				int id_config = log.GetConfigRobot();

				configure = log.GetTaskConfig(id_config);	
				RunAllgorytmConfigureRobot.RunAllgorytmConfigure(configure);
				
				Robot robot = new Robot(RobotIP);
				MazeMap mazeMap = new Gson().fromJson(configure.Map,MazeMap.class);
						
				String currentRobotDestinations = getDestination(RobotID,configure.ConfigFile); 
				List<Destination> destinations = parseDestinations(currentRobotDestinations);
				
			    controller = new RobotController(RobotID,destinations,mazeMap,robot,new RobotManager(),EnvironmentalConfiguration.COLLISIONFREEVELOCITYMETHOD,configure);

			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	    
	    public void Run()
	    {
	    	new Thread(controller).start();
	    }
	    
	    private static List<Destination> parseDestinations(String robotConfigure) {
	    	String[] robotData = robotConfigure.split(";");
	    	int index = 2;
	        List<Destination> destinationList = new ArrayList<>();
	        while (index < robotData.length - 2) {
	            double margin = Double.parseDouble(robotData[index]);
	            double x = Double.parseDouble(robotData[index + 1]);
	            double y = Double.parseDouble(robotData[index + 2]);
	            destinationList.add(new Destination(x, y, margin, robotData.length - index == 3));
	            index += 3;
	        }
	        return destinationList;
	    }
	    
	    
	    private static int getRobotID(String RobotIP)
	    {
	    	return Integer.parseInt(RobotIP.substring(RobotIP.length()-1,RobotIP.length()));
	    }
	
	    private static String getRobotIP() throws UnknownHostException
	    {
	
	    	
	    	return "";
	    }
	    
	    private static String getDestination(int RobotID,String ConfigFile)
	    {
	    	String[] config = ConfigFile.split("\n");
	    	
	    	return config[RobotID];
	    }
}
