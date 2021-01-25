package realeity.core.layering.algorithms.sahc;

import realeity.core.layering.algorithms.AbstractSearchingAlgorithm;
import realeity.core.layering.algorithms.settings.Setup;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.LayerP;
import realeity.core.mdg.elements.LayeredPartition;



/**
 * This algorithm allows to split a layer of a layered partition.
 * @author Alvine Boaye Belle
 * 2013  11:50:46
 */
public class SAHCRelayeringAlgo extends AbstractSearchingAlgorithm {
	private LayeredPartition subsystemFromSplit;
	private LayeredPartition initialSystemOfLayer;
	private LayeredPartition bestSubsystemResultingFromSplitNeighbor;
	private int initialNbLayers;
	private double bestacceptedLQ = -1;

	@Override
	public LayeredPartition applyAlgorithm(LayeredPartition layeredPartition, 
                                           Setup algorithmParameters) {
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!  The relayering has just begun " +
				           "!!!!!!!!!!!!!!!!!!!!!!");
		nbIterations = 1;
		initialNbLayers = layeredPartition.getLayerList().size();
		initialSystemOfLayer = layeredPartition;
		LayerP layerToSplit = layeredPartition.getLayerToSplit();
		System.out.println("\n" + "Couche à diviser par l'algorithme : " 
		                   + layerToSplit.toString()); //à supprimer
			
	    subsystemFromSplit  = layeredPartition.createLayeredSubsystem(layerToSplit);
	    if(subsystemFromSplit != null){
	    	bestSubsystemResultingFromSplitNeighbor = null;
	    	
	    	//create the initial partitionTo be layered  
			layeredPartition = layeredPartition.buildRelayeringPartition(layerToSplit, 
					                                                     subsystemFromSplit);
			System.out.println("Nouvelle partition initiale : " );  
			layeredPartition.printResultWithoutSuccessors();
			 
			// initial value of the fitness function
			double current_LC = layeredPartition.computeCost(costStrategy, 
					                                         algorithmParameters); 
			System.out.println("LQ INITIAL =" + current_LC);
			double bestNeighbor_LQ = MAX_VALUE;
			
			while (true) {
				// find the neighbors's partitions of the current partition
				LayeredPartition bestNeighbor = bestOfAllLayeredNeighborsCost(layeredPartition, 
						                                                      algorithmParameters);
				if(bestNeighbor != null){
					bestNeighbor_LQ = bestNeighbor.computeCost(costStrategy, 
                                                               algorithmParameters);;	          
					// If no best neighbor is found, execution is done
					if (bestNeighbor != null && bestNeighbor_LQ < current_LC){
						layeredPartition = bestNeighbor;// assignment of the best neighbor's partition
						current_LC = bestNeighbor_LQ;
						subsystemFromSplit = bestSubsystemResultingFromSplitNeighbor;
						nbIterations++;	
					}
					else {//try to find a better neighbor
						//System.out.println("Layering: Best neighbor to improve : LC= " + bestNeighbor_LQ);
						LayeredPartition betterLocalNeighbor = findBetterLocalNeighbor(layeredPartition,
								                                                              bestNeighbor, 
								                                                              null,
								                                                              MAX_IMPROVEMENT_TRIALS,
								                                                              algorithmParameters);
						if((betterLocalNeighbor != null) && (betterLocalNeighbor.getPartitionCost() < layeredPartition.getPartitionCost())){
							layeredPartition = betterLocalNeighbor;// assignment of the best neighbor's partition
							current_LC = betterLocalNeighbor.getPartitionCost();
							nbIGlobalterations++;	
						}
					else
						break;	
				}
					
				}
				else 
					break;	
				System.out.println("\n" + "!!!!!!!!!!!!!!!!!!!!!!!!!! End of the iteration " 
				                   + super.nbIterations+"-------------------- maximal value of LC = " 
						           + current_LC+": HillclimbingByCostForRelayering");
			}// end of while
			
			System.out.println("!!!!!!!!!! End of the Relayering algorithm : iteration " 
			                   + nbIterations + " *** Best cost : " + current_LC);
	     }
	    else
	    	System.out.println("The layer can not be splitted because there is no " +
	    			           "internal dependencies linking its elements");

		return layeredPartition;
	}



	@Override
	public LayeredPartition bestOfAllLayeredNeighborsCost(LayeredPartition layeredPartition,
			                                              Setup algorithmParameters){
		LayerP layerToSplit = initialSystemOfLayer.getLayerToSplit();
		LayeredPartition bestNeighbor = null;
		int numeroVoisin = 1;
		bestacceptedLQ = super.MAX_VALUE;//0;
		
		for(int indexFrom = 0; indexFrom < subsystemFromSplit.getLayerList().size() - 1; indexFrom++){
			LayerP layerFrom = subsystemFromSplit.getLayerList().get(indexFrom);
			for(int indexTo = (indexFrom + 1); indexTo < subsystemFromSplit.getLayerList().size(); indexTo++){
				LayerP layerTo = subsystemFromSplit.getLayerList().get(indexTo);
				for(int indexToMove = 0; indexToMove < layerFrom.getLayerContent().size(); indexToMove++){
					 bestNeighbor = acceptOneNeighbor(layeredPartition, layerFrom, 
							                          layerToSplit, indexFrom, 
							                          indexTo, indexToMove, 
							                          bestNeighbor, numeroVoisin,
							                          algorithmParameters);
				
				}//end for(int indexToMove = 0; 
				for(int indexToMove = 0; indexToMove < layerTo.getLayerContent().size(); indexToMove++){
					bestNeighbor = acceptOneNeighbor(layeredPartition, layerTo, 
							                         layerToSplit, indexTo, 
							                         indexFrom, indexToMove, 
							                         bestNeighbor, numeroVoisin,
							                         algorithmParameters);
				}//end for(int indexToMove = 0; 
			}	
		}
		return bestNeighbor;
	}//end method
	
	/**
	 * 
	 * @param layeredPartition
	 * @param currentLayer
	 * @param layerToSplit
	 * @param indexFrom
	 * @param indexTo
	 * @param indexToMove
	 * @param bestNeighbor
	 * @param numeroVoisin
	 * @return
	 */
	protected LayeredPartition acceptOneNeighbor(LayeredPartition layeredPartition, 
                                                 LayerP currentLayer, LayerP layerToSplit, 
                                                 int indexFrom, int indexTo, int indexToMove, 
                                                 LayeredPartition bestNeighbor, 
                                                 int numeroVoisin,
                                                 Setup algorithmParameters){ 
		//TODO refactor this methid since it has too many parameters!!!
		AbstractGraphNode aggregateToMove = currentLayer.getLayerContent().get(indexToMove);
		//build a neighbor partition by moving a packageToMove from layerFrom to layerTo
		LayeredPartition neighborSubsystem = computeALayeringNeighbor(indexFrom, 
				                                                      indexTo, 
				                                                      indexToMove, 
				                                                      aggregateToMove,
				                                                      subsystemFromSplit);
		LayeredPartition fullNeighbor = initialSystemOfLayer.buildRelayeringPartition(layerToSplit, 
				                                                                      neighborSubsystem);
		double neighbor_LC = fullNeighbor.computeCost(costStrategy, 
				                                      algorithmParameters);
		 if(bestacceptedLQ > neighbor_LC && rightLayersNumber(fullNeighbor)){
			 //accept a better neighbor of the layered system if all its layers are not empty
        	 //in order not to have the same input system as the best partition
			/*System.out.println("///////////////// Neighbor's cost: "
        	                   + (numeroVoisin++) + "= " + neighbor_LC + "\n"); */ 
			bestNeighbor = fullNeighbor;
			bestacceptedLQ = neighbor_LC;
			bestSubsystemResultingFromSplitNeighbor = neighborSubsystem;
			
		}
        return bestNeighbor;

    }
	

	/**
	 * Checks whether a system has more layers than the initial system that need to be layered. 
	 * This could help preventing the generation of a neighbor which has the same layers than 
	 * the initial system taken as input by the algorithm that is splits and relayers it.
	 * @param LayeredPartitionWithoutKDM
	 * @return
	 */
	@Override
	protected boolean rightLayersNumber(LayeredPartition layeredPartition){
		int nbNotEmptyLayers = 0;
		for(LayerP layer: layeredPartition.getLayerList()){
			if(layer.getLayerContent().size() > 0){
				nbNotEmptyLayers++;
			}
				
		}
		/*
		 * for(LayerP layer: layeredPartition.getLayerList()){
			if(layer.getLayerContent().size() > 0){
				for(AbstractGraphNode node : layer.getLayerContent()){
					if(node.getNodeList().size() > 0){
						nbNotEmptyLayers++;
						break;
					}
				}
			}
				
		}
		 */
		boolean rightNumber = nbNotEmptyLayers > initialNbLayers;
		return rightNumber;
	}

}
