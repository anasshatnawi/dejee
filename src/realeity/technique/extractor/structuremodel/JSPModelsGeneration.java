package realeity.technique.extractor.structuremodel;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.modisco.jee.jsp.discoverer.DiscoverJspModelFromJavaElement;




/**
 * License GPL
 * Allows to generate the JSP model from the source code of a project. (Copy-paste modify of Alvine's KDMModelsGeneration)
 * @author Anas Shatnawi
 * June 2016
 */
public class JSPModelsGeneration {

	/**
	 * Constructor of the class JSPDiscoverer. 
	 * Its visibility is private in order not to allow the other classes 
	 * to create many instances of JSPDiscoverer
	 */
	private JSPModelsGeneration(){
		
	}
	
	/**
	 * unique instance of JSPDiscoverer.
	 */
	private static JSPModelsGeneration uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of JSPDiscoverer is created.
	 * @return uniqueInstance
	 */
	public static JSPModelsGeneration getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new JSPModelsGeneration();
		}
		return uniqueInstance;
	}
	
	
	
	
	/**
	 * This code is to extract a JSP model of a jee project
	 * Code inspired from MoDisco Documentation at Eclipse. cf. http://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.modisco.jee.doc%2Fmediawiki%2Fwebapp_discoverer%2Fplugin_dev.html
	 * Note that, the 3 following required bundles should be added to the MANIFEST.MF under Required Bundle section:
	 * org.eclipse.modisco.jee.jsp
	 * org.eclipse.modisco.jee.jsp.discoverer
	 * org.eclipse.modisco.infra.discovery.core
	 * @param projectName
	 * @param jspStoragePath
	 */
	public Resource discoverJSPModel(String projectName, String jspStoragePath){
		Resource jspResource = null;
		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectName);
			IJavaProject pro = JavaCore.create(project);
			DiscoverJspModelFromJavaElement jspDiscoverer = new DiscoverJspModelFromJavaElement();
			jspDiscoverer.setSerializeTarget(true);
			jspDiscoverer.discoverElement(pro,
					new NullProgressMonitor());
			jspResource = jspDiscoverer.getTargetModel();
			System.out.println("JSP Model is extracted");
		} 
		catch (Exception e) {
			System.out.println("An error occurred during the generation of the JSP model. ");
			//e.printStackTrace();
		}
		return jspResource;
	}
		
}
