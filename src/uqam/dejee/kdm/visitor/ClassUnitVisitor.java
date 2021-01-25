/**
 * 
 */
package uqam.dejee.kdm.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;

/**
 * @author Anas Shatnawi Email: anasshatnawi@gmail.com 2017
 *
 *         This class is designed to identify the set of methods that are
 *         existed in a given class
 */
public class ClassUnitVisitor {

	/**
	 * this method recieves an instance of ClassUnit and returns the set of
	 * methods (MethodUnit) that are implemented in this class
	 * 
	 * @param classUnit
	 * @return
	 */
	public static List<MethodUnit> identifyMethodsfromClass(ClassUnit classUnit) {
		List<MethodUnit> methods = new ArrayList<>();
		EList<CodeItem> codeItems = classUnit.getCodeElement();
		for (CodeItem codeItem : codeItems) {
			if (codeItem instanceof MethodUnit) {
				MethodUnit internalMethod = (MethodUnit) codeItem;
				methods.add(internalMethod);
			}
		}
		return methods;
	}

}
