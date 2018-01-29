package sub.ent.backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Access to local files and directories.
 *
 */
public class FileAccess {

	/**
	 * Gets all XML files recursively.
	 */
	public List<File> getAllXmlFilesFromDir(File dir) {
		List<File> xmls = new ArrayList<>();
		File[] children = dir.listFiles();
		for (File child : children) {
			if (child.isFile() && child.getName().endsWith("xml")) {
				xmls.add(child);
			} else if (child.isDirectory()) {
				xmls.addAll(getAllXmlFilesFromDir(child));
			}
		}
		return xmls;
	}

	/**
	 * Constructs an output stream for a given file.
	 */
	public OutputStream createOutputStream(File dir, String fileName) throws FileNotFoundException {
		return new FileOutputStream(new File(dir, fileName));
	}

	/**
	 * Creates a directory if it does not exist yet.
	 */
	public void makeSureThatExists(File outputDir) {
		if (!outputDir.exists()) {
			System.out.println("Creating directory: " + outputDir);
			boolean created = outputDir.mkdir();
			if (created) {
				System.out.println(outputDir + " created");
			}
		}
	}

	/**
	 * Deletes everything in a directory.
	 */
	public void cleanDir(File dir) throws IOException {
		if (dir.exists()) {
			FileUtils.cleanDirectory(dir);
		}
	}

}
