package realeity.technique.extractor.codemodel.entities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.code.TemplateUnit;

import realeity.technique.extractor.AbstractKDMEntityExtractor;


/**
 * 
 * @author Alvine
 *
 */
public class InterfaceUnitExtractor extends AbstractKDMEntityExtractor {
	
	/**
	 * Class's constructor. 
	 * Its visibility is private in order not to allow the other classes to create 
	 * many instances of LayeringFacade.
	 */
	private InterfaceUnitExtractor(){
		
	}
	
	/**
	 * unique instance of LayeringFacade.
	 */
	private static InterfaceUnitExtractor uniqueInstance;
	
	/**
	 * Method insuring that a unique instance of InterfaceUnitExtractor is created.
	 * @return uniqueInstance
	 */
	public static InterfaceUnitExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new InterfaceUnitExtractor();
		}
		return uniqueInstance;
	}
	/**
	 * Extract all the ClassUnit contained in one package.
	 * @param pack
	 * @return
	 */
	public List<InterfaceUnit> extractIU (Package pack){
		List<InterfaceUnit> iUsList = new ArrayList<InterfaceUnit>();
		List<TemplateUnit> tUsList = new ArrayList<TemplateUnit>();
		EList<AbstractCodeElement>  abstractCodeElements = pack.getCodeElement();
		for(AbstractCodeElement abstractCode : abstractCodeElements){
			if(abstractCode instanceof InterfaceUnit)
				iUsList.add((InterfaceUnit)abstractCode);
			else if (abstractCode instanceof TemplateUnit)
				tUsList.add((TemplateUnit)abstractCode);
		}
		
		//contain all the interface units nested/contained in a template unit
		List<InterfaceUnit> nestedIUsList = new ArrayList<InterfaceUnit>();
		for(TemplateUnit tu : tUsList)
		{
			nestedIUsList.addAll(extractNestedInterfaceUnit(tu));
		}
		iUsList.addAll(nestedIUsList);
		return iUsList;
	}
	
	/**
	 * Returns all the interface units nested in a template unit.
	 * @param templateUnit
	 * @return
	 */
	private List<InterfaceUnit> extractNestedInterfaceUnit(TemplateUnit templateUnit){
		List<InterfaceUnit> nestedIUList = new ArrayList<InterfaceUnit>();
		EList<CodeItem> codeItems = templateUnit.getCodeElement();;
		for(CodeItem codeItem: codeItems){
			if(codeItem instanceof InterfaceUnit){
				InterfaceUnit internalIU = (InterfaceUnit) codeItem;
				nestedIUList.add(internalIU);
			}
			else if(codeItem instanceof TemplateUnit){
				TemplateUnit internalTu = (TemplateUnit) codeItem;
				nestedIUList.addAll(extractNestedInterfaceUnit(internalTu));

			}
		}
		return nestedIUList;
	}
	
	/**
	 * Get all the interfaceUnits (interfaces) contained in the list of packages relevant 
	 * for the system.
	 * @param segment
	 * @return
	 */
	 public List<InterfaceUnit> getInterfaceUnits(List<Package> packageList){
		List<InterfaceUnit> cUsList = new ArrayList<InterfaceUnit>();
		for(Package pack : packageList){
			cUsList.addAll(extractIU(pack));		
		}
		return cUsList;
	}
}
