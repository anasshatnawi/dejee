package realeity.technique.extractor.codemodel.entities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.action.AbstractActionRelationship;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.Addresses;
import org.eclipse.gmt.modisco.omg.kdm.action.Creates;
import org.eclipse.gmt.modisco.omg.kdm.action.Reads;
import org.eclipse.gmt.modisco.omg.kdm.action.Writes;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.StorableUnit;

import realeity.technique.extractor.AbstractKDMEntityExtractor;


/**
 * Extract all the storable units contained in a datatype (class unit, interfaceunit, and so on).
 * @author Alvine Boaye Belle
 *
 */
public class StorableUnitsExtractor  extends AbstractKDMEntityExtractor {
	
	/**
	 * Constructor of the class StorableUnitConnections. 
	 * Its visibility is private in order not to allow the other classes to create many instances of StorableUnitConnections.
	 */
	private StorableUnitsExtractor(){
		
	}
	
	/**
	 * unique instance of ImplementsExtractor.
	 */
	private static StorableUnitsExtractor uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of StorableUnitsExtractor is created.
	 * @return uniqueInstance
	 */
	public static StorableUnitsExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new StorableUnitsExtractor();
		}
		return uniqueInstance;
	}

	/**
	 * get all the storable units corresponding to the global variables in the datatype
	 * @param datatype
	 * @return
	 */
	private List<StorableUnit> getGlobalStorableUnits(Datatype datatype){
		//get all the storable units corresponding to the global variables in the datatype
		List<StorableUnit> storableUnitList = new ArrayList<StorableUnit>();
		List<CodeItem> codeItems = new ArrayList<CodeItem>();
		if(datatype instanceof ClassUnit)
			codeItems = ((ClassUnit)datatype).getCodeElement();
		if(datatype instanceof InterfaceUnit)
			codeItems = ((InterfaceUnit)datatype).getCodeElement();
		
		for(CodeItem codeItem : codeItems){
			if(codeItem instanceof StorableUnit)
				storableUnitList.add((StorableUnit) codeItem);
		}
				
		return storableUnitList;
	}
	
	/**
	 * Get the storable units inside an enumerated type ie, the storable units inside its internal classes, inside its method units
	 * and those inside its anonymous classes. NB: inside a method we can have storable units (containing actions elements)
	 * or recursively : actions elements containing storable units
	 * @param cu
	 * @return
	 */
	public List<StorableUnit> getStorableUnitsInsideAnEnumeratedType (EnumeratedType enumType){
		 List<ActionElement> actionList = ActionElementsExtractor.getInstance().getActionsInEnumeratedType(enumType);
		 List<StorableUnit> storableList = new ArrayList<StorableUnit>();
		 for(ActionElement action : actionList){
			 EList<AbstractCodeElement> codeList = action.getCodeElement();
			 for(AbstractCodeElement codeEle : codeList){
				 if(codeEle instanceof StorableUnit)
					 storableList.add((StorableUnit) codeEle);
			 }
		 }
	   return storableList;
	}
	
	/**
	 * 
	 * @param cu
	 * @return
	 */
	private static List<StorableUnit>  getStorableUnitsFrom4Relations (ClassUnit cu){
		 List<ActionElement> actionList = ActionElementsExtractor.getInstance().getActionsInClassUnit(cu, 
				                                                                                      new ArrayList<Datatype>());
		 List<StorableUnit> storableList = new ArrayList<StorableUnit>();
		 for(ActionElement action : actionList){
			 EList<AbstractActionRelationship> actionRelations = action.getActionRelation();
			 for(AbstractActionRelationship relation : actionRelations){
				 if((relation.getTo() instanceof StorableUnit) 
					&&((relation instanceof Writes) || (relation instanceof Creates)
					 || (relation instanceof Reads) || (relation instanceof Addresses))){
					 //checks whether some of these 4 relationships are irrelevant. If so, they are removed
					 StorableUnit su = (StorableUnit) relation.getTo();
					 storableList.add(su);
					 //System.out.println("storable unit supplémentaire : "+su.getName());
				 }
			 }
		 }
		 return storableList;
	}
	
	/**
	 * Get the storable units inside a class unit ie, the storable units inside its internal classes, inside its method units
	 * and those inside its anonymous classes. NB: inside a method we can have storable units (containing actions elements)
	 * or recursively : actions elements containing storable units
	 * @param cu
	 * @return
	 */
	public static List<StorableUnit> getStorableUnitsInsideACU (ClassUnit cu){
		//Retrieve all the storable units under the form storableUnit su = actionElement
		 List<ActionElement> actionList = ActionElementsExtractor.getInstance().getActionsInClassUnit(cu,
				                                                                                      new ArrayList<Datatype>());
		 
		 List<StorableUnit> storableList = new ArrayList<StorableUnit>();
		 for(ActionElement action : actionList){
			 EList<AbstractCodeElement> codeList = action.getCodeElement();
			 for(AbstractCodeElement codeEle : codeList){
				 if(codeEle instanceof StorableUnit)
					 storableList.add((StorableUnit) codeEle);
			}
		 }
		//Retrieve all the storable units under which are the destination of  addresses or creates relationship
		 storableList.addAll(getStorableUnitsFrom4Relations(cu));
	     return storableList;
	}
	
	
	/**
	 * get all the StorableUnits elements inside an enumerated type. ie, those corresponding to the global variables, nested classes and
	 * anonymous classes
	 * @param cu
	 * @return
	 */
	public List<StorableUnit> getStorableUnitsFromAnEnumeratedType(EnumeratedType enumType){
		 List<StorableUnit> StorableUnitList = new ArrayList<StorableUnit>();
		 StorableUnitList.addAll(getGlobalStorableUnits(enumType));
		 StorableUnitList.addAll(getStorableUnitsInsideAnEnumeratedType(enumType));
	     return StorableUnitList;
	}
	
	/**
	 * It gets all the StorableUnits elements inside a class, ie, those corresponding to the global variables, nested classes and
	 * anonymous classes.
	 * @param cu
	 * @return
	 */
	public  List<StorableUnit> getStorableUnitsFromACU(ClassUnit cu){
		 List<StorableUnit> StorableUnitList = new ArrayList<StorableUnit>();
		 StorableUnitList.addAll(getGlobalStorableUnits(cu));
		 StorableUnitList.addAll(getStorableUnitsInsideACU(cu));
	     return StorableUnitList;
	}
	
	/**
	 * It gets all the StorableUnits elements inside an interface unit. ie, those corresponding to the global variables.
	 * @param cu
	 * @return
	 */
	public List<StorableUnit> getStorableUnitsFromAnIU (InterfaceUnit iu){
		 List<StorableUnit> StorableUnitList = new ArrayList<StorableUnit>();
		 StorableUnitList.addAll(getGlobalStorableUnits(iu));
	     //there is no StorableUnits inside the methods of an interface unit
	   return StorableUnitList;
	}
}
