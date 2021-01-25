package realeity.application.actions.layering;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.sun.org.apache.bcel.internal.generic.LCONST;

import realeity.application.actions.AbstractMicroAction;
import realeity.application.actions.metrics.DisplayMetricsAction;
import realeity.application.data.GeneratedData;
import realeity.core.algorithms.cost.ICostStrategy;
import realeity.core.algorithms.cost.LCCostStrategy;
import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.core.mdg.elements.LayeredPartition;
import realeity.technique.storage.kdm.KDMStorage;
import realeity.views.swt.LayerRefinementDialog;
import realeity.views.swt.RecoveryBusyMonitor;

/**
 * 
 * License GPL
 * @author Boaye Belle Alvine
 * 2014-11-23
 * 08:29:35
 */
public class SaveRefinementAction extends AbstractMicroAction {
	
	private static SaveRefinementAction saveRefinementAction; 
	
	public static final String SAVE_REFINE_LAYERING_ACTION_NAME = "Save a layering";
	
	private String txtRefinementName = "Save As: ";
	private String dialogTitle = "Save as dialog";

    private String refinementModelName;
    private StructureModel refinementModel;
    

	private SaveRefinementAction(String actionName) {
		super(actionName);
		this.jobName = actionName;
	}
	
	public static SaveRefinementAction getInstance(String actionName){
		if(saveRefinementAction == null){
			saveRefinementAction = new SaveRefinementAction(actionName);
			saveRefinementAction.setEnabled(false);//the action is deactivated by default
		}
		return saveRefinementAction;
	}
	
	@Override
	public void run() {
		if(this.isEnabled()){
			System.out.println("\n The user is saving the refined layering");
			
			//display the a wizard to specify the name of the refinement output
			displaySaveAsWizard();
			
			if(!refinementModelName.equals("")){
				//update the metrics 
				RecoveryBusyMonitor recoveryMonitor = new RecoveryBusyMonitor(9, 100);
				runNextAction = recoveryMonitor.showMonitorForAction(this, jobName);
				//refresh the metrics view
				AbstractMicroAction metricsAction = new DisplayMetricsAction("Displaying the layering metrics");
				metricsAction.run();
			}
		}
	}

	
	public void monitorExecution(){
		ICostStrategy strat = new LCCostStrategy();
		strat.computeLC(GeneratedData.getInstance().getLayeringResult(), AlgorithmContext.algorithmParameters);
	    
		////save the layering refinement in a structure model and put it in GeneratedData.getInstance().getCurrentLayeringModel();
		KDMStorage.getInstance().saveRefinementStructureModel(refinementModel, refinementModelName, 
				                                              GeneratedData.getInstance().getProjectMainSegment(),
                                                              (LayeredPartition) GeneratedData.getInstance().getLayeringResult().clone());
				                                            
	}
		
	/**
	 * 
	 */
	private void displaySaveAsWizard(){
		LayerRefinementDialog saveAsDialog = new LayerRefinementDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                                                                       dialogTitle, txtRefinementName);
		saveAsDialog.create();
		saveAsDialog.open();
		try{
			if(!saveAsDialog.getInputName().equals("")){
				refinementModelName = saveAsDialog.getInputName();
			}
			else
				refinementModelName = "";
		}
		catch(NullPointerException exception){
			//System.out.println("This exception occurs when we hit on the cancel button of the wizard");
		}
	}
	
	/**
	 * 
	 */
	
	
}
