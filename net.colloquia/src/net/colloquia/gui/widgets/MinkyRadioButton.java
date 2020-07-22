package net.colloquia.gui.widgets;

import javax.swing.*;

import net.colloquia.util.*;


public class MinkyRadioButton
extends JRadioButton
{
    public MinkyRadioButton(String text, boolean enabled) {
    	super(text, enabled);

        setFocusPainted(false);
        setOpaque(false);

        setIcon(Utils.getIcon("rb"));
        setSelectedIcon(Utils.getIcon("rbr"));
        setPressedIcon(Utils.getIcon("rbp"));
    }

    public MinkyRadioButton(String text) {
    	this(text, false);
    }
}