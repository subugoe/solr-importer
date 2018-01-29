package sub.ent.backend;

/**
 * Measures the duration between two points in time.
 *
 */
public class Timer {

	private long start;
	private long stop;

	/**
	 * Starting point in time.
	 */
	public void setStart(long millis) {
		start = millis;
	}

	/**
	 * Last point in time.
	 */
	public void setStop(long millis) {
		stop = millis;
	}

	/**
	 * Generates a message containing the duration time.
	 */
	public String getDurationMessage() {
		long duration = stop - start;
		long minutes = duration / 1000 / 60;
		long seconds = (duration - minutes * 60 * 1000) / 1000;
		String potentialZero = seconds < 10 ? "0" : "";

		return "Took " + minutes + ":" + potentialZero + seconds + " minutes.";
	}
}
