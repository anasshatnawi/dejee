package realeity.application;


import realeity.application.layout.AbstractDot;
import realeity.core.CoreFacade;
import realeity.core.layering.algorithms.IRecoveryAlgorithm;
import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.elements.LayeredPartition;
import realeity.technique.util.RealeityUtils;


/**
 * License GPL
 * Facade allowing to access the methods of the classes related to the visualization package.
 * The following class is designed using the Singleton and the Facade patterns.
 * @author Boaye Belle Alvine
 * 2013  
 */
public class ApplicationFacade { 
	/**
	* Constructor of the class visualizationFacade. 
	* Its visibility is private in order not to allow the other classes to create 
	* many instances of visualizationFacade
	*/
	private ApplicationFacade(){

	}

	/**
	* unique instance of visualizationFacade.
	*/
	private static ApplicationFacade uniqueInstance;

	/**
	*Method insuring that a unique instance of visualizationFacade is created.
	* @return uniqueInstance
	*/
	public static ApplicationFacade getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new ApplicationFacade();
		}
		return uniqueInstance;
	}

	
	/**
	 * Generate the dot code corresponding to a layered partition
	 * @param kdmFilePath
	 * @param model
	 * @param granularity
	 * @param layeringAlgorithm
	 * @param relayeringAlgorithm
	 * @param dotRepresentation
	 * @param graphFileName
	 * @param zestFileName
	 * @return
	 */
	public LayeredPartition getALayeredDot(String kdmFilePath,
                                           AbstractModuleDependencyGraph graph,
			                               IRecoveryAlgorithm layeringAlgorithm,
			                               RealeityUtils.GranularityNames granularity,
			                               AbstractDot zestRepresentation, AbstractDot dotRepresentation,
			                               String zestFileName, String graphFileName){
		LayeredPartition resultingPartition = CoreFacade.getInstance().runRandomRelayeringNBTimes(kdmFilePath, 
				                                                                                    graph,
				                                                                                    granularity, 
				                                                                                    layeringAlgorithm);
		System.out.println(resultingPartition.getPartitionMetrics().toString());

		dotRepresentation.generateDotCode(resultingPartition, graphFileName, AlgorithmContext.graphsPath);
		zestRepresentation.generateDotCode(resultingPartition, zestFileName, AlgorithmContext.graphsPath);
		//MetricsEvaluation2.getInstance().evaluateMetrics(resultingPartition);
		return resultingPartition;

  }
	
	/**
	 * 
	 * @param kdmFilePath
	 * @param layeringAlgorithm
	 * @param granularity
	 * @param aBestRelayeringAlgo
	 * @param dotRepresentation
	 * @param graphFileName
	 * @return
	 */
	public LayeredPartition getAReLayeredDot(String kdmFilePath, 
			                                 AbstractModuleDependencyGraph structureModelGraph,
			                                 IRecoveryAlgorithm layeringAlgorithm,
			                                 RealeityUtils.GranularityNames granularity,
			                                 IRecoveryAlgorithm aBestRelayeringAlgo,
			                                 AbstractDot zestRepresentation, 
			                                 AbstractDot dotRepresentation,
			                                 String graphFileName, String zestFileName){
		LayeredPartition resultingPartition = CoreFacade.getInstance().generateARecursiveRelayeredArchitecture(kdmFilePath, 
				                              structureModelGraph, granularity, layeringAlgorithm, aBestRelayeringAlgo);
				
		System.out.println(resultingPartition.getPartitionMetrics().toString());

		dotRepresentation.generateDotCode(resultingPartition, graphFileName, AlgorithmContext.graphsPath);
		zestRepresentation.generateDotCode(resultingPartition, zestFileName, AlgorithmContext.graphsPath);
		//MetricsEvaluation2.getInstance().evaluateMetrics(resultingPartition);
       return resultingPartition;

    }

}

    

