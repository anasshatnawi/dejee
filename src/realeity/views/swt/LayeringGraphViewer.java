package realeity.views.swt;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.widgets.Graph;

/**
 * 
 * License GPL
 * @author alvine
 * 2014-11-22
 * 20:26:16
 */
@SuppressWarnings("restriction")
public class LayeringGraphViewer extends GraphViewer {

	public LayeringGraphViewer(Graph graph) {
		super(graph);
	}
	
	public void setControl(Graph graph){
		super.graph = graph;
	}

}
