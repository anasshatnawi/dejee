package realeity.technique.extractor.structuremodel;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KDMModel;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

/**
 * Retrieve a structure model from a KDM segment representing the analyzed project.
 * @author Alvine
 *
 */
public class StructureModelExtractor {

	/**
	 * Get the list of structure models contained in a KDM segment.
	 * @param maingSegment
	 * @return
	 */
	public static  EList<StructureModel>  retrieveStructureModels(Segment maingSegment){
		//get all the models contained in the segment
		 EList<StructureModel> structureModels = new BasicEList<StructureModel>();
		if(maingSegment != null){
			EList<KDMModel> allTheModels = maingSegment.getModel();
			//get all the structure models
			for(KDMModel kdmModel : allTheModels){
				if(kdmModel instanceof StructureModel)
					structureModels.add((StructureModel)kdmModel);
			}
		}
		
		 return structureModels;
	}
	
}
