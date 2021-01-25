package realeity.core.layering.algorithms.settings;
/**
 * License GPL.
 * @author Boaye Belle Alvine
 * 2014
 */
public class Setup {

	private double adjacencyPenalty;
	private double intraPenalty;
	private double skipPenalty;
	private double backPenalty;
	
	/**
	 * 
	 * @param adjacencyPenalty
	 * @param intraPenalty
	 * @param skipPenalty
	 * @param backPenalty
	 */
	public Setup(double adjacencyPenalty, double intraPenalty,
			     double skipPenalty, double backPenalty) {
		this.adjacencyPenalty = adjacencyPenalty;
		this.intraPenalty = intraPenalty;
		this.skipPenalty = skipPenalty;
		this.backPenalty = backPenalty;
	}
	
	public double getAdjacencyPenalty() {
		return adjacencyPenalty;
	}
	
	public double getIntraPenalty() {
		return intraPenalty;
	}
	
	public double getSkipPenalty() {
		return skipPenalty;
	}
	
	public double getBackPenalty() {
		return backPenalty;
	}
	
	public void setAdjacencyPenalty(double adjacencyPenalty) {
		this.adjacencyPenalty = adjacencyPenalty;
	}
	public void setIntraPenalty(double intraPenalty) {
		this.intraPenalty = intraPenalty;
	}
	
	public void setSkipPenalty(double skipPenalty) {
		this.skipPenalty = skipPenalty;
	}
	public void setBackPenalty(double backPenalty) {
		this.backPenalty = backPenalty;
	}
	
	@Override
	public String toString() {
		String txt = "alpha = " + adjacencyPenalty + ", beta = " + intraPenalty
				     + ", gamma = " + skipPenalty + ", delta = " + backPenalty;
		return txt;
	}
	
}
