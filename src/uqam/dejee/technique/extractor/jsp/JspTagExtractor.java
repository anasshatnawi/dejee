package uqam.dejee.technique.extractor.jsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.xml.Element;
import org.eclipse.gmt.modisco.xml.Node;
import org.eclipse.gmt.modisco.xml.emf.impl.TextImpl;
import org.eclipse.modisco.jee.jsp.JSPComment;
import org.eclipse.modisco.jee.jsp.JSPDirective;
import org.eclipse.modisco.jee.jsp.JSPScriptlet;
import org.eclipse.modisco.jee.jsp.JSPStdAction;
import org.eclipse.modisco.jee.jsp.JSPTagLib;
import org.eclipse.modisco.jee.jsp.Model;
import org.eclipse.modisco.jee.jsp.Page;
import org.w3c.dom.Text;

import realeity.technique.util.ProjectSelection;
import sun.net.www.content.image.jpeg;
import uqam.dejee.element.serverpage.JspPage;

public class JspTagExtractor {

	private static JspTagExtractor uniqueInstance;

	public final static String jspPackageName[] = { "org", "apache", "jsp", "WebContent" };
	public final static int INX_ORG = 0, INX_APACHE = 1, INX_JSP = 2, INX_WEB = 3;

	public JspTagExtractor() {

	}

	public static JspTagExtractor getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new JspTagExtractor();
		}
		return uniqueInstance;
	}

	public Vector<JspPage> identufyInterestedTags(Model jspModel) {
		Vector<JspPage> jspPages = new Vector<JspPage>();
		EList<Page> pages = jspModel.getPages();
		for (Page page : pages) {
			if (page instanceof Page) {
				JspPage jspPage = new JspPage();
				jspPage.setName(page.getName().replace(".jsp", "_jsp"));
				EList<Node> nodes = page.getOwnedElements();
				for (Node node : nodes) {
					if (!(node instanceof Element)) {
						continue;
					} else {
					}
					Element element = (Element) node;
					EList<Node> elementNodes = element.getChildren();
					exploreJspElements((Element) element, jspPage);

					for (Node elmNode : elementNodes) {

						if (elmNode instanceof Element) {
						}
					}
				}
				// add the page
				jspPages.add(jspPage);
			}
		}
		return jspPages;
	}

	public void exploreJspElements(Element element, JspPage page) {
		EList<Node> elementNodes = element.getChildren();
		if (elementNodes.size() == 0) {
			return;
		} else {
			for (Node elmNode : elementNodes) {
				if (elmNode instanceof JSPDirective) {
					page.addJspDirective((JSPDirective) elmNode);
				} else if (elmNode instanceof JSPScriptlet) {
					page.addJspScriptlet((JSPScriptlet) elmNode);
				} else if (elmNode instanceof JSPStdAction) {
					page.addJspStdAction((JSPStdAction) elmNode);
				} else if (elmNode instanceof JSPComment) {
					page.addJspComment((JSPComment) elmNode);
				} else if (elmNode instanceof JSPTagLib) {
					page.addJspTagLib((JSPTagLib) elmNode);
				} else if (elmNode.getName().equals("uri")) {
					page.addJspTagLib((JSPTagLib) elmNode.getParent());
				} else if (elmNode.getName().startsWith("form")) {
					page.addHtmlForm((Element) elmNode);
				} else if (elmNode.getName().equals("href")) {
					page.addHtmlHref((Element) elmNode.getParent());
				} else if (elmNode instanceof TextImpl) {
					page.addText((TextImpl) elmNode);
				}
				if (elmNode instanceof Element) {
					Element subElement = (Element) elmNode;
					if (subElement != null) {
						exploreJspElements(subElement, page);
					}
				}
			}
		}
	}

	public List<AbstractCodeElement> identifyJspKdmServlets(Segment kdmSegment) {

		List<AbstractCodeElement> jspServlets = new ArrayList<AbstractCodeElement>();

		CodeModel projectCodeModel;
		EList<KDMModel> kdmModel = kdmSegment.getModel();
		projectCodeModel = (CodeModel) kdmModel.get(0);

		// get all the abstract code elements contained in these code models
		EList<AbstractCodeElement> abstractCodeElements = projectCodeModel.getCodeElement();
		EList<AbstractCodeElement> jspServletKdm = null;
		for (AbstractCodeElement abstractcode : abstractCodeElements) {
			if (abstractcode instanceof Package && abstractcode.getName().startsWith(jspPackageName[INX_ORG])) {
				EList<AbstractCodeElement> orgPackages = ((Package) abstractcode).getCodeElement();
				for (AbstractCodeElement apachePckge : orgPackages) {
					if (apachePckge instanceof Package
							&& apachePckge.getName().startsWith(jspPackageName[INX_APACHE])) {
						EList<AbstractCodeElement> apachePackages = ((Package) apachePckge).getCodeElement();
						for (AbstractCodeElement jspPckge : apachePackages) {
							if (jspPckge instanceof Package && jspPckge.getName().startsWith(jspPackageName[INX_JSP])) {
								EList<AbstractCodeElement> jspPackages = ((Package) jspPckge).getCodeElement();
								for (AbstractCodeElement webPckge : jspPackages) {
									if (webPckge instanceof Package
											&& webPckge.getName().startsWith(jspPackageName[INX_WEB])) {
										jspServletKdm = ((Package) webPckge).getCodeElement();
										for (AbstractCodeElement jspServlet : jspServletKdm) {
											String str = jspServlet.getName();
											jspServlet.setName(str + "changed");
											jspServlets.add(jspServlet);
											if (jspServlet instanceof ClassUnit) {
											}
										}
									}
								}
							}
						}
					}

				}
			}
		}
		return jspServlets;
	}

}
