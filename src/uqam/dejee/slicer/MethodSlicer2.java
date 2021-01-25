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
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

import realeity.technique.extractor.codemodel.entities.ControlElementsExtractor;
import uqam.dejee.kdm.visitor.KdmExtractors;
import uqam.dejee.slicer.pdg.graph.structure.DependnecyGraph;
import uqam.dejee.slicer.pdg.graph.structure.Node;

public class MethodSlicer2 {

	private static MethodSlicer2 uniqueInstance;

	/**
	 * 
	 */
	private MethodSlicer2() {
		// TODO Auto-generated constructor stub
	}

	public static MethodSlicer2 getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new MethodSlicer2();
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
		Node rootNode = new Node(rootBU);
		callGraph.addNode(rootNode);

		List<AbstractCodeElement> actionList = getActionElementsFromActionElement(rootBU);
		for (AbstractCodeElement elem : actionList) {
			String elemName = elem.getName();
			System.err.println("\telem :" + elem);
			// System.err.println("\t" + elemName);
			if (elemName != null) {
				Node node = new Node((ActionElement) elem);
//				node.addFanOut(rootBU);
				callGraph.addNode(node);
				if (elemName.equals("variable declaration")) {
					evaluateVarDecActionElement((ActionElement) elem, node);
				} else if (elemName.equals("expression statement")) {
					evaluateExpressionStatmentActionElement((ActionElement) elem, node);
				} else if (elemName.equals("if")) {

				} else if (elemName.equals("for")) {

				} else if (elemName.equals("while")) {

				}
			}
		}
		System.out.println(callGraph.toString());

	}

	public void evaluateExpressionStatmentActionElement(ActionElement expression, Node node) {
		EList<AbstractCodeElement> codeElements = expression.getCodeElement();
		for (AbstractCodeElement ce : codeElements) {
			ActionElement ceAE = (ActionElement) ce;
			if (ce.getName().equals("ASSIGN")) {
				List<AbstractActionRelationship> actionRelations = ceAE.getActionRelation();
				for (AbstractActionRelationship relation : actionRelations) {
					System.err.println("\t\t\t" + relation);
					if (relation instanceof Writes) {
						KDMEntity targetEntity = relation.getTo();
						if (targetEntity instanceof StorableUnit) {
							// variable :)
//							System.err.println("target Variable:" + targetEntity.getName());
							node.addLeftSideElement((StorableUnit) targetEntity);
						}
					} else if (relation instanceof Reads) {
						KDMEntity sourceEntity = relation.getTo();
						if (sourceEntity instanceof StorableUnit) {
							// variable :)
//							System.err.println("Source Variable:" + sourceEntity.getName());
							node.addRightSideElement((StorableUnit) sourceEntity);
						}
					}
				}
			} else {
				System.err.println("\nelse....");
				List<AbstractCodeRelationship> actionRelations = ce.getCodeRelation();
				System.err.println("\t\t\tce:" + ce);
				System.err.println("\t\t\telse actionRelations size" + ce.getCodeRelation().size());
				System.err.println("\t\t\telse elements: " + ce.getOwnedElement().size());
				System.err.println("\t\t\telse rec: " + ce.getOwnedRelation().size());
			}
		}
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
//						System.err.println("sourceEntity :" + sourceEntity.getName());
						if (sourceEntity instanceof StorableUnit) {
							// variable :)
							node.addRightSideElement((StorableUnit) sourceEntity);
						} else if (sourceEntity instanceof ActionElement) {
							// equation :(
							System.err.println("ActionElement:" + sourceEntity.getName());
						} else {
							// System.err.println("Non of them");
						}
					}
				}
			}
		}
	}

	public void addFanOut(DependnecyGraph callGraph, List<ActionElement> actionList) {

	}

}
