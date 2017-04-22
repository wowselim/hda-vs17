package vs17;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Central implements Runnable {
	private static Map<String, List<Integer>> productTable = new TreeMap<>();

	public static void main(String[] args) throws Exception {
		DatagramSocket socket = new DatagramSocket(1337);
		DatagramPacket packet = new DatagramPacket(new byte[128], 128);
		Thread managementThread = new Thread(new Central(), "Management Thread");
		managementThread.start();

		while (true) {
			socket.receive(packet);
			byte[] data = packet.getData();
			String dataAsString = new String(data);
			String[] parts = dataAsString.split("#");
			String product = parts[0];
			int amount = Integer.parseInt(parts[1].trim());
			List<Integer> value = productTable.get(product);
			if (value != null) {
				if (!value.contains(amount)) {
					value.add(amount);
					System.out.printf("Updated value for %s to %d.%n", product, amount);
				}
			} else {
				productTable.put(product, new ArrayList<Integer>(Arrays.asList(amount)));
			}

			System.out.println(productTable);
		}
	}

	@Override
	public void run() {
		try {
			DatagramSocket managementSocket = new DatagramSocket(1338);
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