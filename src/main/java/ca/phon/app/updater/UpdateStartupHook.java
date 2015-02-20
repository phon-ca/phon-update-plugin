package ca.phon.app.updater;


import ca.phon.app.hooks.PhonStartupHook;
import ca.phon.plugin.IPluginExtensionFactory;
import ca.phon.plugin.IPluginExtensionPoint;
import ca.phon.plugin.PhonPlugin;
import ca.phon.plugin.PluginException;
import ca.phon.util.OSInfo;
import ca.phon.util.PrefHelper;

@PhonPlugin
public class UpdateStartupHook implements PhonStartupHook, IPluginExtensionPoint<PhonStartupHook> {

	@Override
	public void startup() throws PluginException {
		if(PrefHelper.getBoolean(UpdateChecker.CHECK_FOR_UPDATE_PROP, UpdateChecker.DEFAULT_CHECK_FOR_UPDATE)) {
			if(OSInfo.isMacOs())
				UpdateChecker.checkForUpdates(UpdateChecker.getUpdateURL(), true, false);
			else
				UpdateChecker.checkForUpdatesInBackground();
		}
	}

	@Override
	public Class<?> getExtensionType() {
		return PhonStartupHook.class;
	}

	@Override
	public IPluginExtensionFactory<PhonStartupHook> getFactory() {
		return factory;
	}
	
	private final IPluginExtensionFactory<PhonStartupHook> factory = new IPluginExtensionFactory<PhonStartupHook>() {
		
		@Override
		public PhonStartupHook createObject(Object... arg0) {
			return UpdateStartupHook.this;
		}
		
	};

}
