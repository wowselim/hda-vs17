package vs17.store;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class StoreClient {
	public static int requestPrice(String product, String host, int port) {
		try {
			TTransport transport = new TSocket(host, port);
			transport.open();

			TProtocol protocol = new TBinaryProtocol(transport);
			Store.Client client = new Store.Client(protocol);
			int price = client.requestPrice(new PriceRequest(product));
			transport.close();
			return price;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static void buyProduct(String product, int qty, String host, int port) {
		try {
			TTransport transport = new TSocket(host, port);
			transport.open();

			TProtocol protocol = new TBinaryProtocol(transport);
			Store.Client client = new Store.Client(protocol);
			client.purchase(new PurchaseRequest(product, qty));
			transport.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
