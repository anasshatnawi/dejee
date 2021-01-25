/**
 * 
 */
package uqam.dejee.slicer.pdg.graph.structure;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;

/**
 * @author Anas Shatnawi Email: anasshatnawi@gmail.com 2017
 *
 * 
 */
public class DependnecyGraph {

	public ArrayList<Node> nodes = new ArrayList<Node>();
	public Hashtable<String, Node> elementNode = new Hashtable<String, Node>();
	private static DependnecyGraph instance;

	private DependnecyGraph() {

	}

	public static DependnecyGraph getInstance() {
		if (instance == null) {
			instance = new DependnecyGraph();
		}
		return instance;
	}

	public void addNode(ActionElement element) {
		Node node = new Node(element);
		nodes.add(node);
		elementNode.put(element.getName(), node);
	}

	public void addNode(Node node) {
		nodes.add(node);
		if (node.getElement().getName() == null) {
			elementNode.put("withoutName", node);
		} else {
			elementNode.put(node.getElement().getName(), node);
		}
	}

	public Node getNode(String m) {
		return elementNode.get(m);
	}

	public Node getNode(ActionElement m) {
		return elementNode.get(m.getName());
	}

	@Override
	public String toString() {
		String ret = new String();
		for (Node node : nodes) {
			ret += node.toString() + "\n";
		}
		return ret;
	}

	public void calculateFanOut() {
		for (Node node : nodes) {
			AbstractCodeElement fanin = node.element;
			List<AbstractCodeElement> leftSide = node.getLeftSide();
			for (Node nextNode : nodes) {
				List<AbstractCodeElement> rightSide = nextNode.getRightSide();
				if (node == nextNode ) {
					continue;
				}
				if (rightSide.size()==0){
					continue;
				}
				if (leftSide.size()==0){
					continue;
				}
				List<AbstractCodeElement> intersection = new ArrayList<>();
//				System.err.println("Left side: " + leftSide);
//				System.err.println("Right side: " + rightSide);
				intersection.addAll(rightSide);
				intersection.retainAll(leftSide);
//				System.err.println("Intersection: " + intersection);
				if (intersection.size() > 0) {
					// so there is data dependency between the two nodes
					System.err.println("+++++++++++++++++++++++++++++++++++++We have overlap");
					node.addFanIn(nextNode);
					nextNode.addFanOut(node);
				}
			}
		}
	}

	public void calculateFanIn2() {
		for (Node node : nodes) {
			AbstractCodeElement fanin = node.element;
			for (Node m : node.getFanOut()) {
//				getNode(m.getName()).addFanIn(fanin);
			}
		}
	}

}
