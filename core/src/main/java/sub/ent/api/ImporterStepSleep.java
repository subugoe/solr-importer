package sub.ent.api;

import java.util.Map;

public class ImporterStepSleep extends ImporterStep {

	@Override
	public void execute(Map<String, String> params) throws Exception {
		Thread.sleep(10000);
	}

	@Override
	public String getStepDescription() {
		return "Leerlauf von 10 Sekunden";
	}

}
