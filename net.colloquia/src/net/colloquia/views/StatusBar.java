package net.colloquia.views;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

public class StatusBar extends JLabel {
    public StatusBar() {
        setFont(new Font("SansSerif", Font.BOLD, 12));
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    }

    public Dimension getPreferredSize() {
        return new Dimension(200, 18);
    }

    public void clearText() {
        setText("");
    }

    public void setText(String text) {
    	super.setText(" " + text);
    }
}
