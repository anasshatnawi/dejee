package realeity.application.actions.layering;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import realeity.application.actions.AbstractMacroAction;
import realeity.application.actions.AbstractMicroAction;
import realeity.application.actions.metrics.DisplayMetricsAction;
import realeity.application.data.ActionMemento;
import realeity.application.data.GeneratedData;
import realeity.core.mdg.Graph;
import realeity.views.swt.InitialMDGView;

/**
 * For details about visible when, see http://stackoverflow.com/questions/7049670/how-to-limit-visibility-of-a-popup-menu-only-to-a-certain-project-type
 * License GPL.
 * @author alvine
 * 2014-12-03
 */
public class LayeringAction2 extends AbstractMacroAction {
	
	private static final String kdmErrorMEssage = "ERROR. No KDM found.";
	private File selectedProject;
	

	/**
	 * Class's constructor.
	 * @param actionName
	 * @param progressBarName
	 */
	public LayeringAction2(String actionName, String progressBarName, File selectedProject) {
		super(actionName, progressBarName);
		this.selectedProject = selectedProject;
	}
	
	@Override
	public void run() {
		//reinitialize the sata related to the layering
		resetData();
		
		//deactivate the layering refinement
		SaveRefinementAction.getInstance(SaveRefinementAction.SAVE_REFINE_LAYERING_ACTION_NAME).setEnabled(false);
		
		//check whether the facts have already been extracted and stored in a structure model		
		if(checkFactsExtraction()){
			AbstractMicroAction wizardAction = new DisplayLayeringWizardAction("Setting the algorithm parameters");
		    wizardAction.setAssociatedMacroAction(this);
			wizardAction.run();
			if(wizardAction.getRunNextAction()){
				super.run();
				
				if(super.runNextMacroAction){
					//activate the layering refinement
					SaveRefinementAction.getInstance(SaveRefinementAction.SAVE_REFINE_LAYERING_ACTION_NAME).setEnabled(true);
				}
			}
		}
	}

	/**
	 * Check whether the facts have already been extracted from the analyzed system.
	 * @return
	 */
	private boolean checkFactsExtraction(){
		 AbstractMicroAction checkExtraction = new CheckDataBeforeLayeringAction("Checking the extraction execution");	
		 checkExtraction.setAssociatedMacroAction(this);
		 checkExtraction.run();
		 return checkExtraction.getRunNextAction();
	}
	
	
	@Override
	public List<AbstractMicroAction> fillActionsToExecuteBeforeMonitor() {
		List<AbstractMicroAction> actionsToExecute = new ArrayList<AbstractMicroAction>();
		actionsToExecute.add(new DisplayMetricsAction("Displaying the layering metrics"));
		actionsToExecute.add(new ZestLayeringVisualizationAction("Visualization of the layering results"));
		return actionsToExecute;
	}

	@Override
	public List<AbstractMicroAction> fillActionsToMonitor() {
		List<AbstractMicroAction> actionsToMonitor = new ArrayList<AbstractMicroAction>();
		actionsToMonitor.add(new IsANewContextAction("Checking the context"));
		actionsToMonitor.add(new SaveContextAction("Storage of the algorithm parameters"));
		actionsToMonitor.add(new RunAlgorithmAction("Execution of the layering algorithm"));
		actionsToMonitor.add(new StoreLayeringAction("Storage of the layering results"));
		return actionsToMonitor;
	}

	@Override
	public List<AbstractMicroAction> fillActionsToExecuteAfterMonitor() {
		List<AbstractMicroAction> actionsToExecute = new ArrayList<AbstractMicroAction>();
		actionsToExecute.add(new DisplayMetricsAction("Displaying the layering metrics"));
		actionsToExecute.add(new ZestLayeringVisualizationAction("Visualization of the layering results"));
		return actionsToExecute;
	}
	
	@Override
	protected boolean executeActionsAfterMonitoring() {
		resetViews();
		return super.executeActionsAfterMonitoring();
	}

	@Override
	public void resetData() {
		GeneratedData.getInstance().resetForLayering();	
	}

	@Override
	public void resetViews() {
		//reset the ReALEITY-Layering and the ReALEITY-Metrics views
		super.resetViews();
		if(GeneratedData.getInstance().getprojectChanged()){
			//reset all the views
			InitialMDGView.updateView(new Graph());
		}
		
	}
	
	@Override
	public String getErrorMessage() {
		return "";
	}

	@Override
	public boolean getRunNextAction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ActionMemento createMemento() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public File getSelectedProject() {
		return selectedProject;
	}

}

