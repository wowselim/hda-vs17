package vs17;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Sensor implements Runnable {
	private static int instanceCount;
	private int instanceNo;
	private String product;
	private volatile int amount;

	private volatile boolean connected;

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
		}, 1_000L, 3_000L);
	}

	public void stopTransmitting() {
		connected = false;
	}

	@Override
	public void run() {
		while (connected) {
			try {
				TimeUnit.SECONDS.sleep(5L);
				byte[] msg = (product + ',' + amount).getBytes();
				packet = new DatagramPacket(msg, msg.length, remoteHost, remotePort);
				socket.send(packet);
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				socket.close();
			}
		}
	}

	public static void main(String[] args) {
		try {
			remoteHost = InetAddress.getByName(args[0]);
			remotePort = Integer.parseInt(args[1]);
		} catch (Exception e) {
			System.out.println("Usage: <remoteHost> <remotePort>");
			throw new RuntimeException(e);
		}

		IntStream.range(0, 10).forEach(i -> {
			new Sensor("milk").startTransmitting();
		});
	}
}