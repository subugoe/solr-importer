package sub.ent.api;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import sub.ent.backend.FileAccess;
import sub.ent.backend.SolrAccess;
import sub.ent.backend.Uploader;

/**
 * An importer step that sends XML files from a local directory to a Solr server.
 *
 */
public class ImporterStepUpload extends ImporterStep {

	private Uploader uploader = new Uploader();
	private FileAccess fileAccess = new FileAccess();
	private SolrAccess solrAccess = new SolrAccess();

	/**
	 * Prepares the Solr core, reads XML files, and sends them as Solr documents to Solr.
	 */
	@Override
	public void execute(Map<String, String> params) throws Exception {
		String solrUrl = params.get("solrUrl");
		String solrUser = params.get("solrUser");
		String solrPassword = params.get("solrPassword");
		String core = params.get("solrImportCore");
		String solrXmlDir = params.get("solrXmlDir");
		solrAccess.initialize(solrUrl, core);
		solrAccess.setCredentials(solrUser, solrPassword);
		uploader.setSolrAccess(solrAccess);
		try {
			List<File> xmls = fileAccess.getAllXmlFilesFromDir(new File(solrXmlDir));
			out.println();
			out.println("    Cleaning the import core.");
			solrAccess.cleanSolr();
			out.println("    Reloading the import core.");
			solrAccess.reloadCore();
			out.println("    Uploading index files:");
			int i = 1;
			for (File x : xmls) {
				printCurrentStatus(i, xmls.size());
				uploader.add(x);
				i++;
			}
			solrAccess.commitToSolr();
		} catch (IOException e) {
			e.printStackTrace();
			out.println();
			out.println("Performing a rollback due to errors.");
			solrAccess.rollbackChanges();
			throw e;
		}
	}

	private void printCurrentStatus(int currentNumber, int lastNumber) {
		if (currentNumber % 10000 == 0 || currentNumber == lastNumber) {
			out.println("    ... " + currentNumber);
		}
	}

	@Override
	public String getStepDescription() {
		return "Hochladen XML -> Solr";
	}
}
