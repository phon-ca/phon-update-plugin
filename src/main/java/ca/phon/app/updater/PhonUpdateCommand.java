package ca.phon.app.updater;

import java.awt.event.ActionEvent;

import ca.phon.app.hooks.HookableAction;
import ca.phon.util.icons.IconManager;
import ca.phon.util.icons.IconSize;

public class PhonUpdateCommand extends HookableAction {

	private final static String TXT = "Check for updates...";

	private final static String DESC = "Check for application updates";
	
	private final static String ICON = "actions/update";
	
	public PhonUpdateCommand() {
		super();
		putValue(NAME, TXT);
		putValue(SHORT_DESCRIPTION, DESC);
		putValue(SMALL_ICON, IconManager.getInstance().getIcon(ICON, IconSize.SMALL));
	}
	
	@Override
	public void hookableActionPerformed(ActionEvent ae) {
		PhonUpdateChecker.checkForUpdates();
	}

}
