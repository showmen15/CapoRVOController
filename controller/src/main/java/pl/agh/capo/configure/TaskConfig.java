package pl.agh.capo.configure;

import java.util.Date;

public class TaskConfig {
	
	public int ID_Case;
	
	
	public int ID_Program;
	public String Name_Program;
	
	public int ID_Trials;
	
	public int ID_Map;
	public String Name_Map;
    
	public String Map;
    
	public int ID_Config;
	 public String Name_Config;
   
    public String ConfigFile;
    	
	private Date StartTime = new Date();
    
	public TaskConfig()
	{		
		
	}	
	
	public long RobotSimulationTimeMilisecond()
	{
		Date date2 = new Date();
		
		return  date2.getTime() - StartTime.getTime();
	}
}
