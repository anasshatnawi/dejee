package uqam.dejee.technique.extractor.xml.parser;

/*
 * Copyright (c) 2004 David Flanagan.  All rights reserved.
 * This code is from the book Java Examples in a Nutshell, 3nd Edition.
 * It is provided AS-IS, WITHOUT ANY WARRANTY either expressed or implied.
 * You may study, use, and modify it for any non-commercial purpose,
 * including teaching and use in open-source projects.
 * You may distribute it non-commercially as long as you retain this notice.
 * For a commercial use license, or to purchase the book, 
 * please visit http://www.davidflanagan.com/javaexamples3.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import realeity.technique.extractor.ServletToUrl;

/**
 * Parse a web.xml file using the SAX2 API. This class extends DefaultHandler so
 * that instances can serve as SAX2 event handlers, and can be notified by the
 * parser of parsing events. We simply override the methods that receive events
 * we're interested in see:
 * http://www.java2s.com/Code/Java/Servlets/ParseawebxmlfileusingtheSAX2API.htm
 */
public class WebXmlParser extends org.xml.sax.helpers.DefaultHandler {

	/**
	 * Constructor of the class WebXmlParser. Its visibility is private in order
	 * not to allow the other classes to create many instances of WebXmlParser
	 */
	private WebXmlParser() {

	}

	/**
	 * unique instance of WebXmlParser.
	 */
	private static WebXmlParser uniqueInstance;

	/**
	 * Method insuring that a unique instance of WebXmlParser is created.
	 * 
	 * @return uniqueInstance
	 */
	public static WebXmlParser getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new WebXmlParser();
		}
		return uniqueInstance;
	}

	HashMap nameToClass; // Map from servlet name to servlet class name
	HashMap nameToID; // Map from servlet name to id attribute
	HashMap nameToPatterns; // Map from servlet name to url patterns
	StringBuffer accumulator; // Accumulate text
	String servletName, servletClass, servletPattern; // Remember text
	String servletID; // Value of id attribute of <servlet> tag
	File webXmlFile = null;
	List<String> jspFiles = new ArrayList<String>();

	private List<ServletToUrl> servletToUrlsMapp = new ArrayList<ServletToUrl>();

	// /** The main method sets things up for parsing */
	// public static void main(String[] args) throws IOException, SAXException,
	// ParserConfigurationException {
	// // We use a SAXParserFactory to obtain a SAXParser, which
	// // encapsulates a SAXReader.
	// SAXParserFactory factory = SAXParserFactory.newInstance();
	// factory.setValidating(false); // We don't want validation
	// factory.setNamespaceAware(false); // No namespaces please
	// // Create a SAXParser object from the factory
	// SAXParser parser = factory.newSAXParser();
	// // Now parse the file specified on the command line using
	// // an instance of this class to handle the parser callbacks
	// parser.parse(new File(args[0]), new WebXmlParser());
	// }

	public List<ServletToUrl> getServletToUrlsMapp() {
		return servletToUrlsMapp;
	}

	/** The main method sets things up for parsing */
	public void parseXml(File selectedProject) throws IOException,
			SAXException, ParserConfigurationException {
		// System.err.println("Parsing web.xml...");
		findWebXmlFile(selectedProject.getAbsolutePath());
		File xmlFile = webXmlFile;

		if (xmlFile == null) {
			System.err.println("web.xml file = null");
			System.err.println(xmlFile);
		}
		// We use a SAXParserFactory to obtain a SAXParser, which
		// encapsulates a SAXReader.
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false); // We don't want validation
		factory.setNamespaceAware(false); // No namespaces please
		// Create a SAXParser object from the factory
		SAXParser parser = factory.newSAXParser();
		// Now parse the file specified on the command line using
		// an instance of this class to handle the parser callbacks
		parser.parse(xmlFile, this);
	}

	public void findWebXmlFile(String directoryName) {
		if (webXmlFile != null) {
			return;
		}
		File directory = new File(directoryName);
		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			// System.err.println("file Name:" + file.getName() + ".");
			if (file.isFile()) {
				if (file.getName().startsWith("web.xml")) {
					webXmlFile = file;
					return;
				}
			} else if (file.isDirectory()) {
				findWebXmlFile(file.getAbsolutePath());
			}
		}
	}

	// Called at the beginning of parsing. We use it as an init() method
	public void startDocument() {
		accumulator = new StringBuffer();
		nameToClass = new HashMap();
		nameToID = new HashMap();
		nameToPatterns = new HashMap();
	}

	// When the parser encounters plain text (not XML elements), it calls
	// this method, which accumulates them in a string buffer.
	// Note that this method may be called multiple times, even with no
	// intervening elements.
	public void characters(char[] buffer, int start, int length) {
		accumulator.append(buffer, start, length);
	}

	// At the beginning of each new element, erase any accumulated text.
	public void startElement(String namespaceURL, String localName,
			String qname, Attributes attributes) {
		accumulator.setLength(0);
		// If its a servlet tag, look for id attribute
		if (qname.equals("servlet"))
			servletID = attributes.getValue("id");
	}

	// Take special action when we reach the end of selected elements.
	// Although we don't use a validating parser, this method does assume
	// that the web.xml file we're parsing is valid.
	public void endElement(String namespaceURL, String localName, String qname) {
		// Since we've indicated that we don't want name-space aware
		// parsing, the element name is in qname. If we were doing
		// namespaces, then qname would include the name, colon and prefix,
		// and localName would be the name without the the prefix or colon.
		if (qname.equals("servlet-name")) { // Store servlet name
			servletName = accumulator.toString().trim();
		} else if (qname.equals("servlet-class") || qname.equals("jsp-file")) {
			// Store servlet class
			servletClass = accumulator.toString().trim();
			if (qname.equals("jsp-file")) {
				jspFiles.add(servletClass);
			}
		} else if (qname.equals("url-pattern")) { // Store servlet pattern
			servletPattern = accumulator.toString().trim();
		} else if (qname.equals("servlet")) { // Map name to class
			nameToClass.put(servletName, servletClass);
			nameToID.put(servletName, servletID);
		} else if (qname.equals("servlet-mapping")) {// Map name to pattern
			List patterns = (List) nameToPatterns.get(servletName);
			if (patterns == null) {
				patterns = new ArrayList();
				nameToPatterns.put(servletName, patterns);
			}
			patterns.add(servletPattern);
		}
	}

	// Called at the end of parsing. Used here to print our results.
	public void endDocument() {
		// Note the powerful uses of the Collections framework. In two lines
		// we get the key objects of a Map as a Set, convert them to a List,
		// and sort that List alphabetically.
		List servletNames = new ArrayList(nameToClass.keySet());
		Collections.sort(servletNames);
		// Loop through servlet names

		for (Iterator iterator = servletNames.iterator(); iterator.hasNext();) {
			ServletToUrl servlet = new ServletToUrl();
			String name = (String) iterator.next();
			// For each name get class and URL patterns and print them.

			String classname = (String) nameToClass.get(name);
			String id = (String) nameToID.get(name);
			List patterns = (List) nameToPatterns.get(name);
			// System.err.println("Servlet: " + name);
			System.err.print("");
			servlet.setServletName(name);
			// System.err.println("Class: " + classname);
			System.err.print("");
			servlet.setCalssName(classname);
			if (id != null)
				// System.err.println("ID: " + id);
				System.err.print("");
			if (patterns != null) {
				// System.err.println("Patterns:");
				System.err.print("");
				for (Iterator i = patterns.iterator(); i.hasNext();) {
					// String p = i.next().toString();
					i.next();
					// System.err.println("\t" + i.next());
					// servlet.addPattern(p);
				}
				for (Object s : patterns) {
					String p = (String) s;
					// System.err.println("\tp:" + p);
					// System.err.println("sssss: " + s);
					servlet.addPattern(p);
				}
			}
			System.err.print("");
			// System.err.println(servlet.toString());
			servletToUrlsMapp.add(servlet);
			servlet = null;

		}
		for (String s: jspFiles){
//			System.err.println("JSP fiel:" +s);
			for (ServletToUrl svlt : servletToUrlsMapp) {
				if (svlt.getClassName().equals(s)){
					svlt.setIsJsp(true);
				}
			}
		}
		
		

	}

	// Issue a warning
	public void warning(SAXParseException exception) {
		System.err.println("WARNING: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
	}

	// Report a parsing error
	public void error(SAXParseException exception) {
		System.err.println("ERROR: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
	}

	// Report a non-recoverable error and exit
	public void fatalError(SAXParseException exception) throws SAXException {
		System.err.println("FATAL: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
		throw (exception);
	}
}