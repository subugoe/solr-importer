package sub.ent.api;

import java.io.IOException;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;

import sub.ent.backend.CoreSwapper;

public class ImporterStepCoreSwap extends ImporterStep {

	private CoreSwapper swapper = new CoreSwapper();

	@Override
	public void execute(Map<String, String> params) throws Exception {
		String solrUrl = params.get("solrUrl");
		String core = params.get("solrImportCore");
		String swapCore = params.get("solrOnlineCore");

		out.println();
		out.println("    Switching to the online core: " + core + " -> " + swapCore);
		try {
			swapper.setSolrEndpoint(solrUrl, core);
			swapper.switchTo(swapCore);
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
			throw new IOException(e);
		}

	}

	@Override
	public String getStepDescription() {
		return "Online-Schaltung";
	}

}
