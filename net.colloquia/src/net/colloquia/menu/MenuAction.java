package net.colloquia.menu;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import net.colloquia.util.*;
import net.colloquia.views.*;

/**
*/
public abstract class MenuAction
extends AbstractAction
{
    private ImageIcon mIcon;
    private ImageIcon bIcon;
    private ImageIcon bIconRoll;
    private ImageIcon bIconDisabled;
    private ImageIcon bIconClicked;

    private String text;
    private JButton button;

    private Font font = new Font("SansSerif", Font.BOLD, 9);

    private class MouseMonitor extends MouseAdapter {
        private StatusBar statusBar;

        public MouseMonitor(StatusBar statusBar) {
            this.statusBar = statusBar;
        }

        public void mouseEntered(MouseEvent e) {
            statusBar.setText(text);
        }

        public void mouseExited(MouseEvent e) {
            statusBar.clearText();
        }
    }

    public MenuAction(String text, String iconName) {
        this(text, iconName, null);
    }

    public MenuAction(String text, String iconName, StatusBar statusBar) {
        this.text = text;

        if(iconName != null) {
            mIcon = Utils.getIcon(iconName + "m");   // Menu icon
            bIcon = Utils.getIcon(iconName);         // Button icon
            bIconRoll = Utils.getIcon(iconName + "r");   // Rollover icon
            bIconDisabled = Utils.getIcon(iconName + "d");   // Disabled icon
            bIconClicked = Utils.getIcon(iconName + "c");   // Pressed icon
        }

        // Menu
        putValue(Action.NAME, text);
        if(mIcon != null) setMenuIcon(mIcon);
        else if(bIcon != null) setMenuIcon(bIcon);

        // Button
        button = createButton();
        if(statusBar != null) button.addMouseListener(new MouseMonitor(statusBar));
    }

    public void setStatusBar(StatusBar statusBar) {
        if(statusBar != null && button != null)
            button.addMouseListener(new MouseMonitor(statusBar));
    }

    public void setMenuIcon(ImageIcon icon) {
        putValue(Action.SMALL_ICON, icon);
    }

    public void setButtonIcon(ImageIcon icon) {
        if(button != null) button.setIcon(icon);
    }

    /*
    * Sets text
    */
    public void setText(String text) {
        putValue(Action.NAME, text);
        if(button != null) button.setToolTipText(text);
    }

    public void setEnabled(boolean newValue) {
        button.setEnabled(newValue);
        super.setEnabled(newValue);
    }

    public JButton getButton() {
        return button;
    }

    //public void setMnemonic(int keyCode) {
    //    button.setMnemonic(keyCode);
    //}

    private JButton createButton() {
        JButton button = new JButton();

        button.setBorder(new EmptyBorder(0, 2, 0, 2));
		//button.setMargin(new Insets(0, 2, 0, 2));

        button.setFont(font);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);

        if(bIcon != null) button.setIcon(bIcon);
        if(bIconRoll != null) button.setRolloverIcon(bIconRoll);
        if(bIconDisabled != null) button.setDisabledIcon(bIconDisabled);
        if(bIconClicked != null) button.setPressedIcon(bIconClicked);

        button.setEnabled(isEnabled());

        button.addActionListener(this);

        button.setToolTipText(text);

        button.setContentAreaFilled(false);   // YES!!!
        //button.setOpaque(false);            // NO!!
        button.setFocusPainted(false);

        button.setMaximumSize(new Dimension(62, 48));

        return button;
    }

    public void setButtonText(String btnText) {
        if(btnText != null) button.setText(btnText);
    }
}

