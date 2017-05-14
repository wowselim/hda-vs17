package vs17;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class StoreClient {
	public static void requestPrice(String host, int port) {
		try {
			TTransport transport = new TSocket(host, port);
			transport.open();

			TProtocol protocol = new TBinaryProtocol(transport);
			Store.Client client = new Store.Client(protocol);
			int price = client.requestPrice(new PriceRequest("banana"));
			System.out.println(price);
			transport.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		requestPrice("localhost", 8888);
	}
}
