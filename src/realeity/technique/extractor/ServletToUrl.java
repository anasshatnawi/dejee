package realeity.technique.extractor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Anas Shatnawi
 * Email: anasshatnawi@gmail.com
 * 2016
 *
 *
 */

public class ServletToUrl {

	private String servletLocalName = null;
	private String className = null;
	private List<String> patterns = new ArrayList<String>();
	private boolean isJsp = false;

	public void setIsJsp(boolean isJsp) {
		this.isJsp = isJsp;
	}
	
	public boolean isJsp(){
		return this.isJsp;
	}
	public void setServletName(String name) {
		this.servletLocalName = name;
	}

	public String getServletName() {
		return this.servletLocalName;
	}

	public void setCalssName(String name) {
		this.className = name;
	}

	public String getClassName() {
		return this.className;
	}

	public List<String> getListofPatterns() {
		return this.patterns;
	}

	public void addPattern(String pattern) {
		// System.err.println("Add pattern: "+pattern);
		if (pattern.startsWith("/")){
			pattern= pattern.substring(1);
		}
		patterns.add(pattern);
	}

	public String toString() {
		String str = "\n----------------------------------";
		str += "\nServlet Local ID: " + this.servletLocalName;
		str += "\nCorresponding Class/JSP: " + this.className;
		str += "\nIs JSP file =: " + this.isJsp;
		str += "\n\tList of Patterns:";
		for (String s : patterns) {
			str += "\n\t" + s;
		}
		return str;
	}
}
