package uqam.dejee.technique.extractor.jsp.taglib;

import java.util.Vector;

public class CustomTag implements Cloneable{

	String name = "";
	String tagClass ="";
	String info ="";
	Vector<CustomAttribute> customAttributes = new Vector<CustomAttribute>();
	
	public Object clone() throws CloneNotSupportedException {
	    return super.clone();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTagClass() {
		return tagClass;
	}
	public void setTagClass(String tagClass) {
		this.tagClass = tagClass;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public Vector<CustomAttribute> getAttributes() {
		return customAttributes;
	}
	public void setAttributes(Vector<CustomAttribute> attributes) {
		this.customAttributes = attributes;
	}
	
	public void addAttribute(CustomAttribute customAttribute){
		this.customAttributes.addElement(customAttribute);
	}
	public String toString(){
		String str="Custom tag name: " + this.name;
		str+= "\n\t\t\tTag Class Handler: " + this.tagClass;
		str+= "\n\t\t\tother info: " + this.info;
		for(int i= 0; i < customAttributes.size(); i++){
			str+= "\n\t\t\t\t" + customAttributes.elementAt(i).toString();
		}
		return str;
	}
	
}
