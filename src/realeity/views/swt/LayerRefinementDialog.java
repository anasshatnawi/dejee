package realeity.views.swt;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * This code is inspired from http://www.vogella.com/tutorials/EclipseDialogs/article.html
 * License GPL.
 * @author alvine
 * 2014-11-29
 */
public class LayerRefinementDialog extends TitleAreaDialog {
	private static int DIALOG_WIDTH = 400;
	private static int DIALOG_HEIGHT = 200;


	private Text txtLayerName;

    private String inputName;
    private String dialogTitle;
    private String textName;

	  /**
	   * Class's constructor.
	   * @param parentShell
	   */
	  public LayerRefinementDialog(Shell parentShell, String dialogTitle,  String textName) {
	    super(parentShell);
	    this.dialogTitle = dialogTitle;
	    this.textName = textName;
	  }

	  @Override
	  public void create() {
	    super.create();
	    setTitle(dialogTitle);
	   // setMessage("This is a TitleAreaDialog", IMessageProvider.INFORMATION);
	  }

	  @Override
	  protected Control createDialogArea(Composite parent) {
	    Composite area = (Composite) super.createDialogArea(parent);
	    Composite container = new Composite(area, SWT.NONE);
	    container.setLayoutData(new GridData(GridData.FILL_BOTH));
	    GridLayout layout = new GridLayout(2, false);
	    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    container.setLayout(layout);

	    createLayerName(container);

	    return area;
	  }

	  private void createLayerName(Composite container) {
	    Label lbtFirstName = new Label(container, SWT.NONE);
	    lbtFirstName.setText(textName);

	    GridData dataFirstName = new GridData();
	    dataFirstName.grabExcessHorizontalSpace = true;
	    dataFirstName.horizontalAlignment = GridData.FILL;

	    txtLayerName = new Text(container, SWT.BORDER);
	    txtLayerName.setLayoutData(dataFirstName);
	  }
	  
	  @Override
	protected Point getInitialSize() {
		return new Point (DIALOG_WIDTH, DIALOG_HEIGHT);
	}

	  @Override
	  protected boolean isResizable() {
	    return true;
	  }

	  // save content of the Text fields because they get disposed
	  // as soon as the Dialog closes
	  private void saveInput() {
	    inputName = txtLayerName.getText();

	  }

	  @Override
	  protected void okPressed() {
		saveInput();
	    if(!inputName.equals("")){
	        setMessage("", IMessageProvider.INFORMATION);
	    	super.okPressed();
	    }
	    else
	    	setErrorMessage("The name should not be empty.");
	  }

	  @Override
	protected void cancelPressed() {
		inputName = "";
		super.cancelPressed();
	}
	  
	  public String getInputName() {
	    return inputName;
	  }
	  
	} 
