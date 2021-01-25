package realeity.application.actions;

import java.io.File;
import java.util.List;




import java.util.concurrent.TimeUnit;

import org.eclipse.gmt.modisco.omg.kdm.core.KDMEntity;
import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.draw2d.FigureCanvas;

import realeity.application.data.ActionMemento;
import realeity.application.data.GeneratedData;
import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.technique.extractor.ExtractionFacade;
import realeity.technique.extractor.ModuleDependencyBuilder;
import realeity.technique.extractor.SegmentExtractor;
import realeity.technique.extractor.fact.AbstractExtractedFact;
import realeity.technique.storage.files.FileProcessment;
import realeity.views.swt.wizard.layering.RecoveryWizard;


/**
 * License GPL.
 * January 2014
 * @author Alvine Boaye Belle
 *
 */
@SuppressWarnings("deprecation")
public abstract class AbstractMicroAction extends AbstractRecoveryAction {
	
	protected AbstractMacroAction associatedMacroAction;
	protected boolean runNextAction = false;
	protected static String kdmExtension = ".xmi";
	protected static String jspExtension = ".xmi";
	protected String errorMessage = "";
    protected String jobName;
	


	/**
	 * Class constructor.
	 * @param actionName
	 */
	public AbstractMicroAction(String actionName) {
		super(actionName);
	}
	
	@Override
    public void run() {
		
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * 
	 * @param seconds
	 */
	protected void sleepSomeSeconds(int seconds){
		try {
			TimeUnit.MILLISECONDS.sleep(seconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param structureModel
	 * @return
	 */
	protected AbstractModuleDependencyGraph extractMDGFromStructureModel(StructureModel structureModel){
		List<KDMEntity> entities = ModuleDependencyBuilder.getInstance().retrieveEntities(structureModel);
		List<AbstractExtractedFact> dependencies = ModuleDependencyBuilder.getInstance().retrieveDependencies(structureModel);
		AbstractModuleDependencyGraph graph = ExtractionFacade.getInstance().buildGraph(entities, 
				                                                                        dependencies);
		return graph;
	}
	
	/**
	 * Specifies the set of operations to monitor using a busy monitor.
	 */
	public abstract void monitorExecution();
	
	/**
	 * Displays a wizard to the user.
	 * @param wizard
	 */
	public void displayWizard(RecoveryWizard wizard, int width, int height){
		try{
			// Instantiates and initializes the wizard that allows setting the algorithm parameters
		    //RecoveryWizard wizard = new RecoveryWizard();
		    wizard.init(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getWorkbench(), null);  
		    // Instantiates the wizard container with the wizard and opens it
		    WizardDialog wizardDialog = new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
		    		                                     wizard);
		    wizardDialog.setPageSize(width, height);
		    wizardDialog.create();
		    wizardDialog.open();
		    
		    runNextAction = wizard.getLaunchRecovery();
		    if(runNextAction)
		    	 GeneratedData.getInstance().setLayeringContext(wizard.getContextMap());
		}
		catch(Exception e){
			System.out.println("An Error occured during the wizard opening");
			runNextAction = false;
			//e.printStackTrace();
		}	
	}

	
	public AbstractMacroAction getAssociatedMacroAction() {
		return associatedMacroAction;
	}
	
	
	
	public void setAssociatedMacroAction(AbstractMacroAction abstractMacroAction) {
		this.associatedMacroAction = abstractMacroAction;
	}

	
	public boolean getRunNextAction(){
		return runNextAction;
	}
	
	public void setRunNextAction(boolean executeNextAction){
		this.runNextAction = executeNextAction;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public ActionMemento createMemento() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getSelectedProject() {
		// TODO Auto-generated method stub
		return null;
	}
}
