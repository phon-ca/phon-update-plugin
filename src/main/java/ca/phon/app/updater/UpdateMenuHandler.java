package ca.phon.app.updater;

import java.awt.Window;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import ca.phon.plugin.IPluginMenuFilter;
import ca.phon.plugin.PhonPlugin;

@PhonPlugin
public class UpdateMenuHandler implements IPluginMenuFilter {

	@Override
	public void filterWindowMenu(Window owner, JMenuBar menu) {
		final JMenu helpMenu = menu.getMenu(menu.getMenuCount()-1);
		helpMenu.addSeparator();
		helpMenu.add(new PhonUpdateCommand());
	}

}
