package realeity.core.mdg.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

import realeity.core.algorithms.cost.ICostStrategy;
import realeity.core.layering.algorithms.settings.Setup;
import realeity.core.mdg.AbstractModuleDependencyGraph;


/**
 * 
 * @author Alvine
 * 2013  12:13:22
 */
public abstract class AbstractGraphNode implements Cloneable {

	/**
	 * Number identifying the node.
	 */
	protected int nodeIdentifier;
	
	/**
	 * Contains the list of successors of the node.
	 * Maps a successor node to the weight associated to the 
	 * dependency (current node, successor, weight).
	 */
	protected Map<AbstractGraphNode, Integer> successorsList;
	
	/**
	 * Contains the list of predecessors of the node.
	 * Maps a successor node to the weight associated to the 
	 * dependency (predecessor, current node, weight).
	 */
	protected Map<AbstractGraphNode, Integer> predecessorsList;
	
	/**
	 * A completer
	 */
	protected double partitionCost = -1 ;
	
	private String userDefinedName = "";
	
	/**
	 * Class's constructor.
	 * @param entity
	 */
	public AbstractGraphNode() {
		this.successorsList = new HashMap<AbstractGraphNode, Integer>();
		this.predecessorsList = new HashMap<AbstractGraphNode, Integer>();
	}
	
	/**
	 * Class's constructor.
	 * @param entity
	 */
	public AbstractGraphNode(int nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
		this.successorsList = new HashMap<AbstractGraphNode, Integer>();
		this.predecessorsList = new HashMap<AbstractGraphNode, Integer>();
	}
	
	/**
	 * Class's constructor.
	 * @param entity
	 * @param successorsList
	 * @param predecessorsList
	 */
	public AbstractGraphNode(int nodeIdentifier, 
			                 Map<AbstractGraphNode, Integer> successorsList, 
			                 Map<AbstractGraphNode, Integer> predecessorsList) {
		this.nodeIdentifier = nodeIdentifier;
		this.successorsList = successorsList;
		this.predecessorsList = predecessorsList;
	}
	
	/**
	 * 
	 * @param costStrategy
	 * @return
	 */
	public double computeCost(ICostStrategy costStrategy,
			                  Setup algorithmParameters){
		return 0; //costStrategy.computeLC(this);
	}
	
	
	public abstract  double computeCF(AbstractGraphNode omnipresentCluster,
			                          AbstractGraphNode bagCluster,
                                      AbstractModuleDependencyGraph graph);
	
    public int getNodeId() {
		return nodeIdentifier;
	}
	
	public Map<AbstractGraphNode, Integer> getSuccessorsList() {
		return successorsList;
	}
	
	
	public Map<AbstractGraphNode, Integer> getPredecessorsList() {
		return predecessorsList;
	}
	
	
	public List<AbstractGraphNode> getNodeList() {
		return new ArrayList<AbstractGraphNode>();
	}
	
	public double getPartitionCost() {
		return partitionCost;
	}
	
	public void setNodeId(int nodeId) {
		this.nodeIdentifier = nodeId;
	}
	
	public void setSuccessorsList(Map<AbstractGraphNode, Integer> successorsList) {
		this.successorsList = successorsList;
	}
	
	public void setPredecessorsList(Map<AbstractGraphNode, Integer> predecessorsList) {
		this.predecessorsList = predecessorsList;
	}
	
	public void setPartitionCost(double partitionCost) {
		this.partitionCost = partitionCost;
	}
	
	/**
	 * Compute the name space corresponding to the KDM entity(ies)contained in the node.
	 * @return
	 */
	public  String determineNameSpace(){
		return "Is an aggregate containing many KDM entities";//default value returned
	}
	
	
	@Override
	public Object clone() {
		AbstractGraphNode graphNode = null;
		try {
			graphNode = (AbstractGraphNode) super.clone();
			graphNode.successorsList = new HashMap<AbstractGraphNode, Integer>();
			graphNode.predecessorsList = new HashMap<AbstractGraphNode, Integer>();
			for(AbstractGraphNode node : this.successorsList.keySet()){
				graphNode.successorsList.put((AbstractGraphNode) node.clone(), 
						                     this.successorsList.get(node));
			}
			for(AbstractGraphNode node : this.predecessorsList.keySet()){
				graphNode.predecessorsList.put((AbstractGraphNode) node.clone(), 
						                       this.predecessorsList.get(node));
			}
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return graphNode;
	}

	public KDMEntity getEntity() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getUserDefinedName() {
		return userDefinedName;
	}
	
	public void setUserDefinedName(String userDefinedName) {
		this.userDefinedName = userDefinedName;
	}
}
