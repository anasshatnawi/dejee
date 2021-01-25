package realeity.technique.extractor;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Attribute;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureElement;
import org.eclipse.gmt.modisco.omg.kdm.structure.AbstractStructureRelationship;
import org.eclipse.gmt.modisco.omg.kdm.structure.Component;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import realeity.technique.extractor.codemodel.entities.ClassUnitExtractor;
import realeity.technique.extractor.codemodel.entities.EnumeratedTypeExtractor;
import realeity.technique.extractor.codemodel.entities.InterfaceUnitExtractor;
import realeity.technique.extractor.codemodel.entities.PackageExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;
import realeity.technique.extractor.structuremodel.KDMModelsGeneration;
import realeity.technique.util.RealeityUtils;


/**
 * 
 * @author Alvine
 *
 */
public abstract class AbstractDependencyBuilder {
	protected  static final String RELATION_NAME_TO_STRING_ = "Relation name";
	protected  static final String RELATION_WEIGHT_TO_STRING_ = "Relation weight";
	
	/**
	 * Build the aggregatedRelationships between the entities
	 * and use them to derive the dependencies between the entities.
	 * @param KDMModelToParse
	 * @return
	 */
	abstract protected List<AbstractExtractedFact> extractFactsBetweenEntities(List<KDMEntity> relevantEntities, 
			                                                                   List<KDMEntity> packageList);
	
	
	
	/**
	 * 
	 * @param modelToParse
	 * @return
	 */
	protected  List<KDMEntity> retrieveRelevantEntities(Segment mainSegment,
			                                            List<KDMEntity> extractedModules) {
		// TODO Auto-generated method stub
		
		List<KDMEntity> dataList = new ArrayList<KDMEntity>();
		for(KDMEntity extractedModule : extractedModules){
			if (extractedModule instanceof Package){
				List<ClassUnit> cuList = ClassUnitExtractor.getInstance().extractCU((Package) extractedModule);
				dataList.addAll(cuList);
				List<InterfaceUnit> iuList = InterfaceUnitExtractor.getInstance().extractIU((Package)extractedModule);
				dataList.addAll(iuList);
				List<EnumeratedType> enumList = EnumeratedTypeExtractor.getInstance().extractEnum((Package)extractedModule);
				dataList.addAll(enumList);
			}else if (extractedModule instanceof ClassUnit){
//				List<ClassUnit> cuList = new ArrayList<ClassUnit>();
//				cuList.add((ClassUnit) extractedModule);
//				dataList.addAll(cuList);
				dataList.add(extractedModule);	
			} else if (extractedModule instanceof InterfaceUnit || extractedModule instanceof EnumeratedType ){
				dataList.add(extractedModule);	
			}
		}

//		for(KDMEntity extractedModule : extractedModules){	
//			List<ClassUnit> cuList = ClassUnitExtractor.getInstance().extractCU((Package) extractedModule);
//			dataList.addAll(cuList);
//			List<InterfaceUnit> iuList = InterfaceUnitExtractor.getInstance().extractIU((Package)extractedModule);
//			dataList.addAll(iuList);
//			List<EnumeratedType> enumList = EnumeratedTypeExtractor.getInstance().extractEnum((Package)extractedModule);
//			dataList.addAll(enumList);
//		}	
		return dataList;
	}
	
	/**
	 * Retrieve the elements stored into the  structure model containing the extracted facts.
	 * @param structureModelFact
	 * @return
	 */
	public List<KDMEntity> retrieveEntities(StructureModel structureModelFact){
		List<KDMEntity> packageList = new ArrayList<KDMEntity>();
		EList<AbstractStructureElement> structureElements = structureModelFact.getStructureElement();
		for(AbstractStructureElement component : structureElements){
			Component structComp = (Component) component;
			for(KDMEntity kdmPackage : structComp.getImplementation()){
				packageList.add((Package) kdmPackage);
			}
		}
		return packageList;
	}
	
	/**
	 * 
	 * @param structureModelFact
	 * @return
	 */
	public List<AbstractExtractedFact> retrieveDependencies(StructureModel structureModelFact){
		List<AbstractExtractedFact> dependencies = new ArrayList<AbstractExtractedFact>();
		EList<AbstractStructureElement> structureElements = structureModelFact.getStructureElement();
		List<AbstractStructureRelationship> structureRelations = new ArrayList<AbstractStructureRelationship>();
		for(AbstractStructureElement component : structureElements){
			structureRelations.addAll(component.getStructureRelationship());
		}
		
		for(AbstractStructureRelationship relation : structureRelations){
			Component componentFrom = (Component) relation.getFrom();
			KDMEntity dependencyFrom = componentFrom.getImplementation().get(0);
			Component componentTo = (Component) relation.getTo();
			KDMEntity dependencyTo = componentTo.getImplementation().get(0);
			int dependencyWeight = 0;
			String dependencyName = "";
			List<Attribute> attributeList = relation.getAttribute();
			for(Attribute attrib : attributeList){
				if(attrib.getTag().equals(RELATION_NAME_TO_STRING_))
					dependencyName = attrib.getValue();
				if(attrib.getTag().equals(RELATION_WEIGHT_TO_STRING_))
					dependencyWeight = Integer.parseInt(attrib.getValue());
			}
			AbstractExtractedFact elementaryFact = new ElementaryFact(dependencyFrom, dependencyTo, 
					                                                  dependencyWeight, dependencyName, 
					                                                  "");
			dependencies.add(elementaryFact);
		}
		return dependencies;
	}
	
	
	
	/**
	 * Replace a  dependency (entityA, entityB, 1) appearing x times in extractedFacts 
	 * by a single dependency (entityA, entityB, x) if the extractedFacts are module dependencies
	 * o by a single dependency (entityA, entityB, 1) if the extractedFacts are class level dependencies. 
	 * @param extractedFacts
	 * @return
	 */
	protected abstract List<AbstractExtractedFact> sumWeights(List<AbstractExtractedFact> extractedFacts);
	
	
	/**
	 * Retrieve all the facts i.e dependencies from the analyzed system.
	 * @param systemPath
	 * @return
	 */
	public final List<AbstractExtractedFact> retrieveFacts(String kdmFilePath, 
			                                               List<KDMEntity> extractedModules,
			                                               List<KDMEntity> relevantEntities){
			
		//build the aggregatedRelationships between the entities
		// and use them to derive the dependencies between the entities
		List<AbstractExtractedFact> extractedFacts = extractFactsBetweenEntities(relevantEntities, 
				                                                                 extractedModules);	
	
		extractedFacts = DataDependencyBuilder.getInstance().removeIrrelevantFacts(extractedFacts, 
				                                                                   relevantEntities);//TODO cette methode ne fout rien???	
		extractedModules = PackageExtractor.getInstance().removeDisconnectedPackages(extractedFacts, 
				                                                                     extractedModules);
		
		printStatistics(extractedModules, relevantEntities, extractedFacts);

		
		return extractedFacts;
		
	}
	
	/**
	 * Retrieve all the facts i.e dependencies from the analyzed system.
	 * @param systemPath
	 * @return
	 */
	public final List<AbstractExtractedFact> retrieveFacts(String kdmFilePath){
		
		//retrieve segment
		Segment mainSegment = SegmentExtractor.getInstance().getSegment(kdmFilePath);
		List<AbstractExtractedFact> extractedFacts = retrieveFactsFromSegment(mainSegment, 
				                                                             new ArrayList<KDMEntity>());

		return extractedFacts;
		
	}
	
	/**
	 * 
	 * @param modelToParse
	 * @param moduleList
	 * @return
	 */
	public final List<AbstractExtractedFact> retrieveFactsFromSegment(Segment mainSegment,
			                                                         List<KDMEntity> moduleList){
		List<AbstractExtractedFact> extractedFacts = new ArrayList<AbstractExtractedFact>();

		if((moduleList == null) || (moduleList.size() == 0))
			moduleList = ModuleDependencyBuilder.getInstance().retrieveRelevantModules(mainSegment);
		
		List<KDMEntity> relevantEntities = retrieveRelevantEntities(mainSegment, 
				                                                    moduleList);
			
		//build the aggregatedRelationships between the entities
		// and use them to derive the dependencies between the entities
		extractedFacts = extractFactsBetweenEntities(relevantEntities, 
				                                     moduleList);	
	
		extractedFacts = DataDependencyBuilder.getInstance().removeIrrelevantFacts(extractedFacts, 
				                                                                   relevantEntities);	
		moduleList = PackageExtractor.getInstance().removeDisconnectedPackages(extractedFacts, 
                                                                               moduleList);
		
		printStatistics(moduleList, relevantEntities, extractedFacts);

		
		return extractedFacts;
	}
	
	
	/**
	 * 
	 * @param extractedModules
	 * @param relevantEntities
	 * @param extractedFacts
	 */
   private void printStatistics(List<KDMEntity> extractedModules, 
		                        List<KDMEntity> relevantEntities,
		                        List<AbstractExtractedFact> extractedFacts){
	   for(KDMEntity module : extractedModules){
			/*System.out.println("****** Extracted module : " + (extractedModules.indexOf(module) + 1) 
					           + "  " + module.getName());*/
		}
	   
	   int nbClasses = 0;
	   int nbInterfaces = 0;
	   int nbEnum = 0;
	   for(KDMEntity entity : relevantEntities){
			/*System.out.println("/////////// Extracted entity : " + (relevantEntities.indexOf(entity) + 1) 
					           + "  " + entity.getName());*/
			if(entity instanceof ClassUnit){
				nbClasses++;
			}
			else if(entity instanceof InterfaceUnit){
				nbInterfaces++;
			}
			else if(entity instanceof EnumeratedType){
				nbEnum++;
			}
		}
	   
	/**  for(AbstractExtractedFact fact : extractedFacts){
			System.out.println("*********** Fact between entities/modules : " 
	                           + (extractedFacts.indexOf(fact) + 1) + "  " + fact.toString());
		}**/
	   
	   System.out.println("\r\n" + "***** Statistics: " + extractedModules.size() + " modules: ");
	   System.out.println("\r\n" + "***** Statistics: " + relevantEntities.size() + " entities: ");
	 
	   System.out.println(nbClasses + " classes; " + nbInterfaces 
			              + " interfaces; " + nbEnum + " enum." + "\r\n");
	   System.out.println("***** Number of dependencies between modules/entities : " 
               + extractedFacts.size());
	   
	   
   }
	
	
	/**
	 * Retrieve all the facts i.e dependencies from the analyzed system.
	 * @param systemPath
	 * @return
	 */
	public final List<AbstractExtractedFact> retrieveAndSaveFacts(String projectPath,
			                                               RealeityUtils.ModelNames modelType,
			                                               String modelName,
			                                               String structureModelName){
		List<AbstractExtractedFact> extractedFacts = new ArrayList<AbstractExtractedFact>();
		
		//retrieve segment
		Segment mainSegment = SegmentExtractor.getInstance().getSegment(projectPath);
		List<KDMEntity> extractedModules = ModuleDependencyBuilder.getInstance().retrieveRelevantModules(mainSegment);
		
		StructureModel factStructureModel = (StructureModel)KDMModelConcerns.getInstance().extractModel(mainSegment, 
                                                                                                        modelType, 
                                                                                                        modelName);
		if(factStructureModel == null){	
			//create an empty structure model
			 factStructureModel = KDMModelConcerns.getInstance().createEmptystructureModel(mainSegment, 
					                                                                       modelType, 
					                                                                       modelName);
			
			//retrieve the entities stored in the model
			List<KDMEntity> relevantEntities = retrieveEntities(factStructureModel);
			
			KDMModelConcerns.getInstance().saveRelevantEntities(factStructureModel, relevantEntities);
			
			//build the aggregatedRelationships between the entities
			// and use them to derive the dependencies between the entities
			extractedFacts = extractFactsBetweenEntities(relevantEntities, 
					                                     extractedModules);
			
			//save the dependencies in a structure model
			KDMModelConcerns.getInstance().saveDependenciesInStructureModel(factStructureModel,
					                         extractedFacts);
		}
		else
			extractedFacts = retrieveDependencies(factStructureModel);	
		
		return extractedFacts;
		
	}
	
	/**
	 * If the same dependencies appears many times, it is removed from the list of considered 
	 * dependencies.
	 * @param duplicatedDependencies
	 * @return
	 */
	protected List<AbstractExtractedFact> removeDuplicatedDependencies(List<AbstractExtractedFact> duplicatedDependencies){
		List<AbstractExtractedFact> noDuplicates = new ArrayList<AbstractExtractedFact>();
		for(AbstractExtractedFact dependency : duplicatedDependencies){
			KDMEntity entityFrom = dependency.getDependencyStart();
			KDMEntity entityEnd = dependency.getDependencyEnd();
			boolean duplicated = false;
			for(AbstractExtractedFact otherFactDependency : noDuplicates){
				KDMEntity otherStartEntity = otherFactDependency.getDependencyStart();
				KDMEntity otherEndEntity = otherFactDependency.getDependencyEnd();
				if((entityFrom == otherStartEntity) && (entityEnd == otherEndEntity))
					duplicated =true;
				}
			if(!duplicated){
				noDuplicates.add(dependency);			
			}
			
		}
		return noDuplicates;
	}
	
}