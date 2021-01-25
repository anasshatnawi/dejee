package realeity.application.actions.layering;

import java.io.File;

import javax.swing.JFileChooser;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import realeity.application.actions.AbstractMicroAction;
import realeity.application.data.GeneratedData;
import realeity.views.swt.wizard.layering.RecoveryWizard;


/**
 * License GPL
 * @date 2014-06-11 
 * @author Alvine
 *
 */
public class DisplayLayeringWizardAction extends AbstractMicroAction {

	/**
	 * Class constructor.
	 * @param actionName
	 */
	public DisplayLayeringWizardAction(String actionName) {
		super(actionName);
	}

	@Override
	public void run(){
		// Instantiates and initializes the wizard that allows setting the algorithm parameters
		 RecoveryWizard wizard = new RecoveryWizard();
		 displayWizard(wizard, wizard.getWIZARD_WIDTH(), wizard.getWIZARD_HEIGHT());
	}
		
	@Override
	public void monitorExecution() {
		// TODO Auto-generated method stub
		
	}
}
