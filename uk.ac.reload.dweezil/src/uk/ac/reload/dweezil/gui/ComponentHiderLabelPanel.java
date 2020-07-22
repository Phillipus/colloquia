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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A Panel consisting of a ComponentHider button, a label and an underline bar
 *
 * @author Phillip Beauvoir
 * @version $Id: ComponentHiderLabelPanel.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class ComponentHiderLabelPanel extends JPanel {

    /**
     * The Hider Button
     */
    private ComponentHiderButton _hiderButton; 
    
    /**
     * The Title Label
     */
    private JLabel _titlelabel;
    
    /**
     * The Description Label
     */
    private JLabel _descriptionlabel;

    
	/**
	 * Constructor
	 * @param titleText The initial title text 
	 * @param descriptionText The initial description text .  If null, the description label will not be visible.
	 */
	public ComponentHiderLabelPanel(String titleText, String descriptionText) {
	    setLayout(new BorderLayout());
	    setOpaque(false);
	    
		// Hider sub-Panel
		JPanel hiderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
		hiderPanel.setOpaque(false);
		add(hiderPanel, BorderLayout.NORTH);
		
		hiderPanel.add(getComponentHiderButton());
		
		// Title
		hiderPanel.add(getTitleLabel());
		getTitleLabel().setText(titleText);

		// Underline Bar
		add(UIFactory.createGradientUnderLineBar(), BorderLayout.CENTER);
		
		// Description
		if(descriptionText != null) {
		    getDescriptionLabel().setText(descriptionText);
		}
	}
	
	/**
	 * @return The Hider Button
	 */
	public ComponentHiderButton getComponentHiderButton() {
	    if(_hiderButton == null) {
	        _hiderButton = new ComponentHiderButton();
	        _hiderButton.setToolTipText("Click to expand");
	    }
	    return _hiderButton;
	}

	/**
	 * @return The Title Label
	 */
	public JLabel getTitleLabel() {
	    if(_titlelabel == null) {
	        _titlelabel = new JLabel("<title>");
	        _titlelabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
	        _titlelabel.setFont(_titlelabel.getFont().deriveFont(Font.BOLD, 12));
	        _titlelabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	        _titlelabel.setToolTipText("Click to expand");
	        _titlelabel.addMouseListener(new MouseAdapter() {
	            public void mouseReleased(MouseEvent e) {
	                getComponentHiderButton().showComponent(getComponentHiderButton().isHidden());
	            }
	        });
	    }
	    return _titlelabel;
	}
	
	/**
	 * @return The Description Label
	 */
	public JLabel getDescriptionLabel() {
	    if(_descriptionlabel == null) {
	        _descriptionlabel = new JLabel("<description>");
	        _descriptionlabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
	        add(_descriptionlabel, BorderLayout.SOUTH);
	    }
	    return _descriptionlabel;
	}

}