package realeity.technique.extractor;

import java.io.File;

import javax.annotation.Generated;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmPackage;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;

import realeity.application.actions.extraction.GenerateKDMAction;
import realeity.application.data.GeneratedData;
import realeity.core.layering.algorithms.settings.AlgorithmContext;

import java.io.File;

import javax.annotation.Generated;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmPackage;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.modisco.jee.jsp.Model;

import realeity.application.data.GeneratedData;

/**
 * License GPL
 * @date 2014-06-11 
 * @author Boaye Belle Alvine
 **/
public class SegmentExtractor extends AbstractKDMEntityExtractor {
	private Resource resourceModel = null; // to save KDM model
	private Resource JSPResourceModel = null; // added by Anas to save the JSP resource model
	private Resource webXmlModel = null;
	private static SegmentExtractor uniqueInstance;
	private static String kdmExtension = "kdm.xmi";
	private static String jspExtension = "jsp.xmi";
	private static String webExtension = "web.xml";
	/**
	* Class's constructor. 
	* Its visibility is private in order not to allow the other classes to create 
	* many instances of SegmentExtractor.
	*/
	private SegmentExtractor(){

	}

	/**
	* unique instance of SegmentExtractor.
	*/

	/**
	* Method insuring that a unique instance of SegmentExtractor is created.
	* @return uniqueInstance
	*/
	public static SegmentExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new SegmentExtractor();
		}
		return uniqueInstance;
	}

	public Segment getSegment(String projectPath){
		// cf http://stackoverflow.com/questions/6571638/emf-resource-problem 
		//cf https://trac.rtsys.informatik.uni-kiel.de/trac/11ss-eclipse/wiki/Tutorials/EMF/Resources
		Segment segment = null;
		try{
			KdmPackage kdmInstance = KdmPackage.eINSTANCE;
			
			// Register the default resource factory 
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", 
					                                                          new XMIResourceFactoryImpl());
					
			// Create a resource set.
			ResourceSet resourceSet = new ResourceSetImpl();
					
			// Get the URI of the model file.
			URI fileURI = URI.createFileURI(new File(projectPath).getAbsolutePath());

			// Demand load the resource for this file, here the actual loading is done.
			resourceModel = resourceSet.getResource(fileURI, true);

			// get the KDM segment  from the resource
			segment = (Segment) resourceModel.getContents().get(0); 
		}
		catch(Exception e){
			System.out.println("Error during the segment extraction : the KDM file " +
					           "can not be found");
			//e.printStackTrace();
		}
		
		return segment;		
	}
	
	/**
	 * Checks whether the KDM segment has already been generated for the selected project.
	 * @param selectedProject
	 * @return
	 */
	public Segment findKDMSegment(File selectedProject){
		/** String kdmFileName = selectedProject.getAbsolutePath() + "\\"
                             + selectedProject.getName()+
                             GenerateKDMAction.KDM_STRING + kdmExtension;*/
		Segment mainSegment = null;
		if(mainSegment == null){
			File[] filesInsideProject = selectedProject.listFiles();
			for(int indexFile = 0; indexFile < filesInsideProject.length ; indexFile++){
				if(filesInsideProject[indexFile].isFile()){
					String fileName = filesInsideProject[indexFile].getName();
					if(fileName.endsWith(kdmExtension)){
						String absolutePath = filesInsideProject[indexFile].getAbsolutePath();
						AlgorithmContext.kdmFilePath = absolutePath;
						mainSegment = SegmentExtractor.getInstance().getSegment(absolutePath);
						if(mainSegment != null)
							break;
					}
				}
			}
		}
		
		return mainSegment;
	}
	
	/**
	 * Checks whether the JSP model has already been generated for the selected project.
	 * @param selectedProject
	 * @return
	 */
	public Model findJspModel(File selectedProject) {
		Model jspModel = null;
		if (jspModel == null) {
			File[] filesInsideProject = selectedProject.listFiles();
			for (int indexFile = 0; indexFile < filesInsideProject.length; indexFile++) {
				if (filesInsideProject[indexFile].isFile()) {
					String fileName = filesInsideProject[indexFile].getName();
					if (fileName.endsWith(jspExtension)) {
						String absolutePath = filesInsideProject[indexFile]
								.getAbsolutePath();
						AlgorithmContext.jspModelFilePath = absolutePath;
						jspModel = SegmentExtractor.getInstance()
								.getJspModel(absolutePath);
						if (jspModel != null)
						{
							break;
						}
					}
				}
			}
		}
		return jspModel;
	}
	
	public Model getJspModel(String projectPath){
		Model model = null;
		try{			
			// Register the default resource factory 
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", 
					                                                          new XMIResourceFactoryImpl());	
			// Create a resource set.
			ResourceSet resourceSet = new ResourceSetImpl();
					
			// Get the URI of the model file.
			URI fileURI = URI.createFileURI(new File(projectPath).getAbsolutePath());

			// Demand load the resource for this file, here the actual loading is done.
			resourceModel = resourceSet.getResource(fileURI, true);

			// get the KDM segment  from the resource
			model = (Model) resourceModel.getContents().get(0); 
		}
		catch(Exception e){
			System.err.println("Error during the JspModel extraction : the xmi file " +
					           "can not be found");
		}
		
		return model;		
	}
	
	public void setResourceModel(Resource resourceModel) {
		this.resourceModel = resourceModel;
	}
	
	public Resource getResourceModel() {
		return resourceModel;
	}
	
	public Resource getJSPResourceModel() {
		return JSPResourceModel;
	}

	public void setJSPResourceModel(Resource jSPResourceModel) {
		JSPResourceModel = jSPResourceModel;
	}
	
	/**
	 * Checks whether the JSP model has already been generated for the selected project.
	 * @param selectedProject
	 * @return
	 */
	public Model findwebXmlFile(File selectedProject) {
		
		Model jspModel = null;
		if (jspModel == null) {
			File[] filesInsideProject = selectedProject.listFiles();
			for (int indexFile = 0; indexFile < filesInsideProject.length; indexFile++) {
				if (filesInsideProject[indexFile].isFile()) {
					String fileName = filesInsideProject[indexFile].getName();
					if (fileName.endsWith(jspExtension)) {
						String absolutePath = filesInsideProject[indexFile]
								.getAbsolutePath();
						AlgorithmContext.jspModelFilePath = absolutePath;
						jspModel = SegmentExtractor.getInstance()
								.getJspModel(absolutePath);
						if (jspModel != null)
						{
							break;
						}
					}
				}
			}
		}
		return jspModel;
	}
	
}

