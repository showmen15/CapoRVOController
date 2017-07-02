package pl.agh.capo.simulation.visualization;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import pl.agh.capo.utilities.communication.StateReceivedCallback;
import pl.agh.capo.utilities.state.Location;
import pl.agh.capo.utilities.state.Point;
import pl.agh.capo.utilities.state.State;
import pl.agh.capo.utilities.state.Velocity;

public class StateCollectorVisualizer {

	private StateReceivedCallback StateReceivedCallback;
	private final ScheduledExecutorService controlScheduler = Executors.newScheduledThreadPool(1);
	
	private State[] Log;
	//private int visualizationStep;
	
	StateCollectorVisualizer(StateReceivedCallback stateReceivedCallback,State[] tempLog)
	{
		StateReceivedCallback = stateReceivedCallback;
		
		controlScheduler.scheduleAtFixedRate(this::run, 200, 200, TimeUnit.MILLISECONDS);
 
		Log = tempLog;
		//new Thread()
		//new Thread(new StateCollectorVisualizer()).start();;
	}
	
	public void run()
	{
		long deley;
		
		for(int k = 0; k < Log.length;k++)
		{
			if(k + 1 < Log.length)
				deley = Log[k + 1].getTimeStemp() - Log[k].getTimeStemp();
			else
				deley = 200;
		
		StateReceivedCallback.handle(Log[k]);

		try {
			Thread.sleep(deley);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		System.exit(1);	
	}	
}
