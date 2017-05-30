package vs17.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import vs17.shared.Products;

public class Producer {
	public static void main(String[] args) throws Exception {
		String host = "";
		int port = 25565;
		int producerCount = 3;
		try {
			host = args[0];
			port = Integer.parseInt(args[1]);
			producerCount = Integer.parseInt(args[2]);
		} catch (Exception e) {
			throw new IllegalArgumentException("Usage: <host> <port> <numberOfProducers>", e);
		}
		final String fHost = host;
		final int fPort = port;

		List<MqttClient> producers = new ArrayList<>();
		for (int i = 0; i < producerCount; i++) {
			MqttClient client = new MqttClient("tcp://" + host + ':' + port, "Producer #" + i);
			client.connect();
			producers.add(client);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MqttClient client = new MqttClient("tcp://" + fHost + ':' + fPort, MqttClient.generateClientId());
					client.connect();

					client.subscribe("request/+", new IMqttMessageListener() {
						@Override
						public void messageArrived(String topic, MqttMessage msg) throws Exception {
							String product = topic.split("/")[1];
							String amount = new String(msg.getPayload());
							System.out.println(amount + " units of product " + product + " requested.");
						}
					});
				} catch (MqttException e) {
					e.printStackTrace();
				}
			}
		}, "NachfrageSubscriberThread").start();

		while (true) {
			for (MqttClient client : producers) {
				int random = ThreadLocalRandom.current().nextInt(Products.products.length);
				int randomPrice = ThreadLocalRandom.current().nextInt(100, 240);
				MqttMessage msg = new MqttMessage((randomPrice + " by " + client.getClientId()).getBytes());
				client.publish("offer/" + Products.products[random], msg);
			}
			TimeUnit.SECONDS.sleep(30);
		}
	}
}
