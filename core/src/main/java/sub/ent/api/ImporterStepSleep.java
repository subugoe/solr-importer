package sub.ent.api;

import java.util.Map;

/**
 * An importer step that just creates a pause.
 * Can be useful when used as the very first step to give the user time to cancel the whole import.
 */
public class ImporterStepSleep extends ImporterStep {

	/**
	 * Creates a short pause.
	 */
	@Override
	public void execute(Map<String, String> params) throws Exception {
		Thread.sleep(10000);
	}

	@Override
	public String getStepDescription() {
		return "Leerlauf von 10 Sekunden";
	}

}
