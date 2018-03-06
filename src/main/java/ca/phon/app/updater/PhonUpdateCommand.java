package ca.phon.app.updater;

import java.awt.event.ActionEvent;

import ca.phon.app.hooks.HookableAction;

public class PhonUpdateCommand extends HookableAction {

	private final static String TXT = "Check for updates...";

	private final static String DESC = "Check for application updates";
	
	public PhonUpdateCommand() {
		super();
		putValue(NAME, TXT);
		putValue(SHORT_DESCRIPTION, DESC);
	}
	
	@Override
	public void hookableActionPerformed(ActionEvent ae) {
		PhonUpdateChecker.checkForUpdates();
	}

}
