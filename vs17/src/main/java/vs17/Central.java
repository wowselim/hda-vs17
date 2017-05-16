package vs17;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Central implements Runnable {
	private static Map<String, List<Integer>> productTable = new ConcurrentHashMap<>(Products.products.length);
	private static int dataPort = 1337;
	private static int managementPort = 1338;
	private static int httpPort = 8080;

	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			try {
				if (i == 0) {
					dataPort = Integer.parseInt(args[0]);
				} else if (i == 1) {
					managementPort = Integer.parseInt(args[1]);
				} else if (i == 2) {
					httpPort = Integer.parseInt(args[2]);
				}
			} catch (Exception e) {
				System.out.println("Usage: <dataPort> <managementPort> <httpPort>");
				e.printStackTrace();
			}
		}
		System.out.printf("Receiving data on %d.%n", dataPort);
		System.out.printf("Management port is %d.%n", managementPort);
		System.out.printf("Listening for HTTP connections on port %d.%n", httpPort);
		DatagramPacket packet = new DatagramPacket(new byte[128], 128);
		Thread managementThread = new Thread(new Central(), "Management Thread");
		managementThread.start();

		Thread httpHandlerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try (ServerSocket serverSocket = new ServerSocket(httpPort)) {
					while (true) {
						new Thread(new HttpHandler(serverSocket.accept())).start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				while (true) {

				}
			}
		}, "HttpHandler Thread");
		httpHandlerThread.setDaemon(true);
		httpHandlerThread.start();

		try (DatagramSocket socket = new DatagramSocket(dataPort)) {
			while (true) {
				socket.receive(packet);
				byte[] data = packet.getData();
				String dataAsString = new String(data);
				String[] parts = dataAsString.split("#");
				String product = parts[0];
				int amount = Integer.parseInt(parts[1]);
				List<Integer> value = productTable.get(product);
				if (value != null) {
					if (value.size() > 0) {
						int currentAmount = value.get(value.size() - 1);
						int delta = Math.abs(amount - currentAmount);
						if (delta > 0 && delta < 2) {
							// sensor is up to date, update value
							value.add(amount);
						}
					}
				} else {
					productTable.put(product, new ArrayList<>(Arrays.asList(amount)));
				}
				value = productTable.get(product);
				sendAcknowledge(socket, packet.getAddress(), packet.getPort(),
						parts[2].trim() + "#" + value.get(value.size() - 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendAcknowledge(DatagramSocket socket, InetAddress address, int port,
			String packetIdAndAmount) {
		try {
			DatagramPacket ackPacket = new DatagramPacket(Arrays.copyOf(packetIdAndAmount.getBytes(), 64), 64, address,
					port);
			socket.send(ackPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try (DatagramSocket managementSocket = new DatagramSocket(managementPort)) {
			while (true) {
				byte[] incomingData = new byte[1];
				DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
				managementSocket.receive(incomingPacket);

				byte[] outgoingData = new byte[512];
				byte[] dataToBeSent = productTable.toString().getBytes();
				System.arraycopy(dataToBeSent, 0, outgoingData, 0, dataToBeSent.length);
				DatagramPacket outgoingPacket = new DatagramPacket(outgoingData, outgoingData.length,
						incomingPacket.getAddress(), incomingPacket.getPort());
				managementSocket.send(outgoingPacket);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<String, List<Integer>> getProductTable() {
		return productTable;
	}
}
