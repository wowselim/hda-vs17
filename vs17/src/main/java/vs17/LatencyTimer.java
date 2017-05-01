package vs17;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.HdrHistogram.Histogram;

public class LatencyTimer {
	private static Histogram histogram = new Histogram(3);
	private static Map<Long, Long> packetTimestamps = new ConcurrentHashMap<>();
	private static double[] desiredPercentiles = new double[] { 0.5, 0.8, 0.95, 0.99, 1.0 };
	private static TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			Map<Double, Long> percentiles = new TreeMap<>();
			for (double percentile : desiredPercentiles) {
				percentiles.put(percentile, histogram.getValueAtPercentile(percentile * 100.0) / 1_000_000);
			}
			System.out.println("Latencies: " + percentiles);
			packetTimestamps.clear();
			histogram.reset();
		}
	};

	public static void start() {
		new Timer("LatencyTimer", true).scheduleAtFixedRate(timerTask, 1_000L, 30_000L);
	}

	public static void startTime(long packetId) {
		packetTimestamps.put(packetId, System.nanoTime());
	}

	public static void stopTime(long packetId) {
		Long startTime = packetTimestamps.get(packetId);
		if (startTime != null) {
			histogram.recordValue(System.nanoTime() - startTime);
		}
	}
}
