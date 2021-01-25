package realeity.core.mdg.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.technique.util.KDMEntityFullName;





/**
 * 
 * @author Alvine Boaye Belle
 * 2013  12:14:04
 */
public class Cluster extends AbstractGraphNode {

	/**
	 * List of nodes contained in the cluster.
	 */
	private List<AbstractGraphNode> nodeList;
	
	/**
	 * Class's constructor.
	 */
	public Cluster(int nodeIdentifier) {
		super(nodeIdentifier);
		this.nodeList = new ArrayList<AbstractGraphNode>();
	}
	
	/**
	 * Class's constructor
	 * @param nodeIdentifier
	 * @param nodeList
	 */
	public Cluster(int nodeIdentifier, List<AbstractGraphNode> nodeList) {
		super(nodeIdentifier);
		this.nodeList = nodeList;
	}
	
	/**
	 * Class's constructor
	 * @param nodeIdentifier
	 * @param successorsList
	 * @param predecessorsList
	 * @param nodeList
	 */
	public Cluster(int nodeIdentifier, Map<AbstractGraphNode, Integer> successorsList, 
                   Map<AbstractGraphNode, Integer> predecessorsList, 
                   List<AbstractGraphNode> nodeList) {
		super(nodeIdentifier, successorsList, predecessorsList);
		this.nodeList = nodeList;
	}
	
	
	/**
	 * Compute the clustering factor(CF) of a cluster. Cf Bunch
	 * @param omnipresent
	 * @param bag
	 * @param navigator
	 * @return
	 */
	public double computeCF(AbstractGraphNode omnipresentCluster,
			                AbstractGraphNode bagCluster,
			                AbstractModuleDependencyGraph graph){
		//compute the intra-connectivity AI of the current cluster as the sum of 
		//the edges' weight linking the nodes of this cluster
	    double AI = 0;
	    for(AbstractGraphNode node: nodeList){
	 	   for(AbstractGraphNode successorNode: node.getSuccessorsList().keySet()){
	 		   if(nodeList.contains(successorNode) 
	 			  && !omnipresentCluster.getNodeList().contains(node)
	 			  && !omnipresentCluster.getNodeList().contains(successorNode)
	 			  && !bagCluster.getNodeList().contains(successorNode))
	 		  	 AI += node.getSuccessorsList().get(successorNode);
	 	   }
	    }
	    
	    //compute the inter connectivity EI of the current cluster as the sum of the 
	    //edges's weights linking each node of this cluster to the nodes that do not belong to it
	    int Eij = 0;
		int Eji = 0;
	    for(AbstractGraphNode node: nodeList){
	    	for(AbstractGraphNode successorNode: node.getSuccessorsList().keySet()){
	    	  if(!nodeList.contains(successorNode) 
	    	     && !omnipresentCluster.getNodeList().contains(successorNode)
	    	     && !omnipresentCluster.getNodeList().contains(node)
	    	     && !bagCluster.getNodeList().contains(successorNode))	{
	    		  Eij += node.getSuccessorsList().get(successorNode) ;
	    	  }
	        }
	    	for(AbstractGraphNode predecessorNode: node.getPredecessorsList().keySet()){
	    		if(!nodeList.contains(predecessorNode) 
		    	     && !omnipresentCluster.getNodeList().contains(predecessorNode)
		    	     && !omnipresentCluster.getNodeList().contains(node))	{
	    			Eji += node.getPredecessorsList().get(predecessorNode); 
		    		
		        }
		     }
	    	
	    }
	    double EI = Eij + Eji;
	    //compute the cluster factor of the current cluster using the formula CF=(2*AI)/((2*AI)+EI)
	    double clusterFactor = 0;
	    if(EI != 0) {
	    	clusterFactor = (2 * AI) / ((2 * AI) + EI);
	 	 }
	  
		return  clusterFactor;
	}//end method
	
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public void addNode(AbstractGraphNode node){
		this.nodeList.add(node);
	}//end method (tested)
		
	/**
	 * 
	 * @param node
	 * @return
	 */
	public void removeNode(AbstractGraphNode node){
		this.removeNode(node);
	}//end method
	
	
	@Override
	public String toString() {
		String txt = "{";
		for(AbstractGraphNode node : this.nodeList){
			txt += KDMEntityFullName.getInstance().determineSpaceName(node.getEntity()) + ", ";
		}
		txt += "}\n";
		return txt;
	}
	
    @Override
	public List<AbstractGraphNode> getNodeList() {
		return nodeList;
	}
	
	public void setNodeList(List<AbstractGraphNode> nodeList) {
		this.nodeList = nodeList;
	}
	
	@Override
	public Object clone() {
		Cluster cluster = null;
		try {
			cluster = (Cluster) super.clone();
			cluster.nodeList = new ArrayList<AbstractGraphNode>();
			for(AbstractGraphNode node : this.nodeList){
				cluster.nodeList.add(node);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cluster;
	}
}

