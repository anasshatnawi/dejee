package realeity.technique.extractor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.code.AbstractCodeElement;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.EnumeratedType;
import org.eclipse.gmt.modisco.omg.kdm.code.InterfaceUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.technique.extractor.AbstractKDMRelationshipExtractor;
import realeity.technique.extractor.codemodel.entities.PackageExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;
import realeity.technique.util.RealeityUtils;

/**
 * License GPL Build dependencies at the module level (packages, etc.). These
 * modules comprise compilationUnit.
 * 
 * @author Alvine Boaye Belle
 * 
 */
public class ModuleDependencyBuilder extends AbstractDependencyBuilder {

	/**
	 * Class's constructor. Its visibility is private in order not to allow the
	 * other classes to create many instances of LayeringFacade.
	 */
	private ModuleDependencyBuilder() {

	}

	/**
	 * unique instance of LayeringFacade.
	 */
	private static ModuleDependencyBuilder uniqueInstance;

	/**
	 * Method insuring that a unique instance of ModuleDependencyBuilder is
	 * created.
	 * 
	 * @return uniqueInstance
	 */
	public static ModuleDependencyBuilder getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new ModuleDependencyBuilder();
		}
		return uniqueInstance;
	}

	protected List<KDMEntity> retrieveRelevantModules(Segment mainSegment) {
		// extractedModules is not used since it is generally an empty list
		List<KDMEntity> extractedPackages = new ArrayList<KDMEntity>();
		extractedPackages = getAllRelevantInternalPackages(mainSegment);// PackagesExtraction.getAllRelevantPackages(segment);
		extractedPackages = removeIrrelevantPackages(extractedPackages,
				AlgorithmContext.irrelevantNamesFile);

		List<KDMEntity> extractedClasses = new ArrayList<KDMEntity>(); 
		extractAllClassesFromPackages(extractedPackages, extractedClasses);
		return extractedClasses;
	}


	/**
	 * extract ClassUnits from a given Segment
	 * 
	 * @param mainSegment
	 *            contains KDM model
	 * @param extractedPackages: a list of KDMEntity(s) sent to hold the results
	 */
	public void extractAllClassesFromPackages(
			List<KDMEntity>  allPackages, List<KDMEntity> allClasses) {
		for (KDMEntity packag : allPackages) {
			EList<AbstractCodeElement> codeElements = ((Package) packag)
					.getCodeElement();
			for (AbstractCodeElement clazz : codeElements) {
				if (clazz instanceof ClassUnit || clazz instanceof InterfaceUnit || clazz instanceof EnumeratedType) {
					allClasses.add(clazz);
				} 
			}
		}
	}

//	protected List<KDMEntity> retrieveRelevantModules2(Segment mainSegment) {
//		// extractedModules is not used since it is generally an empty list
//		List<KDMEntity> extractedPackages = new ArrayList<KDMEntity>();
//		extractedPackages = getAllRelevantInternalPackages(mainSegment);// PackagesExtraction.getAllRelevantPackages(segment);
//		extractedPackages = removeIrrelevantPackages(extractedPackages,
//				AlgorithmContext.irrelevantNamesFile);
//		return extractedPackages;
//	}

	/**
	 * Remove all the packages that are not significant for the analysis of the
	 * project
	 * 
	 * @param packageList
	 *            list of the packages extracted from the project
	 * @param irrelevantNamesFile
	 * @return
	 */
	public List<KDMEntity> removeIrrelevantPackages(
			List<KDMEntity> packageList, String irrelevantNamesFile) {
		// get the full name of the packages that are irrelevant for the system
		List<KDMEntity> relevantPackageList = PackageExtractor.getInstance()
				.removeIrrelevantPackages(packageList);
		return relevantPackageList;
	}

	/**
	 * Returns recursively all the packages contained in pack.
	 * 
	 * @param pack
	 * @return
	 */
	public List<KDMEntity> getInternalPackages(KDMEntity pack) {
		List<KDMEntity> packageList = new ArrayList<KDMEntity>();
		EList<AbstractCodeElement> abstractCodeElements = ((Package) pack)
				.getCodeElement();
		for (AbstractCodeElement codeElement : abstractCodeElements) {
			if (codeElement instanceof Package) {
				Package internalPackage = (Package) codeElement;
				packageList.add(internalPackage);
				// add all the packages contained in internalPackage
				packageList.addAll(getInternalPackages(internalPackage));
			}
		}
		return packageList;
	}

	/**
	 * It returns the list of the packages contained in the segment. It only
	 * returns the packages that constitute the architecture of the system, and
	 * doesn't take into account the packages nested inside other packages.
	 * 
	 * @param segment
	 *            root of the KDM representation. contains all the KDM models of
	 *            the analyzed system
	 * @return
	 */
	public List<KDMEntity> getPackages(Segment segment) {
		// get all the models contained in the segment
		EList<KDMModel> allTheModels = segment.getModel();

		// get all the code models
		EList<CodeModel> codeModels = new BasicEList<CodeModel>();
		for (KDMModel kdmModel : allTheModels) {
			if (kdmModel instanceof CodeModel)
				codeModels.add((CodeModel) kdmModel);
		}

		// get all the abstract code elements contained in these code models
		EList<AbstractCodeElement> abstractCodeElements = new BasicEList<AbstractCodeElement>();
		for (CodeModel codModel : codeModels) {
			abstractCodeElements.addAll(codModel.getCodeElement());
		}

		// get all the packages contained in the abstractCodeElements list.
		List<KDMEntity> packageSet = new ArrayList<KDMEntity>();
		for (AbstractCodeElement abstractcode : abstractCodeElements) {
			if (abstractcode instanceof Package)
				packageSet.add((Package) abstractcode);
		}

		return packageSet;
	}

	/**
	 * Get the code model containing only the packages of the analyzed project.
	 * 
	 * @param projectSegment
	 * @return
	 */
	public CodeModel getProjectcodeModel(Segment projectSegment) {
		EList<KDMModel> kdmModel = projectSegment.getModel();
		CodeModel projectCodeModel = (CodeModel) kdmModel.get(0);
		return projectCodeModel;
	}

	/**
	 * Returns the list of the packages contained in the segment. It only return
	 * the packages that constitute the architecture of the system, and doesn't
	 * take into account the packages nested inside other packages, nor the java
	 * packages.
	 * 
	 * @param segment
	 *            root of the KDM representation. contains all the KDM models of
	 *            the analyzed system
	 * @return
	 */
	public List<KDMEntity> getAllRelevantPackages(Segment KDMsegment) {
		// TODO deplacer toutes les methodes relatives aux pagages et aux
		// codemodel
		CodeModel projectCodeModel = getProjectcodeModel(KDMsegment);
		// get all the abstract code elements contained in these code models
		EList<AbstractCodeElement> abstractCodeElements = projectCodeModel
				.getCodeElement();

		// get all the packages contained in the abstractCodeElements list.
		List<KDMEntity> packageSet = new ArrayList<KDMEntity>();
		for (AbstractCodeElement abstractcode : abstractCodeElements) {
			if (abstractcode instanceof Package)
				packageSet.add((Package) abstractcode);
		}

		return packageSet;
	}

	/**
	 * Returns all the packages included in the system, even the packages
	 * included in other packages.
	 * 
	 * @param pack
	 * @return
	 */
	public List<KDMEntity> getAllInternalPackages(Segment segment) {
		// find all the packages forming the subsystems of the system
		List<KDMEntity> packageSet = getPackages(segment);

		// find all the packages inside each subsystem represented by a package
		// of packageSet
		List<KDMEntity> packageList = new ArrayList<KDMEntity>();
		for (KDMEntity pack : packageSet) {
			packageList.addAll(getInternalPackages(pack));
		}
		// Reunite all the packages contained in the system
		packageSet.addAll(packageList);
		return packageSet;

	}

	/**
	 * It returns all the packages contained in the system, even the packages
	 * included in other packages. it only return the packages that constitute
	 * the architecture of the system, and doesn't take into account the java
	 * packages
	 * 
	 * @param segment
	 *            root of the KDM representation. contains all the KDM models of
	 *            the analyzed system
	 * @return
	 */
	public List<KDMEntity> getAllRelevantInternalPackages(Segment segment) {
		List<KDMEntity> packageSet = getAllRelevantPackages(segment);
		List<KDMEntity> packageList = new ArrayList<KDMEntity>();
		packageList.addAll(packageSet);
		for (KDMEntity pack : packageSet) {
			// System.out.println("package père: "+pack.getName());
			List<KDMEntity> internalPackages = getInternalPackages(pack);
			packageList.addAll(internalPackages);
		}
		return packageList;
	}

	@Override
	protected List<AbstractExtractedFact> extractFactsBetweenEntities(
			List<KDMEntity> relevantEntities, List<KDMEntity> packageList) {
		List<AbstractExtractedFact> entitiesFacts = DataDependencyBuilder
				.getInstance().extractFactsBetweenEntities(relevantEntities,
						packageList);
		List<AbstractExtractedFact> moduleFacts = new ArrayList<AbstractExtractedFact>();

		
		
		for (AbstractExtractedFact dependency : entitiesFacts) {
			KDMEntity startContainer = AbstractKDMRelationshipExtractor
					.findPackageTo(dependency.getDependencyStart(), packageList);
			KDMEntity endContainer = AbstractKDMRelationshipExtractor
					.findPackageTo(dependency.getDependencyEnd(), packageList);

			if (startContainer != endContainer) {
				AbstractExtractedFact fact = new ElementaryFact(startContainer,
						endContainer, dependency.getDependencyWeight(),
						dependency.getDependencyName(), "");
				moduleFacts.add(fact);
			}

		}

		// extractedFacts = removeExternalDatatypes(extractedFacts,
		// packageList);

		moduleFacts = sumWeights(moduleFacts);

		/*
		 * extractedFacts =
		 * removeDependenciesRelatedToAncestors(relevantEntities,
		 * extractedFacts, packageList);
		 */

		System.err.println("number of Facts is "+ moduleFacts.size());
	    for (AbstractExtractedFact fact: moduleFacts){
	    	System.err.println(fact);
	    }
//		return moduleFacts;
	    return entitiesFacts;
	}

	@Override
	protected List<AbstractExtractedFact> sumWeights(
			List<AbstractExtractedFact> extractedFacts) {
		// if the same FactDependency (packA, packB, 1) appears x times in
		// extractedFacts,
		// then a single FactDependency (packA, packB, x) should replace it
		List<AbstractExtractedFact> duplicatesWithWeight = new ArrayList<AbstractExtractedFact>();
		// sum the weights of the similar extractedFacts (same from package, end
		// package and relation)
		// and creates the corresponding FactDependency
		for (AbstractExtractedFact dependency : extractedFacts) {
			KDMEntity entityFrom = dependency.getDependencyStart();
			KDMEntity entityEnd = dependency.getDependencyEnd();
			int weight = 0;
			for (AbstractExtractedFact otherDependency : extractedFacts) {
				KDMEntity otherEntityFrom = otherDependency
						.getDependencyStart();
				KDMEntity otherEntityEnd = otherDependency.getDependencyEnd();
				if ((entityFrom.equals(otherEntityFrom))
						&& (entityEnd.equals(otherEntityEnd)))
					weight++;
			}
			AbstractExtractedFact newFactDependency = new ElementaryFact(
					entityFrom, entityEnd, weight,
					RealeityUtils.RelationsTypes.Unspecified.toString(), "");// replace
																				// it
																				// by
																				// aggregatedFact
			duplicatesWithWeight.add(newFactDependency);

		}
		// remove the duplicates in duplicatesWithWeight
		List<AbstractExtractedFact> noDuplicates = removeDuplicatedDependencies(duplicatesWithWeight);
		return noDuplicates;
	}

}
