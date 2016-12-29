package pl.agh.capo.simulation;

import pl.agh.capo.utilities.communication.StateCollector;

import java.io.FileNotFoundException;

import pl.agh.capo.simulation.visualization.MazeVisualizer;

public class Main {

    public static void main(String[] args) {
        MazeVisualizer mazeVisualizer = MazeVisualizer.getInstance();
        try {
			mazeVisualizer.open();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        StateCollector stateCollector = StateCollector.createAndEstablishConnection(mazeVisualizer);
        if (!stateCollector.isConnectionEstablished()) {
            System.exit(1);
        }
    }

}
