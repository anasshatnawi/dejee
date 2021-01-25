package realeity.application.actions.extraction;

import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.Label;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import realeity.application.actions.AbstractMicroAction;
import realeity.application.data.GeneratedData;
import realeity.application.layout.AbstractDot;
import realeity.application.layout.InitialMDGDot;
import realeity.application.layout.zest.InitialMDGZest;
import realeity.application.layout.zest.NodeModelContentProviderZest;
import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.technique.util.KDMEntityFullName;
import realeity.views.swt.InitialMDGView;
import realeity.views.swt.LayeringView;


/**
 * License GPL
 * Allows to display the graph representing the modules of the system.
 * @author Alvine Boaye Belle
 *
 */
@SuppressWarnings("restriction")
public class InitialMDGAction extends AbstractMicroAction {

	/**
	 * Class constructor.
	 * @param actionName
	 * @param tooltipText
	 * @param actionIconPath
	 */
	public InitialMDGAction(String actionName) {
		super(actionName);
	}

	@Override
	public void run(){
		try{
			generateGraphForGraphViz(AlgorithmContext.initialMDGPath, AlgorithmContext.initialMDGGraphViz);
			generateGraphForZest(AlgorithmContext.initialMDGPath, AlgorithmContext.initialMDGZest);
			runNextAction = true;	
		}
		catch(NullPointerException exception){
			// this exception occurs when the metrics view is hidden behind another view
    		//or if the tReALEITY perspective is not displayed on Eclipse
			System.out.println("--- An error occurred. You should open the ReALEITY perspective" +
					           " and put the MDG view on focus.");
		}
	}
	
	
	
	/**
	 * 
	 * @param parentDirectory
	 * @param zestFileName
	 */
	private void generateGraphForZest(String parentDirectory, String zestFileName){

		AbstractModuleDependencyGraph simpleMDG = GeneratedData.getInstance().getExtractedGraph();
		InitialMDGView.updateView(simpleMDG);
		String fileFullPath = GeneratedData.getInstance().getSelectedProject() + 
	                          parentDirectory + zestFileName;
		AbstractDot graphContent = new InitialMDGZest();
		//save the graph in a file
		graphContent.generateDotCode(simpleMDG, fileFullPath, 
                                     GeneratedData.getInstance().getSelectedProject() + parentDirectory);
	}
	
	/**
	 * 
	 * @param parentDirectory
	 * @param graphvizFileName
	 */
	private void generateGraphForGraphViz(String parentDirectory, String graphvizFileName){
		AbstractModuleDependencyGraph simpleMDG = GeneratedData.getInstance().getExtractedGraph();
		AbstractDot graphContent = new InitialMDGDot();
		String fileFullPath = GeneratedData.getInstance().getSelectedProject() + 
				              parentDirectory + graphvizFileName;
		graphContent.generateDotCode(simpleMDG, fileFullPath, 
				                     GeneratedData.getInstance().getSelectedProject() + parentDirectory);
	}
	
	@Override
	public void monitorExecution() {
		// TODO Auto-generated method stub
		
	}
	
}

