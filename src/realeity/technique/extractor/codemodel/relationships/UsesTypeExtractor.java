package realeity.technique.extractor.codemodel.relationships;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.action.AbstractActionRelationship;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.UsesType;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.Imports;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;

import realeity.technique.extractor.AbstractKDMRelationshipExtractor;
import realeity.technique.extractor.codemodel.entities.ActionElementsExtractor;




/**
 * Build a dependency between a classunit cu and the destination of a UsesType (method instanceof in java, 
 * casting???).
 * @author Alvine Boaye Belle
 *
 */
public class UsesTypeExtractor  extends AbstractKDMRelationshipExtractor {
	
	/** 
	 * Each action element named "method invocation is considered as the from-point of a usesType.
	 */
	private String usesTypeActionName1 = "instanceof";
	private String usesTypeActionName2 = "cast";
	
	private List<String> actionNames = new ArrayList<String>();
	
	/**
	 * Constructor of the class UsesTypeExtraction. 
	 * Its visibility is private in order not to allow the other classes to create many instances of UsesTypeExtraction
	 */
	private UsesTypeExtractor(){
		actionNames.add(usesTypeActionName1);
		actionNames.add(usesTypeActionName2);
	}
	
	/**
	 * unique instance of UsesTypeExtraction.
	 */
	private static UsesTypeExtractor uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of UsesTypeExtraction is created.
	 * @return uniqueInstance
	 */
	public static UsesTypeExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new UsesTypeExtractor();
		}
		return uniqueInstance;
	}

	
	/**
	 * Get all the UsesType of a data type.
	 * @param cu
	 * @return
	 */
	 public List<UsesType> getUsesInDatatype(Datatype typeFrom){ 
		if(typeFrom instanceof ClassUnit){
			return getusesTypeInACU((ClassUnit) typeFrom);
		}
		else if(typeFrom instanceof InterfaceUnit){
			return getusesTypeInAnIU((InterfaceUnit) typeFrom);
		}
		else if(typeFrom instanceof EnumeratedType){
			return getUsesTypesInAnEnumeratedType((EnumeratedType) typeFrom);
		}
		else
			return new ArrayList<UsesType>();
	}
	 
	
	/**
	 * It gets all the usesType where an action element is the from-endpoint of the relationship. 
	 * An action can contains actionRelations or storableUnits. in the second case
	 * we have to get these storable units and retrieve the usesType inside.
	 * @param action
	 * @return
	 */
	private List<UsesType> getOutgoingUsesType(Datatype usesTypeStart, ActionElement action){
		List<UsesType> usesTypeList = new BasicEList<UsesType>();
		if(actionNames.contains(action.getName())){//the action is a method invocation
			EList<AbstractActionRelationship> actionRelations = action.getActionRelation();
			//System.out.println("le nombre d'actions relations pour le action element "
			//+ action.getName() + "  est: "+actionRelations.size());
			for(AbstractActionRelationship codeElement : actionRelations){
				if(codeElement instanceof UsesType){
					usesTypeList.add((UsesType)codeElement); 
					//System.out.println(" JE TRAITE le usesType allant de: "+(((UsesType)codeElement).getFrom()).getName()+
					//" à "+(((UsesType)codeElement).getTo()).getName());
				}
			}
		}
		
		return usesTypeList;
	}
	
	/**
	 * It gets all the usesType made by the action elements stored in the class unit
	 * @param cu
	 * @return
	 */
	 public List<UsesType> getusesTypeInACU(ClassUnit cu){ 
		//get all the action elements of the class
		List<ActionElement>  actionsInTheClassUnit = ActionElementsExtractor.getInstance().getActionsElementsFromDatatype(cu);
		//get all the usesType made by the action elements stored in the class unit
		List<UsesType> usesType = new ArrayList<UsesType>();
		for(ActionElement action : actionsInTheClassUnit){
			usesType.addAll(getOutgoingUsesType(cu, action));
		}
		
		return usesType;
	}
	
	/**
	 * It gets all the usesType made by the action elements stored in the interface unit. 
	 * All the action elements in the interface unit correspond to the declaration of its
	 * global variables, since, there is no action elements in its method units.
	 * @param iu
	 * @param actionNames
	 * @return
	 */
      List<UsesType> getusesTypeInAnIU(InterfaceUnit iu){
		//get all the usesType made by the action elements stored in the interface unit
		List<ActionElement>  actionsInTheInterfaceUnit =  ActionElementsExtractor.getInstance().getActionsElementsFromDatatype(iu);
		
		//get all the usesType made by the action elements stored in the interface unit
		List<UsesType> usesType = new ArrayList<UsesType>();
		for(ActionElement action : actionsInTheInterfaceUnit){
			usesType.addAll(getOutgoingUsesType(iu, action));
		}
		return usesType;
	}
	
	/**
	 * It gets all the usesType made by the action elements stored in the class unit.
	 * @param cu
	 * @return
	 */
	 List<UsesType> getUsesTypesInAnEnumeratedType(EnumeratedType enumType){ 
		//get all the action elements of the class
		List<ActionElement>  actionsInTheClassUnit = ActionElementsExtractor.getInstance().getActionsElementsFromDatatype(enumType);
		//get all the usesType made by the action elements stored in the class unit
		List<UsesType> usesType = new ArrayList<UsesType>();
		for(ActionElement action : actionsInTheClassUnit){
			usesType.addAll(getOutgoingUsesType(enumType, action));
		}
		
		return usesType;
	}
	
}
