package realeity.views.swt;


import javax.swing.UIManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.layouts.LayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.gef4.zest.core.widgets.internal.GraphLabel;


import org.eclipse.gef4.zest.core.widgets.internal.PolylineArcConnection;
import org.eclipse.gef4.zest.core.widgets.internal.ZestRootLayer;

import realeity.application.actions.AbstractRecoveryAction;
import realeity.application.actions.AbstractMicroAction;
import realeity.application.actions.layering.RefinementAction;
import realeity.application.actions.layering.LoadLayeringAction;
import realeity.application.actions.layering.SaveRefinementAction;
import realeity.application.layout.zest.ZestGraphContainer;
import realeity.core.mdg.elements.LayeredPartition;
import realeity.views.swt.event.MouseDoubleClick;

import com.plugin.realeity.version1.Activator;

/**
 * 
 * @author Boaye Belle Alvine
 *
 */
@SuppressWarnings("restriction")
public class LayeringView extends ViewPart implements ISelectionListener, IRecoveryView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = Activator.PLUGIN_ID; //is equal to Activator.PLUGIN_ID;
	private static final String VIEW_ID = "com.plugin.realeity.version1.layering";
	private String titleView = "ReALEITY";
	
	private AbstractMicroAction saveRefinementAction;
	
	private AbstractMicroAction loadLayeringAction;


	private static final String SAVE_REFINE_LAYERING_ICON_ACTION_PATH = "icons/picture_save.png"; 
	private static final String SAVE_REFINE_LAYERING_TOOLTIP_TEXT = SaveRefinementAction.SAVE_REFINE_LAYERING_ACTION_NAME;
	
	private static final String LOAD_LAYERING_ICON_ACTION_PATH = "icons/load_images.png"; 
	private static final String LOAD_LAYERING_TOOLTIP_TEXT = LoadLayeringAction.LOAD_LAYERING_ACTION_NAME;

	private static LayeringGraphViewer layeringViewer;
	
	private static Shell viewerShell;
	
	private static int nodeStyle = ZestStyles.NODES_FISHEYE | ZestStyles.NODES_HIDE_TEXT;
	private Action doubleClickAction;
	
	private GraphNode selectedNode = null;
	private GraphNode previousSelectNode = null;
	
	
	
	//picture_save.png, layers.png and chart_bar.png comes from the website http://www.famfamfam.com/lab/icons/silk/


	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	class LayeringViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
			
		}
		public Object[] getElements(Object parent) {
			return new String[] { "Refine Layering"};//, "Metrics", "Optional" };
		}
	}
	class LayeringViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
				   getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 * The code in the try catch clause is derived from the code of Mathieu Tétreault.
	 */
	@SuppressWarnings("restriction")
	public LayeringView() {
		/*Bundle bundle = Platform.getBundle(ID);
		@SuppressWarnings("restriction")
		URL fullPathString = BundleUtility.find(bundle, menuIconPath); */
		//URL layeringIconURL =  BundleUtility.find(ID,LAYERING_ICONS_ACTION_PATH);// menuIconPath);
		/*
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
		Graph layeringGraph = new Graph(parent, SWT.NONE);
		
		//create viewer
		layeringViewer = new LayeringGraphViewer(layeringGraph);
		
		//add the contextual menu to the view
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(layeringViewer.getControl(), ID);
		makeActions();
		hookContextMenu();
		//hookDoubleClickAction();
		contributeToActionBars();
	}
	
	public static void initialize(Composite parent){
		layeringViewer.setControl(new Graph(parent, SWT.NONE));
	}

	
	/**
	 * Update the view with the data in the layered partition.
	 * @param partition
	 */
	public static void updateView(LayeredPartition partition){
		try{
			//put the view in front or open it so that it is not considered as null
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(VIEW_ID);
			
			Color nodeColor = new Color(viewerShell.getDisplay(), 138, 11, 33);

			//fill the view with the data of a graph
			ZestGraphContainer graphContainer = new ZestGraphContainer(LayeringView.getGraph().getDisplay()); 
			graphContainer.createContainer(LayeringView.getGraph(), 
					                       partition,
					                       nodeStyle, nodeColor);
			// set graph layout
			
			LayeringView.getGraph().setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);
			//displays the nodes and their connections on the graph
			LayeringView.getGraph().applyLayout();	
			// to detect the graph node that has been double clicked and apply a treatment on it.
			LayeringView.getGraph().addMouseListener(new MouseDoubleClick(LayeringView.getGraph()));
			
		}
		catch (PartInitException exception) {
			System.out.println("The layering view could not be put in front so as to be seen" +
					           " by the user.");
		}
		catch (org.eclipse.swt.SWTException e) {
			// TODO: exception occurring when the view is closed
		}
		catch (NullPointerException exception) {
			/* this exception occurring when the view is is hidden behind another view
			 or if the the ReALEITY perspective is not displayed on Eclipse.
			 Is avoided with PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(VIEW_ID);*/
			/*System.out.println("--- An error occurred. You should open the ReALEITY perspective" +
					           " and put the layering view on focus.");
			exception.printStackTrace();*/
		}
		
	}
	
	
	/**
	 * This code come from http://www.vogella.com/tutorials/EclipseZest/article.html
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

	 private void hookContextMenu() {
			MenuManager menuMgr = new MenuManager("#PopupMenu");
			menuMgr.setRemoveAllWhenShown(true);
			menuMgr.addMenuListener(new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					fillContextMenu(manager);
				}
			});
			Menu menu = menuMgr.createContextMenu(layeringViewer.getControl());
			layeringViewer.getControl().setMenu(menu);
			getSite().registerContextMenu(menuMgr, layeringViewer);
		}

		private void contributeToActionBars() {
			IActionBars bars = getViewSite().getActionBars();
			fillLocalPullDown(bars.getMenuManager());
			fillLocalToolBar(bars.getToolBarManager());
		}

		private void fillLocalPullDown(IMenuManager manager) {
			//manager.add(factExtractionAction);
			//manager.add(new Separator());
			manager.add(saveRefinementAction);
			manager.add(new Separator());
			manager.add(loadLayeringAction);
			
		}

		private void fillContextMenu(IMenuManager manager) {
			//rajoute des elements au menu contextuel!!!
			//manager.add(factExtractionAction);
			manager.add(saveRefinementAction);
			manager.add(loadLayeringAction);
			//manager.add(visualizationAction);
			// Other plug-ins can contribute there actions here
			manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			
		}
		
		private void fillLocalToolBar(IToolBarManager manager) {
			//rajoute des elements a la barre d'outils de la vue?
			//rajoute des elements a la barre d'outils de la vue?
			//manager.add(factExtractionAction);
			manager.add(saveRefinementAction);
			manager.add(loadLayeringAction);
			//manager.add(visualizationAction);
		}

		private void makeActions() {
			saveRefinementAction = SaveRefinementAction.getInstance(SaveRefinementAction.SAVE_REFINE_LAYERING_ACTION_NAME);
			saveRefinementAction.specifyImage(SAVE_REFINE_LAYERING_TOOLTIP_TEXT, 
					                          SAVE_REFINE_LAYERING_ICON_ACTION_PATH);
			loadLayeringAction = LoadLayeringAction.getInstance(LoadLayeringAction.LOAD_LAYERING_ACTION_NAME);
			loadLayeringAction.specifyImage(LOAD_LAYERING_TOOLTIP_TEXT, 
					                        LOAD_LAYERING_ICON_ACTION_PATH);

		}
		

		private void hookDoubleClickAction() {
			/*layeringViewer.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event) {
					doubleClickAction.run();
				}
			}); */
		}
		private void showMessage(String message) {

		}

		
		/**
		 * Passing the focus request to the viewer's control.
		 */
		public void setFocus() {
			//layeringViewer.getControl().setFocus();
			layeringViewer.getControl().setFocus();
		}
		
		
		

		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			//comment recuperer le projet a partir duquel le menu contextuel est utilisé
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

		
		public static Shell getViewerShell() {
			return viewerShell;
		}
		
		
		public static Graph getGraph() {
			return (Graph) layeringViewer.getControl();
		}
		
		public static void setGraph(Graph graphIn) {
			layeringViewer.setControl(graphIn);
		}
		

	}