package ca.phon.app.updater;


import ca.phon.app.hooks.PhonStartupHook;
import ca.phon.plugin.IPluginExtensionFactory;
import ca.phon.plugin.IPluginExtensionPoint;
import ca.phon.plugin.PhonPlugin;
import ca.phon.plugin.PluginException;
import ca.phon.util.PrefHelper;
import ca.phon.worker.PhonWorker;

@PhonPlugin
public class UpdateStartupHook implements PhonStartupHook, IPluginExtensionPoint<PhonStartupHook> {
	
	@Override
	public void startup() throws PluginException {
		if(PrefHelper.getBoolean(PhonUpdateChecker.CHECK_FOR_UPDATE_PROP, PhonUpdateChecker.DEFAULT_CHECK_FOR_UPDATE)) {
			final PhonWorker worker = PhonWorker.createWorker();
			worker.setName("Automatic Updater");
			worker.setFinishWhenQueueEmpty(true);
			worker.invokeLater(() -> {
				PhonUpdateChecker.checkForUpdates(PhonUpdateChecker.getUpdateURL(), true, true);
			});
			worker.start();
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
