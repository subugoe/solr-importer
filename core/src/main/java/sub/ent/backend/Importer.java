package sub.ent.backend;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;

import sub.ent.api.ImporterStep;

public class Importer {

	protected List<ImporterStep> steps;
	public void setSteps(List<ImporterStep> newSteps) {
		steps = newSteps;
	}

	public void executeAllSteps() {
		for (ImporterStep step : steps) {
			step.execute(new HashMap<>());
		}
	}

	public String getContext() throws IOException {
		String contextContent = "";
		InputStream contextStream = Importer.class.getResourceAsStream("/context.xml");
		if (contextStream != null) {
			contextContent = IOUtils.toString(contextStream, "UTF-8");
			return contextContent;
		}
		contextStream = Importer.class.getResourceAsStream("/context-default.xml");
		contextContent = IOUtils.toString(contextStream, "UTF-8");
		return contextContent;
		
	}
}
