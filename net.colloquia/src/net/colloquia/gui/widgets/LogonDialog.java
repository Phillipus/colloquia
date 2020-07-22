package net.colloquia.gui.widgets;

import java.awt.*;

import javax.swing.*;

import net.colloquia.util.*;

/**
 */
public class LogonDialog
extends PasswordDialog
{
    protected String userName;
    protected JTextField userTextField;

    public LogonDialog(Frame owner, String message, String title) {
        super(owner, message, title);

        userTextField = new JTextField();
        JLabel label = new JLabel(LanguageManager.getString("3_10"));

        setGridSize(2, 2);

        inPanel.remove(tLabel);
        inPanel.remove(tField);

        inPanel.add(label);
        inPanel.add(userTextField);
        inPanel.add(tLabel);
        inPanel.add(tField);
    }

    protected void okClicked() {
        userName = userTextField.getText();
        super.okClicked();
    }

    public void setUserName(String userName) {
        userTextField.setText(userName);
    }

    public void setPassword(String pwd) {
        tField.setText(pwd);
    }

    public String getUserName() {
    	return userName;
    }

    public int show() {
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        userTextField.requestFocus();
        dialog.setVisible(true);
        return retValue;
    }
}
