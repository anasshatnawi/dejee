package realeity.technique.storage.kdm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmt.modisco.omg.kdm.core.AggregatedRelationship;
import org.eclipse.gmt.modisco.omg.kdm.core.CoreFactory;
import org.eclipse.gmt.modisco.omg.kdm.core.ModelElement;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Attribute;
import org.eclipse.gmt.modisco.omg.kdm.kdm.KdmFactory;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.Component;
import org.eclipse.gmt.modisco.omg.kdm.structure.Layer;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureFactory;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureRelationship;

import realeity.application.data.GeneratedData;
import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.LayerP;
import realeity.core.mdg.elements.LayeredPartition;
import realeity.technique.extractor.ExtractionFacade;
import realeity.technique.extractor.SegmentExtractor;
import realeity.technique.util.RealeityUtils.MetricTypes;
import realeity.technique.util.RealeityUtils.RelationsTypes;


/**
 * Store a KDM element (entity or model) in a KDM representation. 
 * @author Alvine Boaye Belle
 *
 */
public class KDMStorage {
	private final static String COMPONENT_NAME = "Component_";
	private final static String LAYER_NAME = "Layer_";
	private String structureModelNameMDG = "Structure model (facts)";
	public  static final String RELATION_NAME_TO_STRING = "Relation name";
	public  static final String RELATION_WEIGHT_TO_STRING = "Relation weight";

	private static KDMStorage uniqueInstance;
	
	/**
	* Class's constructor. 
	* Its visibility is private in order not to allow the other classes to create 
	* many instances of KDMStorage.
	*/
	private KDMStorage(){

	}

	/**
	* Unique instance of KDMStorage.
	*/

	/**
	* Method insuring that a unique instance of KDMStorage is created.
	* @return uniqueInstance
	*/
	public static KDMStorage getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new KDMStorage();
		}
		return uniqueInstance;
	}
	
	/**
	 * Build a structure model and add the extracted facts in that structure model.
	 * The structure model is then added to the other models of the segment of the project
	 * @param factList
	 */
	public void serializeFacts(Segment mainSegment, AbstractModuleDependencyGraph simpleGraph, 
			                   String projectPath, String modelName){
		populateStructureModel(mainSegment, simpleGraph, projectPath, modelName);
	}
	

	/**
	 * Create a structure model and populate it with the nodes of the module dependency graph.
	 * The dependencies between the nodes are stored as aggregated relationships.
	 * @param seg
	 * @param simpleGraph contains nodes which respectively encapsulates a KDM package.
	 */
	private  void populateStructureModel(Segment mainSegment, AbstractModuleDependencyGraph simpleGraph,
			                             String projectPath, String modelName) {
		StructureModel structureModel = createEmptyStructureModel(mainSegment);
		
		Map<AbstractGraphNode, Component> componentMap = new HashMap<AbstractGraphNode, Component>();
		for (AbstractGraphNode node : simpleGraph.getNodeList()) {
			Component structComp = StructureFactory.eINSTANCE.createComponent();
			structureModel.getStructureElement().add(structComp);
			structComp.getImplementation().add(node.getEntity());
			structComp.setName(COMPONENT_NAME + (1 + simpleGraph.getNodeList().indexOf(node)));
			componentMap.put(node, structComp);
		}
		
		//build the relationships  between the nodes of the graph
		 for (AbstractGraphNode node : componentMap.keySet()) {
			 for(AbstractGraphNode successorNode: node.getSuccessorsList().keySet()) {
				 int dependencyWeight = node.getSuccessorsList().get(successorNode);
				 createAStructureRelationship(SegmentExtractor.getInstance().getResourceModel(), 
						                      componentMap.get(node),
						                      componentMap.get(successorNode), 
						                      RelationsTypes.Unspecified.toString(),
						                      "" + dependencyWeight);
			 }
		 }
		 
		//add the structure model to the models of the segment
		mainSegment.getModel().add(structureModel);
		
		// add the segment to the resource
		SegmentExtractor.getInstance().getResourceModel().getContents().add(mainSegment);
		
		// give a name to the structure model
		structureModel.setName(modelName); //structureModelName
		
		//save the resource. This operation allows to serialize the content of the kdm segment into an existing XMI file.
		try {
			SegmentExtractor.getInstance().getResourceModel().save(null);
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("--- An error occurred when saving the structure model");
		}
	}
	
	/**
	 * Save the clone of the layering result
	 * @param refinementModel
	 * @param refinementModelName
	 * @param mainSegment
	 */
	public void saveRefinementStructureModel(StructureModel refinementModel, String refinementModelName,
			                                 Segment mainSegment, LayeredPartition partition){
		refinementModel = KDMStorage.getInstance().createEmptyStructureModel(mainSegment);
		refinementModel.setName(refinementModelName);
		populateResultStructureModel(mainSegment, 
				                     partition, 
				                     refinementModel);
	}
	
	/**
	 * Populate an existing structure model with the layers obtained during the layering.
	 * The dependencies between the nodes are stored as aggregated relationships.
	 * @param seg
	 * @param simpleGraph contains nodes which respectively encapsulates a KDM package.
	 */
	public  void populateResultStructureModel(Segment mainSegment, LayeredPartition layeringResult,
			                                  StructureModel resultModel) {
		
		//Map<AbstractGraphNode, Layer> layerMap = new HashMap<AbstractGraphNode, Layer>();
		int layerIndex = layeringResult.getLayerList().size();
		int componentIndex = 1;
		for(LayerP layerPartition : layeringResult.getLayerList()){
			Layer structLayer = StructureFactory.eINSTANCE.createLayer();
			if(layerPartition.getUserDefinedName().equals(""))
				structLayer.setName(LAYER_NAME + (layerIndex));
			else
				structLayer.setName(layerPartition.getUserDefinedName());
			layerIndex--;

			resultModel.getStructureElement().add(structLayer);
			for(AbstractGraphNode node : layerPartition.getLayerContent()){
				Component structComp = StructureFactory.eINSTANCE.createComponent();
				structLayer.getStructureElement().add(structComp);
				structComp.setName(COMPONENT_NAME + (componentIndex++));
				structComp.getImplementation().add(node.getEntity());
			}
		}
		//store the metrics value in the structure
		Map< String, String> metricsMap = new HashMap<String, String>();
		metricsMap.put(MetricTypes.AdjacencyCallsMetric.toString(), 
				       layeringResult.getPartitionMetrics().getNumberOfAdjacencyCalls() + "");
		metricsMap.put(MetricTypes.SkipCallsMetric.toString(), 
				       layeringResult.getPartitionMetrics().getNumberOfSkipCalls() + "");
		metricsMap.put(MetricTypes.IntraCallsMetric.toString(), 
				       layeringResult.getPartitionMetrics().getNumberOfIntraCalls() + "");
		metricsMap.put(MetricTypes.BackCallsMetric.toString(), 
				       layeringResult.getPartitionMetrics().getNumberOfBackCalls() + "");
		addAttributesToAKDMElement(resultModel, metricsMap);
		 
		//add the structure model to the models of the segment
		mainSegment.getModel().add(resultModel);
		
		// add the segment to the resource
		SegmentExtractor.getInstance().getResourceModel().getContents().add(mainSegment);
		
		//save the resource. This operation allows to serialize the content of the kdm segment into an existing XMI file.
		try {
			SegmentExtractor.getInstance().getResourceModel().save(null);
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("--- An error occurred when populating the structure model");
		}
		
	}
	

	
	/**
	 * Create a structure relationship between two Components and save it the resource.
	 * I have thought that since the dependencies between the components of the structure model are inferred,
	 * then these components should be related by the mean of structure relationships (edges between the graph nodes)
	 * instead of aggregated relationships since the
	 * @param resourceModel
	 * @param node
	 * @param successor
	 */
	private void createAStructureRelationship(Resource resourceModel, Component startComponent,
			                                  Component endComponent, String dependencyName,
			                                  String dependencyWeight){
		/*System.out.println("Structure model : create aggregation relationship " +
				           "from node = " + startComponent.getName()
	                       + " to node = " + endComponent.getName());*/ 
		StructureRelationship structureRelation = StructureFactory.eINSTANCE.createStructureRelationship();
		structureRelation.setFrom(startComponent);
		structureRelation.setTo(endComponent);
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put(RELATION_NAME_TO_STRING, dependencyName);//RelationsTypes.Unspecified.toString()
		attributesMap.put(RELATION_WEIGHT_TO_STRING, "" + dependencyWeight);//startComponent.getSuccessorsList().get(endComponent)
		addAttributesToAKDMElement(structureRelation, attributesMap);
		//add the structure Relationship to the component
	    startComponent.getStructureRelationship().add(structureRelation);
	    //TODO also create relationship from endComponent to startComponent

	}
	
	/**
	 * 
	 * @param resourceModel
	 * @param node
	 * @param successor
	 */
	private void createAggregation(Resource resourceModel, AbstractGraphNode node, 
			                       AbstractGraphNode successor){
		
		/*System.out.println("Structure model : create aggregation relationship from node = " +
				           "" + node.getEntity().getName() + " to node = " + successor.getEntity().getName());*/ 
		AggregatedRelationship aggregatedRelationship = CoreFactory.eINSTANCE.createAggregatedRelationship();
		aggregatedRelationship.setFrom(node.getEntity());
		aggregatedRelationship.setTo(successor.getEntity());
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put(RELATION_NAME_TO_STRING, RelationsTypes.Unspecified.toString());
		attributesMap.put(RELATION_WEIGHT_TO_STRING, "" + node.getSuccessorsList().get(successor));
		addAttributesToAKDMElement(aggregatedRelationship, attributesMap);
		EList<AggregatedRelationship> nodeAggregatedRelationships = node.getEntity().getOutAggregated();
		nodeAggregatedRelationships.add(aggregatedRelationship);
		
		//add the aggregatedRelationship to the resource
		resourceModel.getContents().add(aggregatedRelationship );		
	}
	
	/**
	 * Add a set of attributes to a KDM element i.e relationship or model.
	 * the method setName() is not applicable (implemented) to annotation, 
	 * that is the reason why i use attributes instead of annotations to 
	 * add informations to a relationship.
	 * @param kdmElement
	 * @param attributesMap
	 */
	public void addAttributesToAKDMElement(ModelElement kdmElement, 
			                              Map<String, String> attributesMap) {
		// get the list of attributes of the KDM element
		EList<Attribute> relationAttributes = kdmElement.getAttribute();
		
		//add the attributes to the relationship
		for(String attributeKey : attributesMap.keySet()){
			Attribute attribute = KdmFactory.eINSTANCE.createAttribute();
			attribute.setTag(attributeKey);
			attribute.setValue(attributesMap.get(attributeKey));
			relationAttributes.add(attribute);
		}
	}
	
	/**
	 * Add a set of attributes to a KDM relationship.
	 * @param relation
	 * @param attributesMap
	 */
	public void addAnnotationsToARelation(ModelElement relation, 
			                              Map<String, String> annotationMap) {
		// get the list of attributes of the KDM relationship
		/*EList<Annotation> relationAnnotations = relation.getAnnotation();
		
		//add the attributes to the relationship
		for(String annotationKey : annotationMap.keySet()){
			Annotation annotation = KdmFactory.eINSTANCE.createAnnotation();
			annotation.setName(annotationKey);
			annotation.setText(annotationMap.get(annotationKey));
			relationAnnotations.add(annotation);
		}*/
	}

	/**
	 * Save the parameters of the algorithm into a structure model.
	 * @param contextMapp
	 * @return
	 */
	public StructureModel saveContext(Map< String, String> contextMap, Segment segment){
		//create an empty structure model
		StructureModel model = createEmptyStructureModel(segment);
		String modelName = AlgorithmContext.WizardArguments.StructureModelLayering.toString();
		model.setName(contextMap.get(modelName));
		
		//add attributes to the structure model. These attributes are the parameters of the algorithm	
		addAttributesToAKDMElement(model, contextMap);
	    return model;
	}
	
	/**
	 * Retrieve the metrics from the structure model.
	 * @param resultModel
	 * @return
	 */
	public Map<String, String> retrieveMetrics(StructureModel resultModel){
		Map< String, String> metricsMaps = new HashMap< String, String>();
		
		// get the list of attributes of the structure model
		EList<Attribute> modelAttributes = resultModel.getAttribute();
		for(Attribute attribu : modelAttributes){
			String attributeName = attribu.getTag();
			if( attributeName.equals(MetricTypes.AdjacencyCallsMetric.toString()))
				metricsMaps.put(MetricTypes.AdjacencyCallsMetric.toString(), attribu.getValue());
			else if( attributeName.equals(MetricTypes.SkipCallsMetric.toString()))
				metricsMaps.put(MetricTypes.SkipCallsMetric.toString(), attribu.getValue());
			else if( attributeName.equals(MetricTypes.IntraCallsMetric.toString()))
				metricsMaps.put(MetricTypes.IntraCallsMetric.toString(), attribu.getValue());
			else if( attributeName.equals(MetricTypes.BackCallsMetric.toString()))
				metricsMaps.put(MetricTypes.BackCallsMetric.toString(), attribu.getValue());
		}
		return metricsMaps;
	}
	

	/**
	 * Retrieve the structure model in the segment containing all the kdm models of the analyzed system.
	 * @param principalSegment
	 * @return
	 */
	public StructureModel findStructureModel(Segment principalSegment)  {
		 EList<StructureModel> structureModels = ExtractionFacade.getInstance().retrieveStructureModels(principalSegment);
		 StructureModel structureModel = null;
		 for(StructureModel structModel : structureModels){
			 if(structModel.getName().equals(structureModelNameMDG)){
				 structureModel = structModel;
				 break;
			 }
		 }
		
		if(structureModel == null)
			System.out.println("The structure model containing the extracted " +
					           "facts was not found. A new one should be created"); //$NON-NLS-1$
		return structureModel;
	}
	
	/**
	 * 
	 * Create an empty structure model and add it to the list of the kdm models contained in the 
	 * main KDM segment.
	 * @param principalSegment
	 * @return
	 */
	public StructureModel createEmptyStructureModel(Segment segment)  {
		StructureModel structureModel = StructureFactory.eINSTANCE.createStructureModel();
		//Retrieve all the models contained in the segment
		//EList<KDMModel> allTheProjectModels = segment.getModel();
		//allTheProjectModels.add(structureModel);
		return structureModel;
	}
	
	
	public String getStructureModelNameMDG() {
		return structureModelNameMDG;
	}
}
