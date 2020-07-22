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

import javax.swing.JCheckBox;

/**
 * A Flat Check Box<p>
 *
 * @author Phillip Beauvoir
 * @version $Id: FlatCheckBox.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class FlatCheckBox
extends JCheckBox
{

    /**
     * Default Constructor
     */
    public FlatCheckBox() {
        super();
        setUI();
    }

    /**
     * Creates a check box with text and specifies whether 
     * or not it is initially selected.
     *
     * @param text the text of the check box.
     * @param selected a boolean value indicating the initial selection
     *        state. If <code>true</code> the check box is selected
     */
    public FlatCheckBox(String text, boolean selected) {
        super(text, selected);
        setUI();
    }
    
    /**
     * Creates an initially unselected check box with text.
     * @param text the text of the check box.
     */
	public FlatCheckBox(String text) {
		super(text);
		setUI();
	}
	
	/**
	 * Setup the UI
	 */
	protected void setUI() {
		setOpaque(false);
		setBorderPaintedFlat(true);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#updateUI()
	 */
	public void updateUI() {
		super.updateUI();
		setUI();
	}
}