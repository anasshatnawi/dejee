package realeity.application.actions.layering;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.List;

import org.eclipse.draw2d.Layer;
import org.eclipse.gef4.zest.core.widgets.GraphContainer;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import realeity.application.actions.AbstractMicroAction;
import realeity.application.actions.metrics.DisplayMetricsAction;
import realeity.application.data.GeneratedData;
import realeity.application.layout.zest.ZestGraphContainer;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.LayerP;
import realeity.core.mdg.elements.LayeredPartition;
import realeity.views.swt.LayerRefinementDialog;
import realeity.views.swt.wizard.layering.RecoveryWizard;
import realeity.views.swt.wizard.layering.refinement.NodeShiftingWizard;

/**
 * 
 * License GPL.
 * @author Boaye Belle Alvine.
 * 2014-11-22
 * 19:30:43
 */
public class RefinementAction extends AbstractMicroAction {
	
	private static RefinementAction refinementAction; 
	
	public static final String REFINE_LAYERING_ACTION_NAME = "Layering refinement";
	private GraphNode nodeToProcess;
	
	private String layerRefinementName = "Layer new Name: ";
	private String dialogTitle = "Layer renaming dialog";
	private boolean destinationSpecified = false;
	

    /**
     * Class's constructor.
     * @param actionName
     * @param nodeToProcess
     */
	public RefinementAction(String actionName, GraphNode nodeToProcess) {
		super(actionName);
		this.nodeToProcess = nodeToProcess;
	}
	
	
	@Override
	public void run() {
		boolean canRefresh = true;
		if(this.isEnabled()){
			if(nodeToProcess instanceof GraphContainer){//the node is a layer that needs to be renamed
				displayRenamingWizard();
			}
			else if(nodeToProcess instanceof GraphNode) {//the node has to be moved
				//display the refinement wizard
				NodeShiftingWizard refinementWizard = new NodeShiftingWizard();
				displayNodeShiftingWizard(refinementWizard);
				if(destinationSpecified){
					shiftNode(GeneratedData.getInstance().getLayeringResult(),
							  nodeToProcess, 
							  refinementWizard.getPageOne().getLayerToName());
				}
				else 
					canRefresh = false;
			}
			if(canRefresh){

				//update the layering view
				AbstractMicroAction visualizationAction = new ZestLayeringVisualizationAction("Visualization of the layering results");
				visualizationAction.run();
			}
			
		}
	}
	
	/**
	 * Display a dialog allowing the user to rename a layer in the Layering-view and
	 * update the layering view according to the name specified by the user.
	 */
	private void displayRenamingWizard(){
		LayerRefinementDialog renamingView = new LayerRefinementDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
				                                                       dialogTitle, layerRefinementName);
		renamingView.create();
		renamingView.open();
		try{
			if(!renamingView.getInputName().equals("")){
				int selectedLayerIndex = findLayerIndex(GeneratedData.getInstance().getLayeringResult(), 
						                                nodeToProcess.getText());
				GeneratedData.getInstance().getLayeringResult().getLayerList().get(selectedLayerIndex).setUserDefinedName(renamingView.getInputName());
			}
		}
		catch(NullPointerException exception){
			//System.out.println("This exception occurs when we hit on the cancel button of the wizard");
		}
			
	}
	
	/**
	 * Find the index of the layer to modify in the layering result
	 * @param partition
	 * @param selectedLayer
	 * @return
	 */
	private int findLayerIndex(LayeredPartition partition, String selectedLayerName){
		int layerIndex = -1;
		
		String [] layerToken = selectedLayerName.split(" ");
		for(LayerP layer : partition.getLayerList()){
			if(layer.getUserDefinedName().equalsIgnoreCase(selectedLayerName)
				||(layerToken.length > 1 && layer.getUserDefinedName().equalsIgnoreCase(layerToken[1]))){
				layerIndex = partition.getLayerList().indexOf(layer);
				break;
			}
		}
		
		
	  if(layerIndex == -1){//by default, a layer name is: LAYER_PREFIX + "layer_id"
			String [] tokens;
			try{
				tokens = selectedLayerName.split(ZestGraphContainer.LAYER_PREFIX);
				layerIndex = partition.getLayerList().size() - Integer.parseInt(tokens[1]);
			}
			catch(Exception exception){
				tokens = selectedLayerName.split(" ");
				layerIndex = partition.getLayerList().size() - Integer.parseInt(tokens[1]);
			}
			

		}
		return layerIndex;
	}
	
	/**
	 * Display a wizard allowing the user to specify the layer where the selected
	 * node should be moved.
	 * @param wizard
	 */
	private void displayNodeShiftingWizard(NodeShiftingWizard wizard){
		try{
			// Instantiates and initializes the wizard that allows setting the algorithm parameters
		    //RecoveryWizard wizard = new RecoveryWizard();
		    wizard.init(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getWorkbench(), null);  
		    // Instantiates the wizard container with the wizard and opens it
		    WizardDialog wizardDialog = new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
		    		                                     wizard);
		    wizardDialog.create();
		    NodeShiftingWizard.centerWizard(wizardDialog);
		    wizardDialog.open();
		    
		    destinationSpecified = wizard.getLaunchRecovery();
		}
		catch(Exception e){
			System.out.println("An Error occured during the wizard opening");
			runNextAction = false;
			e.printStackTrace();
		}	
	}
	
	
	private void shiftNode(LayeredPartition partition, GraphNode nodeToMove, String layerToName){
		//find the destination layer
		LayerP destinationLayer = partition.getLayerList().get(findLayerIndex(partition, 
				                                               layerToName));
		
		//find the original layer
		LayerP originalLayer = null;
		String nodeToMoveName = nodeToMove.getText();
		for(LayerP layerFrom : partition.getLayerList()){
			for(AbstractGraphNode nodeFrom: layerFrom.getLayerContent()){
				if(nodeFrom.determineNameSpace().equalsIgnoreCase(nodeToMoveName)){
					originalLayer = layerFrom;
					break;
				}
			}
		}
		
	
		//move the node
		if(originalLayer != null)
			originalLayer.shiftNode(nodeToMoveName, destinationLayer);

	}
	
	@Override
	public void monitorExecution() {
		// TODO Auto-generated method stub
		
	}
}
