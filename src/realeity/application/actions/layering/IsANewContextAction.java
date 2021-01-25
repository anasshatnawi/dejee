package realeity.application.actions.layering;

import realeity.application.actions.AbstractMicroAction;


/**
 * License GPL
 * @date 2014-06-11
 * @author Alvine
 *
 */
public class IsANewContextAction extends AbstractMicroAction {

	/**
	 * Class constructor.
	 * @param actionName
	 */
	public IsANewContextAction(String actionName) {
		super(actionName);
	}

	@Override
	public void run(){
		runNextAction = true;
	}
	
	@Override
	public void monitorExecution() {
		// TODO Auto-generated method stub
		
	}
}
