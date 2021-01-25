package realeity.technique.extractor.codemodel.relationships;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.HasValue;
import org.eclipse.gmt.modisco.omg.kdm.code.Imports;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.StorableUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Value;

import realeity.technique.extractor.AbstractKDMRelationshipExtractor;
import realeity.technique.extractor.codemodel.entities.ActionElementsExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;
import realeity.technique.util.RealeityUtils;



/**
 * 
 * @author Alvine Boaye Belle
 * 2013  18:12:03
 */

public class ArrayInitializerConnections extends AbstractKDMRelationshipExtractor {
	
	private static String actionName = "array initializer";
	
	/**
	 * Constructor of the class MethodUnitsExtractor. 
	 * Its visibility is private in order not to allow the other classes to 
	 * create many instances of ArrayInitializerConnections.
	 */
	private ArrayInitializerConnections(){
		
	}
	
	/**
	 * unique instance of ArrayInitializerConnections.
	 */
	private static ArrayInitializerConnections uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of ArrayInitializerConnections is created.
	 * @return uniqueInstance
	 */
	public static ArrayInitializerConnections getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new ArrayInitializerConnections();
		}
		return uniqueInstance;
	}
	
	/**
	 * Get all the storable units corresponding to the global variables in the datatype. 
	 * @param datatype
	 * @return
	 */
	static List<StorableUnit> getGlobalStorableUnits(Datatype datatype){
		//get all the storable units corresponding to the global variables in the cu 
		List<StorableUnit> storableUnitList = new ArrayList<StorableUnit>();
		EList<CodeItem> codeItems = new BasicEList<CodeItem>();
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
	 * 
	 * @param globalActions
	 * @param actionNames
	 * @param datatype
	 * @return
	 */
	private List<AbstractExtractedFact> getFactFromActions(List<ActionElement> globalActions, 
			                                               List<String> actionNames, Datatype datatype){
		List<AbstractExtractedFact> tripleList = new ArrayList<AbstractExtractedFact>();
		//get the literal stored in these actions
		for(ActionElement action: globalActions){
			List<AbstractCodeElement> codeElements = new ArrayList<AbstractCodeElement>();
			if(actionNames.contains(action.getName())){
				codeElements.addAll(action.getCodeElement());
				List<Value> valueList = new ArrayList<Value>();
				for(AbstractCodeElement codeEle : codeElements){
					if(codeEle instanceof Value)
						valueList.add((Value) codeEle);
				}
				for(Value value : valueList){
					Datatype data = value.getType();
					AbstractExtractedFact FactDependency = new ElementaryFact (datatype, data, 1, 
							                                                   RealeityUtils.RelationsTypes.Unspecified.toString(),
							                                                   "");
					tripleList.add(FactDependency);
				}
			}
		}
		
	return tripleList;
	}
	/**
	 * 
	 * @param storableUnitList
	 * @return
	 */
	public List<AbstractExtractedFact> processStorableUnit(List<StorableUnit> storableUnitList,
			                                              Datatype datatype){
		List<AbstractExtractedFact> tripleList = new ArrayList<AbstractExtractedFact>();
		if(storableUnitList.size() > 0){
			//get all the hasvalue relationships in each storable unit
			EList<HasValue> hasValueRelations = new BasicEList<HasValue>();
			for(StorableUnit storableUnit : storableUnitList){
				EList<AbstractCodeRelationship> codeRelations = storableUnit.getCodeRelation();
				for(AbstractCodeRelationship relation : codeRelations){
					if(relation instanceof HasValue){
						hasValueRelations.add((HasValue) relation);
					}
				}
			}

	       //get actions from hasvalue relations
			List<String> actionNames = new ArrayList<String>();
			actionNames.add(actionName);
			List<ActionElement> globalActions = new ArrayList<ActionElement>();
			for(HasValue hasValueRelation : hasValueRelations){
				AbstractCodeElement  codeElement = hasValueRelation.getTo();
				if(codeElement instanceof ActionElement) {
					ActionElement action = (ActionElement) codeElement;
					List<ActionElement> actionList = ActionElementsExtractor.getInstance().getInternalActions(action, 
							                                                                                  actionNames);
					globalActions.addAll(actionList);
			
				}
		    }
			
			//get the literal stored in these actions
			tripleList.addAll(getFactFromActions(globalActions, actionNames, datatype));
		}
		return tripleList;
	}
	
	/**
	 * 
	 * @param cu
	 * @return
	 */
	public List<AbstractExtractedFact> getConnectionsInACu(Datatype cu){
		List<AbstractExtractedFact> tripleList = new ArrayList<AbstractExtractedFact>();
		//get the storable units corresponding to the global variable  of the class unit
		List<StorableUnit> storableUnitList = getGlobalStorableUnits(cu);
		tripleList.addAll(processStorableUnit(storableUnitList, cu));
		
		//get the storable units corresponding to the content of the method units of the class
		List<String> actionNames = new ArrayList<String>();
		actionNames.add(actionName);
		List<ActionElement>  actions = ActionElementsExtractor.getInstance().getActionsInMethodUnits((ClassUnit) cu, 
				                                                                                     actionNames, 
				                                                                                     actionNames);
		tripleList.addAll(getFactFromActions(actions, actionNames, cu));
		
		return tripleList;
	}
	
	
	/**
	 * 
	 * @param cu
	 * @return
	 */
	public List<AbstractExtractedFact> getConnectionsInAnIu(Datatype iu){
		
		//get the storable units corresponding to the instructions  of the interface
		List<StorableUnit> storableUnitList = getGlobalStorableUnits(iu);
		return processStorableUnit(storableUnitList, iu);
	}
	
	
	/**
	 * 
	 * @param cu
	 * @return
	 */
	public List<AbstractExtractedFact> getConnectionsInADatatype(Datatype typeFrom){
		
		if (typeFrom instanceof ClassUnit)
			return getConnectionsInACu(typeFrom);
		else if (typeFrom instanceof InterfaceUnit)
			return getConnectionsInAnIu(typeFrom);
		else
			return new ArrayList<AbstractExtractedFact>();
	}
	
}

