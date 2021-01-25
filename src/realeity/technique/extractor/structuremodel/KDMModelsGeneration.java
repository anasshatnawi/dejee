package realeity.technique.extractor.structuremodel;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.modisco.java.discoverer.DiscoverKDMModelFromProject;




/**
 * License GPL
 * Allows to generate the KDM model from the source code of a project.
 * @author Alvine Boaye Belle
 * december 2013  
 */
public class KDMModelsGeneration {

	/**
	 * Constructor of the class KDMDiscoverer. 
	 * Its visibility is private in order not to allow the other classes 
	 * to create many instances of KDMDiscoverer
	 */
	private KDMModelsGeneration(){
		
	}
	
	/**
	 * unique instance of KDMDiscoverer.
	 */
	private static KDMModelsGeneration uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of KDMDiscoverer is created.
	 * @return uniqueInstance
	 */
	public static KDMModelsGeneration getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new KDMModelsGeneration();
		}
		return uniqueInstance;
	}
	
	
	
	
	/**
	 * Code inspired from Thanasis Naskos's one , cf : http://www.eclipse.org/forums/index.php/t/213661/. 
	 * The code has been modified to get a KDM model instead of a java model from the sources of a project.
	 * The following link contains the method testJavaModelDiscoveryFromJavaProject that also explains how 
	 * to get a java model from the sources of a project :
	 * https://dev.eclipse.org/svnroot/modeling/org.eclipse.mdt.modisco/main/trunk/org.eclipse.modisco.java.discoverer.tests/src/org/eclipse/modisco/java/discoverer/tests/SimpleBlackBoxDiscovery.java
	 * Nb: the 3 following required dependencies should be added to the MANIFEST.MF, 
	 * otherwise, the code will always indicate errors:
	 * org.eclipse.gmt.modisco.java
	 * org.eclipse.modisco.java.discoverer
	 * org.eclipse.modisco.infra.discovery.core
	 * @param projectName
	 * @param kdmStoragePath
	 */
	public Resource discoverKDMModel(String projectName, String kdmStoragePath){
		Resource kdmResource = null;
		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			DiscoverKDMModelFromProject kdmDiscoverer = new DiscoverKDMModelFromProject();
			kdmDiscoverer.setSerializeTarget(true);
			kdmDiscoverer.discoverElement(project, new NullProgressMonitor());
			kdmResource = kdmDiscoverer.getTargetModel();
		} 
		catch (Exception e) {
			System.out.println("An error occurred during the generation of the KDM model. ");
			//e.printStackTrace();
		}
		return kdmResource;
	}
		
}	
	