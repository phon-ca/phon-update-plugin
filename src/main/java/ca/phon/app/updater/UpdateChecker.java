package ca.phon.app.updater;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.phon.util.PrefHelper;

import com.install4j.api.launcher.ApplicationLauncher;
import com.install4j.api.update.UpdateScheduleRegistry;

/**
 * Check for updates using the install4j update application.
 *
 */
public class UpdateChecker {
	
	private static final Logger LOGGER = Logger
			.getLogger(UpdateChecker.class.getName());
	
	/** Update application id */
	private final static String APP_ID = "PHON_UPDATER";
	private final static String BG_APP_ID = "PHON_SILENT_UPDATER";
	
	public final static String UPDATE_URL = UpdateChecker.class.getName() + ".updateURL";
	public final static String DEFAULT_UPDATE_URL = "https://www.phon.ca/downloads/phon/updates.xml";
	
	public static String getUpdateURL() {
		return PrefHelper.get(UPDATE_URL, DEFAULT_UPDATE_URL);
	}
	
	/**
	 * Check for updates
	 */
	public static void checkForUpdatesInBackground(String updateURL) {
		checkForUpdates(updateURL, true);
	}
	
	public static void checkForUpdatesInBackground() {
		checkForUpdates(getUpdateURL(), true);
	}
	
	public static void checkForUpdates(String updateURL) {
		checkForUpdates(updateURL, false);
	}
	
	public static void checkForUpdates() {
		checkForUpdates(getUpdateURL(), false);
	}
	
	/**
	 * Check for updates
	 * 
	 * @param checkInBackground
	 */
	public static void checkForUpdates(String updateURL, boolean checkInBackground) {
		try {
			if(checkInBackground) {
				Logger.getLogger(UpdateChecker.class.toString()).info("Running updater....");
				ApplicationLauncher.launchApplicationInProcess(BG_APP_ID, null, new ApplicationLauncher.Callback() {
		            public void exited(int exitValue) {
		            	LOGGER.info("Update application exited with value " + exitValue);
		            }
		            
		            public void prepareShutdown() {
		            	LOGGER.info("Updater is shutting down application.");
		            }
		        }, ApplicationLauncher.WindowMode.FRAME, null);
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
		    LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}

	}

}
