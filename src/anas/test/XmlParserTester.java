package anas.test;

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uqam.dejee.technique.extractor.jsp.taglib.CustomAttribute;
import uqam.dejee.technique.extractor.jsp.taglib.CustomTag;
import uqam.dejee.technique.extractor.jsp.taglib.CustomTagLibrary;
import uqam.dejee.util.Statictics;

public class XmlParserTester {
	public static void main(String[] args) {

		try {
			File inputFile = new File("taglib.tld");
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			UserHandler userhandler = new UserHandler();
			saxParser.parse(inputFile, userhandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println(CustomTagLibrary.getInstance().toSting());
	}
}

class UserHandler extends DefaultHandler {

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
