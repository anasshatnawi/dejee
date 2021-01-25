package realeity.core.layering.algorithms;


import java.util.List;

import realeity.core.layering.algorithms.settings.Setup;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.LayeredPartition;



/**
 * 
 * @author Alvine Boaye BELLE
 * 2013  11:42:31
 */
public interface IRecoveryAlgorithm {
	/**
	 * 
	 * @param layeredPartition
	 * @param algorithmParameters
	 * @return
	 */
	abstract LayeredPartition recoverLayers(LayeredPartition layeredPartition, 
			                                Setup algorithmParameters);
	
	/**
	 * 
	 * @param nodeList
	 * @return
	 */
	abstract List<AbstractGraphNode> clusteringByName(List<AbstractGraphNode> nodeList);

}
