package net.colloquia.views.entities;

import java.awt.*;

import javax.swing.*;

import net.colloquia.datamodel.entities.*;
import net.colloquia.util.*;

/**
 * The ancestor of all panel component views
 */
public abstract class ColloquiaView extends JPanel {
    protected ColloquiaComponent tc;
    protected ColloquiaContainer parentGroup;
    public Dimension getMinimumSize() { return new Dimension(0, 0); }
    public abstract void updateView();
    public abstract void setComponent(ColloquiaComponent selComponent, ColloquiaContainer parentGroup);

    protected static String msgLoading = LanguageManager.getString("LOADING") + " ";
}
