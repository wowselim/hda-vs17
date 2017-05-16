package vs17;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.thrift.TException;

public class StoreServer implements Store.Iface {
	private Set<String> availableProducts;

	public StoreServer() {
		this.availableProducts = new HashSet<>();
	}

	@Override
	public int requestPrice(PriceRequest request) throws TException {
		String product = request.getProduct();
		int randomPrice = ThreadLocalRandom.current().nextInt(40, 750);
		boolean available = availableProducts.contains(product);
		return available ? randomPrice : -1;
	}

	@Override
	public void purchase(PurchaseRequest request) throws TException {
		String product = request.getProduct();
		int qty = request.getQty();
		System.out.printf("Sold %d of %s.%n", qty, product);
	}

	public void addProduct(String product) {
		availableProducts.add(product);
	}
}
