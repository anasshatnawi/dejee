package realeity.technique.extractor.structuremodel;

import realeity.technique.extractor.AbstractKDMRelationshipExtractor;


/**
 * 
 * @author Alvine
 *
 */
public class AggregationRelationshipExtractor extends AbstractKDMRelationshipExtractor {

	/**
	 * Class's constructor. 
	 * Its visibility is private in order not to allow the other classes to create 
	 * many instances of LayeringFacade.
	 */
	private AggregationRelationshipExtractor(){
		
	}
	
	/**
	 * unique instance of LayeringFacade.
	 */
	private static AggregationRelationshipExtractor uniqueInstance;
	
	/**
	 * Method insuring that a unique instance of AggregationRelationshipExtractor is created.
	 * @return uniqueInstance
	 */
	public static AggregationRelationshipExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new AggregationRelationshipExtractor();
		}
		return uniqueInstance;
	}
}






