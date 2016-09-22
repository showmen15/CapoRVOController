package pl.agh.capo.robot;

import pl.agh.capo.utilities.state.Location;
import pl.edu.agh.amber.common.AmberClient;
import pl.edu.agh.amber.location.LocationCurrent;
import pl.edu.agh.amber.location.LocationProxy;
import pl.edu.agh.amber.roboclaw.RoboclawProxy;

import java.io.IOException;

public class Robot implements IRobot {

    private final RoboclawProxy roboclawProxy;
    private final LocationProxy locationProxy;

    public Robot(String hostname) throws IOException {
        AmberClient amberClient = new AmberClient(hostname, 26233);
        this.roboclawProxy = new RoboclawProxy(amberClient, 0);
        this.locationProxy = new LocationProxy(amberClient, 0);
    }

    @Override
    public void setVelocity(double leftVelocity, double rightVelocity) {
        try {
            int robotLeftVelocity = (int) (leftVelocity * 1000.0);
            int robotRightVelocity = (int) (rightVelocity * 1000.0);
            System.out.println(String.format("LEFT: %d, RIGHT: %d", robotLeftVelocity, robotRightVelocity));
            roboclawProxy.sendMotorsCommand(robotLeftVelocity, robotRightVelocity, robotLeftVelocity, robotRightVelocity);
        } catch (IOException e) {
            System.out.println("Error in sending a command: " + e);
        }
    }

    @Override
    public Location getRobotLocation() {
        try {
            LocationCurrent locationCurrent = locationProxy.getCurrentLocation();
            locationCurrent.waitAvailable();
            System.out.println(String.format("X: %f, Y: %f, Alpha: %f", locationCurrent.getX(), locationCurrent.getY(), locationCurrent.getAngle()));
            return new Location(locationCurrent.getX(), locationCurrent.getY(), locationCurrent.getAngle());
        } catch (Exception e) {
            return null;
        }
    }
}
