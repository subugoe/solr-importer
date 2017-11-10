package sub.ent.backend;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sub.ent.api.ImporterStep;

public class Importer {

	private PrintStream out = System.out;
	protected List<ImporterStep> steps;

	public void setSteps(List<ImporterStep> newSteps) {
		steps = newSteps;
	}

	public void setLogOutput(PrintStream newOut) {
		out = newOut;
	}

	public int getNumberOfSteps() {
		return steps.size();
	}

	public void executeStep(int stepNumber, Map<String, String> parametersForAllSteps) {
		ImporterStep stepToExecute = steps.get(stepNumber);
		stepToExecute.setLogOutput(out);
		stepToExecute.execute(parametersForAllSteps);
	}

}
