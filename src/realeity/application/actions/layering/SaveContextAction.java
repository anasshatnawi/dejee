package realeity.application.actions.layering;
import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import realeity.application.actions.AbstractMicroAction;
import realeity.application.data.GeneratedData;
import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.technique.extractor.SegmentExtractor;

/**
 * License GPL
 * @date 2014-06-11 
 * @author Boaye Belle Alvine
 **/

public class SaveContextAction extends AbstractMicroAction {

	/**
	 * Class constructor.
	 * @param actionName
	 */
	public SaveContextAction(String actionName) {
		super(actionName);
	}

	@Override
	public void run(){
		 //1. set the parameters of the layering algorithm and save them in a new structure model
		Map<String,String> layeringContext = GeneratedData.getInstance().getLayeringContext();
	    StructureModel resultModel = AlgorithmContext.specifyContext(layeringContext, 
	    		                                                     GeneratedData.getInstance().getProjectMainSegment());  
	    GeneratedData.getInstance().setCurrentLayeringModel(resultModel);
	    runNextAction = true;
	}
	
	@Override
	public void monitorExecution() {
		// TODO Auto-generated method stub
		
	}
	
}

