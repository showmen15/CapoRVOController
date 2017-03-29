package pl.agh.capo.utilities.communicationUDP1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.concurrent.TimeoutException;

import pl.agh.capo.utilities.communication.StateReceivedCallback;
import pl.agh.capo.utilities.state.State;

public class StateCollectorUDP extends StateConnectorUDP {

	public int ID;
	
	private StateReceivedCallback stateReceivedCallback;

	private Thread reciverThr;

	private StateCollectorUDP(StateReceivedCallback stateReceivedCallback) {
		this.stateReceivedCallback = stateReceivedCallback;
	}

	public static StateCollectorUDP createAndEstablishConnection(StateReceivedCallback stateReceivedCallback) {
		StateCollectorUDP stateCollector = new StateCollectorUDP(stateReceivedCallback);
		try {
			stateCollector.openChannel();
			stateCollector.setIsConnectionEstablished(true);
		} catch (IOException | TimeoutException e) {
			stateCollector.setIsConnectionEstablished(false);
		} finally {
			return stateCollector;
		}
	}

	protected void openChannel() throws IOException, TimeoutException {
		createChannel();

		reciverThr = createConsumer();
		reciverThr.start();
	}

	private Thread createConsumer() {
		return new Thread(new Runnable() {
			public void run() {

				if (stateReceivedCallback == null) {
					return;
				}

				while (true) {
					try {
						DatagramPacket packet = new DatagramPacket(buf, buf.length);

						socket.receive(packet);

						ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData());
						ObjectInput in = new ObjectInputStream(bis);

						State state = (State) in.readObject();

						//System.out.println("Recive: " + state.getRobotId() + " Time: " + state.TimeStamp + " ID: ");

						stateReceivedCallback.handle(state);
					} catch (IOException | ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
	}
}

//
// public class StateCollector extends StateConnector {
//
// private static final Logger logger = Logger.getLogger(StateCollector.class);
//
// private StateReceivedCallback stateReceivedCallback;
//
// public int ID;
//
// private StateCollector(StateReceivedCallback stateReceivedCallback){
// this.stateReceivedCallback = stateReceivedCallback;
// }
//
// public static StateCollector
// createAndEstablishConnection(StateReceivedCallback stateReceivedCallback) {
// StateCollector stateCollector = new StateCollector(stateReceivedCallback);
// try {
// stateCollector.openChannel();
// stateCollector.setIsConnectionEstablished(true);
// } catch (IOException | TimeoutException e) {
// logger.error(e.getMessage());
// stateCollector.setIsConnectionEstablished(false);
// } finally {
// return stateCollector;
// }
// }
//
// protected void openChannel() throws IOException, TimeoutException {
// createChannel();
// String queueName = channel.queueDeclare().getQueue();
//
//
// channel.queueBind(queueName, EnvironmentalConfiguration.CHANNEL_NAME, "");
//
// logger.debug("[RABBIT] Waiting for messages");
// Consumer consumer = createConsumer();
// channel.basicConsume(queueName, true, consumer);
// }
//
// private Consumer createConsumer() {
// return new DefaultConsumer(channel) {
// @Override
// public void handleDelivery(String consumerTag, Envelope envelope,
// AMQP.BasicProperties properties, byte[] body) throws IOException {
// if (stateReceivedCallback == null) {
// return;
// }
// ByteArrayInputStream bis = new ByteArrayInputStream(body);
// ObjectInput in = new ObjectInputStream(bis);
// try {
// State state = (State) in.readObject();
//
// System.out.println("Recive: " + state.getRobotId() + " Time: " +
// state.TimeStamp + " ID: " + ID);
//
// stateReceivedCallback.handle(state);
// } catch (ClassNotFoundException e) {
// logger.debug("[RABBIT] Error parsing message");
// }
// }
// };
// }
//
// }
//
