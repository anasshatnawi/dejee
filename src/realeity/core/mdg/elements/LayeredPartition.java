package realeity.core.mdg.elements;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.gmt.modisco.omg.kdm.code.Package;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;

import realeity.core.LayersMetrics;
import realeity.core.algorithms.cost.ICostStrategy;
import realeity.core.layering.algorithms.IRecoveryAlgorithm;
import realeity.core.layering.algorithms.settings.Setup;
import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.Graph;
import realeity.core.mdg.InferredGraph;
import realeity.technique.extractor.AbstractKDMRelationshipExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.fact.ElementaryFact;
import realeity.technique.util.KDMEntityFullName;
import realeity.technique.util.RealeityUtils;




/**
 * License GPL
 * This class describes a partition made of layers.
 * @author Alvine Boaye Belle
 * February 2013
 */
public class LayeredPartition extends AbstractGraphNode implements Cloneable {
	
	//TODO: remplacer l'usage de layerContent par layerList !!!
	/**
	 * Data structure containing the dependencies between the system's packages.
	 */
	protected AbstractModuleDependencyGraph moduleDependencyGraph;
	
	/**
	 * Set of layers constituting the layered architecture of the system under analysis.
	 */
	private List<LayerP> layerList;
	
	/**
	 * Data structure containing the dependencies between packages
	 */
	protected AbstractModuleDependencyGraph inferredMDG;

	/**
	 * Number of layers constituting the system.
	 */
	protected int nbDefaultLayers;
	
	/**
	 * 
	 */
	private LayerP layerToBeSplitted;
	
	/**
	 * 
	 */
	private int nbLayersForSplit;
	
	
	private LayersMetrics partitionMetrics;
	
	
	//interfacing degree of the layer
	private int IDL = -1; //default value
		
	/***
	 * Class's constructor.
	 * @param nbDefaultLayers
	 * @param MAX_LEVEL
	 */
	public LayeredPartition(int nbDefaultLayers) {
		super();
		layerList = new ArrayList<LayerP>();
		this.nbDefaultLayers = nbDefaultLayers;
		layerToBeSplitted = new LayerP();
		partitionMetrics = new LayersMetrics(0, 0, 0, 0);
	}
	
	@Override
	public double computeCost(ICostStrategy costStrategy,
                              Setup algorithmParameters){
		return costStrategy.computeLC(this,
                                      algorithmParameters); 
    }

	
	/**
	 * Randomly affect  packages to layers in order to build an initial partition that will be the parameter 
	 * of a layering algorithm.
	 * Each layer has at leas
	 * t one one.
	 * @param systemOfLayers
	 */
	public void randomInitialLayering(){
		AbstractModuleDependencyGraph graph = chooseRightGraph();

		
		Random random = new Random();
		List<LayerP> systemLayers = new ArrayList<LayerP>();
		//initialize the layers
		for(int index = 0; index < nbDefaultLayers; index++){
			systemLayers.add(new LayerP());
		}
		
		//mettre aleatoirement un package dans chaque couche: ensures that each initial layer has at least one package
		List<AbstractGraphNode> assignedPackages = new ArrayList<AbstractGraphNode>();
		for(int layerNumber = 0;  layerNumber < systemLayers.size() ; layerNumber++){
			int nodeNumber = -1;
			AbstractGraphNode pickedPackage = null;
			do{
			    nodeNumber = random.nextInt(graph.getNodeList().size());
			    pickedPackage = graph.getNodeList().get(nodeNumber);

			}while((assignedPackages.size() < graph.getNodeList().size()) && (assignedPackages.contains(pickedPackage)));
			
			LayerP layer = systemLayers.get(layerNumber);
			systemLayers.remove(layer);
			layer.getLayerContent().add(pickedPackage);
			systemLayers.add(layerNumber, layer);
			assignedPackages.add(pickedPackage);
			
		}
		
		//mettre les packages restants dans une couche aleatoire
		for(AbstractGraphNode pack : graph.getNodeList()){
			if(!assignedPackages.contains(pack)){
				//pick a random number between 0 and nb layer , and then put a package in the corresponding layer
				 int layerNumber = random.nextInt(nbDefaultLayers);
				 LayerP pickedLayer = systemLayers.get(layerNumber);
				 pickedLayer.getLayerContent().add(pack);
				 systemLayers.remove(layerNumber);
				 systemLayers.add(layerNumber, pickedLayer);
				 assignedPackages.add(pack);
			}
		}
		
		layerList = systemLayers;
		
		 //à supprimer: juste pour affichage
		 for(int index = 0; index < systemLayers.size(); index++){
			 System.out.print("Contenu de la couche "+index + " de taille: " 
		                      + systemLayers.get(index).getLayerContent().size() + " :  ");
			 for(AbstractGraphNode pack : systemLayers.get(index).getLayerContent()){
				 System.out.print(pack.determineNameSpace() + ",  ");
			 }
			 System.out.println("");
		 }
		
	}
	
	

	/**
	 * Create the module dependency graph (MDG) that will contain all the modules (nodes) 
	 * corresponding to the entities of the system.
	 * @return
	 */
	public  void createModuleDependencyGraph(List<AbstractGraphNode> clusterList, 
			                                 List<AbstractExtractedFact> dependenciesToConsider, 
			                                 AbstractModuleDependencyGraph simpleMDG){
		inferredMDG = new InferredGraph();
		inferredMDG.buildInferredDependencyGraph(clusterList, simpleMDG);
	}
	
	/**
	 * Creates the layers of the system. 
	 * The uppermost layer is constituted by the packages which don't have incident edges. 
	 * The lowermost layer is constituted by the packages which don't have outgoing edges. 
	 * And the rest of the packages are randomly affected to the intermediate layers.
	 * @param packages
	 * @param nbDefaultLayers
	 * @return
	 */
	public void createInitialLayers(){
		//TODO : s'assurer qu'aucune couche intermediaire n'est vide
		
		//si nb layers ==2, ne construire que lowermost et uppermost!!!
		//determine for each node the amount of outgoing edges and incident edges
		AbstractModuleDependencyGraph graph = chooseRightGraph();
		if(graph != null){
			int nodeNumber = graph.getNodeList().size();
			//System.out.println(graph.toString());//à supprimer
			
			//determine for each module the amount of outgoing edges 
			 int[] outgoingEdges = computeOutgoingEdges(nodeNumber, graph);
			 
			//determine for each node the amount of incident edges 
			 int[] incidentEdges = computeIngoingEdges(nodeNumber, graph);
			 ////////////////////
			
			 List<AbstractGraphNode> remainingModules = new ArrayList<AbstractGraphNode>();
			 
			//put in the lowermost layer all the packages having no outgoing edges and 
			//put in the uppermost layer all the packages having no incident edges
			LayerP lowermostLayer = new LayerP();
			LayerP uppermostLayer = new LayerP();
			 for(int index = 0; index < nodeNumber; index++){
				 boolean moduleAdded = false;
				 AbstractGraphNode moduleToAdd = graph.getNodeList().get(index);
				 if(incidentEdges[index] == 0){
					 uppermostLayer.getLayerContent().add(moduleToAdd);
					 moduleAdded = true;
					 //System.out.println("LayeredPartition:  j'ajoute le package "+index+" à uppermostLayer");
				 }
				 if(outgoingEdges[index] == 0  && !uppermostLayer.getLayerContent().contains(moduleToAdd)){
					 lowermostLayer.getLayerContent().add(moduleToAdd);
					 //System.out.println(" LayeredPartition: j'ajoute le package "+index+" à lowermost");
					 moduleAdded = true;
				 }	 
				 	 
				 if(!moduleAdded)
					 remainingModules.add(moduleToAdd);
			 }
			 
			 //build the initial layers of the system
			 List<LayerP> intermediateLayers = buildIntermediateLayers(remainingModules, 
					                                                   lowermostLayer);
			 layerList = new ArrayList<LayerP>();
			 layerList.add(uppermostLayer);
			 for(LayerP layer : intermediateLayers){
				 layerList.add(layer);
			 }
			 layerList.add(lowermostLayer);
			 
			 //à supprimer: juste pour affichage
			/* for(int index = 0; index < layerList.size(); index++){
				 System.out.print("Random content of the layer " + index + " :  ");
				 System.out.println(layerList.get(index).toString());
			 }*/
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private AbstractModuleDependencyGraph chooseRightGraph(){
		AbstractModuleDependencyGraph graph = null;
		if(this.moduleDependencyGraph == null)
			graph = inferredMDG;
		if(this.inferredMDG == null)
			graph = this.moduleDependencyGraph;
		return graph;
	}
	
	/**
	 * Checks whether two layered partitions contains the same elements.
	 * @param tabuSolution
	 * @param neighborSolution
	 * @return
	 */
	public boolean isIdenticalTo(LayeredPartition neighborSolution){
		//TODO: creer une methode determineSpaceName dans AbstractGraphNode et c'est cette
		//methode qui va utiliser KDMEntityFullName pour calculer le nom complet du noeud!
		if(this.getLayerList().size() != neighborSolution.getLayerList().size()){
			return false;
		}
		else{
			int layerIndex = 0;
			while(layerIndex < this.getLayerList().size()){
				if(this.getLayerList().get(layerIndex).getLayerContent().size() 
					!= neighborSolution.getLayerList().get(layerIndex).getLayerContent().size()){
					return false;
				}
				layerIndex++;
			}
			layerIndex = 0;
			while(layerIndex < this.getLayerList().size()){
				LayerP tabuSolutionLayer = this.getLayerList().get(layerIndex);
				LayerP neighborSolutionLayer = neighborSolution.getLayerList().get(layerIndex);
				int nodeIndex = 0;
				while(nodeIndex < tabuSolutionLayer.getLayerContent().size()){
					AbstractGraphNode tabuSolutionNode = tabuSolutionLayer.getLayerContent().get(nodeIndex);
					String tabuNodeName = tabuSolutionNode.determineNameSpace();
					boolean found = false;
					for(AbstractGraphNode neighborSolutionNode : neighborSolutionLayer.getLayerContent()){
						if(tabuNodeName.equals(neighborSolutionNode.determineNameSpace())){
							found = true;
						}
					}
					if(!found)
						return false;
					nodeIndex++;
				}//while (nodeIndex <
				layerIndex++;
			}
		}//else
		
		return true;
	}
	
	/**
	 * 
	 * @param nodeNumber
	 * @param graph
	 * @return
	 */
	private int[] computeOutgoingEdges(int nodeNumber,
			                           AbstractModuleDependencyGraph graph){
		 int[] outgoingEdges = new int[nodeNumber]; //will contain all the outgoing edges of each module
		 int tabIndex = 0;
		 for(AbstractGraphNode node : graph.getNodeList()){
			 outgoingEdges [tabIndex] = 0;
			 for(AbstractGraphNode successor: node.getSuccessorsList().keySet()){
				 outgoingEdges[tabIndex] =  outgoingEdges[tabIndex] + 1;
			 }
			 tabIndex++;
		 }
		 return outgoingEdges;
	}
	
	 /** 
	 * @param nodeNumber
	 * @param graph
	 * @return
	 */
	private int[] computeIngoingEdges(int nodeNumber,
			                          AbstractModuleDependencyGraph graph){
		 int[] incidentEdges = new int[nodeNumber];//will contain all the incident edges of each module
		 int tabIndex = 0;
		 for(AbstractGraphNode node : graph.getNodeList()){
			 incidentEdges [tabIndex] = 0;
			 for(AbstractGraphNode predecessor: node.getPredecessorsList().keySet()){
				 incidentEdges[tabIndex] =  incidentEdges[tabIndex] + 1;
			 }
			 tabIndex++;
		 }
		 return incidentEdges;
	}
	
	/**
	 * Put the remaining modules in the layers located between the lowermost and the uppermost layers.
	 * @param remainingNodes
	 * @param lowermostLayer
	 */
	private List<LayerP> buildIntermediateLayers(List<AbstractGraphNode> remainingNodes, 
			                                     LayerP lowermostLayer){
		//put randomly in the other layers, all the packages having outgoing and incident layers
		 List<LayerP> intermediateLayers = new ArrayList<LayerP>();
		 if(nbDefaultLayers > 2){
			 for(int index = 1; index < (nbDefaultLayers - 1); index++){
				 intermediateLayers.add(new LayerP());
			 }
			 Random random = new Random();
			 
			//Fill each intermediate layer with a random node
			 for(LayerP intermLayer : intermediateLayers){
				 if(remainingNodes.size() > 0){
					 int randomNode = random.nextInt(remainingNodes.size());
					 intermLayer.getLayerContent().add(remainingNodes.get(randomNode));
					 remainingNodes.remove(randomNode);
				 }
			 }
			 
			//pick a random number between 0 and nb layer -2, and 
			 //then put a node in the corresponding layer
			 while( remainingNodes.size() > 0){
				 int layerNumber = random.nextInt(nbDefaultLayers - 2);
				 AbstractGraphNode moduleToAdd = remainingNodes.get(0);
				 intermediateLayers.get(layerNumber).getLayerContent().add(moduleToAdd);
				 remainingNodes.remove(0);
			 }
		 }
		 else{
			 for(AbstractGraphNode node : remainingNodes){
				 lowermostLayer.getLayerContent().add(node);
			 }
		 }
		 
		 return intermediateLayers;
	}
	
	/**
	 * Checks whether a system has more layers than the initial system that need to be layered. 
	 * This could help preventing the generation of a neighbor which has the same layers 
	 * than the initial system taken as input by the algorithm
	 * that is splits and relayers it.
	 * @return
	 */
	public boolean hasTheDesiredNumberOfLayers(){
		int nbNotEmptyLayers = 0;
		for(LayerP layer: this.getLayerList()){
			if(layer.getLayerContent().size() > 0){
				nbNotEmptyLayers++;
			}
		}
		return nbNotEmptyLayers == this.getNbUserSpecifiedLayers();
		
	}

	
	/**
	 * Randomly affect  packages to layers in order to build an initial 
	 * partition that will be the parameter of a layering algorithm.
	 * @param LayeredPartitionWithoutKDM
	 */
	public void initialLayeringForHC(){
		Random random = new Random();
		List<LayerP> layerList = new ArrayList<LayerP>();
		//initialize the layers
		for(int index = 0; index < nbDefaultLayers; index++){
			layerList.add(new LayerP());
		}
		
		for(AbstractGraphNode pack : inferredMDG.getNodeList()){
			//pick a random number between 0 and nb layer , and then put a package in the corresponding layer
			 int layerNumber = random.nextInt(nbDefaultLayers);
			 LayerP pickedLayer = layerList.get(layerNumber);
			 pickedLayer.getLayerContent().add(pack);
			 layerList.remove(layerNumber);
			 layerList.add(layerNumber, pickedLayer);
			
		}
		
		setlayerList(layerList);
	}
	
	
	/**
	 * 
	 * @param layeringAlgorithm
	 * @return
	 */
	public LayeredPartition callLayeringAlgo(IRecoveryAlgorithm layeringAlgorithm, 
			                                 Setup algorithmParameters){
		LayeredPartition result = layeringAlgorithm.recoverLayers(this, 
				                                                  algorithmParameters);
		return result;
	}
	
	/**
	 * Generate the successors of each layer.
	 * A layer to is a successor of a layer from if some nodes 
	 * of layer to are successors of some nodes of layer from.
	 */
	public void generateLayersSuccessors(){
		for(LayerP layerFrom : layerList){
			for(LayerP layerTo: layerList){
				if(layerFrom != layerTo){
					for(AbstractGraphNode nodeFrom: layerFrom.getLayerContent()){
						int dependencyWeight = 0; //packageList.indexOf(packageFrom);
						for(AbstractGraphNode nodeTo: layerTo.getLayerContent()){
							dependencyWeight +=  nodeFrom.getSuccessorsList().get(nodeTo);
						}
						if(dependencyWeight > 0){ //layerTo is a successor of layerFrom
							Map<AbstractGraphNode, Integer> successors = layerFrom.getSuccessorsList();
							successors.put(layerTo, dependencyWeight);
							layerFrom.setSuccessorsList(successors);
						}
					}
		         }
	        }
		}
	}
	
	/**
	 * build a second layered system from a layered system (this) obtained by applying a layering algorithm.
	 * For this purpose, it splits a layer (LayerToSplit) into nbLayers and replace in the system the layer 
	 * to split by the resulting nbLayers layers. The resulting layered system will we taken as an input to 
	 * apply again a layering algorithm.
	 * subsystemResultingFromSplit is obtained using the method createLayeredSubsystem()
	 * @param LayerToSplit
	 * @param nbLayersForSplit
	 */
	public LayeredPartition buildRelayeringPartition(LayerP layerToSplit, 
			                                         LayeredPartition subsystemResultingFromSplit){
		LayeredPartition LayeredPartitionClone = (LayeredPartition) this.clone();
		
		//replace in the system (this) layerToSplit by the layers of subsystemResultingFromSplit
		List<LayerP> newLayerList = new ArrayList<LayerP>();
	   	int layerIndex = 0;
	   	int indexOfLayerToSplit = this.layerList.indexOf(layerToSplit);
	   	while(layerIndex < indexOfLayerToSplit){
	   		newLayerList.add(LayeredPartitionClone.getLayerList().get(layerIndex));
	   		layerIndex++;
	   	}
	   	for(LayerP layer : subsystemResultingFromSplit.getLayerList()){
	   		newLayerList.add(layer);
	   	}
	   	layerIndex++;//in order not to add layerToSplit
	   	while(layerIndex < LayeredPartitionClone.getLayerList().size()){
	   		LayerP layer = LayeredPartitionClone.getLayerList().get(layerIndex);
	   		newLayerList.add(layer);
	   		layerIndex++;
	   	}
	   	LayeredPartitionClone.setlayerList(newLayerList);
	   	return LayeredPartitionClone;
	}
	
	/**
	 * Create a layered system made of the layers resulting from the split of 
	 * layerToSplit into nbLayersForSplit.
	 * @param layerToSplit
	 * @param nbLayersForSplit
	 * @return
	 */
	public LayeredPartition createLayeredSubsystem(LayerP layerToSplit){
		List<AbstractGraphNode> entitiesListForRelayering = new ArrayList<AbstractGraphNode>();
		List<AbstractExtractedFact> dependenciesToConsider = new ArrayList<AbstractExtractedFact>();
		for(AbstractGraphNode fromNode :  layerToSplit.getLayerContent()){
			for(AbstractGraphNode successorNode :  fromNode.getSuccessorsList().keySet()){
				//check whether the successorNode belongs to layerToSplit
				boolean belongTo = false;
				for(AbstractGraphNode node : layerToSplit.getLayerContent()){
					if(node.getNodeId() == successorNode.getNodeId()){
					//if(node.getNodeFullName().equals(successorNode.getNodeFullName())){
						belongTo = true;
						break;
					}	
				}
				if(belongTo){
					int dependencyWeight = fromNode.getSuccessorsList().get(successorNode);
					AbstractExtractedFact dependency = new ElementaryFact(fromNode.getEntity(), 
							                                              successorNode.getEntity(), 
							                                              dependencyWeight,
							                                              RealeityUtils.RelationsTypes.Aggregated.toString(), 
							                                              "");
					//System.out.println("Dependency used to create the subsystem : " + dependency.toString());
					dependenciesToConsider.add(dependency);
					
				}
			}
		}
		
		LayeredPartition  subsystemResultingFromSplit;
		if(dependenciesToConsider.size() == 0){
			subsystemResultingFromSplit = null;
		}
		else{
			for(AbstractGraphNode node:  layerToSplit.getLayerContent()){
				//entitiesListForRelayering.add(node);
				entitiesListForRelayering.add(node);
			}
			subsystemResultingFromSplit = new LayeredPartition(nbLayersForSplit);
			AbstractModuleDependencyGraph subgraph = new Graph();
			subgraph.buildGraph(entitiesListForRelayering, dependenciesToConsider);
			//subgraph.buildInferredDependencyGraph(dependenciesToConsider, entitiesListForRelayering);
			subsystemResultingFromSplit.setInferredModuleDependencyGraph(subgraph);
			subsystemResultingFromSplit.randomInitialLayering();//LayeredPartition.createInitialLayers();//
			restoreOriginalSuccessors(subsystemResultingFromSplit);
			//System.out.println("Sous-système créé : " + subsystemResultingFromSplit.toString());
		}
		
		
		return subsystemResultingFromSplit;
	}
	
	/**
	 * Restore the original successors of the subsystem's nodes,
	 * since these successors have been changed in the method 
	 * createLayeredSubsystem in order to create the initial subsystem's layers.
	 * @param subsystemResultingFromSplit
	 */
	public void restoreOriginalSuccessors(LayeredPartition subsystemResultingFromSplit){
		for(LayerP subsystemLayer : subsystemResultingFromSplit.getLayerList()){
			for(AbstractGraphNode subsystemNode : subsystemLayer.getLayerContent()){
				//for(AbstractGraphNode node : this.inferredMDG.getNodeList()){
				for(AbstractGraphNode node : this.moduleDependencyGraph.getNodeList()){
					//if(subsystemNode.getNodeFullName().equals(node.getNodeFullName())){
					//if(subsystemNode.getNodeId() == node.getNodeId()){
					if(subsystemNode.getEntity() == node.getEntity()){
						subsystemNode.setSuccessorsList(node.getSuccessorsList());
						break;
					}
				}
			}
		}
	}
	/**
	 * Take a list of layers as input and remove all the empty layers from that list
	 * @param layerList
	 * @return
	 */
	public void createFinalLayers(){
		List<LayerP> processedLayerList = layerList;
		List<LayerP> finalLayerList = new ArrayList<LayerP>();
		for(LayerP layer : layerList){
			if(layer.getLayerContent().size() > 0)
				finalLayerList.add(layer);
		}
		setlayerList(finalLayerList);
	}
		
	
	/**
	 * clone the reference this.
	 */
	public Object clone(){
		
		LayeredPartition layeredPartition = null;
		layeredPartition = (LayeredPartition) super.clone();
		layeredPartition.moduleDependencyGraph = moduleDependencyGraph;
		layeredPartition.inferredMDG = (AbstractModuleDependencyGraph) inferredMDG;
		layeredPartition.layerToBeSplitted = (LayerP) layerToBeSplitted.clone();
		layeredPartition.layerList  = new ArrayList<LayerP>();
		for(LayerP layer : layerList){
			layeredPartition.layerList.add((LayerP) layer.clone());
		}
		return layeredPartition;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String txt ="\n";
        int index = 0;
		for(LayerP layer : layerList){//à supprimer
	     	   txt += "The aggregated partition layer content is : layer " 
		              +(layerList.size() - (index++)) + " " +  layer.toString() + "\n";
	      }
		return txt;
	}
	
	/**
	 * 
	 */
	public void printResultWithoutSuccessors(){
		int index = 0;
		 for(LayerP layer : this.getLayerList()){//à supprimer: juste pour affichage
	     	   System.out.println("The content of the layer " 
		                          + (this.getLayerList().size() - (index++))+ " is : {");
	     		for(AbstractGraphNode node : layer.getLayerContent()){
	     			KDMEntity container = AbstractKDMRelationshipExtractor.findPackageTo(node.getEntity());
	     			String fullName = "";
	     			if(node.getEntity() instanceof Package)
	     				fullName = KDMEntityFullName.getInstance().determineSpaceName(container);
	     			else
	     			fullName = KDMEntityFullName.getInstance().determineSpaceName(container) + "_"
	     					   + node.getEntity().getName();
	     			System.out.print(fullName + ", ");
	     		}
	     		System.out.println("}");
	     		System.out.println("Number of elements in the layer : " 
	     		                   + layer.getLayerContent().size() + "\r\n");
	        }
	}
	
	public AbstractModuleDependencyGraph getModuleDependencyGraph() {
		return moduleDependencyGraph;
	}
	
	public List<LayerP> getLayerList() {
		return layerList;
	}

	public int getNbUserSpecifiedLayers() {
		return nbDefaultLayers;
	}
	
	public LayerP getLayerToSplit() {
		return layerToBeSplitted;
	}
	
	
	public int getNbLayersForSplit() {
		// TODO Auto-generated method stub
		return getNbLayersForSplit();
	}
	
	public LayersMetrics getPartitionMetrics() {
		return partitionMetrics;
	}
	
	public int getIDL() {
		return IDL;
	}
	
	
	public void setlayerList(List<LayerP> layerList) {
		this.layerList = layerList;
	}
	
	
	public void setLayerToBeSplitted(LayerP layerToBeSplitted) {
		this.layerToBeSplitted = layerToBeSplitted;
	}
	
	public void setNbLayersForSplit(int nbLayersForSplit) {
		this.nbLayersForSplit = nbLayersForSplit;
	}
	
	public void setPartitionMetrics(LayersMetrics partitionMetrics) {
		this.partitionMetrics = partitionMetrics;
	}
	
	public void setIDL(int iDL) {
		IDL = iDL;
	}
	

	public void computeMostSkipCallsDoers() {
		// TODO Auto-generated method stub
		
	}

	public AbstractModuleDependencyGraph getInferredMDG() {
		return inferredMDG;
	}

	public void setInferredModuleDependencyGraph(
			AbstractModuleDependencyGraph moduleDependencyGraph) {
		this.inferredMDG = moduleDependencyGraph;
		
	}


	@Override
	public double computeCF(AbstractGraphNode omnipresentCluster,
			                AbstractGraphNode bagCluster, 
			                AbstractModuleDependencyGraph graph) {
		// TODO Auto-generated method stub
		return 0;
	}
	
		
	public void setModuleDependencyGraph(AbstractModuleDependencyGraph mdg) {
		this.moduleDependencyGraph = mdg;
	}
	
	}

