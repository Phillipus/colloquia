package net.colloquia.gui.widgets;

import java.awt.*;

import javax.swing.*;

import net.colloquia.menu.*;

/**
* A special type JToolBar
*/
public class ColloquiaToolBar extends JToolBar {

    public ColloquiaToolBar() {
        init();
        //setBackground(ColloquiaConstants.toolBarColor);
    }

    public ColloquiaToolBar(Color color) {
        init();
        setBackground(color);
    }

    private void init() {
        setFloatable(false);
        // Hooray!!  Keep this kludge in to ensure rollovers ALWAYS work in
        // Metal L&F
        putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    }

    public void add(MenuAction a) {
        super.add(a.getButton());
    }

    public void add(MenuAction a, int index) {
        super.add(a.getButton(), index);
    }

    public void add(MenuAction a, boolean enabled) {
        add(a);
        a.setEnabled(enabled);
    }

    public void remove(MenuAction a) {
        super.remove(a.getButton());
    }
}
