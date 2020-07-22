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

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import uk.ac.reload.dweezil.menu.MenuAction;


/**
 * Cool Bar - A composite tool bar that consists of a Label and Regular ToolBar.
 * When clicked on, will change to a nice gradient colour.
 *
 * @author Phillip Beauvoir
 * @version $Id: CoolBar.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class CoolBar
extends JPanel
{
	/**
	 * The ToolBar
	 */
    private DweezilToolBar _toolBar;
	
	/**
	 * The Text Label
	 */
    private JLabel _label;
	
	/**
	 * Whether this CoolBar has the selection focus or not
	 */
    private boolean _isSelected;
	
	/**
	 * The size in pixels of the height
	 */
    private int _height = 26;
	
	/**
	 * The Color for the Gradient
	 */
    private Color _color = new Color(128, 128, 255);
	
	/**
	 * Mouse Listener for mouse clicks
	 */
	protected MouseAdapter mouseListener = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			setSelected(true);
		}
	};
	
	/**
	 * Default Constructor
	 */
	public CoolBar() {
		super(new BorderLayout());
		
		setBorder(new EmptyBorder(0, 5, 0, 0));
		
		_label = new JLabel();
		add(_label, BorderLayout.WEST);
		
		_toolBar = new DweezilToolBar();
		add(_toolBar, BorderLayout.EAST);
		
		addMouseListener(mouseListener);
		_label.addMouseListener(mouseListener);
	}
	
	/**
	 * Constructor
	 * @param text The text to display in the CoolBar
	 */
	public CoolBar(String text) {
		this();
		setText(text);
	}
	
	/**
	 * Constructor
	 * @param text The text to display in the CoolBar
	 * @param icon The icon to display in the CoolBar
	 */
	public CoolBar(String text, Icon icon) {
		this(text);
		setIcon(icon);
	}
	
	/**
	 * Set the text label in the CoolBar
	 * @param text The text to set
	 */
	public void setText(String text) {
		_label.setText(text);
	}
	
	/**
	 * Set the icon for the label in the CoolBar
	 * @param icon The icon to set
	 */
	public void setIcon(Icon icon) {
		_label.setIcon(icon);
	}
	
	/**
	 * Set the height of the CoolBar
	 * @param height The height size in pixels
	 */
	public void setHeight(int height) {
		_height = height;
	}
	
	/**
	 * Set the Gradient start color on the CoolBar
	 * @param color The Color
	 */
	public void setGradientColor(Color color) {
		_color = color;
	}
	
	/**
	 * Add a Menu Action to the ToolBar.  A Button will be added.
	 * @param action The MenuAction to add
	 */
	public void addMenuAction(MenuAction action) {
		addButton(action.getButton());
	}
	
	/**
	 * Add a JButton to the toolbar part of the CoolBar
	 * @param button The Button to add
	 */
	public void addButton(JButton button) {
		// Adjust size
		button.setPreferredSize(new Dimension(_height - 2, _height -2));
		button.addMouseListener(mouseListener);
		_toolBar.add(button);
	}
	
	/**
	 * Add a Component to the toolbar part of the CoolBar
	 * @param component
	 */
	public void addComponent(Component component) {
		component.addMouseListener(mouseListener);
		_toolBar.add(component);
	}
	
	/**
	 * Set the Selection focus.  If true, the bar will be colored and a Selection event fired.
	 * @param selected true or false
	 */
	public void setSelected(boolean selected) {
	    // Don't repeat selection
	    if(selected == _isSelected) {
	        return;
	    }
	    
		_isSelected = selected;
		
		if(selected) {
			_label.setForeground(Color.white);
			
			// Tell Selection Listeners
			fireComponentSelected();
		}
		
		else {
			_label.setForeground(Color.black);
		}
		
		repaint();
	}
	
    /**
     * Set enabled or not
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        _label.setEnabled(enabled);
    }

    /**
	 * @return the ToolBar
	 */
	public DweezilToolBar getToolBar() {
		return _toolBar;
	}
	
	/**
	 * Make sure we don't resize too small
	 * @see java.awt.Component#getMinimumSize()
	 */
	public Dimension getMinimumSize() {
		return new Dimension(_label.getWidth() + _toolBar.getWidth() + 10,
				super.getMinimumSize().height);
	}
	
	/** 
	 * Ensure a minimum height
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return new Dimension(super.getPreferredSize().width, _height);
	}
	
	/** 
	 * Paint the Bar
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(_isSelected && isEnabled()) {
			Graphics2D g2d = (Graphics2D)g;
			GradientPaint gradient =
				new GradientPaint(0, 0, _color, (int)(getWidth() * .7), getHeight(), getBackground()); 
			g2d.setPaint(gradient);
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}
	}
	
	
	// ==========================================================================================
	// SELECTION LISTENERS
	// ==========================================================================================
	
	/**
	 * List of Listeners
	 */
	private Vector _listeners;

	/**
	 * Add an IComponentSelectionListener
	 * @param listener The IComponentSelectionListener
	 */
	public synchronized void addComponentSelectionListener(IComponentSelectionListener listener) {
		if(_listeners == null) {
		    _listeners = new Vector();
		}

		if(!_listeners.contains(listener)) {
		    _listeners.addElement(listener);
		}
	}
	
	/**
	 * Remove an IComponentSelectionListener
	 * @param listener The IComponentSelectionListener
	 */
	public synchronized void removeComponentSelectionListener(IComponentSelectionListener listener) {
	    if(_listeners != null) {
	        _listeners.removeElement(listener);
	    }
	}

    /**
     * Tell our listeners that a component was selected
     */
    protected void fireComponentSelected() {
        if(_listeners != null) {
            for(int i = _listeners.size() - 1; i >= 0; i--) {
                IComponentSelectionListener listener = (IComponentSelectionListener)_listeners.elementAt(i);
                listener.componentSelected(this);
            }
        }
    }
}
