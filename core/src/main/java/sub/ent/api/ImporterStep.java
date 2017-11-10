package sub.ent.api;

import java.io.PrintStream;
import java.util.Map;

public abstract class ImporterStep {

	protected PrintStream out = System.out;

	public void setLogOutput(PrintStream newOut) {
		out = newOut;
	}

	public abstract void execute(Map<String, String> params);
}
