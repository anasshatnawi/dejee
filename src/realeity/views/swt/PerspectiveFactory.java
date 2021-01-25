package realeity.views.swt;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import realeity.application.actions.layering.LayeringHandler;
/**
 * License GPL
 * For examples, see http://www.programcreek.com/2013/02/eclipse-plug-in-development-creat-a-perspective/
 * and from http://www.eclipse.org/articles/using-perspectives/PerspectiveArticle.html
 * and http://projectdev.blogspot.ca/2012/03/eclipse-how-to-create-new-perspective.html
 * june 2014
 * @author Alvine Boaye Belle
 *
 */
public class PerspectiveFactory  implements IPerspectiveFactory {
	
	 
	@Override
	public void createInitialLayout(IPageLayout layout) {

    }
	
	public void defineActions(IPageLayout layout) {
    
    }
	
	public void defineLayout(IPageLayout layout) {
        
    }
	

	void fillOpenToMenu(IMenuManager menu, IStructuredSelection selection) {
		
	}
}
