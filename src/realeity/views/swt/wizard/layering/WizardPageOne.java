package realeity.views.swt.wizard.layering;

import java.util.HashMap;
import java.util.Map;



import javax.swing.JPanel;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
//import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;

import realeity.core.layering.algorithms.settings.AlgorithmContext;


/**
 * First page of the wizard.
 * In this case, the wizard comprises a single page.
 * @author Mathieu Tétreault
 * @author Alvine Boaye Belle
 * 2013  16:40:45
 */
class WizardPageOne extends WizardPage implements Listener {
	
	/**
	 * Elements entered in the workbench selection once the wizard is started
	 */
	private Map<String,String> enteredElements;
	
	/**
	 * Number of Columns in this wizard page.
	 */
	private static int columnNumber = 2;
	
	private final int TEXT_HEIGHT = 18;
	private final int TEXT_WIDTH = 240;
	private final int ALGO_COMBO_HEIGHT = 18;
	private final int ALGO_COMBO_WIDTH = 208;
	private final int SPINNER_WIDTH = 30;
	private final int SPINNER_HEIGHT = 18;
	
	private final int RANDOM_ITER_DEFAULT = 50;
	private final int TABU_LIST_SIZE_DEFAULT = 10 ;
	private final int TABU_MAXIMAL_ITERATIONS_DEFAULT = 100;
	private final int INITIAL_LAYERS_NUMBER = 3; // number of layers in the initial partition
	
	/**
	 * Max value of the parameter sliders.
	 */
	public static final int SLIDER_MAX_VALUE = 50000;
	private boolean skipValueEntered = false;
	private boolean intraValueEntered = false;
	//private int backCallMinValue = 0;
	private int backSliderValue = 0;
	private int skipSliderValue = 0;
	private int intraSliderValue = 0;
	private int adjacencySliderValue = 0;
	
	public static final String SLIDER_HELP_TEXT_1 = "1. Select the Intra and Skip-calls penalties.";
	public static final String SLIDER_HELP_TEXT_2 = "2. Choose the Back-calls penalties.";
	public static final String SKIP_CALL_SLIDER_NAME = "Skip penalty";
	public static final String INTRA_SLIDER_NAME = "Intra penalty";
	
	//widgets
	JPanel algorithmParametersPanel;
	private Button omnipresentCheckBox;
	private Text graphNameText;
	private Spinner layerNumberSlider;
	private Combo algoList;
	private Combo fitnessList;
	private Spinner adjacencySlider;
	private Spinner backCallsSlider;
	private Spinner skipCallsSlider;
	private Spinner intraSlider;
	
	private Spinner randomIterSlider;
	private Spinner tabuListSizeSlider;
	private Spinner tabuMaxIterSlider;
	
	private final static String[] algorithmsToRun = {"Tabu search algorithm",
		                                             "SAHC layering algorithm"
		                                             };/*{"Repetitive layering", "Simple 3-layering",
		                                             "Relayering", "Recursive relayering"};*/
	private final static String[] fitnessFunctionsToRun = {"Layering Cost (LC)"};//{"GraphViz", "Zest"};

	boolean textWellCompleted = false;
	
	/**
	 *  flag indicating  whether the first page of the wizard can be completed or not 
	 */
	protected boolean firstPageFilled = false;
	
	
	/**
	 * Class's constructor.
	 */
	public WizardPageOne(IWorkbench workbench, IStructuredSelection selection) {
		super("New recovery");
		setTitle("Recovery parameters");
		//TODO check if the name of the has already been used
		//setDescription("Select the parameters used to create the module dependency graph");
	}
	

	public void createControl(Composite compositeParent) {
		//Creation of the composite that comprises the widgets
		Composite compositeElement =  new Composite(compositeParent, SWT.NULL);

	    // create the layout for the wizard page
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = columnNumber;
		compositeElement.setLayout(gridLayout);
		
	    // create the Texts that comprises the files names 
		
		graphNameText = createTextarea(compositeElement, "Name of the output : ");
		//createLine(compositeElement, columnNumber);
		
		
		//create the list containing the recovery algorithms
		algoList = createAlgorithmsCombo(compositeElement, "Recovery algorithm:", 
				                         algorithmsToRun);
		//createLine(compositeElement, columnNumber);
		
		////create the list containing the tools used to generate the graphs
		fitnessList = createAlgorithmsCombo(compositeElement, "Fitness function : ", 
					                                  fitnessFunctionsToRun);
		layerNumberSlider = createSWTSlider(compositeElement, true, "Number n of layers: ", INITIAL_LAYERS_NUMBER);
		 
	    createLine(compositeElement, columnNumber);
		
		//create the omnipresent button. The latter is unchecked by default
        omnipresentCheckBox = createTheOmnipresentButton(compositeElement, "Discard Omnipresent", 
        		                                         false);
        createLine(compositeElement, columnNumber);

		
		//create the panel containing the sliders 
		adjacencySlider = createSWTSlider(compositeElement, false, "Adjacency penalty: ", 0);
		intraSlider = createSWTSlider(compositeElement, true, "Intra penalty: ", 0);
		skipCallsSlider = createSWTSlider(compositeElement, true, "Skip-calls penalty: ", 0);
		backCallsSlider = createSWTSlider(compositeElement, false, "Back-calls penalty: ", 0);
		
		randomIterSlider = createSWTSlider(compositeElement, true, "Random iterations: ", 0);
		tabuListSizeSlider = createSWTSlider(compositeElement, true, "Tabu list size: ", 0);
		tabuMaxIterSlider = createSWTSlider(compositeElement, true, "Tabu maximal iterations: ", 0);
	
		
		createLine(compositeElement, columnNumber);
		
		//createPenaltyPanel(compositeElement);
		
	    //set the composite as the control for this page
		setControl(compositeElement);		
		addListeners();
		setPageComplete(true);
	}
	
	
	private void addListeners(){
        omnipresentCheckBox.addListener(SWT.Selection, this);
		algoList.addListener(SWT.Selection, this);
		fitnessList.addListener(SWT.Selection, this);
		graphNameText.addListener(SWT.KeyUp, this);
		layerNumberSlider.addListener(SWT.Selection, this); //useful for the method handleEvent
		adjacencySlider.addListener(SWT.Selection, this);  //useful for the method handleEvent
		intraSlider.addListener(SWT.Selection, this); //useful for the method handleEvent
		skipCallsSlider.addListener(SWT.Selection, this);  //useful for the method handleEvent
		backCallsSlider.addListener(SWT.Selection, this); 

		randomIterSlider.addListener(SWT.Selection, this);  //useful for the method handleEvent
		tabuListSizeSlider.addListener(SWT.Selection, this);  //useful for the method handleEvent
		tabuMaxIterSlider.addListener(SWT.Selection, this);  //useful for the method handleEvent
		/*.addFocusListener
		(new FocusListener(){
			public void focusGained(FocusEvent e){
				
			}
			public void focusLost(FocusEvent e){
				if(firstPageFilled){
					textFilled();
				}
					
			}
		}
		);*/
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
	private Combo createAlgorithmsCombo(Composite compositeElement, String comboText, 
			                            String[] list){
		new Label (compositeElement, SWT.BOLD).setText(comboText);
		Combo algoCombo = new Combo(compositeElement, SWT.BORDER | SWT.READ_ONLY);
		GridData gridData = new GridData(ALGO_COMBO_WIDTH, ALGO_COMBO_HEIGHT);
		//GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		algoCombo.setLayoutData(gridData);
		algoCombo.setItems(list);
		algoCombo.setText(list[0]);
		return algoCombo;
	}
	
	/**
	 * 
	 * @param compositeElement
	 * @param buttontText
	 * @param isSelected
	 * @return
	 */
	private Button createTheOmnipresentButton(Composite compositeElement, String buttontText, 
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
	 * This code is inspired from 
	 * http://git.eclipse.org/c/platform/eclipse.platform.swt.git/tree/examples/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet45.java
	 * @param parentComposite
	 * @return
	 */
	private Spinner createSWTSlider(final Composite composite, boolean sliderIsActive, 
			                        String sliderName, int MinValue){
		Label sliderLabel = new Label (composite, SWT.NONE);
		sliderLabel.setText(sliderName);
		final Spinner slider = new Spinner (composite, SWT.BORDER);
		GridData gridData = new GridData(SPINNER_WIDTH,SPINNER_HEIGHT);
		slider.setLayoutData(gridData);//setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		slider.setMinimum(MinValue);
		slider.setMaximum(SLIDER_MAX_VALUE);
		slider.setSelection(0);//the spinner displays a zero by default
		slider.setIncrement(1);
		slider.setPageIncrement(1);
		Rectangle clientArea = composite.getClientArea();
		//slider.setLocation(clientArea.x, clientArea.y);
		slider.setBounds(clientArea.x, clientArea.y, 25, 25);
		//slider.setForeground(composite.getDisplay().getSystemColor(SWT.COLOR_GREEN));
		//slider.pack();
		slider.setEnabled(sliderIsActive);
        slider.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent spinnerEvent) {
		       
		    	compareBackCallValues(spinnerEvent, composite);
		      }
		    });
		return slider;
	}
	
	/**
	 * Just to see how to create a panel with four columns
	 * @param parentComposite
	 */
	public void createPenaltyPanel(Composite parentComposite){
		Composite penaltyComposite = new Composite(parentComposite, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		penaltyComposite.setLayout(gridLayout);
		new Button(penaltyComposite, SWT.PUSH).setText("Button One");
		new Button(penaltyComposite, SWT.PUSH).setText("Button Two");
		new Button(penaltyComposite, SWT.PUSH).setText("Button 3");
		new Button(penaltyComposite, SWT.PUSH).setText("Button 4");
	}

	
	/**
	 * Method comparing the backCall slider value with the one from another slider
	 * and updating the value if it's needed.
	 * Inspired from the code of Mathieu Tétreault.
	 * @param sliderEvent The slider with the new value to compare.
	 */
	public void compareBackCallValues(SelectionEvent sliderEvent, Composite compositeElement) {
		Spinner selectedSpinner = (Spinner) sliderEvent.getSource();
		int spinnerValue = selectedSpinner.getSelection(); 
		if(spinnerValue < SLIDER_MAX_VALUE){
			if (selectedSpinner == skipCallsSlider){
				skipSliderValue = spinnerValue;
				skipValueEntered = true;
				backSliderValue = maxOfThreeValues(skipSliderValue, intraSliderValue, 
						                           backSliderValue);
			    updateBackCallValues(compositeElement);

			} else if (selectedSpinner == intraSlider) {
				intraSliderValue = spinnerValue;
				intraValueEntered = true;
				backSliderValue = maxOfThreeValues(skipSliderValue, intraSliderValue, 
						                           backSliderValue);
			    updateBackCallValues(compositeElement);
			}
			else if (selectedSpinner == backCallsSlider) {
					backSliderValue = maxOfThreeValues(skipSliderValue, intraSliderValue, 
							                           spinnerValue);
					backCallsSlider.setSelection(backSliderValue);
			     }
		}	
	}
	
	/**
	 * Compute the maximum of 3 values.
	 * @param value1
	 * @param value2
	 * @param value3
	 * @return
	 */
	private int maxOfThreeValues(int value1, int value2, int value3){
		int max = 0;
		int intermediairyMax = Math.max(value1, value2);
		max = Math.max(intermediairyMax, value3);
		return max;
	}
	/**
	 * Method updating the Back Call slider values.
	 * Inspired from the code of Mathieu Tétreault.
	 */
	private void updateBackCallValues(Composite compositeElement) {
		//set the default value displayed by backCallsSlider
		backCallsSlider.setSelection(backSliderValue);
		
		if (intraValueEntered && skipValueEntered) {
			backCallsSlider.setEnabled(true);
			
		} else {
			backCallsSlider.setEnabled(false);
		}
	}

	/**
	 * 
	 */
	void textFilled(){
		textWellCompleted = true;
		fillContextMap();
	}

	
	public void handleEvent(Event event) {
		if(algorithmsToRun[algoList.getSelectionIndex()].equals("Tabu search algorithm")){
			 activateTabuParams(true);
		}
		else if(!algorithmsToRun[algoList.getSelectionIndex()].equals("Tabu search algorithm")){
			activateTabuParams(false);
		}
		if(graphNameText.getText().equals("")){
			setErrorMessage("You should enter the name of the graph file.");
			this.firstPageFilled = false;
		}
		else{
				setErrorMessage(null);
				setMessage(null);
				fillContextMap();
			}
			
		getWizard().getContainer().updateButtons();		
	}
	
	/**
	 * 
	 * @param activation
	 */
	private void activateTabuParams(boolean activation){
	
	            tabuListSizeSlider.setEnabled(activation);
	            tabuMaxIterSlider.setEnabled(activation);
	}
	/**
	 * Create the algorithm context. Checks whether it is a new context. 
	 */
	
	void fillContextMap(){
		//create the context
		//check if it is a new context.
		enteredElements = new HashMap<String,String>(); 
		enteredElements.put(AlgorithmContext.WizardArguments.StructureModelLayering.toString(), 
				            "StructureModel_" + this.graphNameText.getText());
		enteredElements.put(AlgorithmContext.WizardArguments.GraphFileName.toString(), 
				            this.graphNameText.getText());
		enteredElements.put(AlgorithmContext.WizardArguments.DiscardOmnipresent.toString(), 
				            omnipresentCheckBox.getSelection() + "");
		enteredElements.put(AlgorithmContext.WizardArguments.ChosenAlgorithm.toString(), 
				            algorithmsToRun[algoList.getSelectionIndex()]);
		enteredElements.put(AlgorithmContext.WizardArguments.FitnessFunction.toString(), 
	                        fitnessFunctionsToRun[fitnessList.getSelectionIndex()]);
		enteredElements.put(AlgorithmContext.WizardArguments.nbSpecifiedLayers.toString(), 
				            layerNumberSlider.getSelection() + "");
		enteredElements.put(AlgorithmContext.WizardArguments.AdjacencyPenalty.toString(), 
				            adjacencySliderValue + "");
		enteredElements.put(AlgorithmContext.WizardArguments.IntraPenalty.toString(), 
				            intraSliderValue + "");
		enteredElements.put(AlgorithmContext.WizardArguments.SkipCallsPenalty.toString(),
				            skipSliderValue + "");
		enteredElements.put(AlgorithmContext.WizardArguments.BackPenalty.toString(), 
				            backSliderValue + "");
		
		enteredElements.put(AlgorithmContext.WizardArguments.RandomIter.toString(), 
				            randomIterSlider.getSelection() + "");
		
		if(tabuListSizeSlider.isEnabled() && tabuMaxIterSlider.isEnabled()){
			enteredElements.put(AlgorithmContext.WizardArguments.TabuListSize.toString(), 
		                        tabuListSizeSlider.getSelection() + "");
            enteredElements.put(AlgorithmContext.WizardArguments.TabuMaxIter.toString(), 
		                        tabuMaxIterSlider.getSelection() + "");
		}
		else{
			enteredElements.put(AlgorithmContext.WizardArguments.TabuListSize.toString(), "-1");
			 enteredElements.put(AlgorithmContext.WizardArguments.TabuMaxIter.toString(), "-1");
		}
		
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
	

	private static boolean textIsFilled(Text fileName)
	{
		String text = fileName.getText();
		if ((text != null) && (text.trim().length() > 0))
			return true;
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

}

