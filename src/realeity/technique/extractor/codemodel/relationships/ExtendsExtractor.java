package realeity.technique.extractor.codemodel.relationships;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.Extends;
import org.eclipse.gmt.modisco.omg.kdm.code.Implements;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;

import realeity.technique.extractor.AbstractKDMRelationshipExtractor;
import realeity.technique.extractor.codemodel.entities.ClassUnitExtractor;



/**
 * 
 * @author Alvine
 *
 */
public class ExtendsExtractor extends AbstractKDMRelationshipExtractor {

	/**
	 * Class's constructor. 
	 * Its visibility is private in order not to allow the other classes to create 
	 * many instances of LayeringFacade.
	 */
	private ExtendsExtractor(){
		
	}
	
	/**
	 * unique instance of LayeringFacade.
	 */
	private static ExtendsExtractor uniqueInstance;
	
	/**
	 * Method insuring that a unique instance of ExtendsExtractor is created.
	 * @return uniqueInstance
	 */
	public static ExtendsExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new ExtendsExtractor();
		}
		return uniqueInstance;
	}
	
	/**
	 * Get all the Extends of a data type.
	 * @param cu
	 * @return
	 */
	 public List<Extends> getExtendsInDatatype(Datatype typeFrom){ 
		if(typeFrom instanceof ClassUnit){
			return getExtendsForACu((ClassUnit) typeFrom);
		}
		else if(typeFrom instanceof InterfaceUnit){
			return getExtendsForAnIu((InterfaceUnit) typeFrom);
		}
		else if(typeFrom instanceof EnumeratedType){
			return getExtendsForAnEnumeratedType((EnumeratedType) typeFrom);
		}
		else
			return new ArrayList<Extends>();
	}

	/**
	 * Get all the extends relationships of a class unit and those of the class units it contains
	 * @param cu
	 * @return
	 */
	 public List<Extends>  getExtendsForACu(ClassUnit cu){
		List<Extends> listExtends = new ArrayList<Extends>();
		EList<AbstractCodeRelationship> codeRelations = cu.getCodeRelation();
		for(AbstractCodeRelationship codeRelation: codeRelations){ 
			if(codeRelation instanceof Extends){
				Extends extend = (Extends) codeRelation;
				listExtends.add(extend);
			}	
	   }
		 //get the extends relations of the internal classes
		 List<ClassUnit> nestedClasses = ClassUnitExtractor.getInstance().getTheInternalClassesInTheClassUnit(cu);
		 for(ClassUnit nestedClass : nestedClasses){
			 listExtends.addAll(getExtendsForACu(nestedClass));
		 }
		return listExtends;
	}
	
	/**
	 * Get all the extends relationships of a class unit and those of the class units it contains
	 * @param cu
	 * @return
	 */
	 public List<Extends>  getExtendsForAnEnumeratedType(EnumeratedType enumType){
		List<Extends> listExtends = new ArrayList<Extends>();
		EList<AbstractCodeRelationship> codeRelations = enumType.getCodeRelation();
		for(AbstractCodeRelationship codeRelation: codeRelations){ 
			if(codeRelation instanceof Extends){
				Extends extend = (Extends) codeRelation;
				listExtends.add(extend);
			}	
	   }
		return listExtends;
	}
	
	
	/**
	 * Get all the extends relationships of a class unit.
	 * @param iu
	 * @return
	 */
	 public List<Extends>  getExtendsForAnIu(InterfaceUnit iu){
		List<Extends> listExtends = new ArrayList<Extends>();
		EList<AbstractCodeRelationship> codeRelations = iu.getCodeRelation();
		for(AbstractCodeRelationship codeRelation: codeRelations){ 
			if(codeRelation instanceof Extends){
				Extends extendsRelation = (Extends) codeRelation;
				listExtends.add(extendsRelation);
			}	
	   }
		return listExtends;
	}
	
}





