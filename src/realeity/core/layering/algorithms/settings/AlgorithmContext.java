package realeity.core.layering.algorithms.settings;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import realeity.application.data.GeneratedData;
import realeity.core.layering.algorithms.IRecoveryAlgorithm;
import realeity.core.layering.algorithms.sahc.LayeringAlgorithm;
import realeity.core.layering.algorithms.sahc.RecursiveRelayeringAlgo;
import realeity.core.layering.algorithms.sahc.SAHCRelayeringAlgo;
import realeity.core.layering.algorithms.tabu.TabuSearchAlgorithmKDM;
import realeity.technique.extractor.structuremodel.KDMModelsGeneration;
import realeity.technique.storage.kdm.KDMStorage;


/**
 * Contains all the parameters (paths, files names) required to run the algorithms.
 * @author Alvine Boaye Belle
 * 2013  17:06:58
 */
public class AlgorithmContext {
	
	/**
     * Path indicating where the KDM file representing the analyzed system is stored.
	 */
	public static String kdmFilePath = "E:/PhD/Java/MoDiscoWorkspace/JHotDraw7.0.6/JHotDraw 7.0.6_kdm.xmi";
	// added by Anas to refer to the JSP model path
	public static String jspModelFilePath = "E:/PhD/Java/MoDiscoWorkspace/JHotDraw7.0.6/JHotDraw 7.0.6_jsp.xmi";
		
	
	public static IRecoveryAlgorithm chosenAlgo = null;
	public static IRecoveryAlgorithm layeringAlgo = new LayeringAlgorithm();
	public static IRecoveryAlgorithm relayeringAlgorithm = new SAHCRelayeringAlgo();
	public static IRecoveryAlgorithm tabuSearchAlgorithm = new TabuSearchAlgorithmKDM(0,0);
	//public static IRecoveryAlgorithm recursiveLayeringAlgorithm = new RecursiveAggregationAlgorithm();
	public static IRecoveryAlgorithm aBestRelayeringAlgorithm = new RecursiveRelayeringAlgo(null);
	//public static IRecoveryAlgorithm namingAlgorithm = new NamingAlgorithm();
	
	public static String kdmProjectPath = "";
	public static String contextPath = "ContextDirectory";
	public static String graphsPath = "/ReALEITY_Trace/Layering_Results/"; //"/Trace/Layering_Results/";
	public static String initialMDGPath = "/ReALEITY_Trace/Module_Dependency_Graphs/"; //"/Trace/Module_Dependency_Graphs/";
	public static String dotFileName = "graphForGraphViz.txt";
	public static String zestFileName = "graphForZest.txt";
	public static String initialMDGGraphViz = "MDG_GraphViz.txt";
	public static String initialMDGZest = "MDG_Zest.txt";

	public static int nbDefaultLayers = 3;
	public static Setup algorithmParameters = new Setup(0, 2, 1, 4);//{adjacence, intra/inside, skip, back}
	public static int layerToSplitNumber = 2;
	public static int nbLayersForSplit = 3;
	public static boolean computeOmnipresent = true;
	public static String fitnessFunction = "";
	public static  int tabuMaxIterations = 1000;
	public static  int tabuListSize = 10;
	public static int randomIterations = 50;
	//public static int AGGREGATION_MAX_LEVEL = 4;
	public static String layeringInTextFilePath = "graphForTextEditor.txt";


	
	/**
	 * Indicates whether the KDM of the analyzed system will be used as an input 
	 * to build the initial partition.
	 */
	public static boolean withKDM = true;

	
	/**
	 * List of packages that should not be considered during the reconstruction phase.
	 */
	public static String irrelevantNamesFile = "irrelevantPackageNames.txt";
	
	/**
	 * Path indicating where the connections of the analyzed system are stored.
	 * Is necessary when the variable withKDM is set to false.
	 */
	public static String storedConnectionsPath = "D:/MoDiscoWorkspace/junit-4.10-src/storedConnections.txt";
			//"D:/MoDiscoWorkspace/LayeringWithKDM_/InputConnections/JHotDrawConnections.txt";
	
	public enum WizardArguments {

		GraphFileName, StructureModelLayering, DiscardOmnipresent, ChosenAlgorithm, FitnessFunction,
		SkipCallsPenalty, AdjacencyPenalty, IntraPenalty, BackPenalty, nbSpecifiedLayers,
		TabuMaxIter, TabuListSize, RandomIter;
	}
	
	public static Map<String, Integer> mapAlgo = mapAlgorithms();
	
	/**
	 * Map each algorithm to a number.
	 * @return
	 */
	private static Map<String, Integer> mapAlgorithms(){
		Map<String, Integer> mapAlgos = new HashMap<String, Integer>();
		mapAlgos.put(AlgorithmContext.layeringAlgo.getClass().getName(), 1);
		mapAlgos.put(AlgorithmContext.relayeringAlgorithm.getClass().getName(), 2);
		mapAlgos.put(AlgorithmContext.aBestRelayeringAlgorithm.getClass().getName(), 3);
		mapAlgos.put(AlgorithmContext.tabuSearchAlgorithm.getClass().getName(), 4);
		//mapAlgos.put(AlgorithmContext.recursiveLayeringAlgorithm.getClass().getName(), 4);
		return mapAlgos;
	}
	
	/**
	 * Allows to set the values to the context.
	 * @param contextMap
	 * @param resultModel
	 */
	public static  StructureModel specifyContext(Map<String, String> contextMap,
			                                     Segment mainSegment){
		
		
		//System.out.println("algo choisi: "+mapAlgo.get(chosenAlgo.getClass().getName()));
		graphsPath = GeneratedData.getInstance().getSelectedProject() + "/ReALEITY_Trace/Layering_Results/";
		dotFileName = contextMap.get(AlgorithmContext.WizardArguments.GraphFileName.toString()) + "_GraphViz";
		zestFileName = contextMap.get(AlgorithmContext.WizardArguments.GraphFileName.toString()) + "_Zest";
		layeringInTextFilePath = contextMap.get(AlgorithmContext.WizardArguments.GraphFileName.toString()) 
				                 + "_forTextEditor.txt";

		//nbDefaultLayers = 3;
		fitnessFunction = contextMap.get(AlgorithmContext.WizardArguments.FitnessFunction.toString());
		//{0, 2, 1, 4};//{adjacence, intra/inside, skip, back}
		int adjac= Integer.parseInt(contextMap.get(AlgorithmContext.WizardArguments.AdjacencyPenalty.toString()));
		int intra = Integer.parseInt(contextMap.get(AlgorithmContext.WizardArguments.IntraPenalty.toString()));
		int skip = Integer.parseInt(contextMap.get(AlgorithmContext.WizardArguments.SkipCallsPenalty.toString()));
		int back = Integer.parseInt(contextMap.get(AlgorithmContext.WizardArguments.BackPenalty.toString()));
		algorithmParameters = new Setup(adjac, intra, skip, back);
		nbDefaultLayers = Integer.parseInt(contextMap.get(AlgorithmContext.WizardArguments.nbSpecifiedLayers.toString()));
		
		computeOmnipresent = Boolean.parseBoolean(contextMap.get(AlgorithmContext.WizardArguments.DiscardOmnipresent.toString()));
		
		tabuMaxIterations = Integer.parseInt(contextMap.get(AlgorithmContext.WizardArguments.TabuMaxIter.toString()));
		tabuListSize = Integer.parseInt(contextMap.get(AlgorithmContext.WizardArguments.TabuListSize.toString()));;
		randomIterations = Integer.parseInt(contextMap.get(AlgorithmContext.WizardArguments.RandomIter.toString()));
		
		tabuSearchAlgorithm = new TabuSearchAlgorithmKDM(tabuMaxIterations, tabuListSize);
		//AGGREGATION_MAX_LEVEL = 4;
		
		//set the algorithm chosen by the user
				String chosenAlgorithm = contextMap.get(AlgorithmContext.WizardArguments.ChosenAlgorithm.toString());
				switch(chosenAlgorithm){
					case "SAHC layering algorithm": chosenAlgo =  layeringAlgo;
						break;
					case "Relayering": chosenAlgo = relayeringAlgorithm;
						break;
					case "Recursive relayering": chosenAlgo = aBestRelayeringAlgorithm;
						break;
					case "Tabu search algorithm" : chosenAlgo = tabuSearchAlgorithm ;
						break;
					default : System.out.println("an unknown algorithm has been specified in the execution context");
				}
		
		StructureModel resultModel = KDMStorage.getInstance().saveContext(contextMap, 
				                                                          mainSegment);
		return resultModel;
	}
	
	public static String getContextPath() {
		return contextPath;
	}
	
	public static IRecoveryAlgorithm getChosenAlgo() {
		return chosenAlgo;
	}
	
	public static void setChosenAlgo(IRecoveryAlgorithm chosenAlgo) {
		AlgorithmContext.chosenAlgo = chosenAlgo;
	}
	
}
