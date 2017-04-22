package vs17;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Sensor implements Runnable {
	private static int instanceCount;
	private int instanceNo;
	private String product;
	private volatile int amount;

	private volatile boolean connected = true;

	private DatagramSocket socket;
	private DatagramPacket packet;
	private static InetAddress remoteHost;
	private static int remotePort;

	public Sensor(final String product) {
		this.instanceNo = ++instanceCount;
		this.product = product;
		this.amount = ThreadLocalRandom.current().nextInt(3, 9);
	}

	public void startTransmitting() {
		try {
			socket = new DatagramSocket(8080);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Thread thread = new Thread(this, String.format("Transmitter for Sensor %d", instanceNo));
		thread.setDaemon(true);
		thread.start();

		Timer timer = new Timer(String.format("Timer for Sensor %d", instanceNo), true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (amount > 0) {
					amount--;
				} else {
					cancel();
				}
			}
		}, 1_000L, 10_000L);
	}

	public void stopTransmitting() {
		connected = false;
	}

	@Override
	public void run() {
		try {
			while (connected) {
				byte[] msg = new byte[128];
				byte[] productBytes = product.getBytes();
				byte[] seperator = new byte[] { '#' };
				byte[] amountBytes = String.valueOf(amount).getBytes();
				System.arraycopy(productBytes, 0, msg, 0, productBytes.length);
				System.arraycopy(seperator, 0, msg, productBytes.length, seperator.length);
				System.arraycopy(amountBytes, 0, msg, productBytes.length + 1, amountBytes.length);
				packet = new DatagramPacket(msg, msg.length, remoteHost, remotePort);
				socket.send(packet);
				System.out.println("Sent " + new String(packet.getData()));
				TimeUnit.SECONDS.sleep(1L);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			socket.close();
		}
	}

	public static void main(String[] args) throws Exception {
		remoteHost = InetAddress.getByName("localhost");
		remotePort = 1337;
		new Sensor("milk").startTransmitting();
		Thread.sleep(1000000L);
		/*
		 * try { remoteHost = InetAddress.getByName(args[0]); remotePort =
		 * Integer.parseInt(args[1]); } catch (Exception e) {
		 * System.out.println("Usage: <remoteHost> <remotePort>"); throw new
		 * RuntimeException(e); }
		 */
	}
}