package pl.agh.capo.utilities.communicationUDP1;

import java.io.*;
import java.net.*;
import java.util.*;

public abstract class StateConnectorUDP {

	protected boolean isConnectionEstablished;
	 
	 protected MulticastSocket socket;
	 protected InetAddress addressGroup;
	 protected  DatagramPacket packet;
	 protected  byte[] buf;

	    protected void setIsConnectionEstablished(boolean value) {
	        isConnectionEstablished = value;
	    }

	    public boolean isConnectionEstablished() {
	        return isConnectionEstablished;
	    }
	    
	    protected void createChannel() throws IOException {

	    	addressGroup = InetAddress.getByName("230.0.0.1");
	    	socket = new MulticastSocket(4446);	        
	        socket.joinGroup(addressGroup);
	        buf = new byte[1024]; //zmienis na siez of state
	    }
	    
	    public void closeConnection() {
	        try {
				socket.leaveGroup(addressGroup);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        socket.close();
	    	
	        isConnectionEstablished = false;
	    }
}

/*
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
        channel.basicQos(1000);
        //channel.bas
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
}*/
