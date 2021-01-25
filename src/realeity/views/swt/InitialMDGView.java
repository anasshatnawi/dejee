package realeity.views.swt;

import java.util.Iterator;

import javax.swing.UIManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.LayoutAlgorithm;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import realeity.application.data.GeneratedData;
import realeity.application.layout.zest.NodeModelContentProviderZest;
import realeity.application.layout.zest.ZestLabelProvider;
import realeity.application.layout.zest.ZestNodeContentProvider;
import realeity.core.mdg.AbstractModuleDependencyGraph;
import realeity.core.mdg.elements.AbstractGraphNode;
import realeity.technique.util.KDMEntityFullName;

import com.plugin.realeity.version1.Activator;


/**
 * 
 * @author Alvine
 *
 */
@SuppressWarnings("restriction")
public class InitialMDGView extends ViewPart implements ISelectionListener, IRecoveryView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = Activator.PLUGIN_ID; //is equal to Activator.PLUGIN_ID;
	private static final String VIEW_ID = "com.plugin.realeity.version1.initialMDG";
	private String titleView = "ReALEITY";
	

	private static GraphViewer initialMDGViewer;
	
	private static Shell viewerShell;
	
	//TODO factoriser cet attribut qui se retrouve aussi dans certaines vues
	private static int curveDepth = 45;
	
	//layers.png and chart_bar.png comes from the website http://www.famfamfam.com/lab/icons/silk/
	
	/**
	 * The constructor.
	 * The code in the try catch clause is derived from the code of Mathieu Tétreault.
	 */
	@SuppressWarnings("restriction")
	public InitialMDGView() {
		/*This code comes from Mathieu tetreault
		 * Try/catch setting the look and feel (the UI) of the application.
		 * More details : http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {//allows to use the look and feel of the Operating System used
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		
		viewerShell = parent.getShell();
		initialMDGViewer = new GraphViewer(parent, SWT.BORDER);
		initialMDGViewer.setContentProvider(new ZestNodeContentProvider());
		initialMDGViewer.setLabelProvider(new ZestLabelProvider());
		InitialMDGView.getInitialMDGViewer().setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);//to draw directed edges
		/*NodeModelContentProvider model = new NodeModelContentProvider();
	    layeringViewer.setInput(model.getNodes());*/
	    LayoutAlgorithm layout = setLayout();
	    initialMDGViewer.setLayoutAlgorithm(layout, true);
	    initialMDGViewer.applyLayout();//layeringViewer.setSorter(new NameSorter());
	    
	    /*
	     * Makes the selection available to the workbench.
	     * See http://www.vogella.com/tutorials/EclipseCommandsAdvanced/article.html#commands_restrictions
	     */
	    getSite().setSelectionProvider(initialMDGViewer);
	
	}
	
	/**
	 * Change the input of the view
	 * @param model
	 */
	public static void update(NodeModelContentProviderZest model){
		try{
			initialMDGViewer.setInput(model.getNodes());
		}
		catch (NullPointerException exception) {
			// TODO: handle exception
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void updateView(AbstractModuleDependencyGraph graph) {
		//TO-DO DEPLACER une partie de CE CODE VERS LA VUE!!!
		System.out.println();
		try{
			NodeModelContentProviderZest model = new NodeModelContentProviderZest(graph); 
			InitialMDGView.update(model);
			
			//put the view in front or open it so that it is not considered as null
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(VIEW_ID);
		    
		    //customize the nodes
		    java.util.List<GraphNode> nodeList = initialMDGViewer.getGraphControl().getNodes();
		    Display display = InitialMDGView.getViewerShell().getDisplay();
		    Color backgroundColor = new Color(display,223, 242, 255);//37, 253, 233);//255, 255, 107);
		    for (GraphNode node : nodeList) {//setting the weight of the connections
		    	node.setBackgroundColor(backgroundColor);
		    }
		   
		    
		    //customize the connections
		    Color connectionColor = new Color(display,1,1, 2);
		    java.util.List<GraphConnection> connections = initialMDGViewer.getGraphControl().getConnections();
		    customizeConnections(graph, connections, connectionColor);
		   /* for(GraphConnection conn : connections){
		    	conn.setCurveDepth(3);
		    }*/
		    
		    //display the new graph on the view
		    InitialMDGView.getInitialMDGViewer().applyLayout(); 
		}
		catch (PartInitException e) {
			System.out.println("The MDG view could not be put in front so as to be seen" +
					           " by the user.");
		}
		catch(org.eclipse.swt.SWTException exception){
			//this exception occurs when the window displaying the graph is closed without a prompt
			System.out.println("An error occurred when updating the ReALEITY-MDG view");
		}
		catch(NullPointerException exception){
			//this exception occurs when the window is hidden behind another window
			System.out.println("An error occurred. The module dependency graph " +
					           "can not be displayed. Put it on focus");
			//exception.printStackTrace();
		}
		
	}	
	
	
	
	/**
	 * Set the weights of the connections
	 * @param initialMDG
	 * @param connections
	 */
	public static void  customizeConnections(AbstractModuleDependencyGraph initialMDG, 
			                        java.util.List<GraphConnection> connections,
			                        Color connectionColor){
		for(GraphConnection conn : connections){
			conn.setLineColor(connectionColor);
			GraphNode source = conn.getSource();
		    
			GraphNode destination = conn.getDestination();
			AbstractGraphNode sourceInMDG = null;
			AbstractGraphNode destinationInMDG = null;
			for(AbstractGraphNode node : initialMDG.getNodeList()){
				String nodeName = KDMEntityFullName.getInstance().determineSpaceName(node.getEntity());
				if(source.getText().equals(nodeName))
					sourceInMDG = node;
				else if(destination.getText().equals(nodeName))
					destinationInMDG = node;
				if((sourceInMDG != null) && (destinationInMDG != null))
				{
					conn.setText("" + sourceInMDG.getSuccessorsList().get(destinationInMDG));
					break;
				}
			}
	    }
		
		specifyConnectionsCurves(curveDepth, connections);
	}
	
	/**
	 * Set the curve of each graph's connection.
	 * @param curveDepth
	 * @param connections
	 */
	private static void specifyConnectionsCurves(int curveDegree, 
			                                     java.util.List<GraphConnection> connections){
		//TODO Factoriser ce code commun a certaines vues 
		int connectionSize = connections.size();
		PolygonDecoration decoration = new PolygonDecoration();
        decoration.setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));


		//to avoid a concurrent modification exception:
		for(int curveIndex = 0; curveIndex < connectionSize; curveIndex++){
			GraphConnection conn = connections.get(curveIndex);
			conn.setCurveDepth(curveDegree);
			//decorate the connection
	        //decoration.setScale(10, 7);
	        //((PolylineConnection) conn.getConnectionFigure()).setTargetDecoration(decoration);

		}
	
	}
	
	/**
	 * http://www.vogella.com/tutorials/EclipseZest/article.html
	 * @return
	 */
	 private LayoutAlgorithm setLayout() {
		    LayoutAlgorithm layout;
		    // layout = new
		    // SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		    layout = new TreeLayoutAlgorithm();//new RadialLayoutAlgorithm();//
		    // layout = new
		    // GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		    // layout = new
		    // HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		    // layout = new
		    // RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
		    return layout;
	}
	 
	private void showMessage(String message) {
		MessageDialog.openInformation(
			initialMDGViewer.getControl().getShell(),
			titleView,
			message);
	}

	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		initialMDGViewer.getControl().setFocus();
	}
	

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
            Object firstElement = ((IStructuredSelection)selection).getFirstElement();
            IProject selectedProject;//project from which the contextual menu has been used
            if (firstElement instanceof IResource) {
            	selectedProject= ((IResource)firstElement).getProject();
            	//get the path
            	IPath projectPath = selectedProject.getLocation();

            	showMessage("The name of the selected project is : " + selectedProject.getName());
            }
            else if (firstElement instanceof IFile) {
                   //TODO complete eventually
            }
       }
	}
	
	public static GraphViewer getInitialMDGViewer() {
		return initialMDGViewer;
	}
	
	public static Shell getViewerShell() {
		return viewerShell;
	}
}