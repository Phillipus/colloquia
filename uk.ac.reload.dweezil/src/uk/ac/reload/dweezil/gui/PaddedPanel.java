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

import javax.swing.Box;
import javax.swing.JPanel;

import uk.ac.reload.dweezil.gui.layout.SGLayout;

/**
 * A Panel that has padding on the left or right
 *
 * @author Phillip Beauvoir
 * @version $Id: PaddedPanel.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class PaddedPanel extends JPanel {

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    
    /**
     * Padding Type
     */
    private int _type;

    
    /**
     * Default Constructor for a Padding Type of LEFT and padFactor of 0
     */
    public PaddedPanel() {
        this(LEFT, 0);
    }

    /**
     * Constructs a PadPanel
     * @param type Either PaddedPanel or PaddedPanel.RIGHT
     * @param padFactor The scale value for the padding
     */
	public PaddedPanel(int type, double padFactor) {
		if(type != LEFT && type != RIGHT) {
		    throw new RuntimeException("PadPanel type must be one of PaddedPanel.LEFT or PaddedPanel.RIGHT");
		}
		
		_type = type;
		
	    SGLayout sgLayout = new SGLayout(1, 2);
		sgLayout.setColumnScale(type, padFactor);
		setLayout(sgLayout);
		
		// If it's a Left Padding we need to add a first component
		if(type == LEFT) {
		    add(Box.createGlue());
		}
	}

	/**
	 * @return The Padding Type - either LEFT or RIGHT
	 */
	public int getType() {
	    return _type;
	}
	
	/**
	 * Set the UI
	 */
	protected void setUI() {
	    setOpaque(false);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#updateUI()
	 */
	public void updateUI() {
		super.updateUI();
		setUI();
	}
}