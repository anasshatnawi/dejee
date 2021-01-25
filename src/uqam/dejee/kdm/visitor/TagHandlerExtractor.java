/**
 * 
 */
package uqam.dejee.kdm.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Extends;
import org.eclipse.gmt.modisco.omg.kdm.code.Implements;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

/**
 * @author Anas Shatnawi Email: anasshatnawi@gmail.com 2017
 *
 *         This class is to deal with tag handler class units
 */
public class TagHandlerExtractor {

	private static TagHandlerExtractor uniqueInstance;

	private final String tagHandlerClasses [] = { "TagSupport"};
	private final String tagHandlerInterfaces[] = { "IterationTag"};

	
	/**
	 * 
	 */
	private TagHandlerExtractor() {
		// TODO Auto-generated constructor stub
	}

	public static TagHandlerExtractor getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new TagHandlerExtractor();
		}
		return uniqueInstance;
	}

	/**
	 * This method identifies the set of classes related to tag handlers from a
	 * given set of class units
	 * 
	 * @param sourceClassUnits
	 * @return
	 */
	public List<ClassUnit> identifyTagHandlers(List<ClassUnit> sourceClassUnits) {
		List<ClassUnit> resultsClassUnits = new ArrayList<>();
		for (ClassUnit classUnit : sourceClassUnits) {
			EList<AbstractCodeRelationship> relations = classUnit.getCodeRelation();
			for (AbstractCodeRelationship relation : relations) {
				if (relation instanceof Implements) {
					KDMEntity entiity = relation.getTo();
					if (entiity instanceof InterfaceUnit){
						String interfaceName= entiity.getName();
						if (interfaceName.equals("IterationTag")){
							resultsClassUnits.add(classUnit);
						}
					}
					
				} 
				else if (relation instanceof Extends) {
					KDMEntity entiity = relation.getTo();
					if (entiity instanceof ClassUnit){
						String className= entiity.getName();
						if (isFoundTagHandelrClass(className)){
							resultsClassUnits.add(classUnit);
						}
					}
				}
			}
		}

		return resultsClassUnits;
	}
	
	private boolean isFoundTagHandelrClass(String name){
		for (int i = 0; i < tagHandlerClasses.length; i++){
			if (name.equals(tagHandlerClasses[i])){
				return true;
			}
		}
		return false;
	}
	
	private boolean isFoundTagHandelrInterface(String name){
		for (int i = 0; i < tagHandlerInterfaces.length; i++){
			if (name.equals(tagHandlerInterfaces[i])){
				return true;
			}
		}
		return false;
	}

}
