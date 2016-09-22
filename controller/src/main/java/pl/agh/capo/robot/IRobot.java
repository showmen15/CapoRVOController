package pl.agh.capo.robot;

import pl.agh.capo.utilities.state.Location;

public interface IRobot {

    public void setVelocity(double leftVelocity, double rightVelocity);
    public Location getRobotLocation();

}
