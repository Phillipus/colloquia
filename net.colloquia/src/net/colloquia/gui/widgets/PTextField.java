package net.colloquia.gui.widgets;

import javax.swing.*;

import net.colloquia.*;

/**
A TextField with a text cursor
*/
public class PTextField extends JTextField {
    public PTextField() {
        setCursor(ColloquiaConstants.textCursor);
    }

    public PTextField(String text) {
        this();
        setText(text);
    }

    public PTextField(int columns) {
        this();
        setColumns(columns);
    }

    public PTextField(String text, int columns) {
        this(columns);
        setText(text);
    }
}

