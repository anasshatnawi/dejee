/**
 * 
 */
package uqam.dejee.slicer;

import java.util.List;

import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;

/**
 * @author Anas Shatnawi Email: anasshatnawi@gmail.com 2017
 *
 * 
 */
public class TagHandlerSlicer extends Slicer {

	private static TagHandlerSlicer uniqueInstance;

	private List<MethodUnit> methods;
	/**
	 * 
	 */
	private TagHandlerSlicer() {
		// TODO Auto-generated constructor stub
	}

	public static TagHandlerSlicer getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new TagHandlerSlicer();
		}
		return uniqueInstance;
	}


	
}
