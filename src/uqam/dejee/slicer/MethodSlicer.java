package uqam.dejee.slicer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.action.AbstractActionRelationship;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.action.Reads;
import org.eclipse.gmt.modisco.omg.kdm.action.Writes;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.HasValue;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.StorableUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Value;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

import realeity.technique.extractor.codemodel.entities.ControlElementsExtractor;
import uqam.dejee.kdm.visitor.KdmExtractors;
import uqam.dejee.slicer.pdg.graph.structure.DependnecyGraph;
import uqam.dejee.slicer.pdg.graph.structure.Node;

public class MethodSlicer {

	private static MethodSlicer uniqueInstance;

	/**
	 * 
	 */
	private MethodSlicer() {
		// TODO Auto-generated constructor stub
	}

	public static MethodSlicer getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new MethodSlicer();
		}
		return uniqueInstance;
	}

	public BlockUnit getBlockUnitofMehtodUnit(MethodUnit methodUnit) {
		List<AbstractCodeElement> codeElements = new ArrayList<AbstractCodeElement>();
		codeElements = methodUnit.getCodeElement();
		for (AbstractCodeElement codeElement : codeElements) {
			if (codeElement instanceof BlockUnit) {
				return (BlockUnit) codeElement;
			}
		}
		return null;
	}

	public List<AbstractCodeElement> getActionElementsFromActionElement(ActionElement actionElement) {
		List<AbstractCodeElement> codeElements = new ArrayList<AbstractCodeElement>();
		codeElements = actionElement.getCodeElement();
		return codeElements;
	}

	public void sliceMethod(MethodUnit method) {
		System.err.println("Slicing a method " + method.getName());
		BlockUnit rootBU = getBlockUnitofMehtodUnit(method);

		// fill graph vertics
		DependnecyGraph callGraph = DependnecyGraph.getInstance();
		rootBU.setName("EntryNode");
		Node rootNode = new Node(rootBU);
		callGraph.addNode(rootNode);
		buildGraph(rootBU, callGraph, rootNode);

		callGraph.calculateFanOut();
		System.out.println(callGraph.toString());

	}

	public void buildGraph(AbstractCodeElement actionElement, DependnecyGraph callGraph, Node rootNode) {
		List<AbstractCodeElement> codeElements = new ArrayList<AbstractCodeElement>();
		codeElements = ((ActionElement) actionElement).getCodeElement();
		for (AbstractCodeElement elem : codeElements) {
			String elemName = elem.getName();
			Node node = new Node(elem);
			node.addFanOut(rootNode);
			callGraph.addNode(node);
			if (elemName != null) {
				if (elemName.equals("variable declaration")) {
					buildGraph(elem, callGraph, node);
					evaluateVarDecActionElement((ActionElement) elem, node);
				} else if (elemName.equals("expression statement")) {
					buildGraph(elem, callGraph, node);
					evaluateExpressionStatmentActionElement((ActionElement) elem, node);
				} else if (elemName.equals("if")) {
					buildGraph(elem, callGraph, node);
				} else if (elemName.equals("for")) {
					buildGraph(elem, callGraph, node);
				} else if (elemName.equals("while")) {
					buildGraph(elem, callGraph, node);
				} else if (elem instanceof BlockUnit) {
					buildGraph(elem, callGraph, node);
				}
			} else {
				AbstractCodeElement container = (AbstractCodeElement) elem.eContainer();
				elem.setName(container.getName());
				buildGraph(elem, callGraph, node);
			}
		}

	}

	public void evaluateExpressionStatmentActionElement(ActionElement expression, Node node) {
		EList<AbstractCodeElement> codeElements = expression.getCodeElement();
		for (AbstractCodeElement ce : codeElements) {
			ActionElement ceAE = (ActionElement) ce;
			if (ce.getName().equals("ASSIGN")) {
				List<AbstractActionRelationship> actionRelations = ceAE.getActionRelation();
				for (AbstractActionRelationship relation : actionRelations) {
					if (relation instanceof Writes) {
						KDMEntity targetEntity = relation.getTo();
						if (targetEntity instanceof StorableUnit) {
							// variable :)
							node.addLeftSideElement((StorableUnit) targetEntity);
						}
					} else if (relation instanceof Reads) {
						KDMEntity sourceEntity = relation.getTo();
						if (sourceEntity instanceof StorableUnit) {
							// variable :)
							node.addRightSideElement((StorableUnit) sourceEntity);
						}
					}
				}
			} else if (ce.getName().equals("method invocation")) {
				parseMethodInvocation(ce, node);
			} else {
				// System.err.println("\nelse....");
				// ActionElement actionElemeent = (ActionElement) ce;
				// List<AbstractActionRelationship> actionRelations =
				// actionElemeent.getActionRelation();
				// System.err.println("\t\t\tce:" + ce);
				// System.err.println("\t\t\telse actionRelations size" +
				// actionElemeent.getActionRelation().size());
			}
		}
	}

	public void parseMethodInvocation(AbstractCodeElement methodInvocation, Node node) {
		ActionElement actionElement = (ActionElement) methodInvocation;
		List<AbstractCodeElement> codeElements = actionElement.getCodeElement();
		for (AbstractCodeElement element : codeElements) {
			if ((element instanceof Value) || (element instanceof StorableUnit)
					|| (element instanceof ActionElement && element.getName().equals("method invocation"))) {
				node.addMethodParameter(element);
			} else {
				node.addMethodManyParameter(fromMathActionElementToPrimitiveActionElement(element));
			}
		}
	}

	/**
	 * To be implimneted!
	 * 
	 * @param methodInvocation
	 * @return
	 */
	public AbstractCodeElement extractObjectOnewrOfMethod(AbstractCodeElement methodInvocation) {
		AbstractCodeElement object = null;
		ActionElement actionElement = (ActionElement) methodInvocation;
		List<AbstractActionRelationship> codeElements = actionElement.getActionRelation();
		return object;
	}

	/**
	 * This method receives an element related to mathmatical expresion and
	 * extract the set of variables and method invocations that compose it
	 * 
	 * @param mathActionElement
	 * @return
	 */
	public List<AbstractCodeElement> fromMathActionElementToPrimitiveActionElement(
			AbstractCodeElement mathActionElement) {
		List<AbstractCodeElement> primitiveElements = new ArrayList<>();
		ActionElement actionElement = (ActionElement) mathActionElement;
		List<AbstractCodeElement> elements = actionElement.getCodeElement();
		for (AbstractCodeElement element : elements) {
			if ((element instanceof Value) || (element instanceof StorableUnit)
					|| (element instanceof ActionElement && element.getName().equals("method invocation"))) {
				primitiveElements.add(element);
			} else {
				primitiveElements.addAll(fromMathActionElementToPrimitiveActionElement(element));
			}
		}
		return primitiveElements;
	}

	public void evaluateAssignElement(AbstractCodeElement assign) {

	}

	public void evaluateVarDecActionElement(ActionElement varDec, Node node) {
		EList<AbstractCodeElement> codeElements = varDec.getCodeElement();
		for (AbstractCodeElement ce : codeElements) {
			if (ce instanceof StorableUnit) {
				node.addLeftSideElement((StorableUnit) ce);
				EList<AbstractCodeRelationship> relations = ce.getCodeRelation();
				for (AbstractCodeRelationship codeRelation : relations) {
					if (codeRelation instanceof HasValue) {
						KDMEntity sourceEntity = codeRelation.getTo();
						// System.err.println("sourceEntity :" +
						// sourceEntity.getName());
						if (sourceEntity instanceof StorableUnit) {
							// variable :)
							node.addRightSideElement((StorableUnit) sourceEntity);
						} else if (sourceEntity instanceof ActionElement) {
							// equation :(
							System.err.println("ActionElement:" + sourceEntity.getName());
							if (sourceEntity.getName().equals("method invocation")) {
								node.addRightSideElement((AbstractCodeElement) sourceEntity);
								System.err.println("\tWe added: " + sourceEntity.getName());


							} else {
								List<AbstractCodeElement> elements = fromMathActionElementToPrimitiveActionElement(
										(AbstractCodeElement) sourceEntity);
								for (AbstractCodeElement element : elements) {
									System.err.println("\tWe added: " + element.getName());
									node.addRightSideElement(element);

								}
							}
						} else {
							// System.err.println("Non of them");
						}
					}
				}
			}
		}
	}

	public void addFanOut(DependnecyGraph callGraph) {

	}

}
