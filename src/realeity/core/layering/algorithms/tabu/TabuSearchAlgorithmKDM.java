package realeity.core.layering.algorithms.tabu;

import java.util.ArrayList;
import java.util.List;

import realeity.core.layering.algorithms.AbstractSearchingAlgorithm;
import realeity.core.layering.algorithms.settings.Setup;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.LayerP;
import realeity.core.mdg.elements.LayeredPartition;




/**
 * @author Boaye Belle Alvine
 * 2014-11-13
 */
public class TabuSearchAlgorithmKDM extends AbstractSearchingAlgorithm {
	
	protected int maxIterations;
	protected int tabuSize;
	private int insertionPosition = 0;
	
	
	public TabuSearchAlgorithmKDM(int maxIterations, int tabuSize) {
		this.maxIterations = maxIterations;
		this.tabuSize = tabuSize;
	}

	/* (non-Javadoc)
	 * @see realeity.core.layering.IRecoveryAlgorithm#recoverLayers(realeity.core.mdg.elements.SystemOfLayerWithoutKDM, realeity.core.layering.settings.Setup)
	 */
	@Override
	public LayeredPartition applyAlgorithm(LayeredPartition initialSystemOfLayerWithoutKDM,
			                               Setup algorithmParameters) { 
		
		//setParameters(algorithmParameters);
		//System.out.println("Initial partition: "+initialSystemOfLayerWithoutKDM.toString());//To delete
		LayeredPartition currentSolution = initialSystemOfLayerWithoutKDM;
		LayeredPartition bestSolution = currentSolution;
		LayeredPartition tabuList[] = new LayeredPartition[tabuSize];//it should not be initialized to null: correct the algo
		List<LayeredPartition> candidates = new ArrayList<LayeredPartition>();//it should not be initialized to null: correct the algo
		int K_iteration = 0;
		while(K_iteration < maxIterations){
			//System.out.println("////// Iteration " + (K_iteration + 1) + " of the tabu search \n");
			candidates.clear();
			List<LayeredPartition> neighborList = computeNeighbors(currentSolution, 
					                                               algorithmParameters);
			for(LayeredPartition neighborSolution : neighborList){
				if(!containsNeighborSolution(tabuList, neighborSolution)){
					candidates.add(neighborSolution);
				}
			}
			currentSolution = locateBestSolution(candidates, algorithmParameters);
			if(costStrategy.computeLC(currentSolution, algorithmParameters) < 
		       costStrategy.computeLC(bestSolution, algorithmParameters)){
				updateTabuList(tabuList, currentSolution);
				bestSolution = currentSolution;
			}
			K_iteration = K_iteration + 1; 
		}
		
		System.out.println("*********************** End of tabu search : best LC = " 
		                   + bestSolution.getPartitionCost());
		System.out.println("Tabu: maxIterations =" +maxIterations + " ; tabu size = " +tabuSize);

		
		return bestSolution;
	}
	
	/**
	 * Checks whether the neighbor solution is contained in the tabu list.
	 * The neighbor solution is contained in the tabu list if a given solution
	 * contained in the tabu list contains the same elements than the neighbor
	 * solution.
	 * @param tabuList
	 * @param neighborSolution
	 * @return
	 */
	protected boolean containsNeighborSolution(LayeredPartition tabuList [], 
			                                 LayeredPartition neighborSolution){
		for(LayeredPartition tabuSolution : tabuList){
			if(tabuSolution != null){
				boolean identicalSolutions = tabuSolution.isIdenticalTo(neighborSolution);
				if(identicalSolutions){
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	/**
	 * Locate the best solution among the candidate solutions
	 * @param LayeredPartition
	 * @param algorithmParameters
	 * @return
	 */
	private LayeredPartition locateBestSolution(List<LayeredPartition>  candidateSolutions,
                                                Setup algorithmParameters){
		LayeredPartition bestCandidate = null;
		int candidateIndex = 0;
		double bestCandidateLC = MAX_VALUE;
		while(candidateIndex < candidateSolutions.size()){
			LayeredPartition candidate = candidateSolutions.get(candidateIndex);
			double candidateLC = costStrategy.computeLC(candidate, algorithmParameters);
			if(candidateLC < bestCandidateLC){
				bestCandidate = candidate;
				bestCandidateLC = candidateLC;
			}
			candidateIndex++;
		}
		return bestCandidate;
	}
	
	/**
	 * Update the tabuList by adding the current solution at the end of the tabu list,
	 * knowing that the tabu list's size is limited to TABU_SIZE.
	 * @param tabuList
	 * @param currentSolution
	 * @return
	 */
	protected void updateTabuList(LayeredPartition tabuList [],
                                LayeredPartition currentSolution){
		
		tabuList[insertionPosition] = currentSolution;	
		if(insertionPosition == (tabuSize - 1))//the tabu list is full
			insertionPosition = 0;
		else
			insertionPosition++;
	}
	/**
	 * Compute all the neighbors of a layered partition.
	 * @param currentSolution
	 * @return
	 */
	/**
	 * Compute all the neighbors of a layered partition.
	 * @param currentSolution
	 * @return
	 */
	private List<LayeredPartition> computeNeighbors(LayeredPartition layeredPartition,
			                                        Setup algorithmParameters){
		List<LayeredPartition> currentSolutionNeighbors = new ArrayList<LayeredPartition>();
		for(int indexFrom = 0; indexFrom < layeredPartition.getLayerList().size() -1; indexFrom++){
			LayerP layerFrom = layeredPartition.getLayerList().get(indexFrom);
			for(int indexTo = (indexFrom + 1); indexTo < layeredPartition.getLayerList().size(); indexTo++){
				LayerP layerTo = layeredPartition.getLayerList().get(indexTo);
				for(int indexToMove = 0; indexToMove < layerFrom.getLayerContent().size(); indexToMove++){
					LayeredPartition neighborSolution = computeANeighborSolution(layeredPartition,
                                                                                layerFrom, indexFrom, 
                                                                                indexTo, indexToMove);
					if(neighborSolution != null)
						currentSolutionNeighbors.add(neighborSolution);
				
				}//end for(int indexToMove = 0; 
				
				for(int indexToMove = 0; indexToMove < layerTo.getLayerContent().size(); indexToMove++){
					LayeredPartition neighborSolution = computeANeighborSolution(layeredPartition,
							                                                    layerTo,
							                                                    indexTo, indexFrom, 
                                                                                indexToMove);
					if(neighborSolution != null)
						currentSolutionNeighbors.add(neighborSolution);
				}//end for(int indexToMove = 0; 
			}	
		}
		return currentSolutionNeighbors;
	}
	

	/**
	 * Computes a neighbor of a layered solution and returns it if it has the right number of
	 * layers.
	 * @param layeredPartition
	 * @param layerFrom
	 * @param indexFrom
	 * @param indexTo
	 * @param indexToMove
	 * @return
	 */
	protected LayeredPartition computeANeighborSolution(LayeredPartition layeredPartition,
			                                           LayerP layerFrom, int indexFrom, 
			                                           int indexTo, int indexToMove){
		AbstractGraphNode nodeToMove = layerFrom.getLayerContent().get(indexToMove);
		LayeredPartition neighborSolution = computeALayeringNeighbor(indexFrom, indexTo, 
                                                                     indexToMove, nodeToMove, 
                                                                     layeredPartition);
		if(rightLayersNumber(neighborSolution))
			return neighborSolution;
		else
			return null;
	}

	@Override
	protected boolean rightLayersNumber(LayeredPartition neighborSolution) {
		// TODO Auto-generated method stub
		return neighborSolution.hasTheDesiredNumberOfLayers();
	}


	

}
