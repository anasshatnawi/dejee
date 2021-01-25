/**
 * 
 */
package uqam.dejee.kdm.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.action.ActionElement;
import org.eclipse.gmt.modisco.omg.kdm.action.BlockUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.ClassUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeItem;
import org.eclipse.gmt.modisco.omg.kdm.code.CodeModel;
import org.eclipse.gmt.modisco.omg.kdm.code.Datatype;
import org.eclipse.gmt.modisco.omg.kdm.code.MethodUnit;
import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;

import realeity.technique.extractor.codemodel.entities.ActionElementsExtractor;
import realeity.technique.extractor.codemodel.entities.ClassUnitExtractor;
import realeity.technique.extractor.codemodel.entities.ControlElementsExtractor;
import realeity.technique.extractor.codemodel.entities.PackageExtractor;

/**
 * @author Anas Shatnawi Email: anasshatnawi@gmail.com 2017
 *
 * 
 */
public class KdmExtractors {

	private static KdmExtractors uniqueInstance;

	/**
	 * 
	 */
	private KdmExtractors() {
		// TODO Auto-generated constructor stub
	}

	public static KdmExtractors getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new KdmExtractors();
		}
		return uniqueInstance;
	}

	/**
	 * This method extracts a code model from a given KDM segment
	 * 
	 * @param kdmSegment
	 * @return
	 */
	public CodeModel extractCodeModelFromSegment(Segment kdmSegment) {
		CodeModel projectCodeModel = null;
		EList<KDMModel> kdmModel = kdmSegment.getModel();
		projectCodeModel = (CodeModel) kdmModel.get(0);
		if (projectCodeModel == null) {
			System.err.println("Error in CodeModel extraction");
		}
		return projectCodeModel;
	}

	public List<Package> extractAllPackagesFromSegment(Segment kdmSegment) {
		List<Package> packages = PackageExtractor.getInstance().getAllRelevantInternalPackages(kdmSegment);
		return packages;
	}

	public List<ClassUnit> extractAllClassUnitsFromPackages(List<Package> packageList) {
		return ClassUnitExtractor.getInstance().getClassUnits(packageList);
	}

	public MethodUnit findMethodByNameinClassUnit(String name, ClassUnit classUnit) {
		MethodUnit method = null;
		EList<CodeItem> codeElements = classUnit.getCodeElement();
		for (CodeItem cItem : codeElements) {
			if (cItem instanceof MethodUnit) {
				if (cItem.getName().equals(name)) {
					return (MethodUnit) cItem;
				}
			}
		}
		return method;
	}

	public List<ActionElement> extractSequenceofActionElements(MethodUnit method) {
		List<ActionElement> actionList = new ArrayList<ActionElement>();
		List<Datatype> dataForCatchConnections = new ArrayList<>();
		List<BlockUnit> buList = ControlElementsExtractor.getInstance().getBlockUnits(method);
		for (BlockUnit bu : buList) {
			actionList.addAll(
					ActionElementsExtractor.getInstance().getActionElementsFromABlock(bu, dataForCatchConnections));
		}
		return actionList;
	}

}
