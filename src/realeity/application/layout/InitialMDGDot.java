package realeity.application.layout;

import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.LayeredPartition;


/**
 * Allow generating the dot code corresponding to the module dependency of the analyzed system. 
 * That code is meant to be visualized by GraphViz.
 * @author Alvine
 *
 */
public class InitialMDGDot extends AbstractDot {
	
	@Override
	protected String generateConnectionsBetweenNodes(AbstractModuleDependencyGraph graph) {
		String dotForConnections = "";
		for(AbstractGraphNode node : graph.getNodeList()){
			for(AbstractGraphNode successorNode : node.getSuccessorsList().keySet()){
				dotForConnections += super.createAConnectionBetween2Nodes(node, successorNode);
			}
		}
			
		return dotForConnections + "\r\n";
	}
	
	@Override
	public String generateGraphHeader(LayeredPartition layeredPartition, 
                                      AbstractModuleDependencyGraph graph){
		String headGraph = "digraph MDG_GraphViz {" + "\r\n";
		headGraph += super.generateGraphHeader(layeredPartition, graph);
		return headGraph;
	}
	
	@Override
	protected String generateNodes(AbstractModuleDependencyGraph graph) {
		String dotNode = "";
		for(AbstractGraphNode node : graph.getNodeList()){
			dotNode += createANode(node);
		}
		return dotNode;
	}
	
	@Override
	protected String generateSubgraphs(LayeredPartition partition) {
		// TODO Auto-generated method stub
		return "\r\n";
	}

	@Override
	protected String generateNodes(LayeredPartition partition) {
		// TODO Auto-generated method stub
		return "\r\n";
	}

	@Override
	protected String generateConnectionsBetweenNodes(LayeredPartition partition) {
		// TODO Auto-generated method stub
		return "\r\n";
	}

}
