package sub.ent.backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;

/**
 * Manages the life cycle of a log file.
 *
 */
public class LogAccess {

	private File logFile;

	private void init() {
		File outputDir = new File(new Environment().getVariable("OUTPUT_DIR"));
		logFile = new File(outputDir, "log.txt");
	}

	/**
	 * Reads the contents of the current log file.
	 */
	public String getLogContents() {
		init();
		try {
			if (!logFile.exists()) {
				FileUtils.writeStringToFile(logFile, "No logs yet");
			}
			return FileUtils.readFileToString(logFile);
		} catch (IOException e) {
			e.printStackTrace();
			return "Could not access log file: " + e.getMessage();
		}
	}

	/**
	 * Finds out if the current log has errors etc.
	 */
	public String getStatusOfLastLog() {
		String log = getLogContents();
		if (log.contains("ERROR")) {
			return "ERROR";
		} else if (log.contains("WARNING")) {
			return "WARNING";
		} else if (log.contains("Took") && log.contains("minutes")){
			return "SUCCESS";
		} else {
			return "UNKNOWN";
		}
	}

	/**
	 * Recreates an empty log file.
	 */
	public void clear() {
		init();
		try {
			FileUtils.forceDelete(logFile);
			FileUtils.touch(logFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Connects to the current log file.
	 */
	public PrintStream getOutput() {
		init();
		PrintStream logOut = null;
		try {
			logOut = new PrintStream(logFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return logOut;
	}

	/**
	 * Appends a message to the current log file.
	 */
	public void append(String message) {
		init();
		PrintStream logOut = null;
		try {
			logOut = new PrintStream(logFile);
			logOut.println(message);
			System.out.println(message);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			close(logOut);
		}
	}

	private void close(PrintStream logOut) {
		if (logOut != null) {
			logOut.close();
		}
	}

}
