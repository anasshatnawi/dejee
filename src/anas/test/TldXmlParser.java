package anas.test;

/*
 * Modified by Anas Shatnawi to parse .tld file of JSP tag library,
 * Based on David Flanagan Web.XML parser.  All rights reserved.
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
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import realeity.technique.extractor.ServletToUrl;
import uqam.dejee.technique.extractor.jsp.taglib.CustomAttribute;
import uqam.dejee.technique.extractor.jsp.taglib.CustomTag;
import uqam.dejee.technique.extractor.jsp.taglib.CustomTagLibrary;
import uqam.dejee.util.Statictics;

/**
 * Parse a web.xml file using the SAX2 API. This class extends DefaultHandler so
 * that instances can serve as SAX2 event handlers, and can be notified by the
 * parser of parsing events. We simply override the methods that receive events
 * we're interested in see:
 * http://www.java2s.com/Code/Java/Servlets/ParseawebxmlfileusingtheSAX2API.htm
 */
public class TldXmlParser extends org.xml.sax.helpers.DefaultHandler {

	/**
	 * Constructor of the class WebXmlParser. Its visibility is private in order
	 * not to allow the other classes to create many instances of WebXmlParser
	 */
	private TldXmlParser() {

	}

	/**
	 * unique instance of WebXmlParser.
	 */
	private static TldXmlParser uniqueInstance;

	/**
	 * Method insuring that a unique instance of WebXmlParser is created.
	 * 
	 * @return uniqueInstance
	 */
	public static TldXmlParser getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new TldXmlParser();
		}
		return uniqueInstance;
	}

	Vector<CustomTagLibrary> cutomTaglib = new Vector<CustomTagLibrary>();

	// String constants correspond to the XML tags used in TLD files.
	final String XML_TAGLIB = "taglib";
	final String XML_TAGLIB_NAME = "shortname";
	final String XML_TAGLIB_INRO = "info";
	final String XML_TAG = "tag";
	final String XML_TAG_NAME = "name";
	final String XML_TAG_CLASS = "tagclass";
	final String XML_TAG_INFO = "info";
	final String XML_ATTRIBUTE = "attribute";
	final String XML_ATTRIBUTE_NAME = "name";
	final String XML_ATTRIBUTE_IS_REQUIRED = "required";
	File tldFile = null;

	public static void main(String[] args) {

		try {
			File inputFile = new File("taglib.tld");
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			XmlHandler userhandler = new XmlHandler();
			saxParser.parse(inputFile, userhandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println(CustomTagLibrary.getInstance().toSting());
	}

	/** The main method sets things up for parsing */
	public void parseTldXmlFile(File selectedProject) throws IOException, SAXException, ParserConfigurationException {
		// System.err.println("Parsing web.xml...");
		findTldFile(selectedProject.getAbsolutePath());
		File xmlFile = tldFile;

		if (xmlFile == null) {
			System.err.println("taglib.tld file is null (not found)!");
			System.err.println(xmlFile);
		}

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			XmlHandler userhandler = new XmlHandler();
			saxParser.parse(tldFile, userhandler);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void findTldFile(String directoryName) {
		if (tldFile != null) {
			return;
		}
		File directory = new File(directoryName);
		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			// System.err.println("file Name:" + file.getName() + ".");
			if (file.isFile()) {
				if (file.getName().endsWith(".tld")) {
					tldFile = file;
					return;
				}
			} else if (file.isDirectory()) {
				findTldFile(file.getAbsolutePath());
			}
		}
	}
}

class XmlHandler extends DefaultHandler {

	boolean isTaglib = false;
	boolean isTaglibName = false;
	boolean isTaglibInfo = false;
	boolean isTag = false;
	boolean isTagName = false;
	boolean isTagClass = false;
	boolean isTagInfo = false;
	boolean isAttribute = false;
	boolean isAttributeName = false;
	boolean isAttributeIsRequired = false;
	boolean isAttributeisRtexprvalue = false;

	// As there are two different tags have the same id (the name tags for tag
	// and attribute), we use this to distenghwish
	boolean isTagScope = false;

	final String XML_TAGLIB = "taglib";
	final String XML_TAGLIB_NAME = "shortname";
	final String XML_TAGLIB_INRO = "info";
	final String XML_TAG = "tag";
	final String XML_TAG_NAME = "name";
	final String XML_TAG_CLASS = "tagclass";
	final String XML_TAG_INFO = "info";
	final String XML_ATTRIBUTE = "attribute";
	final String XML_ATTRIBUTE_NAME = "name";
	final String XML_ATTRIBUTE_IS_REQUIRED = "required";
	final String XML_ATTRIBUTE_IS_RTEXPRVALUE = "rtexprvalue";
	private CustomTag customTag = new CustomTag();
	private CustomAttribute customAttribute = new CustomAttribute();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (qName.equalsIgnoreCase(XML_TAGLIB)) {
			isTaglib = true;
		} else if (qName.equalsIgnoreCase(XML_TAGLIB_NAME)) {
			isTaglibName = true;
		} else if (qName.equalsIgnoreCase(XML_TAGLIB_INRO)) {
			isTaglibInfo = true;
		} else if (qName.equalsIgnoreCase(XML_TAG)) {
			isTag = true;
			isTagScope = true;
		} else if (qName.equalsIgnoreCase(XML_TAG_NAME) && isTagScope) {
			isTag = false;
			isTagName = true;
		} else if (qName.equalsIgnoreCase(XML_TAG_CLASS)) {
			isTagClass = true;
			isTagName = false;
		} else if (qName.equalsIgnoreCase(XML_TAG_INFO)) {
			isTagInfo = true;
			isTagClass = false;
		} else if (qName.equalsIgnoreCase(XML_ATTRIBUTE)) {
			isTagScope = false;
			isAttribute = true;
			isTagInfo = false;
		} else if (qName.equalsIgnoreCase(XML_ATTRIBUTE_NAME)) {
			isAttributeName = true;
			isAttribute = false;
		} else if (qName.equalsIgnoreCase(XML_ATTRIBUTE_IS_REQUIRED)) {
			isAttributeIsRequired = true;
			isAttributeName = false;
		} else if (qName.equalsIgnoreCase(XML_ATTRIBUTE_IS_RTEXPRVALUE)) {
			isAttributeisRtexprvalue = true;
			isAttributeIsRequired = false;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName.equalsIgnoreCase(XML_TAG)) {
			CustomTag temTag = new CustomTag();
			try {
				temTag = (CustomTag) customTag.clone();
			} catch (CloneNotSupportedException e) {
				System.err.println("Error in casting custom tag objects");
				e.printStackTrace();
			}
			CustomTagLibrary.getInstance().addCustomTag(temTag);
			customTag = null;
			customTag = new CustomTag();

		} else if (qName.equalsIgnoreCase(XML_ATTRIBUTE)) {
			// System.out.println(customAttribute.toString());
			CustomAttribute temAttribute = new CustomAttribute();
			try {
				temAttribute = (CustomAttribute) customAttribute.clone();
			} catch (CloneNotSupportedException e) {
				System.err.println("Error in casting custom attribute objects");
				e.printStackTrace();
			}
			customTag.addAttribute(temAttribute);
			customAttribute = null;
			customAttribute = new CustomAttribute();
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		String value = new String(ch, start, length);
		if (isTaglib) {
			Statictics.noOfTagLibs++;
			isTaglib = false;
		} else if (isTaglibName) {
			CustomTagLibrary.getInstance().setName(value);
			isTaglibName = false;
		} else if (isTaglibInfo) {
			CustomTagLibrary.getInstance().setInfo(value);
			isTaglibInfo = false;
		} else if (isTag) {
			customTag = new CustomTag();
			isTag = false;
		} else if (isTagName) {
			customTag.setName(value);
			isTagName = false;
		} else if (isTagClass) {
			customTag.setTagClass(value);
			isTagClass = false;
		} else if (isTagInfo) {
			customTag.setInfo(value);
			isTagInfo = false;
		} else if (isAttribute) {
			customAttribute = new CustomAttribute();
			isAttribute = false;
		} else if (isAttributeName) {
			customAttribute.setName(value);
			isAttributeName = false;
		} else if (isAttributeIsRequired) {
			if (value.equalsIgnoreCase("true")) {
				customAttribute.setRequired(true);
			} else if (value.equalsIgnoreCase("true")) {
				customAttribute.setRequired(false);
			}
			isAttributeIsRequired = false;
		} else if (isAttributeisRtexprvalue) {
			if (value.equalsIgnoreCase("true")) {
				customAttribute.setRtExprValue(true);
			} else if (value.equalsIgnoreCase("true")) {
				customAttribute.setRtExprValue(false);
			}
			isAttributeisRtexprvalue = false;
		}

	}

}
