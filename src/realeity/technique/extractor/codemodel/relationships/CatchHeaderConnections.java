package realeity.technique.extractor.codemodel.relationships;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

import realeity.technique.extractor.AbstractKDMRelationshipExtractor;
import realeity.technique.extractor.codemodel.entities.ActionElementsExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;
import realeity.technique.util.RealeityUtils;



/**
 * Allows to build a dependency from a classUnit to the type of error specified in the header of a CatchUnit
 * @author Alvine Boaye Belle
 * 2013  18:07:49
 */

public class CatchHeaderConnections extends AbstractKDMRelationshipExtractor {

	/**
	 * Constructor of the class CatchConnections. 
	 * Its visibility is private in order not to allow the other classes to create many instances of CatchConnections
	 */
	private CatchHeaderConnections(){
		
	}
	
	/**
	 * unique instance of CatchConnections.
	 */
	private static CatchHeaderConnections uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of CatchConnections is created.
	 * @return uniqueInstance
	 */
	public static CatchHeaderConnections getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new CatchHeaderConnections();
		}
		return uniqueInstance;
	}
	
	
	/**
	 * Retrieve the types of the storable units defined in the header of the catch block (CatchUnit) 
	 * existing in the class unit and build the corresponding dependencies.
	 * @param tryUnit
	 * @return cu
     */
	public List<AbstractExtractedFact> extractCatchConnections(ClassUnit cu, 
			                                                   List<KDMEntity> packageList){
		//TryUnit is a grandson of a BlockUnit
		
		List<Datatype> dataForCatchConnections = new ArrayList<Datatype>();
		ActionElementsExtractor.getInstance().getActionsInClassUnit(cu, dataForCatchConnections);
				
		List<AbstractExtractedFact> catchDependencies = new ArrayList<AbstractExtractedFact>();
		for(Datatype type : dataForCatchConnections){
			if(type instanceof Datatype)
				type = super.definedInsideTheFileOfAnotherClass((Datatype) type, packageList);
			if(type != null){
				AbstractExtractedFact factDependency = new ElementaryFact(cu, type, 1, 
						                                                  RealeityUtils.RelationsTypes.ConnectionsFromStorableUnit.toString(),
						                                                  "");
				catchDependencies.add(factDependency);	
			}
		}
		return catchDependencies;
	}
}
