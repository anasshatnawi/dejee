package realeity.views.swt.event;

import org.eclipse.gef4.zest.core.widgets.GraphContainer;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.gef4.zest.core.widgets.Graph;

import realeity.application.actions.layering.RefinementAction;

/**
 * Handle a double-click on a graph' element (node, layer, etc.).
 * License GPL.
 * @author Boaye Belle Alvine
 * 2014-11-29
 */
@SuppressWarnings("restriction")
public class MouseDoubleClick implements MouseListener {
	private Graph graph;
	private static int lastEventX ;
	private static int lastEventY;
	
	public MouseDoubleClick(Graph graph){
		this.graph = graph;
	}

	@Override
	public void mouseDoubleClick(MouseEvent event) {
		//to avoid that the double-clicking causes twice the realization of the same action on the same node
		int newEventX = event.x;
		int newEventY = event.y; //a layer is a graphcontainer and a graphnode !!!
		if((lastEventX != newEventX) && (lastEventY != newEventY) 
			&& (graph.getSelection().get(0) instanceof GraphNode)){
			GraphNode selectedNode = (GraphNode) graph.getSelection().get(0);
			System.out.println("The user is refining the element : " + selectedNode.getText());
			RefinementAction refinementAction = new RefinementAction(RefinementAction.REFINE_LAYERING_ACTION_NAME,
					                                                                 selectedNode);
			//do something with selectedNode
			refinementAction.run();
			
			//reset the positions
			lastEventX = lastEventY = 0;
		}
		lastEventX = newEventX;
		lastEventY = newEventY;
		
	}

	@Override
	public void mouseDown(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseUp(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
