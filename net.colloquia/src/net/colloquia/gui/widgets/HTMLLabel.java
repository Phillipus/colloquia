package net.colloquia.gui.widgets;

import java.awt.*;

import javax.swing.*;


/**
* A label that prints HTML
*/
public class HTMLLabel extends JLabel {

    public HTMLLabel(String t, int fontSize) {
        StringBuffer sb = new StringBuffer();
        sb.append("<html><font size=" + fontSize);
        sb.append(" face=helvetica>" + t + "</html>");
        setForeground(Color.black);
        setText(sb.toString());
    }

    public HTMLLabel(String t, int fontSize, String toolTipText) {
        this(t, fontSize);
        setToolTipText(toolTipText);
    }
}

