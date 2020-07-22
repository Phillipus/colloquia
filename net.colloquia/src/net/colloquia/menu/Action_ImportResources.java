package net.colloquia.menu;

import java.awt.event.*;
import java.io.*;

import net.colloquia.*;
import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;

/**
 */
public class Action_ImportResources
extends MenuAction
implements Runnable, ComponentSelectionListener
{
    /** The thread which will be spun */
    private Thread thread;
    /** The file name */
    private File fileName;
    /** The progress monitor */
    private PMonitor monitor;

    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;

    static String name = LanguageManager.getString("ACTION_11");

    public Action_ImportResources() {
        super(name + "...", ColloquiaConstants.iconResource);
        ColloquiaTree.getInstance().addComponentSelectionListener(this);
        setEnabled(false);
    }

    /**
     * Triggered from the Menu.  Calls {@link #importResources}. <p>
     */
    public void actionPerformed(ActionEvent e) {
        importResources();
    }

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
        this.selComponent = selComponent;
        this.parentGroup = parentGroup;
    }

    private void importResources() {
        // What have we selected?  It must be a Resource group or Activity
        if(selComponent == null) return;
        if(!(selComponent instanceof ColloquiaContainer)) return;

        // Get the file name
        PFileChooser chooser = new PFileChooser();
        chooser.setDialogTitle(name);
        int returnVal = chooser.showOpenDialog(MainFrame.getInstance());
        if(returnVal != PFileChooser.APPROVE_OPTION) return;
        fileName = chooser.getSelectedFileAndStore();

        // Set up the progress monitor
        monitor = new PMonitor(MainFrame.getInstance(), name,
                               LanguageManager.getString("ACTION_12"),
                               LanguageManager.getString("9"));
        monitor.init(Utils.countLines(fileName) - 1);

        // Start the thread (calls run())
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Starts the thread.  This is the entry point for the thread.
     */
    public void run() {
        // Expand node
        // Update Current View
        ViewPanel.getInstance().updateCurrentView();
        ColloquiaTree.getInstance().expandNode(parentGroup, true);
        // Stop this thread
        stop();
    }

    /**
     * Stops the thread.
     */
    private void stop() {
        thread = null;
    }
}
