package realeity.technique.util;


/**
 * 
 * @author Alvine
 *
 */
public class RealeityUtils {
	/**
	 * 
	 * @author Alvine
	 *
	 */
	public enum RelationsTypes {
		Calls, Extends, Imports, Implements, Creates, Unspecified, 
		ConnectionsFromStorableUnit, ParametersConnections, 
		UsesTypes, Aggregated
	}
	
	/**
	 * 
	 * @author Alvine
	 *
	 */
	public enum ModelNames {
		CodeModel, StructureModel, InventoryModel
	}

	public enum GranularityNames {
		Class, Module
	}
	
	public enum MainActions {
		Extraction, Layering, None
	}
	
	public enum MetricTypes {
		AdjacencyCallsMetric, IntraCallsMetric, SkipCallsMetric, 
		BackCallsMetric
	}
	
	public static String[] IrrelevantPackagesNames = {
		"org.omg.*",
		"org.omg.CORBA",
		"org.w3c.*",
		"org.w3c.dom",
		"org.jhotdraw.test",
		"org.jhotdraw.test.contrib",
		"org.jhotdraw.test.figures",
		"org.jhotdraw.test.framework",
		"org.jhotdraw.test.samples.javadraw",
		"org.jhotdraw.test.samples.minimap",
		"org.jhotdraw.test.samples.net",
		"org.jhotdraw.test.samples.nothing",
		"org.jhotdraw.test.samples.pert",
		"org.jhotdraw.test.standard",
		"org.jhotdraw.test.util",
		"org.jhotdraw.test.util.collections.jdk11",
		"org.jhotdraw.test.util.collections.jdk12",
		"org.jhotdraw.test.util.*",
		"org.jhotdraw.test.*",
		"CH.ifa.draw.test.*",
		"CH.ifa.draw.test.contrib",
		"CH.ifa.draw.test.figures",
		"CH.ifa.draw.test.framework",
		"CH.ifa.draw.test.samples.javadraw",
		"CH.ifa.draw.test.samples.minimap",
		"CH.ifa.draw.test.samples.net",
		"CH.ifa.draw.test.samples.nothing",
		"CH.ifa.draw.test.samples.pert",
		"CH.ifa.draw.test.standard",
		"CH.ifa.draw.test.util.*",
		"CH.ifa.draw.test.util.collections.jdk11",
		"CH.ifa.draw.test.util.collections.jdk12",
		"net.roydesign.event",
		"net.roydesign.app",
		"org.xml.sax.*",
		"org.xml.sax.helpers"
	};


}
