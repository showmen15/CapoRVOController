package pl.agh.capo.utilities.communicationUDP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.util.concurrent.TimeoutException;

import pl.agh.capo.utilities.state.State;

public class StatePublisherUDP extends StateConnectorUDP {

	 private StatePublisherUDP() {
	    }

	    public static StatePublisherUDP createAndEstablishConnection() {
	    	StatePublisherUDP statePublisher = new StatePublisherUDP();
	        try {
	            statePublisher.createChannel();
	            statePublisher.setIsConnectionEstablished(true);
	        } catch (IOException e) {
	            //logger.error(e.getMessage());
	            statePublisher.setIsConnectionEstablished(false);
	        } finally {
	            return statePublisher;
	        }
	    }
	    
	    public void publishRobotState(State state) {
	        try {
	            ByteArrayOutputStream bos = new ByteArrayOutputStream();
	            ObjectOutput out = new ObjectOutputStream(bos);
	            out.writeObject(state);
	            
	            byte[] bufferSend = bos.toByteArray();
	            
	            DatagramPacket packet = new DatagramPacket(bufferSend, bufferSend.length, addressGroup, 4446);
	            
	            //System.out.println("Publish: " + state.getRobotId() + " Time: " + state.TimeStamp);
	            socket.send(packet);

	        } catch (IOException e) {
	           // logger.error("ERROR SENDING: " + e.getMessage());
	        }
	    }
	
}



////public class StatePublisher extends StateConnector {
////
////    private static final Logger logger = Logger.getLogger(StatePublisher.class);
//
//    private StatePublisher() {
//    }
//
//    public static StatePublisher createAndEstablishConnection() {
//        StatePublisher statePublisher = new StatePublisher();
//        try {
//            statePublisher.createChannel();
//            statePublisher.setIsConnectionEstablished(true);
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//            statePublisher.setIsConnectionEstablished(false);
//        } catch (TimeoutException e) {
//            logger.error(e.getMessage());
//            statePublisher.setIsConnectionEstablished(false);
//        } finally {
//            return statePublisher;
//        }
//    }
//
//    public void publishRobotState(State state) {
//        try {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            ObjectOutput out = new ObjectOutputStream(bos);
//            out.writeObject(state);
//            
//            byte[] temp = bos.toByteArray();
//            int tt = temp.length;
//            
//            ByteArrayInputStream bis = new ByteArrayInputStream(temp);
//            ObjectInput in = new ObjectInputStream(bis);
//            State state1 = (State) in.readObject();
//            
//            
//            System.out.println("Publish: " + state.getRobotId() + " Time: " + state.TimeStamp);
//            channel.basicPublish(EnvironmentalConfiguration.CHANNEL_NAME, "", null, bos.toByteArray());
//        } catch (IOException | ClassNotFoundException e) {
//            logger.error("ERROR SENDING: " + e.getMessage());
//        }
//    }
//}
