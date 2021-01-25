package realeity.technique.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.action.Creates;
import org.eclipse.gmt.modisco.omg.kdm.action.UsesType;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.Extends;
import org.eclipse.gmt.modisco.omg.kdm.code.Implements;
import org.eclipse.gmt.modisco.omg.kdm.code.Imports;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import realeity.technique.extractor.codemodel.entities.ActionElementsExtractor;
import realeity.technique.extractor.codemodel.relationships.ArrayInitializerConnections;
import realeity.technique.extractor.codemodel.relationships.CallsExtractor;
import realeity.technique.extractor.codemodel.relationships.CatchHeaderConnections;
import realeity.technique.extractor.codemodel.relationships.CreatesExtractor;
import realeity.technique.extractor.codemodel.relationships.DotClassConnections;
import realeity.technique.extractor.codemodel.relationships.ExtendsExtractor;
import realeity.technique.extractor.codemodel.relationships.ImplementsExtractor;
import realeity.technique.extractor.codemodel.relationships.ImportsExtractor;
import realeity.technique.extractor.codemodel.relationships.ParameterConnections;
import realeity.technique.extractor.codemodel.relationships.StorableUnitConnections;
import realeity.technique.extractor.codemodel.relationships.UsesTypeExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;
import realeity.technique.util.KDMEntityFullName;
import realeity.technique.util.RealeityUtils;


/**
 * Build dependencies at the level of compilationUnit.
 * In this context, a compilation unit can for instance be a class in Java,
 * a compilation unit in COBOL, and so on.
 * @author Alvine
 *
 */

public class DataDependencyBuilder extends AbstractDependencyBuilder {
	
	private final static int CLASS_DEPENDENCY_WEIGHT = 1;

	/**
	 * Class's constructor. 
	 * Its visibility is private in order not to allow the other classes to create 
	 * many instances of LayeringFacade.
	 */
	private DataDependencyBuilder(){
		
	}
	
	/**
	 * unique instance of LayeringFacade.
	 */
	private static DataDependencyBuilder uniqueInstance;
	
	/**
	 * Method insuring that a unique instance of DataDependencyBuilder is created.
	 * @return uniqueInstance
	 */
	public static DataDependencyBuilder getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new DataDependencyBuilder();
		}
		return uniqueInstance;
	}
	
	
	protected List<AbstractExtractedFact> extractFactsBetweenEntities(List<KDMEntity> relevantEntities, 
                                                                      List<KDMEntity> packageList){
		List<AbstractExtractedFact> extractedFacts = new ArrayList<AbstractExtractedFact>(); 
		for(KDMEntity extractedData : relevantEntities){
			if(extractedData instanceof Datatype){
				extractedFacts.addAll(getRelationsFromDataTypeToADatatype((Datatype) extractedData, packageList));
			}
	
		}

		extractedFacts = removeExternalDatatypes(extractedFacts, relevantEntities,
				                                 packageList);

		extractedFacts = sumWeights(extractedFacts);//modifier: toutes les dependances entre classes doivent avoir un poids egal a 1

		/*extractedFacts = removeDependenciesRelatedToAncestors(relevantEntities,
				                                              extractedFacts, packageList);*/
		
		return extractedFacts;
	}
	
	/**
	 * If a class B depends on a class C, then all the dependencies going from A (where A 
	 * is a daughter or a grand-daughter of B) to C should be ignored.
	 * Then if we have two dependencies (A, C) and (B, C), and that A is a  daughter or a 
	 * grand-daughter of B, then the dependency (A, C) should be removed from the list of dependencies.
	 * @param extractedDependencies
	 * @param packageList
	 * @return
	 */
	protected List<AbstractExtractedFact> removeDependenciesRelatedToAncestors(List<KDMEntity> relevantEntities,
			                                                                   List<AbstractExtractedFact> extractedDependencies,
			                                                                   List<KDMEntity> packageList){
		//TODO
		List<AbstractExtractedFact>  undesiredDependencies = new ArrayList<AbstractExtractedFact>();
		Map<Datatype, List<Datatype>> mapEntitiesToAncestors = new HashMap<Datatype,  List<Datatype>>();
		for(KDMEntity extractedData : relevantEntities){
			mapEntitiesToAncestors.put((Datatype) extractedData, 
					                      AbstractKDMRelationshipExtractor.fetchAncestorsList((Datatype) extractedData));
		}
		
		for(AbstractExtractedFact dependency1 : extractedDependencies){
			KDMEntity depEnd1 = dependency1.getDependencyEnd();
			KDMEntity depStart1 = dependency1.getDependencyStart();

			for(AbstractExtractedFact dependency2 : extractedDependencies){
				KDMEntity depEnd2 = dependency2.getDependencyEnd();

				if((depEnd1 == depEnd2) 
					&& mapEntitiesToAncestors.get(depStart1).contains(depEnd2)){
					undesiredDependencies.add(dependency1);
				}

			}
		}
		//remove all the undesired dependencies
		List<AbstractExtractedFact>  desiredDependencies = new ArrayList<AbstractExtractedFact>();
		for(AbstractExtractedFact dependency : extractedDependencies){
			if(!undesiredDependencies.contains(dependency) 
			   && relevantEntities.contains(dependency.getDependencyEnd()))
				desiredDependencies.add(dependency);
		}
		return desiredDependencies;
		
	}
	
	/** 
	 * Remove all the dependencies where the end-point does not belong to the analyzed system.
	 * @param dependencies
	 * @return
	 */
	protected List<AbstractExtractedFact> removeExternalDatatypes(List<AbstractExtractedFact> dependencies,
			                                                      List<KDMEntity> relevantEntities,
			                                                      List<KDMEntity> relevantModules){
		List<AbstractExtractedFact> sortedDependencies = new ArrayList<AbstractExtractedFact>();
		for(AbstractExtractedFact fact : dependencies){
			KDMEntity factEnd = fact.getDependencyEnd();
			if(relevantEntities.contains(factEnd))
				sortedDependencies.add(fact);
		}
		return sortedDependencies;
      }
	
	
	/**
	 * Replace a  FactDependency (packA, packB, 1) appearing x times in extractedFacts 
	 * by a single FactDependency (packA, packB, 1). 
	 * @param extractedFacts
	 * @return
	 */
	@Override
	protected List<AbstractExtractedFact> sumWeights(List<AbstractExtractedFact> extractedFacts){
		//if the same FactDependency (packA, packB, 1) appears x times in extractedFacts, 
		//then a single FactDependency (packA, packB, x)  should replace it
		List<AbstractExtractedFact> duplicatesWithWeight = new ArrayList<AbstractExtractedFact>();
		//sum the weights of the similar extractedFacts (same from package, end package and relation) 
		//and creates the corresponding FactDependency
		for(AbstractExtractedFact dependency : extractedFacts){
			KDMEntity entityFrom = dependency.getDependencyStart();
			KDMEntity entityEnd = dependency.getDependencyEnd();
			int weight = 0;
			for(AbstractExtractedFact otherDependency : extractedFacts){
				KDMEntity otherEntityFrom = otherDependency.getDependencyStart();
				KDMEntity otherEntityEnd = otherDependency.getDependencyEnd();
				if((entityFrom.equals(otherEntityFrom)) && (entityEnd.equals(otherEntityEnd)))
					weight++;
			}
		
			AbstractExtractedFact newFactDependency = new ElementaryFact(entityFrom, 
					                                                     entityEnd,
					                                                     CLASS_DEPENDENCY_WEIGHT, 
					                                                     RealeityUtils.RelationsTypes.Unspecified.toString(),
					                                                     "");
			duplicatesWithWeight.add(newFactDependency);
			
		}
		//remove the duplicates in duplicatesWithWeight
		List<AbstractExtractedFact> noDuplicates = removeDuplicatedDependencies(duplicatesWithWeight);
		return noDuplicates;
	}
	
	
	/**
	 * Delete the undesired dependencies between the extracted entities.
	 * @param extractedFacts
	 * @param relevantEntities
	 * @return
	 */
	public List<AbstractExtractedFact> removeIrrelevantFacts(List<AbstractExtractedFact> extractedFacts, 
			                                                 List<KDMEntity> relevantEntities){
		//TODO COMPLETER CETTE METHODE!!!
		return extractedFacts;
	}
	
	/**
	 * 
	 * @param relation
	 * @param typeFrom
	 * @param packageList
	 * @return
	 */
	private AbstractExtractedFact createAFact(KDMRelationship relation, Datatype typeFrom, 
                                              List<KDMEntity> packageList){
		
		AbstractExtractedFact factDependency = null;
		KDMEntity destination = AbstractKDMRelationshipExtractor.findDestinationDatatype(relation);
		//if endRelation is contained in the file of another class, then endRelation is assigned the value of that class
		
		if(destination instanceof Datatype)
			destination = AbstractKDMRelationshipExtractor.definedInsideTheFileOfAnotherClass((Datatype) destination, packageList);
		
		if((destination != null) && !(destination instanceof Package) && (destination != typeFrom)){
			//si la relation est un Imports, on ne la considère que si sa destination n'est pas un package (mais est un iu/cu)
			 factDependency = new ElementaryFact(typeFrom, destination, 1, 
					                             RealeityUtils.RelationsTypes.Unspecified.toString(),
					                             ""); 
		}
			return factDependency;
	}
	/**
	 * 
	 * @param typeFrom
	 * @param packageList
	 * @return
	 */
	private List<AbstractExtractedFact>  getRelationsFromDataTypeToADatatype(Datatype typeFrom, 
                                                                             List<KDMEntity> packageList){
		                    
		List<KDMRelationship> kdmRelationships = new ArrayList<KDMRelationship>();
		
		//get all the action elements of the class
		List<ActionElement>  actionsInTheClassUnit = ActionElementsExtractor.getInstance().getActionsElementsFromDatatype(typeFrom);
				
		List<Calls> callsList = CallsExtractor.getInstance().getCallsInDatatype(typeFrom, actionsInTheClassUnit);
		kdmRelationships.addAll(callsList);

		List<AbstractCodeRelationship> codeRelationList = CallsExtractor.getInstance().getCodeRelationInDatatype(typeFrom, actionsInTheClassUnit);
		kdmRelationships.addAll(codeRelationList);
		
		List<Creates> createsList = CreatesExtractor.getInstance().getCreatesInDatatype(typeFrom, actionsInTheClassUnit); 
		kdmRelationships.addAll(createsList); 

		List<Extends> extendsList = ExtendsExtractor.getInstance().getExtendsInDatatype(typeFrom);
		kdmRelationships.addAll(extendsList); 

		List<Imports> importsList = ImportsExtractor.getInstance().getImportsInADatatype(typeFrom);
		kdmRelationships.addAll(importsList); 
		
		if(!(typeFrom instanceof EnumeratedType)){
			List<Implements> implementsList = ImplementsExtractor.getInstance().getImplementsInDatatype(typeFrom);
			kdmRelationships.addAll(implementsList); 
		}
		if(typeFrom instanceof ClassUnit){
			List<UsesType> usesList = UsesTypeExtractor.getInstance().getUsesInDatatype(typeFrom);
			kdmRelationships.addAll(usesList);
		}
		//System.out.println("");
				
		List<AbstractExtractedFact> extractedFacts = new ArrayList<AbstractExtractedFact>();
		
		for(KDMRelationship relation : kdmRelationships){
			AbstractExtractedFact factDependency = createAFact(relation, typeFrom, packageList);
			if(factDependency != null)
				extractedFacts.add(factDependency);
		}
		
		//get the connections related to the signatures of each method unit defined in the data type
		if(typeFrom instanceof ClassUnit){
			List<AbstractExtractedFact> catchHeaderConnections = CatchHeaderConnections.getInstance().extractCatchConnections((ClassUnit) typeFrom, 
					                                                                                                          packageList);
		    extractedFacts.addAll(catchHeaderConnections);
		}
		
		if((typeFrom instanceof ClassUnit) || (typeFrom instanceof InterfaceUnit)){
			List<AbstractExtractedFact> arrayInitializerConnections = ArrayInitializerConnections.getInstance().getConnectionsInADatatype(typeFrom);
			extractedFacts.addAll(arrayInitializerConnections); 
			
		}
		List<AbstractExtractedFact> dotClassConnections = DotClassConnections.getInstance().getConnections(typeFrom);
		extractedFacts.addAll(dotClassConnections);
		  List<AbstractExtractedFact> parameterConnectionsInCu = ParameterConnections.getInstance().getParameterRelationsInDataType(typeFrom, 
                                                                 extractedFacts, packageList);
          extractedFacts.addAll(parameterConnectionsInCu);
      	List<AbstractExtractedFact> storableUnitsConnections = StorableUnitConnections.getInstance().getRelationsInDatatype(typeFrom,
                                                               extractedFacts);
        extractedFacts.addAll(storableUnitsConnections);
		
        // To do: check dependency duplication
//	    System.err.println("defore remove duplication, number of Facts is "+ extractedFacts.size());
//	    for (AbstractExtractedFact fact: extractedFacts){
//	    	System.err.println(fact);
//	    }
	    
	    extractedFacts = removeDuplicateInFactDependency(extractedFacts);
		
//	    System.err.println("number of Facts is "+ extractedFacts.size());
//	    for (AbstractExtractedFact fact: extractedFacts){
//	    	System.err.println(fact);
//	    }
	    	
		return extractedFacts;
	}
	
	
	/**
	 * Replace n extractedFacts of the form (start, end, 1) by a single FactDependency 
	 * under the form (start, end, 1).
	 * @param extractedFacts
	 * @return
	 */
	static List<AbstractExtractedFact> removeDuplicateInFactDependency(List<AbstractExtractedFact> extractedFacts){
		//if two entities are related through a relation, then the weight of 
		//all these relations should be considered as 1 
		//remove the duplicates in duplicatesWithWeight
		List<AbstractExtractedFact> noDuplicates = new ArrayList<AbstractExtractedFact>();
		for(AbstractExtractedFact FactDependency : extractedFacts){
			//System.out.println("voici un FactDependency extrait : " + FactDependency.toString());
			KDMEntity from = FactDependency.getDependencyStart();
			KDMEntity end = FactDependency.getDependencyEnd();
			boolean duplicated = false;
			for(AbstractExtractedFact otherFactDependency : noDuplicates){
				KDMEntity otherFrom =  otherFactDependency.getDependencyStart();
				KDMEntity otherEnd = otherFactDependency.getDependencyEnd();
				if((from == otherFrom) && (end == otherEnd))
					duplicated = true;
				}
			if(!duplicated && (end != null)){
				noDuplicates.add(FactDependency);			
			}
			
		}
			
		return noDuplicates;
	}

}

