package realeity.technique.extractor;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.Graph;
import realeity.technique.extractor.codemodel.entities.PackageExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.structuremodel.StructureModelExtractor;


/**
 * Facade allowing to access the methods of the classes related to the extraction package.
 * It extract the data from the KDM representation of the system under analysis. 
 * Then, it builds a module dependency using them.
 * The following class is designed using the Singleton and the Facade patterns.
 * @author Alvine Boaye Belle
 * 2013  17:14:03
 */

public class ExtractionFacade {
	
	
	/**
	 * Constructor of the class Extractionfacade. 
	 * Its visibility is private in order not to allow the other classes to create 
	 * many instances of Extractionfacade.
	 */
	private ExtractionFacade(){
		
	}
	
	/**
	 * unique instance of Extractionfacade.
	 */
	private static ExtractionFacade uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of Extractionfacade is created.
	 * @return uniqueInstance
	 */
	public static ExtractionFacade getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new ExtractionFacade();
		}
		return uniqueInstance;
	}
	
	/**
	 * @param kdmFilePath
	 * @return
	 */
	public  List<KDMEntity> retrieveModules(String kdmFilePath){
		Segment mainSegment = SegmentExtractor.getInstance().getSegment(kdmFilePath);
		List<KDMEntity> extractedModules = ModuleDependencyBuilder.getInstance().retrieveRelevantModules(mainSegment);
		
		return extractedModules;
	}
	
	/**
	 * @param kdmFilePath
	 * @return
	 */
	public  List<KDMEntity> retrieveEntities(String kdmFilePath,
			                                 List<KDMEntity> extractedModules){
		Segment mainSegment = SegmentExtractor.getInstance().getSegment(kdmFilePath);
		List<KDMEntity> relevantEntities = DataDependencyBuilder.getInstance().retrieveRelevantEntities(mainSegment, 
                                                                                                        extractedModules);	
		
		return relevantEntities;
	}
	
	
	/**
	 * Extract the facts between the entities (e.g. classes, interfaces, enumerated types). 
	 * @param projectPath
	 * @return
	 */
	public  List<AbstractExtractedFact> retrieveFactsBetweenEntities(String kdmFilePath,
			                                                         List<KDMEntity> extractedModules, 
			                                                         List<KDMEntity> relevantEntities){	
		List<AbstractExtractedFact> extractedFacts = DataDependencyBuilder.getInstance().retrieveFacts(kdmFilePath, 
				                                                                              extractedModules, 
				                                                                              relevantEntities);
		return extractedFacts;
	}
	
	/**
	 * Extract the facts between the modules (e.g. packages). 
	 * @param projectPath
	 * @return
	 */
	public  List<AbstractExtractedFact> retrieveFactsBetweenModules(String kdmFilePath, 
			                                                        List<KDMEntity> extractedModules,
			                                                        List<KDMEntity> relevantEntities){
		return ModuleDependencyBuilder.getInstance().retrieveFacts(kdmFilePath, 
                                                                   extractedModules, 
                                                                   relevantEntities);
	}
	
	/**
	 * 
	 * @param projectPath
	 * @return
	 */
	public  List<AbstractExtractedFact> retrieveFactsBetweenEntities(String projectPath){
		return DataDependencyBuilder.getInstance().retrieveFacts(projectPath);
	}
	
	/**
	 * Extract the facts between the modules (e.g. packages). 
	 * @param projectPath
	 * @return
	 */
	public  List<AbstractExtractedFact> retrieveFactsBetweenModules(String projectPath){
		return ModuleDependencyBuilder.getInstance().retrieveFacts(projectPath);
	}

	/**
	 * Get the list of structure models contained in a KDM segment.
	 * @param maingSegment
	 * @return
	 */
	public static  EList<StructureModel>  retrieveStructureModels(Segment maingSegment){
		return StructureModelExtractor.retrieveStructureModels(maingSegment);
	}
	
	public AbstractModuleDependencyGraph buildGraph(List<KDMEntity> nodeList, 
                                                    List<AbstractExtractedFact> dependencyList){ 
		AbstractModuleDependencyGraph MDG = new Graph();
		MDG.buildDependencyGraph(nodeList, dependencyList);
		//System.out.println(MDG.toString());
		return MDG;
	}

	
	/**
	 * Build a graph from an input segment.
	 * @return
	 */
	public AbstractModuleDependencyGraph buildGraphFromSegment(List<KDMEntity> moduleList, 
			                                                   List<AbstractExtractedFact> dependencyList,
			                                                   Segment mainSegment){
		//TODO factoriser ce code avec celui de buildGraph
		if(moduleList.size() == 0 || dependencyList.size() == 0){
			//the data to build the graph have not yet been retrieved from the analyzed system.
		    moduleList = ModuleDependencyBuilder.getInstance().retrieveRelevantModules(mainSegment);
		    if(AlgorithmContext.withKDM){
		    	 dependencyList = ModuleDependencyBuilder.getInstance().retrieveFactsFromSegment(mainSegment, 
		    			                                                                        moduleList);

		    }
		  
			/*for(AbstractExtractedFact dependency : dependencyList){
				System.out.println((dependencyList.indexOf(dependency) + 1) + dependency.toString());
			}
			System.out.println();*/
		    //Anas made these two lines comments for not removing non connected packages
//			moduleList = PackageExtractor.getInstance().removeDisconnectedPackages(dependencyList, 
//					                                                               moduleList);
		}
		
		/*for(KDMEntity pack : moduleList){
				System.out.println((moduleList.indexOf(pack) + 1) + " : The connected relevant nodes " +
						           "extracted from the system are: " + pack.getName() + "\r\n");
			}*/
			
		AbstractModuleDependencyGraph MDG = buildGraph(moduleList, dependencyList);
		return MDG;
	}
	
}
