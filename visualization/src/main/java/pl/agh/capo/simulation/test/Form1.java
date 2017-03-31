package pl.agh.capo.simulation.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pl.agh.capo.controller.collision.velocity.ReciprocalVelocityObstaclesCollisionFreeVelocity;
import pl.agh.capo.rvo.Vector2;
import pl.agh.capo.utilities.state.Destination;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.Point;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;

public class Form1   {

	private static Map<Integer, State2> AllRobotStates = new ConcurrentHashMap<>();

	private static List<SimRVO3>  rvo = new ArrayList<>();
	static List<Thread> thr = new ArrayList<>();
	
	  static List<Vector2> robotStartPos = new ArrayList<Vector2>();
      static List<Vector2> robotEndPos = new ArrayList<Vector2>();
	
      private static Boolean working = false;
      
            
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		button1_Click();
		
		initThred();
		
		for(int i = 0; i < thr.size(); i++)
			thr.get(i).start();
	
	
	}

	public static void Run() throws IOException
	{
		button1_Click();
		
		initThred();
		
		for(int i = 0; i < thr.size(); i++)
			thr.get(i).start();
	}
	
	  private static void button1_Click() throws IOException
	  {
		  String sFileName = "D:\\Desktop\\Config.csv";
		  
		List<String> ss = Files.readAllLines(Paths.get(sFileName));
          configurAgent(ss);
	  }
	
      private static void configurAgent(List<String> sConfig)
      {
          //String[] temp = sConfig.split("\n");

          //foreach (var item in temp)
    	  	for (String item : sConfig) {
          
              String[] tmp = item.split(";");

              float xStart = Float.parseFloat(tmp[3].replace(".", "."));
              float yStart = Float.parseFloat(tmp[4].replace(".", "."));

              float xEnd = Float.parseFloat(tmp[6].replace(".", "."));
              float yEnd = Float.parseFloat(tmp[7].replace(".", ".").replace("\n", ""));

              robotStartPos.add(new Vector2(xStart, yStart));
              robotEndPos.add(new Vector2(xEnd, yEnd));
          }
      }
      
      private static void initThred()
      {
          int RobotID = 0;

          for (int i = 0; i < robotStartPos.size(); i++)
          {
        	  
        	  SimRVO3 sim = new SimRVO3(robotEndPos.get(i),RobotID,null);
  
        	  
        	  rvo.add(sim);

              State2 state = new State2();
              state.location = robotStartPos.get(i);
              state.velocity = new Vector2(0, 0);
              state.robotId = RobotID;

              AllRobotStates.putIfAbsent(RobotID, state);             
              RobotID++;

              thr.add(new Thread(new RunSim(sim, AllRobotStates)));
          }
          working = true;
      }	  
}
