package realeity.core.mdg.elements;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import realeity.core.mdg.AbstractModuleDependencyGraph;



/**
 * 
 * @author Boaye Belle Alvine
 * 2013  12:22:22
 */
public class LayerP extends AbstractGraphNode implements Cloneable {
	/**
	 * Unique digit identifying the layer.
	 */
	private int layerId;
	
	/**
	 * List of clusters contained in the layer.
	 */
	private List<AbstractGraphNode> layerContent;//TODO effacer cet attribut pour nútiliser que nodelist
	
	/**
	 * Class constructor.
	 */
	public LayerP() {
		super();
		layerContent = new ArrayList<AbstractGraphNode>();
	}
	
	
	/**
	 * Remove a node in the current layer and put it in another layer;
	 * @param nodeToMoveName
	 * @param layerTo
	 */
	public void shiftNode(String nodeToMoveName, LayerP layerTo){
		AbstractGraphNode nodeToMove = null;
		int indexToMove = -1;
		//find the node to move
		for(AbstractGraphNode nodeFrom : this.getLayerContent()){
			if(nodeFrom.determineNameSpace().equalsIgnoreCase(nodeToMoveName)){
				nodeToMove = nodeFrom;
				indexToMove = this.getLayerContent().indexOf(nodeFrom);
				break;
			}
		}
		
		this.getLayerContent().remove(indexToMove);
		layerTo.getLayerContent().add(nodeToMove);
		
	}
	
	public LayerP(int layerId, List<AbstractGraphNode> clusterList) {
		this.layerId = layerId;
		this.layerContent = clusterList;
	}
	
	public List<AbstractGraphNode> getLayerContent() {
		return layerContent;
	}
	
	
	public int getLayerId() {
		return layerId;
	}
	
	public void setLayerContent(List<AbstractGraphNode> layerContent) {
		this.layerContent = layerContent;
	}
	
	public void setLayerId(int layerId) {
		this.layerId = layerId;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String layerContentString = "layer "+ this.getUserDefinedName() + " : {";
		for(AbstractGraphNode node : layerContent){
			/*layerContentString += "   cluster " + layerContent.indexOf(node)
					              +  " : " + node.toString();*/
			layerContentString +=  node.toString() + ", ";
		}
		layerContentString += "}\n";
		return layerContentString;
	}
	
	@Override
	public Object clone() {
		LayerP layer = null;
		layer = (LayerP) super.clone();
		layer.layerContent = new ArrayList<AbstractGraphNode>();
		for(AbstractGraphNode node: layerContent){
			layer.layerContent.add(node);
		}
		return layer;
	}

	@Override
	public double computeCF(AbstractGraphNode omnipresentCluster,
			                AbstractGraphNode bagCluster, AbstractModuleDependencyGraph graph) {
		// TODO Auto-generated method stub
		return 0;
	}
}

