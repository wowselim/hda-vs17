package vs17;

import java.util.concurrent.ThreadLocalRandom;

import org.apache.thrift.TException;

public class StoreServer implements Store.Iface {
	@Override
	public int requestPrice(PriceRequest request) throws TException {
		String product = request.getProduct();
		int randomPrice = ThreadLocalRandom.current().nextInt(40, 750);
		boolean available = ThreadLocalRandom.current().nextBoolean();
		return available ? randomPrice : -1;
	}

	@Override
	public void purchase(PurchaseRequest request) throws TException {
		String product = request.getProduct();
		int qty = request.getQty();
		// do stuff
	}
}
