package ca.phon.app.updater;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.phon.app.VersionInfo;
import ca.phon.util.PrefHelper;

import com.install4j.api.launcher.ApplicationLauncher;

/**
 * Check for updates using the install4j update application.
 *
 */
public class PhonUpdateChecker {

	private static final Logger LOGGER = Logger
			.getLogger(PhonUpdateChecker.class.getName());

	/** Update application id */
	private final static String APP_ID = "PHON_UPDATER";
	private final static String BG_APP_ID = "PHON_SILENT_UPDATER";

	public final static String UPDATE_URL = PhonUpdateChecker.class.getName() + ".updateURL";
	public final static String DEFAULT_UPDATE_URL = "https://phon-ca.github.io/phon/updates.xml";
	public final static String DEFAULT_BETA_UPDATE_URL = "https://phon-ca.github.io/phon/updates-beta.xml";

	public final static String CHECK_FOR_UPDATE_PROP = "ca.phon.application.updater.checkOnStartup";

	public final static Boolean DEFAULT_CHECK_FOR_UPDATE = Boolean.TRUE;

	public static String getUpdateURL() {
		String defaultURL = VersionInfo.getInstance().getVersion().matches("[.0-9]+b[0-9]+") ? 
				DEFAULT_BETA_UPDATE_URL : DEFAULT_UPDATE_URL;
		return PrefHelper.get(UPDATE_URL, defaultURL);
	}

	/**
	 * Check for updates
	 */
	public static void checkForUpdatesInBackground(boolean silent, String updateURL) {
		checkForUpdates(updateURL, silent, true);
	}

	public static void checkForUpdatesInBackground() {
		checkForUpdates(getUpdateURL(), true, true);
	}

	public static void checkForUpdates(boolean silent, String updateURL) {
		checkForUpdates(updateURL, silent, false);
	}

	public static void checkForUpdates() {
		checkForUpdates(getUpdateURL(), false, false);
	}

	/**
	 * Check for updates
	 *
	 * @param checkInBackground
	 */
	public static void checkForUpdates(String updateURL, boolean silent, boolean checkInBackground) {
		final String ID = (silent ? BG_APP_ID : APP_ID);
		LOGGER.info("Running updater....");
		try {
			if(!checkInBackground) {
				ApplicationLauncher.launchApplicationInProcess(ID, null, new ApplicationLauncher.Callback() {
		            public void exited(int exitValue) {
		            	LOGGER.info("Update application exited with value " + exitValue);
		            }

		            public void prepareShutdown() {
		            	LOGGER.info("Updater is shutting down application.");
		            }
		        }, ApplicationLauncher.WindowMode.FRAME, null);
			} else {
			    ApplicationLauncher.launchApplication(ID, null, false, new ApplicationLauncher.Callback() {
		            public void exited(int exitValue) {
		            	LOGGER.info("Update application exited with value " + exitValue);
		            }

		            public void prepareShutdown() {
		            	LOGGER.info("Updater is shutting down application.");
		            }
		        } );
			}
		} catch (IOException e) {
		    LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}

	}

}
