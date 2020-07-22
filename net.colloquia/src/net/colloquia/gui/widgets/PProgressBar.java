package net.colloquia.gui.widgets;

import java.awt.*;

import javax.swing.*;

import net.colloquia.*;


public class PProgressBar
extends JComponent
{
	int max;
    float extent;
    int inc;
    int LEFT = 10, RIGHT = 10;

	public PProgressBar(int max) {
    	setMax(max);
    }

    public void paint(Graphics g) {
    	super.paint(g);
        g.setColor(ColloquiaConstants.color1);
        if(extent != 0) g.fill3DRect(LEFT, 5, (int)extent, 10, true);
    }

    public Dimension getPreferredSize() {
    	return new Dimension(100, 20);
    }

    public void setMax(int max) {
        if(max < 1) max = 1;
    	this.max = max;
        inc = 1;
    }

    public void incProgress() {
        int width = getWidth();
        extent = ((float)(width-20) / max) * inc++;
    	repaint();
    }

    public void setProgress(int value) {
        int width = getWidth();
        extent = ((float)(width-20) / max) * value;
    	repaint();
    }
}
