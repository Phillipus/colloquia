package net.colloquia.prefs;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import net.colloquia.ColloquiaConstants;
import net.colloquia.MainFrame;
import net.colloquia.gui.StatusWindow;
import net.colloquia.gui.widgets.PButton;
import net.colloquia.gui.widgets.PCheckBox;
import net.colloquia.gui.widgets.PComboBox;
import net.colloquia.gui.widgets.PFolderChooser;
import net.colloquia.gui.widgets.PLabel;
import net.colloquia.gui.widgets.PTextField;
import net.colloquia.io.DataFiler;
import net.colloquia.util.LanguageManager;
import uk.ac.reload.dweezil.gui.layout.XYConstraints;
import uk.ac.reload.dweezil.gui.layout.XYLayout;
import uk.ac.reload.dweezil.util.DweezilUIManager;

public class PrefsSettingsPanel extends PrefsPanel {

    // Data
    PButton btnDataPath;

    PTextField tfDataPath;

    PTextField tfNagSave;

    PCheckBox cbBackUpOnExit;

    PButton btnBackupPath;

    PTextField tfBackupPath;

    // E-mail quote
    PCheckBox cbIncludeText;

    // L&F
    PComboBox cbLookAndFeel;

    PCheckBox cbShowStatusMessages;

    String existingDataPath;

    public PrefsSettingsPanel(JFrame owner) {
        super(owner, new XYLayout());
        setBackground(ColloquiaConstants.color2);
        setBorder(new EmptyBorder(10, 10, 0, 10));

        PLabel label = new PLabel(LanguageManager.getString("4_5"), ColloquiaConstants.boldFont12);
        add(label, new XYConstraints(0, 0, 280, LABEL_HEIGHT));

        //label = new PLabel(LanguageManager.getString("4_6"));
        //add(label, new XYConstraints(0, 30, 280, LABEL_HEIGHT));

        // Button for data path
        btnDataPath = new PButton(LanguageManager.getString("4_17") + "...");
        btnDataPath.addActionListener(new BtnDataPathClick());
        add(btnDataPath, new XYConstraints(0, 30, 280, BUTTON_HEIGHT));

        // Text field for the data path
        tfDataPath = new PTextField();
        tfDataPath.setEditable(false);
        add(tfDataPath, new XYConstraints(290, 30, 350, TEXTBOX_HEIGHT));

        // Nag Save
        label = new PLabel(LanguageManager.getString("4_7"));
        add(label, new XYConstraints(0, 60, 280, LABEL_HEIGHT));

        tfNagSave = new PTextField();
        add(tfNagSave, new XYConstraints(290, 60, 50, TEXTBOX_HEIGHT));

        // Backup data on exit
        cbBackUpOnExit = new PCheckBox(LanguageManager.getString("4_48"), false);
        add(cbBackUpOnExit, new XYConstraints(0, 90, 350, CHECKBOX_HEIGHT));

        // Button for backup path
        btnBackupPath = new PButton(LanguageManager.getString("4_37") + "...");
        btnBackupPath.addActionListener(new BtnBackupPathClick());
        add(btnBackupPath, new XYConstraints(0, 120, 200, BUTTON_HEIGHT));

        // Text field for the backup path
        tfBackupPath = new PTextField();
        tfBackupPath.setEditable(false);
        add(tfBackupPath, new XYConstraints(290, 120, 350, TEXTBOX_HEIGHT));

        addStrut(150);

        label = new PLabel(LanguageManager.getString("4_12"), ColloquiaConstants.boldFont12);
        add(label, new XYConstraints(0, 170, 280, LABEL_HEIGHT));

        // E-mail include text in reply Checkbox
        cbIncludeText = new PCheckBox(LanguageManager.getString("4_13"), false);
        add(cbIncludeText, new XYConstraints(0, 200, 400, CHECKBOX_HEIGHT));

        addStrut(230);

        // L&F and other settings
        label = new PLabel(LanguageManager.getString("4_14"), ColloquiaConstants.boldFont12);
        add(label, new XYConstraints(0, 250, 280, LABEL_HEIGHT));

        // Look and feel combo box
        label = new PLabel(LanguageManager.getString("4_15"));
        add(label, new XYConstraints(0, 282, 280, LABEL_HEIGHT));

        // Look and Feels
        UIManager.LookAndFeelInfo[] systemLookAndFeels = UIManager.getInstalledLookAndFeels();
        LookAndFeel[] installedLookAndFeels = new LookAndFeel[systemLookAndFeels.length];
        for(int i = 0; i < systemLookAndFeels.length; i++) {
            installedLookAndFeels[i] = new LookAndFeel(systemLookAndFeels[i]);
        }

        cbLookAndFeel = new PComboBox(installedLookAndFeels);
        add(cbLookAndFeel, new XYConstraints(290, 280, 150, COMBOBOX_HEIGHT));
        cbLookAndFeel.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setAppearance(((LookAndFeel)cbLookAndFeel.getSelectedItem()).getClassName());
            }
        });

        cbShowStatusMessages = new PCheckBox(LanguageManager.getString("4_16"), false);
        add(cbShowStatusMessages, new XYConstraints(0, 310, 280, CHECKBOX_HEIGHT));
    }

    /**
     * Look and Feel changed - Update top level frames, including this one
     * @param className
     */
    private void setAppearance(String className) {
        DweezilUIManager.setLookAndFeel(className, new Component[]{MainFrame.getInstance(), StatusWindow.getInstance(),
                PrefsSettingsPanel.this.getParent().getParent()});

    }

    /**
     * Update UserPrefs according to the controls' settings
     */
    public void updateUserPrefs(UserPrefs prefs) {
        // Get the backup path in the Text Field
        String backup = tfBackupPath.getText().trim();

        // Nothing there, so add default based on username - this is OK
        if(backup.length() == 0) {
            backup = DataFiler.getDefaultBackupFolder(prefs.getUserName());
        }

        prefs.putProperty(UserPrefs.BACKUP_FOLDER, backup);

        prefs.putProperty(UserPrefs.BACKUP, cbBackUpOnExit.isSelected());
        prefs.putProperty(UserPrefs.SAVE_NAG_MINS, tfNagSave.getText().trim());

        try {
            int mins = Integer.parseInt(tfNagSave.getText().trim());
            MainFrame.getInstance().mainMenu.actionSave.setNagInterval(mins);
        }
        catch(Exception ex) {
            //MainFrame.getInstance().mainMenu.actionSave.setNagInterval(0);
        }

        prefs.putProperty(UserPrefs.MESSAGE_QUOTE, cbIncludeText.isSelected());

        LookAndFeel lf = (LookAndFeel)cbLookAndFeel.getSelectedItem();
        prefs.putProperty(UserPrefs.APP_LOOK_FEEL, lf.getClassName());

        prefs.putProperty(UserPrefs.STATUS_MESSAGES, cbShowStatusMessages.isSelected());
    }

    /**
     * Set the controls' settings from those found in UserPrefs
     */
    public void setSettings(UserPrefs prefs) {
        tfDataPath.setText(prefs.getProperty(UserPrefs.DATA_ROOT));
        // Save this for a comparison later
        existingDataPath = prefs.getProperty(UserPrefs.DATA_ROOT);

        String backup = prefs.getProperty(UserPrefs.BACKUP_FOLDER);
        // Nothing there, so add default based on username - this is OK
        if(backup == null || backup.length() == 0) {
            backup = DataFiler.getDefaultBackupFolder(prefs.getUserName());
        }
        tfBackupPath.setText(backup);

        cbBackUpOnExit.setSelected(prefs.getBooleanProperty(UserPrefs.BACKUP));

        tfNagSave.setText(prefs.getProperty(UserPrefs.SAVE_NAG_MINS));

        cbIncludeText.setSelected(prefs.getBooleanProperty(UserPrefs.MESSAGE_QUOTE));

        // L&F
        String lf = prefs.getProperty(UserPrefs.APP_LOOK_FEEL);
        cbLookAndFeel.setSelectedItem(lf);

        UIManager.LookAndFeelInfo[] systemLookAndFeels = UIManager.getInstalledLookAndFeels();
        for(int i = 0; i < systemLookAndFeels.length; i++) {
            if(systemLookAndFeels[i].getClassName().equals(lf)) {
                cbLookAndFeel.setSelectedIndex(i);
                break;
            }
        }

        cbShowStatusMessages.setSelected(prefs.getBooleanProperty(UserPrefs.STATUS_MESSAGES));
    }

    /**
     * If the user has manually changed the data path we must find out if there
     * is already some data there and warn them
     */
    public boolean setDataFolder() {
        UserPrefs prefs = UserPrefs.getUserPrefs();
        String warningMsg = LanguageManager.getString("4_3");

        // Get the data path in the Text Field
        String dataRoot = tfDataPath.getText().trim();

        // Nothing there, so add default based on username - this is OK
        if(dataRoot.length() == 0) {
            dataRoot = DataFiler.getDefaultDataFolder(prefs.getUserName());
            prefs.putProperty(UserPrefs.DATA_ROOT, dataRoot);
            return true;
        }

        // Compare current datapath with previous one
        // If different check for existing data and warn user
        if(!existingDataPath.equalsIgnoreCase(dataRoot)) {
            String dataFile1 = DataFiler.addFileSepChar(dataRoot) + DataFiler.treeFolder + DataFiler.fileSepChar + DataFiler.dataFileName;
            // legacy of 1.1
            String dataFile2 = DataFiler.addFileSepChar(dataRoot) + "ll_data" + DataFiler.fileSepChar + DataFiler.dataFileName;
            if(DataFiler.fileExists(dataFile1) || DataFiler.fileExists(dataFile2)) {
                int result = JOptionPane.showConfirmDialog(this, warningMsg, LanguageManager.getString("4_4"), JOptionPane.YES_NO_OPTION);
                if(result != JOptionPane.YES_OPTION) {
                    // Put back to original path
                    tfDataPath.setText(existingDataPath);
                    return false;
                }
                else {
                    // Set and reload
                    prefs.putProperty(UserPrefs.DATA_ROOT, dataRoot);
                    if(MainFrame.getInstance() != null) {
                        DataFiler.reloadData();
                    }
                    return true;
                }
            }
        }

        // Changed
        prefs.putProperty(UserPrefs.DATA_ROOT, dataRoot);
        return true;
    }

    /** Select data path */
    private class BtnDataPathClick extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            PFolderChooser chooser = new PFolderChooser(owner, LanguageManager.getString("4_17"));
            File folder = chooser.getFolder();
            if(folder != null) {
                tfDataPath.setText(folder.getPath());
            }
        }
    }

    /** Select backup path */
    private class BtnBackupPathClick extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            PFolderChooser chooser = new PFolderChooser(owner, LanguageManager.getString("4_17"));
            File folder = chooser.getFolder();
            if(folder != null) {
                tfBackupPath.setText(folder.getPath());
            }
        }
    }

    /**
     * LookAndFeel Class - we use this as a wrapper so we can use toString()
     * to get a shorter name
     */
    private class LookAndFeel {

        private UIManager.LookAndFeelInfo lookAndFeelInfo;

        public LookAndFeel(UIManager.LookAndFeelInfo lookAndFeelInfo) {
            this.lookAndFeelInfo = lookAndFeelInfo;
        }

        public String toString() {
            return lookAndFeelInfo.getName();
        }

        public String getClassName() {
            return lookAndFeelInfo.getClassName();
        }
    }

}