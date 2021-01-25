package realeity.technique.extractor.structuremodel;

import realeity.technique.extractor.AbstractKDMEntityExtractor;


/**
 * 
 * @author Alvine
 *
 */
public class ComponentExtractor extends AbstractKDMEntityExtractor {
	
	/**
	 * Class's constructor. 
	 * Its visibility is private in order not to allow the other classes to create 
	 * many instances of LayeringFacade.
	 */
	private ComponentExtractor(){
		
	}
	
	/**
	 * unique instance of LayeringFacade.
	 */
	private static ComponentExtractor uniqueInstance;
	
	/**
	 * Method insuring that a unique instance of ComponentExtractor is created.
	 * @return uniqueInstance
	 */
	public static ComponentExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new ComponentExtractor();
		}
		return uniqueInstance;
	}
}






