/**
 * 
 */
package uqam.dejee.slicer.pdg.graph.structure;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;

/**
 * @author Anas Shatnawi Email: anasshatnawi@gmail.com 2017
 *
 * 
 */
public class Node {

	AbstractCodeElement element;
	// leftside variables
	List<AbstractCodeElement> leftSide = new ArrayList<>();

	// rightside variables
	List<AbstractCodeElement> rightSide = new ArrayList<>();

	List<AbstractCodeElement> calledMethods = new ArrayList<>();

	List<AbstractCodeElement> methodParameters = new ArrayList<>();

	AbstractCodeElement onwerObjectOfMethodInvocations;

	ArrayList<Node> fanIn = new ArrayList<Node>();
	ArrayList<Node> fanOut = new ArrayList<Node>();

	public Node(AbstractCodeElement element) {
		this.element = element;
	}

	public AbstractCodeElement getOnwerObjectOfMethodInvocations() {
		return onwerObjectOfMethodInvocations;
	}

	public void setOnwerObjectOfMethodInvocations(AbstractCodeElement onwerObjectOfMethodInvocations) {
		this.onwerObjectOfMethodInvocations = onwerObjectOfMethodInvocations;
	}

	public AbstractCodeElement getElement() {
		return element;
	}

	public List<AbstractCodeElement> getLeftSide() {
		return leftSide;
	}

	public List<AbstractCodeElement> getRightSide() {
		return rightSide;
	}

	public void setElement(AbstractCodeElement element) {
		this.element = element;
	}

	public void addMethodParameter(AbstractCodeElement param) {
		methodParameters.add(param);
	}

	public void addMethodManyParameter(List<AbstractCodeElement> params) {
		methodParameters.addAll(params);
	}

	public List<AbstractCodeElement> getMethodParameters() {
		return methodParameters;
	}

	public void addLeftSideElement(AbstractCodeElement variable) {
		leftSide.add(variable);
	}

	public void addRightSideElement(AbstractCodeElement variable) {
		rightSide.add(variable);
	}

	public void addFanIn(Node element) {
		fanIn.add(element);
	}

	public void addFanOut(Node element) {
		fanOut.add(element);
	}

	public ArrayList<Node> getFanIn() {
		return fanIn;
	}

	public ArrayList<Node> getFanOut() {
		return fanOut;
	}

	@Override
	public String toString() {
		String ret = new String();

		ret += "\nNode[" + element.getName() + "] \n";
		ret += "Left variables: ";
		for (AbstractCodeElement v : leftSide) {
			ret += v.getName() + " ";
		}
		ret += "\nRigth variables: ";
		for (AbstractCodeElement v : rightSide) {
			ret += v.getName() + " ";
		}
		ret += "\nMethod Parameters: ";
		for (AbstractCodeElement v : methodParameters) {
			ret += v.getName() + " ";
		}
		ret += "\nFanIn: ";
		for (Node m : fanIn) {
			ret += m.element.getName() + " ";
		}
		ret += "\nFanOut: ";
		for (Node m : fanOut) {
			ret += m.element.getName() + " ";
		}
		ret += "\n";

		return ret;
	}
}