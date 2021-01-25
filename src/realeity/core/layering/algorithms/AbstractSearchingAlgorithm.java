package realeity.core.layering.algorithms;


import java.util.List;

import realeity.core.algorithms.cost.ICostStrategy;
import realeity.core.algorithms.cost.LCCostStrategy;
import realeity.core.layering.algorithms.settings.Setup;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.LayerP;
import realeity.core.mdg.elements.LayeredPartition;


/**
 * An implementation of the steepest ascent hill climbing (SAHC).
 * Is used to generate the layers from a given partition
 * @author Boaye Belle Alvine
 * 2013  11:49:29
 */
public abstract class AbstractSearchingAlgorithm implements IRecoveryAlgorithm {
	
	protected ICostStrategy costStrategy = new LCCostStrategy();
	protected static final int NUMBER_OF_DESIRED_LAYERS = 3;

	
	protected final double MAX_VALUE = 1000000;
	protected double bestacceptedLQ = -1;
	protected  int nbIterations = 1;
	
	protected static int MAX_IMPROVEMENT_TRIALS = 50;
	protected LayeredPartition subsystemResultingFromSplit;
	protected LayeredPartition localBestSubsystem = null;
	protected static int nbIGlobalterations = 1;
	
	
	public LayeredPartition recoverLayers(LayeredPartition layeredPartition, 
			                              Setup algorithmParameters) {
		long algorithmStart = System.currentTimeMillis();

		LayeredPartition layeredSolution = applyAlgorithm(layeredPartition, 
				                                          algorithmParameters);
		//compute the duration of the algorithm
		long algorithmEnd = System.currentTimeMillis();
		long algorithmDuration = algorithmEnd - algorithmStart;
		System.out.println("\n***** The " + this.getClass().getName() + " duration = " + algorithmDuration + " ms");

		
        //display the content of the solution's layers
     	System.out.println("Layers of the solution obtained when layering :");
		layeredSolution.printResultWithoutSuccessors();
       
        return layeredSolution;
	}
	
	/**
	 * Allow applying the layering algorithm chosen by the user.
	 * @param layeredPartition
	 * @param algorithmParameters
	 * @return
	 */
	protected abstract LayeredPartition applyAlgorithm(LayeredPartition layeredPartition, 
                                                       Setup algorithmParameters);
	
	/**
	 * Check whether the input partition has the desired number of layers.
	 * @param layeredPartition
	 * @return
	 */
	protected boolean rightLayersNumber(LayeredPartition layeredPartition){
		return layeredPartition.hasTheDesiredNumberOfLayers();
	}
		
	
	
	/**
	 * Compute the best neighbor of a layered system. 
	 * For this purpose, it computes all the neighbors of the current layered 
	 * system and evaluate them using the fitness function LQ (layering quality).
	 * @param LayeredPartition
	 * @return
	 */
	public LayeredPartition bestOfAllLayeredNeighborsCost(LayeredPartition layeredPartition,
			                                              Setup algorithmParameters){
		LayeredPartition bestNeighbor = null;
		bestacceptedLQ = MAX_VALUE;
		for(int indexFrom = 0; indexFrom < layeredPartition.getLayerList().size() -1; indexFrom++){
			LayerP layerFrom = layeredPartition.getLayerList().get(indexFrom);
			for(int indexTo = (indexFrom + 1); indexTo < layeredPartition.getLayerList().size(); indexTo++){
				LayerP layerTo = layeredPartition.getLayerList().get(indexTo);
				for(int indexToMove = 0; indexToMove < layerFrom.getLayerContent().size(); indexToMove++){
					bestNeighbor = acceptANeighbor(layeredPartition, layerFrom, indexFrom, 
							                       indexTo, indexToMove, bestNeighbor,
							                       algorithmParameters);
				
				}//end for(int indexToMove = 0; 
				
				for(int indexToMove = 0; indexToMove < layerTo.getLayerContent().size(); indexToMove++){
					bestNeighbor = acceptANeighbor(layeredPartition, layerTo, indexTo, 
							                       indexFrom, indexToMove, bestNeighbor,
							                       algorithmParameters);
				}//end for(int indexToMove = 0; 
			}	
		}
		return bestNeighbor;
	}//end method
	
	
	/**
	 * 
	 * @param bestNeighbor
	 * @param localImprovementTrials
	 * @return
	 */
	protected LayeredPartition findBetterLocalNeighbor(LayeredPartition currentSolution,
			                                                  LayeredPartition bestNeighbor, 
			                                                  Object bestSubsystemSplit, 
			                                                  int localImprovementTrials,
			                                                  Setup algorithmParameters){
	//TODO in case of solutions with the same LQ, we are stuck
		LayeredPartition localImprovedSolution = bestNeighbor;
		for(int indexImprovement = 0; indexImprovement < localImprovementTrials; indexImprovement++){
			LayeredPartition bestLocalNeighbor = bestOfAllLayeredNeighborsCost(localImprovedSolution, 
					                                                           algorithmParameters);
			/*if (bestLocalNeighbor != null)
				System.out.println((1 + indexImprovement) + " : bestLocalNeighbor Cost: " + bestLocalNeighbor.getPartitionCost());*/
			if((bestLocalNeighbor != null) && (bestLocalNeighbor.getPartitionCost() < currentSolution.getPartitionCost())){
				return bestLocalNeighbor;
			}
			else {
				localImprovedSolution = bestLocalNeighbor;
			}
		}
		
		return localImprovedSolution;
	}
	
	
	/**
	 * 
	 * @param neighbor
	 * @param bestNeighbor
	 * @param best_LQ
	 */
	protected LayeredPartition acceptANeighbor(LayeredPartition layeredPartition, 
			                                   LayerP currentLayer, int indexFrom, 
			                                   int indexTo, int indexToMove, 
			                                   LayeredPartition bestNeighbor,
			                                   Setup algorithmParameters){
		AbstractGraphNode nodeToMove = currentLayer.getLayerContent().get(indexToMove);
		//build a neighbor partition by moving a packageToMove from layerFrom to layerTo
		LayeredPartition neighbor = computeALayeringNeighbor(indexFrom, indexTo, 
				                                             indexToMove, nodeToMove, 
				                                             layeredPartition);
		
		double neighbor_LQ = neighbor.computeCost(costStrategy, 
				                                  algorithmParameters);
		if(bestacceptedLQ > neighbor_LQ 
		   && rightLayersNumber(neighbor)){
			//accept a better neighbor of the layered system
			bestNeighbor = neighbor;
			bestacceptedLQ = neighbor_LQ;
			//System.out.println("coût du voisin retenu : " + best_LQ);
		}
		return bestNeighbor;
	}
	
	/**
	 * Compute a neighbor by moving a package in a different layer.
	 * @param indexFrom
	 * @param indexTo
	 * @param indexPackageToMove
	 * @param nodeToMove
	 * @param LayeredPartition
	 * @return
	 */
	public LayeredPartition computeALayeringNeighbor(int indexFrom, int indexTo, 
			                                         int indexAggregateToMove, 
			                                         AbstractGraphNode nodeToMove, 
			                                         LayeredPartition LayeredPartition){
		//make a copy of all the layers of the system
		LayeredPartition neighbor = (LayeredPartition) LayeredPartition.clone();
		LayerP LayerFromNeighbor = neighbor.getLayerList().get(indexFrom);
		LayerP LayerToNeighbor =  neighbor.getLayerList().get(indexTo);
		
		//remove packageFrom from aboveLayer by replacing aboveLayer by its copy 
		//System.out.println("IUMalgo: remove layer at position = "+removalPosFrom);
		LayerFromNeighbor.getLayerContent().remove(indexAggregateToMove);
		LayerToNeighbor.getLayerContent().add(nodeToMove);
		
		//remove packageFrom from underUnderLayer by replacing underUnderLayer by its copy
		neighbor.getLayerList().remove(indexFrom);
		neighbor.getLayerList().add(indexFrom, LayerFromNeighbor);
		neighbor.getLayerList().remove(indexTo);
		neighbor.getLayerList().add(indexTo, LayerToNeighbor);
		
		return neighbor;
	}
	
	
	/**
	 * a layer is adjacent to another one if all the layers between are empty
	 * @param currentLayent
	 * @param destinationLayer
	 * @return
	 */
	/*public double isAnIntermediairyDestination(int startIndex, int endIndex, 
			                                LayeredPartition layeredPartition){
		double alpha = 0;//default value corresponding to ???
		boolean noIntermediateLayer = true;
		if(startIndex == endIndex){
			alpha = INSIDE;//if startindex == endindex
		}
		else{
			if(startIndex < endIndex){
				for(int index = startIndex + 1; index < endIndex; index++){
					LayerP layer = layeredPartition.getLayerList().get(index);
					if(layer.getLayerContent().size() > 0){
						noIntermediateLayer = false;
					}
				 }
				if((noIntermediateLayer == false) && (startIndex < (endIndex + 1)))
					alpha = SKIP;
				else if(noIntermediateLayer)
					alpha = ADJACENCE;
			}
			else if(startIndex > endIndex){
				alpha = BACK;
			}
			
		}
		
		return alpha;     
	}*/
	
	@Override
	public List<AbstractGraphNode> clusteringByName(
			List<AbstractGraphNode> nodeList) {
		// TODO Auto-generated method stub
		return null;
	}

}//end of class
