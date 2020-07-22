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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import uk.ac.reload.dweezil.menu.MenuAction;


/**
 * Cool Bar Panel consisting of a CoolBar in the NORTH and a space for a Component in the CENTER.
 * You can add the Component using addMainComponent().  Clicking on this added component or a child
 * will put the focus on the CoolBar.
 *
 * @author Phillip Beauvoir
 * @version $Id: CoolBarPanel.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class CoolBarPanel
extends JPanel
{
	/**
	 * The CoolBar
	 */
    private CoolBar _coolBar;
	
	/**
	 * The Component to be added below the CoolBar
	 */
    private Component _component;
	
	/**
	 * Mouse Listener for mouse clicks
	 */
	protected MouseAdapter mouseListener = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			_coolBar.setSelected(true);
		}
	};

	/**
	 * Default Constructor
	 */
	public CoolBarPanel() {
		super(new BorderLayout());
		
		_coolBar = new CoolBar();
		add(_coolBar, BorderLayout.NORTH);
		
		setBackground(Color.white);
		
		// Forward on CoolBar Selection Events
		_coolBar.addComponentSelectionListener(new IComponentSelectionListener() {
            public void componentSelected(Component component) {
                fireComponentSelected();
            }
		});
	}
	
	/**
	 * Constructor
	 * @param text The text to display in the CoolBar
	 */
	public CoolBarPanel(String text) {
		this();
		setText(text);
	}
		
	/**
	 * Constructor
	 * @param text The text to display in the CoolBar
	 * @param icon The icon to display in the CoolBar
	 */
	public CoolBarPanel(String text, Icon icon) {
		this(text);
		setIcon(icon);
	}

	/**
	 * Set the text label in the CoolBar
	 * @param text The text to set
	 */
	public void setText(String text) {
		_coolBar.setText(text);
	}
	
	/**
	 * Set the icon for the label in the CoolBar
	 * @param icon The Icon to set
	 */
	public void setIcon(Icon icon) {
		_coolBar.setIcon(icon);
	}

	/**
	 * Set the felection focus.  If true, the bar will be colored.
	 * @param selected true or false
	 */
	public void setSelected(boolean selected) {
		// Set the Selection on the CoolBar
	    _coolBar.setSelected(selected);
	}
	
	/**
	 * Add a Menu Action to the ToolBar.  A Button will be added.
	 * @param action The MenuAction to add
	 */
	public void addMenuActionToCoolBar(MenuAction action) {
		_coolBar.addMenuAction(action);
	}
	
	/**
	 * Add a JButton to the toolbar part of the CoolBar
	 * @param button The Button to add
	 */
	public void addButtonToCoolBar(JButton button) {
		_coolBar.addButton(button);
	}
	
	/**
	 * Add a Component to the toolbar part of the CoolBar
	 * @param component
	 */
	public void addComponentToCoolBar(Component component) {
		_coolBar.addComponent(component);
	}
	
	/**
	 * @return The CoolBar in this Component
	 */
	public CoolBar getCoolBar() {
	    return _coolBar;
	}

	/**
	 * Set the Main Component in the Panel
	 * @param component The Main Component to add
	 */
	public void setMainComponent(Component component) {
		// Remove any old one
		if(_component != null) {
			removeMouseListeners(_component);
			remove(_component);
		}
		
		// Add new one and mouse listeners
		_component = component;
		add(_component, BorderLayout.CENTER);
		addMouseListeners(_component);
	}
	
	/**
	 * @return The Component added to this CoolbarPanel
	 */
	public Component getMainComponent() {
	    return _component;
	}

	/**
	 * Add the Mouse Listener for all child components so that we receive selection type events
	 * @param component the Component
	 */
	protected void addMouseListeners(Component component) {
		component.addMouseListener(mouseListener);
		if(component instanceof Container) {
			Component[] children = ((Container)component).getComponents();
			for(int i = 0; i < children.length; i++) {
				addMouseListeners(children[i]);
			}
		}
	}

	/**
	 * Remove the Mouse Listener for all child components
	 * @param component the Component
	 */
	protected void removeMouseListeners(Component component) {
		component.removeMouseListener(mouseListener);
		if(component instanceof Container) {
			Component[] children = ((Container)component).getComponents();
			for(int i = 0; i < children.length; i++) {
				removeMouseListeners(children[i]);
			}
		}
	}
	
    /**
     * Set enabled or not
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        _coolBar.setEnabled(enabled);
    }

	// ==========================================================================================
	// SELECTION LISTENERS - DELEGATE TO COOLBAR
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
