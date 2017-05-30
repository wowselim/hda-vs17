package vs17.central;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HttpHandler implements Runnable {
	private static final String endl = System.getProperty("line.separator");
	private Socket socket;

	public HttpHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try (BufferedReader clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter clientOut = new PrintWriter(socket.getOutputStream())) {

			StringBuilder sb = new StringBuilder();
			String line;
			try {
				while ((line = clientIn.readLine()) != null && line.length() != 0) {
					sb.append(line + endl);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			String request = sb.toString();

			clientOut.println(getResponseForRequest(request));
			clientOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getResponseForRequest(final String request) {
		StringBuilder response = new StringBuilder("HTTP/1.1 ");
		String[] requestedUrl = getRequestedUrl(request);
		if (requestedUrl.length == 0) {
			response.append("200 OK" + endl);
			response.append(endl);
			for (Map.Entry<String, List<Integer>> entry : Central.getProductTable().entrySet()) {
				response.append(entry.getKey() + ": " + entry.getValue().get(entry.getValue().size() - 1) + endl);
			}
			return response.toString();
		} else if (requestedUrl[0].equals("history")) {
			response.append("200 OK" + endl);
			response.append(endl);
			response.append(Central.getProductTable());
			return response.toString();
		} else if (requestedUrl[0].equals("buy")) {
			String product = "[undefined]";
			if (requestedUrl.length == 2) {
				product = requestedUrl[1];
			}
			response.append("200 OK" + endl);
			response.append(endl);
			response.append(Central.buyProduct(product));
			return response.toString();
		}

		response.append("404 Not Found" + endl);
		response.append(endl);
		response.append(String.format("Not found.%nNo mapping for %s.%n", Arrays.toString(requestedUrl)));
		return response.toString();
	}

	private String[] getRequestedUrl(final String request) {
		String[] url = new String[] { "/blubb" };
		try {
			String route = request.split("\\s")[1];
			url = route.split("/");
		} catch (Exception ignored) {
		}
		if (url.length > 0) {
			url = Arrays.copyOfRange(url, 1, url.length);
		}
		return url;
	}
}
