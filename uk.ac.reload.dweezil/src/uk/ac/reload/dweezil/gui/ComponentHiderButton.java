/**
 *  RELOAD TOOLS
 *
 *  Copyright (c) 2003 Oleg Liber, Bill Olivier, Phillip Beauvoir
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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;

import uk.ac.reload.dweezil.util.DweezilUIManager;

/**
 * 
 * ComponentHiderButton.
 * <p><p>
 * This little button will hide a given JComponent and show it again when clicked.
 * Visual feedback is provided by a nice arrow icon.
 * <p><p>
 * The Component that is to be hidden/shown will have its preferred size set to null when it is
 * shown, so it might need to be put it in a container (another JPanel perhaps) that has its
 * preferred size set to the desired size.
 * <p><p>
 * For example:
 * <p>
 * <code>
 *    JTree tree = new JTree();<p>
 *    JScrollPane sp = new JScrollPane(_tree);<p>
 *    sp.setPreferredSize(new Dimension(110, 100));<p>
 *    JPanel dummy = new JPanel(new BorderLayout());<p>
 *    dummy.add(sp2);<p>
 *    ComponentHiderButton hider = new ComponentHiderButton(dummy);<p>
 * </code>
 * <p>
 * @author Phillip Beauvoir
 * @version $Id: ComponentHiderButton.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class ComponentHiderButton extends JButton {
	
	/**
	 * The component to hide/show
	 */
    private JComponent _component;
	
	/**
	 * State of component to hide/show
	 */
	private boolean hidden = false;
	
	/**
	 * This size will hide the component
	 */
	public static Dimension zeroSize = new Dimension(0, 0);
	
	public static Icon iconSouth = DweezilUIManager.getIcon("uk/ac/reload/dweezil/resources/south.gif");
	public static Icon iconEast = DweezilUIManager.getIcon("uk/ac/reload/dweezil/resources/east.gif");
	
	/**
	 * Default Constructor
	 */
	public ComponentHiderButton() {
		setIcon(iconSouth);
		
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showComponent(hidden);
			}
		});
	}

	/**
	 * Constructor
	 * @param component the JComponent to hide or show
	 */
	public ComponentHiderButton(JComponent component) {
	    this();
	    setComponent(component);
	}
	
	/**
	 * Set the component to display
	 * @param component The component to display
	 */
	public void setComponent(JComponent component) {
		_component = component;
	}
	
	/**
	 * @return The component that is the object to be displayed / hidden
	 */
	public JComponent getComponent() {
	    return _component;
	}
	
	/**
	 * Show or hide the governed component.  If the component is null, will have no visible effect.
	 * @param show if true will show the component
	 */
	public void showComponent(boolean show) {
	    if(show) {
			// If preferredSize is null, the UI will be asked for the preferred size.
		    if(_component != null) {
		        _component.setPreferredSize(null);
		    }
			setIcon(iconSouth);
			hidden = false;
		}
		
		else {
		    if(_component != null) {
		        _component.setPreferredSize(zeroSize);
		    }
			setIcon(iconEast);
			hidden = true;
		}
		
		// Redraw
	    if(_component != null) {
	        _component.revalidate();
	        // Not sure if we need this
	        //_component.repaint();
	        
	        // And parents too
	        Component parent = _component.getParent();
	        while(parent != null) {
	            parent.validate();
	            parent = parent.getParent();
	        }
	    }
	}
	
	/**
	 * @return whether the component is hidden or not
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * Set the UI
	 */
	protected void setUI() {
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		setFocusPainted(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	/**
	 * Update the UI
	 */
	public void updateUI() {
		super.updateUI();
		setUI();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return new Dimension(12, 12);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#getMinimumSize()
	 */
	public Dimension getMinimumSize() {
		return new Dimension(5, 5);
	}
}