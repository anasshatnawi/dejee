package realeity.technique.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

/**
 * Compute the full name of a KDM entity (Package, etc).
 * @author Alvine
 * 2013  21:30:45
 */
public class KDMEntityFullName {

	/**
	* Class's constructor. 
	* Its visibility is private in order not to allow the other classes to create 
	* many instances of KDMEntityFullName.
	*/
	private KDMEntityFullName(){

	}

	/**
	* unique instance of KDMEntityFullName.
	*/
	private static KDMEntityFullName uniqueInstance;

	/**
	* Method insuring that a unique instance of KDMEntityFullName is created.
	* @return uniqueInstance
	*/
	public static KDMEntityFullName getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new KDMEntityFullName();
		}
		return uniqueInstance;
	}
	
	/**
	 * Reconstitute the whole name of the entity packag with the "." and the ".*" .
	 * @param packag
	 * @return
	 */
	public String determineSpaceName(KDMEntity entity){
		if(entity instanceof Package){
			Package packag = (Package) entity;
			String entityName = packag.getName(); 
			entityName = "";
			String endName = "";
			//check whether the package contains some packages 
			EList<AbstractCodeElement> abstractCodeElements = packag.getCodeElement();
			boolean packageInside = false;
			for(AbstractCodeElement codeElement: abstractCodeElements){
				if(codeElement instanceof Package){
					packageInside = true;
					endName = ".*";
					break;
				}
			}
			
			//concatenate all the names of the package with those of the packages containing it
			List<String> concatenatedNames = new ArrayList<String>();
			EObject pack2 = packag;
			try{
				while(pack2 instanceof Package){
					Package parentPackage = (Package) pack2.eContainer();
					concatenatedNames.add(parentPackage.getName() + ".");
					pack2 = parentPackage;
				}
			}
			catch(ClassCastException e){//parent is a code model
				//System.out.println("---Error. This instance is not a package");
			}
			
			int size = concatenatedNames.size();
			for(int indexName = 0; indexName < concatenatedNames.size(); indexName ++){
				//browse the list from the last element to the first
				entityName += concatenatedNames.get(size - indexName -1);
			}
			entityName += packag.getName();
			if(packageInside)
				entityName += endName;
			return entityName;

		}
		else return entity.getName();
	}
}
