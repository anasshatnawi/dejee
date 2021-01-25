package realeity.technique.extractor.codemodel.relationships;

import java.awt.Desktop.Action;
import java.util.ArrayList;
import java.util.List;

import javax.management.relation.RelationType;


import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.ArrayType;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.InstanceOf;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.ItemUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.ParameterTo;
import org.eclipse.gmt.modisco.omg.kdm.code.StorableUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.TemplateType;
import org.eclipse.gmt.modisco.omg.kdm.code.TemplateUnit;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

import realeity.technique.extractor.AbstractKDMRelationshipExtractor;
import realeity.technique.extractor.codemodel.entities.StorableUnitsExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;
import realeity.technique.util.RealeityUtils;


 /**
  * Create a dependency between the datatype in which a storableunit is  created and the type of the StorableUnit.
  * @author Alvine Boaye Belle
  *
  */
public class StorableUnitConnections extends AbstractKDMRelationshipExtractor {

	/**
	 * Constructor of the class StorableUnitConnections. 
	 * Its visibility is private in order not to allow the other classes to 
	 * create many instances of StorableUnitConnections.
	 */
	private StorableUnitConnections(){
		
	}
	
	/**
	 * unique instance of ImplementsExtractor.
	 */
	private static StorableUnitConnections uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of StorableUnitConnections is created.
	 * @return uniqueInstance
	 */
	public static StorableUnitConnections getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new StorableUnitConnections();
		}
		return uniqueInstance;
	}
	
	/**
	 * If there in a class unit, there is a variable declaration under the form 
	 * (datatype.internalClass varName), then the connaction from the class unit 
	 * to the internalClass should be ignored.
	 * @param callsList
	 * @return
	 */
	static List<StorableUnit> removeRedundantSU(List<StorableUnit> storableList, 
			                                    List<AbstractExtractedFact> factDependencies){
		List<StorableUnit> suToRemove = new ArrayList<StorableUnit>();
		
		for(StorableUnit su : storableList){
			Datatype type = su.getType();
			if((type != null) && !type.getName().equals(voidName)){
				  EObject typeContainer =  type.eContainer();
				  boolean containsInternal = false;
				for(AbstractExtractedFact fact : factDependencies){
					KDMEntity factEnd = fact.getDependencyEnd();
					if(typeContainer.equals(factEnd)){
						containsInternal = true;
						break;
					}
				}
				if(containsInternal)
					suToRemove.add(su);
			 }
		 }
		
		//remove all the dependencies related to the undesired parameter units
		storableList.removeAll(suToRemove);
		
		return storableList;
	} 
	
	
	/**
	 * 
	 * @param cu
	 * @return
	 */
	public static List<AbstractExtractedFact> getRelationsInACu(Datatype cu, 
			                                                    List<AbstractExtractedFact> factDependencies){
		List<StorableUnit> storableUnits = StorableUnitsExtractor.getInstance().getStorableUnitsFromACU((ClassUnit) cu);
		storableUnits = removeRedundantSU(storableUnits, factDependencies);
		
		//build the connections corresponding to the storable units
		List<AbstractExtractedFact> tripleList = new ArrayList<AbstractExtractedFact>();
		for(StorableUnit unit : storableUnits){
			if(unit != null){
				Datatype type = unit.getType();
				
				if((type instanceof TemplateType) || (type instanceof TemplateUnit)
					|| (type instanceof ArrayType))
					type = extractTypeIntoTemplate(type);
				if(type != null){
					AbstractExtractedFact factDependency = new ElementaryFact(cu, type, 1, 
                                                                              RealeityUtils.RelationsTypes.ConnectionsFromStorableUnit.toString(),
                                                                              "");
					tripleList.add(factDependency);
				}
			}
		}
	//	System.out.println("les connections storable units : "+tripleList.size());
		return tripleList;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	private static Datatype extractTypeIntoTemplate(Datatype type){
		Datatype typeIntemplate = null;
		if(type instanceof TemplateUnit){
			typeIntemplate = getTypeInTemplateUnit(type);
		}
		else if(type instanceof ArrayType){
			ItemUnit itemUnit = ((ArrayType)type).getItemUnit();
			Datatype typeOfItemUnit = itemUnit.getType();
			if((typeOfItemUnit instanceof InterfaceUnit) || (typeOfItemUnit instanceof InterfaceUnit))
				typeIntemplate = typeOfItemUnit;
			else
				typeIntemplate = getTypeInTemplateUnit(typeOfItemUnit);
		}
		else{
			List<AbstractCodeRelationship> codeRelations = type.getCodeRelation();
			for(AbstractCodeRelationship relation : codeRelations){
				if((relation instanceof ParameterTo) || (relation instanceof InstanceOf)){
					KDMEntity relationTo = relation.getTo();
					if(relationTo instanceof TemplateUnit){
						return getTypeInTemplateUnit((TemplateUnit)relationTo);
					}///
				}
				
			}
		}
		
		return typeIntemplate;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	private static Datatype getTypeInTemplateUnit(Datatype type){
		Datatype typeIntemplate = null;
        try{
        	List<CodeItem> codeElements = ((TemplateUnit)type).getCodeElement();
    		for(CodeItem codeIt : codeElements){
    			if(codeIt instanceof ClassUnit){
    				typeIntemplate = (ClassUnit)codeIt;
    				return typeIntemplate;
    			}
    			else if(codeIt instanceof InterfaceUnit){
    				typeIntemplate = (InterfaceUnit)codeIt;
    				return typeIntemplate;
    			}
    		}
        }
        catch(ClassCastException e){
        	
        }
		
		return typeIntemplate;
	}
	
	/**
	 * 
	 * @param iu
	 * @return
	 */
	public List<AbstractExtractedFact> getRelationsInAnIu(Datatype iu){
		List<StorableUnit> storableUnits = StorableUnitsExtractor.getInstance().getStorableUnitsFromAnIU((InterfaceUnit) iu); 
		//build the connections corresponding to the storable units
		List<AbstractExtractedFact> tripleList = new ArrayList<AbstractExtractedFact>();
		
		for(StorableUnit unit : storableUnits){
			if(unit != null){
				Datatype type = unit.getType();
				if(type instanceof TemplateType)
					type = extractTypeIntoTemplate(type);
				AbstractExtractedFact factDependency = new ElementaryFact(iu, type, 1,
						                                                  RealeityUtils.RelationsTypes.ConnectionsFromStorableUnit.toString(),
						                                                  "");
				tripleList.add(factDependency);
			}
		}
		
		return tripleList;
	}
	
	/**
	 * 
	 * @param enumType
	 * @return
	 */
	public List<AbstractExtractedFact> getRelationsInAnEnumeratedType(Datatype enumType){
		List<StorableUnit> storableUnits = StorableUnitsExtractor.getInstance().getStorableUnitsFromAnEnumeratedType((EnumeratedType) enumType);//.getStorableUnitsFromAnIU(iu); 
		//build the connections corresponding to the storable units
		List<AbstractExtractedFact> tripleList = new ArrayList<AbstractExtractedFact>();
		
		for(StorableUnit unit : storableUnits){
			if(unit != null){
				Datatype type = unit.getType();
				AbstractExtractedFact factDependency = new ElementaryFact(enumType, type, 1, 
						                                                  RealeityUtils.RelationsTypes.ConnectionsFromStorableUnit.toString(),
						                                                  "");
				tripleList.add(factDependency);
			}
		}
		
		return tripleList;
	}
	
	/**
	 * 
	 * @param typeFrom
	 * @param factDependencies
	 * @return
	 */
	public List<AbstractExtractedFact> getRelationsInDatatype(Datatype typeFrom, 
			                                                  List<AbstractExtractedFact> factDependencies){
		if (typeFrom instanceof ClassUnit)
			return getRelationsInACu(typeFrom, factDependencies);
		else if (typeFrom instanceof InterfaceUnit)
			return getRelationsInAnIu(typeFrom);
		else if (typeFrom instanceof EnumeratedType)
			return getRelationsInAnEnumeratedType(typeFrom);
		else
			return new ArrayList<AbstractExtractedFact>();
	}
}
