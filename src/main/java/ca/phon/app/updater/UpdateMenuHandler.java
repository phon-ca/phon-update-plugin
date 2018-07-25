package ca.phon.app.updater;

import java.awt.Window;
import java.util.logging.Level;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import ca.phon.app.log.LogUtil;
import ca.phon.plugin.IPluginExtensionFactory;
import ca.phon.plugin.IPluginExtensionPoint;
import ca.phon.plugin.IPluginMenuFilter;
import ca.phon.plugin.PhonPlugin;
import ca.phon.ui.menu.MenuBuilder;

@PhonPlugin
public class UpdateMenuHandler implements IPluginMenuFilter, IPluginExtensionPoint<IPluginMenuFilter> {

	@Override
	public void filterWindowMenu(Window owner, JMenuBar menu) {
		final MenuBuilder builder = new MenuBuilder(menu);
		JMenu helpMenu = builder.getMenu("./Help");
		if(helpMenu == null) {
			helpMenu = builder.addMenu(".", "Help");
		}
		
		builder.addSeparator("./Help@^", "update");
		builder.addItem("./Help@^", new PhonUpdateCommand());
	}

	@Override
	public Class<?> getExtensionType() {
		return IPluginMenuFilter.class;
	}

	@Override
	public IPluginExtensionFactory<IPluginMenuFilter> getFactory() {
		return factory;
	}

	private final IPluginExtensionFactory<IPluginMenuFilter> factory = new IPluginExtensionFactory<IPluginMenuFilter>() {
		
		@Override
		public IPluginMenuFilter createObject(Object... arg0) {
			return UpdateMenuHandler.this;
		}
		
	};
}
