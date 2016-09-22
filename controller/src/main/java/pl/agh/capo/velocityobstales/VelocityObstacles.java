package pl.agh.capo.velocityobstales;

import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.Point;
import pl.agh.capo.utilities.state.Velocity;
import pl.agh.capo.utilities.RobotMotionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class VelocityObstacles {

    private final double radius;
    private final double acceptableRadius;
    private final Point vertex;
    private final Location location;

    private Line firstBoundary;
    private Line secondBoundary;
    private boolean upperFirst;
    private boolean upperSecond;

    private Function<Point, Boolean> inside;

    protected VelocityObstacles(Location location, Point vertex, double radius) throws CollisionException {
        this.radius = radius;
        this.location = location;
        this.vertex = vertex;
        this.acceptableRadius = EnvironmentalConfiguration.ROBOT_MAX_SPEED <= 1.0
                ? RobotMotionModel.ROBOT_HALF_DIAMETER + (EnvironmentalConfiguration.ACCEPTABLE_RADIUS * EnvironmentalConfiguration.ROBOT_MAX_SPEED)
                : RobotMotionModel.ROBOT_HALF_DIAMETER + EnvironmentalConfiguration.ACCEPTABLE_RADIUS;
        init();
    }

    private void init() throws CollisionException {

        // finding intersected points of circles:
        //    1. Minowsky sum of robots surface boundary in center P = P_b - P_a = (x, y)
        //    2. circle with center in (0,0) and radius on tangent to first circle
        //       with length equals distance between center and point of contact
        // then create lines (boundaries) through calculated points and (0, 0)
        // move by suitable vector

        double distance = location.distanceFromOrigin();
        double radius = radius(distance * distance);
        double factor = factor(distance, this.radius, radius);

        double factorX1 = firstFactor(location.getX(), this.radius, radius, distance);
        double factorX2 = secondFactor(location.getY(), distance, factor);
        double factorY1 = firstFactor(location.getY(), this.radius, radius, distance);
        double factorY2 = secondFactor(location.getX(), distance, factor);

        Point p1 = new Point(factorX1 - factorX2 + vertex.getX(), factorY1 + factorY2 + vertex.getY());
        Point p2 = new Point(factorX1 + factorX2 + vertex.getX(), factorY1 - factorY2 + vertex.getY());
        createBoundaries(p1, p2);
        if (firstBoundary.b == 0.0 || secondBoundary.b == 0.0) {
            upperFirst = location.getX() < 0;
            upperSecond = upperFirst;
        } else if (firstBoundary.a == 0.0 || secondBoundary.a == 0.0) {
            upperFirst = location.getY() > 0;
            upperSecond = upperFirst;
        } else {
            upperFirst = firstBoundary.upper(p2);
            upperSecond = secondBoundary.upper(p1);
        }
        createInsideMethod();
    }

    private void createInsideMethod() {
        if (upperFirst && upperSecond) {
            inside = p -> firstBoundary.upper(p) && secondBoundary.upper(p);
        } else if (upperFirst) {
            inside = p -> firstBoundary.upper(p) && secondBoundary.under(p);
        } else if (upperSecond) {
            inside = p -> firstBoundary.under(p) && secondBoundary.upper(p);
        } else {
            inside = p -> firstBoundary.under(p) && secondBoundary.under(p);
        }
    }

    private void createBoundaries(Point p1, Point p2) {
        firstBoundary = new Line(vertex, p1);
        secondBoundary = new Line(vertex, p2);
    }

    public boolean inside(Velocity velocity) {
        if (location.distance(velocity) < acceptableRadius) {
            return true;
        }
        return inside.apply(velocity);
    }

    private double factor(double distance, double r1, double r2) {
        double f1 = distance + r1 + r2;
        double f2 = distance + r1 - r2;
        double f3 = distance - r1 + r2;
        double f4 = r1 + r2 - distance;
        double f5 = f1 * f2 * f3 * f4;
        double f6 = Math.sqrt(f5);
        return f6 / 4;
    }

    private double radius(double distance2) throws CollisionException {
        double radius2 = radius * radius;
        if (distance2 < radius2) {
            throw new CollisionException();
        }
        return Math.sqrt(distance2 - radius2);
    }

    private double firstFactor(double a, double radius1, double radius2, double distance) {
        double f1 = a / 2.0;
        double f2 = a * ((radius1 * radius1) - (radius2 * radius2));
        double f3 = f2 / (2.0 * distance * distance);
        return f1 - f3;
    }

    private double secondFactor(double a, double distance, double factor) {
        return (2.0 * a * factor) / (distance * distance);
    }

    private boolean isDoubleZero(double val) {
        return Math.abs(val) < 2 * Double.MIN_VALUE;
    }


    // Helper classes below:

    private class Line {
        // Line: (a * x) + (b * y) + c = 0
        private double a;
        private double b;
        private double c;

        public Line(Point p1, Point p2) {
            double xDiff = p1.getX() - p2.getX();
            double yDiff = p1.getY() - p2.getY();
            if (isDoubleZero(xDiff)) {
                a = 1.0;
                b = 0.0;
                c = -p1.getX();
            } else {
                a = yDiff / xDiff;
                b = -1.0;
                c = p1.getY() - (a * p1.getX());
            }
        }

        public boolean under(Point p) {
            return calculate(p) > 0;
        }

        public boolean upper(Point p) {
            return calculate(p) < 0;
        }

        public double distance(Point p) {
            double f1 = Math.abs(calculate(p));
            double f2 = Math.sqrt((a * a) + (b * b));
            return f1 / f2;
        }

        public List<Velocity> findVelocitiesDistantFromOrigin(double distance) {
            if (distance == 0.0) {
                List<Velocity> result = new ArrayList<>();
                result.add(new Velocity(0.0, 0.0));
                return result;
            }
            if (b == 0.0) {
                return findVelocitiesIfNotFunction(distance);
            } else {
                return findVelocitiesIfFunction(distance);
            }
        }

        private List<Velocity> findVelocitiesIfFunction(double distance) {
            List<Velocity> result = new ArrayList<>();
            double aa = this.a * this.a;
            double bb = this.b * this.b;
            double cc = this.c * this.c;
            double ac = this.a * this.c;

            double a = 1.0 + (aa / bb);
            double b = (2.0 * ac) / bb;
            double c = (cc / bb) - (distance * distance);
            double delta = (b * b) - (4.0 * a * c);

            double f1 = Math.sqrt(delta) / (2.0 * a);
            double f2 = -(b / (2.0 * a));

            double x1 = f2 - f1;
            double y1 = findY(x1);
            result.add(new Velocity(x1, y1));

            double x2 = f2 + f1;
            double y2 = findY(x2);
            result.add(new Velocity(x2, y2));

            return result;
        }

        private List<Velocity> findVelocitiesIfNotFunction(double distance) {
            List<Velocity> result = new ArrayList<>();
            double deltaY = Math.sqrt((distance * distance) - (c * c));
            result.add(new Velocity(c, deltaY));
            result.add(new Velocity(c, -deltaY));
            return result;
        }

        private double findY(double x) {
            return -(((a * x) + c) / b);
        }

        private double calculate(Point p) {
            return (a * p.getX()) + (b * p.getY()) + c;
        }
    }
}
