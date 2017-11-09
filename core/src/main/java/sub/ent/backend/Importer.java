package sub.ent.backend;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class Importer {

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
