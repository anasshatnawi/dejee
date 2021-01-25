package realeity.technique.extractor.codemodel.relationships;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.Extends;
import org.eclipse.gmt.modisco.omg.kdm.code.Imports;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;

import realeity.technique.extractor.AbstractKDMRelationshipExtractor;
import realeity.technique.extractor.codemodel.entities.ClassUnitExtractor;
import realeity.technique.extractor.codemodel.entities.InterfaceUnitExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;
import realeity.technique.util.RealeityUtils;




/**
 * This class allows to extract all the Imports dependencies relevant to the analyzed system. 
 * We are looking for imports between cu/iu and cu/iu and not between a classunit/interfaceunit and a package.
 * @author Alvine Boaye Belle
 *
 */

public class ImportsExtractor extends AbstractKDMRelationshipExtractor {
	
	/**
	 * Constructor of the class ImportsExtractor. 
	 * Its visibility is private in order not to allow the other classes 
	 * to create many instances of ImportsExtractor.
	 */
	private ImportsExtractor(){
		
	}
	
	/**
	 * unique instance of ImplementsExtractor.
	 */
	private static ImportsExtractor uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of ImportsExtractor is created.
	 * @return uniqueInstance
	 */
	public static ImportsExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new ImportsExtractor();
		}
		return uniqueInstance;
	}
	
	/**
	 * An imports going from cuFrom to a class B should not be considered if an 
	 * ancestor of cuFrom has also imported B
	 * @param importsList
	 * @return
	 */
	private List<Imports> removeHierarchicalImports(ClassUnit cuFrom, List<Imports> importsList){
		
		List<Imports> importsToRemove = new ArrayList<Imports>();
		
		List<Datatype> ancestorsList = fetchAncestorsList(cuFrom);//ExtendsExtraction.getExtendsForACu(cuFrom);
		List<Imports> AncestorsImportsList = new ArrayList<Imports>();
		
		//fetch the datatypes imported by the ancestor(s)
		for(Datatype destination : ancestorsList){
			if(destination instanceof ClassUnit)
			AncestorsImportsList.addAll(retrieveImports((ClassUnit) destination));
			else if(destination instanceof InterfaceUnit)
				AncestorsImportsList.addAll(getImportsForAnIu((InterfaceUnit) destination));
		}
		
		List<Datatype> ancestorsImportsDestination = new ArrayList<Datatype>();
		for(Imports importsRelation : AncestorsImportsList){
			if(importsRelation.getTo() instanceof Datatype)
			ancestorsImportsDestination.add((Datatype)importsRelation.getTo());
		}
			
		for(Imports importsRelation : importsList){
			try{
				Datatype importsDestination = (Datatype) importsRelation.getTo();
				if(ancestorsImportsDestination.contains(importsDestination))
					importsToRemove.add(importsRelation);
			}
			catch(ClassCastException exception){
				
			}
		}
		
		//remove all the dependencies related to the undesired Imports
		for(Imports importsRelation : importsToRemove){
			importsList.remove(importsRelation);
		}
		
		return importsList;
	}
		
	/**
	 * Get all the Imports of a data type.
	 * @param cu
	 * @return
	 */
	 public List<Imports> getImportsInDatatype(Datatype typeFrom){ 
		if(typeFrom instanceof ClassUnit){
			return getImportsForACu((ClassUnit) typeFrom);
		}
		else if(typeFrom instanceof InterfaceUnit){
			return getImportsForAnIu((InterfaceUnit) typeFrom);
		}
		else if(typeFrom instanceof EnumeratedType){
			return getImportsForAnEnumeratedType((EnumeratedType) typeFrom);
		}
		else
			return new ArrayList<Imports>();
	}

	/**
	 * to complete
	 * @param cu
	 * @return
	 */
   private List<Imports>  retrieveImports(ClassUnit cu){
	   List<Imports> listImports = new ArrayList<Imports>();
		EList<AbstractCodeRelationship> codeRelations = cu.getCodeRelation();
		for(AbstractCodeRelationship codeRelation: codeRelations){ 
			if(codeRelation instanceof Imports){
				Imports extend = (Imports) codeRelation;
				listImports.add(extend);
			}	
	   }
		
		//get the imports relations of the internal classes
		 List<ClassUnit> nestedClasses = ClassUnitExtractor.getInstance().getTheInternalClassesInTheClassUnit(cu);
		 for(ClassUnit nestedClass : nestedClasses){
			 listImports.addAll(retrieveImports(nestedClass));
		 }
		 return listImports;
      }
   
		/**
		 * Get all the Imports relationships of a class unit.
		 * @param cu
		 * @return
		 */
		 public List<Imports>  getImportsForACu(ClassUnit cuFrom){
			 List<Imports> importsList = retrieveImports(cuFrom);
			 importsList = removeHierarchicalImports(cuFrom, importsList);
			 return importsList;
		}
		
		
		/**
		 * Get all the Imports relationships of an interface unit.
		 * @param iu
		 * @return
		 */
		static public List<Imports>  getImportsForAnIu(InterfaceUnit iu){
			List<Imports> listImports = new ArrayList<Imports>();
			EList<AbstractCodeRelationship> codeRelations = iu.getCodeRelation();
			for(AbstractCodeRelationship codeRelation: codeRelations){ 
				if(codeRelation instanceof Imports){
					Imports importRelation = (Imports) codeRelation;
					listImports.add(importRelation);
				}	
		   }
			return listImports;
		}
		
		/**
		 * Get all the Imports relationships of an enumerated type.
		 * @param iu
		 * @return
		 */
		public List<Imports>  getImportsForAnEnumeratedType(EnumeratedType enumType){
			List<Imports> listImports = new ArrayList<Imports>();
			EList<AbstractCodeRelationship> codeRelations = enumType.getCodeRelation();
			for(AbstractCodeRelationship codeRelation: codeRelations){ 
				if(codeRelation instanceof Imports){
					Imports importRelation = (Imports) codeRelation;
					listImports.add(importRelation);
				}	
		   }
			return listImports;
		}
		
		/**
		 * Get all the Imports relationships between a package A and a package B.  
		 * A package A is implementing a package B if a class of package A Imports an interface contained in B.
		 * Note that a classUnit can extend a classUnit and an interfaceUnit can extend another interfaceUnit
		 * @param segment
		 * @return
		 */
		public List<AbstractExtractedFact> getImports(List<Package> packageList){
			//list of the FactDependencys to determine 
			List<AbstractExtractedFact> FactDependencys = new ArrayList<AbstractExtractedFact>();
			int nbImports = 0;//à supprimer
			
			List<ClassUnit> cUList = new BasicEList<ClassUnit>();
			List<InterfaceUnit> iUList = new BasicEList<InterfaceUnit>();
			for(Package pack : packageList){ 
				//get the class units contained in the package
				cUList = ClassUnitExtractor.getInstance().extractCU(pack); 
				EList<AbstractCodeRelationship> codeRelations = new BasicEList<AbstractCodeRelationship>();
				for(ClassUnit cu: cUList){
					//System.out.println("package: "+pack.getName()+" classe considérée: "+cu.getName());
					//get the code relations contained in the cu
					codeRelations.addAll(cu.getCodeRelation());
				}
				
				//get the interface units contained in the package
				iUList = InterfaceUnitExtractor.getInstance().extractIU(pack); 
				for(InterfaceUnit iu: iUList){
					//System.out.println("package: "+pack.getName()+" interface considérée: "+iu.getName());
					//get the code relations contained in the iu
					codeRelations.addAll(iu.getCodeRelation());
				}
				//get the Imports FactDependencys contained in the cus and ius
				for(AbstractCodeRelationship codeRelation: codeRelations){
					try{
						if(codeRelation instanceof Imports){
							nbImports++;
							Imports importRelation = (Imports) codeRelation;
							Package packageFrom = pack;
							if(!(importRelation.getTo() instanceof Package)){
								//we want relations between cu/iu and cu/iu and not between a cu/iu and a package
								Package packageEnd= findEndPackage(packageList, importRelation);
								//we don't take in account the Imports relations in the same package
								if((packageFrom != packageEnd) && (packageEnd != null))
									FactDependencys.add(new ElementaryFact(packageFrom, packageEnd, 1, 
											            RealeityUtils.RelationsTypes.Imports.toString(),
											            ""));
						  
							}
						  }
					}
					catch(Exception e){
						
					}
			   }
					
			}

			//System.out.println("////////////////// Le nombre d'Imports est : "+nbImports);
			
			List<AbstractExtractedFact> noDuplicates = removeDuplicates(FactDependencys);
			return noDuplicates;
		}
		
		/**
		 * 
		 * @param data
		 * @return
		 */
		public List<Imports> getImportsInADatatype(Datatype data){
			List<Imports> importsList = new  ArrayList<Imports>();	
			if (data instanceof ClassUnit)
				importsList = getImportsForACu((ClassUnit) data);
			else if (data instanceof InterfaceUnit)
				importsList = getImportsForAnIu((InterfaceUnit) data);
			else if (data instanceof EnumeratedType)
				importsList = getImportsForAnEnumeratedType((EnumeratedType) data);
			return importsList;
		}
	}
			