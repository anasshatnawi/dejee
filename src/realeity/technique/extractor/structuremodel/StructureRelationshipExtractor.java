package realeity.technique.extractor.structuremodel;

import realeity.technique.extractor.AbstractKDMRelationshipExtractor;


/**
 * 
 * @author Alvine
 *
 */
public class StructureRelationshipExtractor extends AbstractKDMRelationshipExtractor {

	/**
	 * Class's constructor. 
	 * Its visibility is private in order not to allow the other classes to create 
	 * many instances of LayeringFacade.
	 */
	private StructureRelationshipExtractor(){
		
	}
	
	/**
	 * unique instance of LayeringFacade.
	 */
	private static StructureRelationshipExtractor uniqueInstance;
	
	/**
	 * Method insuring that a unique instance of StructureRelationshipExtractor is created.
	 * @return uniqueInstance
	 */
	public static StructureRelationshipExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new StructureRelationshipExtractor();
		}
		return uniqueInstance;
	}
}






