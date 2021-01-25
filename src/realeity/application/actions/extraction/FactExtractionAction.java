package realeity.application.actions.extraction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import realeity.application.actions.AbstractMicroAction;
import realeity.application.data.GeneratedData;
import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.technique.extractor.ExtractionFacade;
import realeity.technique.extractor.ModuleDependencyBuilder;
import realeity.technique.extractor.SegmentExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;
import realeity.technique.extractor.structuremodel.KDMModelsGeneration;
import realeity.technique.storage.kdm.KDMStorage;

/**
 * 
 * @author Alvine
 *
 */
public class FactExtractionAction extends AbstractMicroAction {
	
	/**
	 * Default constructor.
 	 * @param actionName
 	 * @param tooltipText
 	 * @param actionIconPath
     */
	public FactExtractionAction(String actionName) {
		super(actionName);
    }
	
	@Override
	public void run(){
		//retrieve the structure model that contains the facts. If the structure model exists, do nothing
		//if the structure model do not exist, retrieve the facts from the KDM and save them in a structure model

		Segment mainSegment = GeneratedData.getInstance().getProjectMainSegment();
		StructureModel structureModel = KDMStorage.getInstance().findStructureModel(mainSegment);
		AbstractModuleDependencyGraph extractedGraph = null;
		if(structureModel == null){
			AlgorithmContext.kdmFilePath = GeneratedData.getInstance().getSelectedProject().getAbsolutePath();//no longer necessary
			extractedGraph = ExtractionFacade.getInstance().buildGraphFromSegment(new ArrayList<KDMEntity>(),
					                                                              new ArrayList<AbstractExtractedFact>(),
					                                                              mainSegment);
			File selectedProject = GeneratedData.getInstance().getSelectedProject();
			KDMStorage.getInstance().serializeFacts(mainSegment, extractedGraph, selectedProject.getAbsolutePath(),
					                                KDMStorage.getInstance().getStructureModelNameMDG());
			
			System.out.println("Extraction phase : the facts have  been extracted and stored in a " +
					            "structure model." + "\n"  + "The MDG has been generated. \n");
			GeneratedData.getInstance().setFactStructureModel(structureModel);//save the structure model containing the extrated facts
		}
		else{
			extractedGraph = extractMDGFromStructureModel(structureModel);
			/*JOptionPane.showMessageDialog(null, "The facts were previously extracted and saved. \n The MDG has been generated. \n",
			        "Error", JOptionPane.INFORMATION_MESSAGE);*/
			System.out.println("Extraction phase : the facts have already been extracted and " +
					           "stored in a structure model." +
					           "\n The MDG has been generated.");
		}
		GeneratedData.getInstance().setExtractedGraph(extractedGraph);
		runNextAction = true;
	}

	@Override
	public void monitorExecution() {
		// TODO Auto-generated method stub
		
	}

	
}


