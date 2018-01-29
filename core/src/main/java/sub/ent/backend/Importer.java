package sub.ent.backend;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sub.ent.api.ImporterStep;

/**
 * Wrapper for the importing functionality.
 * Contains 'importer steps' that should be executed one by one.
 *
 */
public class Importer {

	private PrintStream out = System.out;
	protected List<ImporterStep> steps;

	/**
	 * These steps will be executed.
	 */
	public void setSteps(List<ImporterStep> newSteps) {
		steps = newSteps;
	}

	/**
	 * The output stream will be passed to the importer steps.
	 */
	public void setLogOutput(PrintStream newOut) {
		out = newOut;
	}

	/**
	 * This has to be used in the executer class first, to then run the steps one by one.
	 */
	public int getNumberOfSteps() {
		return steps.size();
	}

	/**
	 * Runs the step number X.
	 * The parameters will be passed to each step.
	 * The steps must extract parameters that they need.
	 */
	public void executeStep(int stepNumber, Map<String, String> parametersForAllSteps) throws Exception {
		ImporterStep stepToExecute = steps.get(stepNumber);
		stepToExecute.setLogOutput(out);
		stepToExecute.execute(parametersForAllSteps);
	}

	/**
	 * The step descriptions can be used to make an overview of all the steps.
	 */
	public List<String> getAllStepDescriptions() {
		List<String> stepDescriptions = new ArrayList<>();
		if(steps != null) {
			for (ImporterStep step : steps) {
				stepDescriptions.add(step.getStepDescription());
			}
		}
		return stepDescriptions;
	}

}
