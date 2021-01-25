package realeity.application.actions.layering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphItem;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.layouts.LayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.HorizontalShiftAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import realeity.application.actions.AbstractMicroAction;
import realeity.application.data.GeneratedData;
import realeity.application.layout.zest.ZestGraphContainer;
import realeity.views.swt.LayeringView;


/**
 * 
 * @author Boaye Belle Alvine
 * 2014
 *
 */
@SuppressWarnings("restriction")
public class ZestLayeringVisualizationAction extends AbstractMicroAction {
	int nodeStyle = ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT;

	/**
	 * Class constructor.
	 * @param actionName
	 */
	public ZestLayeringVisualizationAction(String actionName) {
		super(actionName);
	}
	
	@Override
	public void run(){
		//TODO complete
		try{
			
			LayeringView.updateView(GeneratedData.getInstance().getLayeringResult());
			
		    runNextAction = true;
		}
		catch(Exception e){
			System.out.println("An error occurred. No layering displayed.");
			//e.printStackTrace();
		}
		
	}
			
				
	/**
	 * 
	 * @param style
	 */
	protected void showGraph(){
      //TODO move this code in the Layereing view so as to increase the encapsulation
		Color nodeColor = new Color(LayeringView.getViewerShell().getDisplay(), 138, 11, 33);
		//242, 155, 9);//138, 11, 33);//37, 253, 233);//255, 255, 107)

		//fill the view with the data of a graph
		ZestGraphContainer graphContainer = new ZestGraphContainer(LayeringView.getGraph().getDisplay()); 
		graphContainer.createContainer(LayeringView.getGraph(), 
				                       GeneratedData.getInstance().getLayeringResult(),
				                       nodeStyle, nodeColor);
		
		LayeringView.getGraph().applyLayout();	
		
	}

	@Override
	public void monitorExecution() {
		// TODO Auto-generated method stub
		
	}
	 
}


