package vs17;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
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
		String requestedUrl = getRequestedUrl(request);
		if (requestedUrl.equals("/")) {
			response.append("200 OK" + endl);
			response.append(endl);
			for (Map.Entry<String, List<Integer>> entry : Central.getProductTable().entrySet()) {
				response.append(entry.getKey() + ": " + Collections.min(entry.getValue()) + endl);
			}
			return response.toString();
		} else if (requestedUrl.equals("/history")) {
			response.append("200 OK" + endl);
			response.append(endl);
			response.append(Central.getProductTable());
			return response.toString();
		}

		response.append("404 Not Found" + endl);
		response.append(endl);
		response.append(String.format("Not found.%nNo mapping for %s.%n", requestedUrl));
		return response.toString();
	}

	private String getRequestedUrl(final String request) {
		String url = "/blubb";
		try {
			url = request.split("\\s")[1];
		} catch (Exception ignored) {
		}
		return url;
	}
}
