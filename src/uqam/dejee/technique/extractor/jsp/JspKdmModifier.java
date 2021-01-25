package uqam.dejee.technique.extractor.jsp;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmt.modisco.omg.kdm.action.AbstractActionRelationship;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionRelationship;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.action.EntryFlow;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeFactory;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.CommentUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Annotation;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Attribute;
import org.eclipse.gmt.modisco.omg.kdm.kdm.ExtendedValue;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmFactory;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Stereotype;
import org.eclipse.gmt.modisco.omg.kdm.kdm.impl.KdmFactoryImpl;
import org.eclipse.gmt.modisco.omg.kdm.source.SourceRef;

import realeity.application.data.GeneratedData;
import realeity.technique.extractor.structuremodel.JSPModelsGeneration;
import uqam.dejee.element.serverpage.JspPage;

public class JspKdmModifier {

	/**
	 * Constructor of the class JspKdmModifier. Its visibility is private in
	 * order not to allow the other classes to create many instances of
	 * JSPDiscoverer
	 */
	private JspKdmModifier() {

	}

	/**
	 * unique instance of JSPDiscoverer.
	 */
	private static JspKdmModifier uniqueInstance;

	/**
	 * Method insuring that a unique instance of JspKdmModifier is created.
	 * 
	 * @return uniqueInstance
	 */
	public static JspKdmModifier getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new JspKdmModifier();
		}
		return uniqueInstance;
	}

	// The elements of this array are sequentially ordered using the followed
	// indexes to identify the (sub) packages where translated JSP by JASPER
	public final String jspPackageName[] = { "org", "apache", "jsp", "WebContent" };
	public final int INX_ORG = 0, INX_APACHE = 1, INX_JSP = 2, INX_WEB = 3;

	// To store all packages
	public List<AbstractCodeElement> allPackages = new ArrayList<>();

	// To stroe all classes
	public List<ClassUnit> allClasses = new ArrayList<>();

	public void updateJspKdmJsp(Segment kdmSegment, Vector<JspPage> jspPages) {

		Package jspPackage = getKdmJspServletPackage(kdmSegment);
		if (jspPackage == null) {
			System.err.println("JSP package can not ne found!");
		}
		extractCandidateServletKDM(kdmSegment);

		for (JspPage jspPage : jspPages) {
			String pageName = jspPage.getName();
			System.err.println("Page name:" + pageName);
			ClassUnit caller = getCallerKdmServlet(pageName, jspPackage);
			if (caller == null) {
				System.err.println("caller KDM element not found, where page name is:" + pageName);
			}
			// System.err.println("\tcaller name:" + caller.getName());
			List<ClassUnit> targetKdmServlet = identifyTargetKdmClassUnits(jspPage, allClasses);
			// System.err.println("targetKdmServlet size: " +
			// targetKdmServlet.size());
			for (ClassUnit cu : targetKdmServlet) {
//				 System.err.println("\ttraget name: " + cu.getName());
				addMethodCallBetweenClasses(caller, cu);
				addMethodCallBetweenClassesUsingNewMethods(caller, cu);

			}
		}
	}

	/**
	 * This method returns a set of ClassUnits that represent KDM elements need
	 * to be called from a JSP page (jspPage). It requires a set of ClassUnits
	 * representing the search space (classes)
	 * 
	 * @return classUnits
	 */
	public List<ClassUnit> identifyTargetKdmClassUnits(JspPage jspPage, List<ClassUnit> classes) {
		List<ClassUnit> classUnits = new ArrayList();
		for (ClassUnit cu : classes) {
			// add servlet JSP KDM ClassUnits
			if (isServletUsedInJsp(jspPage, cu)) {
//				System.err.println("cu is added: "+ cu.getName());
				classUnits.add(cu);
			}
			// add JSP KDM ClassUnits
			if (jspPage.getTargetJspPages().contains(cu.getName())) {
				classUnits.add(cu);
				// classUnits.add(cu);
				// classUnits.add(cu);
				// classUnits.add(cu);
				// classUnits.add(cu);
			}
			if (isBeansUsedInJsp(jspPage, cu)) {
				classUnits.add(cu);
				// classUnits.add(cu);
				// classUnits.add(cu);
			}
		}
		return classUnits;
	}

	/**
	 * This method checks if a servlet (ClassUnit) is used as a reference in a
	 * JSP page. Since the servlet names registered at the JSP page contains the
	 * full name including the package, while the other one is outhout package
	 * name
	 * 
	 * @return true or false
	 */
	public boolean isBeansUsedInJsp(JspPage jspPage, ClassUnit classUnit) {
		for (String str : jspPage.getReferencedBeans()) {
			if (str.endsWith(classUnit.getName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * This method checks if a servlet (ClassUnit) is used as a reference in a
	 * JSP page. Since the servlet names registered at the JSP page contains the
	 * full name including the package, while the other one is outhout package
	 * name
	 * 
	 * @return true or false
	 */
	public boolean isServletUsedInJsp(JspPage jspPage, ClassUnit classUnit) {
		for (String str : jspPage.getTargetServlets()) {
			if (str.endsWith(classUnit.getName())) {
				return true;
			}
		}

		return false;
	}

	public void getAllPackages(AbstractCodeElement packag) {
		// collect all packages in one place, then extract the classes by
		// passing these packages.
		EList<AbstractCodeElement> packages = ((Package) packag).getCodeElement();
		for (AbstractCodeElement pckge : packages) {
			if (pckge instanceof Package) {
				allPackages.add(pckge);
				// to go through the nested packages
				getAllPackages(pckge);
			}
		}
	}

	public List<ClassUnit> extractCandidateServletKDM(Segment kdmSegment) {
		List<ClassUnit> results = new ArrayList();

		CodeModel projectCodeModel;
		EList<KDMModel> kdmModel = kdmSegment.getModel();
		projectCodeModel = (CodeModel) kdmModel.get(0);
		// get all the abstract code elements contained in these code models
		EList<AbstractCodeElement> abstractCodeElements = projectCodeModel.getCodeElement();
		for (AbstractCodeElement abstractcode : abstractCodeElements) {
			if (abstractcode instanceof Package) {
				getAllPackages(abstractcode);
			}
		}

//		System.err.println("Number of Packages:" + allPackages.size());

		for (AbstractCodeElement packag : allPackages) {
			EList<AbstractCodeElement> codeElements = ((Package) packag).getCodeElement();
			for (AbstractCodeElement clazz : codeElements) {
				if (clazz instanceof ClassUnit) {
					allClasses.add((ClassUnit) clazz);
				}
			}
		}
		System.err.println("Number of classes:" + allClasses.size());
		for (AbstractCodeElement clazz : allClasses) {
			System.err.println("\t" + clazz.getName());
		}

		return results;
	}

	/**
	 * Adding a CodeRelation instance between two CLassUnit instances
	 */
	public void addMethodCallBetweenClasses(ClassUnit caller, ClassUnit targetClass) {
		if  (caller == null){
			System.err.println("caller class is null!");
			return;
		}
		
		EList<CodeItem> jspServletKdm = caller.getCodeElement();
		for (CodeItem cItem : jspServletKdm) {
			if (cItem instanceof MethodUnit) {
				// select _jspService() method
				if (cItem.getName().equals("getDependants")) {
//					 System.err.println("you are at getDependants()");
					EList<AbstractCodeElement> mElements = ((MethodUnit) cItem).getCodeElement();
					for (AbstractCodeElement element : mElements) {
						if (element instanceof BlockUnit) {
//							 System.err.println("creating action realtion ");
							CodeRelationship call = CodeFactory.eINSTANCE.createCodeRelationship();
							call.setTo(targetClass);
							call.setFrom(caller);
							CodeElement actionElement = CodeFactory.eINSTANCE.createCodeElement();
							actionElement.setName("newCall");
							actionElement.getCodeRelation().add((AbstractCodeRelationship) call);
							((BlockUnit) element).getCodeElement().add(actionElement);
							element.getCodeRelation().add(call);
						}
					}

				}
			}
		}
	}

	/**
	 * Adding a new Method to make the JSP calls between two Classes
	 */
	public void addMethodCallBetweenClassesUsingNewMethods(ClassUnit callerClass, ClassUnit targetClass) {

		if  (callerClass == null){
			System.err.println("caller class is null!");
			return;
		}
		MethodUnit method = null;

		// to search for the method called jspTags
		EList<CodeItem> methods = callerClass.getCodeElement();
		if  (methods == null){
			System.err.println("caller methed is null!");
			return;
		}
		for (CodeItem cItem : methods) {
			if (cItem instanceof MethodUnit && cItem.getName().equals("jspTags")) {
				method = (MethodUnit) cItem;
			}
		}

		// create a method called jspTags, if not existed
		if (method == null) {
			method = CodeFactory.eINSTANCE.createMethodUnit();
			method.setName("jspTags");

		}

		CodeElement element = CodeFactory.eINSTANCE.createCodeElement();
		element.setName("jspCall");
		CodeRelationship call = CodeFactory.eINSTANCE.createCodeRelationship();
		call.setTo(targetClass);
		call.setFrom(callerClass);
		element.getCodeRelation().add(call);
		method.getCodeElement().add(element);
		callerClass.getCodeElement().add(method);
	}

	public List<ClassUnit> identifyTargetKdmClasses(List<String> servletNames, Package pack) {
		List<ClassUnit> results = new ArrayList();

		EList<AbstractCodeElement> jspServletKdm = null;
		jspServletKdm = ((Package) pack).getCodeElement();
		for (AbstractCodeElement jspServlet : jspServletKdm) {
			if (jspServlet instanceof ClassUnit) {
				if (servletNames.contains(jspServlet.getName())) {
					results.add((ClassUnit) jspServlet);
				}
			}
		}
		return results;
	}

	public ClassUnit getCallerKdmServlet(String jspPage, Package pack) {

		// as jsp pages could be organized in terms of packages in the
		// WebContent directory, the generated name_jsp.java will be organized
		// in sub-packages. This to store all sub-packages
		List<AbstractCodeElement> allJspSubPackages = new ArrayList<>();
		// get all sub packages
		getAllPackages(pack, allJspSubPackages);
		allJspSubPackages.add(pack);
		// store the list of kdm elements in packages
		EList<AbstractCodeElement> jspServletKdm = null;
//		System.err.println("allJspSubPackages size " + allJspSubPackages.size());
		for(AbstractCodeElement jspPackage : allJspSubPackages){
			if (jspPackage instanceof Package){
//				System.err.println("jspPackage is instance of packages of size " + ((Package) jspPackage).getCodeElement().size());
				jspServletKdm = ((Package) jspPackage).getCodeElement();	
			}			
		}
		
		if (jspServletKdm == null){
//			System.err.println("jspServletKdm is null!");
			return null;
		}
		for (AbstractCodeElement jspServlet : jspServletKdm) {
//			 System.err.println("jspServlet.getName()" +
//			 jspServlet.getName());
			String str = jspServlet.getName();
			if (jspServlet instanceof ClassUnit) {
//				 System.err.println("it is a class");
				if (jspPage == jspServlet.getName() || jspPage.equals(jspServlet.getName())) {
//					System.err.println("kdm is found");
					return (ClassUnit) jspServlet;
				}
			}
		}
		return null;
	}

	public void getAllPackages(AbstractCodeElement packag, List<AbstractCodeElement> results) {
		// collect all packages in one place, then it is used to extract the
		// classes by passing these packages.
		
		EList<AbstractCodeElement> packages = ((Package) packag).getCodeElement();
		for (AbstractCodeElement pckge : packages) {
			if (pckge instanceof Package) {
				results.add(pckge);
				// to go through the nested packages
				getAllPackages(pckge, results);
			}
		}
	}

	// this method is not used anymore
	public void identifyJspKdmServlets(Segment kdmSegment) {
		CodeModel projectCodeModel;
		EList<KDMModel> kdmModel = kdmSegment.getModel();
		projectCodeModel = (CodeModel) kdmModel.get(0);
		// get all the abstract code elements contained in these code models
		EList<AbstractCodeElement> abstractCodeElements = projectCodeModel.getCodeElement();
		EList<AbstractCodeElement> jspServletKdm = null;
		for (AbstractCodeElement abstractcode : abstractCodeElements) {
			if (abstractcode instanceof Package && abstractcode.getName().startsWith(jspPackageName[INX_ORG])) {
				EList<AbstractCodeElement> orgPackages = ((Package) abstractcode).getCodeElement();
				// EList<KDMEntity> orgPackages =
				// abstractcode.getOwnedElement();
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
//											System.err.println("jspServlet.getName()" + jspServlet.getName());
											String str = jspServlet.getName();
											if (jspServlet instanceof ClassUnit) {
//												System.err.println("it is  a classssss");
												// jspServlet.
												// createNewMethod((ClassUnit)
												// jspServlet);
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

	/**
	 * to identify the package that contians the servlet corresponding to the
	 * JSP pages
	 * 
	 * @param Segment:
	 *            kdmSegment
	 * @return Package: webPckge, null if not exist
	 */
	public Package getKdmJspServletPackage(Segment kdmSegment) {
		// Package
		CodeModel projectCodeModel;
		EList<KDMModel> kdmModel = kdmSegment.getModel();
		projectCodeModel = (CodeModel) kdmModel.get(0);
		// get all the abstract code elements contained in these code models
		EList<AbstractCodeElement> abstractCodeElements = projectCodeModel.getCodeElement();
		EList<AbstractCodeElement> jspServletKdm = null;
		System.err.println("identifying jsp servlet....................");
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
										return (Package) webPckge;
									}
								}
							}
						}
					}

				}
			}
		}
		return null;
	}
}
