package ca.phon.application.updater;

import java.io.IOException;
import java.util.logging.Logger;

import com.install4j.api.launcher.ApplicationLauncher;
import com.install4j.api.update.UpdateScheduleRegistry;

/**
 * Check for updates using the install4j update application.
 *
 */
public class UpdateChecker {
	
	/** Update application id */
	private final static String APP_ID = "365";
	private final static String BG_APP_ID = "312";
//	private final static String SCHEDULER_ID = "408";
	
	/**
	 * Check for updates
	 */
	public static void checkForUpdates(String updateURL) {
		checkForUpdates(updateURL, true);
	}
	
	/**
	 * Check for updates
	 * 
	 * @param checkInBackground
	 */
	public static void checkForUpdates(String updateURL, boolean checkInBackground) {
		try {
			if(checkInBackground) {
//				Logger.getLogger(UpdateChecker.class.toString()).info("Checking update schedule...");
//				if(UpdateScheduleRegistry.checkAndReset()) {
					Logger.getLogger(UpdateChecker.class.toString()).info("Running updater....");
					ApplicationLauncher.launchApplicationInProcess("312", null, new ApplicationLauncher.Callback() {
			            public void exited(int exitValue) {
			            	Logger.getLogger(UpdateChecker.class.toString()).info("Update application exited with value " + exitValue);
			            }
			            
			            public void prepareShutdown() {
			            	Logger.getLogger(UpdateChecker.class.toString()).info("Updater is shutting down application.");
			            }
			        }, ApplicationLauncher.WindowMode.FRAME, null);
//				} else {
//					Logger.getLogger(UpdateChecker.class.toString()).info("Update is not scheduled.");
//				}
			} else {
			    ApplicationLauncher.launchApplication(APP_ID, null, false, new ApplicationLauncher.Callback() {
			            public void exited(int exitValue) {
			            }
			            
			            public void prepareShutdown() {
			            }
			        }
			    );
			}
		} catch (IOException e) {
		    e.printStackTrace();
		}

	}
	
//	/**
//	 * Run the automatic update scheduler
//	 */
//	public static void runUpdateScheduler() {
//		try {
//			 ApplicationLauncher.launchApplication(SCHEDULER_ID, null, false, new ApplicationLauncher.Callback() {
//		            public void exited(int exitValue) {
//		            }
//		            
//		            public void prepareShutdown() {
//		            }
//		        }
//		    );
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	

}
