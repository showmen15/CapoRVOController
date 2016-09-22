package pl.agh.capo.utilities.communication;

import pl.agh.capo.utilities.state.State;

public interface StateReceivedCallback {
	public void handle(State state);
}
