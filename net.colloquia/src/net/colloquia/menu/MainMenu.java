package net.colloquia.menu;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import net.colloquia.datamodel.*;
import net.colloquia.datamodel.entities.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.prefs.*;
import net.colloquia.util.*;
import net.colloquia.views.*;
import net.colloquia.views.tree.*;

/**
 * The Main Menu
 *
 * @author Phillip Beauvoir
 * @version 2004-01-06
 */
public class MainMenu
implements ComponentSelectionListener
{
    public JMenuBar menuBar;
    public ColloquiaToolBar toolBar;
    public JPopupMenu popupMenu;

    private ColloquiaComponent selComponent;
    private ColloquiaContainer parentGroup;
    private Activity currentActivity;

    private Vector refreshListeners;

    private UserPrefs userPrefs;

    public MainMenu(JFrame owner, UserPrefs userPrefs) {
        refreshListeners = new Vector();

        this.userPrefs = userPrefs;

        // Setup Actions
        createActions();

        // Attach Actions to Menu Items
        menuBar = createMenuBar();
        // Add this to owner frame
        owner.setJMenuBar(menuBar);

        // ToolBar
        toolBar = createToolBar();
        // Add this to owner frame
        owner.getContentPane().add(toolBar, BorderLayout.NORTH);

        // Popup menu - this will be attached to the Tree
        popupMenu = createPopupMenu();

        ColloquiaTree.getInstance().addComponentSelectionListener(this);
    }


    // =========================== ACTIONS ==================================
    public Action_History actionBack;
    public Action_History actionForward;

    public Action_Save actionSave;
    public Action_Trash actionTrash;
    public Action_ImportPeople actionImportPeople;
    public Action_ExportPeople actionExportPeople;
    //public Action_ImportResources actionImportResources;
    //public Action_ExportResources actionExportResources;
    public Action_ImportBookmarks actionImportBookmarks;
    public Action_ImportFavourites actionImportFavourites;
    public Action_Exit actionExit;
    public Action_Backup actionBackup;
    public Action_Restore actionRestore;

    public Action_Undo actionUndo;
    public Action_Redo actionRedo;
    public Action_Cut actionCut;
    public Action_Copy actionCopy;
    public Action_Paste actionPaste;
    public Action_Delete actionDelete;
    public Action_Rename actionRename;

    public Action_InsertPerson actionInsertPerson;
    public Action_InsertPersonGroup actionInsertPersonGroup;
    public Action_InsertResourceGroup actionInsertResourceGroup;
    public Action_InsertResource actionInsertResource;
    public Action_InsertAssignment actionInsertAssignment;
    public Action_InsertActivity actionInsertActivity;
    public Action_AddToActivity actionAddToActivity;

    public Action_ExpandTree actionExpandTree;
    public Action_ExpandTree actionCollapseTree;
    public Action_ExpandBranch actionExpandBranch;
    public Action_ExpandBranch actionCollapseBranch;

    public JMenuItem miDebugWindow;
    public Action_ViewProperties actionViewProperties;
    public Action_StatusWindow actionStatusWindow;
    public Action_ShowInboxOutbox actionShowInboxOutbox;

    public Action_GetMail actionGetMail;
    public Action_ImportMail actionImportMail;
    public Action_SendMail actionSendMail;

    public Action_Prefs actionPrefs;

    public Action_SendActivity actionSendActivity;
    public Action_SendResource actionSendResource;
    public Action_SendMyDetails actionSendMyDetails;
    public Action_AcceptDecline actionAcceptDecline;
    public Action_Upload actionUpload;

    public Action_Completed actionMakeCompleted;
    public Action_ActivityProperties actionActivityProperties;
    public Action_ShowPeople actionShowPeople;
    public Action_ShowResources actionShowResources;
    public Action_ShowAssignment actionShowAssignment;

    public Action_HotInfo actionHotInfo;

    public Action_About actionAbout;
    public Action_Help actionHelp;

    // Instantiate the actions
    private void createActions() {
        actionBack = new Action_History(Action_History.BACK);
        actionForward = new Action_History(Action_History.FORWARD);

        int mins = userPrefs.getIntegerProperty(UserPrefs.SAVE_NAG_MINS);
        actionSave = new Action_Save(mins);

        actionBackup = new Action_Backup();
        actionRestore = new Action_Restore();
        actionTrash = new Action_Trash();
        actionImportPeople = new Action_ImportPeople();
        actionExportPeople = new Action_ExportPeople();
        //actionImportResources = new Action_ImportResources();
        //actionExportResources = new Action_ExportResources();
		actionImportBookmarks = new Action_ImportBookmarks();
		actionImportFavourites = new Action_ImportFavourites();
        actionExit = new Action_Exit();

        actionUndo = new Action_Undo();
        actionRedo = new Action_Redo();

        actionCut = new Action_Cut();
        refreshListeners.addElement(actionCut);

        actionCopy = new Action_Copy();
        actionPaste = new Action_Paste();

        actionDelete = new Action_Delete();
        refreshListeners.addElement(actionDelete);

        actionRename = new Action_Rename();

        actionInsertPerson = new Action_InsertPerson();
        actionInsertPersonGroup = new Action_InsertPersonGroup();
        actionInsertResourceGroup = new Action_InsertResourceGroup();
        actionInsertResource = new Action_InsertResource();
        actionInsertAssignment = new Action_InsertAssignment();
        actionInsertActivity = new Action_InsertActivity();
        actionAddToActivity = new Action_AddToActivity();

        actionExpandTree = new Action_ExpandTree(true);
        actionCollapseTree = new Action_ExpandTree(false);
        actionExpandBranch = new Action_ExpandBranch(true);
        actionCollapseBranch = new Action_ExpandBranch(false);

        actionViewProperties = new Action_ViewProperties();
        actionStatusWindow = new Action_StatusWindow();

        actionShowInboxOutbox = new Action_ShowInboxOutbox();
        actionGetMail = new Action_GetMail();
        actionImportMail = new Action_ImportMail();
        actionSendMail = new Action_SendMail();

        actionPrefs = new Action_Prefs();

        actionSendResource = new Action_SendResource();
        actionSendMyDetails = new Action_SendMyDetails();
        actionUpload = new Action_Upload();
        refreshListeners.addElement(actionUpload);

        actionHelp = new Action_Help();
        actionAbout = new Action_About();

        actionActivityProperties = new Action_ActivityProperties();
        refreshListeners.addElement(actionActivityProperties);

        actionShowPeople = new Action_ShowPeople();
        refreshListeners.addElement(actionShowPeople);
        actionShowResources = new Action_ShowResources();
        refreshListeners.addElement(actionShowResources);
        actionShowAssignment = new Action_ShowAssignment();
        refreshListeners.addElement(actionShowAssignment);

        actionMakeCompleted = new Action_Completed();
        refreshListeners.addElement(actionMakeCompleted);

        actionSendActivity = new Action_SendActivity();
        refreshListeners.addElement(actionSendActivity);

        actionAcceptDecline = new Action_AcceptDecline();
        refreshListeners.addElement(actionAcceptDecline);

        actionHotInfo = new Action_HotInfo();
    }


    // =========================== MENU BAR ==================================
    public JMenu fileMenu;
    public JMenu editMenu;
    public JMenu insertMenu;
    public JMenu activityMenu;
    public JMenu viewMenu;
    public JMenu commsMenu;
    public JMenu helpMenu;

    private JMenuBar createMenuBar() {
        JMenuBar mBar = new JMenuBar();

        // The Key Mask will be 'Meta' for Mac and 'Ctrl' for PC/Unix
        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        JMenuItem item;

      	// FILE
      	fileMenu = mBar.add(new JMenu(LanguageManager.getString("FILE")));
        //fileMenu.setMnemonic('f');

        // Save
        item = fileMenu.add(actionSave);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, keyMask));
        fileMenu.addSeparator();
        // Backup
        item = fileMenu.add(actionBackup);
        // Restore
        item = fileMenu.add(actionRestore);
        fileMenu.addSeparator();
        // Import People
        fileMenu.add(actionImportPeople);
        // Export People
        fileMenu.add(actionExportPeople);
        fileMenu.addSeparator();
        // Import Resources
        //fileMenu.add(actionImportResources);
        fileMenu.add(actionImportBookmarks);
        fileMenu.add(actionImportFavourites);
        // Export Resources
        //fileMenu.add(actionExportResources);
        fileMenu.addSeparator();
        // Exit
        fileMenu.add(actionExit);

        // EDIT
        editMenu = mBar.add(new JMenu(LanguageManager.getString("EDIT")));
        // Undo
        item = editMenu.add(actionUndo);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, keyMask));
        // Redo
        item = editMenu.add(actionRedo);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, keyMask + Event.SHIFT_MASK));
        editMenu.addSeparator();
        // Cut
        item = editMenu.add(actionCut);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, keyMask));
        // Copy
        item = editMenu.add(actionCopy);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, keyMask));
        // Paste
        item = editMenu.add(actionPaste);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, keyMask));
        // Delete
        item = editMenu.add(actionDelete);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        // Rename
        editMenu.addSeparator();
        editMenu.add(actionRename);
      	// Separator
      	editMenu.addSeparator();
        // Prefs
        editMenu.add(actionPrefs);

        // INSERT
        insertMenu = mBar.add(new JMenu(LanguageManager.getString("INSERT")));
        // Insert new Person Group
        insertMenu.add(actionInsertPersonGroup);
        // Insert new Person
        insertMenu.add(actionInsertPerson);
        // Insert Resource Group
        insertMenu.add(actionInsertResourceGroup);
        // Insert Resource
        insertMenu.add(actionInsertResource);
        // Insert Assignment
        insertMenu.add(actionInsertAssignment);
        // Insert Activity
        insertMenu.add(actionInsertActivity);

        insertMenu.addSeparator();

        // Add to Activity
        insertMenu.add(actionAddToActivity);


        // ACTIVITY
        activityMenu = mBar.add(new JMenu(LanguageManager.getString("0")));
        // Make Completed
        activityMenu.add(actionMakeCompleted);
        activityMenu.addSeparator();
        // Accept dcline
        activityMenu.add(actionAcceptDecline);
        // send Activity
        activityMenu.add(actionSendActivity);

        activityMenu.add(actionActivityProperties);


        // VIEW
        viewMenu = mBar.add(new JMenu(LanguageManager.getString("VIEW")));
        // History
        viewMenu.add(actionBack);
        viewMenu.add(actionForward);
      	viewMenu.addSeparator();
        // Expand/Collapse Tree
        viewMenu.add(actionExpandTree);
        viewMenu.add(actionCollapseTree);
      	viewMenu.addSeparator();
        // Expand/Collapse branches
        viewMenu.add(actionExpandBranch);
        viewMenu.add(actionCollapseBranch);
        // View Properties
        viewMenu.addSeparator();
        viewMenu.add(actionViewProperties);
        // View Status Window
        viewMenu.add(actionStatusWindow);

        // COMMS
        commsMenu = mBar.add(new JMenu(LanguageManager.getString("COMMS")));
        // Hot Info
        commsMenu.add(actionHotInfo);
        commsMenu.addSeparator();
        // Inbox
        commsMenu.add(actionShowInboxOutbox);
        commsMenu.add(actionGetMail);
        commsMenu.add(actionImportMail);
        commsMenu.add(actionSendMail);
        commsMenu.addSeparator();
        // Send Resource
        commsMenu.add(actionSendResource);
        commsMenu.add(actionUpload);
        // Send My Details
        commsMenu.add(actionSendMyDetails);


/*-------------------------------------------------------------------*/


        // HELP
        helpMenu = mBar.add(new JMenu(LanguageManager.getString("HELP")));
        item = helpMenu.add(actionHelp);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        helpMenu.addSeparator();
        helpMenu.add(actionAbout);

        return mBar;
    }


    // =========================== POPUP MENUS ================================
    private JPopupMenu createPopupMenu() {
        JPopupMenu pMenu = new JPopupMenu();

        pMenu.add(actionCut);
        pMenu.add(actionCopy);
        pMenu.add(actionPaste);
        pMenu.add(actionDelete);
        pMenu.add(actionRename);

        pMenu.addSeparator();

        pMenu.add(actionMakeCompleted);
        pMenu.add(actionActivityProperties);
        pMenu.add(actionShowPeople);
        pMenu.add(actionShowResources);
        pMenu.add(actionShowAssignment);

        pMenu.addSeparator();

        pMenu.add(actionInsertPersonGroup);
        pMenu.add(actionInsertPerson);
        pMenu.add(actionInsertResourceGroup);
        pMenu.add(actionInsertResource);
        pMenu.add(actionInsertAssignment);
        pMenu.add(actionInsertActivity);
        pMenu.addSeparator();
        pMenu.add(actionAddToActivity);

        return pMenu;
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void setNagInterval(int mins) {
    	actionSave.setNagInterval(mins);
    }

    // =========================== TOOLBAR ==================================
    private ColloquiaToolBar createToolBar() {
        ColloquiaToolBar tBar = new ColloquiaToolBar();

        tBar.add(actionSave);
        tBar.addSeparator();

        tBar.add(actionBack);
        tBar.add(actionForward);

        tBar.addSeparator();

        tBar.add(actionPrefs);

        tBar.addSeparator();

        tBar.add(actionShowInboxOutbox);
        tBar.add(actionGetMail);
        //tBar.add(actionImportMail);
        tBar.add(actionSendMail);

        tBar.addSeparator();

        tBar.add(actionMakeCompleted);

        tBar.add(actionSendActivity);

        return tBar;
    }


    /**
     * Some components need the Ctrl-X, Ctrl-C, and Ctrl-V key bindings remapped
     * @param component The Component that binds the keystrokes
     */
    public void remapKeyStrokes(JComponent component) {
    	int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    	component.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_X, keyMask), "CUT");
    	component.getActionMap().put("CUT", actionCut);
    	component.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, keyMask), "COPY");
    	component.getActionMap().put("COPY", actionCopy);
    	component.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, keyMask), "PASTE");
    	component.getActionMap().put("PASTE", actionPaste);
    }

    //============================== LISTENERS =================================

    public void componentSelected(ColloquiaComponent selComponent,
                      ColloquiaContainer parentGroup, Activity currentActivity) {
    	// Do whatever
    }


    /**
    * Refresh all menu items
    */
    public void refreshAll() {
        for(int i = 0; i < refreshListeners.size(); i++) {
            Object o = refreshListeners.elementAt(i);
            if(o instanceof MainMenuItem) ((MainMenuItem)o).refresh();
        }
    }

}


