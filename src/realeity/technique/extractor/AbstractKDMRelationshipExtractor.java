package realeity.technique.extractor;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.Calls;
import org.eclipse.gmt.modisco.omg.kdm.action.Creates;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.ArrayType;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.Extends;
import org.eclipse.gmt.modisco.omg.kdm.code.InstanceOf;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.ItemUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.code.TemplateType;
import org.eclipse.gmt.modisco.omg.kdm.code.TemplateUnit;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMRelationship;
import org.eclipse.gmt.modisco.omg.kdm.source.SourceRef;
import org.eclipse.gmt.modisco.omg.kdm.source.SourceRegion;

import realeity.technique.extractor.codemodel.entities.ClassUnitExtractor;
import realeity.technique.extractor.codemodel.relationships.ExtendsExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;


/**
 * 
 * @author Boaye Belle Alvine
 * 2014
 *
 */
public abstract class AbstractKDMRelationshipExtractor implements IKDMExtractor {
	protected final static int MAX_NUMBER_OF_ANCESTORS_TO_FETCH = 3 ;
	protected static String actionDeclarationName = "variable declaration";
	protected static String CreatesActionName1 = "class instance creation";
	protected static String CreatesActionName2 = "array creation";
	protected static final String voidName = "void";
	
	/**
	 * Retrieve the datatype specified in the template
	 * @param endRelation
	 * @return
	 */
	private static Datatype retrieveDataFromTemplate(Datatype template){
		Datatype dataType = null;
		List<AbstractCodeRelationship> codeRelations = template.getCodeRelation();
		for(AbstractCodeRelationship codeRelation : codeRelations){
			if(codeRelation instanceof InstanceOf){
				TemplateUnit templateUnit = (TemplateUnit) codeRelation.getTo();
				List<CodeItem> codeItemList = templateUnit.getCodeElement();
				for(CodeItem codeItem : codeItemList){
					if ((codeItem instanceof InterfaceUnit) || (codeItem instanceof ClassUnit) 
					    || (codeItem instanceof EnumeratedType)){
						dataType = (Datatype) codeItem;
				     }
				}
			}
		}
		
		return dataType;
	}
	
	
	/**
	 * Find the class unit  or the interface unit corresponding to the end of the relation
	 * @param relation
	 * @return
	 */
	public static KDMEntity findDestinationDatatype(KDMRelationship relation){
		//return an interface unit or a class unit
		KDMEntity endRelation = relation.getTo();
		ActionElement actionFrom = null;
		if(relation instanceof Creates)
			actionFrom = (ActionElement) relation.getFrom();
		
		if((relation instanceof Calls) ||((relation instanceof Creates) 
		    && (actionFrom.getName().equals(CreatesActionName1)))){
			EObject parent = null;
			try{
				while (!(endRelation instanceof InterfaceUnit) && !(endRelation instanceof ClassUnit) 
					   && !(endRelation instanceof EnumeratedType)) {
					parent = endRelation.eContainer();
				
					if(endRelation instanceof TemplateType){
						//retrieve the datatype inside the templatetype
						Datatype datatype = retrieveDataFromTemplate((TemplateType) endRelation);
						parent = datatype;
					}
		 			assert parent != null : "In this case, the parent is null. You should " +
		 					                "check the structure of " + endRelation;
		 			endRelation = (KDMEntity) parent;
		 		}
			}
			catch(Exception e){//TO DO: TROUVER LE BON PARENT DE LA METHODE QUI CAUSE L'EXCEPTION
				//e.printStackTrace();
				endRelation = null;
			}
		}
		else if((relation instanceof Creates) && (actionFrom.getName().equals(CreatesActionName2))){
			ArrayType arrayDestination = (ArrayType) endRelation;
			ItemUnit itemUnit = arrayDestination.getItemUnit();
			Datatype data = itemUnit.getType();
			//System.out.println("arrayDestination= "+arrayDestination+" et datatype="+data);//à supprimer
			endRelation = data;
		}
		return endRelation;
	}
	
	/**
	 * Checks whether possibleAncestor is an ancestor of son entity
	 * @param possibleAncestor
	 * @param sonEntity
	 * @return
	 */
	protected boolean isAnAncestor (KDMEntity possibleAncestor, Datatype sonEntity){
		boolean isAncestor = false;
		ClassUnit ancestor = (ClassUnit) sonEntity;
		List<Extends> extendsList = ExtendsExtractor.getInstance().getExtendsForACu((ClassUnit) ancestor);
		int numberOfAncestors = 0;
		
		try{
			while(extendsList.size() > 0){
				Extends extendsRelation = extendsList.get(0); //a class has a single inherited parent
				ancestor = (ClassUnit) findDestinationDatatype(extendsRelation);
				numberOfAncestors++;
					
				if(ancestor.equals(possibleAncestor)){
					isAncestor = true;
				    break;
				 }
				if(numberOfAncestors >= MAX_NUMBER_OF_ANCESTORS_TO_FETCH)
					break;
				
				extendsList = ExtendsExtractor.getInstance().getExtendsForACu((ClassUnit) ancestor);	
			}
		}
		catch( java.lang.ClassCastException e){
			
		}
	
		return isAncestor;
	}
	
	/**
	 * Fetch all the ancestors of a Datatype sonEntity.
	 * @param possibleAncestor
	 * @param sonEntity
	 * @return
	 */
	public static List<Datatype> fetchAncestorsList (Datatype sonEntity){
		Datatype ancestor = sonEntity;
		List<Extends> extendsList = new ArrayList<Extends>();
		if(ancestor instanceof ClassUnit)
			extendsList = ExtendsExtractor.getInstance().getExtendsForACu((ClassUnit) ancestor);
		if(ancestor instanceof InterfaceUnit)
			extendsList = ExtendsExtractor.getInstance().getExtendsForAnIu((InterfaceUnit) ancestor);
		List<Datatype> ancestorsList = new ArrayList<Datatype>();
		
		try{
			while(extendsList.size() > 0){
				Extends extendsRelation = extendsList.get(0); //a class has a single inherited parent
				ancestor = (ClassUnit) findDestinationDatatype(extendsRelation);
				ancestorsList.add(ancestor);
				//we limit the number of ancestors to fetch in order not to run out of heap space
				if(ancestorsList.size() >= MAX_NUMBER_OF_ANCESTORS_TO_FETCH)
					break;
				extendsList = ExtendsExtractor.getInstance().getExtendsForACu((ClassUnit) ancestor);	
			}
		}
		catch( java.lang.ClassCastException e){
			
		}
	
		return ancestorsList;
	}
	
	/**
	 * Find the package containing the interface which is at the end of the relation 
	 * @return
	 */
     protected Package findEndPackage(List<Package> packageList, KDMRelationship relation){
    	// recover the package containing the action element
        EObject parent = (relation.getTo()).eContainer();
 		while (!(parent instanceof Package)) {
 			parent = (KDMEntity)parent.eContainer();
 			assert parent != null : "In this case, the parent is null. You should " +
 					                "check the structure of " + relation;
 		}
 		
 		Package pack = (Package)parent;
 		if( !(packageList.contains(pack))){
 			pack = null;
 		}
 		return pack;
	}
     
     
 	
	
	
	/**
	 * Check whether the code of classFrom is defined inside the file of another class. 
	 * If so, return the class containing classFrom.
	 * @param classFrom
	 * @param packFrom
	 * @return
	 */
	protected static Datatype definedInsideTheFileOfAnotherClass(Datatype type, 
			                                                     List<KDMEntity> packageList){
		Datatype mummyClass = type;
		String fileName = "";
		
		try{
			EList<SourceRef> sourceRefList = type.getSource();
			for(SourceRef sourceRef : sourceRefList){
				EList<SourceRegion> regionList = sourceRef.getRegion();
				for(SourceRegion region : regionList){
					org.eclipse.gmt.modisco.omg.kdm.source.SourceFile sourceFile = region.getFile();
					if(sourceFile != null){
					    fileName = sourceFile.getName();
						String filePath = sourceFile.getPath();
						if(filePath.contains(type.getName()))
							return type;
						else {//get the class having the same name than the fileName
							mummyClass = (Datatype) type.eContainer();
							return mummyClass;
						}
					}
				}
			}
		}
		catch(Exception e){//the container is probably a package then find the class that matches fileName
			Package packageTo = findPackageTo(type, packageList);
			List<Package> packageListTo = new ArrayList<Package>();
			packageListTo.add(packageTo);
			List<ClassUnit> cuList = ClassUnitExtractor.getInstance().getClassUnits(packageListTo);
			for(ClassUnit cu : cuList){
				if(fileName.contains(cu.getName())){
					mummyClass = cu;
					break;
				}
			}
			//mummyClass = null;
		}
		
		return mummyClass;
	}
	
	/**
	 * Get the package containing the entity.
	 * @param entity
	 * @param packageList
	 * @return
	 */
	public static Package findPackageTo(KDMEntity entityTo, 
			                            List<KDMEntity> packageList){
		// recover the package containing the action element
		
		Package pack = null;
 		try{
 			EObject parent = entityTo;
 			while (!(parent instanceof Package)) {
 	 			parent = (KDMEntity)parent.eContainer();
 	 			assert parent != null : "In this case, the parent is null. You should " +
 	 					                "check the structure of " + entityTo;
 	 		}
 			pack = (Package)parent;
 	 		if( !(packageList.contains(pack))){
 	 			pack = null;
 	 		}

 		}
 		catch(Exception e){
 			//the container of the entityTo might be in a codeModel external 
 			//to the codeModel of the analyzed system 			
 		}
 		return pack; 		
	}
	
	/**
	 * Get the package containing the entity.
	 * @param entity
	 * @param packageList
	 * @return
	 */
	public static Package findPackageTo(KDMEntity entityTo){
		// recover the package containing the action element
		
		Package pack = null;
 		try{
 			EObject parent = entityTo;
 			while (!(parent instanceof Package)) {
 	 			parent = (KDMEntity)parent.eContainer();
 	 			assert parent != null : "In this case, the parent is null. You should " +
 	 					                "check the structure of " + entityTo;
 	 		}
 			pack = (Package)parent;

 		}
 		catch(Exception e){
 			//the container of the entityTo might be in a codeModel external 
 			//to the codeModel of the analyzed system 			
 		}
 		return pack; 		
	}
	
	
	/**
	 * Removes all the duplicates in the extracted facts. 
	 * If a FactDependency (A, B, 1, xxx) appears n times, it is replaced by 
	 * a single FactDependency (A, B, n, xxx).
	 * @param extractedFacts
	 * @return
	 */
	protected List<AbstractExtractedFact> removeDuplicates(List<AbstractExtractedFact> extractedFacts){
		List<AbstractExtractedFact> duplicatesWithWeight = new ArrayList<AbstractExtractedFact>();
		//sum the weights of the similar FactDependencys (same from package, end package and relation) 
		//and creates the corresponding FactDependency
		for(AbstractExtractedFact fact : extractedFacts){
			KDMEntity fromEntity = fact.getDependencyStart();
			KDMEntity endEntity = fact.getDependencyEnd();
			String relationName = fact.getDependencyName();
			int weight = 0;
			for(AbstractExtractedFact otherFact : extractedFacts){
				KDMEntity otherFromEntity = otherFact.getDependencyStart();
				KDMEntity otherEndEntity = otherFact.getDependencyEnd();
				String otherName = otherFact.getDependencyName();
				if((fromEntity.equals(otherFromEntity)) && (endEntity.equals(otherEndEntity)) 
				    && (relationName == otherName))
					weight++;
			}
			
			AbstractExtractedFact newFactDependency = new ElementaryFact(fromEntity, 
					                                                      endEntity, 
					                                                      weight, 
					                                                      relationName,
					                                                      "");
			duplicatesWithWeight.add(newFactDependency);	
		}
		
		//remove the duplicates in duplicatesWithWeight
		List<AbstractExtractedFact> noDuplicates = new ArrayList<AbstractExtractedFact>();
		for(AbstractExtractedFact fact : duplicatesWithWeight){
			KDMEntity fromEntity = fact.getDependencyStart();
			KDMEntity endEntity = fact.getDependencyEnd();
			String relationName = fact.getDependencyName();
			boolean duplicated = false;
			for(AbstractExtractedFact otherFactDependency : noDuplicates){
				KDMEntity otherFromEntity =  otherFactDependency.getDependencyStart();
				KDMEntity otherEndEntity = otherFactDependency.getDependencyEnd();
				String otherName = otherFactDependency.getDependencyName();
				if((fromEntity == otherFromEntity) && (endEntity == otherEndEntity) 
					&& (relationName == otherName))
					duplicated = true;
				}
			if(!duplicated){
				noDuplicates.add(fact);			
			}
			
		}
		return noDuplicates;
	}

}
