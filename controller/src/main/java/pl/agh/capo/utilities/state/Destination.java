package pl.agh.capo.utilities.state;

import pl.agh.capo.utilities.RobotMotionModel;

public class Destination extends Point {

    private final double margin;
    private final boolean isFinal;

    public Destination(double x, double y, double margin, boolean isFinal){
        super(x, y);
        this.margin = (1.0 + margin) * RobotMotionModel.ROBOT_HALF_DIAMETER;
        this.isFinal = isFinal;
    }

    public double getMargin() {
        return margin;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
