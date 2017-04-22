package vs17;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;
import java.util.TreeMap;

public class Central {
	private static Map<String, Integer> productTable = new TreeMap<>();

	public static void main(String[] args) throws Exception {
		DatagramSocket socket = new DatagramSocket(1337);
		DatagramPacket packet = new DatagramPacket(new byte[128], 128);
		while (true) {
			socket.receive(packet);
			byte[] data = packet.getData();
			String dataAsString = new String(data);
			String[] parts = dataAsString.split("#");
			productTable.put(parts[0], Integer.parseInt(parts[1].trim()));

			System.out.println(productTable);
		}
	}
}
