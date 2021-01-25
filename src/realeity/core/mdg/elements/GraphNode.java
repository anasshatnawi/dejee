package realeity.core.mdg.elements;

import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.technique.extractor.AbstractKDMRelationshipExtractor;
import realeity.technique.util.KDMEntityFullName;



/**
 * 
 * @author Alvine Boaye Belle
 * 2013  11:59:59
 */
public class GraphNode extends AbstractGraphNode {

	// TODO complete that class
	/**
	 * Entity represented by the node. Could be a KDM package for instance.
	 */
	private KDMEntity entity;
	
	/**
	 * Class's constructor.
	 * @param nodeIdentifier
	 * @param entity
	 */
	public GraphNode(int nodeIdentifier, KDMEntity entity) {
		super(nodeIdentifier);
		this.entity = entity;
	}
	
	/**
	 * Class's constructor.
	 * @param nodeIdentifier
	 * @param successorsList
	 * @param predecessorsList
	 * @param entity
	 */
	public GraphNode(int nodeIdentifier, KDMEntity entity, 
			         Map<AbstractGraphNode, Integer> successorsList, 
                     Map<AbstractGraphNode, Integer> predecessorsList) {
		super(nodeIdentifier, successorsList, predecessorsList);
		this.entity = entity;;
	}
	
	
	@Override
	public String toString() {
		KDMEntity containerPackage = AbstractKDMRelationshipExtractor.findPackageTo(this.getEntity());
		String txt = "The node " + nodeIdentifier + " named " 
				+ KDMEntityFullName.getInstance().determineSpaceName(containerPackage)
				+ "_" + this.getEntity().getName() + ","
	                 + " Its successors are : ";
		for(AbstractGraphNode successor : this.successorsList.keySet()){
			try{
				txt += successor.getEntity().getName()+ ", ";

			}
			catch(NullPointerException e){
                 System.out.println();
			}
		}
		txt += ". Its predecessors: ";
		for(AbstractGraphNode predecessor : this.predecessorsList.keySet()){
			txt +=  predecessor.getEntity().getName()+ ", ";
		}
		txt += "\r\n";
		return txt;
	}
	
	/* (non-Javadoc)
	 * @see realeity.core.mdg.elements.AbstractGraphNode#determineNameSpace()
	 */
	@Override
	public String determineNameSpace() {
		return KDMEntityFullName.getInstance().determineSpaceName(this.entity);
	}
	
	@Override
	public KDMEntity getEntity() {
		return this.entity;
	}
	
	public void setEntity(KDMEntity entity) {
		this.entity = entity;
	}

	@Override
	public double computeCF(AbstractGraphNode omnipresentCluster,
			AbstractGraphNode bagCluster, AbstractModuleDependencyGraph graph) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
