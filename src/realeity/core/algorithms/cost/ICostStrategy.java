package realeity.core.algorithms.cost;

import realeity.core.layering.algorithms.settings.Setup;
import realeity.core.mdg.elements.AbstractGraphNode;


/**
 * Interfacing grouping the methods allowing to compute the cost of a partition.
 * @author Alvine Boaye Belle
 *
 */
public interface ICostStrategy {
	
	double computeLC(AbstractGraphNode layeredPartition,
			         Setup algorithmParameters);

}
