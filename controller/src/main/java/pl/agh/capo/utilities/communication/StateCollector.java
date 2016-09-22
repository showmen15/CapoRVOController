package pl.agh.capo.utilities.communication;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.apache.log4j.Logger;
import pl.agh.capo.utilities.EnvironmentalConfiguration;
import pl.agh.capo.utilities.state.State;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeoutException;

public class StateCollector extends StateConnector {

    private static final Logger logger = Logger.getLogger(StateCollector.class);

    private StateReceivedCallback stateReceivedCallback;

    private StateCollector(StateReceivedCallback stateReceivedCallback){
        this.stateReceivedCallback = stateReceivedCallback;
    }

    public static StateCollector createAndEstablishConnection(StateReceivedCallback stateReceivedCallback) {
        StateCollector stateCollector = new StateCollector(stateReceivedCallback);
        try {
            stateCollector.openChannel();
            stateCollector.setIsConnectionEstablished(true);
        } catch (IOException | TimeoutException e) {
            logger.error(e.getMessage());
            stateCollector.setIsConnectionEstablished(false);
        } finally {
            return stateCollector;
        }
    }

    protected void openChannel() throws IOException, TimeoutException {
        createChannel();
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EnvironmentalConfiguration.CHANNEL_NAME, "");

        logger.debug("[RABBIT] Waiting for messages");
        Consumer consumer = createConsumer();
        channel.basicConsume(queueName, true, consumer);
    }

    private Consumer createConsumer() {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                if (stateReceivedCallback == null) {
                    return;
                }
                ByteArrayInputStream bis = new ByteArrayInputStream(body);
                ObjectInput in = new ObjectInputStream(bis);
                try {
                    State state = (State) in.readObject();
                    stateReceivedCallback.handle(state);
                } catch (ClassNotFoundException e) {
                    logger.debug("[RABBIT] Error parsing message");
                }
            }
        };
    }

}

