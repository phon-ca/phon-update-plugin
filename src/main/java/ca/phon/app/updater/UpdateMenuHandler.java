package ca.phon.app.updater;

import java.awt.Window;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import ca.phon.plugin.IPluginExtensionFactory;
import ca.phon.plugin.IPluginExtensionPoint;
import ca.phon.plugin.IPluginMenuFilter;
import ca.phon.plugin.PhonPlugin;

@PhonPlugin
public class UpdateMenuHandler implements IPluginMenuFilter, IPluginExtensionPoint<IPluginMenuFilter> {

	@Override
	public void filterWindowMenu(Window owner, JMenuBar menu) {
		JMenu helpMenu = null;
		for(int i = 0; i < menu.getMenuCount(); i++) {
			final JMenu m = menu.getMenu(i);
			if(m.getText().equals("Help")) {
				helpMenu = m;
				break;
			}
		}
		if(helpMenu == null) return;
		
		helpMenu.addSeparator();
		helpMenu.add(new PhonUpdateCommand());
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
