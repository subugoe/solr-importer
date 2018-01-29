package sub.ent.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CoreAdminParams.CoreAdminAction;

import sub.ent.testing.EmbeddedSolr;

/**
 * Manages the connection to the Solr server.
 *
 */
public class Uploader {

	private XMLEventReader eventReader;
	private SolrInputDocument currentSolrDoc;
	private List<SolrInputDocument> allDocs = new ArrayList<>();
	private final int MAX_DOCS = 2000;
	private SolrClient solr;
	private String core;

	private Set<String> ids = new HashSet<>();

	/**
	 * Defines the running Solr server to use.
	 */
	public void setSolrEndpoint(String solrUrl, String coreName) {
		if ("embedded".equals(solrUrl)) {
			solr = EmbeddedSolr.instance;
		} else {
			solr = new HttpSolrClient(solrUrl);
		}
		core = coreName;
	}

	/**
	 * Adds a Solr XML file to be sent to Solr.
	 * The file is actually converted to a Java object first.
	 */
	public void add(File file) throws SolrServerException, IOException {
		InputStream is = new FileInputStream(file);
		try {
			add(is);
		} finally {
			is.close();
		}
	}

	private void add(InputStream is) throws SolrServerException, IOException {
		XMLInputFactory factory = XMLInputFactory.newInstance();

		try {
			eventReader = factory.createXMLEventReader(is);

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				switch (event.getEventType()) {

				case XMLStreamConstants.START_ELEMENT:
					handleStartElement(event.asStartElement());
					break;
				case XMLStreamConstants.END_ELEMENT:
					handleEndElement(event.asEndElement());
					break;
				default:
					// ignore all of the other events
				}
			}

			eventReader.close();

		} catch (XMLStreamException e) {
			throw new IllegalArgumentException("Error reading XML", e);
		}

		if (allDocs.size() >= MAX_DOCS) {
			flushDocs();
		}
	}

	private void handleStartElement(StartElement startTag) throws XMLStreamException {
		String name = startTag.getName().getLocalPart();
		if ("doc".equals(name)) {
			currentSolrDoc = new SolrInputDocument();
		}

		if (name.equals("field")) {
			String fieldName = startTag.getAttributeByName(new QName("name")).getValue();
			XMLEvent nextEvent = eventReader.peek();
			if (nextEvent.isCharacters()) {
				String fieldValue = nextEvent.asCharacters().getData();
				currentSolrDoc.addField(fieldName, fieldValue);

				if (fieldName.equals("id")) {
					if (ids.contains(fieldValue)) {
						// System.out.println("double id: " + fieldValue);
					}
					ids.add(fieldValue);
				}
			}
		}

	}

	private void handleEndElement(EndElement endTag) {
		String name = endTag.getName().getLocalPart();
		if ("doc".equals(name)) {
			allDocs.add(currentSolrDoc);
		}
	}

	private void flushDocs() throws SolrServerException, IOException {
		if (!allDocs.isEmpty()) {
			solr.add(core, allDocs);
			allDocs.clear();
			allDocs = new ArrayList<>();
		}
	}

	/**
	 * Deletes all data in the predefined core.
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
	 * Must be executed after adding all the XML files.
	 */
	public void commitToSolr() throws SolrServerException, IOException {
		flushDocs();
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
}
