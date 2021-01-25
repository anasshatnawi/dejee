package realeity.views.swt.wizard.layering.refinement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import java.util.StringTokenizer;

import javax.swing.JPanel;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.NodeList;

import realeity.application.data.GeneratedData;
import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.core.mdg.elements.GraphNode;
import realeity.core.mdg.elements.LayerP;


/**
 * First page of the wizard.
 * In this case, the wizard comprises a single page.
 * @author Mathieu Tétreault
 * @author Alvine Boaye Belle
 * 2013  16:40:45
 */
public class LayeringRefinementWizardPageOne extends WizardPage implements Listener {
	
	/**
	 * Elements entered in the workbench selection once the wizard is started
	 */
	private Map<String,String> enteredElements;
	
	/**
	 * Number of Columns in this wizard page.
	 */
	private static int columnNumber = 2;
	
	private final int TEXT_HEIGHT = 18;
	private final int TEXT_WIDTH = 210;
	private final int COMBO_HEIGHT = 35;
	private final int COMBO_WIDTH = 110;
	
    private String layerToName = "";

	//widgets
	JPanel algorithmParametersPanel;
	private Combo layersToList;
	


	boolean textWellCompleted = false;
	
	private static final String REFINEMENT_WIZARD_NAME = "Layering Refinement";
	
	/**
	 *  flag indicating  whether the first page of the wizard can be completed or not 
	 */
	protected boolean firstPageFilled = false;
	
	private List<Button> layerList;
	

	
	/**
	 * Class's constructor.
	 */
	public LayeringRefinementWizardPageOne(IWorkbench workbench, IStructuredSelection selection) {
		super("New recovery");
		setTitle(REFINEMENT_WIZARD_NAME);
	}
	

	public void createControl(Composite compositeParent) {
		//Creation of the composite that comprises the widgets
		Composite compositeElement =  new Composite(compositeParent, SWT.NULL);

	    // create the layout for the wizard page
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = columnNumber;
		compositeElement.setLayout(gridLayout);
		
	    // create the Texts that comprises the files names 
		
		//graphNameText = createTextarea(compositeElement, "Name of the refinement output : ");
		//createLine(compositeElement, columnNumber);
		List<String> layersNameList = fillLayersList();
		
		String[] layerNames = new String[layersNameList.size()];
		layerNames = layersNameList.toArray(layerNames);
		layersToList = createComboList(compositeElement, "Choose the destination layer: ", layerNames);
				
	    //set the composite as the control for this page
		setControl(compositeElement);		
		addListeners();
		setPageComplete(true);
		this.getShell().pack();
	}
	
	
	private void addListeners(){

		//////////////
		layersToList.addListener(SWT.Selection, this);
		
	}	
		
	
	/**
	 * 
	 * @param composite
	 * @param textToDisplay
	 * @return
	 */
	private Text createTextarea(Composite composite, String textToDisplay){
		Label zestLabel = new Label (composite, SWT.NONE);
		zestLabel.setText(textToDisplay);
		Text nameText = new Text(composite, SWT.BORDER);
		GridData gridData = new GridData(TEXT_WIDTH,TEXT_HEIGHT);
		//gd.horizontalAlignment = GridData.BEGINNING;
		nameText.setLayoutData(gridData);
		return nameText;
	}
	
	/**
	 * 
	 * @param compositeElement
	 * @param comboText
	 * @return
	 */
	private Combo createComboList(Composite compositeElement, String comboText, 
			                      String[] list){
		new Label (compositeElement, SWT.BOLD).setText(comboText);
		Combo algoCombo = new Combo(compositeElement, SWT.BORDER | SWT.READ_ONLY);
		GridData gridData = new GridData(COMBO_WIDTH, COMBO_HEIGHT);
		//GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		algoCombo.setLayoutData(gridData);
		algoCombo.setItems(list);
		algoCombo.setText(list[0]);
		return algoCombo;
	}
	
	
	private Table createSWTNodeList(Composite compositeElement, String comboText, 
                                   String[] list){
		
		new Label (compositeElement, SWT.BOLD).setText(comboText);
		Table nodeList = new Table(compositeElement, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		GridData gridData = new GridData(COMBO_WIDTH, COMBO_HEIGHT);
		//GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		nodeList.setLayoutData(gridData);
		int indexNode = 1;
		if(list != null){
			for(String stringL : list){
				 TableItem item = new TableItem(nodeList, SWT.NONE);
			     item.setText((indexNode++) + ". " + stringL);
			}
		}
		
		//algoCombo.setItems(list);
		//algoCombo.setText(list[0]);
		return nodeList;
		
	}
	
	
	private void updateSWTNodeList(Table nodeTable, String[] list){
		nodeTable.removeAll();
		int indexNode = 1;
		if(list != null){
			for(String stringL : list){
				 TableItem item = new TableItem(nodeTable, SWT.NONE);
			     item.setText((indexNode++) + ". " + stringL);
			}
		}
		nodeTable.redraw();
	}
	
	/**
	 * 
	 * @param compositeElement
	 * @param buttontText
	 * @param isSelected
	 * @return
	 */
	private Button createButton(Composite compositeElement, String buttontText, 
			                    boolean isSelected){
		 //create the omnipresent button. The latter is unchecked by default
  		Button omniButton = new Button(compositeElement, SWT.CHECK);
  		omniButton.setText(buttontText);//if selected, Remove the omnipresent nodes when clustering
  		GridData gridData = new GridData();
  		//gd = new GridData(GridData.FILL_HORIZONTAL);
  		//gd.horizontalSpan = ncol;
  		omniButton.setLayoutData(gridData);
  		omniButton.setSelection(isSelected);
  		return omniButton;
	}

	/**
	 * 
	 * @return
	 */
	private List<String> fillLayersList(){
		List<String> layerList = new ArrayList<>(); // new String[GeneratedData.getInstance().getLayeringResult().getLayerList().size() + 1];
		layerList.add("Select a Layer");
		int indexLayer = 0;
		for(LayerP layer : GeneratedData.getInstance().getLayeringResult().getLayerList()){
			if(layer.getLayerContent().size() > 0){
				if(layer.getUserDefinedName().equals(""))
					layerList.add("Layer " + (GeneratedData.getInstance().getLayeringResult().getLayerList().size()
						           - (indexLayer)));
				else 
					layerList.add("Layer " + layer.getUserDefinedName());
				indexLayer++;
			}
		}
		return layerList;
	}
	
	
	public void handleEvent(Event event) {
		/**if(layersToList.getSelectionIndex()== 0){
			setErrorMessage("You should enter the name of the graph file.");
			this.firstPageFilled = false;
		}*/
		if(layersToList.getSelectionIndex() > 0) {
			layerToName = layersToList.getItem(layersToList.getSelectionIndex());
			setErrorMessage(null);
			setMessage(null);
			fillContextMap();
		}
		else
			this.firstPageFilled = false;
			
		getWizard().getContainer().updateButtons();		
	}
	
	/**
	 * Create the algorithm context. Checks whether it is a new context. 
	 */
	
	void fillContextMap(){
		//create the context
		//check if it is a new context.
		enteredElements = new HashMap<String,String>(); 
		
      /*  for(String key : enteredElements.keySet()){//delete after
        	System.out.println(key + " : " + enteredElements.get(key));
        }
        System.out.println("//////////////////////////////////////////////////");*/
		this.firstPageFilled = true;
		setPageComplete(true);
       
	}

	public boolean canFlipToNextPage(){
		// there is no next page in this wizard
		return false;
	}
	

	private void createLine(Composite parent, int ncol){
		Label line = new Label(parent, SWT.SEPARATOR|SWT.HORIZONTAL|SWT.BOLD);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = ncol;
		line.setLayoutData(gridData);
	}		
	

	void onEnterPage(){
	   
	}
	
	public Map<String,String>  getenteredElements(){
		return enteredElements;
	}
	
	public boolean getFirstPageCompleted(){
		return this.firstPageFilled;
	}
	
	public void setFirstPageCompleted(boolean firstPageCompleted){
		this.firstPageFilled = firstPageCompleted;
	}
	
	public String getLayerToName() {
		return layerToName;
	}

}

