package realeity.application.actions;

import java.util.List;

import realeity.views.swt.LayeringView;
import realeity.views.swt.MeasuresView;
import realeity.views.swt.RecoveryBusyMonitor;

/**
 * License GPL.
 * @author Boaye Belle Alvine
 * 3 december 2014.
 */
public abstract class AbstractMacroAction extends AbstractRecoveryAction {
	
	protected List<AbstractMicroAction> actionsToExecuteBeforeMonitoring;
	protected List<AbstractMicroAction> ActionsToMonitor;
	protected List<AbstractMicroAction> actionsToExecuteAfterMonitoring;
	protected String progressBarName;
	protected boolean runNextMacroAction;


	/**
	 * Class's constructor
	 * @param actionName
	 * @param progressBarName
	 */
	public AbstractMacroAction(String actionName,  String progressBarName) {
		super(actionName);
		this.progressBarName = progressBarName; 
	}

	
	@Override
	public void undo() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void run() {
		runNextMacroAction = false;
		actionsToExecuteBeforeMonitoring = fillActionsToExecuteBeforeMonitor();
		ActionsToMonitor = fillActionsToMonitor();
		actionsToExecuteAfterMonitoring = fillActionsToExecuteAfterMonitor();
		specifyMacroAction();
		if(executeActionsBeForeMonitoring()){
			if(executeActionsWithCursor(ActionsToMonitor)){
				if(executeActionsAfterMonitoring()){
					runNextMacroAction = true;
				}
			}
		}
		
	}
	
	/**
	 * 
	 */
	protected void specifyMacroAction(){
		for(AbstractMicroAction microAction : actionsToExecuteBeforeMonitoring){
			microAction.setAssociatedMacroAction(this);
		}
		
		for(AbstractMicroAction microAction : ActionsToMonitor){
			microAction.setAssociatedMacroAction(this);
		}
		
		for(AbstractMicroAction microAction : actionsToExecuteAfterMonitoring){
			microAction.setAssociatedMacroAction(this);
		}
	}

	/**
	 * 
	 * @return
	 */
	public abstract List<AbstractMicroAction>  fillActionsToExecuteBeforeMonitor();
	
	/**
	 * 
	 * @return
	 */
	public abstract List<AbstractMicroAction> fillActionsToMonitor();
	
	/**
	 * 
	 * @return
	 */
	public abstract List<AbstractMicroAction> fillActionsToExecuteAfterMonitor();
	
	/**
	 * 
	 */
	protected boolean executeActionsBeForeMonitoring(){
		for(AbstractMicroAction microAction : this.actionsToExecuteBeforeMonitoring){
			microAction.run();
			if(!microAction.getRunNextAction()){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Monitor the list of actions to execute with a cursor
	 * @param ActionsToMonitor
	 */
	protected boolean executeActionsWithCursor(List<AbstractMicroAction> ActionsToMonitor){
		RecoveryBusyMonitor recoveryMonitor = new RecoveryBusyMonitor(this);
		boolean monitorDone = recoveryMonitor.showMonitor();
		return monitorDone;
	}
	
	/**
	 * 
	 */
	protected boolean executeActionsAfterMonitoring(){
		for(AbstractMicroAction microAction : this.actionsToExecuteAfterMonitoring){
			microAction.run();
			if(!microAction.getRunNextAction()){
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * 
	 */
	public abstract void resetData();
	
	/**
	 * Initialize the view so that they display nothing.
	 * @param graph
	 */
	public void resetViews() {
		MeasuresView.updateView(0, 0, 0, 0);
		LayeringView.updateView(null);
		
	}
	
	/**
	 * 
	 */
	public void deactivateActions(){
		
	}
	
	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getRunNextAction() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public List<AbstractMicroAction> getActionsToExecuteAfterMonitoring() {
		return actionsToExecuteAfterMonitoring;
	}
	
	public List<AbstractMicroAction> getActionsToExecuteBeforeMonitoring() {
		return actionsToExecuteBeforeMonitoring;
	}
	
	public List<AbstractMicroAction> getActionsToMonitor() {
		return ActionsToMonitor;
	}
	
	public String getProgressBarName() {
		return progressBarName;
	}

}
