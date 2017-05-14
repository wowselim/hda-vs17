package vs17;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

public class StoreApplication {
	public static void main(String[] args) {
		StoreServer storeServer;
		Store.Processor<StoreServer> processor;

		try {
			storeServer = new StoreServer();
			processor = new Store.Processor<StoreServer>(storeServer);
			TServerTransport serverTransport;
			try {
				serverTransport = new TServerSocket(8888);
				TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
				System.out.println("Thrift serving on port 8888.");
				server.serve();
			} catch (TTransportException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
