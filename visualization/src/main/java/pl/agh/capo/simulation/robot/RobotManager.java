package pl.agh.capo.simulation.robot;

import pl.agh.capo.utilities.maze.MazeMap;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.controller.RobotController;
import pl.agh.capo.robot.IRobotManager;
import pl.agh.capo.simulation.robot.mock.MockRobotFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class RobotManager implements IRobotManager {

    private static final int RUN_COUNT = 30;
    private final File robotConfig;
    private final MazeMap mazeMap;

    private int runCount;
    private List<Thread> robots;

    private Map<Integer, Integer> result;

    public RobotManager(File robotConfig, MazeMap mazeMap) {
        this.robotConfig = robotConfig;
        this.mazeMap = mazeMap;
        runCount = RUN_COUNT + 1;
        result = new TreeMap<>();
        restart();
    }

    private void restart() {
        runCount --;
        result.clear();
        robots = MockRobotFactory.createMockRobotThreadsFromFile(mazeMap, robotConfig, this)
                .stream().map(Thread::new).collect(Collectors.toList());
        robots.forEach(Thread::start);
    }

    @Override
    public void onFinish(int id, int time) {
        result.put(id, time);
        restartIfNeeded();
    }

    @Override
    public void onNewState(int id, boolean collide, State state) {

    }

    private void restartIfNeeded() {
        if (runCount > 0 && result.size() == robots.size()) {
            printResult();
            restart();
        }
    }

    private void printResult() {
        StringBuilder sb = new StringBuilder();
        for (int id : result.keySet()) {
            sb.append(String.format("%d;%d;%d\n", id, result.get(id), result.get(id) * RobotController.MOVE_ROBOT_PERIOD_IN_MS));
        }
        System.out.print(sb.toString());
    }
}
