package net.colloquia.gui.widgets;

import javax.swing.*;

/**
*/
public class PSplitPane extends JSplitPane {
	public static int size = 8;

	public PSplitPane() {
        super();
		init();
    }

    public PSplitPane(int newOrientation) {
    	super(newOrientation);
		init();
    }

    public PSplitPane(int newOrientation, boolean newContinuousLayout) {
    	super(newOrientation, newContinuousLayout);
		init();
    }

    protected void init() {
    	//setDividerSize(size);
        //setOneTouchExpandable(true);
    }
}