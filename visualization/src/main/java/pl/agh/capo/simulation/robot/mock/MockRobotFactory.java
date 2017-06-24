package pl.agh.capo.simulation.robot.mock;

import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.maze.MazeMap;
import pl.agh.capo.utilities.state.Destination;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.controller.RobotController;
import pl.agh.capo.controller.collision.velocity.CollisionFreeVelocityType;
import pl.agh.capo.robot.IRobotManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MockRobotFactory {

    private static final String COMMENT_SIGN = "#";
    private static final String COLUMN_SEPARATOR = ";";

    public static List<RobotController> createMockRobotThreadsFromFile(MazeMap mazeMap, File confFile, IRobotManager robotManager) {
        List<RobotController> robotControllers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(confFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(COMMENT_SIGN)) {
                    continue;
                }
                String[] robotData = line.split(COLUMN_SEPARATOR);
                if (robotData.length < 6) {
                    continue;
                }
                RobotController robotController = createMockRobotController(
                        Integer.parseInt(robotData[0]),
                        EnvironmentalConfiguration.ROBOT_MAX_SPEED,
                        Double.parseDouble(robotData[1]),
                        mazeMap,
                        parseDestinationList(robotData),
                        robotManager);
                robotControllers.add(robotController);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return robotControllers;
    }
    
    private static List<Destination> parseDestinationList(String[] robotData) {
        int index = 2;
        List<Destination> destinationList = new ArrayList<>();
        while (index < robotData.length - 2) {
            double margin = Double.parseDouble(robotData[index]);
            double x = Double.parseDouble(robotData[index + 1]);
            double y = Double.parseDouble(robotData[index + 2]);
            destinationList.add(new Destination(x, y, margin, robotData.length - index == 3));
            index += 3;
        }
        return destinationList;
    }

    public static RobotController createMockRobotController(int id, double maxVelocity, double direction,
                                                            MazeMap mazeMap, List<Destination> destinationList, IRobotManager manager) {
        Destination start = destinationList.get(0);
        MockRobot mockRobot = new MockRobot(maxVelocity, new Location(start.getX(), start.getY(), direction));
        RobotController capoController = new RobotController(id, destinationList, mazeMap, mockRobot, manager, EnvironmentalConfiguration.COLLISIONFREEVELOCITYMETHOD,null);
        return capoController;
    }
}
