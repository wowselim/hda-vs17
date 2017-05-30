package vs17.central;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import vs17.shared.Products;
import vs17.store.StoreClient;

public class Central implements Runnable {
	private static Map<String, List<Integer>> productTable = new ConcurrentHashMap<>(Products.products.length);
	private static int dataPort = 1337;
	private static int managementPort = 1338;
	private static int httpPort = 8080;
	private static List<String> stores = new ArrayList<>();

	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			try {
				if (i == 0) {
					dataPort = Integer.parseInt(args[0]);
				} else if (i == 1) {
					managementPort = Integer.parseInt(args[1]);
				} else if (i == 2) {
					httpPort = Integer.parseInt(args[2]);
				} else {
					String[] parts = args[i].split(":");
					stores.add(parts[0] + ':' + parts[1]);
				}
			} catch (Exception e) {
				System.out.println("Usage: <dataPort> <managementPort> <httpPort> <storeHost:storePort>[0..n]");
				e.printStackTrace();
			}
		}
		System.out.printf("Receiving data on %d.%n", dataPort);
		System.out.printf("Management port is %d.%n", managementPort);
		System.out.printf("Listening for HTTP connections on port %d.%n", httpPort);
		System.out.printf("Registered %d stores.%n", stores.size());
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

	public static String buyProduct(String product) {
		if (Arrays.asList(Products.products).contains(product)) {
			String cheapestStore = "[undefined]";
			int cheapestStorePort = -1;
			int cheapestPrice = Integer.MAX_VALUE;
			for (String store : stores) {
				String[] parts = store.split(":");
				String host = parts[0];
				int port = Integer.parseInt(parts[1]);
				int price = StoreClient.requestPrice(product, host, port);
				if (price < cheapestPrice) {
					cheapestPrice = price;
					cheapestStore = host;
					cheapestStorePort = port;
				}
			}
			if (cheapestStore.equals("[undefined]") || cheapestStorePort == -1) {
				return "No stores registered";
			}
			List<Integer> productHistory = productTable.get(product);
			if (productHistory != null) {
				StoreClient.buyProduct(product, 10, cheapestStore, cheapestStorePort);
				productHistory.add(10);
				return String.format("Bought 10 %s from %s.%n", product, cheapestStore);
			} else {
				return "Product not in fridge";
			}
		}
		return "Product not available";
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
