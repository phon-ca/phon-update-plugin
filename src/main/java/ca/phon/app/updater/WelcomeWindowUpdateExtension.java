package ca.phon.app.updater;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import org.jdesktop.swingx.JXBusyLabel;

import com.install4j.api.Util;
import com.install4j.api.context.UserCanceledException;
import com.install4j.api.launcher.*;
import com.install4j.api.update.*;

import ca.phon.app.log.BufferPanel;
import ca.phon.app.log.BufferWindow;
import ca.phon.app.log.LogUtil;
import ca.phon.app.welcome.*;
import ca.phon.app.welcome.WelcomeWindow.BtnBgPainter;
import ca.phon.extensions.*;
import ca.phon.plugin.*;
import ca.phon.ui.CommonModuleFrame;
import ca.phon.ui.MultiActionButton;
import ca.phon.ui.action.PhonActionEvent;
import ca.phon.ui.action.PhonUIAction;
import ca.phon.ui.fonts.FontPreferences;
import ca.phon.util.icons.*;

@Extension(WelcomeWindow.class)
public class WelcomeWindowUpdateExtension implements ExtensionProvider {

	private WelcomeWindow welcomeWindow;
	
	private MultiActionButton updateButton;
	
	private JXBusyLabel busyLabel;
	
	public WelcomeWindowUpdateExtension() {
	}

	@Override
	public void installExtension(IExtendable obj) {
		welcomeWindow = WelcomeWindow.class.cast(obj);
		
		LogUtil.info("Checking for updates");
//		if(PrefHelper.getBoolean(PhonUpdateChecker.CHECK_FOR_UPDATE_PROP, PhonUpdateChecker.DEFAULT_CHECK_FOR_UPDATE)) {
			checkForUpdateWithInstall4jApi();
//		}
	}

	private void checkForUpdateWithInstall4jApi() {
		
        if (isInstallationDirWritable()) {
            // Here we check for updates in the background with the API.
            new SwingWorker<UpdateDescriptorEntry, Object>() {
                @Override
                protected UpdateDescriptorEntry doInBackground() throws Exception {
                    // The compiler variable sys.updatesUrl holds the URL where the updates.xml file is hosted.
                    // That URL is defined on the "Installer->Auto Update Options" step.
                    // The same compiler variable is used by the "Check for update" actions that are contained in the update
                    // downloaders.
                    String updateUrl = Variables.getCompilerVariable("sys.updatesUrl");
                    UpdateDescriptor updateDescriptor = UpdateChecker.getUpdateDescriptor(updateUrl, ApplicationDisplayMode.GUI);
                    // If getPossibleUpdateEntry returns a non-null value, the version number in the updates.xml file
                    // is greater than the version number of the local installation.
                    return updateDescriptor.getPossibleUpdateEntry();
                }

                @Override
                protected void done() {
                    try {
                        UpdateDescriptorEntry updateDescriptorEntry = get();
                        final PhonUIAction executeUpdateAct = new PhonUIAction(WelcomeWindowUpdateExtension.this, "executeUpdate");
                        // only installers and single bundle archives on macOS are supported for background updates
                        if (updateDescriptorEntry != null && (!updateDescriptorEntry.isArchive() || updateDescriptorEntry.isSingleBundle())) {
                            if (!updateDescriptorEntry.isDownloaded()) {
                            	executeUpdateAct.putValue(PhonUIAction.NAME, "Downloading update");
                            	downloadInBackground();
                            } else if (UpdateChecker.isUpdateScheduled()) {
                            	executeUpdateAct.putValue(PhonUIAction.NAME, "Click to update");
                            }
                            addUpdateNotice(updateDescriptorEntry, executeUpdateAct);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        Throwable cause = e.getCause();
                        // UserCanceledException means that the user has cancelled the proxy dialog
                        if (!(cause instanceof UserCanceledException)) {
                            e.printStackTrace();
                        }
                    }
                }
            }.execute();
        } else {
        	LogUtil.info("Canceling update check");
        }
    }

    private boolean isInstallationDirWritable() {
        try {
            Path installationDirectory = Paths.get(String.valueOf(Variables.getInstallerVariable("sys.installationDir")));
            //only check writable on Unix because the installer requests privileges on Windows and macOS
            return !Files.getFileStore(installationDirectory).isReadOnly() && (Util.isWindows() || Util.isMacOS() || Files.isWritable(installationDirectory));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private void addUpdateNotice(final UpdateDescriptorEntry updateDescriptorEntry, Action action) {
    	if(updateButton == null) {
    		final ImageIcon phonIcn = IconManager.getInstance().getIcon("apps/database-phon", IconSize.MEDIUM);
    		
    		updateButton = new MultiActionButton();
    		updateButton.getTopLabel().setIcon(phonIcn);
    		updateButton.getTopLabel().setFont(FontPreferences.getTitleFont());
    		updateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    		updateButton.getTopLabel().setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    		updateButton.setBackground(new Color(255, 255, 200));
    		
    		busyLabel = new JXBusyLabel(new Dimension(16, 16));
    		busyLabel.setBusy(true);
    		busyLabel.setVisible(false);
    		//updateButton.setOpaque(false);
    		
    		final JPanel btmPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
    		btmPanel.add(busyLabel);
    		updateButton.remove(updateButton.getBottomLabel());
    		btmPanel.add(updateButton.getBottomLabel());
    		btmPanel.setOpaque(false);
    		updateButton.add(btmPanel, BorderLayout.SOUTH);
    		
    		BtnBgPainter bgPainter = new BtnBgPainter();
    		updateButton.setBackgroundPainter(bgPainter);
    		updateButton.addMouseListener(bgPainter);
    		
    		welcomeWindow.getActionList().add(updateButton);
    		welcomeWindow.getActionList().revalidate();    	
    	}
    	updateButton.setDefaultAction(action);
    	
    	final PhonUIAction showDetailsAct = new PhonUIAction(this, "showUpdateDetails", updateDescriptorEntry);
    	showDetailsAct.putValue(PhonUIAction.SMALL_ICON, IconManager.getInstance().getIcon("categories/info-black", IconSize.SMALL));
    	showDetailsAct.putValue(PhonUIAction.LARGE_ICON_KEY, IconManager.getInstance().getIcon("categories/info-black", IconSize.MEDIUM));
    	showDetailsAct.putValue(PhonUIAction.SHORT_DESCRIPTION, "Show changelog");
    	showDetailsAct.putValue(PhonUIAction.NAME, "More information");
    	updateButton.addAction(showDetailsAct);
    	
    	updateButton.setTopLabelText(WorkspaceTextStyler.toHeaderText("Version " + updateDescriptorEntry.getNewVersion() + " available"));
    	updateButton.setBottomLabelText(WorkspaceTextStyler.toDescText(action.getValue(Action.NAME).toString()));
    }
    
    public void doNothing() {}
    
    public void downloadInBackground() {
    	// Here the background update downloader is launched in the background
        // See checkForUpdate(), where the interactive updater is launched for comments on launching an update downloader.
        new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
            	SwingUtilities.invokeLater( () -> { busyLabel.setVisible(true); busyLabel.setBusy(true); });
                // Note the third argument which makes the call to the background updater blocking.
                ApplicationLauncher.launchApplication("2363", null, true, null);
                // At this point the update downloader has returned and we can check if the "Schedule update installation"
                // action has registered an update installer for execution
                // We now switch to the EDT in done() for terminating the application
                return null;
            }

            @Override
            protected void done() {
            	busyLabel.setVisible(false);
            	busyLabel.setBusy(false);
                try {
                    get(); // rethrow exceptions that occurred in doInBackground() wrapped in an ExecutionException
                    if (UpdateChecker.isUpdateScheduled()) {
                    	updateButton.setBottomLabelText(WorkspaceTextStyler.toDescText("Click to update"));
                    } else {
                        updateButton.setBottomLabelText(WorkspaceTextStyler.toDescText("Update could not be downloaded"));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    Toolkit.getDefaultToolkit().beep();
                    updateButton.setBottomLabelText(e.getLocalizedMessage());
                    LogUtil.severe(e);
                }
            }

        }.execute();
    }
    
    public void executeUpdate() {
    	if(UpdateChecker.isUpdateScheduled()) {
	        // The arguments that are passed to the installer switch the default GUI mode to an unattended
	        // mode with a progress bar. "-q" activates unattended mode, and "-splash Updating hello world ..."
	        // shows a progress bar with the specified title.
	        UpdateChecker.executeScheduledUpdate(Arrays.asList("-q", "-splash", "Updating Phon ..."), true, 
	        		() -> { try {
						PluginEntryPointRunner.executePlugin("Exit");
					} catch (PluginException e) {
						LogUtil.severe(e);
					} });
    	}
    }
    
    public void showUpdateDetails(PhonActionEvent pae) {
    	final UpdateDescriptorEntry entry = (UpdateDescriptorEntry)pae.getData();
    	final BufferWindow buffers = BufferWindow.getInstance();
    	final BufferPanel bufferPanel = buffers.createBuffer("Changelog " + entry.getNewVersion(), true);
    	
    	try {
			final PrintWriter out = new PrintWriter
					(new OutputStreamWriter(bufferPanel.getLogBuffer().getStdOutStream(), "UTF-8"));
			out.println(entry.getComment());
			out.flush();
			out.close();
			
			bufferPanel.getLogBuffer().setCaretPosition(0);
			
			buffers.pack();
			
			buffers.setSize(CommonModuleFrame.getCurrentFrame().getWidth()-100, CommonModuleFrame.getCurrentFrame().getHeight()-50);
			
			buffers.setLocationRelativeTo(CommonModuleFrame.getCurrentFrame());
			buffers.setVisible(true);
		} catch (UnsupportedEncodingException e) {
			LogUtil.severe(e.getLocalizedMessage(), e);
		}
    	
    }
}
