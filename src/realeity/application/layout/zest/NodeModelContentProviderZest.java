package realeity.application.layout.zest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.technique.util.KDMEntityFullName;


/**
 * Code inspired from the NodeModelContentProvider's one.
 * @author Alvine
 *
 */
public class NodeModelContentProviderZest {

	 private List<MyConnection> connectionList;
	 private List<MyNode> nodeList;
	 private static Map<AbstractGraphNode, MyNode> mapNode;
	 
	 /**
	  * Class constructor.
	  * @param graph
	  */
	 public NodeModelContentProviderZest(AbstractModuleDependencyGraph graph) {
		 nodeList = new ArrayList<MyNode>();
		 connectionList = new ArrayList<MyConnection>();
		 mapNode = new HashMap<AbstractGraphNode, MyNode>();

		 //create nodes
		 for(AbstractGraphNode graphNode : graph.getNodeList()){
			 MyNode zestNode = createANode(graphNode);
			 nodeList.add(zestNode);
			 mapNode.put(graphNode, zestNode);
		 }
		 
		 //create connections
		 for(AbstractGraphNode graphNode : mapNode.keySet()){
			 for(AbstractGraphNode graphNodeSuccessor :  graphNode.getSuccessorsList().keySet()){
				 String successorName = KDMEntityFullName.getInstance().determineSpaceName(graphNodeSuccessor.getEntity()); 
				 MyNode zestNodeSuccessor = null;
				 for(MyNode zestNode :  nodeList){
					 if(successorName.equals(zestNode.getName())){
						 zestNodeSuccessor = zestNode;
						 break;
					 }
				 }
				 MyNode zestFrom = mapNode.get(graphNode);
				 MyConnection connect = new MyConnection("" + zestFrom.getId(), "" + graphNode.getSuccessorsList().get(graphNode),
						                                 zestFrom,zestNodeSuccessor);
				/* System.out.println("fromId "+zestFrom.getId()+" succId" +zestNodeSuccessor.getId()
						            +" nodeFrom" + zestFrom.getName()+ " nodeSucc" + zestNodeSuccessor.getName());*/
                 connectionList.add(connect);
			 }
		 }
		 
		 //add connections
		 for (MyConnection connection : connectionList) {
		      connection.getSource().getConnectedTo()
		          .add(connection.getDestination());
		 }
}

	 /**
	  * 
	  * @return
	  */
	 private MyNode createANode(AbstractGraphNode graphNode){
		 String nodeName = KDMEntityFullName.getInstance().determineSpaceName(graphNode.getEntity()); 
		 String defaultNodeName = "node" + "_" + graphNode.getNodeId();
		 MyNode zestNode = new MyNode("" + graphNode.getNodeId(), nodeName);
		 return zestNode;
	 }
	 
	 
	public List<MyNode> getNodes() {
		    return nodeList;
	}
	 
	 public static Map<AbstractGraphNode, MyNode> getMapNode() {
		return mapNode;
	}
}
