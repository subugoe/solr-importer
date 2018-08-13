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
	protected SolrClient solr;
	protected String url;
	protected String core;
	protected String solrUser;
	protected String solrPassword;

	public void initialize(String solrUrl, String coreName) {
		if ("embedded".equals(solrUrl)) {
			// This is a little trick to use an embedded Server in integration testing
			solr = EmbeddedSolr.instance;
		} else {
			solr = new HttpSolrClient(solrUrl);
		}
		url = solrUrl;
		core = coreName;
	}
	
	public void setCredentials(String user, String password) {
		if (solr instanceof HttpSolrClient && !empty(user, password)) {
			solrUser = user;
			solrPassword = password;
			((HttpSolrClient)solr).setBaseURL(url.replace("://", "://" + user + ":" + password + "@"));
		}
	}
	
	private boolean empty(String... strings) {
		for (String s : strings) {
			if (s == null || "".equals(s))
				return true;
		}
		return false;
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
	
	public void flushFinishedDocs() throws IOException {
		try {
			if (!allDocs.isEmpty()) {
					solr.add(core, allDocs);
				allDocs.clear();
				allDocs = new ArrayList<>();
			}
		} catch (SolrServerException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Deletes all data in the previously defined core.
	 */
	public void cleanSolr() throws IOException {
		try {
			solr.deleteByQuery(core, "*:*");
		} catch (SolrServerException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Reloading the core is a best practice, because the Solr schema might have been changed.
	 */
	public void reloadCore() throws IOException {
		try {
			CoreAdminRequest adminRequest = new CoreAdminRequest();
			adminRequest.setAction(CoreAdminAction.RELOAD);
			adminRequest.setCoreName(core);
			adminRequest.process(solr);
		} catch (SolrServerException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Performs the actual commit.
	 * Must be executed after adding (finishing) all the documents.
	 */
	public void commitToSolr() throws IOException {
		try {
			flushFinishedDocs();
			solr.commit(core);
			solr.optimize(core);
		} catch (SolrServerException e) {
			throw new IOException(e);
		}
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
	public String getCoreDate() throws IOException {
		try {
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
		} catch (SolrServerException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Executes a core swap between the previously chosen core and the one in the argument.
	 * Note: the names are also switched, so executing this method twice with the same argument
	 * will result in the initial state.
	 */
	public void switchToCore(String swapCore) throws IOException {
		try {
			CoreAdminRequest adminRequest = new CoreAdminRequest();
			adminRequest.setAction(CoreAdminAction.SWAP);
			adminRequest.setCoreName(core);
			adminRequest.setOtherCoreName(swapCore);
			adminRequest.process(solr);
		} catch (SolrServerException e) {
			throw new IOException(e);
		}
	}

}
