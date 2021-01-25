package realeity.core.mdg;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.MDGIterator;
import realeity.technique.extractor.fact.AbstractExtractedFact;



/**
 * 
 * @author Alvine Boaye Belle
 * 2013  12:02:17
 */
public abstract class AbstractModuleDependencyGraph implements Cloneable {

	/**
	 * List of nodes constituting the graph.
	 */
	protected List<AbstractGraphNode> nodeList;
	
	
	/**
	 * List of omnipresent nodes constituting in the graph.
	 */
	protected List<AbstractGraphNode> omnipresentList;
	
	/**
	 * Class's constructor.
	 */
	public AbstractModuleDependencyGraph() {
		nodeList = new ArrayList<AbstractGraphNode>();
		omnipresentList = new ArrayList<AbstractGraphNode>();
	}
	
	abstract public Graph buildDependencyGraph(List<KDMEntity> entitiesList, 
			                                   List<AbstractExtractedFact> dependencies);
	
	abstract public Graph buildGraph(List<AbstractGraphNode> graphNodes,
                                     List<AbstractExtractedFact> dependencies) ;
	
	abstract public void computeOmnipresentNodes ();
	
	/**
	 * Create an iterator over the graph's nodes list.
	 * @return
	 */
	public abstract MDGIterator createMDGIterator();
	
	
	/**
	 * 
	 * @param graphNodeClusters
	 * @param simpleMDG graph whose not are nodes and not aggregates of nodes ie clusters.
	 */
	abstract public void buildInferredDependencyGraph(List<AbstractGraphNode> graphNodeClusters,
			                                          AbstractModuleDependencyGraph simpleMDG);
	
	/**
	 * Use the nodes of a cluster to create a module dependency graph.
	 * The latter is obtained using the connections between the nodes of an input module
	 * dependency graph.
	 */
	public void createSmallerMDG(AbstractGraphNode cluster, 
			                     AbstractModuleDependencyGraph existingMDG){
		
	}
	public abstract void restaureSuccessors(List<AbstractGraphNode> clusterList, 
                                            AbstractModuleDependencyGraph simpleMDG) ;
	
	public List<AbstractGraphNode> getOmnipresentNodes() {
		return this.omnipresentList;
	}
	
	public void setOmnipresentList(List<AbstractGraphNode> omnipresentList) {
		this.omnipresentList = omnipresentList;
	}
	
	public List<AbstractGraphNode> getNodeList() {
		return nodeList;
	}
	
	public void setNodeList(List<AbstractGraphNode> nodeList) {
		this.nodeList = nodeList;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	public AbstractModuleDependencyGraph copyGraph() {
		// TODO Auto-generated method stub
		return null;
	}
}
