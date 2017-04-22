package vs17;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ManagementClient {
	public static void main(String[] args) {
		try {
			DatagramSocket clientSocket = new DatagramSocket(1338);
			DatagramPacket pingPacket = new DatagramPacket(new byte[1], 1, InetAddress.getByName("selim.co"), 1338);
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
