package pl.agh.capo.fear;

import java.util.*;

import com.vividsolutions.jts.math.Vector2D;
import com.vividsolutions.jts.math.Vector3D;

import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.maze.*;



public class Fear {

	public static final double MAXOBSERVATIONDISTANCE = 0.5;  //do sprawdzenia

	private int currentRobotID;
	private ArrayList<Vector3D> GatesList;
	private double RobotFearFactor;

	public Fear(int robotId,List<Gate> gates) throws Exception  {
		currentRobotID = robotId;
		GatesList = new ArrayList<Vector3D>();
		
		RobotFearFactor = 1.0 + 0.01 * (double) robotId;
			
		if(gates.size() > 1)		
			throw new Exception("Fear Za duzo drzwi!!!");
		else if(gates.size() == 1)
		{
			double centX =  0.0;
			double centY = 0.0; 
			
			if(gates.get(0).getFrom().getX() == gates.get(0).getTo().getX())
			{
				centX = gates.get(0).getFrom().getX();
				centY = (gates.get(0).getFrom().getY() + gates.get(0).getTo().getY()) / 2;
			}
			else
			{
				centX = (gates.get(0).getFrom().getX() + gates.get(0).getTo().getX()) / 2;											
				centY = gates.get(0).getFrom().getY();
			}
		
			GatesList.add(new Vector3D(centX, centY, -Math.PI / 2));
		
		}		
	}

	private double calculateFearFactor(Map<Integer, State> states,Location robotLocation, int robotId)
	{
		double robotFearFactor = calculateFearFactorOryginal(states, robotLocation, robotId);

		double gateFearFactor = 1.0;
		
		if(EnvironmentalConfiguration.ACTIVEFEARFACTORGATE && (GatesList.size() == 1))
			gateFearFactor = calculateFearFactorGate2(robotLocation, robotId, GatesList);

		return robotFearFactor * gateFearFactor;
	}	
	
	private double calculateFearFactorOryginal(Map<Integer, State> states,Location robotLocation, int robotId)
	{
		double robotFearFactor = 1.0 + 0.01 * (double) robotId;

		Vector2D robotVersor = new Vector2D(Math.cos(robotLocation.getDirection()), Math.sin(robotLocation.getDirection()));
		
		for (State otherTrajectory : states.values())
			{
				if (otherTrajectory.getRobotId() == robotId)
					continue;
				else
					if (robotLocation.getDistance(otherTrajectory.getLocation()) > MAXOBSERVATIONDISTANCE)
						continue;
					else
					{
						double angleBetweenRobots = robotVersor.angleTo(new Vector2D(Math.cos(otherTrajectory.getLocation().getDirection()), Math.sin(otherTrajectory.getLocation().getDirection())));

						if (Math.abs(angleBetweenRobots) < (Math.PI / 2))
						{
							robotFearFactor += ((MAXOBSERVATIONDISTANCE - robotLocation.getDistance(otherTrajectory.getLocation())) / MAXOBSERVATIONDISTANCE) * Math.cos(angleBetweenRobots) * (1.0 + 0.01 * (double) otherTrajectory.getRobotId());
						}
					}
			}
		
		return robotFearFactor;
	}
	
	private double calculateFearFactorGate2(Location robotLocation, int robotId, ArrayList<Vector3D> gateList)
	{
		double p;
		Vector2D temp_robotPosition;
		Vector2D temp_gatePosition;
		double temp_psi, temp_lambda;

		temp_gatePosition = new Vector2D(gateList.get(0).getX(), gateList.get(0).getY());
		temp_robotPosition = new Vector2D(robotLocation.getX(), robotLocation.getY());

		temp_psi = psi2(temp_robotPosition, temp_gatePosition);
		temp_lambda = lambda(robotLocation.getDirection(), gateList.get(0).getZ());

		p = 1 + temp_psi * temp_lambda;

		return p;
	}	
	
	private double psi2(Vector2D gateCenterPoint, Vector2D robotPosition)
	{
		double distanceGateRobot = getDistance(gateCenterPoint, robotPosition);
		double Rl = 1.0;
		double result;

		if (distanceGateRobot <= Rl)
			result = ((Rl - distanceGateRobot) / Rl);
		else
			result = 0;
		return result;
	}
		
	private double getDistance(Vector2D pointA, Vector2D pointB)
	{
		return Math.sqrt(Math.pow((pointA.getX() - pointB.getX()), 2.0) + (Math.pow((pointA.getY() - pointB.getY()), 2.0)));
	}
	
	private double lambda(double alfa, double gamma)
	{
		Vector2D robotAlfa = new Vector2D(Math.cos(alfa), Math.sin(alfa));
		Vector2D doorGamma = new Vector2D(Math.cos(gamma), Math.sin(gamma));
		double subAlfaGamma = robotAlfa.angleTo(doorGamma);

		double halfPI = Math.PI / 2;

		if ((subAlfaGamma >= (-halfPI)) && (subAlfaGamma <= halfPI))
			return 1;
		else
			return 0;
	}
	
	public double CalculateFearFactor(Map<Integer, State> states,Location robotLocation) {

			Location currentRobotLocation = robotLocation;//states.get(currentRobotID).getLocation();
			double robotFearFactor = calculateFearFactor(states, currentRobotLocation, currentRobotID);
		
		return  robotFearFactor;
	}

	public boolean HaveAvoidCollision(Map<Integer, State> states, double currentFearFactor) 
	{
		for (State current : states.values())
		{
			if(current.getRobotId() == currentRobotID)
				continue;
			else
			   if(currentFearFactor < current.getRobotFearFactor())
				   return true;						
		}
		
		return false;
	}
}
