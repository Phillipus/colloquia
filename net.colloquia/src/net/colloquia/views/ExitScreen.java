package net.colloquia.views;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import net.colloquia.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;

/**
 * Groovy exit screen
 */
public class ExitScreen extends JWindow {
    private JPanel mainPanel;
    private ImageIcon icon;
    private JLabel label;

    public ExitScreen() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
        mainPanel.setBackground(Color.white);

        icon = Utils.getIcon(ColloquiaConstants.iconSplash);
        label = new JLabel(icon);

        mainPanel.add(label, BorderLayout.CENTER);

        mainPanel.add(new PLabel(" " + LanguageManager.getString("SAVE_EXIT") + "...", Color.black), BorderLayout.SOUTH);

        getContentPane().add(mainPanel);

        int picWidth = 320;
        int picHeight = 200;

        Utils.centreWindow(this, picWidth, picHeight);

        setVisible(true);
        setCursor(ColloquiaConstants.waitCursor);
    }
}
