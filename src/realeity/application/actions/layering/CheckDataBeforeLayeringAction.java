package realeity.application.actions.layering;

import java.lang.reflect.InvocationTargetException;



import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

import realeity.application.actions.AbstractMicroAction;
import realeity.application.data.GeneratedData;
import realeity.views.swt.RecoveryBusyMonitor;

/**
 * License GPL
 * @date 2014-06-17 
 * @author Boaye Belle Alvine
 **/
public class CheckDataBeforeLayeringAction extends AbstractMicroAction {

	public CheckDataBeforeLayeringAction(String actionName) {
		super(actionName);
		this.jobName = actionName;

	}
	
	
	@Override
	public void run() {
		RecoveryBusyMonitor recoveryMonitor = new RecoveryBusyMonitor(5, 700);
		boolean MonitorIsDone = recoveryMonitor.showMonitorForAction(this, jobName);	
		if(!MonitorIsDone)
			runNextAction = false;
	}
	


	@Override
	public void monitorExecution() {
		GeneratedData.getInstance().setDataBeforeLayering((this.getAssociatedMacroAction()).getSelectedProject());
		if(GeneratedData.getInstance().getProjectMainSegment() != null){
			runNextAction = true;
		}
		else{
			System.out.println("--- Error. No KDM models found. The layering can not be performed.");
			/*MessageDialog.openConfirm(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
					                  "Error message", 
					                  "--- Error. No KDM found. The layering can not be performed.");*/
			runNextAction = false;
		}
		
	}

}
