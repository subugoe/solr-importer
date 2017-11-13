package sub.ent.api;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;

import sub.ent.backend.FileAccess;
import sub.ent.backend.Uploader;

public class ImporterStepUpload extends ImporterStep {

	private Uploader uploader = new Uploader();
	private FileAccess fileAccess = new FileAccess();

	@Override
	public void execute(Map<String, String> params) throws Exception {
		String solrUrl = params.get("solrUrl");
		String core = params.get("solrImportCore");
		String solrXmlDir = params.get("solrXmlDir");
		uploader.setSolrEndpoint(solrUrl, core);
		try {
			List<File> xmls = fileAccess.getAllXmlFilesFromDir(new File(solrXmlDir));
			out.println();
			out.println("    Cleaning the import core.");
			uploader.cleanSolr();
			out.println("    Reloading the import core.");
			uploader.reloadCore();
			out.println("    Uploading index files:");
			int i = 1;
			for (File x : xmls) {
				printCurrentStatus(i, xmls.size());
				uploader.add(x);
				i++;
			}
			uploader.commitToSolr();
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
			out.println();
			out.println("Performing a rollback due to errors.");
			uploader.rollbackChanges();
			throw new IOException(e);
		}
	}

	private void printCurrentStatus(int currentNumber, int lastNumber) {
		if (currentNumber % 10000 == 0 || currentNumber == lastNumber) {
			out.println("    ... " + currentNumber);
		}
	}
}
