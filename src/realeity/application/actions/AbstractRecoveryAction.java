package realeity.application.actions;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import realeity.application.data.ActionMemento;



/**
 * License GPL
 * @author Boaye Belle Alvine
 * 2014
 *
 */
public abstract class AbstractRecoveryAction extends Action  {
	
	private ActionMemento memento;
	
	/**
	 * Class's constructor.
	 * @param tooltipText
	 * @param actionIconPath
	 * @param numberOfUpdates
	 */
	public AbstractRecoveryAction(String actionName) {
		this.setText(actionName);
	}
	
	/**
	 * Set the image of the action
	 * @param tooltipText
	 * @param actionIconPath
	 */
	public void specifyImage(String tooltipText,
			                 String actionIconPath) {
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		URL actionIconURL = FileLocator.find(bundle, new org.eclipse.core.runtime.Path(actionIconPath), 
				                               null);
		ImageDescriptor actionImageDescriptor = ImageDescriptor.createFromURL(actionIconURL);
        this.setToolTipText(tooltipText);
		this.setImageDescriptor(actionImageDescriptor); 
	}

	
	public void run() {
		memento = createMemento();
	}
	
	/**
	 * 
	 * @return
	 */
	public abstract ActionMemento createMemento();
	
	
	/**
	 * Use a Memento to reset the state of the data as if the current action 
	 * has not been performed.
	 */
	public abstract void undo();
	
	public abstract String getErrorMessage();
	
	public abstract boolean  getRunNextAction();

	public abstract File getSelectedProject();

	public List<AbstractRecoveryAction>  getMicroActionList() {
		return null;
	}
	
}



