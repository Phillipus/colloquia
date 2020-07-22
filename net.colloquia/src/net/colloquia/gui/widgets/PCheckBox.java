package net.colloquia.gui.widgets;

import javax.swing.*;

import net.colloquia.*;

public class PCheckBox
extends JCheckBox
{
    public PCheckBox(String text, boolean selected) {
        super(text, selected);
        setFont(ColloquiaConstants.plainFont12);
        setOpaque(false);
    }
}