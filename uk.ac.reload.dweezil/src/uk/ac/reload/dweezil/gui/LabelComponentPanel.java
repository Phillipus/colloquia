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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import uk.ac.reload.dweezil.gui.layout.SGLayout;

/**
 * A Panel containing a JLabel and JComponent.<br>
 * Sizing will be Relative.
 * The right-hand padding can be set as a proportional width setting.<br>
 * <br>
 *
 * @author Phillip Beauvoir
 * @version $Id: LabelComponentPanel.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class LabelComponentPanel extends JPanel {

    /**
     * The Component we are labelling
     */
    private JComponent _component;
    
    /**
     * The Label
     */
    private JLabel _label;
    
	/**
	 * Constructor for a LD_LabelComponentPanel which will stretch the component to the right edge.
	 * A righ-edge padding factor can be set which will shorten the width of the component.
	 * @param labelText the text to display in the JLabel
	 * @param component the component to the right of the label
	 * @param columnprop the scale value for the first column
	 * @param rightpad a proportion of right-edge padding. A value of 0 means no padding.
	 */
	public LabelComponentPanel(String labelText, JComponent component, double columnprop, double rightpad) {
	    _component = component;
	    
	    setOpaque(false);
	    
	    SGLayout layout = new SGLayout(1, 2);
	    layout.setColumnScale(0, columnprop);
	    setLayout(layout);
		
		_label = new JLabel(labelText);
		add(_label);
		
		// Use right-hand padding
		if(rightpad > 0) {
		    PaddedPanel padPanel = new PaddedPanel(PaddedPanel.RIGHT, rightpad);
		    padPanel.add(component);
		    add(padPanel);
		}
		// No right-hand padding
		else {
		    add(component);
		}
	}
	
	/**
	 * Constructor for a LD_LabelComponentPanel where there is no right-edge padding or stretching.
	 * The given component maintains its preferred size.
	 * @param labelText the text to display in the JLabel
	 * @param component the component to the right of the label
	 * @param columnprop the scale value for the first column
	 */
	public LabelComponentPanel(String labelText, JComponent component, double columnprop) {
	    _component = component;

	    setOpaque(false);
		
	    SGLayout layout = new SGLayout(1, 2);
	    layout.setColumnScale(0, columnprop);
	    setLayout(layout);

	    _label = new JLabel(labelText);
		add(_label);
		
		add(component);
	}
	
    /**
     * @return Returns the component
     */
    public JComponent getComponent() {
        return _component;
    }
    
    /**
     * @return Returns the label
     */
    public JLabel getLabel() {
        return _label;
    }
}