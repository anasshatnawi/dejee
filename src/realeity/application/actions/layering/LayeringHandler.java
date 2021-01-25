package realeity.application.actions.layering;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import realeity.application.actions.AbstractRecoveryAction;
import realeity.application.data.GeneratedData;
import realeity.technique.util.ProjectSelection;
import realeity.technique.util.RealeityUtils;

/**
 * Command used by the menu to launch the layering, display the layering 
 * result and the corresponding metrics.
 * License GPL
 * @date 2014-06-11 
 * @author Boaye Belle Alvine
 **/
public class LayeringHandler extends AbstractHandler {
	private AbstractRecoveryAction layeringAction;
	private static final String actionName = "Layering recovery";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("***********************************" + " Layering phase "
		                   + "***********************************");
		//reset the layering data in its constructor
		File selectedProject = ProjectSelection.getInstance().setSelectedProject(event);
		layeringAction = new LayeringAction2(actionName, actionName, selectedProject);
		layeringAction.run();
		GeneratedData.getInstance().setLastAction(RealeityUtils.MainActions.Layering);
		
		return null;		
	}
	
	
	
}
