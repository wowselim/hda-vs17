package vs17.management;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ManagementClient {
	public static void main(String[] args) {
		String host = "";
		int port = -1;
		try {
			host = args[0];
			port = Integer.parseInt(args[1]);
		} catch (Exception e) {
			throw new RuntimeException("Usage: <remoteHost> <remotePort>", e);
		}

		try (DatagramSocket clientSocket = new DatagramSocket()) {
			System.out.printf("Sending ping packet to %s on port %d.%n", host, port);
			DatagramPacket pingPacket = new DatagramPacket(new byte[1], 1, InetAddress.getByName(host), port);
			clientSocket.send(pingPacket);
			byte[] incomingData = new byte[512];
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
			clientSocket.receive(incomingPacket);
			String history = new String(incomingPacket.getData()).trim();
			System.out.println(history);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
