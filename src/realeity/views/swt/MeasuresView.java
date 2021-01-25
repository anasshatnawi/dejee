package realeity.views.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.IBarSeries;
import org.swtchart.ILegend;
import org.swtchart.Range;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.ext.InteractiveChart;

/**
 * See http://www.swtchart.org/index.html for more details.
 * License GPL
 * @date 2014-06-11 
 * @author Boaye Belle Alvine
 **/

public class MeasuresView extends ViewPart implements IRecoveryView {

	private static final String VIEW_ID = "com.plugin.realeity.version1.metrics";
    private static final String[] categorySeries = { "ADJAC", "INTRA", "SKIP", "BACK"};
    private static final int MAX_RANGE = 100;
    private static final int MIN_RANGE = 0;
    
    /**
     * Contains the values of the 4 metrics adjac, intra, skip and back
     */
    private static  double[] yAxisBarSeries  ;
    private static Shell viewerShell;
    private static IBarSeries barSeries1;

    /** the chart */
    private static Chart metricsChart;

    /*
     * @see WorkbenchPart#createPartControl(Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
    	
    	
        parent.setLayout(new FillLayout());

        // create an interactive chart
        metricsChart = new InteractiveChart(parent, SWT.NONE);

        // set title
        metricsChart.getTitle().setText("ReALEITY Measures Chart");

        // set category series
        metricsChart.getAxisSet().getXAxis(0).enableCategory(true);
        metricsChart.getAxisSet().getXAxis(0).setCategorySeries(categorySeries);

        // create bar series 1
        barSeries1 = (IBarSeries) metricsChart.getSeriesSet().createSeries(
                      SeriesType.BAR, "bar series 1");
        
        //set to zero the metrics to display on the chart
        initializeChartView();

        // adjust the axis range
        metricsChart.getAxisSet().adjustRange();
        
        //set the ticks of the vertical axis
        IAxis yAxis = metricsChart.getAxisSet().getYAxis(0);
        yAxis.setRange(new Range(MIN_RANGE, MAX_RANGE));
        
        //sets the format of series label to show with fixed point numbers
        barSeries1.getLabel().setFormat("##.00");// two digits after the comma
        
        //set the legend on top of the chart
        ILegend metricsLegend = metricsChart.getLegend();
        metricsLegend.setPosition(SWT.TOP);

    }
    
    /**
     * set to zero the metrics to display on the chart.
     * @param barSeries1
     */
    public static void initializeChartView(){
    	
    	try{
    		yAxisBarSeries = new double [] {0, 0, 0, 0};
    		barSeries1.getLabel().setVisible(false);
    		barSeries1.setYSeries(yAxisBarSeries);
            
            //update the chart with  the new metrics
            metricsChart.redraw();
            metricsChart.update();
            
    	}
    	catch (Exception e) {
    		
		}
    }
    
    /**
     * This method collect the 4 types of metrics and use them to
     * set the series.
     * @param barSeries1
     */
    public static void updateView(double adjac, double intra, double skip, 
    		                      double back){
    	try{
    		//put the view in front or open it so that it is not considered as null
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(VIEW_ID);
    		
    		if((adjac == 0) && (intra == 0) && (skip == 0) && (back == 0)){
        		yAxisBarSeries = new double [] {0, 0, 0, 0};
          	    barSeries1.getLabel().setVisible(false);
            }
            else{
                yAxisBarSeries = new double [] {adjac, intra, skip, back};
                barSeries1.getLabel().setVisible(true);
            }
            barSeries1.setYSeries(yAxisBarSeries);
            
            //update the chart with  the new metrics
            metricsChart.redraw();
            metricsChart.update();
            
    	}
    	catch (PartInitException exception) {
			System.out.println("The matrics view could not be put in front so as to be seen" +
					           " by the user.");
		}
    	catch (Exception e) {
    		/* this exception occurs when the view is is hidden behind another view
			 or if the the ReALEITY perspective is not displayed on Eclipse.
			 Is avoided with PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(VIEW_ID);*/
    		
    		/*System.out.println("--- An error occurred. You should open the ReALEITY perspective" +
    				           " and put the metrics view in front.");*/
		}
    }

    /*
     * @see WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        metricsChart.getPlotArea().setFocus();
    }
    
    
    /*
     * @see WorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
    	try{
    		super.dispose();
            metricsChart.dispose();
    	}
    	catch(org.eclipse.swt.SWTException exception){
    		//System.out.println("An error has occurred: the widget has been disposed");
    	}
    }
    
    public static Shell getViewerShell() {
		return viewerShell;
	}
}

