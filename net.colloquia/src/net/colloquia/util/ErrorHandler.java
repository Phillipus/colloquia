package net.colloquia.util;

import java.awt.*;

import javax.swing.*;

import net.colloquia.*;


public class ErrorHandler {

    /**
     * Show warning message with keys from LanguageManager and parent specified
     */
    public static void showWarning(Component parent, String keyMsg, Exception ex, String keyTitle) {
        String msg = LanguageManager.getString(keyMsg);
        if(ex != null) {
            msg += "\n" + ex.getMessage();
            if(ColloquiaConstants.DEBUG) ex.printStackTrace();
        }
        JOptionPane.showMessageDialog(parent, msg,
            LanguageManager.getString(keyTitle), JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show warning message with keys from LanguageManager and MainFrame
     */
    public static void showWarning(String keyMsg, Exception ex, String keyTitle) {
        showWarning(MainFrame.getInstance(), keyMsg, ex, keyTitle);
    }
}