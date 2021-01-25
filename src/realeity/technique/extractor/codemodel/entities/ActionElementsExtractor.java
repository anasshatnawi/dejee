package realeity.technique.extractor.codemodel.entities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.CatchUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.FinallyUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.TryUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.ControlElement;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.HasValue;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.StorableUnit;

import realeity.technique.extractor.AbstractKDMEntityExtractor;

/**
 * Retrieves the action elements from a datatype i.e class unit, interface unit
 * or enumerated type(enum).
 * 
 * @author Alvine
 *
 */
public class ActionElementsExtractor extends AbstractKDMEntityExtractor {

	/**
	 *
	 * Constructor of the class ActionElementsExtractor. Its visibility is
	 * private in order not to allow the other classes to create many instances
	 * of ActionElementsExtractor
	 */
	private ActionElementsExtractor() {

	}

	/**
	 * unique instance of ActionElementsExtractor.
	 */
	private static ActionElementsExtractor uniqueInstance;

	/**
	 * Method insuring that a unique instance of ActionElementsExtractor is
	 * created.
	 * 
	 * @return uniqueInstance
	 */
	public static ActionElementsExtractor getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new ActionElementsExtractor();
		}
		return uniqueInstance;
	}

	/**
	 * Get the actions elements corresponding to the declaration of the global
	 * variables of a datatype (class/interface unit)
	 * 
	 * @param datatype
	 * @return
	 */
	public List<ActionElement> getActionsInDatatypeVariableDeclaration(Datatype datatype) {
		// get all the storable units corresponding to the global variables in
		// the cu
		List<StorableUnit> storableUnitList = new ArrayList<StorableUnit>();
		EList<CodeItem> codeItems = new BasicEList<CodeItem>();
		if (datatype instanceof ClassUnit)
			codeItems = ((ClassUnit) datatype).getCodeElement();
		if (datatype instanceof InterfaceUnit)
			codeItems = ((InterfaceUnit) datatype).getCodeElement();
		for (CodeItem codeItem : codeItems) {
			if (codeItem instanceof StorableUnit)
				storableUnitList.add((StorableUnit) codeItem);
		}

		List<ActionElement> globalActions = new ArrayList<ActionElement>();
		for (StorableUnit storableUnit : storableUnitList) {
			ActionElement actionsInStorableUnit = getActionsInStorableUnits(storableUnit);
			if (actionsInStorableUnit != null) {
				globalActions.add(actionsInStorableUnit);
				globalActions.addAll(getActionElementsFromABlock(actionsInStorableUnit, new ArrayList<Datatype>()));
			}
		}
		return globalActions;

	}

	/**
	 * Get the action elements inside a class unit ie, the action elements
	 * inside its internal classes, inside its method units and those inside its
	 * anonymous classes. NB: inside a method we can have storable units
	 * (containing actions elements) or recursively : actions elements
	 * containing storable units
	 * 
	 * @param cu
	 * @return
	 */
	public List<ActionElement> getActionsInClassUnit(ClassUnit cuFrom, List<Datatype> dataForCatchConnections) {
		List<ActionElement> actionList = new ArrayList<ActionElement>();
		// get the actions elements in the internal classes
		List<ClassUnit> nestedClasses = ClassUnitExtractor.getInstance().getTheInternalClassesInTheClassUnit(cuFrom);
		for (ClassUnit nestedClass : nestedClasses) {
			actionList.addAll(getActionsInClassUnit(nestedClass, dataForCatchConnections));
		}

		// get the actions elements inside the method units
		List<ControlElement> muList = ControlElementsExtractor.getInstance().getControlElementsFor1CU(cuFrom);
		for (ControlElement mu : muList) {
			List<BlockUnit> buList = ControlElementsExtractor.getInstance().getBlockUnits(mu);
			for (BlockUnit bu : buList) {
				actionList.addAll(getActionElementsFromABlock(bu, dataForCatchConnections));
			}

		}
		return actionList;
	}

	/**
	 * Get the action elements inside a EnumeratedType ie, the action elements
	 * inside its internal classes, inside its method units and those inside its
	 * anonymous classes. NB: inside a method we can have storable units
	 * (containing actions elements) or recursively : actions elements
	 * containing storable units
	 * 
	 * @param cu
	 * @return
	 */
	public List<ActionElement> getActionsInEnumeratedType(EnumeratedType enumType) {
		List<ActionElement> actionList = new ArrayList<ActionElement>();
		// get the actions elements inside the method units
		List<ControlElement> muList = ControlElementsExtractor.getControlElementFor1EnumeratedType(enumType);
		for (ControlElement mu : muList) {
			List<BlockUnit> buList = ControlElementsExtractor.getInstance().getBlockUnits(mu);
			for (BlockUnit bu : buList) {
				actionList.addAll(getActionElementsFromABlock(bu, new ArrayList<Datatype>()));
			}
		}
		return actionList;
	}

	/**
	 * Retrieve the actionElements in a tryUnit block.
	 * 
	 * @param tryUnit
	 * @param dataForCatchConnections
	 *            is used to compute the catch connections
	 * @return
	 */
	private List<ActionElement> getActionsfromATryUnit(TryUnit tryUnit, List<Datatype> dataForCatchConnections) {
		// TryUnit is a grandson of a BlockUnit
		List<ActionElement> actionList = new ArrayList<ActionElement>();
		EList<AbstractCodeElement> codeElementsForTry = ((ActionElement) tryUnit).getCodeElement();
		for (int indexTry = 0; indexTry < codeElementsForTry.size(); indexTry++) {// there
																					// might
																					// be
																					// several
																					// catch
																					// blocks
																					// after
																					// a
																					// try
			if (codeElementsForTry.get(indexTry) instanceof CatchUnit) {
				CatchUnit cu = (CatchUnit) codeElementsForTry.get(indexTry);
				EList<AbstractCodeElement> codeElementsForCatch = cu.getCodeElement();
				BlockUnit catchBlockSon = (BlockUnit) codeElementsForCatch.get(0);
				List<ActionElement> actionFromCatchList = getActionElementsFromABlock(catchBlockSon,
						dataForCatchConnections);
				// System.out.println("nombre d'actions elements dans le catch
				// :"+actionFromCatchList.size());
				actionList.addAll(actionFromCatchList);
				StorableUnit storableInCatchHeader = (StorableUnit) codeElementsForCatch.get(1);
				dataForCatchConnections.add(storableInCatchHeader.getType());
			} else if (codeElementsForTry.get(indexTry) instanceof FinallyUnit) {
				FinallyUnit fu = (FinallyUnit) codeElementsForTry.get(indexTry);
				EList<AbstractCodeElement> codeElementsForFinally = fu.getCodeElement();
				BlockUnit finallyBlockSon = (BlockUnit) codeElementsForFinally.get(0);
				List<ActionElement> actionFromFinallyList = getActionElementsFromABlock(finallyBlockSon,
						dataForCatchConnections);
				actionList.addAll(actionFromFinallyList);
			} else if (codeElementsForTry.get(indexTry) instanceof BlockUnit) {
				BlockUnit buTry = (BlockUnit) codeElementsForTry.get(indexTry);
				actionList.addAll(getActionElementsFromABlock(buTry, dataForCatchConnections));
			}
		}
		return actionList;
	}

	/**
	 * Returns recursively all the action elements contained in a blockunit. A
	 * blockunit is an action element and it can contain many action elements.
	 * and these action elements can contain block units too, and so on.
	 * 
	 * @param pack
	 * @param dataForCatchConnections
	 * @return
	 */
	public List<ActionElement> getActionElementsFromABlock(ActionElement bu, List<Datatype> dataForCatchConnections) {

		List<ActionElement> actionList = new ArrayList<ActionElement>();
		EList<AbstractCodeElement> abstractCodeElements = bu.getCodeElement();

		for (AbstractCodeElement codeElement : abstractCodeElements) {
			if (codeElement instanceof TryUnit) {// a TryUnit is a grandson of a
													// BlockUnit
				actionList.addAll(getActionsfromATryUnit((TryUnit) codeElement, dataForCatchConnections));
			} else if (codeElement instanceof BlockUnit) {
				BlockUnit buSon = (BlockUnit) codeElement;
				List<ActionElement> internalActionList = getActionElementsFromABlock(buSon, dataForCatchConnections);
				actionList.addAll(internalActionList);// checks whether there is
														// other blocks inside
														// the blockUnit buSon
			} else if (codeElement instanceof ActionElement) {
				actionList.add((ActionElement) codeElement);
				EList<AbstractCodeElement> abstractCodeSons = ((ActionElement) codeElement).getCodeElement();
				for (AbstractCodeElement codeSon : abstractCodeSons) {// an
																		// action
																		// element
																		// can
																		// contain
																		// other
																		// action
																		// elements
					if (codeSon instanceof ActionElement) {
						ActionElement internalAction = (ActionElement) codeSon;
						actionList.add(internalAction);
						actionList.addAll(getActionElementsFromABlock(internalAction, dataForCatchConnections));
					} else if (codeSon instanceof ClassUnit) {// an action
																// element can
																// contain
																// anonymous/internal
																// classes
																// because an
																// internal
																// class can
																// also be
																// declared
																// inside a
																// class unit
						ClassUnit internalClass = (ClassUnit) codeSon;
						actionList.addAll(getActionsInClassUnit(internalClass, dataForCatchConnections));
					} else if (codeSon instanceof StorableUnit) {
						StorableUnit storableUnit = (StorableUnit) codeSon;
						ActionElement actionIStorableUnit = getActionsInStorableUnits(storableUnit);
						if (actionIStorableUnit != null) {
							actionList.add(actionIStorableUnit);
							actionList
									.addAll(getActionElementsFromABlock(actionIStorableUnit, dataForCatchConnections));
						}
					}
				}
			} else if (codeElement instanceof StorableUnit) {
				StorableUnit storableUnit = (StorableUnit) codeElement;
				ActionElement actionIStorableUnit = getActionsInStorableUnits(storableUnit);
				if (actionIStorableUnit != null)
					actionList.addAll(getActionElementsFromABlock(actionIStorableUnit, dataForCatchConnections));
			} else if (codeElement instanceof ClassUnit) {// an action element
															// can contain
															// anonymous/internal
															// classes
				// because an internal class can also be declared inside a class
				// unit
				ClassUnit internalClass = (ClassUnit) codeElement;
				actionList.addAll(getActionsInClassUnit(internalClass, dataForCatchConnections));
			}
		}
		return actionList;
	}

	/**
	 * Returns recursively all the action elements contained in a blockunit. A
	 * blockunit is an action element and it can contain many action elements.
	 * and these action elements can contain block units too, and so on.
	 * 
	 * @param pack
	 * @return
	 */
	static List<ActionElement> getActionElementsFromABlock(ActionElement bu, List<String> actionNames,
			List<String> actionNamesInStorableUnits) {

		List<ActionElement> actionList = new ArrayList<ActionElement>();
		EList<AbstractCodeElement> abstractCodeElements = bu.getCodeElement();

		for (AbstractCodeElement codeElement : abstractCodeElements) {
			if (codeElement instanceof ActionElement) {
				EList<AbstractCodeElement> abstractCodeSons = ((ActionElement) codeElement).getCodeElement();
				for (AbstractCodeElement codeSon : abstractCodeSons) {
					if (codeSon instanceof BlockUnit) {
						BlockUnit buSon = (BlockUnit) codeSon;
						List<ActionElement> internalActionList = getActionElementsFromABlock(buSon, actionNames,
								actionNamesInStorableUnits);
						actionList.addAll(internalActionList);// checks whether
																// there is
																// other blocks
																// inside the
																// blockUnit
																// buSon
					} else if (codeSon instanceof ActionElement) {
						List<ActionElement> internalActionList = getInternalActions((ActionElement) codeSon,
								actionNames);
						actionList.addAll(internalActionList);
					} else if (codeSon instanceof StorableUnit) {
						List<StorableUnit> storableUnits = new ArrayList<StorableUnit>();
						storableUnits.add((StorableUnit) codeSon);
						actionList.addAll(getActionsInStorableUnits(storableUnits, actionNamesInStorableUnits));
					}
				}
			} ///

		}
		return actionList;
	}

	/**
	 * Returns recursively all the actionElements contained in an action
	 * element. In fact, an action element can contain many AbstractCodeElements
	 * and some of these AbstractCodeElements can also be ActionElements.
	 * 
	 * @param pack
	 * @return
	 */
	public static List<ActionElement> getInternalActions(ActionElement action, List<String> actionNames) {
		List<ActionElement> actionList = new ArrayList<ActionElement>();
		// if(actionNames.contains(action.getName()))
		actionList.add(action);
		// un action element de type method invocation peut contenir d'autres
		// actions
		// elements de type method invocation d'où la récursivité
		EList<AbstractCodeElement> abstractCodeElements = action.getCodeElement();
		for (AbstractCodeElement codeElement : abstractCodeElements) {
			if (codeElement instanceof ActionElement) {
				ActionElement internalAction = (ActionElement) codeElement;
				// System.out.println("l'action element interne est :
				// "+internalAction.getName());
				// actionList.add(internalAction);
				actionList.addAll(getInternalActions(internalAction, actionNames));
			} else if (codeElement instanceof StorableUnit) {
				List<StorableUnit> storableUnits = new ArrayList<StorableUnit>();
				storableUnits.add((StorableUnit) codeElement);
				List<ActionElement> actions = getActionsInStorableUnits(storableUnits, actionNames);
				for (ActionElement actionIStorableUnit : actions) {
					actionList.addAll(getInternalActions(actionIStorableUnit, actionNames));
				}
			} else if (codeElement instanceof ClassUnit) {
				// cas d'une classe anonyme et aussi d'une classe interne?: dans
				// ce cas, ne plus extraire les nested classes!!!
				List<ActionElement> actionsInAnonymousClass = ActionElementsExtractor.getInstance()
						.getActionsInClassUnit((ClassUnit) codeElement, new ArrayList<Datatype>());
				actionList.addAll(actionsInAnonymousClass);
			}

		}

		return actionList;
	}

	/**
	 * 
	 * @param storableUnitList
	 * @param actionNames
	 * @return
	 */
	public static List<ActionElement> getActionsInStorableUnits(List<StorableUnit> storableUnitList,
			List<String> actionNames) {
		// get all the hasvalue relationships in each storable unit
		EList<HasValue> hasValueRelations = new BasicEList<HasValue>();
		for (StorableUnit storableUnit : storableUnitList) {
			EList<AbstractCodeRelationship> codeRelations = storableUnit.getCodeRelation();
			for (AbstractCodeRelationship relation : codeRelations) {
				if (relation instanceof HasValue) {
					hasValueRelations.add((HasValue) relation);
				}
			}
		}

		List<ActionElement> globalActions = new ArrayList<ActionElement>();
		for (HasValue hasValueRelation : hasValueRelations) {
			AbstractCodeElement codeElement = hasValueRelation.getTo();
			if (codeElement instanceof ActionElement) {
				ActionElement action = (ActionElement) codeElement;
				globalActions.addAll(getInternalActions(action, actionNames));
				// des actions appelées "cast" peuvent contenir des Calls ou des
				// Creates
			}
		}
		return globalActions;

	}

	/**
	 * the action element inside a storable unit, in case there is one.
	 * 
	 * @param storableUnitList
	 * @param actionNames
	 * @return
	 */
	static ActionElement getActionsInStorableUnits(StorableUnit storableUnit) {
		// get all the hasvalue relationships in each storable unit
		ActionElement actionInStorableUnit = null;
		HasValue hasValueRelation = null;
		EList<AbstractCodeRelationship> codeRelations = storableUnit.getCodeRelation();
		for (AbstractCodeRelationship relation : codeRelations) {
			if (relation instanceof HasValue) {
				hasValueRelation = (HasValue) relation;
			}
		}
		AbstractCodeElement codeElement = null;
		if (hasValueRelation != null) {
			codeElement = hasValueRelation.getTo();
			if (codeElement instanceof ActionElement) {
				actionInStorableUnit = (ActionElement) codeElement;
			}
		}

		return actionInStorableUnit;
	}

	/**
	 * Get all the actions elements inside an EnumeratedType. i.e, those
	 * corresponding to the global variables, nested classes and anonymous
	 * classes.
	 * 
	 * @param cu
	 * @return
	 */
	public List<ActionElement> getActionsFromEnumeratedType(EnumeratedType enumType) {
		List<ActionElement> actionList = new ArrayList<ActionElement>();
		actionList.addAll(getActionsInDatatypeVariableDeclaration(enumType));
		actionList.addAll(getActionsInEnumeratedType(enumType));
		return actionList;
	}

	/**
	 * Get all the actions elements inside a class. ie, those corresponding to
	 * the global variables, nested classes and anonymous classes.
	 * 
	 * @param cu
	 * @return
	 */
	private List<ActionElement> getActionsFromCU(ClassUnit cu) {
		List<ActionElement> actionList = new ArrayList<ActionElement>();
		actionList.addAll(getActionsInDatatypeVariableDeclaration(cu));
		actionList.addAll(getActionsInClassUnit(cu, new ArrayList<Datatype>()));
		return actionList;
	}

	/**
	 * get all the actions elements inside a class. ie, those corresponding to
	 * the global variables anonymous classes
	 * 
	 * @param cu
	 * @return
	 */
	private List<ActionElement> getActionsFromIU(InterfaceUnit iu) {
		List<ActionElement> actionList = new ArrayList<ActionElement>();
		actionList.addAll(getActionsInDatatypeVariableDeclaration(iu));
		// there is no actions inside the methods of an interface unit
		return actionList;
	}

	/**
	 * Gather together all the actions elements contained in the method units of
	 * the class unit
	 * 
	 * @param cu
	 * @return
	 */
	public List<ActionElement> getActionsInMethodUnits(ClassUnit cu, List<String> actionNames,
			List<String> actionNamesInStorableUnits) {
		// get the method units contained in the classUnit
		List<ControlElement> mUList = ControlElementsExtractor.getInstance().getControlElementsFor1CU(cu);
		List<ActionElement> actionsInMethodUnits = new ArrayList<ActionElement>();

		// get all the action element in the method units of the class unit
		for (ControlElement mu : mUList) {
			// get the block units contained in the method unit
			EList<BlockUnit> blockList = ControlElementsExtractor.getInstance().getBlockUnits(mu);
			for (BlockUnit bu : blockList) {
				// get the action elements contained in the bloc unit
				actionsInMethodUnits.addAll(getActionElementsFromABlock(bu, actionNames, actionNamesInStorableUnits));
			}
		} // System.out.println(" nombre d'actionList : "+actionList.size());

		return actionsInMethodUnits;
	}

	/**
	 * Retrieve all the action elements contained in a data type.
	 * 
	 * @return
	 */
	public List<ActionElement> getActionsElementsFromDatatype(Datatype dataType) {
		List<ActionElement> actionList = new ArrayList<ActionElement>();
		String className = dataType.getClass().getName();
		if (className.contains("ClassUnit"))
			actionList.addAll(getActionsFromCU((ClassUnit) dataType));
		else if (className.contains("InterfaceUnit"))
			actionList.addAll(getActionsFromIU((InterfaceUnit) dataType));
		else if (className.contains("EnumeratedType"))
			actionList.addAll(getActionsFromEnumeratedType((EnumeratedType) dataType));

		return actionList;
	}
}
