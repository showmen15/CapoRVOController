package pl.agh.capo.controller.collision;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.maze.MazeMap;
import pl.agh.capo.utilities.maze.Wall;
import pl.agh.capo.utilities.state.Destination;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.Point;
import pl.agh.capo.utilities.state.Velocity;
import pl.agh.capo.utilities.RobotMotionModel;

import java.util.ArrayList;

public class WallCollisionDetector {

    private ArrayList<LineSegment> wallLineSegments = new ArrayList<>();

    private static final double MARGIN = EnvironmentalConfiguration.WALL_COLLISION_MARGIN_FACTOR * RobotMotionModel.ROBOT_HALF_DIAMETER;

    public WallCollisionDetector(MazeMap mazeMap) {
        for (Wall wall : mazeMap.getWalls()) {
            double x1 = wall.getFrom().getX();
            double y1 = wall.getFrom().getY();
            double x2 = wall.getTo().getX();
            double y2 = wall.getTo().getY();
            wallLineSegments.add(new LineSegment(x1, y1, x2, y2));
        }
    }

    public boolean collisionFree(Point position, Velocity velocity) {
        double x = position.getX() + velocity.getX();
        double y = position.getY() + velocity.getY();
        for (LineSegment segment : wallLineSegments) {
            if (segment.distance(new Coordinate(x, y)) < MARGIN) {
                return false;
            }
        }
        return true;
    }

    public boolean isDestinationVisible(Location location, Destination destination) {
        LineSegment direction = new LineSegment(location.getX(), location.getY(), destination.getX(), destination.getY());
        for (LineSegment lineSegment : wallLineSegments) {
            Coordinate[] closestPoints = lineSegment.closestPoints(direction);
            if (closestPoints[0].distance(closestPoints[1]) < RobotMotionModel.ROBOT_HALF_DIAMETER) {
                return false;
            }
        }
        return true;
    }
    
    public ArrayList<LineSegment> getWallLineSegments()
    {
    	return wallLineSegments;
    }
}