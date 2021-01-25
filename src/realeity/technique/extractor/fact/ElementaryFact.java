package realeity.technique.extractor.fact;

import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

/**
 * 
 * @author Alvine
 *
 */
public class ElementaryFact extends AbstractExtractedFact {
	String snippetLine;

	public ElementaryFact(KDMEntity dependencyStart, KDMEntity dependencyEnd, int dependencyWeight, 
	                      String dependencyName, String snippetLine) {
		super(dependencyStart, dependencyEnd, dependencyWeight, dependencyName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		
		return super.toString();
	}
	
}
