package uqam.dejee.technique.extractor.jsp.taglib;

public class CustomAttribute implements Cloneable {

	String name = "";
	boolean isRequired;
	boolean isRtExprValue;
	String value = "";

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public CustomAttribute() {

	}

	public CustomAttribute(String name, boolean isRequired, boolean isRtExprValue) {
		this.name = name;
		this.isRequired = isRequired;
		this.isRtExprValue = isRtExprValue;
	}

	public String getName() {
		return name;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public boolean isRtExprValue() {
		return isRtExprValue;
	}

	public void setRtExprValue(boolean isRtExprValue) {
		this.isRtExprValue = isRtExprValue;
	}

	public String toString() {
		String str = "Attribure name: " + this.name + " , Value: "+ this.value;
		return str;
	}
}
