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
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

/**
 * An underline bar<br>
 * <br>
 *
 * @author Phillip Beauvoir
 * @version $Id: GradientUnderline.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class GradientUnderline extends JComponent {

	/**
	 * The Colors for the Gradient
	 */
    private Color _startColor, _endColor;
	
	/**
	 * The height of the bar
	 */
    private int _height;

	/**
	 * Constructor
	 * @param height
	 * @param startColor
	 * @param endColor
	 */
	public GradientUnderline(int height, Color startColor, Color endColor) {
		_startColor = startColor;
		_endColor = endColor;
		_height = height;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return new Dimension(super.getPreferredSize().width, _height);
	}
	
	/** 
	 * Paint the gradient
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		GradientPaint gradient = new GradientPaint(0, 0, _startColor, getWidth() / 1.1f, 0, _endColor); 
		g2d.setPaint(gradient);
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}

}