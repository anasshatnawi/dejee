package realeity.application.layout;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gmt.modisco.omg.kdm.code.Package;

import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.core.layering.algorithms.settings.Setup;
import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.LayerP;
import realeity.core.mdg.elements.LayeredPartition;
import realeity.technique.storage.files.FileProcessment;
import realeity.technique.util.KDMEntityFullName;


/**
 * License GPL.
 * Abstract class allowing its daughters to redefine its methods to generate the dot 
 * code of a partition.
 * @author Alvine Boaye Belle
 * 2013  12:31:48
 */
public abstract class AbstractDot {
    /**
     * Separator.
     */
	protected static String separator = ", ";
	
	/**
	 * contains the colors used to color the clusters which are normal ie, not bag 
	 * cluster or omnipresent cluster
	 */
	protected  List<String> layerColors;
	
	/**
	 * 
	 */
	protected static int colorIndex;
	
	/**
	 * Possible colors of a layer.
	 */
	protected enum ColorNames {
		salmon2, lightsteelblue1, darkorchid1, darkturquoise, thistle2, gainsboro, 
		gray100, cornsilk2, gold1, chartreuse1, bisque3, violet, whitesmoke
	};

	/**
	 * shape of the nodes
	 */
	protected String nodeShape = "tab";//box for circle. for more info, see: http://www.graphviz.org/doc/info/shapes.html
	
	protected static String graphPosition = "TB";//"LR " for horizontal layout;//"TB" for a vertical layout
	
	
	protected String clusterColor = "";
	
	private static String resultData = "";
	
	/**
	 * Generate a partition by applying the selected layering algorithm on the data retrieved 
	 * from the system under analysis.
	 * @param layeringAlgorithm
	 */
	public AbstractDot() {
		
		colorIndex = 0;
		layerColors = new ArrayList<String>();
		layerColors.add(ColorNames.thistle2.toString());
		layerColors.add(ColorNames.chartreuse1.toString());
		layerColors.add(ColorNames.gold1.toString());
		layerColors.add(ColorNames.darkturquoise.toString());
		layerColors.add(ColorNames.thistle2.toString());
		clusterColor = ColorNames.gray100.toString();
	}
	
	/**
	 *Generate the dot code corresponding to the nodes comprised by the graph. 
	 *Depending on the partition at hand, a subgrapgh can be a cluster, a layer 
	 *or even not exist.
	 *@param partition
	 * @return
	 */
	protected abstract String generateNodes(LayeredPartition partition);
	
	/**
	 * 
	 * @param graph
	 * @return
	 */
	protected  String generateNodes(AbstractModuleDependencyGraph graph){
		return "\r\n";
	}
	
	/**
	 *Generate the dot code corresponding to the subgraphs comprised by the graph. 
	 *Depending on the partition at hand, a subgrapg can be a cluster, a layer or even not exist.
	 * @return
	 */
	protected abstract String generateSubgraphs(LayeredPartition layeredPartition);
	
	/**
	 * Generate the dot code corresponding to the connections (edges/dependencies) existing 
	 * between nodes. Call the method createAConnectionBetween2Nodes to create the dot codes 
	 * of all the nodes of a partition.
	 * NB: if you have a huge amount of edges, replace the term "label" by "xlabel" 
	 * only in the line allowing to create the connection!
	 * @param partition
	 * @return
	 */
	protected abstract String generateConnectionsBetweenNodes(LayeredPartition partition);
	
	/**
	 * 
	 * @param graph
	 * @return
	 */
	protected String generateConnectionsBetweenNodes(AbstractModuleDependencyGraph graph){
		 String dotCluster = "\r\n";
			
		  return dotCluster;
	}

	
	/**
	 * Allows to create the dot code corresponding to a node.
	 * @param graphNode
	 * @return
	 */
	protected String createANode(AbstractGraphNode graphNode){
		String nodeName = KDMEntityFullName.getInstance().determineSpaceName(graphNode.getEntity()); 
		String defaultNodeName = "node" + "_" + graphNode.getNodeId();
		String nodeDot = defaultNodeName+"[" + "\r\n";
		nodeDot += "label=\"" + "" + nodeName + "\"" + "\r\n";
		nodeDot += "shape =" + nodeShape + "\r\n";
		nodeDot += "style =filled" + "\r\n";
		nodeDot += "color = gray3"	+ "\r\n";
		nodeDot += "fillcolor = white" + "\r\n";
		nodeDot += "]" + "\r\n" + "\r\n";
		return nodeDot;
	}
	
	/**
	 * Generate the dot code corresponding to the header of the graph.
	 * @return
	 */
	 /**
	 * header required to create a dot file representing a graph.
	 * @return
	 */
	 public String generateGraphHeader(LayeredPartition layeredPartition, 
			                           AbstractModuleDependencyGraph graph){
		//String headGraph = "digraph zestGraph {" + "\r\n";
		String headGraph = " graph [" + "\r\n";
		headGraph += " rankdir = " + graphPosition + "\r\n";
		headGraph += computeGraphSize(layeredPartition, graph);
		String font = "" + computeGraphFont(layeredPartition, graph);
		headGraph += "]" + "\r\n";
		headGraph += "node [" + "\r\n";
		String style = "rounded,filled";
		headGraph += "fontsize=\"" + font + "\"" + "\r\n";

		headGraph += "shape = "+ nodeShape +"\r\n";
		headGraph += "]" + "\r\n" + "\r\n";
	 return headGraph;
	}
	
	
	/**
	 * 
	 * @param node
	 * @param successorNode
	 * @return
	 */
	protected String createAConnectionBetween2Nodes(AbstractGraphNode node, 
			                                        AbstractGraphNode successorNode ){
		//build the dot code corresponding to these connections
		String dotForConnections = ""; 
		String startNodeName = "node" + "_" + node.getNodeId();//node.getNodeFullName();
		String endNodeName = "node" + "_" + successorNode.getNodeId();//successor.getNodeFullName();
		int weight = node.getSuccessorsList().get(successorNode);
		dotForConnections += startNodeName + " -> " + endNodeName + "[ penwidth = 1, fontsize = 8," +
	            " label=\"" + weight + "\"" + " ]" + "\r\n";
		
		return dotForConnections;
	}//end method
	
	/**
	 * 
	 * @param cluster
	 * @param clusterIndex
	 * @param graph
	 * @return
	 */
	protected String generateDotForALayer(LayerP Layer, int clusterIndex,
			                              AbstractModuleDependencyGraph graph){
		String dotCluster = "";
		if((Layer != null) && (Layer.getLayerContent().size() > 0)){//est ce necessaire
			 String subgraph = "subgraph cluster" + (1 + clusterIndex) + "{" + "\r\n";
			 String clusterLabel = "label =\"" + "cluster" +  (1 + clusterIndex) + "\"" + "\r\n";
			 String style = "style =filled"+"\r\n";
			 String color = "";
			 color = "color = " + clusterColor + "\r\n";//use one of the color in clustersColors
			                                                                  //to color the cluster
			if(colorIndex == layerColors.size())
				 colorIndex = 0;
			
			//filling the subgraph with nodes (packages)
			 String dotNode = ""; 
			 for(AbstractGraphNode node : Layer.getLayerContent()){
					/*GraphNode nodeInMDG = getNodeInInferredMDG(inferredMDG, node);
					String defaultNodeName = "node"+"_"+ nodeInMDG.getNodeId(); 
					*/
				 String defaultNodeName = "node" + "_" + node.getNodeId(); 
				 dotNode += defaultNodeName + "\r\n";//pack.getPackageName()+"\r\n";//nodesNamesMap.get(pack.getPackage())+"\r\n";//
				}
			 dotCluster += subgraph + clusterLabel + style + color + dotNode;
			 dotCluster += "}" + "\r\n" + "\r\n";
		 }
		 return dotCluster;
	}
	
	/**
	 * Specify the size of the graph as displayed in Graphviz.
	 * @param layeredPartition
	 * @return
	 */
	private String computeGraphSize(LayeredPartition layeredPartition, 
			                        AbstractModuleDependencyGraph graph){
		int mdgSize = getSize(layeredPartition, graph);
		String dotSize = "";
		if(mdgSize < 30){
			dotSize = " size = \"25,25\"" + "\r\n";
		}
		else if (mdgSize < 100){
			dotSize = " size = \"55,55\"" + "\r\n";
		}
		else if (mdgSize < 200){
			dotSize = " size = \"100,100\"" + "\r\n";
		}
		else if (mdgSize < 300){
			dotSize = " size = \"200,200\"" + "\r\n";
		}
		else if (mdgSize < 500){
			dotSize = " size = \"300,300\"" + "\r\n";
		}
		else
			dotSize = " size = \"1000,1000\"" + "\r\n";
		
		return dotSize;
		
	}
	
	/**
	 * 
	 * @param layeredPartition
	 * @param graph
	 * @return
	 */
	private int getSize(LayeredPartition layeredPartition, 
                        AbstractModuleDependencyGraph graph){
		int mdgSize = 0;
		if(layeredPartition != null)
			mdgSize = layeredPartition.getModuleDependencyGraph().getNodeList().size();
		else if(graph != null)
			mdgSize = graph.getNodeList().size();
		return mdgSize;
	}
	
	/**
	 * 
	 * @param layeredPartition
	 * @return
	 */
	private int computeGraphFont(LayeredPartition layeredPartition, 
			                     AbstractModuleDependencyGraph graph){
		int mdgSize = getSize(layeredPartition, graph);
		int fontSize = 10;
		if(mdgSize < 30){
			fontSize = 15;
		}
		else if (mdgSize < 100){
			fontSize = 25;
		}
		else if (mdgSize < 200){
			fontSize = 35;
		}
		else if (mdgSize < 300){
			fontSize = 45;
		}
		else if (mdgSize < 500){
			fontSize = 65;
		}
		else
			fontSize = 95;
		
		return fontSize;
		
	}

	
	/**
	 * Generates the dot/zest code corresponding to a partition and saves it in a file.
	 * @param partition
	 */
	public final void generateDotCode(LayeredPartition partition, String graphFileName, 
			                          String parentDirectory){
		String dotCode = generateGraphHeader(partition, null) + generateNodes(partition)
				         + generateSubgraphs(partition)
				         + generateConnectionsBetweenNodes(partition) + "}";
		Setup algoParameters = AlgorithmContext.algorithmParameters;
		
		resultData = "_LC= " + partition.getPartitionCost()+ "("  
				          + "adjacencyCost =" + algoParameters.getAdjacencyPenalty()  
				          + "_intraCost =" + algoParameters.getIntraPenalty()  + "_skipCost =" 
				          + algoParameters.getSkipPenalty() 
				          + "_backCost =" + algoParameters.getBackPenalty() +").txt";
		graphFileName += resultData;
		FileProcessment.getInstance().storeInFile(dotCode, graphFileName, 
				                                  parentDirectory);
	
	}
	
	/**
	 * Generates the dot/zest code corresponding to the content of an MDG and saves it in a file.
	 * @param partition
	 */
	public void generateDotCode(AbstractModuleDependencyGraph graph, 
			                    String graphFileName,
			                    String parentDirectory){
		String dotCode = generateGraphHeader(null, graph) + generateNodes(graph) + 
				         generateConnectionsBetweenNodes(graph) + "}";
	
		FileProcessment.getInstance().storeInFile(dotCode, graphFileName, 
				                                  parentDirectory);
	
	}

	public static String getResultData() {
		return resultData;
	}
}
