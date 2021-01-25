package realeity.technique.extractor.codemodel.relationships;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Value;

import realeity.technique.extractor.AbstractKDMRelationshipExtractor;
import realeity.technique.extractor.codemodel.entities.ActionElementsExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;
import realeity.technique.util.RealeityUtils;


/**
 * Build a connection between the current datatype and a typeXX corresponding,
 * in the implementation of the datatype, to an action element under the form
 * "typeXX.class..."
 * @author Alvine Boaye Belle
 *
 */
public class DotClassConnections extends AbstractKDMRelationshipExtractor {
	
	private static String actionName1 = "method invocation";
	
	/**
	 * Constructor of the class DotClassConnections. 
	 * Its visibility is private in order not to allow the other classes to 
	 * create many instances of DotClassConnections.
	 */
	private DotClassConnections(){
	}
	
	/**
	 * unique instance of CreatesExtraction.
	 */
	private static DotClassConnections uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of DotClassConnections is created.
	 * @return uniqueInstance
	 */
	public static DotClassConnections getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new DotClassConnections();
		}
		return uniqueInstance;
	}
	
	/**
	 * 
	 * @param datatype
	 * @return
	 */
	public List<AbstractExtractedFact> getConnections(Datatype datatype){
		//get all the action elements from the datatype
		List<ActionElement> actionList = new ArrayList<ActionElement>();
		if(datatype instanceof ClassUnit)
			actionList = ActionElementsExtractor.getInstance().getActionsElementsFromDatatype(datatype);
		else if(datatype instanceof InterfaceUnit)
			actionList = ActionElementsExtractor.getInstance().getActionsElementsFromDatatype(datatype);
		else if(datatype instanceof EnumeratedType)
			actionList = ActionElementsExtractor.getInstance().getActionsElementsFromDatatype(datatype);
		
		List<Value> valueList = new ArrayList<Value>();
		for(ActionElement action : actionList){
			try{
				if(action != null && action.getName().equals(actionName1)){//process the action element
					EList<AbstractCodeElement> codeList = action.getCodeElement();
					for(AbstractCodeElement codeElement: codeList){
						if(codeElement instanceof Value)
							valueList.add((Value) codeElement);
					}
				}
			}
			catch(Exception e){
				//System.out.println("datatype = " + datatype.getName() + " action = " + action.getName());
			}
		}
		
		List<AbstractExtractedFact> tripleList =  buildFacts(datatype, valueList);
		
		return tripleList;
	}
	
	/**
	 * 
	 * @param datatype
	 * @param valueList
	 * @return
	 */
	private List<AbstractExtractedFact> buildFacts(Datatype datatype,
			                                       List<Value> valueList){
		List<AbstractExtractedFact> tripleList = new ArrayList<AbstractExtractedFact>();
		for(Value value : valueList){
			Datatype data = value.getType();
			AbstractExtractedFact factDependency = new ElementaryFact(datatype, data, 1, 
					                                                  RealeityUtils.RelationsTypes.Unspecified.toString(),
					                                                  "");
			tripleList.add(factDependency);
			//System.out.println("!!!!!!!le datatype "+datatype.getName()+" a pour 
			//connections : "+tripleList.size()+" avec : "+data.getName());
		}
		
		return tripleList;
	}

}
