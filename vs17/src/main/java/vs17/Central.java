package vs17;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Central implements Runnable {
	private static Map<String, Set<Integer>> productTable = new TreeMap<>();
	private static int dataPort = 1337;
	private static int managementPort = 1338;

	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			try {
				if (i == 0) {
					dataPort = Integer.parseInt(args[0]);
				} else if (i == 1) {
					managementPort = Integer.parseInt(args[1]);
				}
			} catch (Exception e) {
				System.out.println("Usage: <dataPort> <managementPort>");
				e.printStackTrace();
			}
		}
		System.out.printf("Receiving data on %d.%n", dataPort);
		System.out.printf("Management port is %d.%n", managementPort);
		DatagramPacket packet = new DatagramPacket(new byte[128], 128);
		Thread managementThread = new Thread(new Central(), "Management Thread");
		managementThread.start();

		try (DatagramSocket socket = new DatagramSocket(dataPort)) {
			while (true) {
				socket.receive(packet);
				byte[] data = packet.getData();
				String dataAsString = new String(data);
				String[] parts = dataAsString.split("#");
				String product = parts[0];
				int amount = Integer.parseInt(parts[1]);
				Set<Integer> value = productTable.get(product);
				if (value != null) {
					if (value.add(amount)) {
						System.out.printf("Updated value for %s to %d.%n", product, amount);
					}
				} else {
					productTable.put(product, new HashSet<>(Arrays.asList(amount)));
				}
				sendAcknowledge(socket, packet.getAddress(), packet.getPort(), parts[2].trim());

				System.out.println(productTable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendAcknowledge(DatagramSocket socket, InetAddress address, int port, String packetId) {
		try {
			DatagramPacket ackPacket = new DatagramPacket(Arrays.copyOf(packetId.getBytes(), 32), 32, address, port);
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
}
