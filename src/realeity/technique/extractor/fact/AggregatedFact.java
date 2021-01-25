package realeity.technique.extractor.fact;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

/**
 * 
 * @author Alvine
 *
 */
public class AggregatedFact extends AbstractExtractedFact{
	private List<AbstractExtractedFact> internalFacts = new ArrayList<AbstractExtractedFact>();

	public AggregatedFact(KDMEntity dependencyStart, KDMEntity dependencyEnd,
			              int dependencyWeight, String dependencyName, 
			              List<AbstractExtractedFact> internalFacts) {
		super(dependencyStart, dependencyEnd, dependencyWeight, dependencyName);
		this.internalFacts = internalFacts;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		return super.toString();
	}

}
