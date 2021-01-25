package realeity.core.mdg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.Cluster;
import realeity.core.mdg.elements.MDGIterator;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.util.KDMEntityFullName;



/**
 * 
 * @author Alvine Boaye Belle
 * 2013  23:49:53
 */
public class InferredGraph extends AbstractModuleDependencyGraph {
	
	/**
	 * Unique digit identifying a node.
	 */
	private static int nodeIdentifier = 1;

	 /**
	  * Class's constructor.
	  */
	public InferredGraph() {
		super();
	}
	
	@Override
	public MDGIterator createMDGIterator() {
		return new MDGIterator(this);
	}

	
	/**
	 *  
	 * @param clusterList
	 * @param simpleMDG
	 */
	public void buildInferredDependencyGraph(List<AbstractGraphNode> clusterList, 
			                                 AbstractModuleDependencyGraph simpleMDG) {
		
		for(AbstractGraphNode aggregate : clusterList){
			AbstractGraphNode cluster = new Cluster(aggregate.getNodeId(), 
					                                aggregate.getNodeList());
			nodeList.add(cluster);
		}
		
		// Build the successors of a each entity: for each entity corresponding to the start of a dependency,
		// add a key under the form (end dependency, weight)
		for(AbstractGraphNode cluster : nodeList){
			this.buildClusterSuccessors(cluster);
			this.buildClusterPredecessors(cluster);
		}
		
	}
	
	/**
	 * 
	 * @param clusterList
	 * @param simpleMDG
	 */
	public void restaureSuccessors(List<AbstractGraphNode> clusterList, 
                                   AbstractModuleDependencyGraph simpleMDG) {
		for(AbstractGraphNode cluster : clusterList){
			for(AbstractGraphNode clusterNode : cluster.getNodeList()){
				for(AbstractGraphNode graphNode : simpleMDG.getNodeList()){
					if(graphNode.getNodeId() == clusterNode.getNodeId()){
						clusterNode.setSuccessorsList(graphNode.getSuccessorsList());
						clusterNode.setPredecessorsList(graphNode.getPredecessorsList());
						break;
					}
				}
			}
			
		}
	}
	
	/**
	 * To complete
	 * @param factToConsider
	 * @param clusterList
	 */
	public void buildInferredDependencyGraph(List<AbstractExtractedFact> factToConsider, 
			                                 List<AbstractGraphNode> clusterList) {
		reset();
		for(AbstractGraphNode aggregate : clusterList){
			AbstractGraphNode cluster = new Cluster(aggregate.getNodeId(), aggregate.getNodeList());
			nodeList.add(cluster);
		}
		
		//Build the successors of a each entity: for each entity corresponding to the start of a dependency,
		// add a key under the form (end dependency, weight)
		for(AbstractExtractedFact fact : factToConsider){
			this.addSuccessor(fact);
			this.addPredecessor(fact);
		}
		
	}
	
	/**
	 * A COMPLETER
	 * @param cluster
	 * @param mdg
	 */
	private void buildClusterSuccessors(AbstractGraphNode cluster){
		for(AbstractGraphNode elementaryNode : cluster.getNodeList()){
			for(AbstractGraphNode succcessorNode : elementaryNode.getSuccessorsList().keySet()){
				AbstractGraphNode successorCluster = null;
				//find the aggregate containing the successor node
				for(AbstractGraphNode potentialClusterSuccessor : nodeList){
					if(potentialClusterSuccessor.getNodeList().contains(succcessorNode) 
					   && (potentialClusterSuccessor != cluster)){
						successorCluster = potentialClusterSuccessor;
						break;
					}
				}
				if(successorCluster != null){
					int oldWeight = 0;
					try{
						oldWeight = cluster.getSuccessorsList().get(successorCluster);
					}
					catch (Exception e) {
						//triggered if there is no oldweight stored in the SuccessorsList
					}
					int newWeight = oldWeight + elementaryNode.getSuccessorsList().get(succcessorNode);
					cluster.getSuccessorsList().put(successorCluster, newWeight);
				}
			}
		}
		
	}
	
	/**
	 * A COMPLETER
	 * @param cluster
	 * @param mdg
	 */
	private void buildClusterPredecessors(AbstractGraphNode cluster){
		for(AbstractGraphNode elementaryNode : cluster.getNodeList()){
			for(AbstractGraphNode predecessorNode : elementaryNode.getPredecessorsList().keySet()){
				AbstractGraphNode predecessorCluster = null;
				//find the aggregate containing the successor node
				for(AbstractGraphNode potentialPredecessorCluster : nodeList){
					if(potentialPredecessorCluster.getNodeList().contains(predecessorNode) 
					   && (potentialPredecessorCluster != cluster)){
						predecessorCluster = potentialPredecessorCluster;
						break;
					}
				}
				if(predecessorCluster != null){
					int oldWeight = 0;
					try{
						oldWeight = cluster.getPredecessorsList().get(predecessorCluster);
					}
					catch (Exception e) {//triggered if there is no oldweight stored in the predecessorList
						
					}
					int newWeight = oldWeight + elementaryNode.getPredecessorsList().get(predecessorNode);
					cluster.getPredecessorsList().put(predecessorCluster, newWeight);
				}
			}
		}
	}
	
	
	/**
	 * 
	 * @param dependency
	 */
	private void addSuccessor(AbstractExtractedFact dependency){
		//find the successor of start among the nodes of the graph
		/*AbstractGraphNode successorNode = null;
		for(AbstractGraphNode node : nodeList){
			if(node.getNodeId() == dependency.getDependencyEnd().getNodeId()){
				successorNode = node;
				break;
			}
		}
		
		//add the successor in the  list of successors of  the node containing start
		AbstractGraphNode start = dependency.getDependencyStart();
		for(AbstractGraphNode nodeStart : nodeList){
			if(start.getNodeId() == nodeStart.getNodeId()){
				Map<AbstractGraphNode, Integer> successorsList = nodeStart.getSuccessorsList();
				successorsList.put(successorNode, dependency.getDependencyWeight());
				nodeStart.setSuccessorsList(successorsList);
				break;
				
			}
		}*/
	}
	
	/**
	 * To complete
	 * @param dependency
	 */
	private void addPredecessor(AbstractExtractedFact dependency){
		/*AbstractGraphNode start = dependency.getDependencyStart();
		//Among the nodes of the graph, find the node having the same content that start 
		AbstractGraphNode predecessorNode = null;
		for(AbstractGraphNode node : nodeList){
			if(node.getNodeId() == start.getNodeId()){
				//(node.getNodeFullName().equals(dependency.getDependencyStart().getNodeFullName()))
				predecessorNode = node;
				break;
			}
		}
		
		//add the predecessor in the  list of predecessors of  the node containing end
		AbstractGraphNode end = dependency.getDependencyEnd();
		for(AbstractGraphNode node : nodeList){
			if(end.getNodeId() == node.getNodeId()){
				Map<AbstractGraphNode, Integer> predecessorsList = node.getPredecessorsList();
				predecessorsList.put(predecessorNode, dependency.getDependencyWeight());
				node.setPredecessorsList(predecessorsList);
				break;
			}
		}*/
	}

	/**
	 * Remove a module from the graph.
	 * @param node
	 */
	public void removeNode(AbstractGraphNode node) {
		nodeList.remove(node);
		
	}

	/**
	 * 
	 * @param aggregate
	 * @param nodeList
	 * @param successorsList
	 * @param predecessorsList
	 */
	public void addNode(AbstractGraphNode aggregate, 
			            List<AbstractGraphNode> nodeList,
			            Map<AbstractGraphNode, Integer> successorsList, 
            Map<AbstractGraphNode, Integer> predecessorsList) {
		AbstractGraphNode graphNode = new Cluster(nodeIdentifier, successorsList, 
				                                  predecessorsList, nodeList) ;
		nodeIdentifier++;
		nodeList.add(graphNode);
	}
	
	public void reset(){
		nodeList = new ArrayList<AbstractGraphNode>();
		nodeIdentifier = 1;
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String txt = "The content of the inferred graph is as follows: " + "\r\n";
		/*for(AbstractGraphNode node: nodeList){
			for(AbstractGraphNode successor : node.getSuccessorsList().keySet()){
				AggregatedFact fact = new AggregatedFact(node, successor, 
						                                 node.getSuccessorsList().get(successor), 
						                                 RelationsTypes.Aggregated.toString());
				txt += fact.toString();
			
			}
		}*/
		return txt;
	}

	
	@Override
	public void computeOmnipresentNodes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Graph buildDependencyGraph(List<KDMEntity> entitiesList,
			List<AbstractExtractedFact> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph buildGraph(List<AbstractGraphNode> graphNodes,
			List<AbstractExtractedFact> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
