package realeity.technique.extractor.codemodel.entities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.ControlElement;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.code.TemplateUnit;
import org.eclipse.gmt.modisco.omg.kdm.source.SourceRef;
import org.eclipse.gmt.modisco.omg.kdm.source.SourceRegion;

import realeity.technique.extractor.AbstractKDMEntityExtractor;


/**
 * 
 * @author Alvine
 *
 */
public class ClassUnitExtractor extends AbstractKDMEntityExtractor {

	/**
	 * Class's constructor. 
	 * Its visibility is private in order not to allow the other classes to create 
	 * many instances of LayeringFacade.
	 */
	private ClassUnitExtractor(){
		
	}
	
	/**
	 * unique instance of LayeringFacade.
	 */
	private static ClassUnitExtractor uniqueInstance;
	
	/**
	 * Method insuring that a unique instance of ClassUnitExtractor is created.
	 * @return uniqueInstance
	 */
	public static ClassUnitExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new ClassUnitExtractor();
		}
		return uniqueInstance;
	}

	/**
	 * Check whether cUnit is an internal class. If so, return the class containing 
	 * classFrom. So if cUnit is defined in its own file or is defined in the file 
	 * of another class but outside the code of this file, then the class cUnit is 
	 * returned. 
	 * @param classFrom
	 * @param packFrom
	 * @return
	 */
	private ClassUnit removeClassDefinedInAnotherClass(ClassUnit cUnit){
		//recuperer la classe portant le meme nom que fileName et l'affecter a mummyclass!!!
		String fileName = "";
		
		try{
			EList<SourceRef> sourceRefList = cUnit.getSource();
			for(SourceRef sourceRef : sourceRefList){
				EList<SourceRegion> regionList = sourceRef.getRegion();
				for(SourceRegion region : regionList){
					org.eclipse.gmt.modisco.omg.kdm.source.SourceFile sourceFile = region.getFile();
					if(sourceFile != null){
					    fileName = sourceFile.getName();
						String filePath = sourceFile.getPath();
						if(filePath.contains(cUnit.getName()))
							return cUnit;
						else {//get the class having the same name than the fileName
							Datatype mummyClass = (Datatype) cUnit.eContainer();
							return null;//cUnit est definie a l'interieur d'une autre classe
						}
					}
				}
			}
		}
		catch(Exception e){//the container is probably a package then find the class that matches fileName
			//la classe cUnit est a l'interieur du fichier d'une autre classe mais est definie en dehors de cette classe.
		}
		
		return cUnit;
	}
	
	/**
	 * Extract all the ClassUnit contained in one package. 
	 * Notice that a class unit can also be contained in a template unit.
	 * @param pack
	 * @return
	 */
	public List<ClassUnit> extractCU (Package pack){
		if(pack.getName().equals("applet"))
			System.out.println();
		List<ClassUnit> cUsList = new ArrayList<ClassUnit>();
		EList<AbstractCodeElement>  abstractCodeElements = pack.getCodeElement();
		List<TemplateUnit> tUsList = new ArrayList<TemplateUnit>();
		
		for(AbstractCodeElement abstractCode : abstractCodeElements){
			if(abstractCode instanceof ClassUnit)//si un class unit peut être contenu dans un template unit, alors récupérer les template unit et les traiter
				cUsList.add((ClassUnit)abstractCode);
			else if (abstractCode instanceof TemplateUnit)
				tUsList.add((TemplateUnit)abstractCode);
		}
		//gather all the class units and the class units contained in the template units
		List<ClassUnit> cusInTemplateUnits = new ArrayList<ClassUnit>();//contain all the class units nested/contained in a template unit
		for(TemplateUnit tu : tUsList){
			cusInTemplateUnits.addAll(extractClassUnitsInTemplate(tu));
		}
		cUsList.addAll(cusInTemplateUnits);
		
		//gather all the class units and the class units they contain
		/*List<ClassUnit> allCUsList = new ArrayList<ClassUnit>();
		for(ClassUnit cu : cUsList){
			allCUsList.addAll(getTheInternalClassesInTheClassUnit(cu));
		}
		allCUsList.addAll(cUsList);
		return allCUsList;*/
		  
		
		return cUsList;
	}
	
	/**
	 * Get the anonymous classes nested in the actions elements of the class unit.
	 * @param cu
	 * @param actionNames
	 * @param actionNamesInStorableUnits
	 * @return
	 */
	public List<ClassUnit> getAnonymousNestedClassInMethodUnit(ClassUnit cu, List<String> actionNames, 
			                                                          List<String> actionNamesInStorableUnits){
		
		List<ControlElement> muList = ControlElementsExtractor.getInstance().getControlElementsFor1CU(cu);
		List<ClassUnit> nestedCUList = new ArrayList<ClassUnit>();
		List<BlockUnit> blockList = new ArrayList<BlockUnit>();
		
		for(ControlElement mu : muList){
			blockList = ControlElementsExtractor.getInstance().getBlockUnits(mu);
			List<ActionElement>  actionsInMethodUnits = ActionElementsExtractor.getInstance().getActionsInMethodUnits(cu, 
					                                                                                                  actionNames, 
					                                                                                                  actionNames);
			for(ActionElement action : actionsInMethodUnits){
				List<AbstractCodeElement> codeElements = action.getCodeElement();
				for(AbstractCodeElement codeElement : codeElements){
					if(codeElement instanceof ClassUnit){
						if(!nestedCUList.contains((ClassUnit)codeElement))
							nestedCUList.add((ClassUnit) codeElement); 
					}
				}
			}
		}
		
		return nestedCUList;		
	}
	
	/**
	 * Only returns the the classUnits declared in their own file
	 * @param pack
	 * @return
	 */
	public List<ClassUnit> extractCUDefinedInTheirOwnFiles (Package pack){
		//only contain the classUnit declared in their own file
		List<ClassUnit> cUsList = extractCU(pack);
		List<ClassUnit> cUsListBis = new ArrayList<ClassUnit>();
		for(ClassUnit cUnit : cUsList){
			cUnit = removeClassDefinedInAnotherClass(cUnit);
			if(cUnit != null)
				cUsListBis.add(cUnit);
		}
		return cUsListBis;
	}
	
		
	/**
	 * Returns all the interface units nested in a template unit.
	 * @param templateUnit
	 * @return
	 */
	private List<ClassUnit> extractClassUnitsInTemplate(TemplateUnit templateUnit){
		List<ClassUnit> nestedCUList = new ArrayList<ClassUnit>();
		EList<CodeItem> codeItems = templateUnit.getCodeElement();;
		for(CodeItem codeItem: codeItems){
			if(codeItem instanceof ClassUnit){
				ClassUnit internalCU = (ClassUnit) codeItem;
				nestedCUList.add(internalCU);
			}
			else if(codeItem instanceof TemplateUnit){
				TemplateUnit internalTu = (TemplateUnit) codeItem;
				nestedCUList.addAll(extractClassUnitsInTemplate(internalTu));
			}
		}
		return nestedCUList;
	}
	
	/**
	 * Get the internal classes of the class unit (not the anonymous ones?!).
	 * @param cU
	 * @return
	 */
	public List<ClassUnit> getTheInternalClassesInTheClassUnit(ClassUnit cU){
		List<ClassUnit> internalCUList = new ArrayList<ClassUnit>();
		EList<CodeItem> codeItems = cU.getCodeElement();
		for(CodeItem codeItem: codeItems){
			if(codeItem instanceof ClassUnit){
				ClassUnit internalCU= (ClassUnit) codeItem;
				internalCUList.add(internalCU);
				//internalCUList.addAll(extractNestedClassUnit(internalCU));
			}
		}
		return internalCUList;
	}
	
	/**
	 * Get the internal classes of the class unit (not the anonymous ones?!).
	 * @param cU
	 * @return
	 */
	public List<EnumeratedType> getTheInternalClassesInTheInterfaceUnit(InterfaceUnit iU){
		List<EnumeratedType> internalCUList = new ArrayList<EnumeratedType>();
		EList<CodeItem> codeItems = iU.getCodeElement();
		for(CodeItem codeItem: codeItems){
			if(codeItem instanceof EnumeratedType){
				EnumeratedType internalCU= (EnumeratedType) codeItem;
				internalCUList.add(internalCU);
				//internalCUList.addAll(extractNestedClassUnit(internalCU));
			}
		}
		return internalCUList;
	}
	
	/**
	 * Get all the class units contained in the list of packages relevants for the system
	 * @param segment
	 * @return
	 */
	public List<ClassUnit> getClassUnits(List<Package> packageList){
		List<ClassUnit> cUsList = new ArrayList<ClassUnit>();
		for(Package pack : packageList){
			cUsList.addAll(extractCU(pack));		
		}
		return cUsList;
	}
}



