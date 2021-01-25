package realeity.application.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
import org.eclipse.modisco.jee.jsp.Model;

import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.elements.LayeredPartition;
import realeity.technique.extractor.ExtractionFacade;
import realeity.technique.extractor.SegmentExtractor;
import realeity.technique.extractor.ServletToUrl;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.extractor.structuremodel.KDMModelsGeneration;
import realeity.technique.util.ProjectSelection;
import realeity.technique.util.RealeityUtils;
import uqam.dejee.element.serverpage.JspPage;
import uqam.dejee.technique.extractor.jsp.taglib.CustomTagLibrary;


/**
 * 
 * @author Alvine Boaye Belle
 * 2014
 */

public class GeneratedData {
	//TODO  create a generated data for each project!!!
	
	private Map<String,String> layeringContext = new HashMap<String, String>();
	private File selectedMDGProject;
	private File selectedLayeringProject;
	private String projectFilePath;
	private Segment projectMainSegment;
	private Model projectJspModel;
	private StructureModel factStructureModel;
	private StructureModel currentLayeringModel;
	private File currentZestFile;
	private File currentGraphvizFile;
	private static AbstractModuleDependencyGraph  extractedGraph;
	private LayeredPartition layeringResult;
	private LayeredPartition refinementResult;
	private RealeityUtils.MainActions lastAction = RealeityUtils.MainActions.None; 
	private boolean projectChanged = false;
	private List<ServletToUrl> servletToUrlsMapp = new ArrayList<ServletToUrl>();
	private Vector<JspPage> jspPages = new Vector<JspPage>();
//	private CustomTagLibrary;


	/**
	 * Default constructor.
	 */
	private GeneratedData(){
		resetAll();
	}
	
	/**
	 * unique instance of LayeringFacade.
	 */
	private static GeneratedData uniqueInstance;
	
	/**
	 *Method insuring that a unique instance of LayeringFacade is created.
	 * @return uniqueInstance
	 */
	public static GeneratedData getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new GeneratedData();
		}
		return uniqueInstance;
	}
	
	/**
	 * Initialisation
	 */
	public void resetAll (){
		layeringContext = new HashMap<String, String>();
		factStructureModel = null;
		currentLayeringModel = null;
		currentZestFile = null;
		projectFilePath = "";
		currentGraphvizFile = null;
		//selectedMDGProject = null;
		//projectMainSegment = null;
		extractedGraph = null;
		layeringResult = null;
		refinementResult = null;
		servletToUrlsMapp=null;
		jspPages=null;

	}
	
	/**
	 * Initialisation
	 */
	public void resetForLayering (){
		//factStructureModel = null;
		layeringContext = new HashMap<String, String>();
		currentLayeringModel = null;
		currentZestFile = null;
		currentGraphvizFile = null;
		layeringResult = null;
		refinementResult = null;
		//selectedProject = null;
		//projectMainSegment = null;
		//extractedGraph = null;
	}
	
	/**
	 * 
	 * @param event
	 */
	public void setDataBeforeLayering(File selectedProjectForLayering){
		//TODO  create a generated data for each project and adapt or delete this method!!!
		selectedLayeringProject = selectedProjectForLayering;
		projectChanged = false;
		if(lastAction.equals(RealeityUtils.MainActions.Layering)){
			//we just did a layering and we redo another layering
			if(!selectedLayeringProject.getAbsolutePath().equals(selectedMDGProject.getAbsolutePath())){
				//we still do the layering but switch to another project
				selectedMDGProject = selectedLayeringProject;
				projectChanged = true;
				projectMainSegment = SegmentExtractor.getInstance().findKDMSegment(selectedMDGProject);
			}
		}
		else if(lastAction.equals(RealeityUtils.MainActions.Extraction)){
			if(!selectedLayeringProject.getAbsolutePath().equals(selectedMDGProject.getAbsolutePath())){
				//we did the extraction in a project and now do the layering in another project
				selectedMDGProject = selectedLayeringProject;
				projectChanged = true;
				projectMainSegment = SegmentExtractor.getInstance().findKDMSegment(selectedMDGProject);
			}
		}
		else if(lastAction.equals(RealeityUtils.MainActions.None)){
			//neither extraction nor layering was done before
			selectedMDGProject = selectedLayeringProject;
			projectChanged = true;
			projectMainSegment = SegmentExtractor.getInstance().findKDMSegment(selectedMDGProject);
		}
			
	}
	
	/**
	 * 
	 * @param event
	 */
	public void setDataBeforeExtraction(File selectedProjectForExtraction){
		//TODO  create a generated data for each project and adapt or delete this method!!!
		projectChanged = false;
		if(lastAction.equals(RealeityUtils.MainActions.Layering)){
			//we just did a layering and we do the extraction in a different project
			if(!selectedMDGProject.getAbsolutePath().equals(selectedProjectForExtraction.getAbsolutePath())){
				//selectedMDGProject contained the project used to perform the layering
				selectedMDGProject = selectedProjectForExtraction;
				projectChanged = true;
			}
		}
		else if(lastAction.equals(RealeityUtils.MainActions.Extraction)){
			if(!selectedMDGProject.getAbsolutePath().equals(selectedProjectForExtraction.getAbsolutePath())){
				//we did the extraction in a project and now do the extraction in another project
				selectedMDGProject = selectedProjectForExtraction;
				projectChanged = true;
			}
		}
		else if(lastAction.equals(RealeityUtils.MainActions.None)){
			//neither extraction nor layering was done before
			selectedMDGProject = selectedProjectForExtraction;
			projectChanged = true;
		}
	}
	
	
	public static AbstractModuleDependencyGraph getExtractedGraph() {
		return extractedGraph;
	}
	
	public Segment getProjectMainSegment() {
		return projectMainSegment;
	}
	public File getCurrentGraphvizFile() {
		return currentGraphvizFile;
	}
	
	public StructureModel getCurrentLayeringModel() {
		return currentLayeringModel;
	}
	public File getCurrentZestFile() {
		return currentZestFile;
	}
	
	public StructureModel getFactStructureModel() {
		return factStructureModel;
	}
	
	public Map<String, String> getLayeringContext() {
		return layeringContext;
	}
	
	public LayeredPartition getLayeringResult() {
		return layeringResult;
	}
	
	public LayeredPartition getRefinementResult() {
		return refinementResult;
	}
	
	public File getSelectedProject() {
		return selectedMDGProject;
	}
	
	public void setCurrentGraphvizFile(File currentGraphvizFile) {
		this.currentGraphvizFile = currentGraphvizFile;
	}
	
	public void setCurrentLayeringModel(StructureModel currentLayeringModel) {
		this.currentLayeringModel = currentLayeringModel;
	}

	public void setProjectMainSegment(Segment projectMainSegment) {
		this.projectMainSegment = projectMainSegment;
	}
	
	public void setCurrentZestFile(File currentZestFile) {
		this.currentZestFile = currentZestFile;
	}
	
	
	public void setFactStructureModel(StructureModel factStructureModel) {
		this.factStructureModel = factStructureModel;
	}
	
	public void setSelectedProject(File selectedProject) {
		this.selectedMDGProject = selectedProject;
	}
	
	public static void setExtractedGraph(
			AbstractModuleDependencyGraph extractedGraph) {
		GeneratedData.extractedGraph = extractedGraph;
	}
	
	public void setLayeringContext(Map<String, String> layeringContext) {
		this.layeringContext = layeringContext;
	}
	
	public void setLayeringResult(LayeredPartition layeringResult) {
		this.layeringResult = layeringResult;
	}
	
	public File getSelectedLayeringProject() {
		return selectedLayeringProject;
	}
	
	public void setSelectedLayeringProject(File selectedLayeringProject) {
		this.selectedLayeringProject = selectedLayeringProject;
	}
	
	public void setRefinementResult(LayeredPartition refinementResult) {
		this.refinementResult = refinementResult;
	}
	
	public RealeityUtils.MainActions getLastAction() {
		return lastAction;
	}
	
	public void setLastAction(RealeityUtils.MainActions lastAction) {
		this.lastAction = lastAction;
	}
	
	public boolean getprojectChanged(){
		return projectChanged;
	}
	
	public Model getProjectJspModel() {
		return projectJspModel;
	}

	public void setProjectJspModel(Model projectJspModel) {
		this.projectJspModel = projectJspModel;
	}
	
	public List<ServletToUrl> getServletToUrlsMapp() {
		return servletToUrlsMapp;
	}

	public void setServletToUrlsMapp(List<ServletToUrl> servletToUrlsMapp) {
		this.servletToUrlsMapp = servletToUrlsMapp;
	}
	
	public Vector<JspPage> getJspPages() {
		return jspPages;
	}

	public void setJspPages(Vector<JspPage> jspPages) {
		this.jspPages = jspPages;
	}
	
}
