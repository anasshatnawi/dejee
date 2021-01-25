package realeity.application.actions.layering;

import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import realeity.application.actions.AbstractMicroAction;
import realeity.application.data.GeneratedData;
import realeity.technique.extractor.structuremodel.KDMModelsGeneration;
import realeity.technique.storage.kdm.KDMStorage;


/**
 * License GPL
 * @date 2014-06-11 
 * @author  Boaye Belle Alvine
 *
 */
public class StoreLayeringAction extends AbstractMicroAction {

	/**
	 * Class constructor.
	 * @param actionName
	 */
	public StoreLayeringAction(String actionName) {
		super(actionName);
	}

	@Override
	public void run(){
		//3. store the layering result into a structure model
	  	//storeLayeringInAStructureModel(layeringResult, resultModel);
		StructureModel resultModel = GeneratedData.getInstance().getCurrentLayeringModel();
		KDMStorage.getInstance().populateResultStructureModel(GeneratedData.getInstance().getProjectMainSegment(), 
                                                              GeneratedData.getInstance().getLayeringResult(),
                                                              resultModel);
	    GeneratedData.getInstance().setCurrentLayeringModel(resultModel);
		runNextAction = true;
	}
	
	@Override
	public void monitorExecution() {
		// TODO Auto-generated method stub
		
	}
}

