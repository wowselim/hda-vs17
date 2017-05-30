package vs17.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
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

		List<MqttClient> producers = new ArrayList<>();
		for (int i = 0; i < producerCount; i++) {
			MqttClient client = new MqttClient("tcp://" + host + ':' + port, "Producer #" + i);
			client.connect();
			producers.add(client);
		}

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
