package realeity.technique.util;

import java.io.File;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * License GPL
 * @date 2014-06-12 
 * @author Boaye Belle Alvine
 **/
public class ProjectSelection {
	private static ProjectSelection  uniqueInstance;
	
    private  ProjectSelection() {
		
	}	
    
    public static ProjectSelection getInstance(){
    	if(uniqueInstance == null){
    		uniqueInstance = new ProjectSelection();
    	}
    	return uniqueInstance;
    }
    
    /**
	 * Set the project on which the user just right clicked.
	 * @param event
	 */
    public File setSelectedProject(ExecutionEvent event){
		try{
			// get workbench window
			IWorkbenchWindow openedWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			// set selection service
			ISelectionService service = openedWindow.getSelectionService();
			// set structured selection
			IStructuredSelection selectedStructure = (IStructuredSelection) service.getSelection();
	        Object firstElement = selectedStructure.getFirstElement();
		 
			//get the selected project
			if (firstElement instanceof IAdaptable) {
				// get the selected file
	            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
	            //get the project's location
	            IPath projectLocation = project.getLocation();////affecter cette valeur au modele
				// get the project's name
				String projectName = project.getName();//affecter cette valeur au modele
				System.out.println("Selected project : " + projectName 
						           + ". Location : " + projectLocation);
				return projectLocation.toFile();
			} 
		}
		catch(Exception e){
			System.out.println("Error. No project has been selected.");
			//e.printStackTrace();
		}
		return null;
	}

}
