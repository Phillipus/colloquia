/**
 *  RELOAD TOOLS
 *
 *  Copyright (c) 2004 Oleg Liber, Bill Olivier, Phillip Beauvoir
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  Project Management Contact:
 *
 *  Oleg Liber
 *  Bolton University
 *  Deane Road
 *  Bolton BL3 5AB
 *  UK
 *
 *  e-mail:   o.liber@bolton.ac.uk
 *
 *
 *  Technical Contact:
 *
 *  Phillip Beauvoir
 *  e-mail:   p.beauvoir@dadabeatnik.com
 *
 *  Web:      http://www.reload.ac.uk
 *
 */

package uk.ac.reload.dweezil.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

import javax.swing.JPanel;


/**
 * A Nice Panel with a Gradient
 *
 * @author Phillip Beauvoir
 * @version $Id: GradientPanel.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class GradientPanel
extends JPanel
{
	
	/**
	 * The Color for the Gradient
	 */
    private Color _color = new Color(200, 200, 200);
	
	/**
	 * Constructor
	 */
	public GradientPanel() {
		super();
		setupView();
	}
	
	/**
	 * Constructor
	 * @param layout
	 */
	public GradientPanel(LayoutManager layout) {
		super(layout);
		setupView();
	}
	
	/**
	 * Set up the view
	 */
	protected void setupView() {
		setBackground(Color.WHITE);
	}
	
	/**
	 * Set the Gradient start color on the CoolBar
	 * @param color The Color
	 */
	public void setGradientColor(Color color) {
		_color = color;
	}
	
	/** 
	 * Paint the gradient
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		GradientPaint gradient = new GradientPaint(0, 0, _color, 50, 100, getBackground()); 
		g2d.setPaint(gradient);
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}
}
