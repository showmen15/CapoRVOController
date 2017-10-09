package pl.agh.capo.configure;

import java.io.InputStream;
import java.security.Timestamp;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import pl.agh.capo.configure.TaskConfig;
import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.state.State;

public class ConnectMSSQLServer {

	// info
	// https://msdn.microsoft.com/en-us/library/ms378422(v=sql.110).aspx

	private String ServerName;
	private String User;
	private String Password;
	private String DatabaseName;

	public ConnectMSSQLServer() {
		ServerName =  EnvironmentalConfiguration.ADDRESS_SQL; //"192.168.2.103";//"SZYMON-KOMPUTER";//"192.168.2.101"; // "SZYMON-KOMPUTER"; //"SZSZ\\SQLEXPRESS"; //"WR-7-BASE-74\\SQLEXPRESS";//"SZSZ\\SQLEXPRESS";////"WR-7-BASE-74\\SQLEXPRESS";//// ServerName = "SZYMON-KOMPUTER";
		User = "szsz";
		Password = "szsz";
		DatabaseName = "Doktorat";
	}

	private Connection createNewConnection() throws SQLServerException {
		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setServerName(ServerName);
		ds.setUser(User);
		ds.setPassword(Password);
		ds.setDatabaseName(DatabaseName);

		// ds.setIntegratedSecurity(true);
		// ds.setPortNumber(1433);

		return ds.getConnection();
	}

	public int CountRecords() {
		int i = 0;

		try {
			ResultSet rs = null;
			Statement stmt = null;
			Connection conn;

			conn = createNewConnection();

			String SQL = "SELECT Count(*) FROM [nowa_baza].[dbo].[Database]";

			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);

			if (rs.next())
				i = rs.getInt(1);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

	public int GetRecords() {
		int i = 0;

		try {
			StringBuilder ss = new StringBuilder();
			ResultSet rs = null;
			Statement stmt = null;
			Connection conn;

			conn = createNewConnection();

			String SQL = "select [id],[tekst] from ttt.dbo.Table_4";

			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);

			if (rs.next()) {
				ss.append(rs.getString(2));

				int j = ss.length();

				System.out.println(ss);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

	public int InsertRecords(String result) {
		int i = 0;

		try {
			StringBuilder ss = new StringBuilder();
			ResultSet rs = null;
			Statement stmt = null;
			Connection conn;

			conn = createNewConnection();

			String SQL = "select [id],[tekst] from ttt.dbo.Table_4";

			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);

			if (rs.next()) {
				ss.append(rs.getString(2));

				int j = ss.length();

				System.out.println(ss);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

	public String GetMap(int mapID) {
		String result = "";
		try {

			ResultSet rs = null;
			Statement stmt = null;
			Connection conn;

			conn = createNewConnection();

			String SQL = "SELECT sMap FROM dbo.Maps WHERE ID_map = " + mapID;

			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);

			if (rs.next())
				result = rs.getString(1);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public String GetConfig(int configID) {
		String result = "";
		try {

			ResultSet rs = null;
			Statement stmt = null;
			Connection conn;

			conn = createNewConnection();

			String SQL = "SELECT sConfig FROM dbo.RobotsConfig WHERE ID_Config = " + configID;

			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);

			if (rs.next())
				result = rs.getString(1);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public TaskConfig GetTaskConfig(int ID_Case) {
		TaskConfig result = new TaskConfig();
		try {

			ResultSet rs = null;
			Statement stmt = null;
			Connection conn;

			conn = createNewConnection();

			String SQL = "SELECT ID_Case, ID_Program, ID_Trials, ID_Map, Map, ID_Config, ConfigFile, Name_Program, Name_Map, Name_Config FROM dbo.TaskConfig WHERE ID_Case = "
					+ ID_Case;

			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);

			if (rs.next()) {
				result.ID_Case = rs.getInt("ID_Case");
				result.ID_Program = rs.getInt("ID_Program");
				result.ID_Trials = rs.getInt("ID_Trials");
				result.ID_Map = rs.getInt("ID_Map");
				result.Map = rs.getString("Map");
				result.ID_Config = rs.getInt("ID_Config");
				result.ConfigFile = rs.getString("ConfigFile");
				
				result.Name_Program = rs.getString("Name_Program");
				result.Name_Map =  rs.getString("Name_Map");
				result.Name_Config = rs.getString("Name_Config");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public void SaveResult(TaskConfig config,int ID_Robot,int LoopTime, long TimeMilisecond,String resultRobotPositon)
	{
		
		
		try {

			ResultSet rs = null;
			Statement stmt = null;
			Connection conn;

			conn = createNewConnection();
			
			PreparedStatement sta = conn.prepareStatement("INSERT INTO dbo.Result(ID_Case ,ID_Program,ID_Trials, ID_Map,ID_Config,ID_Robot,LoopTime,TimeMilisecond,RobotPosition) VALUES(?,?,?,?,?,?,?,?,?);");
			
			sta.setInt(1, config.ID_Case);
			sta.setInt(2, config.ID_Program);
			sta.setInt(3, config.ID_Trials);
			sta.setInt(4, config.ID_Map);
			sta.setInt(5, config.ID_Config);
			sta.setInt(6, ID_Robot);
			sta.setInt(7, LoopTime);
			sta.setLong(8, TimeMilisecond);
			sta.setString(9, resultRobotPositon);
			
			sta.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int GetConfigRobot()
	{
		int Result = -1;
		
		try {

			ResultSet rs = null;
			Statement stmt = null;
			Connection conn;

			conn = createNewConnection();

			String SQL = "select MIN(ID_CASE) as ID_CASE from dbo.TasksList";

			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);

			if (rs.next()) {
				Result = rs.getInt("ID_CASE");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return Result;
		
	}
	
	public State[]  GetVisualizeRobotCaseSimulation(int ID_Case,int ID_Trials)
	{
		ArrayList<State> tempResult = new ArrayList<>();
		State[] result;
		
		try {

			ResultSet rs = null;
			Statement stmt = null;
			Connection conn;
			String sLog;
			String[] sSplitLog;
			String[] sSplitState;
			State tempState;
			
			conn = createNewConnection();

			String SQL = "select l.RobotPosition AS RobotPosition  from dbo.Result r INNER JOIN dbo.LogResult l on r.ID = l.ID WHERE r.ID_Case = "
			+ ID_Case + " AND r.ID_Trials = " + ID_Trials;

			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);

			while(rs.next()) 
			{
				sLog = rs.getString("RobotPosition");
				sSplitLog = sLog.split("\n");
				
				for(int i = 0; i< sSplitLog.length; i++)
				{
					tempState = new State(sSplitLog[i]);
					tempState.setTimeStemp(i * 200); //czas sumulacji 200ms;
					
					if(i+1 >= sSplitLog.length) //ustaw jako znikanie robota jako ostatni stan
						tempState.setFinished();
					
					tempResult.add(tempState);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		tempResult.sort((o1,o2) -> (Long.compare(o1.getTimeStemp(), o2.getTimeStemp())));
		
		result = new State[tempResult.size()];
		result = tempResult.toArray(result);
		
		return result;		
	}	
	
	
	public TaskConfig GetTaskConfigVisualization(int ID_Case,int ID_Trials) {
		TaskConfig result = new TaskConfig();
		try {

			ResultSet rs = null;
			Statement stmt = null;
			Connection conn;

			conn = createNewConnection();

			String SQL = "SELECT ID_Case, ID_Program, ID_Trials, ID_Map, Map, ID_Config, ConfigFile, Name_Program, Name_Map, Name_Config FROM "
					+ "dbo.TaskConfigVisualization WHERE ID_Case = "
					+ ID_Case + " AND ID_Trials = " +  ID_Trials;

			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);

			if (rs.next()) {
				result.ID_Case = rs.getInt("ID_Case");
				result.ID_Program = rs.getInt("ID_Program");
				result.ID_Trials = rs.getInt("ID_Trials");
				result.ID_Map = rs.getInt("ID_Map");
				result.Map = rs.getString("Map");
				result.ID_Config = rs.getInt("ID_Config");
				result.ConfigFile = rs.getString("ConfigFile");
				
				result.Name_Program = rs.getString("Name_Program");
				result.Name_Map =  rs.getString("Name_Map");
				result.Name_Config = rs.getString("Name_Config");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	
	public String  FixLogResult(long ID)
	{
		ArrayList<State> tempResult = new ArrayList<>();
		String result = "";
		
		try {

			ResultSet rs = null;
			Statement stmt = null;
			Connection conn;
			String sLog;
			String[] sSplitLog;
			String[] sSplitState;
			State tempState;
			long begin = 0;
			long stamp = 0;

			String resultTemp;
			
			conn = createNewConnection();

			String SQL = "select l.RobotPosition AS RobotPosition  from dbo.LogResult l where l.ID = " + ID;

			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);

			while(rs.next()) 
			{
				sLog = rs.getString("RobotPosition");
				sSplitLog = sLog.split("\n");
				
				
				for(int i = 0; i< sSplitLog.length; i++)
				{
					sSplitState = sSplitLog[i].split(";");
						
					resultTemp = sSplitLog[i].substring(0,sSplitLog[i].length() - (sSplitState[11].length() + 1));	
					
					if(i == 0)
					{					
						begin = Long.parseLong(sSplitState[11]);
						resultTemp += "0;\n";
					}
					else
					{
						stamp += Long.parseLong(sSplitState[11]) - begin;
						begin = Long.parseLong(sSplitState[11]);
						resultTemp +=  Long.toString(stamp) + ";\n";
					}
					
					result += resultTemp;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	public void UpdateLogResult(int ID,String robotPositon)
	{
		
		try {

			ResultSet rs = null;
			Statement stmt = null;
			Connection conn;

			conn = createNewConnection();
			
			//String SQL = "update dbo.LogResult set RobotPosition = '" + robotPositon + "'  where ID = " + ID;
			
			//PreparedStatement sta = conn.prepareStatement("update dbo.LogResult set RobotPosition = ? where ID = ? ");
					

			//stmt = conn.createStatement();
			
			//rs = stmt.executeQuery(SQL);
			
			//sta.setInt(1, ID);
			//sta.setString(2, robotPositon);
			
			
			//conn.prepareStatement("INSERT INTO dbo.Result(ID_Case ,ID_Program,ID_Trials, ID_Map,ID_Config,ID_Robot,LoopTime,TimeMilisecond,RobotPosition) VALUES(?,?,?,?,?,?,?,?,?);");
			/*
			sta.setInt(1, config.ID_Case);
			sta.setInt(2, config.ID_Program);
			sta.setInt(3, config.ID_Trials);
			sta.setInt(4, config.ID_Map);
			sta.setInt(5, config.ID_Config);
			sta.setInt(6, ID_Robot);
			sta.setInt(7, LoopTime);
			sta.setLong(8, TimeMilisecond);
			sta.setString(9, resultRobotPositon);*/
			
			//sta.executeUpdate();
			
			
			 PreparedStatement ps = conn.prepareStatement("update DoktoratLog.dbo.LogResult set RobotPosition = ? where ID = ?");

			 
			 ps.setString(1, robotPositon);
				ps.setInt(2, ID);
				
				    // set the preparedstatement parameters
				   // ps.setString(1,description);
				   // ps.setString(2,author);
				   // ps.setInt(3,id);
				   // ps.setInt(4,seqNum);

				    // call executeUpdate to execute our sql update statement
				    ps.executeUpdate();
				    ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	

}
