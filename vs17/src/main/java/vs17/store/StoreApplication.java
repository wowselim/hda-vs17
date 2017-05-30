package vs17.store;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import vs17.shared.Products;

public class StoreApplication {
	private static StoreServer storeServer;
	private static Store.Processor<StoreServer> processor;

	private static int port;
	private static String producerHost;
	private static int producerPort;

	public static void main(String[] args) {
		try {
			port = Integer.parseInt(args[0]);
			producerHost = args[1];
			producerPort = Integer.parseInt(args[2]);
		} catch (Exception e) {
			throw new IllegalArgumentException("Usage: <port> <producerHost> <producerPort>", e);
		}
		try {
			storeServer = new StoreServer();
			processor = new Store.Processor<StoreServer>(storeServer);
			int productCount = Products.products.length;
			for (int i = 0; i < productCount; i++) {
				storeServer.addProduct(Products.products[i]);
			}
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						MqttClient client = new MqttClient("tcp://" + producerHost + ':' + producerPort, MqttClient.generateClientId());
						client.connect();
						while(true) {
							TimeUnit.SECONDS.sleep(5);
							MqttMessage msg = new MqttMessage(String.valueOf(10).getBytes());
							int random = ThreadLocalRandom.current().nextInt(Products.products.length);
							client.publish("request/" + Products.products[random], msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, "NachfragePublisherThread").start();

			new Thread(new Runnable() {
				@Override
				public void run() {
					MqttClient client;
					try {
						client = new MqttClient("tcp://" + producerHost + ':' + producerPort, "AngebotSubscriber #1");
						client.connect();
						client.subscribe("offer/+", new IMqttMessageListener() {
							@Override
							public void messageArrived(String topic, MqttMessage msg) throws Exception {
								System.out.println("Special Offer: " + topic.split("/")[1] + " being sold for "
										+ new String(msg.getPayload()) + ".");
							}
						});
					} catch (MqttException e) {
						e.printStackTrace();
					}
				}
			}, "AngebotSubscriberThread").start();

			TServerTransport serverTransport;
			try {
				serverTransport = new TServerSocket(port);
				TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
				System.out.printf("Thrift serving on port %d.%n", port);
				server.serve();
			} catch (TTransportException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
