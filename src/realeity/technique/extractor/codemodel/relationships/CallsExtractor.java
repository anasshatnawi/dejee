package realeity.technique.extractor.codemodel.relationships;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.action.AbstractActionRelationship;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeFactory;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

import realeity.technique.extractor.AbstractKDMRelationshipExtractor;
import realeity.technique.extractor.codemodel.entities.ActionElementsExtractor;
import realeity.technique.extractor.codemodel.entities.ClassUnitExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;
import realeity.technique.util.RealeityUtils;

/**
 * This class allows to extract all the Calls dependencies (a method of a class
 * A calling a method of a class B) relevant to the analyzed system.
 * 
 * @author Alvine Boaye Belle
 * 
 */
public class CallsExtractor extends AbstractKDMRelationshipExtractor {
	/**
	 * Each action element named "method invocation" is considered as the
	 * from-point of a calls.
	 */
	private static String callsActionName1 = "method invocation";
	private static String callsActionName2 = "super constructor invocation";
	private static String callsActionName3 = "super method invocation";
	static List<String> actionNames = new ArrayList<String>();

	/**
	 * Constructor of the class CallsExtraction. Its visibility is private in
	 * order not to allow the other classes to create many instances of
	 * CallsExtraction.
	 */
	private CallsExtractor() {
		actionNames.add(callsActionName1);
		actionNames.add(callsActionName2);
		actionNames.add(callsActionName3);
	}

	/**
	 * unique instance of CallsExtraction.
	 */
	private static CallsExtractor uniqueInstance;

	/**
	 * Method insuring that a unique instance of CallsExtraction is created.
	 * 
	 * @return uniqueInstance
	 */
	public static CallsExtractor getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new CallsExtractor();
		}
		return uniqueInstance;
	}

	/**
	 * Get all the calls made by the action elements stored in the class unit.
	 * 
	 * @param cu
	 * @return
	 */
	public List<AbstractCodeRelationship> getCodeRelationInDatatype(
			Datatype typeFrom, List<ActionElement> actionsInTheClassUnit) {

		if (typeFrom instanceof ClassUnit) {
			System.err.println("Classs name: " + typeFrom.getName());
			EList<CodeItem> codeItems = ((ClassUnit) typeFrom).getCodeElement();
			for (CodeItem cItem : codeItems) {
				if (cItem instanceof MethodUnit) {
					System.err
							.println("\tmethof unit name: " + cItem.getName());
					EList<AbstractCodeElement> mElements = ((MethodUnit) cItem)
							.getCodeElement();
					for (AbstractCodeElement element : mElements) {
						if (element instanceof BlockUnit) {
							System.err.println("We added : "
									+ element.getCodeRelation().size()
									+ "code relations");
							return element.getCodeRelation();
						}
					}

				}else{
					System.err.println("not method, name: "+ cItem.getName());
				}
			}
		}
		return new ArrayList<AbstractCodeRelationship>();
	}

	/**
	 * Get all the calls made by the action elements stored in the class unit.
	 * 
	 * @param cu
	 * @return
	 */
	public List<Calls> getCallsInDatatype(Datatype typeFrom,
			List<ActionElement> actionsInTheClassUnit) {
		if (typeFrom instanceof ClassUnit) {
			return getCallsInACU(typeFrom, actionsInTheClassUnit);
		} else if (typeFrom instanceof InterfaceUnit) {
			return getCallsInAnIU(typeFrom, actionsInTheClassUnit);
		} else if (typeFrom instanceof EnumeratedType) {
			return getCallsInAnEnumeratedType(typeFrom);
		} else
			return new ArrayList<Calls>();
	}

	/**
	 * Get all the calls where an action element is the from-endpoint of the
	 * relationship. An action can contains actionRelations or storableUnits. in
	 * the second case we have to get these storable units and retrieve the
	 * Calls inside.
	 * 
	 * @param action
	 * @return
	 */
	private List<Calls> getOutgoingCalls(Datatype CallsStart,
			ActionElement action, List<Calls> undesiredCallsList) {
		List<Calls> callsList = new BasicEList<Calls>();
		if (actionNames.contains(action.getName())) {// the action is a method
														// invocation
			EList<AbstractActionRelationship> actionRelations = action
					.getActionRelation();
			// System.out.println("le nombre d'actions relations pour le action element "+action.getName()+"  est: "+actionRelations.size());
			for (AbstractActionRelationship codeElement : actionRelations) {
				if ((codeElement instanceof Calls)) {
					callsList.add((Calls) codeElement);
					// System.out.println(" JE TRAITE le Calls allant de: "+(((Calls)codeElement).getFrom()).getName()+
					// " à "+(((Calls)codeElement).getTo()).getName());
				}
			}
		} else {
			try {
				if (action.getName().equals(
						AbstractKDMRelationshipExtractor.CreatesActionName1)) {
					EList<AbstractActionRelationship> actionRelations = action
							.getActionRelation();
					// System.out.println("le nombre d'actions relations pour le action element "+action.getName()
					// + "  est: "+actionRelations.size());
					for (AbstractActionRelationship codeElement : actionRelations) {
						if ((codeElement instanceof Calls)) {
							undesiredCallsList.add((Calls) codeElement);
							// System.out.println(" JE TRAITE le Calls allant de: "
							// + (((Calls)codeElement).getFrom()).getName() +
							// " à " +
							// (((Calls)codeElement).getTo()).getName());
						}
					}
				}
			} catch (NullPointerException e) {
				// e.printStackTrace();
			}
		}

		return callsList;
	}

	/**
	 * build all the FactDependencys corresponding to the calls stored in a list
	 * 
	 * @param callsList
	 * @return
	 */
	List<AbstractExtractedFact> buildFactDependencys(List<Package> packageList,
			Package fromPackage, List<Calls> callsList) {
		List<AbstractExtractedFact> FactDependencys = new ArrayList<AbstractExtractedFact>();
		for (Calls callRelation : callsList) {
			try {
				Package packageFrom = fromPackage;
				Package packageEnd = findEndPackage(packageList, callRelation);
				if ((packageFrom != packageEnd) && (packageEnd != null)) { // we
																			// don't
																			// take
																			// in
																			// account
																			// the
																			// calls
																			// relations
																			// in
																			// the
																			// same
																			// package
					AbstractExtractedFact dependency = new ElementaryFact(
							packageFrom, packageEnd, 1,
							RealeityUtils.RelationsTypes.Calls.toString(), "");
					FactDependencys.add(dependency);
					// System.out.println(" calls: " +
					// dependency.toString());//delete: just for debug
				}
			} catch (Exception e) {
				// System.out.println("CallsExtraction: the FactDependency can
				// not be
				// formed because the end package does not belong to the
				// system");
			}
		}
		return FactDependencys;
	}

	/**
	 * If in a class unit, there is a calls under the form ClassA.internalClass,
	 * then the calls from classA to internalClass should be ignored.
	 * 
	 * @param callsList
	 * @return
	 */
	static List<Calls> removeRedundantCalls(List<Calls> callsList) {
		// TODO this method should also be used in CreatesExtractor!
		List<Calls> callsToRemove = new ArrayList<Calls>();
		Map<Calls, Datatype> mapCallsToDestination = new HashMap<Calls, Datatype>();
		for (Calls callsRelation : callsList) {
			Datatype destination = (Datatype) findDestinationDatatype(callsRelation);
			mapCallsToDestination.put(callsRelation, destination);
		}

		for (Calls callsRel : mapCallsToDestination.keySet()) {
			try {
				Datatype destination = mapCallsToDestination.get(callsRel);
				List<ClassUnit> internalClasses = new ArrayList<ClassUnit>();
				List<EnumeratedType> internalEnum = new ArrayList<EnumeratedType>();
				if (destination instanceof ClassUnit)
					internalClasses = ClassUnitExtractor.getInstance()
							.getTheInternalClassesInTheClassUnit(
									(ClassUnit) destination);
				else if (destination instanceof InterfaceUnit)
					internalEnum = ClassUnitExtractor.getInstance()
							.getTheInternalClassesInTheInterfaceUnit(
									(InterfaceUnit) destination);

				for (Calls callsRel2 : mapCallsToDestination.keySet()) {
					Datatype destination2 = mapCallsToDestination
							.get(callsRel2);
					if (internalClasses.contains(destination2)
							|| internalEnum.contains(destination2))
						callsToRemove.add(callsRel2);
				}
			} catch (java.lang.ClassCastException e) {

			} catch (NullPointerException e) {

			}
		}

		// remove all the dependencies related to the undesired Calls
		for (Calls calls : callsToRemove) {
			callsList.remove(calls);
		}

		return callsList;
	}

	/**
	 * A calls going from cuFrom to a method of any of its ancestors should not
	 * be taken into account.
	 * 
	 * @param callsList
	 * @return
	 */
	private List<Calls> removeHierarchicalCalls(Datatype typeFrom,
			List<Calls> callsList) {
		List<Calls> callsToRemove = new ArrayList<Calls>();

		for (Calls callRelation : callsList) {
			KDMEntity callsDestination = findDestinationDatatype(callRelation);
			if (isAnAncestor(callsDestination, typeFrom)) {
				callsToRemove.add(callRelation);
			}
		}

		// remove all the dependencies related to the undesired Calls
		for (Calls calls : callsToRemove) {
			callsList.remove(calls);
		}

		return callsList;
	}

	/**
	 * to complete
	 * 
	 * @param callsList
	 * @return
	 */
	private List<Calls> removeUndesiredCallsAncestors(List<Calls> callsList,
			List<Calls> undesiredCallsList) {

		Map<Calls, Datatype> mapCallsToDestination = new HashMap<Calls, Datatype>();
		for (Calls callsRelation : callsList) {
			Datatype destination = (Datatype) findDestinationDatatype(callsRelation);
			mapCallsToDestination.put(callsRelation, destination);
		}

		Map<Calls, List<Datatype>> mapUndesiredCallsToDestination = new HashMap<Calls, List<Datatype>>();
		for (Calls callsRelation : undesiredCallsList) {
			Datatype destination = (Datatype) findDestinationDatatype(callsRelation);
			if (destination != null)
				mapUndesiredCallsToDestination.put(callsRelation,
						fetchAncestorsList(destination));
		}

		for (Calls callsRel : mapCallsToDestination.keySet()) {
			Datatype possibleAncestor = mapCallsToDestination.get(callsRel);
			for (Calls undesiredCallsRel : mapUndesiredCallsToDestination
					.keySet()) {
				List<Datatype> ancestorsList = mapUndesiredCallsToDestination
						.get(undesiredCallsRel);
				if (ancestorsList.contains(possibleAncestor)) {
					callsList.remove(callsRel);
				}

			}
		}

		return callsList;
	}

	/**
	 * Get all the calls made by the action elements stored in the class unit.
	 * 
	 * @param cu
	 * @return
	 */
	public List<Calls> getCallsInACU(Datatype cuFrom,
			List<ActionElement> actionsInTheClassUnit) {
		// get all the calls made by the action elements stored in the class
		// unit
		List<Calls> callsList = new ArrayList<Calls>();
		List<Calls> undesiredCallsList = new ArrayList<Calls>();

		for (ActionElement action : actionsInTheClassUnit) {
			callsList.addAll(getOutgoingCalls(cuFrom, action,
					undesiredCallsList));
		}

		callsList = removeUndesiredCallsAncestors(callsList, undesiredCallsList);
		callsList = removeRedundantCalls(callsList);
		callsList = removeHierarchicalCalls(cuFrom, callsList);

		return callsList;
	}

	/**
	 * Get all the calls made by the action elements stored in the interface
	 * unit. All the action elements in the interface unit correspond to the
	 * declaration of its global variables, since, there is no action elements
	 * in its method units.
	 * 
	 * @param iu
	 * @param actionNames
	 * @return
	 */
	public List<Calls> getCallsInAnIU(Datatype iu,
			List<ActionElement> actionsInTheInterfaceUnit) {

		// get all the calls made by the action elements stored in the interface
		// unit
		List<Calls> calls = new ArrayList<Calls>();
		List<Calls> undesiredCallsList = new ArrayList<Calls>();

		for (ActionElement action : actionsInTheInterfaceUnit) {
			calls.addAll(getOutgoingCalls(iu, action, undesiredCallsList));
		}
		return calls;
	}

	/**
	 * Get all the calls made by the action elements stored in the class unit.
	 * 
	 * @param cu
	 * @return
	 */
	public List<Calls> getCallsInAnEnumeratedType(Datatype enumType) {
		// get all the action elements of the class
		List<ActionElement> actionsInTheClassUnit = ActionElementsExtractor
				.getInstance().getActionsElementsFromDatatype(enumType);
		// get all the calls made by the action elements stored in the class
		// unit
		List<Calls> calls = new ArrayList<Calls>();
		List<Calls> undesiredCallsList = new ArrayList<Calls>();

		for (ActionElement action : actionsInTheClassUnit) {
			calls.addAll(getOutgoingCalls(enumType, action, undesiredCallsList));
		}

		return calls;
	}

}
