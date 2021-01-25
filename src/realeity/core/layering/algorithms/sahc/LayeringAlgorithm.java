package realeity.core.layering.algorithms.sahc;

import java.util.List;


import realeity.core.layering.algorithms.AbstractSearchingAlgorithm;
import realeity.core.layering.algorithms.settings.Setup;
import realeity.core.mdg.elements.LayeredPartition;


/**
 * 
 * @author Alvine Boaye Belle
 * 2013  11:50:05
 */
public class LayeringAlgorithm extends AbstractSearchingAlgorithm { 
	protected static int MAX_IMPROVEMENT_TRIALS = 50;
	protected LayeredPartition subsystemResultingFromSplit;
	protected LayeredPartition localBestSubsystem = null;
	protected int nbIGlobalterations = 1;

	
	public LayeredPartition applyAlgorithm(LayeredPartition layeredPartition, 
                                           Setup algorithmParameters) { 
		
		double current_LC = layeredPartition.computeCost(costStrategy, 
                                                         algorithmParameters); // initial value of the fitness function
		System.out.println("LQ INITIAL = " + current_LC);
		double bestNeighbor_LQ = MAX_VALUE;

		while(true) {
			//find the neighbors's partitions of the current partition
			LayeredPartition bestNeighbor = bestOfAllLayeredNeighborsCost(layeredPartition,
                                     algorithmParameters);

			//If no best neighbor is found, execution is done
			if (bestNeighbor != null) {
				bestNeighbor_LQ = bestNeighbor.computeCost(costStrategy, 
														algorithmParameters);

				if(bestNeighbor_LQ < current_LC){
					layeredPartition = bestNeighbor;// assignment of the best neighbor's partition
					current_LC = bestNeighbor_LQ;
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
			/*System.out.println("\n" + "!!!!!!!!!!!!!!!!!!!!!!!!!! End of the iteration " + nbIterations + 
								"-------------------- maximal value of LC = " + current_LC);*/
		}// end of while

		System.out.println("\n" + "!!!!!!!!!!!!!!!!!!!!!!!!!! End of the algorithm HillClimbingByCost " +
					       ": iteration " + nbIterations + " -------------------- maximal value of LC : " +
			               current_LC);

		return layeredPartition;
	}
		
		
	

	
}
