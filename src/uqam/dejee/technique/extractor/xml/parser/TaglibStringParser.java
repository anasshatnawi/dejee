package uqam.dejee.technique.extractor.xml.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gmt.modisco.xml.emf.impl.TextImpl;

import uqam.dejee.technique.extractor.jsp.taglib.CustomAttribute;
import uqam.dejee.technique.extractor.jsp.taglib.CustomTag;

public class TaglibStringParser {

	public static CustomTag parseTextToCustomTag(TextImpl text) {
		CustomTag tag = new CustomTag();
		// divide them based on whilespace
		String[] mainParts = text.getName().split("[\\s\\xA0]+");
//		System.err.println("main parts:");
//		for (int i = 0; i < mainParts.length; i++) {
//			System.err.println("\t\t\t\t" + mainParts[i]);
//		}
		// that contains the prefix and the tag name
		if (mainParts[0] != null) {
			String[] firstParts = mainParts[0].split(":");
			tag.setName(firstParts[1]);
		}
		// that contains attributes
		if (mainParts.length > 0) {
			for (int i = 1; i < mainParts.length; i++) {
				String[] parts = mainParts[i].split("=");
//				System.err.println("---------------------------------attribute name :" + parts[0]);
				if (parts.length > 1) {
					CustomAttribute att = new CustomAttribute();

					att.setName(parts[0]);
					att.setValue(parts[1]);
					tag.addAttribute(att);
					System.err.println(att.toString());
				}
			}
		}
		return tag;
	}

	public static String removeBeforePrefix(String input, String word) {
		return input.substring(input.indexOf(word));
	}
}
