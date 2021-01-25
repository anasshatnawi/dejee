package realeity.technique.extractor.codemodel.entities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.code.TemplateUnit;

import realeity.technique.extractor.AbstractKDMEntityExtractor;


/**
 * 
 * @author Alvine
 *
 */
public class EnumeratedTypeExtractor extends AbstractKDMEntityExtractor  {

	/**
	 * Class's constructor. 
	 * Its visibility is private in order not to allow the other classes to create 
	 * many instances of LayeringFacade.
	 */
	private EnumeratedTypeExtractor(){
		
	}
	
	/**
	 * unique instance of LayeringFacade.
	 */
	private static EnumeratedTypeExtractor uniqueInstance;
	
	/**
	 * Method insuring that a unique instance of EnumTypeExtractor is created.
	 * @return uniqueInstance
	 */
	public static EnumeratedTypeExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new EnumeratedTypeExtractor();
		}
		return uniqueInstance;
	}


	/**
	 * Extract all the enumeratedType contained in one package. An enumerated type can be contained in a template unit.
	 * @param pack
	 * @return
	 */
	public List<EnumeratedType> extractEnum (Package pack ){
		List<EnumeratedType> EnumsList = new ArrayList<EnumeratedType>();
		EList<AbstractCodeElement>  abstractCodeElements = pack.getCodeElement();
		List<TemplateUnit> tUsList = new ArrayList<TemplateUnit>();
		
		for(AbstractCodeElement abstractCode : abstractCodeElements){
			if(abstractCode instanceof EnumeratedType)//si un EnumeratedType peut être contenu dans un template unit, alors récupérer les template unit et les traiter
				EnumsList.add((EnumeratedType)abstractCode);
			else if (abstractCode instanceof TemplateUnit)
				tUsList.add((TemplateUnit)abstractCode);
		}
		//gather all the class units and the class units contained in the template units
		List<EnumeratedType> EnumsInTemplateUnits = new ArrayList<EnumeratedType>();//contain all the interface units nested/contained in a template unit
		for(TemplateUnit tu : tUsList)
		{
			EnumsInTemplateUnits.addAll(extractEnumeratedTypesInTemplate(tu));
		}
		EnumsList.addAll(EnumsInTemplateUnits);
	
		return EnumsList;
	}
		
	/**
	 * Returns all the EnumeratedTypes nested in a template unit.
	 * @param templateUnit
	 * @return
	 */
	private static List<EnumeratedType> extractEnumeratedTypesInTemplate(TemplateUnit templateUnit){
		List<EnumeratedType> nestedEnumList = new ArrayList<EnumeratedType>();
		EList<CodeItem> codeItems = templateUnit.getCodeElement();;
		for(CodeItem codeItem: codeItems){
			if(codeItem instanceof EnumeratedType){
				EnumeratedType internalEnum = (EnumeratedType) codeItem;
				nestedEnumList.add(internalEnum);
			}
			else if(codeItem instanceof TemplateUnit){
				TemplateUnit internalTu = (TemplateUnit) codeItem;
				nestedEnumList.addAll(extractEnumeratedTypesInTemplate(internalTu));

			}
		}
		return nestedEnumList;
	}
	
	/**
	 * Get all the EnumeratedTypes contained in the list of packages relevant for the system
	 * @param segment
	 * @return
	 */
	public List<EnumeratedType> getEnumeratedTypes(List<Package> packageList){
		List<EnumeratedType> EnumsList = new ArrayList<EnumeratedType>();
		for(Package pack : packageList){
			EnumsList.addAll(extractEnum(pack));		
		}
		return EnumsList;
	}
}




