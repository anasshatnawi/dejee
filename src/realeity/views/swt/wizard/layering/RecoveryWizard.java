package realeity.views.swt.wizard.layering;

import java.util.Map;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.jface.viewers.IStructuredSelection;

import realeity.views.swt.wizard.AbstractWizard;


/**
 * Class that creates the wizard used to gather the informations required to perform a given 
 * layering algorithm.
 * @author Boaye Belle Alvine
 *
 */

public class RecoveryWizard extends AbstractWizard {
	protected int WIZARD_WIDTH = 150;
	protected int WIZARD_HEIGHT = 375;
	
	/**
	 * 
	 */
	protected IStructuredSelection selection;
	
	/**
	 *  the workbench instance
	 */
	protected IWorkbench wizardWorkbench;
	
	/**
	 * First page of he wizard.
	 */
	private WizardPageOne pageOne;
	
	/**
	 * 
	 */
	private  boolean launchRecovery = false;
	
	/**
	 * Default class constructor.
	 */
	public RecoveryWizard() {
		
	}
	
	/**
	 * Class's constructor.
	 */
	public RecoveryWizard(IWorkbench workbench) {
		super();
		this.wizardWorkbench = workbench;
	}
	
	public void addPages(){
		pageOne = new WizardPageOne(wizardWorkbench, selection);
		addPage(pageOne);
	}

	@Override
	public void init(IWorkbench workBench, IStructuredSelection selection) {
		this.wizardWorkbench = workBench;
		this.selection = selection;
	}

	public boolean canFinish(){ 
		return this.pageOne.getFirstPageCompleted();	
	}
	
	@Override
	public boolean performFinish() {
		if(pageOne.getFirstPageCompleted()){
			launchRecovery = true;
		  }
		else{
			pageOne.setErrorMessage("This context has already been used. Specify a new one.");
			//TODO change this error message
			this.pageOne.setFirstPageCompleted(false);//to disable the can finish button
			launchRecovery = false;
		}
		return launchRecovery;
	}
	
	public boolean getLaunchRecovery(){
		return this.launchRecovery;
	}
	
	public IWorkbench getWizardWorkbench() {
		return wizardWorkbench;
	}
	
	public void setPageOne(WizardPageOne pageOne) {
		this.pageOne = pageOne;
	}
	
	public void setLaunchRecovery(boolean launchRecovery) {
		this.launchRecovery = launchRecovery;
	}
	
	public void setWizardWorkbench(IWorkbench wizardWorkbench) {
		this.wizardWorkbench = wizardWorkbench;
	}
	
	public Map<String, String> getContextMap() {
		return pageOne.getenteredElements();
	}
	
	
	public int getWIZARD_HEIGHT() {
		return WIZARD_HEIGHT;
	}
	
	public int getWIZARD_WIDTH() {
		return WIZARD_WIDTH;
	}
	
}//end class



	
		
		