package net.colloquia.views.browser;

import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.io.*;
import net.colloquia.menu.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;


/**
 * The Browser for a Person, Resource, Activity, or Assignment
 */
public class ComponentBrowserPanel
extends BrowserPanel
{
    ColloquiaComponent tc;

    protected GotoHomeAction action_GotoHome;
    protected GotoLocalFileAction action_GotoLocalFile;
    protected AddResourceAction action_AddResource;
    protected SetURLAction action_SetURL;
    protected SuckPageAction action_SuckPage;

    public ComponentBrowserPanel() {
        toolBar.add(action_GotoHome = new GotoHomeAction(), 0);
        toolBar.add(action_GotoLocalFile = new GotoLocalFileAction(), 1);
        toolBar.add(action_AddResource = new AddResourceAction(), 2);
        toolBar.add(action_SetURL = new SetURLAction(), 3);
        toolBar.add(action_SuckPage = new SuckPageAction(), 4);
        toolBar.add(new JToolBar.Separator(), 5);
	}

    /**
    * Load the URL or Local File
    */
    public void loadComponent(ColloquiaComponent tc) {
        this.tc = tc;

        boolean hasLocalFile = tc.getLocalFile().length() != 0;

        // Either display the page now
        if(UserPrefs.getUserPrefs().getBooleanProperty(UserPrefs.BROWSER_NOW) == false) clearContent();
        else {
            // If a local file
            if(hasLocalFile) gotoLocalFile();
            // Else, get net URL
            else gotoHome();
        }

        // If a read-only entity, don't let the user set their own URL
        action_SetURL.setEnabled(tc.isMine());
    }

    /**
    * Suck the page to a local file
    */
    protected class SuckPageAction extends MenuAction {
        public SuckPageAction() {
            super(LanguageManager.getString("3_3"), ColloquiaConstants.iconSave);
            setStatusBar(statusBar);
            setButtonText(LanguageManager.getString("BUT3"));
        }

        public void actionPerformed(ActionEvent e) {
            suckPage();
        }
    }

    protected void suckPage() {
        // Get tc URL
        String loc = tc.getURL().trim();
        // Nothing there so get location in browser
        if(loc.length() == 0) {
            loc = browser.getCurrentLocation().trim();
        }
        if(loc.length() != 0) {
            URL url = URLUtils.normalizeAddress(loc);
        	if(url != null) new FlatLiner(url, tc);
        	//new FlatLiner2(browser, tc);
        }
    }

    /**
    * Over-ride to set local file name and unpack Zip file
    * @return File if successful or null if not
    */
    protected File downloadFile(URL url) {
        // Store local copy of tc in case user selects another one in mean time
        ColloquiaComponent tcomp = this.tc;

        // Get the File - a null value means failure
        File file = super.downloadFile(url);

        // Now do some further work
        if(file != null && tcomp != null) {
            MainFrame.getInstance().setCursor(ColloquiaConstants.waitCursor);
            String fileName = file.getPath().toLowerCase();

            // If it's a zip file, unpack it and set file to first file extracted
            if(fileName.endsWith(".zip") && UserPrefs.getUserPrefs().getBooleanProperty(UserPrefs.UNPACK_ZIPS)) {
                try {
                    File[] files = Utils.unpackZip(file, DataFiler.addFileSepChar(file.getParent()));
                    if(files != null && files.length > 0) {
                        // Delete Zip
                        file.delete();
                        // Set File to first File
                        file = files[0];
                    }
                }
                catch(ColloquiaFileException ex) {
                    System.out.println("Could not extract zip");
                }
            }

        	// Set Local URL to file
            tcomp.setLocalFile(file.getPath(), false);
            // Update View - PENDING this should be a Listener Model
            ViewPanel.getInstance().repaintCurrentView();
            MainFrame.getInstance().setCursor(ColloquiaConstants.defaultCursor);
        }

        return file;
    }


    /**
    * Goto the LocalFile URL in the datasheet
    */
    protected class GotoLocalFileAction extends MenuAction {
        public GotoLocalFileAction() {
            super(LanguageManager.getString("3_4"), ColloquiaConstants.iconViewLocalFile);
            setStatusBar(statusBar);
            setButtonText(LanguageManager.getString("BUT29"));
        }

        public void actionPerformed(ActionEvent e) {
            gotoLocalFile();
        }
    }

    /**
    * For the URL to display properly in the ClueBrowser it must start
    * with file:/, otherwise it will be launched in an application
    */
    protected void gotoLocalFile() {
        String localUrl = "";

        String urlString = tc.getLocalFile();
        if(urlString.length() == 0) return;

        // Make lower case
        String tmp = urlString.toLowerCase();

        if(tmp.startsWith("file:") || tmp.startsWith("jar:")) {
            setCurrentLocation(urlString);
            return;
        }

        // Else try to launch it
        boolean appLaunched = AppLauncher.run(urlString);
        if(appLaunched) return;

        // Else try a last resort
        setCurrentLocation("file:" + urlString);
    }

    /**
    * Goto the home URL in the datasheet
    */
    protected class GotoHomeAction extends MenuAction {
        public GotoHomeAction() {
            super(LanguageManager.getString("3_5"), ColloquiaConstants.iconHome);
            setStatusBar(statusBar);
            setButtonText(LanguageManager.getString("BUT28"));
        }

        public void actionPerformed(ActionEvent e) {
            gotoHome();
        }
    }

    protected void gotoHome() {
        setCurrentLocation(tc.getURL());
    }

    /**
    Add the currently selected Page as a Resource to the Resource Folder
    */
    protected class AddResourceAction extends MenuAction {
        public AddResourceAction() {
            super(LanguageManager.getString("3_6"), ColloquiaConstants.iconCreateResource);
            setStatusBar(statusBar);
            setButtonText(LanguageManager.getString("BUT26"));
        }

        public void actionPerformed(ActionEvent e) {
            addResource();
        }
    }

    protected void addResource() {
        ColloquiaContainer parentGroup;
        ColloquiaComponent tc;
        ColloquiaTree tree = ColloquiaTree.getInstance();

        // Get current url
        String text = txtField.getText();
        if((text == null) || (text.length() == 0)) return;

        // Get group to plop it in
        tc = tree.getSelectedComponent();
        parentGroup = tree.getSelectedParent();
        if(tc instanceof ResourceGroup || tc instanceof Activity) parentGroup = (ColloquiaContainer)tc;
        else if(!(parentGroup instanceof ResourceGroup) && !(parentGroup instanceof Activity)) parentGroup = DataModel.getResourceGroup();

        ColloquiaComponent newResource = ComponentTransferManager.addNewComponent(ColloquiaComponent.RESOURCE, parentGroup);
        if(newResource != null) newResource.setURL(text, true);
    }

    protected class SetURLAction extends MenuAction {
        public SetURLAction() {
            super(LanguageManager.getString("3_7"), ColloquiaConstants.iconSet);
            setStatusBar(statusBar);
            setButtonText(LanguageManager.getString("BUT27"));
        }

        public void actionPerformed(ActionEvent e) {
            setURL();
        }
    }

    protected void setURL() {
        String loc = txtField.getText();
        if(loc == null || loc.length() == 0) return;
        int result = JOptionPane.showConfirmDialog
            (MainFrame.getInstance(),
            LanguageManager.getString("3_8"),
            LanguageManager.getString("3_7"),
            JOptionPane.YES_NO_OPTION);
        if(result != JOptionPane.YES_OPTION) return;
        tc.setURL(loc, true);
        ViewPanel.getInstance().repaintCurrentView();
    }

    // Over-ride so we can show in external browser
    public void setCurrentLocation(String url) {
        if(tc != null && tc.isExternalBrowser()) showInExternalBrowser(url);
        else super.setCurrentLocation(url);
    }

    // Display in External Browser
    protected void showInExternalBrowser(String urlString) {
        try {
            URL url = URLUtils.normalizeAddress(urlString);
            if(url != null) BrowserLauncher.openURL(url.toString());
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
