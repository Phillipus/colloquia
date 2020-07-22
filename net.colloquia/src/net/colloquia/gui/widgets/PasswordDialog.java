package net.colloquia.gui.widgets;

import java.awt.*;

import javax.swing.*;

/**
*/
public class PasswordDialog
extends InputDialog
{
    public PasswordDialog(Frame owner, String message, String title) {
        super(owner, message, title);
        inPanel.remove(tField);
        tField = null;
        tField = new JPasswordField();
        tField.addActionListener(okListener);
        inPanel.add(tField);
    }

    public int show() {
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        tField.requestFocus();
        dialog.setVisible(true);
        return retValue;
    }

    public String getPassword() {
    	return getValue();
    }
}