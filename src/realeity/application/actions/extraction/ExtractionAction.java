package realeity.application.actions.extraction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import realeity.application.actions.AbstractMacroAction;
import realeity.application.actions.AbstractMicroAction;
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
public class ExtractionAction extends AbstractMacroAction {
	

	/**
	 * Class's constructor.
	 * @param actionName
	 * @param progressBarName
	 */
	public ExtractionAction(String actionName, String progressBarName) {
		super(actionName, progressBarName);
		resetData();
	}
	
	@Override
	public void run() {
		//specify the list of micro-actions fillMicroActionList();
		resetViews();
	    deactivateActions();
		super.run();
	}

	@Override
	public List<AbstractMicroAction> fillActionsToExecuteBeforeMonitor() {

		return new ArrayList<AbstractMicroAction>();
	}

	@Override
	public List<AbstractMicroAction> fillActionsToMonitor() {
		List<AbstractMicroAction> actionsToExecute = new ArrayList<AbstractMicroAction>();
		actionsToExecute.add(new GenerateKDMAction("KDM generation"));
		actionsToExecute.add(new FactExtractionAction("Extraction of the facts"));
		return actionsToExecute;
	}

	@Override
	public List<AbstractMicroAction> fillActionsToExecuteAfterMonitor() {
		List<AbstractMicroAction> actionsToExecute = new ArrayList<AbstractMicroAction>();
		actionsToExecute.add(new InitialMDGAction("Construction of the  module dependency graph"));
		return actionsToExecute;
	}

	@Override
	public void resetData() {
		super.actionsToExecuteBeforeMonitoring = new ArrayList<AbstractMicroAction>();
		super.ActionsToMonitor = new ArrayList<AbstractMicroAction>();
		super.actionsToExecuteAfterMonitoring = new ArrayList<AbstractMicroAction>();
		GeneratedData.getInstance().resetAll();
	}
	

	@Override
	public void resetViews() {
		super.resetViews();
		InitialMDGView.updateView(new Graph());
		
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

	@Override
	public File getSelectedProject() {
		// TODO Auto-generated method stub
		return null;
	}

}
