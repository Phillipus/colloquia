package net.colloquia.prefs;

import java.awt.*;

import javax.swing.*;

import uk.ac.reload.dweezil.gui.layout.*;

public abstract class PrefsPanel
extends JPanel
{
    public static final int TEXTBOX_HEIGHT = 22;
    public static final int LABEL_HEIGHT = 15;
    public static final int COMBOBOX_HEIGHT = 22;
    public static final int CHECKBOX_HEIGHT = 20;
    public static final int BUTTON_HEIGHT = 22;

    JFrame owner;

    public PrefsPanel(JFrame owner) {
        super();
        this.owner = owner;
    }

    public PrefsPanel(JFrame owner, LayoutManager layout) {
        super(layout);
        this.owner = owner;
    }

    public void addStrut(int y) {
        JPanel strut = new JPanel();
        add(strut, new XYConstraints(0, y, 670, 3));
    }

    /**
    * Update UserPrefs according to the controls' settings
    */
    public abstract void updateUserPrefs(UserPrefs prefs);

    /**
    * Set the controls' settings from those found in UserPrefs
    */
    public abstract void setSettings(UserPrefs prefs);
}
