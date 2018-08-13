package sub.ent.api;

import java.io.IOException;
import java.util.Map;

import sub.ent.backend.SolrAccess;

/**
 * An importer step that switches the names of two Solr cores.
 *
 */
public class ImporterStepCoreSwap extends ImporterStep {

	private SolrAccess solrAccess = new SolrAccess();

	/**
	 * Changes the name of the offline core to that of the online core and vice versa.
	 */
	@Override
	public void execute(Map<String, String> params) throws Exception {
		String solrUrl = params.get("solrUrl");
		String core = params.get("solrImportCore");
		String swapCore = params.get("solrOnlineCore");
		String solrUser = params.get("solrUser");
		String solrPassword = params.get("solrPassword");

		out.println();
		out.println("    Switching to the online core: " + core + " -> " + swapCore);
		try {
			solrAccess.initialize(solrUrl, core);
			solrAccess.setCredentials(solrUser, solrPassword);
			solrAccess.switchToCore(swapCore);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException(e);
		}

	}

	@Override
	public String getStepDescription() {
		return "Online-Schaltung";
	}

}
