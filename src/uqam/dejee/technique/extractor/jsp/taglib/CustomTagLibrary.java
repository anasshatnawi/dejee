package uqam.dejee.technique.extractor.jsp.taglib;

import java.util.Vector;

import uqam.dejee.technique.extractor.xml.parser.TldXmlParser;

public class CustomTagLibrary {

	private static CustomTagLibrary uniqueInstance;
	
	/**
	 * Method insuring that a unique instance of CustomTagLibrary is created.
	 * 
	 * @return uniqueInstance
	 */	
	public static CustomTagLibrary getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new CustomTagLibrary();
		}
		return uniqueInstance;
	}
	
	String name ="";
	String info ="";

	

	Vector<CustomTag> customTags = new Vector<CustomTag>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void addCustomTag(CustomTag customTag){
		this.customTags.addElement(customTag);
	}
	
	public Vector<CustomTag> getCustomTags() {
		return customTags;
	}

	public void setCustomTags(Vector<CustomTag> customTags) {
		this.customTags = customTags;
	}
	
	public String toSting(){
		String str = "Taglib name: "; 
		str += "\n\tList of custom tags:";
		for (int i=0; i < customTags.size(); i++){
			str += "\n\t\t"+ customTags.elementAt(i).toString();
		}
		return str;
	}	
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
