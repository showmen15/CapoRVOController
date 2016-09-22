package pl.agh.capo.simulation;

import pl.agh.capo.utilities.communication.StateCollector;
import pl.agh.capo.simulation.visualization.MazeVisualizer;

public class Main {

    public static void main(String[] args) {
        MazeVisualizer mazeVisualizer = MazeVisualizer.getInstance();
        mazeVisualizer.open();
        StateCollector stateCollector = StateCollector.createAndEstablishConnection(mazeVisualizer);
        if (!stateCollector.isConnectionEstablished()) {
            System.exit(1);
        }
    }

}
