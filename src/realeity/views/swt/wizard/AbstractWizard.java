package realeity.views.swt.wizard;

import java.util.Map;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


/**
 * 
 * License GPL.
 * @author Boaye Belle Alvine
 * 2014-12-13
 *
 */
public class AbstractWizard extends Wizard implements INewWizard {
	
	public AbstractWizard() {
		
	}
	//TODO abstract the methods of the daughter classes here

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}
	
}//end class



	
		
	