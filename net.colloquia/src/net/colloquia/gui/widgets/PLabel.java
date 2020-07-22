package net.colloquia.gui.widgets;

import java.awt.*;

import javax.swing.*;

import net.colloquia.*;


/**
A nice label type
*/
public class PLabel extends JLabel {

    public PLabel(String t) {
        setForeground(Color.black);
        setFont(ColloquiaConstants.plainFont12);
        setText(t);
    }

    public PLabel(String t, Color color) {
        setForeground(color);
        setFont(ColloquiaConstants.boldFont12);
        setText(t);
    }

    public PLabel(String t, Font font) {
        setForeground(Color.black);
        setFont(font);
        setText(t);
    }

    public PLabel(String t, int horizontalAlignment) {
        this(t);
        setHorizontalAlignment(horizontalAlignment);
    }

    public PLabel(String t, String toolTipText) {
        this(t);
        setToolTipText(toolTipText);
    }
}


