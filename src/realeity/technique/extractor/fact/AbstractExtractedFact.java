package realeity.technique.extractor.fact;

import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

import realeity.technique.util.KDMEntityFullName;


/**
 * 
 * @author Alvine
 *
 */
public abstract class AbstractExtractedFact implements Cloneable {
	
	protected int dependencyWeight;
	protected String dependencyName;
	protected KDMEntity dependencyStart;
	protected KDMEntity dependencyEnd; 
	
	/**
	 * Class's constructor.
	 * @param dependencyStart
	 * @param dependencyEnd
	 * @param dependencyWeight
	 * @param dependencyName
	 */
	public AbstractExtractedFact(KDMEntity dependencyStart, KDMEntity dependencyEnd, 
			                     int dependencyWeight, String dependencyName) {
		this.dependencyStart = dependencyStart;
		this.dependencyEnd = dependencyEnd;
		this.dependencyName = dependencyName;
		this.dependencyWeight = dependencyWeight;
	}
	
	public KDMEntity getDependencyEnd() {
		return dependencyEnd;
	}
	
	public String getDependencyName() {
		return dependencyName;
	}
	
	public KDMEntity getDependencyStart() {
		return dependencyStart;
	}
	
	public int getDependencyWeight() {
		return dependencyWeight;
	}
	
	public void setDependencyEnd(KDMEntity dependencyEnd) {
		this.dependencyEnd = dependencyEnd;
	}
	
	public void setDependencyName(String dependencyName) {
		this.dependencyName = dependencyName;
	}
	
	public void setDependencyStart(KDMEntity dependencyStart) {
		this.dependencyStart = dependencyStart;
	}
	
	public void setDependencyWeight(int dependencyWeight) {
		this.dependencyWeight = dependencyWeight;
	}
	
	@Override
	public String toString() {
		String txt = "(" + KDMEntityFullName.getInstance().determineSpaceName(dependencyStart) + "," 
	             +KDMEntityFullName.getInstance().determineSpaceName(dependencyEnd) + "," 
	             + dependencyWeight + "," 
	             + dependencyName + ")" ;
		return txt;
	}
	
	@Override
	protected Object clone() {
		AbstractExtractedFact factDependency = null;
		try {
			factDependency = (AbstractExtractedFact) super.clone();
			
		} 
		catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return factDependency;
	}

}
