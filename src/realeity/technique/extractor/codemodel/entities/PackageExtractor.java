package realeity.technique.extractor.codemodel.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;

import realeity.technique.extractor.AbstractKDMEntityExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.util.KDMEntityFullName;
import realeity.technique.util.RealeityUtils;


/**
 * 
 * @author Alvine
 *
 */
public class PackageExtractor extends AbstractKDMEntityExtractor {

	/**
	 * Class's constructor. 
	 * Its visibility is private in order not to allow the other classes to create 
	 * many instances of LayeringFacade.
	 */
	private PackageExtractor(){
		
	}
	
	/**O
	 * unique instance of LayeringFacade.
	 */
	private static PackageExtractor uniqueInstance;
	
	/**
	 * Method insuring that a unique instance of PackageExtractor is created.
	 * @return uniqueInstance
	 */
	public static PackageExtractor getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new PackageExtractor();
		}
		return uniqueInstance;
	}


	/**
	 * Remove all the packages that are not significant for the analysis of the project
	 * @param packageList list of the packages extracted from the project
	 * @return
	 */
	public static List<KDMEntity> removeIrrelevantPackages(List<KDMEntity> packageList){
		//get the full name of the packages that are irrelevant for the system
		List<String> irrelevantNames = Arrays.asList(RealeityUtils.IrrelevantPackagesNames);
		
		//remove all the irrelevant packages from the list of the packages
		List<KDMEntity> relevantPackageList = new ArrayList<KDMEntity>();
		for(KDMEntity pack : packageList){
			String nodeName = KDMEntityFullName.getInstance().determineSpaceName(pack);
			if(!irrelevantNames.contains(nodeName))
				relevantPackageList.add(pack); 
		}
		
		/*for(Package pack : relevantPackageList){
			System.out.println("relevant package: "+packageFullName(pack));
		}*/
		return relevantPackageList;
	}
	
	
	/**
	 * Remove all the packages that are not connected to other through connections
	 * @param connections
	 * @param packageList
	 * @return
	 */
	public  List<KDMEntity>  removeDisconnectedPackages(List<AbstractExtractedFact> connections,
			                                            List<KDMEntity> packageList){
		 List<KDMEntity> packageWithConnections = new ArrayList<KDMEntity>();
		 for(KDMEntity pack : packageList){
			 for(AbstractExtractedFact conn : connections){
				 KDMEntity fromPackage = conn.getDependencyStart();
				 KDMEntity endPackage = conn.getDependencyEnd();
				 if(((pack == fromPackage) || (pack == endPackage)) 
					 && (!packageWithConnections.contains(pack)))
					 packageWithConnections.add(pack);
			 }
			 
		 }
		 
		 return packageWithConnections;
	}
	
	/**
	 * Returns recursively all the packages contained in pack.
	 * @param pack
	 * @return
	 */
	public List<Package> getInternalPackages (Package pack){
		List<Package> packageList = new ArrayList<Package>();
		EList<AbstractCodeElement> abstractCodeElements = pack.getCodeElement();
		for(AbstractCodeElement codeElement: abstractCodeElements){
			if(codeElement instanceof Package){
				Package internalPackage = (Package) codeElement;
				packageList.add(internalPackage);//adding all the packages contained in internalPackage
				packageList.addAll(getInternalPackages(internalPackage));
			}
		}
		return packageList;
	}
	
	
	/**
	 * Returns the list of the packages contained in the segment. It only returns the 
	 * packages that constitute the architecture of the system, and doesn't take into 
	 * account the packages nested inside other packages.
	 * @param segment root of the KDM representation. contains all the KDM models of the analyzed system
	 * @return
	 */
	public List<Package> getPackages(Segment segment){
		//get all the models contained in the segment
		EList<KDMModel> allTheModels = segment.getModel();
		
		//get all the code models
		EList<CodeModel> codeModels = new BasicEList<CodeModel>();
		for(KDMModel kdmModel : allTheModels){
			if(kdmModel instanceof CodeModel)
				codeModels.add((CodeModel)kdmModel);
		}
		
		//get all the abstract code elements contained in these code models
		EList<AbstractCodeElement> abstractCodeElements = new BasicEList<AbstractCodeElement>();
		for(CodeModel codModel : codeModels){
			abstractCodeElements.addAll(codModel.getCodeElement());
		}
		
		//get all the packages contained in the abstractCodeElements list. 
		List<Package> packageSet = new ArrayList<Package>();
		for(AbstractCodeElement abstractcode: abstractCodeElements){
			if(abstractcode instanceof Package)
				packageSet.add((Package)abstractcode);
		}
		
		return packageSet;
	}
	
	/**
	 * Get the code model containing only the packages of the analyzed project. 
	 * @param projectSegment
	 * @return
	 */
	public CodeModel getProjectcodeModel(Segment projectSegment){
		EList<KDMModel> kdmModel = projectSegment.getModel();
		CodeModel projectCodeModel = (CodeModel) kdmModel.get(0);
		return projectCodeModel;
	}
	
	/**
	 * Returns the list of the packages contained in the segment. 
	 * It only returns the packages that constitute the architecture of the system, and doesn't take
	 * into account the packages nested inside other packages, nor the java packages.
	 * @param segment root of the KDM representation. contains all the KDM models of the analyzed system
	 * @return
	 */
	public List<Package> getAllRelevantPackages(Segment KDMsegment){
		CodeModel projectCodeModel = getProjectcodeModel(KDMsegment);
		//get all the abstract code elements contained in these code models
		EList<AbstractCodeElement> abstractCodeElements = projectCodeModel.getCodeElement();
		
		//get all the packages contained in the abstractCodeElements list. 
		List<Package> packageSet = new ArrayList<Package>();
		for(AbstractCodeElement abstractcode: abstractCodeElements){
			if(abstractcode instanceof Package)
				packageSet.add((Package)abstractcode);
		}
				
		return packageSet;
	}
	
	/**
	 * Returns all the packages included in the system, even the packages included in other packages
	 * @param pack
	 * @return
	 */
	public List<Package>  getAllInternalPackages(Segment segment){
		//find all the packages forming the subsystems of the system
		List<Package> packageSet = getPackages(segment);
		
		
		//find all the packages inside each subsystem represented by a package of packageSet
		List<Package> packageList = new ArrayList<Package>();
		for(Package pack: packageSet){
			packageList.addAll(getInternalPackages(pack));
		}
		//Reunite all the packages contained in the system
		packageSet.addAll(packageList);
		return packageSet;
		
	}
	
	/**
	 * Returns all the packages contained in the system, even the packages included in other packages. 
	 * It only returns the packages that constitute the architecture of the 
	 * system, and doesn't take into account the java packages
	 * @param segment root of the KDM representation. It contains all the KDM models of the analyzed system
	 * @return
	 */
	public List<Package> getAllRelevantInternalPackages(Segment segment){
		List<Package> packageSet = getAllRelevantPackages(segment);
		List<Package> packageList = new ArrayList<Package>(); 
		packageList.addAll(packageSet);
		for(Package pack : packageSet){
			//System.out.println("package père: "+pack.getName());
			List<Package> internalPackages = getInternalPackages(pack);
			packageList.addAll(internalPackages);
		}
		return packageList;
	}
	
	/**
	 * Get the package containing the entity.
	 * @param entity
	 * @param packageList
	 * @return
	 */
	public Package findPackageTo(KDMEntity entityTo, 
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
 			//the container of the entityTo might be in a codeModel 
 			//external to the codeModel of the analyzed system
 			
 		}
 		return pack; 		
	}
}






