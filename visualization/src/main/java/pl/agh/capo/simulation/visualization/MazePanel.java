package pl.agh.capo.simulation.visualization;

import pl.agh.capo.utilities.maze.Gate;
import pl.agh.capo.utilities.maze.MazeMap;
import pl.agh.capo.utilities.maze.Wall;
import pl.agh.capo.utilities.maze.helper.MazeHelper;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.Point;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;
import pl.agh.capo.utilities.RobotMotionModel;
import pl.agh.capo.velocityobstales.CollisionException;
import pl.agh.capo.velocityobstales.VelocityObstacles;
import pl.agh.capo.velocityobstales.VelocityObstaclesBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

class MazePanel extends JPanel {

    private final static Color[] ROBOT_COLORS = new Color[]{Color.black, Color.blue, Color.cyan, Color.green, Color.magenta, Color.orange, Color.pink, Color.yellow};

    private final static double MAZE_SIZE = 500.0;
    private final static double START_MAZE_COORDINATE = 50.0;

    private MazeMap map;
    private HashMap<Integer, State> robotStates = new HashMap<>();

    private double minY;
    private double minX;
    private double mazeSize;
    private double ratio;
    private double robotHalfDiameter;

    void addState(State state) {
        if (state.isFinished()){
            robotStates.remove(state.getRobotId());
        } else {
            robotStates.put(state.getRobotId(), state);
        }
        this.repaint(100);
    }

    void setMaze(MazeMap map) {
        this.map = map;
        calculateNormalizationData();
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        if (map == null) {
            return;
        }
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawCoordinateGrid(g2);
        drawGates(g2, map.getGates(), Color.cyan);
        drawWalls(g2, map.getWalls(), Color.gray);
        
        
        
      //  drawVelocityObstacles(g2, 0,8,Color.yellow);
//        drawVelocityObstacles(g2, 8,0,Color.green);
//        
//        
//        int index = 0;
//        
//        for(int i = 0; i < 13; i++)
//        {
//        
//        	
//        }
        
        
        // drawVelocityObstacles(g2, 8,0);
        
//        drawVelocityObstacles(g2, 0, 2);
//        drawVelocityObstacles(g2, 0, 3);
        drawStates(g2);
//        test(g2);
    }


    private void drawStates(Graphics2D g2) {
        try {
            for (State state : robotStates.values()) {
                drawState(g2, state);
            }
        } catch (ConcurrentModificationException ignored) {
        }
    }

    private void drawState(Graphics2D g2, State state) {
        g2.setColor(ROBOT_COLORS[state.getRobotId() % ROBOT_COLORS.length]);
        drawLocation(g2, state.getLocation());
        drawPoint(g2, state.getDestination());
        g2.setColor(Color.RED);
        g2.draw(new Line2D.Double(
        		normalizeCoordinate(state.getLocation().getX(), minX),
                normalizeCoordinate(state.getLocation().getY(), minY),
                normalizeCoordinate(state.getLocation().getX() + state.getVelocity().getX(), minX),
                normalizeCoordinate(state.getLocation().getY() + state.getVelocity().getY(), minY)
        ));
        
        double poX =  normalizeCoordinate(state.getLocation().getX() + state.getVelocity().getX(), minX);
        double poY = normalizeCoordinate(state.getLocation().getY() + state.getVelocity().getY(), minY);
        
        double pozX =  Math.round( state.getVelocity().getX()*1000.0)/1000.0; 
        double pozY =  Math.round(state.getVelocity().getY()*1000.0)/1000.0; 
        
   //     g2.drawString(" X: " + pozX + " Y: " + pozY, (float) poX -3, (float) poY -3);
        
        int x = (int) normalizeCoordinate(state.getLocation().getX(), minX) + 20;
        int y = (int) normalizeCoordinate(state.getLocation().getY(), minY) + 20;
        String tempFear = String.format("%.2f", state.getRobotFearFactor()); //  Double.toString();
        
        g2.drawString(tempFear,x, y);
    }

    private void drawPoint(Graphics2D g2, Point point) {
        double size = MAZE_SIZE / 100.0;
        g2.draw(new Ellipse2D.Double(
                normalizeCoordinate(point.getX(), minX) - size / 2.0,
                normalizeCoordinate(point.getY(), minY) - size / 2.0,
                size, size
        ));
    }

    private void drawLocation(Graphics2D g2, Location location) {
        double x = normalizeCoordinate(location.getX(), minX);
        double y = normalizeCoordinate(location.getY(), minY);
        g2.draw(new Ellipse2D.Double(x - robotHalfDiameter, y - robotHalfDiameter, robotHalfDiameter * 2, robotHalfDiameter * 2));
        g2.draw(new Line2D.Double(x, y, x + robotHalfDiameter * 2 * Math.cos(location.getDirection()), y + robotHalfDiameter * 2 * Math.sin(location.getDirection())));
    }

    private void drawGates(Graphics2D g, java.util.List<Gate> gates, Color color) {
        g.setStroke(new BasicStroke(1));
        g.setColor(color);
        for (Gate gate : gates) {
            drawGate(gate, g);
        }
    }

    private void drawGate(Gate gate, Graphics2D g) {
        double x1 = normalizeCoordinate(gate.getFrom().getX(), minX);
        double x2 = normalizeCoordinate(gate.getTo().getX(), minX);
        double y1 = normalizeCoordinate(gate.getFrom().getY(), minY);
        double y2 = normalizeCoordinate(gate.getTo().getY(), minY);

        g.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    private void drawWalls(Graphics2D g, java.util.List<Wall> walls, Color color) {
        g.setStroke(new BasicStroke(3));
        g.setColor(color);
        for (Wall wall : walls) {
            drawWall(wall, g);
        }
    }

    private void drawWall(Wall wall, Graphics2D g) {
        double x1 = normalizeCoordinate(wall.getFrom().getX(), minX);
        double x2 = normalizeCoordinate(wall.getTo().getX(), minX);
        double y1 = normalizeCoordinate(wall.getFrom().getY(), minY);
        double y2 = normalizeCoordinate(wall.getTo().getY(), minY);

        g.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    private double normalizeCoordinate(double val, double min) {
        return ((val - min) * ratio) + START_MAZE_COORDINATE;
    }

    private void calculateNormalizationData() {
        double minY = MazeHelper.getMinY(map.getWalls());
        double maxY = MazeHelper.getMaxY(map.getWalls());
        double minX = MazeHelper.getMinX(map.getWalls());
        double maxX = MazeHelper.getMaxX(map.getWalls());

        double height = maxY - minY;
        double width = maxX - minX;
        this.minX = minX;
        this.minY = minY;
        this.mazeSize = Math.ceil(Math.max(maxX, maxY));
        if (height > width) {
            ratio = MAZE_SIZE / height;
        } else {
            ratio = MAZE_SIZE / width;
        }
        robotHalfDiameter = RobotMotionModel.ROBOT_HALF_DIAMETER * ratio;
    }

    private void drawCoordinateGrid(Graphics2D g2) {
        for (double i = 0; i < mazeSize + 1; i++) {
            g2.setStroke(new BasicStroke(1));
            g2.setColor(Color.lightGray);
            double x1 = normalizeCoordinate(i, minX);
            double x2 = normalizeCoordinate(i, minX);
            double y1 = normalizeCoordinate(0.0, minY);
            double y2 = normalizeCoordinate(mazeSize, minY);
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
            x1 = normalizeCoordinate(0.0, minX);
            x2 = normalizeCoordinate(mazeSize, minX);
            y1 = normalizeCoordinate(i, minY);
            y2 = normalizeCoordinate(i, minY);
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
    }

    private void drawVelocityObstacles(Graphics2D g2, int robot1, int robot2,Color col) {
        State first = robotStates.get(robot1);
        State second = robotStates.get(robot2);
        if (first == null || second == null){
            return;
        }

        double px = first.getLocation().getX();
        double py = first.getLocation().getY();

        VelocityObstaclesBuilder vob = new VelocityObstaclesBuilder();
        vob.setFirstRobotCenter(first.getLocation())
                .setSecondRobotCenter(second.getLocation())
                .setFirstRobotVelocity(first.getVelocity())
                .setSecondRobotVelocity(first.getVelocity());
        
        VelocityObstacles vo;
        try {
            vo = vob.buildReciprocalVelocityObstacles();
        } catch (CollisionException e) {
            return;
        }

        g2.setColor(col);
        for (double x = -px; x < mazeSize - px; x += 0.05) 
        {
            for (double y = -py; y < mazeSize - py; y += 0.05) 
            {
                if (vo.inside(new Velocity(x, y))) 
                {
                    double dx = normalizeCoordinate(x + px, minX);
                    double dy = normalizeCoordinate(y + py, minY);
                    g2.draw(new Ellipse2D.Double(dx, dy, 1, 1));
                }
            }
        }
    }

    private void test(Graphics2D g2) {
        Location l = new Location(2.5, 2.5, Math.toRadians(0));

        g2.setColor(Color.red);
        Velocity v = new Velocity(0.5, 2);
        /*double alpha = Math.atan(v.getY() / v.getX());
        double cos = Math.cos(alpha);
        double sin = Math.sin(alpha);
        for (double x = 1.0; x >= -1.0; x -= 0.05) {
            double a = Math.sqrt(1.0 - (x * x));
            for (double y = a; y >= -a; y -= 0.05) {
                double x1 = x * cos - y * sin;
                double y1 = x * sin + y * cos;
                Velocity vvv = new Velocity(x1, y1);
                double angle = vvv.toVector2D().angle() - l.getDirection();
                if (Math.abs(angle) < Math.PI/2.0) {
                    g2.draw(new Ellipse2D.Double(
                            normalizeCoordinate(l.getX() + x1, minX),
                            normalizeCoordinate(l.getY() + y1, minY),
                            1, 1
                    ));
                }
            }
        }*/
        g2.setColor(Color.black);
        drawLocation(g2, l);
        g2.setColor(Color.green);
        g2.draw(new Line2D.Double(
                normalizeCoordinate(l.getX(), minX),
                normalizeCoordinate(l.getY(), minY),
                normalizeCoordinate(l.getX() + v.getX(), minX),
                normalizeCoordinate(l.getY() + v.getY(), minY)
        ));

        Velocity v2 = test2(new Velocity(0, -2));

        g2.setColor(Color.red);
        g2.draw(new Line2D.Double(
                normalizeCoordinate(l.getX(), minX),
                normalizeCoordinate(l.getY(), minY),
                normalizeCoordinate(l.getX() + v2.getX(), minX),
                normalizeCoordinate(l.getY() + v2.getY(), minY)
        ));

    }

    private Velocity test2(Velocity v) {

        double alpha = Math.atan2(v.getY(), v.getX());
        double cos = Math.cos(alpha);
        double sin = Math.sin(alpha);
        double speed = 1.0;
        double maxSpeed = 1.0;
        double step = (speed + maxSpeed) / 100.0;

        int i = 0;
        int max = 50;
        // TURN RIGHT
        System.out.print("R");
        for (double y = 0.0; y <= speed; y += step) {
            double maxX = Math.sqrt((maxSpeed * maxSpeed) - (y * y));
            for (double x = maxX; x >= 0.0; x -= step) {
                double x1 = x * cos - y * sin;
                double y1 = x * sin + y * cos;
                if (i == max) {
                    return new Velocity(x1, y1);
                } else {
                    i++;
                }
            }
        }
    double x;
        for (x = speed; x > 0.0; x -= step) {
            double maxY = Math.sqrt((maxSpeed * maxSpeed) - (x * x));
            for (double y = 0.0; y <= maxY; y += step) {
                double x1 = x * cos - y * sin;
                double y1 = x * sin + y * cos;
                if (i == max) {
                    return new Velocity(x1, y1);
                } else {
                    i++;
                }
            }
        }

        // TURN LEFT
        System.out.print("L");
        for (x = speed; x > 0.0; x -= step) {
            double maxY = Math.sqrt((maxSpeed * maxSpeed) - (x * x));
            for (double y = 0.0; y < maxY; y += step) {
                double x2 = x * cos + y * sin;
                double y2 = x * sin - y * cos;
                if (i == max) {
                    return new Velocity(x2, y2);
                } else {
                    i++;
                }
            }
        }

        // TURN BACK RIGHT SIDE
        System.out.print("BR");
        for (x = 0.0; x >= -maxSpeed; x -= step) {
            double maxY = Math.sqrt((maxSpeed * maxSpeed) - (x * x));
            for (double y = 0.0; y <= maxY; y += step) {
                for (double xx = x; x > 0.0; x -= step) {
                    double x1 = x * cos - y * sin;
                    double y1 = x * sin + y * cos;
                    if (i == max) {
                        return new Velocity(x1, y1);
                    } else {
                        i++;
                    }
                }
            }
        }

        // TURN BACK LEFT SIDE
        System.out.print("BL");
        for (x = 0.0; x > -maxSpeed; x -= step) {
            double maxY = Math.sqrt((maxSpeed * maxSpeed) - (x * x));
            for (double y = 0.0; y < maxY; y += step) {
                double x2 = x * cos + y * sin;
                double y2 = x * sin - y * cos;
                if (i == max) {
                    return new Velocity(x2, y2);
                } else {
                    i++;
                }
            }
        }
        return null;
    }
}
