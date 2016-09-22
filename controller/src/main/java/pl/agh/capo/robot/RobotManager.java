package pl.agh.capo.robot;

import pl.agh.capo.utilities.state.State;

public class RobotManager implements IRobotManager {

    @Override
    public void onFinish(int id, int time) {
        System.out.println(String.format("FINISH\nid: %d; loop count: %d", id, time));
    }

    @Override
    public void onNewState(int id, boolean collide, State state) {

    }
}
