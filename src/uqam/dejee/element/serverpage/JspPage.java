package uqam.dejee.element.serverpage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.xml.Attribute;
import org.eclipse.gmt.modisco.xml.Element;
import org.eclipse.gmt.modisco.xml.Node;
import org.eclipse.gmt.modisco.xml.emf.impl.TextImpl;
import org.eclipse.modisco.jee.jsp.JSPComment;
import org.eclipse.modisco.jee.jsp.JSPDirective;
import org.eclipse.modisco.jee.jsp.JSPScriptlet;
import org.eclipse.modisco.jee.jsp.JSPStdAction;
import org.eclipse.modisco.jee.jsp.JSPTagLib;

import realeity.technique.extractor.ServletToUrl;
import uqam.dejee.technique.extractor.jsp.taglib.CustomTag;
import uqam.dejee.technique.extractor.xml.parser.TaglibStringParser;

public class JspPage {

	private String name;

	private AbstractCodeElement servletClass;

	private List<Element> htmlForms = new ArrayList<Element>();
	private List<Element> htmlHrefs = new ArrayList<Element>();
	private List<JSPDirective> jspDirectives = new ArrayList<JSPDirective>();
	private List<JSPScriptlet> jspScriptlets = new ArrayList<JSPScriptlet>();
	private List<JSPStdAction> jspStdActions = new ArrayList<JSPStdAction>();
	private List<JSPComment> jspComments = new ArrayList<JSPComment>();
	private List<JSPTagLib> jspTagLibs = new ArrayList<JSPTagLib>();

	// to stroe the text in the JSP model, some tags are considered as text
	private List<TextImpl> texts = new ArrayList<TextImpl>();

	// to store the texts that contain taglib prefex
	// remove all string before prefix
	private List<TextImpl> textsContainTaglibPrefix = new ArrayList<TextImpl>();

	// to store the list of Servlets that need to be called from the service
	// method of this jsp page
	private List<String> targetServletsUrlPatterns = new ArrayList<String>();

	// to save the set of JSP pages that need to be called from this page
	private List<String> targetJspPages = new ArrayList<String>();
	// to save the ser of servlet classes that need to be called from this page
	private List<String> targetServletClasses = new ArrayList<String>();
	// to save the set of url patterns that are not related to any servlet/jsp
	// in the web.xml file
	private List<String> notMappedURL = new ArrayList<String>();

	// to store the list of beans that are references in this jsp page
	private List<String> referencedBeans = new ArrayList<String>();

	public List<String> getReferencedBeans() {
		return referencedBeans;
	}

	public void setReferencedBeans(List<String> referencedBeans) {
		this.referencedBeans = referencedBeans;
	}

	public void identifyTextContainTaglibPrefex() {
		for (TextImpl text : texts) {
			for (JSPTagLib taglib : jspTagLibs) {
				EList<Node> nodes = taglib.getChildren();
				for (Node node : nodes) {
					if (node instanceof Attribute) {
						Attribute att = (Attribute) node;
						if (att.getName().equals("prefix")) {
							// System.err.println("\t\tprefix = " +
							// att.getValue());
							if (text.getName().contains(att.getValue())) {
								text.setName(TaglibStringParser.removeBeforePrefix(text.getName(), att.getValue()));
								textsContainTaglibPrefix.add(text);
							}
						}
					}
				}
			}
		}
	}

	// public void normalizedStringContainTaglibUsage() {
	// for (TextImpl txt : textsContainTaglibPrefix) {
	// String srt = TaglibStringParser.removeBeforePrefix(input, word)
	// }
	//
	// }

	public void mapURLtoServlet(List<ServletToUrl> servletToUrlsMapp) {
		// to be implemented: each url to identify the class name that need to
		// be called in the service method;
		for (String urlPattern : targetServletsUrlPatterns) {
			boolean isAdded = false;
			for (ServletToUrl svlt : servletToUrlsMapp) {
				if (svlt.getListofPatterns().contains(urlPattern)) {
					if (svlt.isJsp()) {
						targetJspPages.add(svlt.getClassName().replace(".jsp", "_jsp"));
					} else {
						// System.err.println("$$$$$ svlt.getClassName(): " +
						// svlt.getClassName() + " , urlPattern: "+urlPattern);
						targetServletClasses.add(svlt.getClassName());
					}
					isAdded = true;
				}
			}
			if (!isAdded) {
				if (urlPattern.endsWith(".jsp") || urlPattern.endsWith(".JSP")) {
					targetJspPages.add(urlPattern.replace(".jsp", "_jsp"));
				} else {
					// need to check annoutations pattern decliration
					notMappedURL.add(urlPattern);
				}
			}
		}
	}

	public void identifyTargetServlets() {
		identifyHtmlFormsServlets();
		identifyHrefServlets();
		identifyJspDirectiveServlets();
		identiftyScripletsServlets();
		identiftyJspStdActionServlets();
	}

	public List<TextImpl> getText() {
		return texts;
	}

	public void setText(List<TextImpl> text) {
		this.texts = text;
	}

	public void identiftyJspStdActionServlets() {
		for (JSPStdAction action : jspStdActions) {
			if (action.getName().equals("jsp:include")) {
				EList<Node> nodes = action.getChildren();
				for (Node node : nodes) {
					if (node instanceof Attribute) {
						Attribute att = (Attribute) node;
						if (att.getName().equals("page")) {
							String pattern = att.getValue();
							if (pattern.startsWith("/")) {
								pattern = pattern.substring(1);
							}
							targetServletsUrlPatterns.add(pattern);
						}
					}
				}
			} else if (action.getName().equals("jsp:forward")) {
				EList<Node> nodes = action.getChildren();
				for (Node node : nodes) {
					if (node instanceof Attribute) {
						Attribute att = (Attribute) node;
						if (att.getName().equals("page")) {
							String pattern = att.getValue();
							if (pattern.startsWith("/")) {
								pattern = pattern.substring(1);
							}
							targetServletsUrlPatterns.add(pattern);
						}
					}
				}
			} else if (action.getName().equals("jsp:useBean")) {
				EList<Node> nodes = action.getChildren();
				for (Node node : nodes) {
					if (node instanceof Attribute) {
						Attribute att = (Attribute) node;
						if (att.getName().equals("class")) {
							referencedBeans.add(att.getValue());
						}
					}
				}
			} else if (action.getName().equals("jsp:setProperty")) {

			} else if (action.getName().equals("jsp:getProperty")) {

			}
		}
	}

	public void identiftyScripletsServlets() {
		for (JSPScriptlet scriptlet : jspScriptlets) {
			EList<Node> nodes = scriptlet.getChildren();
			for (Node node : nodes) {
				if (node.getName().contains("getRequestDispatcher(")) {
					String servlet = identifyReqDispatureLine(node.getName());
					if (servlet != null) {
						if (servlet.startsWith("/")) {
							servlet = servlet.substring(1);
						}
						targetServletsUrlPatterns.add(servlet);
					}
				}
			}
		}
	}

	public String identifyReqDispatureLine(String code) {
		String servlet = null;
		if (code.contains("getRequestDispatcher(")) {
			String[] tokens = code.split("getRequestDispatcher");
			if (tokens.length >= 1) {
				servlet = extractServletFromRequestDispature(tokens[1]);
				return servlet;
			}
		}
		return servlet;
	}

	public String extractServletFromRequestDispature(String request) {
		String servletName = "";
		String[] tokens = request.split("\"");
		if (tokens.length >= 1) {
			servletName = tokens[1];
		}
		return servletName;
	}

	public void identifyJspDirectiveServlets() {
		for (JSPDirective directive : jspDirectives) {
			if (directive.getName().equals("include")) {
				EList<Node> nodes = directive.getChildren();
				for (Node node : nodes) {
					if (node instanceof Attribute) {
						Attribute att = (Attribute) node;
						if (att.getName().equals("file")) {
							String pattern = att.getValue();
							if (pattern.startsWith("/")) {
								pattern = pattern.substring(1);
							}
							targetServletsUrlPatterns.add(pattern);
						} else if (att.getName().equals("page")) {
							String pattern = att.getValue();
							if (pattern.startsWith("/")) {
								pattern = pattern.substring(1);
							}
							targetServletsUrlPatterns.add(pattern);
						}
					}
				}
			} else if (directive.getName().equals("page")) {
				EList<Node> nodes = directive.getChildren();
				for (Node node : nodes) {
					if (node instanceof Attribute) {
						Attribute att = (Attribute) node;
						if (att.getName().equals("errorPage")) {
							String pattern = att.getValue();
							if (pattern.startsWith("/")) {
								pattern = pattern.substring(1);
							}
							targetServletsUrlPatterns.add(pattern);
						}
					}
				}
			}
		}
	}

	public void identifyHtmlFormsServlets() {
		for (Element form : htmlForms) {
			EList<Node> nodes = form.getChildren();
			for (Node node : nodes) {
				if (node instanceof Attribute) {
					Attribute att = (Attribute) node;
					if (att.getName().equals("action")) {
						String pattern = att.getValue();
						if (pattern.startsWith("/")) {
							pattern = pattern.substring(1);
						}
						targetServletsUrlPatterns.add(pattern);
					}
				}
			}
		}
	}

	public void identifyHrefServlets() {
		for (Element href : htmlHrefs) {
			EList<Node> nodes = href.getChildren();
			for (Node node : nodes) {
				if (node instanceof Attribute) {
					Attribute att = (Attribute) node;
					if (att.getName().equals("href")) {
						String pattern = att.getValue();
						if (pattern.startsWith("/")) {
							pattern = pattern.substring(1);
						}
						targetServletsUrlPatterns.add(pattern);
					}
				}
			}
		}
	}

	public void printTargetServelts() {
		System.err.println("The list of target URL patters in " + name + " are:");
		for (String str : targetServletsUrlPatterns) {
			System.err.println("\t" + str);
		}

		System.err.println("The list of referenced Beans in " + name + " are:");
		for (String str : referencedBeans) {
			System.err.println("\t" + str);
		}
	}

	public void printTargetServeltJsp() {
		System.err.println("The list of referenced servlets in " + name + " are:");
		for (String str : targetServletClasses) {
			System.err.println("\t" + str);
		}

		System.err.println("The list of referenced JSPs in " + name + " are:");
		for (String str : targetJspPages) {
			System.err.println("\t" + str);
		}

		System.err.println("The list of non existed referenced URLs in " + name + " are:");
		for (String str : notMappedURL) {
			System.err.println("\t" + str);
		}
	}

	public void printUsedTagLibs() {
		System.err.println("The list of taglibs used in " + name + " are:");
		for (JSPTagLib taglib : jspTagLibs) {
			System.err.println("\tName: " + taglib.getName() + ", ");
			EList<Node> nodes = taglib.getChildren();
			for (Node node : nodes) {
				if (node instanceof Attribute) {
					Attribute att = (Attribute) node;
					if (att.getName().equals("uri")) {
						System.err.println("\t\turi = " + att.getValue());
					} else if (att.getName().equals("prefix")) {
						System.err.println("\t\tprefix = " + att.getValue());
					}
				}
			}
		}

		System.err.println("The list of referenced JSPs in " + name + " are:");
		for (String str : targetJspPages) {
			System.err.println("\t" + str);
		}

		System.err.println("The list of non existed referenced URLs in " + name + " are:");
		for (String str : notMappedURL) {
			System.err.println("\t" + str);
		}
	}

	public AbstractCodeElement getServletClass() {
		return servletClass;
	}

	public void setServletClass(AbstractCodeElement servletClass) {
		this.servletClass = servletClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Element> getHtmlForms() {
		return htmlForms;
	}

	public void addHtmlForm(Element htmlForm) {
		this.htmlForms.add(htmlForm);
	}

	public List<Element> getHtmlHref() {
		return htmlHrefs;
	}

	public void addHtmlHref(Element htmlHref) {
		this.htmlHrefs.add(htmlHref);
	}

	public void addText(TextImpl text) {
		this.texts.add(text);
	}

	public List<JSPDirective> getJspDirective() {
		return jspDirectives;
	}

	public void addJspDirective(JSPDirective jspDirective) {
		this.jspDirectives.add(jspDirective);
	}

	public List<JSPScriptlet> getJspScriptlets() {
		return jspScriptlets;
	}

	public void addJspScriptlet(JSPScriptlet jspScriptlet) {
		this.jspScriptlets.add(jspScriptlet);
	}

	public List<JSPStdAction> getJspStdActions() {
		return jspStdActions;
	}

	public void addJspStdAction(JSPStdAction jspStdAction) {
		this.jspStdActions.add(jspStdAction);
	}

	public List<JSPComment> getJspComments() {
		return jspComments;
	}

	public void addJspComment(JSPComment jspComment) {
		this.jspComments.add(jspComment);
	}

	public List<JSPTagLib> getJspTagLibs() {
		return jspTagLibs;
	}

	public void addJspTagLib(JSPTagLib jspTagLib) {
		this.jspTagLibs.add(jspTagLib);
	}

	public List<String> getTargetJspPages() {
		return this.targetJspPages;
	}

	public List<String> getTargetServlets() {
		return this.targetServletClasses;
	}

	public String toString() {
		String str = "";
		str += "------------------------------------------------";
		str += "\nPage Name: " + name;
		str += "\n\tList of HTML forms: ";
		str += "\n\t" + htmlForms.toString();
		str += "\n\tList of Href: ";
		str += "\n\t" + htmlHrefs.toString();
		str += "\n\n\tList of Comments: ";
		str += "\n\t" + jspComments.toString();
		str += "\n\n\t List of Directives";
		str += "\n\t" + jspDirectives.toString();
		str += "\n\n\tList of Scriptlets:";
		str += "\n\t" + jspScriptlets.toString();
		str += "\n\n\tList of JSP StdActions:";
		str += "\n\t" + jspStdActions.toString();
		str += "\n\n\tList of jspTagLibs:";
		str += "\n\t" + jspTagLibs.toString();
		str += "\n\n\tList of texts (" + texts.size() + "): ";
		str += "\n\t" + texts.toString();
		str += "\n\n\tList of texts contain taglib prefix (" + textsContainTaglibPrefix.size() + "): ";
		// str += "\n\t" + textsContainTaglibPrefix.toString();
		for (TextImpl txt : textsContainTaglibPrefix) {
			str += "\n\t\t." + txt.getName() + ".";
		}
		str += "\n\n\tList of Custom Tags: ";
		for (TextImpl txt : textsContainTaglibPrefix) {
			CustomTag tag = TaglibStringParser.parseTextToCustomTag(txt);
			str += "\n\t\t" + tag.toString();
		}

		return str;
	}

	public void identifySetOfUsedCustomTags() {
		// here we go tomorrow
	}

}
