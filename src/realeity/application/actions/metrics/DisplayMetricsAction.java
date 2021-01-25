package realeity.application.actions.metrics;

import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.eclipse.gmt.modisco.omg.kdm.structure.StructureModel;

import realeity.application.actions.AbstractMicroAction;
import realeity.application.data.GeneratedData;
import realeity.technique.extractor.structuremodel.KDMModelsGeneration;
import realeity.technique.storage.kdm.KDMStorage;
import realeity.technique.util.RealeityUtils.MetricTypes;
import realeity.views.swt.MeasuresView;


/**
 * License GPL
 * january 2014
 * @author  Boaye Belle Alvine
 *
 */
public class DisplayMetricsAction extends AbstractMicroAction {
	private static double adjac = 0;//value of the adjacent metric
	private static double skip = 0;//value of the skip-calls metric
	private static double intra = 0;//value of the intra metric
	private static double back = 0;//value of the back-calls metric
	private static boolean  metricsDisplayed = false;
	private static String PANE_TITLE = "-- Metrics Values--";

	/**
	 * Class constructor.
	 * @param actionName
	 * @param tooltipText
	 * @param actionIconPath
	 */
	public DisplayMetricsAction(String actionName) {
		super(actionName);
	}
	
	/**
	 * Get the layering metrics
	 */
	public void retrieveMetrics(){
		try{
			StructureModel resultModel = GeneratedData.getInstance().getCurrentLayeringModel();
			if(resultModel != null){
				Map<String, String> metricsMap = KDMStorage.getInstance().retrieveMetrics(resultModel);
				adjac = GeneratedData.getInstance().getLayeringResult().getPartitionMetrics().getNumberOfAdjacencyCalls();//Integer.parseInt(metricsMap.get(MetricTypes.AdjacencyCallsMetric.toString()));
				skip = GeneratedData.getInstance().getLayeringResult().getPartitionMetrics().getNumberOfSkipCalls();//Integer.parseInt(metricsMap.get(MetricTypes.SkipCallsMetric.toString()));
				intra = GeneratedData.getInstance().getLayeringResult().getPartitionMetrics().getNumberOfIntraCalls();//Integer.parseInt(metricsMap.get(MetricTypes.IntraCallsMetric.toString()));
				back = GeneratedData.getInstance().getLayeringResult().getPartitionMetrics().getNumberOfBackCalls();//Integer.parseInt(metricsMap.get(MetricTypes.BackCallsMetric.toString()));
			}
			
		}
		catch(NumberFormatException exception){
			
		}
		
	}

	@Override
	public void run(){
		adjac = intra = skip = back = 0; 
		//metricsDisplayed = true;
		/*SwingWorker metricTask = new MetricsSwingWorker();
		metricTask.execute();*/
		
		//get the layering metrics
		retrieveMetrics();
		
		//update the view with the new metrics
		double sum = adjac + intra + skip + back;
		if(sum == 0)
			MeasuresView.updateView(0, 0, 0, 0);
		else{
			double adjacPercentage = (adjac * 100) / sum;
			double intraPercentage = (intra * 100) / sum;
			double skipPercentage = (skip * 100) / sum;
			double backPercentage = (back * 100) / sum;
			MeasuresView.updateView(adjacPercentage, intraPercentage, 
		                           skipPercentage, backPercentage);
		}
			
		runNextAction = true;
				
    } 
	
	class MetricsSwingWorker extends SwingWorker<Void, Void> {

    	/**
    	 * Class's constructor.
    	 */
    	public MetricsSwingWorker() {
    		
    	}
    	
    	@Override
    	public Void doInBackground() {	
    		boolean run = true;
    		while(run){
    			//get the layering metrics
    			retrieveMetrics();
    			
    			//update the view with the new metrics
    			double sum = adjac + intra + skip + back;
    			if(sum == 0)
    				MeasuresView.updateView(0, 0, 0, 0);
    			else
    				MeasuresView.updateView(adjac * 100/sum, intra * 100/sum, 
    						               skip*100/sum, back*100/sum);
    			
    			//to get out from the loop
    			 run = false;
    		}

            return null;
       }
	
	}

	@Override
	public void monitorExecution() {
		// TODO Auto-generated method stub
		
	}

}
