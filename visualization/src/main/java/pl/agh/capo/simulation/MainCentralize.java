package pl.agh.capo.simulation;

import java.io.FileNotFoundException;

import pl.agh.capo.simulation.visualization.MazeVisualizer;
import pl.agh.capo.simulation.visualization.MazeVisualizerCentralize;
import pl.agh.capo.utilities.communicationUDP.StateCollectorUDP;

public class MainCentralize {
	  public static void main(String[] args) {
		  MazeVisualizerCentralize mazeVisualizer = MazeVisualizerCentralize.getInstance();
	        try {
				mazeVisualizer.open();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        StateCollectorUDP stateCollector = StateCollectorUDP.createAndEstablishConnection(mazeVisualizer);
	        if (!stateCollector.isConnectionEstablished()) {
	            System.exit(1);
	        }
	    }
}
