package realeity.core.algorithms.cost;

import realeity.core.LayersMetrics;
import realeity.core.layering.algorithms.settings.Setup;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.LayerP;
import realeity.core.mdg.elements.LayeredPartition;
import realeity.technique.util.RealeityUtils;


/**
 * Evaluate the layering cost LC of a given layered partition
 * @author Alvine Boaye Belle
 * 2014
 */

public class LCCostStrategy implements ICostStrategy {

	
	private static RealeityUtils.MetricTypes dependencyType = RealeityUtils.MetricTypes.AdjacencyCallsMetric;
	
	/**
	 * Class's constructor.
	 */
	public LCCostStrategy() {

	}
	
	
	public double computeLC(AbstractGraphNode partition, 
			                Setup algoParameters){
		if(partition instanceof LayeredPartition){
			LayeredPartition layeredPartition = (LayeredPartition) partition;
			evaluateMetrics(layeredPartition);
			//TODO generalize the cost computation to the other children of AbstractGraphNode!
			//this way, all the other children can use their own strategy to compute the cost
				
			 double layeringCost = (algoParameters.getAdjacencyPenalty() * layeredPartition.getPartitionMetrics().getNumberOfAdjacencyCalls()) +
			                       (algoParameters.getIntraPenalty() * layeredPartition.getPartitionMetrics().getNumberOfIntraCalls()) +
			                       (algoParameters.getSkipPenalty() * layeredPartition.getPartitionMetrics().getNumberOfSkipCalls()) +
			                       (algoParameters.getBackPenalty() * layeredPartition.getPartitionMetrics().getNumberOfBackCalls()) ;
			 partition.setPartitionCost(layeringCost);
			 return layeringCost;
		}
		else
			return 0 ; //default value
	 }
	
	/**
	 * Determine the kind of dependency existing between two layers.
	 * The kind can be adjacent, skip-call, intra dependency or 
	 * back-call dependency.
	 * @param startIndex
	 * @param endIndex
	 * @param systemOfLayers
	 */
	 void determineDependencyType(int startIndex, int endIndex, 
			                      LayeredPartition systemOfLayers){
			boolean noIntermediateLayer = true;
			if(startIndex == endIndex){
				dependencyType = RealeityUtils.MetricTypes.IntraCallsMetric;//if startindex == endindex
			}
			else{
				if(startIndex < endIndex){
					for(int index = startIndex + 1; index < endIndex; index++){
						LayerP layer = systemOfLayers.getLayerList().get(index);
						if(layer.getLayerContent().size() > 0)
							noIntermediateLayer = false;
					 }
					if((noIntermediateLayer == false) && (startIndex < (endIndex + 1)))
						dependencyType = RealeityUtils.MetricTypes.SkipCallsMetric;
					else if(noIntermediateLayer)
						dependencyType = RealeityUtils.MetricTypes.AdjacencyCallsMetric;
				}
				else if(startIndex > endIndex){
					dependencyType = RealeityUtils.MetricTypes.BackCallsMetric;
				}
				
			}
			     
		}
	 
	
		
	 /**
	  * Evaluate the 4 types of metrics in a layered partition.
	  * @param layeredPartition
	  */
		public  void evaluateMetrics(LayeredPartition layeredPartition){
			int adjac = 0;
			int intra = 0;
			int skip= 0;
			int back = 0;

			for(LayerP layerFrom : layeredPartition.getLayerList()){
				for(AbstractGraphNode nodeFrom : layerFrom.getLayerContent()){
					for(AbstractGraphNode successor : nodeFrom.getSuccessorsList().keySet()){
						//find the layer to which successor belongs
						LayerP successorLayer = null;
						for(LayerP layer : layeredPartition.getLayerList()){//find the layer to 
							for(AbstractGraphNode node : layer.getLayerContent()){
								//if(node.getNodeList().get(0).getEntity() == successor.getNodeList().get(0).getEntity()){
								if(node.getEntity() == successor.getEntity()){//the  first package is the same
									//TODO normally, it should be an if(node.getNodeId() == successor.getNodeId())
									//but nodes with different names can end up with the same nodeID. 
									//Find out why and use the appropriate if
									successorLayer = layer;
									break;
								}
							}
							if(successorLayer != null)
								break;	
						}
					determineDependencyType(layeredPartition.getLayerList().indexOf(layerFrom), 
                                            layeredPartition.getLayerList().indexOf(successorLayer),
                                            layeredPartition);
					
					//compute the corresponding metric
					switch (dependencyType) {
					case AdjacencyCallsMetric: adjac +=  nodeFrom.getSuccessorsList().get(successor) ;
						break;
					case IntraCallsMetric: intra +=  nodeFrom.getSuccessorsList().get(successor) ;
					break;
					case SkipCallsMetric: skip +=  nodeFrom.getSuccessorsList().get(successor) ;
					break;
					case BackCallsMetric: back +=  nodeFrom.getSuccessorsList().get(successor) ;
					break;

					default:
						break;
					}
				//System.out.println(" Evaluation de métriques: alpha="+alpha+ " * " 
				// + graphNode.getSuccessorsList().get(successor) );
				}
			 }
			}
			
			LayersMetrics metrics = new LayersMetrics(adjac, intra, skip, back);
			layeredPartition.setPartitionMetrics(metrics);
			//print the 4 layers metrics
			
		}
		
		/**
		 * Compute the ratio/perecentage of desired dependencies (adjacent dependencies) 
		 * over the existing dependencies between nodes.
		 * @param partition
		 * @return
		 */
		public double ratioOfDesiredDependencies(LayeredPartition partition){
			double ratio = (100 * partition.getPartitionMetrics().getNumberOfAdjacencyCalls())/
					       (partition.getPartitionMetrics().getNumberOfAdjacencyCalls() + 
					        partition.getPartitionMetrics().getNumberOfIntraCalls() + 
					        partition.getPartitionMetrics().getNumberOfSkipCalls() + 
					        partition.getPartitionMetrics().getNumberOfBackCalls());
			return ratio;
		}
		
		
		
		
	}
