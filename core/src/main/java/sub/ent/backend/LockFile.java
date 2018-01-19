package sub.ent.backend;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

/**
 * Manages the lifecycle of a lock file.
 * As long as the file is present, it should not be possible to start an import process.
 *
 */
public class LockFile {

	private Environment env = new Environment();
	private File lockFilePath;

	private void init() {
		File outputDir = new File(env.getVariable("OUTPUT_DIR"));
		lockFilePath = new File(outputDir, "lock");
	}

	/**
	 * Creates a lock file in the preconfigured directory.
	 * The directory is defined in an environment variable.
	 */
	public void create() throws IOException {
		init();
		FileUtils.writeStringToFile(lockFilePath, new Date().toString());
	}

	/**
	 * Checks if the lock file currently exists.
	 */
	public boolean exists() {
		init();
		return lockFilePath.exists();
	}

	/**
	 * Deletes the previously created lock file.
	 */
	public void delete() {
		init();
		try {
			FileUtils.forceDelete(lockFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
