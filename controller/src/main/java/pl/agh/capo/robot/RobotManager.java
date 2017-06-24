package pl.agh.capo.robot;

import pl.agh.capo.configure.ConnectMSSQLServer;
import pl.agh.capo.configure.TaskConfig;
import pl.agh.capo.controller.RobotController;
import pl.agh.capo.utilities.state.State;

public class RobotManager implements IRobotManager {

	@Override
	public void onFinishDB(TaskConfig configure,int id, int time,String loggRobotPostition )
	{
		if(configure != null)
		{	
			ConnectMSSQLServer log = new ConnectMSSQLServer();
			log.SaveResult( configure, id, time, configure.RobotSimulationTimeMilisecond(), loggRobotPostition);	
		} 
        
    	System.out.println(String.format("FINISH\nid: %d; loop count: %d", id, time));
         System.exit(1);
	}
	
	
    @Override
    public void onFinish(int id, int time,String loggRobotPostition) {
    	
    	System.out.println(String.format("FINISH\nid: %d; loop count: %d", id, time));
    }

    @Override
    public void onNewState(int id, boolean collide, State state) {

    }
    
//    private void printResult() {   	
//    	
//    	
//        StringBuilder sb = new StringBuilder();
//        for (int id : result.keySet()) {
//        	
//        	  sb.append(String.format("%d;%d;%d\n", id, result.get(id), result.get(id) * RobotController.MOVE_ROBOT_SIMULATION_IN_MS));
//        	
//         	
//          
//        }
//        System.out.print(sb.toString());
//        System.exit(1);
//    }
}
