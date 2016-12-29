package pl.agh.capo.utilities.communication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.Logger;
import pl.agh.capo.utilities.EnvironmentalConfiguration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class StateConnector {

    private static final Logger logger = Logger.getLogger(StateConnector.class);

    protected Channel channel;
    private boolean isConnectionEstablished;

    protected void setIsConnectionEstablished(boolean value) {
        isConnectionEstablished = value;
    }

    public boolean isConnectionEstablished() {
        return isConnectionEstablished;
    }

    protected void createChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(EnvironmentalConfiguration.ADDRESS);
        factory.setUsername(EnvironmentalConfiguration.USERNAME);
        factory.setPassword(EnvironmentalConfiguration.PASSWORD);

        Connection connection = factory.newConnection();   
        channel = connection.createChannel();
        channel.exchangeDeclare(EnvironmentalConfiguration.CHANNEL_NAME, "fanout");
    }

    public void closeConnection() {
        if (channel == null) {
            return;
        }
        try {
            channel.close();
            channel.getConnection().close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (TimeoutException e) {
            logger.error(e.getMessage());
        }
        isConnectionEstablished = false;
    }
}
