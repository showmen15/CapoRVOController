package pl.agh.capo.utilities.communication;

import org.apache.log4j.Logger;
import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.state.State;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class StatePublisher extends StateConnector {

    private static final Logger logger = Logger.getLogger(StatePublisher.class);

    private StatePublisher() {
    }

    public static StatePublisher createAndEstablishConnection() {
        StatePublisher statePublisher = new StatePublisher();
        try {
            statePublisher.createChannel();
            statePublisher.setIsConnectionEstablished(true);
        } catch (IOException e) {
            logger.error(e.getMessage());
            statePublisher.setIsConnectionEstablished(false);
        } catch (TimeoutException e) {
            logger.error(e.getMessage());
            statePublisher.setIsConnectionEstablished(false);
        } finally {
            return statePublisher;
        }
    }

    public void publishRobotState(State state) {
        try {
        	
        	System.out.println("Robot: " + state.getRobotId() + " Publish: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.n")));

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(state);
            channel.basicPublish(EnvironmentalConfiguration.CHANNEL_NAME, "", null, bos.toByteArray());
        } catch (IOException e) {
            logger.error("ERROR SENDING: " + e.getMessage());
        }
    }
}
