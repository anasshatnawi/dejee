package realeity.application.layout.zest;

import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.LayerP;
import realeity.core.mdg.elements.LayeredPartition;


/**
 * Represent the MDG obtained by the extraction of the facts from the analyzed system
 * That code is meant to be visualized by Zest.
 * @author Alvine
 *
 */
public class InitialMDGZest extends ZestGraphDot {
	
	
	private String clusterColor = "";
	private String layerPosition = "TB";
	/**
	 * Generate a partition by applying the selected layering algorithm on the data retrieved 
	 * from the system under analysis.
	 * @param layeringAlgorithm
	 */
	public InitialMDGZest() {
	}
	
	 /**
	  * 	
	  * @param layeredPartition
	  * @return
	  */
		@Override
	  public String generateSubgraphs(LayeredPartition partition){ 
			/**
			 * the dot code of a layer is like a cluster's one and the relations between 
			 * nodes are treated as dependencies in the graph.
			 */
	    	String layerDot = "\r\n";
			
			return layerDot;
		}//end  method
	
	/**
	  * Generates the dot code corresponding to the creation of the clusters (subgraphs) of the graph. 
	  * each cluster corresponds to a layer of the analyzed system.
	  */
	  public String createClusters(AbstractModuleDependencyGraph inferredMDG, 
			                       LayerP layer){ 
		  String dotCluster = "\r\n";
			
		  return dotCluster;
		}//end  method
	  
	  
	  /**
		 * header required to create a dot file representing a graph.
		 * @return
		 */
	  @Override
		 public String generateGraphHeader(LayeredPartition layeredPartition, 
                                           AbstractModuleDependencyGraph graph){
			String headGraph = "digraph zestGraph {" + "\r\n";//"digraph{" + "\r\n";//
			//headGraph += super.generateGraphHeader();
		 return headGraph;
		}
	  
	@Override
	protected String generateNodes(AbstractModuleDependencyGraph graph) {
		 String dotCluster = "\r\n";
		 for(AbstractGraphNode node : graph.getNodeList()){
			 dotCluster += createANode(node);
		 }
		  return dotCluster;
	}
	
	@Override
	protected String generateConnectionsBetweenNodes(AbstractModuleDependencyGraph graph) {
		String dotForConnections = "\r\n";
		 for(AbstractGraphNode node : graph.getNodeList()){
			 for(AbstractGraphNode successorNode : node.getSuccessorsList().keySet()){
					dotForConnections += createAConnectionBetween2Nodes(node, 
							                                            successorNode);
				}
		 }
		 return dotForConnections;
	}
	 

	@Override
	protected String generateNodes(LayeredPartition partition) {
		return "";
	}
}
