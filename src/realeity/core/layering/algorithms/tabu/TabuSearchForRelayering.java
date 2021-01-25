package realeity.core.layering.algorithms.tabu;

import java.util.ArrayList;
import java.util.List;

/**
 * 27 november 2015
 * @author alvine belle
 */


import realeity.core.layering.algorithms.AbstractSearchingAlgorithm;
import realeity.core.layering.algorithms.settings.Setup;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.GraphNode;
import realeity.core.mdg.elements.LayerP;
import realeity.core.mdg.elements.LayeredPartition;

public class TabuSearchForRelayering extends TabuSearchAlgorithmKDM {
	
	protected LayeredPartition subsystemResultingFromSplit;
	protected LayeredPartition localBestSubsystem = null;
	protected static int nbIGlobalterations = 1;
	protected static int MAX_IMPROVEMENT_TRIALS = 50;

	
	/**
	 * 
	 * @param maxIterations
	 * @param tabuSize
	 */
	public TabuSearchForRelayering(int maxIterations, int tabuSize) {
		super(maxIterations, tabuSize);
		// TODO Auto-generated constructor stub
	}

	LayeredPartition initialSystemOfLayer;
	LayeredPartition bestSubsystemResultingFromSplitNeighbor;
	int initialNbLayers;
	
	@Override
	public LayeredPartition applyAlgorithm(LayeredPartition currentSolution, Setup algorithmParameters) {
		// TODO Auto-generated method stub
		//setParameters(algorithmParameters);
		nbIGlobalterations = 0;
		initialNbLayers = currentSolution.getLayerList().size(); 
		initialSystemOfLayer = currentSolution;
		LayerP layerToSplit = currentSolution.getLayerToSplit();
	    subsystemResultingFromSplit  = currentSolution.createLayeredSubsystem(layerToSplit);
	    LayeredPartition bestSolution = currentSolution;
	    
	    if(subsystemResultingFromSplit != null){
	    	bestSubsystemResultingFromSplitNeighbor = null;
		   	
			//create the initial partitionTo be layered  
		     bestSolution = currentSolution.buildRelayeringPartition(layerToSplit, 
		    		                                                                  subsystemResultingFromSplit); 
			 //System.out.println("\n"+"couche à diviser: "+layerToSplit.toString());
			 for(LayerP layer : currentSolution.getLayerList()){//à supprimer
		     	   //System.out.println("initialement: couche "+systemOfLayers.getLayerList().indexOf(layer)+" : "+layer.toString());
		        }
			 
			double current_LQ = costStrategy.computeLC(currentSolution, algorithmParameters); //computeLQ(systemOfLayers);// initial value of the fitness function
			//System.out.println("LQ INITIAL =" + current_LQ);
			double bestNeighbor_LQ = MAX_VALUE;
			////////////////////////////////
			
	      // setParameters(algorithmParameters);//				

			
			//System.out.println("Initial partition: "+initialSystemOfLayerP.toString());//To delete
			LayeredPartition tabuList[] = new LayeredPartition[tabuSize];//it should not be initialized to null: correct the algo
			List<LayeredPartition> candidates = new ArrayList<LayeredPartition>();//it should not be initialized to null: correct the algo
			int K_iteration = 0;
			while(K_iteration < maxIterations){
				//System.out.println("////// Iteration " + (K_iteration + 1) + " of the tabu search \n");
				candidates.clear();
				currentSolution = bestOfAllLayeredNeighborsCost(currentSolution, tabuList, algorithmParameters);
				
				if((currentSolution != null) 
					&& (costStrategy.computeLC(currentSolution, algorithmParameters) < costStrategy.computeLC(bestSolution, algorithmParameters))){
					updateTabuList(tabuList, currentSolution);
					bestSolution = currentSolution;
					subsystemResultingFromSplit = bestSubsystemResultingFromSplitNeighbor;
				}
				K_iteration = K_iteration + 1; 
			}
	    }
	    
		
		return bestSolution;
		
	}

	
	public LayeredPartition bestOfAllLayeredNeighborsCost(LayeredPartition systemOfLayers, 
			                                              LayeredPartition tabuList[],
			                                              Setup algorithmParameters){
		LayerP layerToSplit = initialSystemOfLayer.getLayerToSplit();
		LayeredPartition bestNeighbor = null;
		int numeroVoisin = 1;
		double best_LQ = super.MAX_VALUE;//0;
		
		for(int indexFrom = 0; indexFrom < subsystemResultingFromSplit.getLayerList().size() -1; indexFrom++){
			LayerP layerFrom = subsystemResultingFromSplit.getLayerList().get(indexFrom);
			for(int indexTo = (indexFrom + 1); indexTo < subsystemResultingFromSplit.getLayerList().size(); indexTo++){
				LayerP layerTo = subsystemResultingFromSplit.getLayerList().get(indexTo);
				for(int indexToMove = 0; indexToMove < layerFrom.getLayerContent().size(); indexToMove++){
					GraphNode packToMove = (GraphNode) layerFrom.getLayerContent().get(indexToMove);
					//build a neighbor partition by moving a packageToMove from layerFrom to layerTo
					//System.out.println(" bestOfAllLayeredNeighbors: déplacement du package "+packToMove.getPackageName()+" de "+layerFrom+" vers "+layerTo);
					LayeredPartition subsystemResultingFromSplitNeighbor = computeALayeringNeighborCost(indexFrom, indexTo, indexToMove, packToMove, subsystemResultingFromSplit);
					LayeredPartition fullNeighbor = initialSystemOfLayer.buildRelayeringPartition(layerToSplit, 
							                                                                      subsystemResultingFromSplitNeighbor);
					double neighbor_LQ = costStrategy.computeLC(fullNeighbor, algorithmParameters);
					 if(best_LQ > neighbor_LQ && rightLayersNumber(fullNeighbor)){//accept a better neighbor of the layered system if all its layers are not empty
			        	                                                      //in order not to have the same input system as the best partition
						//System.out.println("///////////////////////le Coût du déplacement du voisin "+(numeroVoisin++)+"= "+neighbor_LQ);  System.out.println(); 
						bestNeighbor = fullNeighbor;
						best_LQ = neighbor_LQ;
						if(!containsNeighborSolution(tabuList, bestNeighbor)){
							bestSubsystemResultingFromSplitNeighbor = subsystemResultingFromSplitNeighbor;

						}
						
					}
				
				}//end for(int indexToMove = 0; 
				for(int indexToMove = 0; indexToMove < layerTo.getLayerContent().size(); indexToMove++){
					GraphNode packToMove = (GraphNode) layerTo.getLayerContent().get(indexToMove);
					//build a neighbor partition by moving a packageToMove from layerFrom to layerTo
					//System.out.println(" bestOfAllLayeredNeighbors: déplacement du package "+packToMove.getPackageName()+" de "+layerFrom+" vers "+layerTo);
					LayeredPartition subsystemResultingFromSplitNeighbor = computeALayeringNeighborCost(indexTo, indexFrom, indexToMove, packToMove, subsystemResultingFromSplit);
					LayeredPartition fullNeighbor = initialSystemOfLayer.buildRelayeringPartition(layerToSplit, 
							                                                                      subsystemResultingFromSplitNeighbor);
					double neighbor_LQ = costStrategy.computeLC(fullNeighbor, algorithmParameters);
					if(best_LQ > neighbor_LQ && rightLayersNumber(fullNeighbor) ){//accept a better neighbor of the layered system if all its layers are not empty
                                                                               //in order not to have as neighbor the same input system as the best partition
					//System.out.println("///////////////////////le Coût du déplacement du voisin "+(numeroVoisin++)+"= "+neighbor_LQ);  System.out.println(); 
						bestNeighbor = fullNeighbor;
						best_LQ = neighbor_LQ;
						if(!containsNeighborSolution(tabuList, bestNeighbor)){
							bestSubsystemResultingFromSplitNeighbor = subsystemResultingFromSplitNeighbor;

						}
					}
						
					
				}//end for(int indexToMove = 0; 
			}	
		}
		return bestNeighbor;
	}//end method
	
	
	/**
	 * Compute a neighbor by moving a package in a different layer.
	 */
	public LayeredPartition computeALayeringNeighborCost(int indexFrom, int indexTo, int indexPackageToMove, 
			                                             GraphNode packToMove, LayeredPartition systemOfLayersWithoutKDM){
		//make a copy of all the layers of the system
		LayeredPartition neighbor = (LayeredPartition) systemOfLayersWithoutKDM.clone();
		LayerP LayerFromNeighbor = neighbor.getLayerList().get(indexFrom);
		LayerP LayerToNeighbor =  neighbor.getLayerList().get(indexTo);
		
		//remove packageFrom from aboveLayer by replacing aboveLayer by its copy 
		//System.out.println("IUMalgo: remove layer at position = "+removalPosFrom);
		LayerFromNeighbor.getLayerContent().remove(indexPackageToMove);
		LayerToNeighbor.getLayerContent().add(packToMove);
		
		//remove packageFrom from underUnderLayer by replacing underUnderLayer by its copy
		neighbor.getLayerList().remove(indexFrom);
		neighbor.getLayerList().add(indexFrom, LayerFromNeighbor);
		neighbor.getLayerList().remove(indexTo);
		neighbor.getLayerList().add(indexTo, LayerToNeighbor);
		
		return neighbor;
	}
	
	/**
	 * Checks whether a system has more layers than the initial system that need to be layered. This could help preventing
	 * the generation of a neighbor which has the same layers than the initial system taken as input by the algorithm
	 * that is splits and relayers it.
	 * @param systemOfLayers
	 * @return
	 */
	@Override
	protected boolean rightLayersNumber(LayeredPartition systemOfLayers){
		int nbNotEmptyLayers = 0;
		for(LayerP layer: systemOfLayers.getLayerList()){
			if(layer.getLayerContent().size() > 0)
				nbNotEmptyLayers++;
		}
		return nbNotEmptyLayers > initialNbLayers;
	}
	
	
}

