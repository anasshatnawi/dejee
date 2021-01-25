package realeity.application.actions.extraction;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


import realeity.application.actions.AbstractRecoveryAction;

import realeity.application.data.GeneratedData;
import realeity.technique.util.ProjectSelection;
import realeity.technique.util.RealeityUtils;



/**
 * License GPL
 * @date 2014-05-31 
 * @author Boaye Belle Alvine
 **/
public class InitialMDGHandler extends AbstractHandler  {
	private AbstractRecoveryAction extractionAction;
	private static final String actionName = "Facts extraction";


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		System.out.println("***********************************" + " Extraction phase "
		                   + "***********************************");
		//reset the data in its constructor
		extractionAction = new ExtractionAction(actionName, actionName);
		File selectedProject = ProjectSelection.getInstance().setSelectedProject(event);
		GeneratedData.getInstance().setDataBeforeExtraction(selectedProject);
		extractionAction.run();
		GeneratedData.getInstance().setLastAction(RealeityUtils.MainActions.Extraction);
		return null;			
	}


}
