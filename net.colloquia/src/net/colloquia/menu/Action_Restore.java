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
public class Action_Restore
extends MenuAction
implements MainMenuItem
{
    static String name = LanguageManager.getString("ACTION_27");

    public Action_Restore() {
        super(name + "...", null);
    }

    public void refresh() {
    }

    /**
     * Triggered from the Menu.
     */
    public void actionPerformed(ActionEvent e) {
        Thread doRun = new Thread() {
            public void run() {
                boolean result = Restore.restoreData();
                if(result)
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    LanguageManager.getString("ACTION_30"), LanguageManager.getString("ACTION_27"),
                    JOptionPane.INFORMATION_MESSAGE);
            }
        };
        //SwingUtilities.invokeLater(doRun);
        doRun.start();
    }
}
