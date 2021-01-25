/**
 * 
 */
package uqam.dejee.slicer;

import java.io.File;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmPackage;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;

import realeity.application.data.GeneratedData;
import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.technique.extractor.SegmentExtractor;
import uqam.dejee.kdm.visitor.KdmExtractors;
import uqam.dejee.kdm.visitor.TagHandlerExtractor;

/**
 * @author Anas Shatnawi Email: anasshatnawi@gmail.com 2017
 *
 * 
 */
public class Main {

	private static String kdmModel = "res\\kdmmodel.xmi";
	private static Resource resourceModel = null; // to save KDM model

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Segment kdmSegment = getSegment(kdmModel);
		List<Package> packages = KdmExtractors.getInstance().extractAllPackagesFromSegment(kdmSegment);
		List<ClassUnit> classUnits = KdmExtractors.getInstance().extractAllClassUnitsFromPackages(packages);
		List<ClassUnit> tagHnadlers = TagHandlerExtractor.getInstance().identifyTagHandlers(classUnits);

		for (ClassUnit abstractcode : tagHnadlers) {
			System.err.println("Class name: " + abstractcode.getName());
			MethodUnit method = KdmExtractors.getInstance().findMethodByNameinClassUnit("doStartTag", abstractcode);
			if (abstractcode.getName().equals("PrevFormTag")){
				MethodSlicer.getInstance().sliceMethod(method);				
			}
		}

		System.err.println("Finish");
	}

	public static Segment getSegment(String projectPath) {
		// cf http://stackoverflow.com/questions/6571638/emf-resource-problem
		// cf
		// https://trac.rtsys.informatik.uni-kiel.de/trac/11ss-eclipse/wiki/Tutorials/EMF/Resources
		Segment segment = null;
		try {
			KdmPackage kdmInstance = KdmPackage.eINSTANCE;
			// Register the default resource factory
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
			// Create a resource set.
			ResourceSet resourceSet = new ResourceSetImpl();
			// Get the URI of the model file.
			URI fileURI = URI.createFileURI(new File(projectPath).getAbsolutePath());
			System.err.println("url: " + fileURI);
			// Demand load the resource for this file, here the actual loading
			// is done.
			resourceModel = resourceSet.getResource(fileURI, true);
			// get the KDM segment from the resource
			segment = (Segment) resourceModel.getContents().get(0);
		} catch (Exception e) {
			System.out.println("Error during the segment extraction : the KDM file " + "can not be found");
			// e.printStackTrace();
		}
		return segment;
	}

}
