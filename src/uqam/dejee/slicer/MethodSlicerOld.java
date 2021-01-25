package uqam.dejee.slicer;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.HasValue;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.StorableUnit;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

import uqam.dejee.kdm.visitor.KdmExtractors;
import uqam.dejee.slicer.pdg.graph.structure.DependnecyGraph;
import uqam.dejee.slicer.pdg.graph.structure.Node;

public class MethodSlicerOld {

	private static MethodSlicerOld uniqueInstance;

	/**
	 * 
	 */
	private MethodSlicerOld() {
		// TODO Auto-generated constructor stub
	}

	public static MethodSlicerOld getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new MethodSlicerOld();
		}
		return uniqueInstance;
	}

	public void sliceMethod(MethodUnit method) {
		System.err.println("Slicing a method " + method.getName());
		List<ActionElement> actionList = KdmExtractors.getInstance().extractSequenceofActionElements(method);

		// fill graph vertics
		DependnecyGraph callGraph = DependnecyGraph.getInstance();
		for (ActionElement elem : actionList) {
			String elemName = elem.getName();
			System.err.println("\t" + elemName);
			if (elemName != null) {
				Node node = new Node(elem);
				callGraph.addNode(node);
				if (elemName.equals("variable declaration")) {
					// EList<AbstractCodeElement> codeElements
					// =elem.getCodeElement();
					// for (AbstractCodeElement ce :codeElements){
					// System.err.println(ce.toString());
					// }
					evaluateVarDecActionElement(elem, node);
				} else if (elemName.equals("expression statement")) {

				} else if (elemName.equals("if")) {

				} else if (elemName.equals("for")) {

				} else if (elemName.equals("while")) {

				}
			}
		}
		System.out.println(callGraph.toString());

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
						System.err.println("sourceEntity :" + sourceEntity.getName());
						if (sourceEntity instanceof StorableUnit) {
							// variable :)
							System.err.println("Source Variable:" + sourceEntity.getName());
							node.addRightSideElement((StorableUnit) sourceEntity);
						} else if (sourceEntity instanceof ActionElement) {
							// equation :(
							System.err.println("ActionElement:" + sourceEntity.getName());
						} else {
							System.err.println("Non of them");
						}
					}
				}
			}
		}
	}

	public void addFanOut(DependnecyGraph callGraph, List<ActionElement> actionList) {

	}

}
