package realeity.core.layering.algorithms.sahc;

import realeity.core.layering.algorithms.IRecoveryAlgorithm;
import realeity.core.layering.algorithms.settings.Setup;
import realeity.core.mdg.elements.LayerP;
import realeity.core.mdg.elements.LayeredPartition;



/**
 * Layering algorithm that iteratively divide each layer of the partition.
 * It stops when the cost of the partition can no longer be improved.
 * @author Alvine Boaye Belle
 * 2013  11:52:26
 */
public class RecursiveRelayeringAlgo extends SAHCRelayeringAlgo {

	/**
	 * TO COMPLETE
	 */
	private static int MAX_LAYERS_FOR_SPLIT = 3 ;
	private static int MIN_LAYERS_FOR_SPLIT = 2 ;
	//private IRecoveryAlgorithm relayeringAlgorithm = new RelayeringAlgo();
	private IRecoveryAlgorithm relayeringAlgorithm;
	
	/**
	 * 
	 * @param reLayeringAlgorithm
	 */
	public RecursiveRelayeringAlgo(IRecoveryAlgorithm reLayeringAlgorithm) {
		this.relayeringAlgorithm = reLayeringAlgorithm;
	}
	
	@Override
	 public LayeredPartition recoverLayers(LayeredPartition initialSolution, 
			                               Setup algorithmParameters) {
	
		 LayeredPartition currentSolution = initialSolution;
		 double currentLC = currentSolution.computeCost(costStrategy, 
				                                        algorithmParameters);
		 LayeredPartition layeredSolution = null;
		 int indexLayer = currentSolution.getLayerList().size() - 1;//the lowermost layer
		 do {
			LayerP layerToSplit = currentSolution.getLayerList().get(indexLayer);
			
			if(layerToSplit.getLayerContent().size() > 1){
				LayeredPartition bestSplittedNeighbor = null;
				double bestLC = super.MAX_VALUE;
				
				 for(int nbLayersForSplit = MIN_LAYERS_FOR_SPLIT; nbLayersForSplit <= MAX_LAYERS_FOR_SPLIT; nbLayersForSplit++){
					 currentSolution.setLayerToBeSplitted(layerToSplit);
				     currentSolution.setNbLayersForSplit(nbLayersForSplit);
				     layeredSolution = currentSolution.callLayeringAlgo(relayeringAlgorithm,
				    		                                            algorithmParameters);
				     double layeredSolutionLC = layeredSolution.computeCost(costStrategy,
				    		                                                algorithmParameters);
					 if(bestLC> layeredSolutionLC){
						 bestSplittedNeighbor = layeredSolution;
						 bestLC = layeredSolutionLC;
					 }
				 }
				 
				 if(currentLC > bestLC){
					 currentSolution = bestSplittedNeighbor; 
					 currentLC = bestLC;
				 }
			}//end if
			
			indexLayer--;
			
		 }while(indexLayer >= 0);
		 
		 System.out.println("++++++++++++ End of the search of a Best Relayering partition. " +
		 		            "Best cost: = " + currentSolution.computeCost(costStrategy, algorithmParameters));
		 
		 //display the result of the layers
		currentSolution.printResultWithoutSuccessors();
		
		 
		 return currentSolution;
		 
	 }
		
}
