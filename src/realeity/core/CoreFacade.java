package realeity.core;

import java.util.ArrayList;
import java.util.List;










import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import realeity.application.data.GeneratedData;
import realeity.core.layering.algorithms.IRecoveryAlgorithm;
import realeity.core.layering.algorithms.sahc.LayeringAlgorithm;
import realeity.core.layering.algorithms.sahc.RecursiveRelayeringAlgo;
import realeity.core.layering.algorithms.sahc.SAHCRelayeringAlgo;
import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.core.layering.algorithms.tabu.TabuSearchAlgorithmKDM;
import realeity.core.layering.algorithms.tabu.TabuSearchForRelayering;
import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.Graph;
import realeity.core.mdg.elements.LayeredPartition;
import realeity.technique.extractor.ExtractionFacade;
import realeity.technique.extractor.ModuleDependencyBuilder;
import realeity.technique.extractor.codemodel.entities.PackageExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.util.RealeityUtils;



/**
 * Facade allowing to access the interfaces of the classes related to the layering package.
 * The following class is designed using the Singleton and the Facade patterns.
 * @author Alvine Boaye Belle
 * 2013  23:36:21
 */

public class CoreFacade {
	
	/**
	 * Constructor of the class LayeringFacade. 
	 * Its visibility is private in order not to allow the other classes to 
	 * create many instances of LayeringFacade.
	 */
	private CoreFacade(){
		
	}
	
	/**
	 * unique instance of LayeringFacade.
	 */
	private static CoreFacade uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of LayeringFacade is created.
	 * @return uniqueInstance
	 */
	public static CoreFacade getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new CoreFacade();
		}
		return uniqueInstance;
	}
	
	/**
	 * Build an MDG where nodes are KDM entities at class level.
	 * @param kdmFilePath
	 * @return
	 */
	public AbstractModuleDependencyGraph buildMDGFromEntities(String kdmFilePath){
		List<KDMEntity> relevantModules = ExtractionFacade.getInstance().retrieveModules(kdmFilePath);
		List<KDMEntity> relevantEntities = ExtractionFacade.getInstance().retrieveEntities(kdmFilePath,
				                                                                           relevantModules);
		List<AbstractExtractedFact> extractedFacts = ExtractionFacade.getInstance().retrieveFactsBetweenEntities(kdmFilePath, 
				                                                                                                 relevantModules, 
				                                                                                                 relevantEntities);
		AbstractModuleDependencyGraph graph = new Graph();
		graph.buildDependencyGraph(relevantEntities, extractedFacts);
		//graph.computeOmnipresentNodes();
		return graph;
	}
	
	/**
	 * Build an MDG where nodes are KDM entities at module level.
	 * @param kdmFilePath
	 * @return
	 */
	public AbstractModuleDependencyGraph buildMDGFromModules(String kdmFilePath){
		List<KDMEntity> relevantModules = ExtractionFacade.getInstance().retrieveModules(kdmFilePath);
		List<KDMEntity> relevantEntities = ExtractionFacade.getInstance().retrieveEntities(kdmFilePath,
				                                                                           relevantModules);
		List<AbstractExtractedFact> extractedFacts = ExtractionFacade.getInstance().retrieveFactsBetweenModules(kdmFilePath, 
				                                                                                                relevantModules,
				                                                                                                relevantEntities);
		relevantModules = PackageExtractor.getInstance().removeDisconnectedPackages(extractedFacts, 
				                                                                    relevantModules);
		AbstractModuleDependencyGraph graph = new Graph();
		graph.buildDependencyGraph(relevantModules, extractedFacts);
		return graph;
	}
	
	
	/**
	 * 
	 * @param kdmFilePath
	 * @param model
	 * @param granularity
	 * @param layeringAlgo
	 * @return
	 */
	public LayeredPartition generateALayeredArchitecture(String kdmFilePath, 
			                                             AbstractModuleDependencyGraph structureModelGraph,
                                                         RealeityUtils.GranularityNames granularity,
                                                         IRecoveryAlgorithm layeringAlgo){
	   LayeredPartition firstLayering = new LayeredPartition(AlgorithmContext.nbDefaultLayers);
  	   AbstractModuleDependencyGraph simpleMDG = null;
  	   if(structureModelGraph == null){
  		   if(granularity.equals(RealeityUtils.GranularityNames.Class))
    		   simpleMDG = buildMDGFromEntities(kdmFilePath);
    	   else if(granularity.equals(RealeityUtils.GranularityNames.Module))
    		   simpleMDG = buildMDGFromModules(kdmFilePath);
  	   }
  	   else
  		 simpleMDG = structureModelGraph;
  	    // System.out.println(simpleMDG.toString());
  	   firstLayering.setModuleDependencyGraph(simpleMDG);
  	   firstLayering.randomInitialLayering();
  	   firstLayering = firstLayering.callLayeringAlgo(layeringAlgo, 
                                                      AlgorithmContext.algorithmParameters);
  	   return firstLayering;
	}
 
    /**
     * Allows to recursive aggregated layered system from the facts retrieved in a KDM representation.
     * The latter is extracted from the system under analysis
     * @param recursiveLayeringAlgorithm
     * @param kdmFilePath
     * @return
     */
       public LayeredPartition generateARecursiveRelayeredArchitecture(String kdmFilePath,
    		                                                           AbstractModuleDependencyGraph structureModelGraph,
    		                                                           RealeityUtils.GranularityNames granularity,
    		                                                           IRecoveryAlgorithm layeringAlgo, 
    		                                                           IRecoveryAlgorithm aBestRelayeringAlgo){
    	   LayeredPartition firstLayering = generateALayeredArchitecture(kdmFilePath, 
    			                                                         structureModelGraph, 
    			                                                         granularity, 
    			                                                         layeringAlgo);
    	   
    	   LayeredPartition secondLayering = firstLayering.callLayeringAlgo(aBestRelayeringAlgo, 
                                                                            AlgorithmContext.algorithmParameters);
           return secondLayering;
   		 
   	    }
      
 
       /**
        * 
        * @return
        */
       public LayeredPartition runRandomRelayeringNBTimes(String kdmFilePath, AbstractModuleDependencyGraph structureModelGraph,
                                                          RealeityUtils.GranularityNames granularity, IRecoveryAlgorithm layeringAlgo){
    	   LayeredPartition bestRelayering = null;
    	   for(int indexRun = 0; indexRun < AlgorithmContext.randomIterations; indexRun ++){
    		   LayeredPartition firstLayering = generateALayeredArchitecture(kdmFilePath, structureModelGraph, 
                                                                             granularity, layeringAlgo);
    		   IRecoveryAlgorithm recursiveAlgo = null;
    		   if(layeringAlgo instanceof TabuSearchAlgorithmKDM){
    			   recursiveAlgo = new RecursiveRelayeringAlgo(new TabuSearchForRelayering(AlgorithmContext.tabuMaxIterations, 
    					   AlgorithmContext.tabuListSize));
    		   }
    		   else  if(layeringAlgo instanceof LayeringAlgorithm){
    			   recursiveAlgo = new RecursiveRelayeringAlgo(new SAHCRelayeringAlgo());
    		   }
    		   LayeredPartition secondRelayering = firstLayering.callLayeringAlgo(recursiveAlgo, 
                                                                                  AlgorithmContext.algorithmParameters);
    		   
    		   System.out.println("END OF THE RANDOM EXECUTION " + (1 + indexRun)+ "\n \n");
               if((bestRelayering == null) || (bestRelayering.getPartitionCost() > secondRelayering.getPartitionCost())){
            	   bestRelayering =    secondRelayering;
               }
               
    	   }
    	   
    	   return bestRelayering;
       }
}
