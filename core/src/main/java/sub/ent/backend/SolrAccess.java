package sub.ent.backend;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CoreAdminParams.CoreAdminAction;

import sub.ent.testing.EmbeddedSolr;

public class SolrAccess {

	private SolrInputDocument currentSolrDoc;
	private List<SolrInputDocument> allDocs = new ArrayList<>();
	private SolrClient solr;
	private String url;
	private String core;

	public void initialize(String solrUrl, String coreName) {
		if ("embedded".equals(solrUrl)) {
			solr = EmbeddedSolr.instance;
		} else {
			solr = new HttpSolrClient(solrUrl);
		}
		url = solrUrl;
		core = coreName;
	}
	
	public void setCredentials(String user, String password) {
		if (solr instanceof HttpSolrClient) {
			((HttpSolrClient)solr).setBaseURL(url.replace("://", "://" + user + ":" + password + "@"));
		}
	}
	
	public void startDoc() {
		currentSolrDoc = new SolrInputDocument();
	}
	
	public void addFieldToStartedDoc(String fieldName, String fieldValue) {
		currentSolrDoc.addField(fieldName, fieldValue);
	}
	
	public void finishDoc() {
		allDocs.add(currentSolrDoc);
	}
	
	public int numberOfFinishedDocs() {
		return allDocs.size();
	}
	
	public void flushFinishedDocs() throws SolrServerException, IOException {
		if (!allDocs.isEmpty()) {
			solr.add(core, allDocs);
			allDocs.clear();
			allDocs = new ArrayList<>();
		}
	}

	/**
	 * Deletes all data in the previously defined core.
	 */
	public void cleanSolr() throws SolrServerException, IOException {
		solr.deleteByQuery(core, "*:*");
	}

	/**
	 * Reloading the core is a best practice, because the Solr schema might have been changed.
	 */
	public void reloadCore() throws SolrServerException, IOException {
		CoreAdminRequest adminRequest = new CoreAdminRequest();
		adminRequest.setAction(CoreAdminAction.RELOAD);
		adminRequest.setCoreName(core);
		adminRequest.process(solr);
	}

	/**
	 * Performs the actual commit.
	 * Must be executed after adding (finishing) all the documents.
	 */
	public void commitToSolr() throws SolrServerException, IOException {
		flushFinishedDocs();
		solr.commit(core);
		solr.optimize(core);
	}

	/**
	 * Tries to take Solr to the previous state if there is a failure during the import.
	 */
	public void rollbackChanges() {
		try {
			solr.rollback(core);
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Asks for the last modified date of the previously chosen core.
	 * 
	 * @return European date and time.
	 */
	public String getCoreDate() throws SolrServerException, IOException {
		CoreAdminRequest adminRequest = new CoreAdminRequest();
		adminRequest.setAction(CoreAdminAction.STATUS);
		adminRequest.setCoreName(core);
		CoreAdminResponse response = adminRequest.process(solr);
		Date coreDate = (Date) response.getCoreStatus().findRecursive(core, "index", "lastModified");
		if (coreDate != null) {
			DateFormat form = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.GERMANY);
			TimeZone timezone = TimeZone.getTimeZone("Europe/Berlin");
			form.setTimeZone(timezone);
			return form.format(coreDate);
		} else {
			return "leer";
		}
	}

	/**
	 * Executes a core swap between the previously chosen core and the one in the argument.
	 * Note: the names are also switched, so executing this method twice with the same argument
	 * will result in the initial state.
	 */
	public void switchToCore(String swapCore) throws SolrServerException, IOException {
		CoreAdminRequest adminRequest = new CoreAdminRequest();
		adminRequest.setAction(CoreAdminAction.SWAP);
		adminRequest.setCoreName(core);
		adminRequest.setOtherCoreName(swapCore);
		adminRequest.process(solr);
	}

}
