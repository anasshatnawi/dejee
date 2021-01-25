package realeity.views.swt;

import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

import realeity.application.actions.AbstractMacroAction;
import realeity.application.actions.AbstractMicroAction;
import realeity.application.data.GeneratedData;

/**
 * 
 * License GPL.
 * @author Boaye Belle Alvine
 * 2014-12-03
 */
public class RecoveryBusyMonitor {
	
	private AbstractMacroAction actionToMonitor;
	private static final int  MAX_PROGRESS = 100;
	private int  initialProgress = 3; //default initial progress value
	private int  timeToSleep = 100;
	private long startTime;
	
	/**
	 * Class's constructor.
	 * @param actionToMonitor
	 */
	public RecoveryBusyMonitor(AbstractMacroAction actionToMonitor) {
		this.actionToMonitor = actionToMonitor;
	}
	
	/**
	 * Class's constructor.
	 * @param initialProgress
	 * @param timeToSleep
	 */
	public RecoveryBusyMonitor(int  initialProgress, int  timeToSleep) {
		this.initialProgress = initialProgress;
		this.timeToSleep = timeToSleep;
	}
	
	
	/**
	 * Is used to monitor a macro-action comprising many micro-actions.
	 * @return
	 */
	public boolean showMonitor(){
		//TODO: rajouter la determination de la duree de l'execution
	   boolean monitorDone = true;
	   startTime = System.currentTimeMillis();

		try {
			PlatformUI.getWorkbench().getProgressService().
			busyCursorWhile(new IRunnableWithProgress()  {
			public void run(IProgressMonitor monitor) {
				monitor.beginTask(actionToMonitor.getProgressBarName() + " -- " 
	                              + GeneratedData.getInstance().getSelectedProject().getName(), 
	                              MAX_PROGRESS);
				
				// report that INITIAL_PROGRESS additional units are done
			    monitor.worked(initialProgress);
			     
				 // sleep a TIME_TO_SLEEP ms
			    sleepSomeSeconds(timeToSleep);
			    
			   // Execute the actions
			    int indexAction = 1;
			    int  actionProgress = 0;
			   for(AbstractMicroAction microAction : actionToMonitor.getActionsToMonitor()){
				   monitor.subTask(microAction.getText());
				   microAction.run();
				   actionProgress = (indexAction * 100)/actionToMonitor.getActionsToMonitor().size();
				   // report that actionProgress additional units are done
				   monitor.worked(actionProgress - initialProgress);
				   indexAction++;
			   }
			   
			   if(actionProgress < MAX_PROGRESS){// a cause des arrondis dans le calcul de actionProgress
				   int remainingUnits = MAX_PROGRESS - actionProgress;
				   monitor.worked(remainingUnits);
			   }
			  
			  // sleep another TIME_TO_SLEEP ms and makes a beep sound
			  sleepSomeSeconds(timeToSleep);
			 makeABeepSound();

			}
			});
		} 
		catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			System.out.println("--- An error occurred when performing the task.");
			monitorDone = false;
		}
		
	    long timeElapsed = (System.currentTimeMillis() - startTime)/1000;
        String durationText = convertInHours(timeElapsed);
        System.out.println("The current phase lasted: " + durationText);
		
		return monitorDone;
	}
	
	/**
	 * 
	 * @param timeElapsed
	 * @return
	 */
	private String convertInHours(long timeElapsed){
		  long temp = timeElapsed;
		  String durationInHours = "";
		  double hours = temp/3600;
		  temp = temp % 3600;
		  double minutes = temp/60;
		  double seconds = temp % 60;

		  durationInHours = hours + "h " + minutes + "min " + seconds + "s    "; 
	
          return durationInHours;

	  }
	  
	/**
	 * 
	 */
	private void makeABeepSound(){
		Toolkit.getDefaultToolkit().beep();
		  /**for(int i=0; i<260;i++){
			  System.out.print("\007"); 
			  System.out.flush(); 
		 }**/
		  
	}

	
	/**
	 * Is used to monitor a single micro-action
	 * @param microAction
	 * @param jobName
	 */
	public boolean showMonitorForAction(final AbstractMicroAction microAction, final String jobName){
		boolean monitorDone = true;
		try {
			PlatformUI.getWorkbench().getProgressService().
			busyCursorWhile(new IRunnableWithProgress()  {
			public void run(IProgressMonitor monitor) {
				monitor.beginTask(jobName, MAX_PROGRESS);
				// report that 5 additional units are done
			    monitor.worked(initialProgress);
				// sleep a 700 ms
			    sleepSomeSeconds(timeToSleep);
			    // Do the work.
				microAction.monitorExecution();
				// report that 95 additional units are done
			    monitor.worked(MAX_PROGRESS - initialProgress);
			    
			    // sleep another TIME_TO_SLEEP ms and makes a beep sound
				sleepSomeSeconds(timeToSleep);
				makeABeepSound();
			}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			System.out.println("--- An error occurred when performing the task.");
			monitorDone = false;

		}
		return monitorDone;
	}
	
	/**
	 * 
	 * @param seconds
	 */
	protected void sleepSomeSeconds(int seconds){
		//TODO: delete this method in MicroAction
		try {
			TimeUnit.MILLISECONDS.sleep(seconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

}
