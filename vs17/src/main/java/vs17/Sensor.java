package vs17;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

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

	private static List<Thread> threads = new ArrayList<>();

	private static long packetId;
	private static ReentrantLock mutex = new ReentrantLock();

	public Sensor(final String product) {
		this.instanceNo = ++instanceCount;
		this.product = product;
		this.amount = ThreadLocalRandom.current().nextInt(3, 9);
	}

	public void startTransmitting() {
		try {
			socket = new DatagramSocket();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		Runnable latencyRecorder = new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						byte[] ackBuffer = new byte[64];
						DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
						socket.receive(ackPacket);
						String ackResponse = new String(ackBuffer).trim();
						String[] ackResponseParts = ackResponse.split("#");
						long packetId = Long.parseLong(ackResponseParts[0]);
						int amount = Integer.parseInt(ackResponseParts[1]);
						LatencyTimer.stopTime(packetId);
						Sensor.this.amount = amount;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Thread latencyRecorderThread = new Thread(latencyRecorder,
				String.format("LatencyRecorder for sensor %d", instanceNo));
		latencyRecorderThread.setDaemon(true);
		threads.add(latencyRecorderThread);
		latencyRecorderThread.start();

		Thread thread = new Thread(this, String.format("Transmitter for sensor %d", instanceNo));
		thread.setDaemon(true);
		threads.add(thread);
		thread.start();

		Timer timer = new Timer(String.format("Timer for sensor %d", instanceNo), true);
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
		while (connected) {
			mutex.lock();
			try (ByteArrayOutputStream byteWriter = new ByteArrayOutputStream()) {
				byte[] msg;
				byte[] seperator = new byte[] { '#' };
				byte[] productBytes = product.getBytes();
				byte[] amountBytes = String.valueOf(amount).getBytes();
				byte[] packetIdBytes = String.valueOf(packetId++).getBytes();

				byteWriter.write(productBytes);
				byteWriter.write(seperator);
				byteWriter.write(amountBytes);
				byteWriter.write(seperator);
				byteWriter.write(packetIdBytes);

				msg = Arrays.copyOf(byteWriter.toByteArray(), 128);

				packet = new DatagramPacket(msg, msg.length, remoteHost, remotePort);
				socket.send(packet);
				LatencyTimer.startTime(packetId);

			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				mutex.unlock();
			}
			try {
				TimeUnit.SECONDS.sleep(1L);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		socket.close();
	}

	public static void main(String[] args) throws Exception {
		try {
			remoteHost = InetAddress.getByName(args[0]);
			remotePort = Integer.parseInt(args[1]);
			int numberOfSensors = Integer.parseInt(args[2]);
			String[] products = new String[] { "milk", "cheese", "ice cream", "banana", "tomato" };

			for (int i = 0; i < numberOfSensors; i++) {
				Sensor sensor = new Sensor(products[i % products.length]);
				sensor.startTransmitting();
			}
			System.out.printf("Started %d sensors for %d products.%n", threads.size(), products.length);

			LatencyTimer.start();
			System.out.println("Started latency recording.");

			for (Thread t : threads) {
				t.join();
			}
		} catch (Exception e) {
			System.out.println("Usage: <remoteHost> <remotePort> <numberOfSensors>");
			throw new RuntimeException(e);
		}
	}
}
