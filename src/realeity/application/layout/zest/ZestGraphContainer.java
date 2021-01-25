package realeity.application.layout.zest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.LayeredPane;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphContainer;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.layouts.LayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.HorizontalShiftAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.LayerP;
import realeity.core.mdg.elements.LayeredPartition;
import realeity.technique.util.KDMEntityFullName;


/**
 * This code is inspired from https://eclipse.googlesource.com/gef/org.eclipse.gef/+/b5187fa044386457f89a0f45b5d3a25f28dda00c/org.eclipse.zest.tests/src/org/eclipse/zest/tests/swt/NestedGraphSnippet.java
 * @author Alvine
 *
 */
public class ZestGraphContainer {
	private static Image image1;
	private static Image classImage;
	private static final int CONTAINER_WIDTH = 250;
	private static final int CONTAINER_HEIGHT = 230;
	static Display display;
	private int graphOrientation = 2;
	private Map<AbstractGraphNode, GraphNode> mapNodes;
	public static String LAYER_PREFIX = "Layer_";
	
	//TODO factoriser cet attribut qui se retrouve aussi dans certaines vues
	private int curveDepth = 45;

	//int nodeStyle = ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT;
	
	public ZestGraphContainer(Display display) {
		this.display = display;
		mapNodes = new HashMap<AbstractGraphNode, GraphNode>();
	}

	public void createContainer(Graph graph, LayeredPartition partition, 
			                    int nodeStyle, Color nodeColor) {
		//remove all the nodes and connections comprised in the graph 
		
	    deleteNodesAndConnections(graph);
		try{
			graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(), true); 
			graph.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
			
			List<GraphNode> nodes = new ArrayList<GraphNode>();
			List<GraphContainer> layerContainers = createLayerContainers(graph, partition, 
					                                                     nodeStyle, nodeColor,
					                                                     nodes);
			
			for(GraphNode node: nodes){
				node.setBackgroundColor(nodeColor);
				node.setForegroundColor(new Color(nodeColor.getDevice(), 255, 255, 255));
				node.setHighlightColor(new Color(nodeColor.getDevice(), 242, 155, 9));
	         }

			createConnections(nodes, graph);
			
			//specify the alignment of the nodes in each container ie layer
			for(GraphContainer layerContainer : layerContainers){
				layerContainer.setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);
			}
			
			//specifyConnectionsCurves(curveDepth, graph.getConnections());

			graph.setOrientation(graphOrientation);
		}
		catch (Exception e) {
			// TODO: in case the partition is null for instance
		}
	}
	
	/**
	 * remove all the nodes and connections comprised in the graph 
	 * @param graph
	 */
	private void deleteNodesAndConnections(Graph graph){
		List<GraphNode> oldNodes = graph.getNodes();
		List<GraphConnection> oldConnections = graph.getConnections();
		int index = oldNodes.size();
		for(int nodeIndex = 0; nodeIndex < index; nodeIndex++){
			GraphNode oldNode = oldNodes.get(0);
			if(!oldNode.isDisposed())
			    oldNode.dispose();
		}
		
		int indexConn = oldConnections.size();
		for(int conNum = 0; conNum < indexConn; conNum++){
			GraphConnection oldConn = oldConnections.get(0);
			if(!oldConn.isDisposed())
			oldConn.dispose();
		}
	}
	
	/**
	 * Create the containers comprising the content of  the layers.
	 * @param graph
	 * @param partition
	 * @param nodeStyle
	 * @param nodeColor
	 * @param nodes
	 * @return
	 */
	private List<GraphContainer> createLayerContainers(Graph graph, LayeredPartition partition,
			                                    int nodeStyle, Color nodeColor,
			                                    List<GraphNode> nodes){
		List<GraphContainer> layerContainers = new ArrayList<GraphContainer>();

		int scaleIndicator = 2;
		int index = 0;
		for(LayerP layer : partition.getLayerList()){
			if(layer.getLayerContent().size() > 0){
				GraphContainer layerContainer = new GraphContainer(graph, SWT.NONE);
				//b.setLocation(10 + (moveX * i), 10 + (moveY * i));
				layerContainer.setSize(CONTAINER_WIDTH, CONTAINER_HEIGHT);
				layerContainer.setFont(new Font(display,"Arial", 17,SWT.BOLD));
				layerContainer.setForegroundColor(new Color(nodeColor.getDevice(), 255, 255, 255));
				int layerNumber = partition.getLayerList().size() - index++;
				if(layer.getUserDefinedName().equals(""))
					layerContainer.setText(LAYER_PREFIX + layerNumber);
				else 
					layerContainer.setText(layer.getUserDefinedName());
				layerContainer.setImage(classImage);
				layerContainer.setBackgroundColor(nodeColor);//(new Color(nodeColor.getDevice(), 251, 191, 95));
				scaleIndicator = 2;
				nodes.addAll(createNodes(layerContainer, graph, scaleIndicator, 
						     true, layer, nodeStyle));
				layerContainers.add(layerContainer);
			}

		}
		return layerContainers;
	}
	
	
	
	/**
	 * create a connection between two nodes
	 * @param graphModel
	 * @param style
	 * @param source
	 * @param destination
	 * @param labelConnection
	 * @return
	 */
	private GraphConnection createConnection(Graph graphModel, int style, GraphNode source, 
			                                 GraphNode destination, int labelConnection){
		GraphConnection connection = new GraphConnection(graphModel, style, 
				                                         source, destination); 
		
		connection.setText("" + labelConnection);
		connection.setLineColor(new Color(display, 35, 15, 22));
		return connection;
	}
	
	
	/**
	 * Set the curve of each graph's connection.
	 * @param curveDepth
	 * @param connections
	 */
	private static void specifyConnectionsCurves(int curveDegree, 
			                                     java.util.List<GraphConnection> connections){
		//TODO Factoriser ce code commun a certaines vues 
		int connectionSize = connections.size();
		
		//to avoid a concurrent modification exception:
		for(int curveIndex = 0; curveIndex < connectionSize; curveIndex++){
			GraphConnection conn = connections.get(curveIndex);
			conn.setCurveDepth(curveDegree);
		}
	
	}
	
	/**
	 * 
	 * @param nodes
	 */
	private void createConnections(List<GraphNode> nodes, Graph graph){
		//create connections 
		for(AbstractGraphNode mdgNode: mapNodes.keySet()){
		for(AbstractGraphNode successorNode : mdgNode.getSuccessorsList().keySet()){
			String successorName = KDMEntityFullName.getInstance().determineSpaceName(successorNode.getEntity()); 
			 GraphNode zestNodeSuccessor = null;
			 for(GraphNode zestNode :  nodes){
				 if(successorName.equals(zestNode.getText())){
					 zestNodeSuccessor = zestNode;
					 break;
				 }
			 }
			 GraphNode zestFrom = mapNodes.get(mdgNode);
			 int weight = mdgNode.getSuccessorsList().get(successorNode);
			 GraphConnection connect = createConnection(graph, 1, mapNodes.get(mdgNode), 
					                                    zestNodeSuccessor, weight);
	   }
	 }
				
	}

	/**
	 * 
	 * @param c
	 * @param g
	 * @param number
	 * @param radial
	 */
	private List<GraphNode> createNodes(GraphContainer container, Graph g, int number, boolean radial, 
			                LayerP layer, int nodeStyle) {
		List<GraphNode> nodes = new ArrayList<GraphNode>();
		//create nodes
		for(AbstractGraphNode mdgNode: layer.getLayerContent()){
			GraphNode zestNode = new GraphNode(container, nodeStyle);
			String nodeName = KDMEntityFullName.getInstance().determineSpaceName(mdgNode.getEntity());
			zestNode.setText(nodeName);
			zestNode.setImage(classImage);
			mapNodes.put(mdgNode, zestNode);
			nodes.add(zestNode);	
		}
			
		if (number == 1) {
			container.setScale(0.75);
		} else if (number == 2) {
			container.setScale(0.50);
		} else {
			container.setScale(0.25);
		}
		if (radial) {
			container.setLayoutAlgorithm(new org.eclipse.gef4.zest.layouts.algorithms.RadialLayoutAlgorithm(), 
					                     true);
		} else {
			container.setLayoutAlgorithm(new org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm(), 
					                     true);
		}
		
		return nodes;
	}
	
	
	public int getCurveDepth() {
		return curveDepth;
	}
	
	public void setCurveDepth(int curveDepth) {
		this.curveDepth = curveDepth;
	}
}


