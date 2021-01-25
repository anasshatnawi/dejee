package realeity.technique.extractor.codemodel.relationships;

import java.util.ArrayList;
import java.util.List;



import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.Creates;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.Implements;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;

import realeity.technique.extractor.AbstractKDMRelationshipExtractor;
import realeity.technique.extractor.codemodel.entities.ClassUnitExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;
import realeity.technique.util.RealeityUtils;


/**
 * This class allows to extract all the Implements dependencies (a class implementing 
 * an interface) relevant to the analyzed system.
 * @author Alvine Boaye Belle
 *
 */
public class ImplementsExtractor extends AbstractKDMRelationshipExtractor {
	
	/**
	 * Constructor of the class ImplementsExtractor. 
	 * Its visibility is private in order not to allow the other classes 
	 * to create many instances of ImplementsExtractor.
	 */
	private ImplementsExtractor(){
		
	}
	
	/**
	 * unique instance of ImplementsExtractor.
	 */
	private static ImplementsExtractor uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of ImplementsExtractor is created.
	 * @return uniqueInstance
	 */
	public static ImplementsExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new ImplementsExtractor();
		}
		return uniqueInstance;
	}
	
	/**
	 * Get all the Implements of a data type.
	 * @param cu
	 * @return
	 */
	 public List<Implements> getImplementsInDatatype(Datatype typeFrom){ 
		if(typeFrom instanceof ClassUnit){
			return getImplementsForACu((ClassUnit) typeFrom);
		}
		else if(typeFrom instanceof InterfaceUnit){
			return getImplementsForAnIu((InterfaceUnit) typeFrom);
		}
		else
			return new ArrayList<Implements>();
	}
	
	/**
	 * Get all the implements relationships of a class unit.
	 * @param cu
	 * @return
	 */
	public List<Implements>  getImplementsForACu(ClassUnit cu){
		List<Implements> listImplements = new ArrayList<Implements>();
		EList<AbstractCodeRelationship> codeRelations = cu.getCodeRelation();
		for(AbstractCodeRelationship codeRelation: codeRelations){ 
			if(codeRelation instanceof Implements){
				Implements implement = (Implements) codeRelation;
				listImplements.add(implement);
			}	
	   }
		//get the Implements relations of the internal classes
		 List<ClassUnit> nestedClasses = ClassUnitExtractor.getInstance().getTheInternalClassesInTheClassUnit(cu);
		 for(ClassUnit nestedClass : nestedClasses){
			 listImplements.addAll(getImplementsForACu(nestedClass));
		 }
		return listImplements;
	}
	
	
	/**
	 * Get all the implements relationships of a class unit.
	 * @param iu
	 * @return
	 */
	public List<Implements>  getImplementsForAnIu(InterfaceUnit iu){
		List<Implements> listImplements = new ArrayList<Implements>();
		EList<AbstractCodeRelationship> codeRelations = iu.getCodeRelation();
		for(AbstractCodeRelationship codeRelation: codeRelations){ 
			if(codeRelation instanceof Implements){
				Implements implement = (Implements) codeRelation;
				listImplements.add(implement);
			}	
	   }
		return listImplements;
	}
	
	/**
	 * 
	 * @param cu
	 * @return
	 */
	public List<Implements> extractImplements(ClassUnit cu){
		List<Implements> implementsRelations = new ArrayList<Implements>();
		return implementsRelations;
	}
	
	/**
	 * 
	 * @param iu
	 * @return
	 */
	public List<Implements> extractImplements(InterfaceUnit iu){
		List<Implements> implementsRelations = new ArrayList<Implements>();
		return implementsRelations;
	}
	
	
	/**
	 * Get all the Implements relationships between a package A and a package B.  
	 * A package A is implementing a package B if a class of package A implements an interface contained in B.
	 * @param segment
	 * @return
	 */
	public List<AbstractExtractedFact> getImplements(List<Package> packageList){
		//list of the FactDependencys to determine 
		List<AbstractExtractedFact> FactDependencys = new ArrayList<AbstractExtractedFact>();
		int nbImplements = 0;//à supprimer
		
		List<ClassUnit> cUList = new BasicEList<ClassUnit>();
		for(Package pack : packageList){  
			cUList = ClassUnitExtractor.getInstance().extractCU(pack);  
			for(ClassUnit cu: cUList){
				//System.out.println("package: " + pack.getName() + " considered class : " + cu.getName());
				//get the code relations contained in the cu
				EList<AbstractCodeRelationship> codeRelations = cu.getCodeRelation();
				//get the Implements FactDependencys contained in the cu
				for(AbstractCodeRelationship codeRelation: codeRelations){
					try{
						if(codeRelation instanceof Implements){
							nbImplements++; 
							Implements implement = (Implements) codeRelation;
							Package packageFrom = pack;
							Package packageEnd= findEndPackage(packageList, implement);
							if((packageFrom != packageEnd) && (packageEnd != null)) //we don't take into account the implements relations in the same package
							FactDependencys.add(new ElementaryFact(packageFrom, packageEnd, 1,
									            RealeityUtils.RelationsTypes.Implements.toString(),
									            ""));
						}
					}
					catch(Exception e){
						
					}
				}
			}
			System.out.println("****** Le package " + pack.getName() + " contient le package " +
					           " " + pack.getName()+" dont le nombre de classes est: " + cUList.size());	
		}
		System.out.println("");//to get an empty line
		System.out.println("////////////////// Le nombre d'implements est : " + nbImplements);
		List<AbstractExtractedFact> noDuplicates = removeDuplicates(FactDependencys);
		return noDuplicates;
	}
	
	
}
