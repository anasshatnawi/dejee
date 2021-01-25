package realeity.core;
/**
 * License GPL.
 * @author Boaye Belle Alvine
 * 2014
 */
public class LayersMetrics {
	private int numberOfAdjacencyCalls = 0;
	private int numberOfIntraCalls = 0;
	private int numberOfSkipCalls = 0;
	private int numberOfBackCalls = 0;

	
	public LayersMetrics(int numberOfAdjacencyCalls,
			             int numberOfIntraCalls,
			             int numberOfSkipCalls,
			             int numberOfBackCalls) {
		this.numberOfAdjacencyCalls = numberOfAdjacencyCalls;
		this.numberOfIntraCalls = numberOfIntraCalls;
		this.numberOfSkipCalls = numberOfSkipCalls;
		this.numberOfBackCalls = numberOfBackCalls;
	}
	
	public int getNumberOfAdjacencyCalls() {
		return numberOfAdjacencyCalls;
	}
	
	public int getNumberOfIntraCalls() {
		return numberOfIntraCalls;
	}
	
	public int getNumberOfSkipCalls() {
		return numberOfSkipCalls;
	}
	
	
	public int getNumberOfBackCalls() {
		return numberOfBackCalls;
	}
	
	public void setNumberOfAdjacencyCalls(int numberOfAdjacencyCalls) {
		this.numberOfAdjacencyCalls = numberOfAdjacencyCalls;
	}
	
	public void setNumberOfIntraCalls(int numberOfIntraCalls) {
		this.numberOfIntraCalls = numberOfIntraCalls;
	}
	
	public void setNumberOfSkipCalls(int numberOfSkipCalls) {
		this.numberOfSkipCalls = numberOfSkipCalls;
	}
	
	public void setNumberOfBackCalls(int numberOfBackCalls) {
		this.numberOfBackCalls = numberOfBackCalls;
	}
	
	public void setMetrics(int numberOfAdjacencyCalls,
                           int numberOfIntraCalls,
                           int numberOfSkipCalls,
                           int numberOfBackCalls) {
		this.numberOfAdjacencyCalls = numberOfAdjacencyCalls;
		this.numberOfIntraCalls = numberOfIntraCalls;
		this.numberOfSkipCalls = numberOfSkipCalls;
		this.numberOfBackCalls = numberOfBackCalls;
    }
	
	@Override
	public String toString() {
		String txt = "Metrics : Adjac= " + numberOfAdjacencyCalls 
				     + ", Intra= " + numberOfIntraCalls  
				     + ",  Skip= " + numberOfSkipCalls +
				     ", Back= " + numberOfBackCalls;
		return txt;
	}

}
