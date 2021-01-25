package realeity.application.actions.layering;

import java.util.ArrayList;

import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import realeity.application.ApplicationFacade;
import realeity.application.actions.AbstractMicroAction;
import realeity.application.data.GeneratedData;
import realeity.application.layout.AbstractDot;
import realeity.application.layout.LayeredPartitiondot;
import realeity.application.layout.zest.ZestGraphDot;
import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.elements.LayeredPartition;
import realeity.technique.extractor.ExtractionFacade;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.util.RealeityUtils;


/**
 * License GPL.
 * @author Boaye Belle Alvine
 * 2013
 *
 */
public class RunAlgorithmAction extends AbstractMicroAction {

	/**
	 * Class constructor.
	 * @param actionName
	 */
	public RunAlgorithmAction(String actionName) {
		super(actionName);
	}

	@Override
	public void run(){
		//2. run the layering algorithm. this operation can be split in two: aggregation + layering.
	    AbstractDot zestRepresentation = new ZestGraphDot();
 		AbstractDot dotRepresentation = new LayeredPartitiondot();
 		AbstractModuleDependencyGraph extractedGraph = ExtractionFacade.getInstance().buildGraphFromSegment(new ArrayList<KDMEntity>(),
                                                                       new ArrayList<AbstractExtractedFact>(),
                                                                       GeneratedData.getInstance().getProjectMainSegment());
 		GeneratedData.getInstance().setExtractedGraph(extractedGraph);
 		
 		LayeredPartition layeringResult = ApplicationFacade.getInstance().getALayeredDot(
 				                          GeneratedData.getInstance().getSelectedProject().getAbsolutePath(),
 				                          GeneratedData.getInstance().getExtractedGraph(),
 				                          AlgorithmContext.chosenAlgo,
 				                          RealeityUtils.GranularityNames.Module,
 				                          zestRepresentation, dotRepresentation, 
 				                          AlgorithmContext.graphsPath + AlgorithmContext.zestFileName,
 				                          AlgorithmContext.graphsPath + AlgorithmContext.dotFileName);
 		//System.out.println("The height of the name tree is : " + NameTree.treeHeight);   
 		GeneratedData.getInstance().setLayeringResult(layeringResult);
		runNextAction = true;
			
	}
	
	@Override
	public void monitorExecution() {
		// TODO Auto-generated method stub
		
	}
	
}
