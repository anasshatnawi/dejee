package anas.test;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.xml.Element;
import org.eclipse.gmt.modisco.xml.Node;
import org.eclipse.modisco.jee.jsp.Model;
import org.eclipse.modisco.jee.jsp.Page;

public class TestKDMRead {

	public final static String jspPackageName[] = { "org", "apache", "jsp",
			"WebContent" };
	public final static int INX_ORG = 0, INX_APACHE = 1, INX_JSP = 2,
			INX_WEB = 3;

	public static void identifyJspKdmServlets(Segment kdmSegment) {
		CodeModel projectCodeModel;
		EList<KDMModel> kdmModel = kdmSegment.getModel();
		projectCodeModel = (CodeModel) kdmModel.get(0);

		// get all the abstract code elements contained in these code models
		EList<AbstractCodeElement> abstractCodeElements = projectCodeModel
				.getCodeElement();
		EList<AbstractCodeElement> jspServletKdm = null;
		System.err.println("identifying jsp servlet....................");
		for (AbstractCodeElement abstractcode : abstractCodeElements) {
			if (abstractcode instanceof Package
					&& abstractcode.getName().startsWith(
							jspPackageName[INX_ORG])) {
				System.err.println("org package->");
				EList<AbstractCodeElement> orgPackages = ((Package) abstractcode)
						.getCodeElement();
				System.err.println("orgAbstractCodeElements:"
						+ orgPackages.size());
				// EList<KDMEntity> orgPackages =
				// abstractcode.getOwnedElement();
				for (AbstractCodeElement apachePckge : orgPackages) {
					System.err.println("inside org" + apachePckge.getName());
					if (apachePckge instanceof Package
							&& apachePckge.getName().startsWith(
									jspPackageName[INX_APACHE])) {
						System.err.println("\tapache package");
						EList<AbstractCodeElement> apachePackages = ((Package) apachePckge)
								.getCodeElement();
						for (AbstractCodeElement jspPckge : apachePackages) {
							if (jspPckge instanceof Package
									&& jspPckge.getName().startsWith(
											jspPackageName[INX_JSP])) {
								System.err.println("\t\tjsp package");
								EList<AbstractCodeElement> jspPackages = ((Package) jspPckge)
										.getCodeElement();
								for (AbstractCodeElement webPckge : jspPackages) {
									if (webPckge instanceof Package
											&& webPckge.getName().startsWith(
													jspPackageName[INX_WEB])) {
										System.err.println("web package");
										jspServletKdm = ((Package) webPckge)
												.getCodeElement();
										for (AbstractCodeElement jspServlet : jspServletKdm) {
											System.err
													.println("jspServlet.getName()"
															+ jspServlet
																	.getName());
											String str = jspServlet.getName();
//											jspServlet.setName(str + "changed");
											if (jspServlet instanceof ClassUnit) {
												System.err
														.println("it is  a classssss");
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

	}

	public static void printKdmSegment(Segment kdmSegment) {
		CodeModel projectCodeModel;
		EList<KDMModel> kdmModel = kdmSegment.getModel();
		projectCodeModel = (CodeModel) kdmModel.get(0);

		// get all the abstract code elements contained in these code models
		EList<AbstractCodeElement> abstractCodeElements = projectCodeModel
				.getCodeElement();

		// get all the packages contained in the abstractCodeElements list.
		List<KDMEntity> packageSet = new ArrayList<KDMEntity>();

		for (AbstractCodeElement abstractcode : abstractCodeElements) {
			System.err.println("Code element name:" + abstractcode.getName());
			if (abstractcode instanceof Package) {
				packageSet.add((Package) abstractcode);
				System.err.println("this is a package element");
			}
		}
	}

	public static void modifyKdmPackages(Segment kdmSegment) {

		CodeModel projectCodeModel;
		EList<KDMModel> kdmModel = kdmSegment.getModel();
		projectCodeModel = (CodeModel) kdmModel.get(0);

		// get all the abstract code elements contained in these code models
		EList<AbstractCodeElement> abstractCodeElements = projectCodeModel
				.getCodeElement();

		// get all the packages contained in the abstractCodeElements list.
		List<KDMEntity> packageSet = new ArrayList<KDMEntity>();

		for (AbstractCodeElement abstractcode : abstractCodeElements) {
			System.err.println("Code element name before change:"
					+ abstractcode.getName());
			if (abstractcode instanceof Package) {
				abstractcode.setName(abstractcode.getName() + "Modified");
				packageSet.add((Package) abstractcode);

				System.err
						.println("this is a package element, the name become:"
								+ abstractcode.getName());
			}
		}
	}

	public static void readJspElements(Element element, String t) {
		EList<Node> elementNodes = element.getChildren();
		if (elementNodes.size() == 0) {
			return;
		} else {
			for (Node elmNode : elementNodes) {

				System.err.println(t+"Element name: "
						+ elmNode.getName());
				if (elmNode instanceof Element) {
					Element subElement = (Element) elmNode;
					if (subElement != null) {
						t=t+"\t";
						readJspElements(subElement, t);
					}
				}
			}
		}

	}

	public static void printJSPmodel(Model jspModel) {

		EList<Page> pages = jspModel.getPages();
		// jspModel.
		for (Page page : pages) {
			System.err.println("Code element name:" + page.getName());
			if (page instanceof Page) {
				System.err.println("\tthis is a page element ...");
				EList<Node> nodes = page.getOwnedElements();
				for (Node node : nodes) {
					// System.err.println("\t\tNode name: " + node.toString());
					Element element = (Element) node;
					EList<Node> elementNodes = element.getChildren();
					for (Node elmNode : elementNodes) {
						if (elmNode.getName().startsWith("body")) {
							System.err.println("subElement name: "
									+ elmNode.getName());
							readJspElements((Element) elmNode, "\t");

						}

					}

				}

			}

		}

	}

}
