package realeity.application.layout.zest;


import org.eclipse.gmt.modisco.omg.kdm.code.Package;

import realeity.application.layout.AbstractDot;
import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.LayerP;
import realeity.core.mdg.elements.LayeredPartition;
import realeity.technique.util.KDMEntityFullName;




/**
 * Store in a file the dot code corresponding to a layered partition.
 * This dot code can be visualized using the plugin Zest.
 * @author Alvine Boaye Belle
 * 2013  23:36:56
 */
public class ZestGraphDot extends AbstractDot {
	
	
	private String clusterColor = "";
	private String layerPosition = "TB";
	/**
	 * Generate a partition by applying the selected layering algorithm on 
	 * the data restrieved from the system under analysis.
	 * @param layeringAlgorithm
	 */
	public ZestGraphDot() {
	}
	
	 @Override
	 public String createANode(AbstractGraphNode node){
			String dot = ""; 
			String nodeName = KDMEntityFullName.getInstance().determineSpaceName(node.getEntity()); 
			String defaultNodeName = "node" + "_" + node.getNodeId();
			dot += defaultNodeName + "[" + "\r\n";
			dot += "label=\"" + "" + nodeName + "\"" + "\r\n";
			dot += "]" + "\r\n" ;
			 
			
			return dot;
		}//end method
	 
	 /**
		 * generates the dot code necessary to create the connection between the packages.
		 * these connections are stored as FactDependencys (packageFrom, packageEnd, weight).
		 * the graph has no clusters and each of its node represents a package.
		 * NB: if you have a huge amount of edges, replace the "label" by "xlabel" 
		 * only in the line allowing to create the connection!
		 * @param listOfFactDependencys represent the weight of the relation between  2 packages. 
		 * @return
		 */
	 @Override
		public String generateConnectionsBetweenNodes(LayeredPartition layeredPartition){
			//build the dot code corresponding to these connections
			String dotForConnections = "";
			for(LayerP layer : layeredPartition.getLayerList()){
				for(AbstractGraphNode node : layer.getLayerContent()){
					for(AbstractGraphNode successorNode : node.getSuccessorsList().keySet()){
						dotForConnections += createAConnectionBetween2Nodes(node, successorNode);
					}
				}
				
			}
				
			return dotForConnections + "\r\n";
		}//end method
		
		
		/**
		 * 
		 * @param node
		 * @param successorNode
		 * @return
		 */
		@Override
		protected String createAConnectionBetween2Nodes(AbstractGraphNode node,
				                                        AbstractGraphNode successorNode ){
			//build the dot code corresponding to these connections
			String dotForConnections = ""; 
			String startNodeName = "node" + "_" + node.getNodeId();//node.getNodeFullName();
			String endNodeName = "node" + "_" + successorNode.getNodeId();//successor.getNodeFullName();
			int weight = node.getSuccessorsList().get(successorNode);
			dotForConnections += startNodeName + " -> " + endNodeName + "[ penwidth = 1, fontsize = 8," +
		                         " label=\"" + weight + "\"" + " ]" + "\r\n";
			
			return dotForConnections;
		}//end method
	
	 /**
	  * 	
	  * @param layeredPartition
	  * @return
	  */
		@Override
	  public String generateSubgraphs(LayeredPartition partition){ 
			/**
			 * the dot code of a layer is like a cluster's one and the relations between nodes are 
			 * treated as dependencies in the graph.
			 */
	    	String layerDot = "";
	    	LayeredPartition layeredPartition = (LayeredPartition) partition;
			for(int index = 0; index <= layeredPartition.getLayerList().size() - 1; index++){
				LayerP layer = layeredPartition.getLayerList().get(index);
				 if(layer != null){
					 String subgraph = "subgraph cluster" + (layeredPartition.getLayerList().size() - index) 
							           + "{" + "\r\n";
					 String clusterLabel = "label =\"" + "Layer" + (layeredPartition.getLayerList().size() - index) 
							               + "\"" + "\r\n";
					 String alignment = "rankdir = " + layerPosition + "\r\n";
					
					//filling the subgraph with nodes (packages)
					 String dotNode = ""; 
					 for(AbstractGraphNode node : layer.getLayerContent()){
							/*GraphNode nodeInMDG = getNodeInInferredMDG(inferredMDG, node);
							String defaultNodeName = "node"+"_"+ nodeInMDG.getNodeId(); 
							*/
						 dotNode += createANode(node) + "\r\n";
					}
					 
					 layerDot += subgraph + clusterLabel + alignment + dotNode;
					 layerDot += "}" + "\r\n" + "\r\n";
				 }
				 
			}
			
			return layerDot;
		}//end  method
	
	/**
	  * Generates the dot code corresponding to the creation of the clusters (subgraphs) of the graph. 
	  * Each cluster corresponds to a layer of 
	  * the analyzed system.
	  */
	  public String createClusters(AbstractModuleDependencyGraph inferredMDG, 
			                       LayerP layer){ 
		  String dotCluster = "";
			for(AbstractGraphNode cluster : layer.getLayerContent()){
				 if((cluster != null) && (cluster.getNodeList().size() > 0)){//est ce necessaire
					
					//filling the subgraph with nodes (packages)
 					 for(AbstractGraphNode node : cluster.getNodeList()){
							//GraphNode nodeInMDG = getNodeInInferredMDG(inferredMDG, node);
							//dotCluster += createANode(nodeInMDG);
 						dotCluster += createANode(node);
						}
					
					 dotCluster +=  "\r\n";
				 }
			}
			
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
	protected String generateNodes(LayeredPartition partition) {
		return "";
	}
}
