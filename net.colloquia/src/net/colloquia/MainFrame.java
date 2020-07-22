package net.colloquia;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import net.colloquia.comms.tables.Inbox;
import net.colloquia.comms.tables.MessageWindow;
import net.colloquia.datamodel.DataModel;
import net.colloquia.gui.StatusWindow;
import net.colloquia.gui.widgets.PSplitPane;
import net.colloquia.io.Backup;
import net.colloquia.io.ColloquiaFileException;
import net.colloquia.io.DataFiler;
import net.colloquia.menu.MainMenu;
import net.colloquia.prefs.PrefsWindow;
import net.colloquia.prefs.UserPrefs;
import net.colloquia.util.ErrorHandler;
import net.colloquia.util.LanguageManager;
import net.colloquia.util.Utils;
import net.colloquia.views.ChildWindow;
import net.colloquia.views.ExitScreen;
import net.colloquia.views.SplashScreen;
import net.colloquia.views.StatusBar;
import net.colloquia.views.ViewPanel;
import net.colloquia.views.entities.ActivityInviteView;
import net.colloquia.views.entities.BasePersonView;
import net.colloquia.views.entities.LiveActivityView;
import net.colloquia.views.entities.PersonGroupView;
import net.colloquia.views.entities.ResourceGroupView;
import net.colloquia.views.entities.ResourceView;
import net.colloquia.views.entities.StudentsAssignmentView;
import net.colloquia.views.entities.StudentsPersonView;
import net.colloquia.views.entities.TemplateGroupView;
import net.colloquia.views.entities.TemplateView;
import net.colloquia.views.entities.TutorsAssignmentView;
import net.colloquia.views.entities.TutorsPersonView;
import net.colloquia.views.tree.ColloquiaTree;
import net.colloquia.views.tree.TreePanel;

/**
 * The Colloquia Main Frame
 *
 * @author Phillip Beauvoir
 * @version 2004-01-06
 */
public class MainFrame
extends JFrame
{
    private static MainFrame instance; // This
    public static MainFrame getInstance() { return instance; }

    // Splitters
    public PSplitPane mainSplitter;

    // The Status Bar
    public StatusBar statusBar;

    // The Menu
    public MainMenu mainMenu;

    SplashScreen splash;

    /**
    * Launch the App
    * This is on a thread so we can clear the LogOnWindow
    */
    public static void launchApp() {
        if(instance != null) return;

        Thread thread = new Thread() {
            public void run() {
                new MainFrame();
            }
        };

        thread.start();
    }


    // Constructor
    public MainFrame() {
        instance = this;

        // Show splash screen
        splash = new SplashScreen(20);

        UserPrefs userPrefs = UserPrefs.getUserPrefs();
        splash.setProgress(1);

        // Set Look And Feel - be careful where this is placed!!
        try {
  	        UIManager.setLookAndFeel(userPrefs.getProperty(UserPrefs.APP_LOOK_FEEL));
            setUIDefaults();
  	    } catch (Exception exc) {
  	        System.out.println("Error loading L&F: " + exc);
  	    }

        // Get the StatusWindow stuff going first
        StatusWindow.newInstance();

        // Status bar
        statusBar = new StatusBar();
        getContentPane().add(statusBar, BorderLayout.SOUTH);

        // Get Application Icon
        setIconImage(Utils.getIcon(ColloquiaConstants.iconAppIcon).getImage());

        // Add title
        setTitle(ColloquiaConstants.APP_NAME + " - " + userPrefs.getUserName());

        // Add main splitter
        mainSplitter = new PSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
        getContentPane().add(mainSplitter, BorderLayout.CENTER);
        splash.setProgress(2);

        // Load any essential classes NOW
        loadClasses();
        splash.setProgress(3);

        // Add views Panel
        mainSplitter.setRightComponent(ViewPanel.getInstance());
        splash.setProgress(4);

        // Add Tree (BEFORE constructActions())
        mainSplitter.setLeftComponent(TreePanel.getInstance());
        splash.setProgress(5);

        // Add Menu Bar / ToolBar and Popup menu
        mainMenu = new MainMenu(this, userPrefs);
        
        // Now do Tree Keystroke remapping
        mainMenu.remapKeyStrokes(ColloquiaTree.getInstance());
        
        // Trap window closing event for our Window listener
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Add a listener to Close our window down on exit
        addWindowListener(new WindowAdapter() {
  	        public void windowClosing(WindowEvent e) {
  	            closeApp();
  	        }
  	    });

        // Set tooltip delay to 0
        //ToolTipManager.sharedInstance().setInitialDelay(0);

        // Make sure this goes here
        pack();

        // Set Window size and location BEFORE setVisible(true)
        int windowState = userPrefs.getIntegerProperty(UserPrefs.APP_STATE);
        int x = userPrefs.getIntegerProperty(UserPrefs.APP_X);
        int y = userPrefs.getIntegerProperty(UserPrefs.APP_Y);
        int width = userPrefs.getIntegerProperty(UserPrefs.APP_WIDTH);
        int height = userPrefs.getIntegerProperty(UserPrefs.APP_HEIGHT);
        setBounds(x, y, width, height);

        if(windowState == MAXIMIZED_BOTH) setExtendedState(windowState);

        // Position main Splitter
        mainSplitter.setDividerLocation(userPrefs.getIntegerProperty(UserPrefs.APP_MAINSPLITTER_POS));

        loadViews();
        splash.setProgress(19);

        // Load user data
        DataFiler.loadTreeData(DataFiler.getDataFileName(false));
        splash.setProgress(20);

        // Show window
        splash.close();
        setVisible(true);

        /*
        // This doesn't work!!  And it could be dangerous!!
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    DataFiler.saveAll();
                }
                catch(Exception ex) {

                }
            }
        });
        */

        // If user wants to work on-line then get messages
        if(GlobalSettings.isOnline()) Inbox.getInstance().getMail(getInstance(), true);

        //DataFiler.autoSaveStart(1);
    }


    //======================================================================
    // CHILD WINDOWS
    //======================================================================

    private static Vector childWindows = new Vector();

    public static synchronized void addchildWindow(ChildWindow child) {
        if(!childWindows.contains(child)) childWindows.addElement(child);
    }

    public static synchronized void removeChildWindow(ChildWindow child) {
        childWindows.removeElement(child);
    }

    public static boolean hasChildWindows() {
        return !childWindows.isEmpty();
    }

    private static void closeChildWindows() {
        for(int i = childWindows.size() - 1; i >= 0; i--) {
            ChildWindow childWindow = (ChildWindow)childWindows.elementAt(i);
            childWindow.close();
        }
    }

    //======================================================================
    //======================================================================

    /**
    * Closes the program and checks that things might need saving
    */
    public void closeApp() {
        int doBackup = JOptionPane.NO_OPTION;

        // Check to see if we have any open Children and close them
        closeChildWindows();
        // Still got some, so return
        if(hasChildWindows()) return;

        // Ask to do Zip backup if required
        if(UserPrefs.getUserPrefs().getBooleanProperty(UserPrefs.BACKUP)) {
            doBackup = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                LanguageManager.getString("ACTION_26"),
                LanguageManager.getString("ACTION_21"),
                JOptionPane.YES_NO_CANCEL_OPTION);
        }

        if(doBackup == JOptionPane.CANCEL_OPTION) return;

        ExitThread thread = new ExitThread(doBackup);
        thread.start();
    }

    private class ExitThread
    extends Thread
    {
    	int doBackup;

        public ExitThread(int doBackup) {
        	this.doBackup = doBackup;
            setCursor(ColloquiaConstants.waitCursor);
        }

        public void run() {
            ExitScreen exitScreen = new ExitScreen();

            try {
                DataFiler.saveAll();
            }
            catch(ColloquiaFileException ex) {
                exitScreen.setVisible(false);
	            setCursor(ColloquiaConstants.defaultCursor);
                ErrorHandler.showWarning("ERR2", ex, "ERR");
                return;
            }

            DataFiler.cleanFolders();
            DataFiler.deleteEmptyFolders(DataFiler.getDataFolder());
            setCursor(ColloquiaConstants.defaultCursor);

            if(doBackup == JOptionPane.YES_OPTION) {
                try {
                    exitScreen.setVisible(false);
                    Backup.backupData();
                }
                catch(ColloquiaFileException ex) {
                    ErrorHandler.showWarning("ERR3", ex, "ERR");
                    return;
                }
            }

            System.exit(0);
        }
    }

    /**
    * Load some vital classes NOW
    */
    private void loadClasses() {
        DataModel.initialise();
    }

    /**
    * Load the views in the background
    */
    private void loadViews() {
        if(!ColloquiaConstants.PRELOAD_VIEWS) return;

        LiveActivityView.getInstance();
        splash.setProgress(6);

        ActivityInviteView.getInstance();
        splash.setProgress(7);

        BasePersonView.getInstance();
        splash.setProgress(8);

        TutorsPersonView.getInstance();
        splash.setProgress(9);

        StudentsPersonView.getInstance();
        splash.setProgress(10);

        ResourceView.getInstance();
        splash.setProgress(11);

        StudentsAssignmentView.getInstance();
        splash.setProgress(12);

        TutorsAssignmentView.getInstance();
        splash.setProgress(13);

        MessageWindow.getInstance();
        splash.setProgress(14);

        TemplateView.getInstance();
        splash.setProgress(15);

        TemplateGroupView.getInstance();
        splash.setProgress(16);

        ResourceGroupView.getInstance();
        splash.setProgress(17);

        PersonGroupView.getInstance();
        splash.setProgress(18);

        PrefsWindow.getInstance();
    }

    private void setUIDefaults() {
        UIDefaults ui = UIManager.getDefaults();
        //ui.put("Menu.selectionBackground", Color.white);
        //ui.put("Menu.background", Constants.color2);
        //ui.put("MenuBar.background", Constants.color2);
        ui.put("Table.selectionBackground", ColloquiaConstants.color1);
        ui.put("Table.focusCellHighlightBorder", ColloquiaConstants.focusBorder);
    }
}


