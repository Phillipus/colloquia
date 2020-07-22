package net.colloquia;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.colloquia.gui.widgets.HTMLLabel;
import net.colloquia.gui.widgets.PLabel;
import net.colloquia.gui.widgets.PTextField;
import net.colloquia.io.DataFiler;
import net.colloquia.prefs.PrefsWindow;
import net.colloquia.prefs.UserPrefs;
import net.colloquia.util.ErrorHandler;
import net.colloquia.util.LanguageManager;
import net.colloquia.util.Utils;

/**
 * The Main Logon Window
 * 
 * @author Phillipus
 * @version 2004-01-06
 *
 */
public class    LogonWindow
extends         JFrame
{
    private JButton                 btnOK;
    private JComboBox               userBox;
    private JComboBox               languageBox;
    private JCheckBox				online;
    private JList                   userList;
    private PTextField              userNameField;
    private Page1                   page1;
    private Page2                   page2;
    private Page3                   page3;
    private Image                   backDrop;
    private int                     width;
    private int                     height;

    public LogonWindow() {
        setIconImage(Utils.getIcon(ColloquiaConstants.iconAppIcon).getImage());
        setTitle(ColloquiaConstants.APP_NAME + " " + LanguageManager.getString("1_1"));
        setResizable(false);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        backDrop = Utils.getIcon(ColloquiaConstants.iconLogonBackground).getImage();
        width = backDrop == null ? 480 : backDrop.getWidth(this);
        height = backDrop == null ? 280 : backDrop.getHeight(this);

        int offset = Utils.getOS() == Utils.WINDOWS ? 20 : 0;
        Utils.centreWindow(this, width, height + offset);

        page1 = new Page1();
        setPanel(page1);

        setVisible(true);
        btnOK.grabFocus();
        getRootPane().setDefaultButton(btnOK);
    }


    private class MyPanel extends JPanel {
        public MyPanel() {
            setLayout(new BorderLayout());
            setOpaque(false);
        }

        public void paint(Graphics g) {
            if(backDrop != null) g.drawImage(backDrop, 0, 0, this);
            super.paint(g);
        }
    }


    // MAIN //
    public static void main(String args[]) {
        // Get the Language first
        String language = GlobalSettings.getLanguage();
        LanguageManager.setLanguage(language);

        // Command line args
        for(int i = 0 ; i < args.length; i++) {
            // User Home Dir
            if(args[i].equalsIgnoreCase("-H")) {
                DataFiler.setUserHome(args[++i]);
            }
            if(args[i].equalsIgnoreCase("-N")) {
                ColloquiaConstants.NETWORKED = true;
            }
            //System.out.println(args[i]);
        }

        // Log on
        new LogonWindow();
    }

    /**
    * Get User Names
    */
    private Vector getUserNames() {
        Vector v = new Vector();

        File file = new File(DataFiler.getColloquiaFolder());
        if(!file.exists() || !file.isDirectory()) {
            return v;
        }
        String[] files = file.list();

        for(int i = 0; i < files.length; i++) {
            if(files[i].endsWith(".cqp")) {
                v.addElement(Utils.getFileName(files[i]));
            }
        }

        Utils.sortVector(v, true);
        return v;
    }

    /**
    * Update combos, buttons et al.
    */
    private void update() {
        Vector users = getUserNames();
        userBox.setModel(new DefaultComboBoxModel(users));
        userList.setListData(users);
        btnOK.setEnabled(userBox.getModel().getSize() != 0);
    }

    private String validateName(String name) {
    	if(name == null) {
            return null;
        }
        name = name.trim();
        name = name.replace(' ', '_');
        return name;
    }

    private class Page1 extends MyPanel {
        public Page1() {
            JPanel messagePanel = new JPanel();
            messagePanel.setBorder(new EmptyBorder(20, 0, 20, 0));
            messagePanel.setOpaque(false);
            add(messagePanel);

            //PLabel l = new PLabel(LanguageManager.getString("1_2"), Constants.boldFont12);
            //l.setPreferredSize(new Dimension(380, 15));
            //messagePanel.add(l);
            HTMLLabel l1 = new HTMLLabel(LanguageManager.getString("1_3"), 3);
            l1.setPreferredSize(new Dimension(380, 45));
            messagePanel.add(l1);

            PLabel l = new PLabel(LanguageManager.getString("1_4"));
            l.setPreferredSize(new Dimension(132, 15));
            messagePanel.add(l);

            // User profile name
            userBox = new JComboBox(getUserNames());
            userBox.setPreferredSize(new Dimension(200, 20));
            userBox.setOpaque(false);
            userBox.setSelectedItem(GlobalSettings.getLastUser());
            messagePanel.add(userBox);

            l = new PLabel(LanguageManager.getString("1_5"));
            l.setPreferredSize(new Dimension(132, 15));
            messagePanel.add(l);

            // Preferred Language
            languageBox = new JComboBox(LanguageManager.getLanguageNames());
            languageBox.setPreferredSize(new Dimension(200, 20));
            languageBox.setSelectedItem(LanguageManager.getCurrentLanguage());
            languageBox.addActionListener(new LanguageSet());
            messagePanel.add(languageBox);

            l = new PLabel(LanguageManager.getString("1_11"));
            l.setPreferredSize(new Dimension(132, 15));
            messagePanel.add(l);

            // Online
            online = new JCheckBox(LanguageManager.getString("1_8"), GlobalSettings.isOnline());
            online.setPreferredSize(new Dimension(200, 20));
            online.setOpaque(false);
            messagePanel.add(online);

            // Add Button Panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setOpaque(false);
            add(buttonPanel, BorderLayout.SOUTH);

            btnOK = new JButton(LanguageManager.getString("1_6"));
            btnOK.setEnabled(userBox.getModel().getSize() != 0);
            btnOK.addActionListener(new OKClick());
            buttonPanel.add(btnOK);

            JButton btnProfiles = new JButton(LanguageManager.getString("1_7"));
            buttonPanel.add(btnProfiles);
            btnProfiles.addActionListener(new ProfilesClick());

            JButton btnCancel = new JButton(LanguageManager.getString("EXIT"));
            buttonPanel.add(btnCancel);
            btnCancel.addActionListener(new CancelClick());

            btnOK.grabFocus();
        }
    }

    private class LanguageSet extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            String language = (String)languageBox.getSelectedItem();
            LanguageManager.setLanguage(language);
            GlobalSettings.save((String)userBox.getSelectedItem(), language, online.isSelected());
            new LogonWindow();
            dispose();
        }
    }

    private class OKClick extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            String userName = (String)userBox.getSelectedItem();
            if(userName == null) return;
            userName = userName.trim();
            if(userName.length() == 0) return;

            // Load user Prefs
            UserPrefs userPrefs = UserPrefs.loadUserPrefs(userName);
            UserPrefs.setUserPrefs(userPrefs);

            // Language
            String language = (String)languageBox.getSelectedItem();
            LanguageManager.setLanguage(language);

            // Save settings
            GlobalSettings.save(userName, language, online.isSelected());

            // Launch
            MainFrame.launchApp();

            dispose();
        }
    }

    private class CancelClick extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            dispose();
            System.exit(0);
        }
    }

    private class ProfilesClick extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if(page2 == null) page2 = new Page2();
            setPanel(page2);
        }
    }

    private class BackClick extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            setPanel(page1);
        }
    }

    private class DeleteClick extends AbstractAction {
        int val;
        String[] options = {LanguageManager.getString("1_9"),
                            LanguageManager.getString("1_10"),
                            LanguageManager.getString("CANCEL")};

        String msg = LanguageManager.getString("1_12");

        public void actionPerformed(ActionEvent e) {
            val = JOptionPane.showOptionDialog(LogonWindow.this, msg, LanguageManager.getString("1_13"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.DEFAULT_OPTION,
                    null, options, options[1]);

            String profileName = (String)userList.getSelectedValue();

            switch(val) {
                // X Close / Cancel
                case -1:
                case 2:
                    break;
                // Delete Files
                case 0:
                    DataFiler.deleteProfile(profileName, true);
                    break;
                // Don't Delete Files
                case 1:
                    DataFiler.deleteProfile(profileName, false);
                    break;
            }

            update();
        }
    }

    private class RenameClick extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            String profileName = (String)userList.getSelectedValue();
            if(profileName == null) return;

            // Ask for a new name
            String newName = (String)JOptionPane.showInputDialog(LogonWindow.this,
                                LanguageManager.getString("1_14"),
                                LanguageManager.getString("RENAME"),
                                JOptionPane.QUESTION_MESSAGE, null, null, profileName);

            // Check name
            newName = validateName(newName);
            if(newName == null) return;
            if(newName.length() == 0 || newName.equalsIgnoreCase(profileName)) return;

            // New fileName
            String fileName = DataFiler.getUserPrefsFileName(newName);

            // Exists?
            if(DataFiler.fileExists(fileName)) {
                ErrorHandler.showWarning(LogonWindow.this, "1_16", null, "1_17");
                return;
            }

            // Valid?
            if(!DataFiler.isValidFileName(fileName)) {
                ErrorHandler.showWarning(LogonWindow.this, "1_18", null, "1_17");
                return;
            }

            // OK
            DataFiler.renameProfile(profileName, newName);

            update();
        }
    }


    private class NewClick extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if(page3 == null) page3 = new Page3();
            setPanel(page3);
            userNameField.grabFocus();
        }
    }

    /**
    * New Profile Name
    */
    private class SetClick extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            String userName = userNameField.getText().trim();
            userName = validateName(userName);
            if(userName == null || userName.length() == 0) return;
            String fileName = DataFiler.getUserPrefsFileName(userName);

            // Exists?
            if(DataFiler.fileExists(fileName)) {
                ErrorHandler.showWarning(LogonWindow.this, "1_16", null, "1_17");
                return;
            }

            // Valid fileName?
            if(userName.length() == 0 || !DataFiler.isValidFileName(fileName)) {
                ErrorHandler.showWarning(LogonWindow.this, "1_18", null, "1_17");
                return;
            }

            // Save settings
            GlobalSettings.save(userName, (String)languageBox.getSelectedItem(), online.isSelected());

            // New Prefs
            UserPrefs prefs = new UserPrefs(userName);
            prefs.putProperty(UserPrefs.DATA_ROOT, DataFiler.getDefaultDataFolder(userName));
            prefs.putProperty(UserPrefs.EMAIL_IMPORT_FOLDER, DataFiler.getDefaultDataFolder(userName));
            UserPrefs.setUserPrefs(prefs);

            LogonWindow.this.dispose();

            // Launch Prefs
            PrefsWindow.getInstance().showWindow();
        }
    }

    private JPanel currentPanel;

    public void setPanel(JPanel panel) {
        if(currentPanel != null) {
            getContentPane().remove(currentPanel);
        }
        currentPanel = panel;
        getContentPane().add(currentPanel);
        validate();
        repaint();
    }


    // Options for New, Rename, Delete
    private class Page2 extends MyPanel {
        JButton btnRename, btnDelete;

        public Page2() {
            JPanel messagePanel = new JPanel(new BorderLayout());
            messagePanel.setOpaque(false);
            messagePanel.setBorder(new EmptyBorder(5, 90, 70, 90));
            add(messagePanel);

            HTMLLabel l = new HTMLLabel(LanguageManager.getString("1_19"), 3);
            l.setPreferredSize(new Dimension(300, 50));
            messagePanel.add(l, BorderLayout.NORTH);

            userList = new JList(getUserNames());
            //userList.setPreferredSize(new Dimension(290, 50));
            userList.addListSelectionListener(new MyListSelectionListener());
            userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane sp = new JScrollPane(userList);
            messagePanel.add(sp, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setOpaque(false);
            add(buttonPanel, BorderLayout.SOUTH);

            JButton btnNew = new JButton(LanguageManager.getString("NEW") + "...");
            buttonPanel.add(btnNew);
            btnNew.addActionListener(new NewClick());

            btnRename = new JButton(LanguageManager.getString("RENAME") + "...");
            btnRename.setEnabled(false);
            buttonPanel.add(btnRename);
            btnRename.addActionListener(new RenameClick());

            btnDelete = new JButton(LanguageManager.getString("DELETE") + "...");
            btnDelete.setEnabled(false);
            buttonPanel.add(btnDelete);
            btnDelete.addActionListener(new DeleteClick());

            JButton btnBack = new JButton("<< " + LanguageManager.getString("BACK"));
            buttonPanel.add(btnBack);
            btnBack.addActionListener(new BackClick());
        }


        private class MyListSelectionListener implements ListSelectionListener {
            public void valueChanged(ListSelectionEvent e) {
                boolean selected = true;
                int index = userList.getSelectedIndex();
                int numRows = userList.getModel().getSize();
                // No row selected
                if(index == -1 || numRows == 0) selected = false;

                btnDelete.setEnabled(selected);
                btnRename.setEnabled(selected);
            }
        }
    }

    // New Profile
    private class Page3 extends MyPanel {
        JButton btnSet, btnBack;

        public Page3() {
            JPanel messagePanel = new JPanel();
            messagePanel.setOpaque(false);
            messagePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            add(messagePanel);

            HTMLLabel msg = new HTMLLabel(LanguageManager.getString("1_24") +
                LanguageManager.getString("1_25") + LanguageManager.getString("1_26"), 3);
            msg.setPreferredSize(new Dimension(410, 50));
            messagePanel.add(msg);

            PLabel l = new PLabel(LanguageManager.getString("1_4"));
            l.setPreferredSize(new Dimension(95, 15));
            messagePanel.add(l);
            userNameField = new PTextField();
            userNameField.setPreferredSize(new Dimension(250, 20));
            userNameField.addActionListener(new SetClick());
            userNameField.getDocument().addDocumentListener(new UserNameListener());
            messagePanel.add(userNameField);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setOpaque(false);
            add(buttonPanel, BorderLayout.SOUTH);

            btnSet = new JButton(LanguageManager.getString("ADD"));
            btnSet.setEnabled(false);
            buttonPanel.add(btnSet);
            btnSet.addActionListener(new SetClick());

            JButton btnBack = new JButton(LanguageManager.getString("CANCEL"));
            buttonPanel.add(btnBack);
            btnBack.addActionListener(new BackClick());
        }

        private class UserNameListener implements DocumentListener {
            public void removeUpdate(DocumentEvent evt) {
                setButton();
            }

            public void changedUpdate(DocumentEvent evt) {
                setButton();
            }

            public void insertUpdate(DocumentEvent evt) {
                setButton();
            }

            private void setButton() {
                btnSet.setEnabled(userNameField.getText().length() != 0);
            }
        }
    }

}
