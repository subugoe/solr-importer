package sub.ent.backend;

import java.util.Map;

import sub.ent.api.ImporterStep;

public class ImporterStepUpload extends ImporterStep {

	@Override
	public void execute(Map<String, String> params) {
		out.println("    in step!!!!!!!!!!!!!!!!");
	}

}
