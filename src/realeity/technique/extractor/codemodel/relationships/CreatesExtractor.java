package realeity.technique.extractor.codemodel.relationships;
/**
 * Get all the creates relationships between packages.
 */
import java.util.ArrayList;
import java.util.List;



import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.action.AbstractActionRelationship;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.action.Creates;
import org.eclipse.gmt.modisco.omg.kdm.action.Writes;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;

import realeity.technique.extractor.AbstractKDMRelationshipExtractor;
import realeity.technique.extractor.codemodel.entities.ActionElementsExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;
import realeity.technique.util.RealeityUtils;



/**
 * Retrieve all the Creates relationshiphs contained in a list of action elements.
 * These action elements have been extracted from a Datatype (class, interface or enumerated type)
 * @author Alvine Boaye Belle
 * 2013  18:09:26
 */

public class CreatesExtractor extends AbstractKDMRelationshipExtractor {
	
	private static String CreatesActionName1 = "class instance creation";
	private static String CreatesActionName2 = "array creation";
	private static String callsActionName1 = "method invocation";
	private static List<String> actionNames = new ArrayList<String>();
	private static List<String> actionNamesInStorableUnits = new ArrayList<String>();
	private static String RelationName = "Creates";
	
	/**
	 * Constructor of the class CreatesExtraction. 
	 * Its visibility is private in order not to allow the other classes to 
	 * create many instances of CreatesExtraction.
	 */
	private CreatesExtractor(){
	}
	
	/**
	 * unique instance of CreatesExtraction.
	 */
	private static CreatesExtractor uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of CreatesExtraction is created.
	 * @return uniqueInstance
	 */
	public static CreatesExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new CreatesExtractor();
		}
		return uniqueInstance;
	}
	
	/**
	 * Get all the Creates where an action element is the from-endpoint of the relationship.
	 * @param action
	 * @return
	 */
	private List<Creates> getOutgoingCreates(ActionElement action){
		List<Creates> CreatesList = new BasicEList<Creates>();
		EList<AbstractActionRelationship> actionRelations = action.getActionRelation();
		for(AbstractActionRelationship relation : actionRelations){
			if(relation instanceof Creates){
				CreatesList.add((Creates)relation); 
				/*System.out.println(" JE TRAITE le Creates allant de: "+(((Creates)relation).getFrom()).getName() 
				+ "à "+(((Creates)relation).getTo()).getName());*/
			}
		}
		
		return CreatesList;
	}
	
	/**
	 * Get all the Creates made by the action elements stored in the data type.
	 * @param cu
	 * @return
	 */
	 public List<Creates> getCreatesInDatatype(Datatype typeFrom, List<ActionElement> actionsInTheClassUnit){ 
		if(typeFrom instanceof ClassUnit){
			return getCreatesInACU(typeFrom, actionsInTheClassUnit);
		}
		else if(typeFrom instanceof InterfaceUnit){
			return getCreatesInAnIU(typeFrom, actionsInTheClassUnit);
		}
		else if(typeFrom instanceof EnumeratedType){
			return getCreatesInAnEnumeratedType(typeFrom);
		}
		else
			return new ArrayList<Creates>();
	}
	
	/**
	 * Get all the calls made by the action elements stored in the class unit
	 * @param cu
	 * @return
	 */
     public List<Creates> getCreatesInACU(Datatype cu, List<ActionElement>  actionsInTheClassUnit){
		//get all the creates made by the action elements stored in the class unit
    	 
		List<Creates> creates = new ArrayList<Creates>();
		for(ActionElement action : actionsInTheClassUnit){
			creates.addAll(getOutgoingCreates(action));
		}
		return creates;
	}
	
	
	/**
	 * Get all the calls made by the action elements stored in the class unit
	 * @param cu
	 * @return
	 */
     public List<Creates> getCreatesInAnEnumeratedType(Datatype enumType){
		//get all the action elements of the class
		List<ActionElement>  actionsInTheClassUnit = ActionElementsExtractor.getInstance().getActionsFromEnumeratedType((EnumeratedType) enumType);
		
		//get all the calls made by the action elements stored in the class unit
		List<Creates> calls = new ArrayList<Creates>();
		for(ActionElement action : actionsInTheClassUnit){
			calls.addAll(getOutgoingCreates(action));
		}
		return calls;
	}
	
	/**
	 * Get all the calls made by the action elements stored in the interface unit. 
	 * All the action elements in the interface unit correspond to the declaration of its
	 * global variables, since, there is no action elements in its method units
	 * @param iu
	 * @param actionNames
	 * @return
	 */
	 public List<Creates> getCreatesInAnIU(Datatype iu, List<ActionElement>  actionsInTheInterfaceUnit){
		
		//get all the calls made by the action elements stored in the interface unit
		List<Creates> createsList = new ArrayList<Creates>();
		for(ActionElement action : actionsInTheInterfaceUnit){
			createsList.addAll(getOutgoingCreates(action));
		}
		return createsList;
	}
	
	
	/**
	 * build all the FactDependencys corresponding to the calls stored in a list
	 * @param callsList
	 * @return
	 */
     List<AbstractExtractedFact> buildFactDependencys(List<Package> packageList, Package fromPackage, List<Creates> createsList){
		List<AbstractExtractedFact> FactDependencys = new ArrayList<AbstractExtractedFact>();
		for(Creates createsRelation: createsList){
			try{
				Package packageFrom = fromPackage;
				Package packageEnd = findEndPackage(packageList, createsRelation); 
				if((packageFrom != packageEnd) && (packageEnd != null)) //we don't take in account the creates relations in the same package
					FactDependencys.add(new ElementaryFact(packageFrom, packageEnd, 1, 
							            RealeityUtils.RelationsTypes.Creates.toString(), ""));
			}
			catch(Exception e){
				//System.out.println("CreatesExtraction : the FactDependency can not be formed 
				//because the end package does not belong to the system");
		    }
		
	    }
		return FactDependencys;
	}
	
}