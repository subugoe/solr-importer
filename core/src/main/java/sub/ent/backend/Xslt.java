package sub.ent.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.MessageListener;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

/**
 * Wrapper for an XSLT script that can have input parameters.
 *
 */
public class Xslt {
	private Processor processor = new Processor(false);
	private XsltExecutable exe;
	private Map<String, String> parameters = new HashMap<String, String>();
	private PrintStream errorOut = System.out;
	private Set<String> unknownButProcessedElements = new HashSet<>();

	/**
	 * All error messages coming from the XSLT script will be printed here.
	 */
	public void setErrorOut(PrintStream newErrorOut) {
		errorOut = newErrorOut;
	}

	/**
	 * Sets the script that will be executed.
	 */
	public void setXsltScript(String xsltPath) throws SaxonApiException, FileNotFoundException {
		setXsltScript(new FileInputStream((new File(xsltPath))));
	}

	/**
	 * Sets the script that will be executed.
	 */
	public void setXsltScript(InputStream xsltStream) throws SaxonApiException {
		XsltCompiler comp = processor.newXsltCompiler();
		exe = comp.compile(new StreamSource(xsltStream));
	}

	/**
	 * Adds a parameter for the XSLT script. The parameter must be declared in the script.
	 */
	public void setParameter(String key, String value) {
		parameters.put(key, value);
	}

	/**
	 * Starts the transformation using the previously set XSLT script.
	 * If the script produces error messages, those will be written to the previously set error output.
	 */
	public void transform(final String inputXmlPath, OutputStream outputXmlStream) throws SaxonApiException {
		XdmNode source = processor.newDocumentBuilder().build(new StreamSource(new File(inputXmlPath)));
		Serializer out = processor.newSerializer();
		out.setOutputStream(outputXmlStream);
		XsltTransformer transformer = exe.load();
		for (Map.Entry<String, String> param : parameters.entrySet()) {
			transformer.setParameter(new QName(param.getKey()), new XdmAtomicValue(param.getValue()));
		}
		transformer.setInitialContextNode(source);
		transformer.setDestination(out);
		transformer.setMessageListener(new MessageListener() {
			public void message(XdmNode content, boolean terminate, SourceLocator locator) {
				String warnMessage = content.getStringValue();
				if (unknownButProcessedElements.contains(warnMessage)) {
					return;
				} else {
					unknownButProcessedElements.add(warnMessage);
					errorOut.println();
					errorOut.println("WARNING " + warnMessage);
					errorOut.println(inputXmlPath);
					errorOut.println();
				}
			}
		});
		transformer.transform();
	}
}
