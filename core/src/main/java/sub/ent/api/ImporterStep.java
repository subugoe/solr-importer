package sub.ent.api;

import java.io.PrintStream;
import java.util.Map;

/**
 * Parent class for all 'importer steps'.
 */
public abstract class ImporterStep {

	protected PrintStream out = System.out;

	/**
	 * This stream should be used for logging messages and errors.
	 */
	public void setLogOutput(PrintStream newOut) {
		out = newOut;
	}

	/**
	 * Runs the importer step with the given parameters.
	 */
	public abstract void execute(Map<String, String> params) throws Exception;

	/**
	 * Gives a short description of the importer step.
	 */
	public abstract String getStepDescription();
}
