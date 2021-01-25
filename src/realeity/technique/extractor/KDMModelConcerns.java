package realeity.technique.extractor;

import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.util.RealeityUtils;


/**
 * Extract the KDM models comprised in a KDM representation.
 * @author Boaye Belle Alvine
 * 2014
 */
public class KDMModelConcerns {

	/**
	 * Class's constructor. 
	 * Its visibility is private in order not to allow the other classes to create 
	 * many instances of LayeringFacade.
	 */
	private KDMModelConcerns(){
		
	}
	
	/**
	 * unique instance of LayeringFacade.
	 */
	private static KDMModelConcerns uniqueInstance;
	
	/**
	 * Method insuring that a unique instance of KDMModelExtractor is created.
	 * @return uniqueInstance
	 */
	public static KDMModelConcerns getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new KDMModelConcerns();
		}
		return uniqueInstance;
	}
	
	/**
	 * 
	 * @param segment
	 * @param modelName
	 * @return
	 */
	private KDMModel extractInventoryModel(Segment segment, String modelName){
		KDMModel retrievedModel = null; 
		return retrievedModel;
	}
	
	/**
	 * 
	 * @param segment
	 * @param modelName
	 * @return
	 */
	private KDMModel extractCodeModel(Segment projectSegment, String modelName){
		EList<KDMModel> kdmModel = projectSegment.getModel();
		CodeModel projectCodeModel = (CodeModel) kdmModel.get(0);
		return projectCodeModel;
	}
	
	/**
	 * 
	 * @param segment
	 * @param modelName
	 * @return
	 */
	private KDMModel extractStructureModel(Segment projectSegment, String modelName){
		KDMModel structureModel = null; 
		//get all the models contained in the segment
		 EList<StructureModel> structureModels = new BasicEList<StructureModel>();
		if(projectSegment != null){
			EList<KDMModel> allTheModels = projectSegment.getModel();
			//get all the structure models
			for(KDMModel kdmModel : allTheModels){
				if(kdmModel instanceof StructureModel)
					structureModels.add((StructureModel)kdmModel);
			}
			
		     structureModel = null;
			 for(StructureModel structModel : structureModels){
				 if(structModel.getName().equals(modelName)){
					 structureModel = structModel;
					 break;
				 }
			 }
			
			if(structureModel == null)
				System.out.println("Did not find structure model containing the extrated facts. " +
						           "A new one should be created"); //$NON-NLS-1$
		}
		 
		return structureModel;
	}
	
	/**
	 * 
	 * @param segment
	 * @param modelType
	 * @return
	 */
	public KDMModel extractModel(Segment projectSegment, RealeityUtils.ModelNames modelType,
			                     String modelName){
		KDMModel retrievedModel = null;
		switch (modelType) {
		case StructureModel: retrievedModel = extractStructureModel(projectSegment, 
				                                                    modelName);
			
			break;
			
        case CodeModel: retrievedModel = extractCodeModel(projectSegment, 
        		                                          modelName);
			
			break;
        
        case InventoryModel: retrievedModel = extractInventoryModel(projectSegment, 
        		                                                    modelName);
			
			break;

		default:
			break;
		};
		
		return retrievedModel;
		
	}
	
	/**
	 * Create an empty structure model and add it to the main segment
	 * @param mainSegment
	 * @param modelType
	 * @param modelName
	 * @return
	 */
	public StructureModel createEmptystructureModel(Segment mainSegment, 
			                                        RealeityUtils.ModelNames modelType, 
			                                        String structureModelName){
		//TODO
		return null;
		
	}
	
	/**
	 * Save the extracted facts in a structure model
	 * @param actStructureModel
	 * @param extractedFactsextractedFacts
	 */
	public void saveDependenciesInStructureModel(StructureModel factStructureModel,
			                                     List<AbstractExtractedFact> extractedFactsextractedFacts){
		//TODO
	}
	
	/**
	 * If the structure model with the specified name do not exist, this method saves 
	 * the entities in a new structure model named after the input name and stored the entities inside.
	 * @param structureModelName
	 */
	protected void saveRelevantEntities(StructureModel factStructureModel,
			                            List<KDMEntity> relevantEntities){
		//TODO
	}
}
