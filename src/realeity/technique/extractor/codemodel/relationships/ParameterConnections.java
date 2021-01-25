package realeity.technique.extractor.codemodel.relationships;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeRelationship;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.ControlElement;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.Imports;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.ParameterTo;
import org.eclipse.gmt.modisco.omg.kdm.code.ParameterUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.TemplateType;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.code.Signature;

import realeity.technique.extractor.AbstractKDMRelationshipExtractor;
import realeity.technique.extractor.codemodel.entities.ClassUnitExtractor;
import realeity.technique.extractor.codemodel.entities.ControlElementsExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;
import realeity.technique.util.RealeityUtils;


/**
 * Get all the parameter units appearing in the signature of a method unit of a class A. 
 * And if the parameter unit' datatype is a class B by example, then exist a
 * connection between class A and class B.
 * @author Alvine Boaye Belle
 *
 */
public class ParameterConnections extends AbstractKDMRelationshipExtractor {
	private static String unspecifiedNameUsedAsTemplate = "T";
	
	/**
	 * Constructor of the class ParameterConnections. 
	 * Its visibility is private in order not to allow the other classes to create many instances of ParameterConnections.
	 */
	private ParameterConnections(){
		
	}
	
	/**
	 * unique instance of ImplementsExtractor.
	 */
	private static ParameterConnections uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of ParameterConnections is created.
	 * @return uniqueInstance
	 */
	public static ParameterConnections getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new ParameterConnections();
		}
		return uniqueInstance;
	}
	
	/**
	 * TO complete
	 * @param cu
	 * @return
	 */
	private static List<ParameterUnit> getMethodsParameter( ClassUnit cu){
		List<ParameterUnit> parameterList = new ArrayList<ParameterUnit>();
		//get all the parameters connections in the anonymous classes implemented inside cu
		List<ClassUnit> anonymousCuClasses = ClassUnitExtractor.getInstance().getAnonymousNestedClassInMethodUnit(
				                             cu, new ArrayList<String>(), new ArrayList<String>());
		for(ClassUnit anonymousClass : anonymousCuClasses){
			List<ParameterUnit> anonymousParameterList = getMethodsParameter(anonymousClass);
			parameterList.addAll(anonymousParameterList);
		}
		
		List<ControlElement> muList = ControlElementsExtractor.getInstance().getControlElementsFor1CU(cu);
		
		//extract the signatures of each method unit
		List<org.eclipse.gmt.modisco.omg.kdm.code.Signature> signatureList = new ArrayList<Signature>();
		for(ControlElement mu : muList){
			List<AbstractCodeElement> codeElements = mu.getCodeElement();
			for(AbstractCodeElement codeElement : codeElements){
				if(codeElement instanceof org.eclipse.gmt.modisco.omg.kdm.code.Signature)
					{
					signatureList.add((org.eclipse.gmt.modisco.omg.kdm.code.Signature) codeElement);
						
					}
			}
		}
		
		//extract all the parameter units from the signatures
		for(org.eclipse.gmt.modisco.omg.kdm.code.Signature signature : signatureList){
			parameterList.addAll(signature.getParameterUnit());
				}
		return parameterList;
	}
	
	/**
	 * If there in a class unit, there is a signature under the form (datatype.internalClass varName), 
	 * then the connaction from the class unit to the internalClass should be ignored
	 * @param callsList
	 * @return
	 */
	private List<ParameterUnit> removeRedundantParameters(List<ParameterUnit> parameterList, 
			                                             List<AbstractExtractedFact> factDependencies){
		List<ParameterUnit> parameterList2 = new ArrayList<ParameterUnit>();
		
		for(ParameterUnit parameterUnit : parameterList){
			Datatype type = parameterUnit.getType();
			if(!type.getName().equals(voidName)){
				  EObject contener =  type.eContainer();
				  boolean containsInternal = false;
				for(AbstractExtractedFact fact : factDependencies){
					KDMEntity factEnd = fact.getDependencyEnd();
					if(contener.equals(factEnd)){
						containsInternal = true;
						break;
					}
				}
				if(!containsInternal)
					parameterList2.add(parameterUnit);
			 }
		 }
		
		//remove all the dependencies related to the undesired parameter units
		
		return parameterList2;
	}
	

	/**
	 * 
	 * @param cu
	 * @return
	 */
	public List<AbstractExtractedFact> getRelationsInACu(ClassUnit cu, 
			                                             List<AbstractExtractedFact> factDependencies,
			                                             List<KDMEntity> packageList){
		List<ParameterUnit> parameterList = getMethodsParameter(cu);
		parameterList = removeRedundantParameters(parameterList, factDependencies);
		//build the connections corresponding to the parameter units
		List<AbstractExtractedFact> tripleList = buildConnectionsForParameterUnits(parameterList, cu, packageList);
		return tripleList;
	}
	
	/**
	 * 
	 * @param cu
	 * @return
	 */
	public  List<AbstractExtractedFact> getRelationsInAnIu(Datatype iu, List<KDMEntity> packageList){
		List<ControlElement> muList = ControlElementsExtractor.getInstance().getControlElementsFor1IU((InterfaceUnit) iu);
		
		//extract the signatures of each method unit
		List<org.eclipse.gmt.modisco.omg.kdm.code.Signature> signatureList = new ArrayList<org.eclipse.gmt.modisco.omg.kdm.code.Signature>();
		for(ControlElement mu : muList){
			List<AbstractCodeElement> codeElements = mu.getCodeElement();
			for(AbstractCodeElement codeElement : codeElements){
				if(codeElement instanceof org.eclipse.gmt.modisco.omg.kdm.code.Signature)
					signatureList.add((org.eclipse.gmt.modisco.omg.kdm.code.Signature) codeElement); 
			}
		}
		
		//extract all the parameter units from the signatures
		List<ParameterUnit> parameterList = new ArrayList<ParameterUnit>();
		for(org.eclipse.gmt.modisco.omg.kdm.code.Signature signature : signatureList){
			parameterList.addAll(signature.getParameterUnit());
		}
		
		//build the connections corresponding to the parameter units
		List<AbstractExtractedFact> tripleList = buildConnectionsForParameterUnits(parameterList, 
				                                                                iu, packageList);
		
		return tripleList;
	}
	
	/**
	 * 
	 * @param cu
	 * @return
	 */
	public  List<AbstractExtractedFact> getRelationsInAnEnumeratedType(Datatype enumType, 
			                                                           List<KDMEntity> packageList){
		List<ControlElement> muList = ControlElementsExtractor.getControlElementFor1EnumeratedType((EnumeratedType) enumType);
		
		//extract the signatures of each method unit
		List<org.eclipse.gmt.modisco.omg.kdm.code.Signature> signatureList = new ArrayList<Signature>();
		for(ControlElement mu : muList){
			List<AbstractCodeElement> codeElements = mu.getCodeElement();
			for(AbstractCodeElement codeElement : codeElements){
				if(codeElement instanceof org.eclipse.gmt.modisco.omg.kdm.code.Signature)
					signatureList.add((org.eclipse.gmt.modisco.omg.kdm.code.Signature) codeElement); 
			}
		}
		
		//extract all the parameter units from the signatures
		List<ParameterUnit> parameterList = new ArrayList<ParameterUnit>();
		for(org.eclipse.gmt.modisco.omg.kdm.code.Signature signature : signatureList){
			parameterList.addAll(signature.getParameterUnit());
		}
		
		//build the connections corresponding to the parameter units
		List<AbstractExtractedFact> tripleList = buildConnectionsForParameterUnits(parameterList, 
				                                                                enumType, packageList);
		
		return tripleList;
	}
	
	static Datatype extractTypeFromTemplate(TemplateType type){
		List<AbstractCodeRelationship> codeRelations = type.getCodeRelation();
		Datatype typeInsideTemplate = null;
		for(AbstractCodeRelationship relation : codeRelations){
			if(relation instanceof ParameterTo){
				typeInsideTemplate =  (Datatype) relation.getTo();
			}
		}
		return typeInsideTemplate;
	}
	
	/**
	 * to complete
	 * @return
	 */
	 List<AbstractExtractedFact>  buildConnectionsForParameterUnits(List<ParameterUnit> parameterList, 
			                                                        Datatype data, List<KDMEntity> packageList){
		List<AbstractExtractedFact> tripleList = new ArrayList<AbstractExtractedFact>();
		for(ParameterUnit parameterUnit : parameterList){
			Datatype type = parameterUnit.getType();
			if(!type.getName().equals("void")){
				if(type instanceof TemplateType){//the real type should be extracted from the template type
					type = extractTypeFromTemplate((TemplateType)type);
				}
				try{
					if((type != null) && !(type.getName().equals(unspecifiedNameUsedAsTemplate))){
						if(type instanceof ClassUnit)
							type = definedInsideTheFileOfAnotherClass((Datatype) type, packageList);
						if(type != null){
							AbstractExtractedFact FactDependency = new ElementaryFact(data, type, 1, 
									                                                  RealeityUtils.RelationsTypes.ParametersConnections.toString(),
									                                                  "");
							tripleList.add(FactDependency);
						}
					}
				}
				catch(NullPointerException e){
					
				}
			}
		}
		return tripleList;
	}
	 
	 /**
	  * 
	  * @param typeFrom
	  * @param factDependencies
	  * @param packageList
	  * @return
	  */
	 public List<AbstractExtractedFact> getParameterRelationsInDataType(Datatype typeFrom, 
                                                                        List<AbstractExtractedFact> factDependencies,
                                                                        List<KDMEntity> packageList){
			if (typeFrom instanceof ClassUnit)
				return getRelationsInACu((ClassUnit) typeFrom, factDependencies, packageList);
			else if (typeFrom instanceof InterfaceUnit)
				return getRelationsInAnIu(typeFrom, packageList);
			else if (typeFrom instanceof EnumeratedType)
				return getRelationsInAnEnumeratedType(typeFrom, packageList);
			else
				return new ArrayList<AbstractExtractedFact>();
}
	
}
