package realeity.views.swt.wizard.layering.refinement;


import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Map;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.viewers.IStructuredSelection;

import realeity.application.data.GeneratedData;
import realeity.views.swt.wizard.AbstractWizard;



/**
 * Class that creates the wizard used to gather the informations required to perform a given 
 * layering algorithm.
 * @author Boaye Belle Alvine
 *
 */

public class NodeShiftingWizard extends AbstractWizard {
	public  static final int REFINEMENT_WIZARD_WIDTH = 350;
	public  static final int REFINEMENT_WIZARD_HEIGHT = 250;
	
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
	private LayeringRefinementWizardPageOne pageOne;
	
	/**
	 * 
	 */
	private  boolean launchRecovery = false;
	
	/**
	 * Default class constructor.
	 */
	public NodeShiftingWizard() {
		
	}
	
	/**
	 * Class's constructor.
	 */
	public NodeShiftingWizard(IWorkbench workbench, int width, int height) {
		super();
		this.wizardWorkbench = workbench;
				
	}
	
	@Override
	public void addPages(){
		pageOne = new LayeringRefinementWizardPageOne(wizardWorkbench, selection);
		addPage(pageOne);
	}
    
	@Override
	public void init(IWorkbench workBench, IStructuredSelection selection) {
		this.wizardWorkbench = workBench;
		this.selection = selection;
	}

	@Override
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
	
	public void setPageOne(LayeringRefinementWizardPageOne pageOne) {
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
	
	/**
	 * Center the wizard
	 * node should be moved.
	 * @param wizard
	 * @param width
	 * @param height
	 */
	public static void centerWizard (WizardDialog wizardDialog){
	    final Toolkit toolkit = Toolkit.getDefaultToolkit();
		final Dimension screenSize = toolkit.getScreenSize();
		final int pos_x = (screenSize.width - REFINEMENT_WIZARD_WIDTH) / 2;
		final int pos_y = (screenSize.height - REFINEMENT_WIZARD_HEIGHT) / 2;
		wizardDialog.getShell().setLocation(pos_x, pos_y);
		wizardDialog.getShell().setSize(REFINEMENT_WIZARD_WIDTH, REFINEMENT_WIZARD_HEIGHT);
	    //wizardDialog.setPageSize(width, height);
	    
	}
		
	public LayeringRefinementWizardPageOne getPageOne() {
		return pageOne;
	}
}//end class



	
		
