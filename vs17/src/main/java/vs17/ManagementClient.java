package vs17;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ManagementClient {
	public static void main(String[] args) {
		String host = "";
		int port = -1;
		if (args.length != 2) {
			throw new RuntimeException("Usage: <remoteHost> <remotePort>");
		}
		for (int i = 0; i < args.length; i++) {
			try {
				if (i == 0) {
					host = args[0];
				} else if (i == 1) {
					port = Integer.parseInt(args[1]);
				}
			} catch (Exception e) {
				throw new RuntimeException("Usage: <remoteHost> <remotePort>", e);
			}
		}

		DatagramSocket clientSocket = null;
		try {
			System.out.printf("Sending ping packet to %s on port %d.%n", host, port);
			clientSocket = new DatagramSocket();
			DatagramPacket pingPacket = new DatagramPacket(new byte[1], 1, InetAddress.getByName("selim.co"), 1338);
			clientSocket.send(pingPacket);
			byte[] incomingData = new byte[512];
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
			clientSocket.receive(incomingPacket);
			String history = new String(incomingPacket.getData()).trim();
			System.out.println(history);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (clientSocket != null) {
				clientSocket.close();
			}
		}
	}
}
