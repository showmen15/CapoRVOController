package pl.agh.capo.simulation.robot.mock;

import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.RobotMotionModel;
import pl.agh.capo.robot.IRobot;

public class MockRobot implements IRobot {

    private final RobotMotionModel motionModel;
    private long lastMilliseconds;

    public MockRobot(double maxLinearVelocity, Location startLocation) {
        motionModel = new RobotMotionModel(maxLinearVelocity);
        motionModel.setLocation(startLocation);
        lastMilliseconds = System.currentTimeMillis();
    }

    @Override
    public Location getRobotLocation() {
        performMove();
        return motionModel.getLocation();
    }

    @Override
    public void setVelocity(double leftVelocity, double rightVelocity) {
        motionModel.setVelocity(leftVelocity, rightVelocity);
    }

    private void performMove() {
        synchronized (motionModel) {
            long currentMilliseconds = System.currentTimeMillis();
            int deltaTime = (int)(currentMilliseconds - lastMilliseconds);
            motionModel.performMoveByTimeInMilliseconds(deltaTime);
            lastMilliseconds = currentMilliseconds;
        }
    }
}
