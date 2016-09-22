package pl.agh.capo.robot;

import pl.agh.capo.utilities.state.State;

public interface IRobotManager {
    public void onFinish(int id, int time);
    public void onNewState(int id, boolean collide, State state);
}
