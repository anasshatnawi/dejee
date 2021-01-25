package realeity.application.actions.layering;

import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import realeity.application.actions.AbstractMicroAction;
import realeity.application.data.GeneratedData;
import realeity.core.layering.algorithms.settings.AlgorithmContext;

/**
 * License GPL.
 * @author Boaye Belle Alvine
 * 30 november 2014
 * 
 * Fetch a layering/refinement using a fileChooser and display it in the layering view
 *
 */
public class LoadLayeringAction extends AbstractMicroAction {

private static LoadLayeringAction loadLayeringAction; 
	
	public static final String LOAD_LAYERING_ACTION_NAME = "Load a layering";


	private LoadLayeringAction(String actionName) {
		super(actionName);
	}
	
	public static LoadLayeringAction getInstance(String actionName){
		if(loadLayeringAction == null){
			loadLayeringAction = new LoadLayeringAction(actionName);
			//loadLayeringAction.setEnabled(false);//the action is deactivated by default
		}
		return loadLayeringAction;
	}
	
	@Override
	public void run() {
		if(this.isEnabled()){
			System.out.println("\n The user is saving the layering");
			//display the a wizard to specify the name of the refinement output
			
			//
			//refresh the metrics views
			//save the layering refinement in a structure model
		}
	}
	
	@Override
	public void monitorExecution() {
		// TODO Auto-generated method stub
		
	}
	
}
