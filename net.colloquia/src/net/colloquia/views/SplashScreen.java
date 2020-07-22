package net.colloquia.views;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import net.colloquia.*;
import net.colloquia.gui.widgets.*;
import net.colloquia.util.*;

/**
* Groovy splash screen
*/
public class SplashScreen extends JWindow {
    private JPanel mainPanel;
    private ImageIcon icon;
    private JLabel label;
    private PProgressBar pBar;

    public SplashScreen(int progressMax) {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
        mainPanel.setBackground(Color.white);

        icon = Utils.getIcon(ColloquiaConstants.iconSplash);
        label = new JLabel(icon);

        mainPanel.add(label, BorderLayout.CENTER);

        pBar = new PProgressBar(progressMax);
        mainPanel.add(pBar, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);

        //picWidth = Constants.iconSplash.getIconWidth();
        //picHeight = Constants.iconSplash.getIconHeight();

        int picWidth = 320;
        int picHeight = 200;

        //pack();

        Utils.centreWindow(this, picWidth, picHeight);

        setVisible(true);
        setCursor(ColloquiaConstants.waitCursor);
    }

    public void _incProgress() {
    	pBar.incProgress();
    }

    public void setProgress(int value) {
    	pBar.setProgress(value);
    }

    public void close() {
        // This is a workaround for Bug Id 4302818
        // Otherwise the animated gif continues to run in the background
        // It does not work for java 1.3
        icon.getImage().flush();
        dispose();
    }
}
