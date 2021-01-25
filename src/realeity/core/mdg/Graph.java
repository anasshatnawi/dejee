package realeity.core.mdg;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.GraphNode;
import realeity.core.mdg.elements.MDGIterator;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.util.KDMEntityFullName;



/**
 * 
 * @author Alvine Boaye Belle
 * 2013  12:02:37
 */
public class Graph extends AbstractModuleDependencyGraph {
	
	/**
	 * Unique digit identifying a node.
	 */
	private int nodeIdentifier = 1;
	
	/**
	 * Class's constructor.
	 */
	public Graph() {
		super();
	}
	
	@Override
	public MDGIterator createMDGIterator() {
		return new MDGIterator(this);
	}


	/**
	 * Add a node to the graph.
	 * @param entity
	 * @param successorsList
	 */
	public void addNode(KDMEntity entity, Map<AbstractGraphNode, Integer> successorsList, 
			            Map<AbstractGraphNode, Integer> predecessorsList) {
		// TODO Auto-generated method stub
		AbstractGraphNode graphNode = new GraphNode(nodeIdentifier, 
				                                    entity, 
				                                    successorsList, 
				                                    predecessorsList);
		nodeIdentifier++;
		nodeList.add(graphNode);
	}

	/**
	 * Remove a module from the graph.
	 * @param node
	 */
	public void removeNode(AbstractGraphNode node) {
		// TODO Auto-generated method stub
		nodeList.remove(node);
		
	}

	/**
	 * Build a graph using the data (dependencies) extracted from the KDM representation 
	 * of the system under analysis.
	 * @param entities : all the entities contained in the dependencies as startDependency or endDependency.
	 * @param dependencies
	 * @return
	 */
	public Graph buildDependencyGraph(List<KDMEntity> entitiesList, 
			                          List<AbstractExtractedFact> dependencies) {
		reset();
	    //Specify the nodes of the graph
		for(KDMEntity entity: entitiesList){
			this.addNode(entity, new HashMap<AbstractGraphNode, Integer>(), 
					     new HashMap<AbstractGraphNode, Integer>());
		}
		
		//Build the successors of a each entity: for each entity corresponding to the start of a 
		//dependency add a key under the form (end dependency, weight)
		for(AbstractExtractedFact dependency : dependencies){
			addANodeSuccessor(dependency);
			addANodePredecessor(dependency);
		}
		
		return this;
	}
	
	@Override
	public Graph buildGraph(List<AbstractGraphNode> graphNodes,
			                List<AbstractExtractedFact> dependencies) {
		reset();
	    //Specify the nodes of the graph
		for(AbstractGraphNode entity: graphNodes){
			AbstractGraphNode node = new GraphNode(entity.getNodeId(), 
					                               entity.getEntity(), 
					                               new HashMap<AbstractGraphNode, Integer>(), 
					                               new HashMap<AbstractGraphNode, Integer>());
			this.nodeList.add(node);
		}
		
		//Build the successors of a each entity: for each entity corresponding to the start of a 
		//dependency add a key under the form (end dependency, weight)
		for(AbstractExtractedFact dependency : dependencies){
			addANodeSuccessor(dependency);
			addANodePredecessor(dependency);
		}
		
		return this;
		
	}

	/**
	 * 
	 * @param dependency
	 */
	private void addANodeSuccessor(AbstractExtractedFact dependency){
		KDMEntity start = dependency.getDependencyStart();
		//find the successor of start among the nodes of the graph
		AbstractGraphNode successorNode = null;
		for(AbstractGraphNode node : nodeList){
			if(((GraphNode) node).getEntity().equals(dependency.getDependencyEnd())){
				successorNode = node;
				break;
			}
		}
		
		//add the successor in the  list of successors of  the node containing start
		for(AbstractGraphNode node : nodeList){
			if(start.equals(((GraphNode) node).getEntity())){
				Map<AbstractGraphNode, Integer> successorsList = node.getSuccessorsList();
				if(successorNode != null){
					successorsList.put(successorNode, dependency.getDependencyWeight());
					node.setSuccessorsList(successorsList);
					break;
				}
				
			}
		}
	}
	
	/**
	 * 
	 * @param dependency
	 */
	private void addANodePredecessor(AbstractExtractedFact dependency){
		
		//find the predecessor of end among the nodes of the graph
		AbstractGraphNode predecessorNode = null;
		for(AbstractGraphNode node : nodeList){
			if(((GraphNode) node).getEntity().equals(dependency.getDependencyStart())){
				predecessorNode = node;
				break;
			}
		}
		
		//add the predeccessor in the  list of successors of  the node containing end
		KDMEntity end = dependency.getDependencyEnd();
		for(AbstractGraphNode node : nodeList){
			if(end.equals(((GraphNode) node).getEntity())){
				Map<AbstractGraphNode, Integer> predecessorsList = node.getPredecessorsList();
				predecessorsList.put(predecessorNode, dependency.getDependencyWeight());
				node.setPredecessorsList(predecessorsList);
				break;
			}
		}
	}
	
	
	@Override
	public void createSmallerMDG(AbstractGraphNode cluster, 
			                     AbstractModuleDependencyGraph existingMDG){
		//specify the node of the current graph.
		for(AbstractGraphNode clusterNode : cluster.getNodeList()){
			this.nodeList.add(clusterNode);
		}
		
		//specify the successors and the predecessors of the current graph
		for(AbstractGraphNode graphNode : this.nodeList){
			buildSuccessors(graphNode, cluster);
			buildPredecessors(graphNode, cluster);
		}
	}
	
	/**
	 * Build the successors of a graph's node.
	 * @param graphNode
	 * @param cluster
	 */
	private void buildSuccessors(AbstractGraphNode graphNode, 
			                     AbstractGraphNode cluster){
		Map<AbstractGraphNode, Integer> successorsMap = new HashMap<AbstractGraphNode, Integer>();
		for(AbstractGraphNode successorNode : graphNode.getSuccessorsList().keySet()){
			if(cluster.getNodeList().contains(successorNode)){
				int weight = graphNode.getSuccessorsList().get(successorNode);
				successorsMap.put(successorNode, weight);
			}
		}
		//Replace the former successors of the node with new ones
		graphNode.setSuccessorsList(successorsMap);
	}
	
	/**
	 * Build the predecessors of a graph's node.
	 * @param graphNode
	 * @param cluster
	 */
	private void buildPredecessors(AbstractGraphNode graphNode, 
			                       AbstractGraphNode cluster){
		Map<AbstractGraphNode, Integer> predecessorsMap = new HashMap<AbstractGraphNode, Integer>();
		for(AbstractGraphNode predecessorNode : graphNode.getPredecessorsList().keySet()){
			if(cluster.getNodeList().contains(predecessorNode)){
				int weight = graphNode.getPredecessorsList().get(predecessorNode);
				predecessorsMap.put(predecessorNode, weight);
			}
		}
		//Replace the former predecessors of the node with new ones
		graphNode.setPredecessorsList(predecessorsMap);
	}
	
	/**
	 * Initialize the list of nodes.
	 */
	public void reset(){
		nodeList = new ArrayList<AbstractGraphNode>();
		nodeIdentifier = 1;
	}
	
	
	@Override
	public String toString() {
		String txt = "The content of the graph is as follows: " + "\r\n";
		for(AbstractGraphNode node: this.nodeList){
			txt += node.toString();
		}
		return txt;
	}

	@Override
	/**
	 * Put all the omnipresent entities of the graph in the omnipresent cluster (this). 
	 * All these modules will not be considered when partitioning. An entity is omnipresent 
	 * if the number of its incident edges is greater than upper tail (see the box-plot formula). 
	 * We do not take the weight of a relation into account, so that an entity which is used 
	 * many times by the same entity will not be considered as an omnipresent.
	 * @param listEntities
	 * @return
	 */
	public void computeOmnipresentNodes (){
		//AbstractGraphNode[] potentialOmni = new GraphNode[this.getModuleList().size()];
		//each element of this table contains the entity whose the sum of 
		AbstractGraphNode nodeToSort [] = new GraphNode[this.getNodeList().size()];
		int[] sumEdgesToSort = new int[this.getNodeList().size()];
		
		int indexNode = 0;
		for(AbstractGraphNode node : this.nodeList){	
			nodeToSort[indexNode] = node;
			sumEdgesToSort[indexNode++] = node.getPredecessorsList().size();
			
		}//end for
		
		//order the elements of sumEdgesToSort from the smallest to the largest using the bubble sort
		boolean inversion = true;
		while(inversion){
			inversion = false;
			for(int indexToSort = 0; indexToSort < (nodeToSort.length - 1); indexToSort++){
				if(sumEdgesToSort[indexToSort] > sumEdgesToSort[indexToSort + 1]){
					//check whether sumEdgesToSort.get(i)>sumEdgesToSort.get(j)and
					//permute the two if so
					int val_i = sumEdgesToSort[indexToSort];
					sumEdgesToSort[indexToSort] = sumEdgesToSort[indexToSort+1];
					sumEdgesToSort[indexToSort + 1] = val_i; 
					AbstractGraphNode val_nodes_i = nodeToSort[indexToSort];
					nodeToSort[indexToSort] = nodeToSort[indexToSort+1];
					nodeToSort[indexToSort + 1] = val_nodes_i;
					inversion = true;
					}
				}

		}
		
		for(int nodeIndex = 0; nodeIndex < sumEdgesToSort.length; nodeIndex++){
			System.out.println("Graph class : sumEdgesToSort=" + sumEdgesToSort[nodeIndex] 
					           + " i=" + nodeIndex + " potential omni=" 
					           + KDMEntityFullName.getInstance().determineSpaceName(nodeToSort[nodeIndex].getEntity()));
		}
			omnipresentBoxPlot(sumEdgesToSort, nodeToSort);
			//computeExtraOmnipresent(navigator);
	}
	
	/**
	 * Put in the omnipresent list, all the entities whose sum of the incident edges is 
	 * greater than a threshold (upper_tail).
	 * @param sumEdgesToSort
	 * @param potentialOmni
	 * @param nodeList
	 */
	void omnipresentBoxPlot(int[] sumEdges, AbstractGraphNode[] nodeToSort){
		int median_index = sumEdges.length/2;
		int Q1_index = sumEdges.length/4;
		int Q3_index = (3 * sumEdges.length)/4;
		int median = sumEdges[median_index];
		int Q1 = sumEdges[Q1_index];
		int Q3 = sumEdges[Q3_index];
		int d = Q3 - Q1;
		int upper_tail = Q3 + (3 * d)/2;
		int lower_tail = Q1 -(3 * d)/2;
	
		System.out.println("median_index=" + median_index + " Q1_index=" 
		                   + Q1_index + " Q3_index=" + Q3_index + " d=" + d + " " +
				           "upper_tail="+upper_tail+" lower_tail="+lower_tail);
		
		//consider as omnipresent all the nodes whose sum of the incident edges is greater than upper tail
		for(int nodeIndex = 0; nodeIndex < sumEdges.length; nodeIndex++){
			if(sumEdges[nodeIndex] > upper_tail){
				omnipresentList.add(nodeToSort[nodeIndex]);	
				System.out.println("upper_tail= " + upper_tail + " " +
						           "sumEdges = "+ sumEdges[nodeIndex] + " omnipresentNode: " + 
						            KDMEntityFullName.getInstance().determineSpaceName(nodeToSort[nodeIndex].getEntity()));
				//nodeList.remove(potentialOmni[i]);
			}		
		}
		System.out.println();
	}
	
	
	public int getNodeIdentifier() {
		return nodeIdentifier;
	}
	
	public void setOmnipresentNodes(List<AbstractGraphNode> omnipresentNodes) {
		this.omnipresentList = omnipresentNodes;
	}
	
	public void setNodeIdentifier(int nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}
    
	public List<AbstractGraphNode> getNodeList() {
		return nodeList;
	}
	
	public void setNodeList(List<AbstractGraphNode> nodeList) {
		this.nodeList = nodeList;
	}
	

	@Override
	public void buildInferredDependencyGraph(
			List<AbstractGraphNode> graphNodeClusters, AbstractModuleDependencyGraph simpleMDG) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public AbstractModuleDependencyGraph copyGraph(){
		Graph graph = new Graph();
		for(AbstractGraphNode node : nodeList){
			AbstractGraphNode nodeCopy = new GraphNode(node.getNodeId(), 
					                                   node.getEntity(), 
					                                   node.getSuccessorsList(),
					                                   node.getPredecessorsList());
			graph.nodeList.add(nodeCopy);
		}
		for(AbstractGraphNode omnipresentNode : omnipresentList){
			AbstractGraphNode omnipresentCopy = new GraphNode(omnipresentNode.getNodeId(), 
					                                          omnipresentNode.getEntity(), 
					                                          omnipresentNode.getSuccessorsList(),
					                                          omnipresentNode.getPredecessorsList());
			graph.omnipresentList.add(omnipresentCopy);
		}
		return graph;
	}

	@Override
	public void restaureSuccessors(List<AbstractGraphNode> clusterList,
			AbstractModuleDependencyGraph simpleMDG) {
		// TODO Auto-generated method stub
		
	}
	
	
}//end class
