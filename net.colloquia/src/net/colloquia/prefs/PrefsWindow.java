package net.colloquia.prefs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.comms.*;
import net.colloquia.util.*;


public class PrefsWindow
extends JFrame
{
    private static PrefsWindow instance = new PrefsWindow(); // This
    public static PrefsWindow getInstance() { return instance;  }

    PrefsMyDetailsPanel myDetailsPanel;
    PrefsEmailPanel emailSettingsPanel;
    PrefsSettingsPanel settingsPanel;
    PrefsBrowserPanel browserSettingsPanel;

    JButton btnCancel;
    JButton btnOK;

    private PrefsWindow() {
        setIconImage(Utils.getIcon(ColloquiaConstants.iconMyDetails).getImage());
        setTitle(ColloquiaConstants.APP_NAME + " - " + LanguageManager.getString("MY_DETAILS"));

        // Add a Close Window listener
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new CancelListener());

        getContentPane().setLayout(new BorderLayout());

        JTabbedPane tabPane = new JTabbedPane();
        getContentPane().add(tabPane, BorderLayout.CENTER);

        JPanel buttonPanel = constructButtonPanel();
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        myDetailsPanel = new PrefsMyDetailsPanel(this);
        emailSettingsPanel = new PrefsEmailPanel(this);
        settingsPanel = new PrefsSettingsPanel(this);
        browserSettingsPanel = new PrefsBrowserPanel(this);

        tabPane.addTab(LanguageManager.getString("MY_DETAILS"), myDetailsPanel);
        tabPane.addTab(LanguageManager.getString("4_1"), emailSettingsPanel);
        tabPane.addTab(LanguageManager.getString("4_2"), settingsPanel);
        tabPane.addTab(LanguageManager.getString("4_8"), browserSettingsPanel);

        //pack();
        Utils.centreWindow(this, 700, 590);
    }

    public void showWindow() {
        myDetailsPanel.setSettings(UserPrefs.getUserPrefs());
        emailSettingsPanel.setSettings(UserPrefs.getUserPrefs());
        settingsPanel.setSettings(UserPrefs.getUserPrefs());
        browserSettingsPanel.setSettings(UserPrefs.getUserPrefs());

        setVisible(true);
        setState(JFrame.NORMAL);
        requestFocus();
    }

    /**
    * Construct the bottom button panel
    */
    private JPanel constructButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        btnOK = new JButton(LanguageManager.getString("OK"));
        buttonPanel.add(btnOK);
        btnOK.addActionListener(new ButtonClickListener());
        getRootPane().setDefaultButton(btnOK);

        btnCancel = new JButton(LanguageManager.getString("CANCEL"));
        buttonPanel.add(btnCancel);
        btnCancel.addActionListener(new ButtonClickListener());

        return buttonPanel;
    }

    /**
    * Click a button listener
    */
    private class ButtonClickListener extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if(e.getSource().equals(btnCancel)) cancel();
            else if(e.getSource().equals(btnOK)) buttonOKClicked();
        }
    }


    private class CancelListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            cancel();
        }
    }

    public void buttonOKClicked() {
        // Check for OK data path
        if(!settingsPanel.setDataFolder()) return;

        myDetailsPanel.updateUserPrefs(UserPrefs.getUserPrefs());
        emailSettingsPanel.updateUserPrefs(UserPrefs.getUserPrefs());
        settingsPanel.updateUserPrefs(UserPrefs.getUserPrefs());
        browserSettingsPanel.updateUserPrefs(UserPrefs.getUserPrefs());

        UserPrefs.saveUserPrefs(UserPrefs.getUserPrefs());
        UserPrefs.setUserPrefs(UserPrefs.getUserPrefs());

        // Clear e-mail passwords
        MessageManager.clearPasswords();

        setVisible(false);

        // If we came here from the LogOn, launch main program
        if(MainFrame.getInstance() == null) MainFrame.launchApp();
    }

    public void cancel() {
        // If MainFrame hasn't been instantiated, we came from the Logon window with a new profile
        if(MainFrame.getInstance() == null) System.exit(0);
        else setVisible(false);
    }
}







