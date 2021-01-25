/**
 * 
 */
package uqam.dejee.slicer;

import java.util.List;

import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;

/**
 * @author Anas Shatnawi Email: anasshatnawi@gmail.com 2017
 *
 * 
 */
public class PrintAllActionElements {

	public static void printActionElements(List<ActionElement> elements) {
		for (ActionElement element : elements) {
			printOneActionElement(element);
		}
	}

	public static void printOneActionElement(ActionElement element) {
		System.err.println("Action element name: " + element.getName());
	}
}
