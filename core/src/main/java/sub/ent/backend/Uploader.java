package sub.ent.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Manages the connection to the Solr server.
 *
 */
public class Uploader {

	private XMLEventReader eventReader;
	private final int MAX_DOCS = 2000;
	private SolrAccess solrAccess = new SolrAccess();

	private Set<String> ids = new HashSet<>();

	public void setSolrAccess(SolrAccess newAccess) {
		solrAccess = newAccess;
	}

	/**
	 * Adds a Solr XML file to be sent to Solr.
	 * The file is actually converted to a Java object first.
	 */
	public void add(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		try {
			add(is);
		} finally {
			is.close();
		}
	}

	private void add(InputStream is) throws IOException {
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

		if (solrAccess.numberOfFinishedDocs() >= MAX_DOCS) {
			solrAccess.flushFinishedDocs();
		}
	}

	private void handleStartElement(StartElement startTag) throws XMLStreamException {
		String name = startTag.getName().getLocalPart();
		if ("doc".equals(name)) {
			solrAccess.startDoc();
		}

		if (name.equals("field")) {
			String fieldName = startTag.getAttributeByName(new QName("name")).getValue();
			XMLEvent nextEvent = eventReader.peek();
			if (nextEvent.isCharacters()) {
				String fieldValue = nextEvent.asCharacters().getData();
				solrAccess.addFieldToStartedDoc(fieldName, fieldValue);

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
			solrAccess.finishDoc();
		}
	}

}
