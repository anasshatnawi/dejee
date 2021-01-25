package realeity.application.layout;

import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.LayerP;
import realeity.core.mdg.elements.LayeredPartition;

/**
 * License GPL
 * Allows to generate the dot code of a a layered partition where each
 * layer comprises clusters.
 * That code is meant to be visualized by GraphViz.
 * @author Alvine Boaye Belle
 * 2013  12:33:02
 */
public class LayeredPartitiondot extends AbstractDot{


	@Override
	protected String generateNodes(LayeredPartition layeredPartition) {
		String nodesDot = "";
		for(LayerP layer : layeredPartition.getLayerList()){
			for(AbstractGraphNode graphNode : layer.getLayerContent()){
				nodesDot += createANode(graphNode);
			}
		}
		return nodesDot;
	}
	  

	@Override
	protected String generateSubgraphs(LayeredPartition partition) {
		LayeredPartition layeredPartition = (LayeredPartition) partition;
		String layerDot = "";
		for(int index = layeredPartition.getLayerList().size()-1; index >= 0; index--){
			LayerP layer = layeredPartition.getLayerList().get(index);
			 if(layer != null){
				 String subgraph = "subgraph cluster" + (layeredPartition.getLayerList().size() - index) 
						           + "{" + "\r\n";
				 String clusterLabel = "label =\"" + "Layer" + (layeredPartition.getLayerList().size() - index) + "\"" + "\r\n";
				 String style = "style =filled" + "\r\n";
				 String color="";
				 color = "color = "+(layerColors.get(colorIndex++))+"\r\n";//use one of the color in clustersColors
				                                                                  //to color the cluster
				if(colorIndex == layerColors.size())
					 colorIndex=0;
				
				//filling the subgraph with nodes (packages)
				 String dotNode = ""; 
				 for(AbstractGraphNode node : layer.getLayerContent()){
						/*GraphNode nodeInMDG = getNodeInInferredMDG(inferredMDG, node);
						String defaultNodeName = "node"+"_"+ nodeInMDG.getNodeId(); 
						*/
					 String defaultNodeName = "node" + "_" + node.getNodeId(); 
					 dotNode += defaultNodeName + "\r\n";
				}
				 
				 layerDot += subgraph + clusterLabel + style + color + dotNode;
				 layerDot += "}" + "\r\n" + "\r\n";
			 }
			 
		}
		
		return layerDot;
	}

	@Override
	protected String generateConnectionsBetweenNodes(LayeredPartition partition){ 
		String dotForConnections = "";
		LayeredPartition layeredPartition = (LayeredPartition) partition;
		for(LayerP layer : layeredPartition.getLayerList()){
			for(AbstractGraphNode node : layer.getLayerContent()){
				for(AbstractGraphNode successorNode : node.getSuccessorsList().keySet()){
					dotForConnections += super.createAConnectionBetween2Nodes(node, 
							                                                  successorNode);
				}
			}
		}
			
		return dotForConnections + "\r\n";
			
	}
	
	@Override
	public String generateGraphHeader(LayeredPartition layeredPartition, 
            AbstractModuleDependencyGraph graph) {
		String headGraph = "digraph layeredGraph {" + "\r\n";
		headGraph += super.generateGraphHeader(layeredPartition, graph);
		return headGraph;
	}

}
