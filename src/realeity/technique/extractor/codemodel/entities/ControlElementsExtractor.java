package realeity.technique.extractor.codemodel.entities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.ControlElement;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.TemplateUnit;

import realeity.technique.extractor.AbstractKDMEntityExtractor;


/**
 * This class allows to extract all the ControlElements/MethodUnit (java methods) relevant to the analyzed system.
 * @author Alvine Boaye Belle
 *
 */
public class ControlElementsExtractor  extends AbstractKDMEntityExtractor {
	
	 /**
	 * Constructor of the class MethodUnitsExtractor. 
	 * Its visibility is private in order not to allow the other classes to create many instances of MethodUnitsExtractor
	 */
	private ControlElementsExtractor(){
		
	}
	
	/**
	 * unique instance of MethodUnitsExtractor.
	 */
	private static ControlElementsExtractor uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of MethodUnitsExtractor is created.
	 * @return uniqueInstance
	 */
	public static ControlElementsExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new ControlElementsExtractor();
		}
		return uniqueInstance;
	}
	
	/**
	 * Get all the MethodUnits contained in one class unit.
	 * @param segment
	 * @return
	 */
	public List<ControlElement> getControlElementsFor1CU( ClassUnit cu){
		//Get all the CodeItems contained in  cu
		List<CodeItem> codeItems = cu.getCodeElement();
		
		//Get all the MethodUnits contained in the codeItems list
		List<ControlElement> muList = new ArrayList<ControlElement>();		
		for(CodeItem ci : codeItems){
			if((ci instanceof ControlElement) ||(ci instanceof MethodUnit))
				muList.add((ControlElement) ci);
			else if(ci instanceof TemplateUnit){//it can contains method units
				List<CodeItem> codeItemsInTU = ((TemplateUnit)ci).getCodeElement();
				for(int indexElement = 0; indexElement < codeItemsInTU.size(); indexElement++){
					CodeItem ciInTU = codeItemsInTU.get(indexElement);
					if((ciInTU instanceof ControlElement) || (ciInTU instanceof MethodUnit)){
						muList.add((ControlElement) ciInTU);
						//System.out.println("mu in tu"+ciInTU.getName());
					}
				}
			}
		}
		
		return muList;
	}
	
	/**
	 * Get all the MethodUnits contained in one interface unit.
	 * @param segment
	 * @return
	 */
	public List<ControlElement> getControlElementsFor1IU(InterfaceUnit iu){
		//Get all the CodeItems contained in  cu
		List<CodeItem> codeItems = iu.getCodeElement();
		
		//Get all the MethodUnits contained in the codeItems list
		List<ControlElement> muList = new ArrayList<ControlElement>();		
		for(CodeItem ci : codeItems){
			if((ci instanceof ControlElement) ||(ci instanceof MethodUnit))
				muList.add((ControlElement) ci);
		}
		
		return muList;
	}
	
	/**
	 * Get all the MethodUnits contained in one interface unit.
	 * @param segment
	 * @return
	 */
	public static List<ControlElement> getControlElementFor1EnumeratedType( EnumeratedType enumType){
		//Get all the CodeItems contained in  cu
		List<CodeItem> codeItems = enumType.getCodeElement();
		
		//Get all the MethodUnits contained in the codeItems list
		List<ControlElement> muList = new ArrayList<ControlElement>();		
		for(CodeItem ci : codeItems){
			if((ci instanceof ControlElement) ||(ci instanceof MethodUnit))
				muList.add((ControlElement) ci);
		}
		
		return muList;
	}
	
	
	/**
	 * Returns the principal blockUnit contained in a method unit.
	 * @param mu
	 */
	public EList<BlockUnit> getBlockUnits(ControlElement mu){
		EList<AbstractCodeElement> codeElements = new BasicEList<AbstractCodeElement>();
		codeElements = mu.getCodeElement();
		EList<BlockUnit> blockList = new BasicEList<BlockUnit>();
		for(AbstractCodeElement codeElement: codeElements){
			if(codeElement instanceof BlockUnit)
				blockList.add((BlockUnit)codeElement);
		}
		return blockList;
	}

	/**
	 * Get all the MethodUnits contained in the list of ClassUnits relevant for the system
	 * @param segment
	 * @return
	 */
	public List<ControlElement> getMethodUnits(List<ClassUnit> cUsList){
		List<ControlElement> muList = new ArrayList<ControlElement>();
		for(ClassUnit cu : cUsList){
			muList.addAll(getControlElementsFor1CU(cu));
		}
		return muList;
	}
	
}
