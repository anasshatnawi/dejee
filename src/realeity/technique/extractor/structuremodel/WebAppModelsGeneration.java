package realeity.technique.extractor.structuremodel;


import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.modisco.jee.jsp.discoverer.DiscoverJspModelFromJavaElement;
import org.eclipse.modisco.jee.webapp.discoverer.WebXmlDiscoverer2;

import realeity.application.data.GeneratedData;
import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.technique.extractor.SegmentExtractor;




/**
 * License GPL
 * Allows to generate the WebApp model from the source code of a project. (Copy-paste modify of Alvine's KDMModelsGeneration)
 * @author Anas Shatnawi
 * June 2016
 */
public class WebAppModelsGeneration {

	/**
	 * Constructor of the class WebAppDiscoverer. 
	 * Its visibility is private in order not to allow the other classes 
	 * to create many instances of WebAppDiscoverer
	 */
	private WebAppModelsGeneration(){
		
	}
	
	/**
	 * unique instance of WebAppDiscoverer.
	 */
	private static WebAppModelsGeneration uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of WebAppDiscoverer is created.
	 * @return uniqueInstance
	 */
	public static WebAppModelsGeneration getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new WebAppModelsGeneration();
		}
		return uniqueInstance;
	}
	
	
	public File findWebXmlFile(File selectedProject){
		File xmlFile = null;
		if (xmlFile == null) {
			File[] filesInsideProject = selectedProject.listFiles();
			for (int indexFile = 0; indexFile < filesInsideProject.length; indexFile++) {
				if (filesInsideProject[indexFile].isFile()) {
					String fileName = filesInsideProject[indexFile].getName();
					if (fileName.endsWith("web.xml")) {
						xmlFile = filesInsideProject[indexFile];
						if (xmlFile != null)
						{
							break;
						}
					}
				}
			}
		}
		return xmlFile;
	}
	
	
	/**
	 * This code is to extract a WebApp model of a jee project
	 * Code inspired from MoDisco Documentation at Eclipse. cf. http://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.modisco.jee.doc%2Fmediawiki%2Fwebapp_discoverer%2Fplugin_dev.html
	 * Note that, the 3 following required bundles should be added to the MANIFEST.MF under Required Bundle section:
	 * org.eclipse.modisco.jee.webapp
	 * org.eclipse.modisco.jee.webapp.discoverer
	 * org.eclipse.modisco.infra.discovery.core
	 * @param projectName
	 * @param webAppStoragePath
	 */
	public Resource discoverWebXmlModel(String projectName, String webAppStoragePath){
		
		File webXmlFile = findWebXmlFile(GeneratedData.getInstance().getSelectedProject());
				
		Resource webAppResource = null;
		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectName);
			IJavaProject pro = JavaCore.create(project);
			
			
//			WebXmlDiscoverer2 discoverer = new WebXmlDiscoverer2();
//			discoverer.discoverElement(webXmlFile, new NullProgressMonitor());
//			Resource webXmlResource = discoverer.getTargetModel();
			
			DiscoverJspModelFromJavaElement jspDiscoverer = new DiscoverJspModelFromJavaElement();
			jspDiscoverer.setSerializeTarget(true);
			jspDiscoverer.discoverElement(pro,
					new NullProgressMonitor());
			webAppResource = jspDiscoverer.getTargetModel();
			System.out.println("webApp Model is extracted");
		} 
		catch (Exception e) {
			System.out.println("An error occurred during the generation of the webApp model. ");
			//e.printStackTrace();
		}
		return webAppResource;
	}
		
}
