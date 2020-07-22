package net.colloquia.menu;

import java.awt.event.*;

import javax.swing.*;

import net.colloquia.*;
import net.colloquia.io.*;
import net.colloquia.util.*;
import net.colloquia.views.*;

/**
 *
 */
public class Action_Backup
extends MenuAction
implements MainMenuItem
{
    static String name = LanguageManager.getString("ACTION_21");
    static String msg = LanguageManager.getString("ACTION_22") + "\n" +
        LanguageManager.getString("ARE_YOU_SURE");

    public Action_Backup() {
        super(name + "...", null);
    }

    public void refresh() {
    }

    /**
     * Triggered from the Menu.
     */
    public void actionPerformed(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog
            (MainFrame.getInstance(),
            msg,
            name,
            JOptionPane.YES_NO_OPTION);
        if(result != JOptionPane.YES_OPTION) return;

        // Save first
        try {
            DataFiler.saveAll();
        }
        catch(ColloquiaFileException ex) {
            ErrorHandler.showWarning("ERR2", ex, "ERR");
            return;
        }

        // Put on thread, not SwingUtilities.invokeLater()
        // So that Progress monitor displays
        Thread doRun = new Thread() {
            public void run() {
                try {
                    Backup.backupData();
                }
                catch(ColloquiaFileException ex) {
                    ErrorHandler.showWarning("ERR3", ex, "ERR");
                    return;
                }

                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    LanguageManager.getString("ACTION_23"), LanguageManager.getString("ACTION_21"),
                    JOptionPane.INFORMATION_MESSAGE);
            }
        };

        doRun.start();
    }
}
