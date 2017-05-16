package vs17;

import java.util.concurrent.ThreadLocalRandom;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

public class StoreApplication {
	private static StoreServer storeServer;
	private static Store.Processor<StoreServer> processor;

	private static int port;

	public static void main(String[] args) {
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			throw new IllegalArgumentException("Usage: <port>", e);
		}
		try {
			storeServer = new StoreServer();
			processor = new Store.Processor<StoreServer>(storeServer);
			int productCount = Products.products.length;
			for (int i = 0; i < productCount; i++) {
				if (ThreadLocalRandom.current().nextBoolean()) {
					storeServer.addProduct(Products.products[i]);
				}
			}

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
