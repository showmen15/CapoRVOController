package pl.agh.capo;

import com.google.gson.Gson;
import pl.agh.capo.utilities.RobotMotionModel;
import pl.agh.capo.utilities.maze.MazeMap;
import pl.agh.capo.utilities.state.Destination;
import pl.agh.capo.controller.RobotController;
import pl.agh.capo.controller.collision.velocity.CollisionFreeVelocityType;
import pl.agh.capo.robot.Robot;
import pl.agh.capo.robot.RobotManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// build command: gradle clean jar
// call example:  java -jar controller.jar 127.0.0.1 map.roson 1 0.0#1.75#1.75#1.0#0.75#0.75#0.0#1.75#1.75 1
public class Main {

    public static void main(String[] args) throws IOException {
        Robot robot = new Robot(args[0]);
        MazeMap mazeMap = new Gson().fromJson(new FileReader(new File(args[1])), MazeMap.class);
        RobotController controller = new RobotController(
                Integer.parseInt(args[2]),
                Main.parseDestinations(args[3].split("#")),
                mazeMap,
                robot,
                new RobotManager(),
                Integer.parseInt(args[4]) == 1
                        ? CollisionFreeVelocityType.RIGHT_HAND
                        : CollisionFreeVelocityType.VELOCITY_OBSTACLES,
                        null
        );
        new Thread(controller).start();
    }

    private static List<Destination> parseDestinations(String[] robotData){
        int index = 0;
        List<Destination> destinationList = new ArrayList<>();
        while (index < robotData.length) {
            double margin = Double.parseDouble(robotData[index]);
            double x = Double.parseDouble(robotData[index + 1]);
            double y = Double.parseDouble(robotData[index + 2]);
            destinationList.add(new Destination(x, y, margin, false));
            index += 3;
        }
        Destination last = destinationList.get(destinationList.size() - 1);
        destinationList.add(new Destination(last.getX(), last.getY(), 0.0, true));
        return destinationList;
    }
}
