package ca.phon.app.updater;

import ca.phon.app.hooks.PhonStartupHook;
import ca.phon.plugin.PhonPlugin;
import ca.phon.plugin.PluginException;

@PhonPlugin
public class UpdateStartupHook implements PhonStartupHook {

	@Override
	public void startup() throws PluginException {
		UpdateChecker.checkForUpdatesInBackground();
	}

}
