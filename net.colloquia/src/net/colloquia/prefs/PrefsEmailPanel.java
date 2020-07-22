package net.colloquia.prefs;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;

import net.colloquia.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;
import uk.ac.reload.dweezil.gui.layout.*;

/**
 * The Preferences Page for E-mail Settings
 *
 * @author Phillip Beauvoir
 * @version 2003-11-17
 */
public class PrefsEmailPanel
extends PrefsPanel
{
    PTextField tfEmailAddress;
    PComboBox cbAliases;
    PTextField tfInUserName;        // User name for incoming mail
    PComboBox cbOutServer;
    PTextField tfInServer;
    PTextField tfFolder;
    PComboBox cbProtocol;
    PCheckBox cbExpunge;
    PCheckBox cbAuthSMTP;           // Authenticated SMTP checkbox
    //PTextField tfOutUserName;       // Authenticated SMTP user name Text Field
    PButton importFolderButton;
    PTextField tfImportFolder;

    public PrefsEmailPanel(JFrame owner) {
        super(owner, new XYLayout());
        setBackground(ColloquiaConstants.color2);
        setBorder(new EmptyBorder(10, 10, 0, 10));

        int YPOS = 0;

        PLabel label = new PLabel(LanguageManager.getString("4_18"));
        add(label, new XYConstraints(0, YPOS + 2, 280, LABEL_HEIGHT));
        tfEmailAddress = new PTextField();
        add(tfEmailAddress, new XYConstraints(290, YPOS, 300, TEXTBOX_HEIGHT));

        YPOS += 30;

        // Aliases
        label = new PLabel(LanguageManager.getString("4_50"));
        add(label, new XYConstraints(0, YPOS, 280, LABEL_HEIGHT));

        cbAliases = new PComboBox();
        cbAliases.setEditable(true);
        add(cbAliases, new XYConstraints(290, YPOS, 300, COMBOBOX_HEIGHT));

        YPOS += 30;

        addStrut(YPOS);

        // OUTGOING MAIL SERVER

        YPOS += 10;

        // Main label
        label = new PLabel(LanguageManager.getString("4_40"), ColloquiaConstants.boldFont12);
        add(label, new XYConstraints(0, YPOS + 2, 280, LABEL_HEIGHT));

        YPOS += 28;

        // Out Server
        label = new PLabel(LanguageManager.getString("4_19"));
        add(label, new XYConstraints(0, YPOS + 2, 280, LABEL_HEIGHT));

        cbOutServer = new PComboBox();
        cbOutServer.setEditable(true);
        add(cbOutServer, new XYConstraints(290, YPOS, 300, COMBOBOX_HEIGHT));

        YPOS += 28;

        // Authenticated SMTP Tick box
        cbAuthSMTP = new PCheckBox(LanguageManager.getString("4_39"), false);
        add(cbAuthSMTP, new XYConstraints(0, YPOS, 380, CHECKBOX_HEIGHT));

        YPOS += 30;

        addStrut(YPOS);

        // INCOMING MAIL SERVER

        YPOS += 10;

        label = new PLabel(LanguageManager.getString("4_20"), ColloquiaConstants.boldFont12);
        add(label, new XYConstraints(0, YPOS, 280, LABEL_HEIGHT));

        YPOS += 30;

        label = new PLabel(LanguageManager.getString("4_21"));
        add(label, new XYConstraints(0, YPOS + 2, 280, LABEL_HEIGHT));

        tfInServer = new PTextField();
        add(tfInServer, new XYConstraints(290, YPOS, 300, TEXTBOX_HEIGHT));

        YPOS += 30;

        label = new PLabel(LanguageManager.getString("4_22"));
        add(label, new XYConstraints(0, YPOS + 2, 280, LABEL_HEIGHT));

        tfInUserName = new PTextField();
        add(tfInUserName, new XYConstraints(290, YPOS, 300, TEXTBOX_HEIGHT));

        YPOS += 30;

        label = new PLabel(LanguageManager.getString("4_23"));
        add(label, new XYConstraints(0, YPOS + 2, 280, LABEL_HEIGHT));

        cbProtocol = new PComboBox(new String[] {"imap", "pop3"});
        cbProtocol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setFolderAccess();
            }
        });
        add(cbProtocol, new XYConstraints(290, YPOS, 100, COMBOBOX_HEIGHT));

        YPOS += 30;

        cbExpunge = new PCheckBox(LanguageManager.getString("4_24"), false);
        add(cbExpunge, new XYConstraints(0, YPOS, 380, CHECKBOX_HEIGHT));

        YPOS += 30;

        label = new PLabel(LanguageManager.getString("4_25"));
        add(label, new XYConstraints(0, YPOS + 2, 280, LABEL_HEIGHT));

        tfFolder = new PTextField();
        add(tfFolder, new XYConstraints(290, YPOS, 300, TEXTBOX_HEIGHT));

        YPOS += 30;

        addStrut(YPOS);

        // IMPORT FOLDER

        YPOS += 8;

        label = new PLabel(LanguageManager.getString("4_27"), ColloquiaConstants.boldFont12);
        add(label, new XYConstraints(0, YPOS, 500, LABEL_HEIGHT));

        YPOS += 28;

        label = new PLabel(LanguageManager.getString("4_28"));
        add(label, new XYConstraints(0, YPOS, 600, LABEL_HEIGHT));

        YPOS += 30;

        importFolderButton = new PButton(LanguageManager.getString("4_29"));
        importFolderButton.addActionListener(new ImportFolderButtonClick());
        add(importFolderButton, new XYConstraints(0, YPOS, 220, BUTTON_HEIGHT));

        tfImportFolder = new PTextField();
        tfImportFolder.setEditable(false);
        add(tfImportFolder, new XYConstraints(290, YPOS, 300, TEXTBOX_HEIGHT));

        setFolderAccess();
     }

    /**
    * Update UserPrefs according to the controls' settings
    */
    public void updateUserPrefs(UserPrefs prefs) {
		prefs.putProperty(UserPrefs.EMAIL_ADDRESS, tfEmailAddress.getText().trim());
		prefs.setAliases(cbAliases.getAllItems());
		prefs.putProperty(UserPrefs.EMAIL_IN_USER_NAME, tfInUserName.getText().trim());
		prefs.setOutServers(cbOutServer.getAllItems());
		prefs.putProperty(UserPrefs.EMAIL_ACTIVE_OUT_SERVER, (String)cbOutServer.getSelectedItem());
		prefs.putProperty(UserPrefs.EMAIL_IN_SERVER, tfInServer.getText().trim());
		prefs.putProperty(UserPrefs.EMAIL_FOLDER, tfFolder.getText().trim());
		prefs.putProperty(UserPrefs.EMAIL_PROTOCOL, (String)cbProtocol.getSelectedItem());
		prefs.putProperty(UserPrefs.EMAIL_LEAVE_ON_SERVER, cbExpunge.isSelected());
        prefs.putProperty(UserPrefs.EMAIL_AUTHSMTP, cbAuthSMTP.isSelected());
		prefs.putProperty(UserPrefs.EMAIL_IMPORT_FOLDER, tfImportFolder.getText().trim());
    }

    /**
    * Set the controls' settings from those found in UserPrefs
    */
    public void setSettings(UserPrefs prefs) {
        tfEmailAddress.setText(prefs.getProperty(UserPrefs.EMAIL_ADDRESS));
        cbAliases.setObjects(prefs.getAliases());
        tfInUserName.setText(prefs.getProperty(UserPrefs.EMAIL_IN_USER_NAME));
        cbOutServer.setObjects(prefs.getOutServers());
        cbOutServer.setSelectedItem(prefs.getProperty(UserPrefs.EMAIL_ACTIVE_OUT_SERVER));
        tfInServer.setText(prefs.getProperty(UserPrefs.EMAIL_IN_SERVER));
        tfFolder.setText(prefs.getProperty(UserPrefs.EMAIL_FOLDER));
        cbProtocol.setSelectedItem(prefs.getProperty(UserPrefs.EMAIL_PROTOCOL));
        cbExpunge.setSelected(prefs.getBooleanProperty(UserPrefs.EMAIL_LEAVE_ON_SERVER));
        cbAuthSMTP.setSelected(prefs.getBooleanProperty(UserPrefs.EMAIL_AUTHSMTP));
        tfImportFolder.setText(prefs.getProperty(UserPrefs.EMAIL_IMPORT_FOLDER));
    }

    /**
    * If protocol is pop3 set folder to INBOX and grey out folder field
    */
    private void setFolderAccess() {
        String selection = (String)cbProtocol.getSelectedItem();
        if(selection.equals("pop3")) {
            tfFolder.setText("INBOX");
            tfFolder.setEnabled(false);
        }
        else {
            tfFolder.setEnabled(true);
        }
    }

    /**
    * Get Import Folder
    */
    private class ImportFolderButtonClick extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            PFolderChooser chooser = new PFolderChooser(owner, LanguageManager.getString("CHOOSE_FOLDER"));
            File folder = chooser.getFolder();
            if(folder != null) tfImportFolder.setText(folder.getPath());
        }
    }
}
