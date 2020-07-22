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

import java.awt.Component;
import java.util.Vector;



/**
 * A CoolBarPanel Grouping - Tracks the currently selected CoolBar in a Group of CoolBarPanels.
 *
 * @author Phillip Beauvoir
 * @version $Id: CoolBarPanelGroup.java,v 1.3 2007/07/15 20:27:30 phillipus Exp $
 */
public class CoolBarPanelGroup
implements IComponentSelectionListener
{
	
	/**
	 * The currently selected CoolBarPanel
	 */
    private CoolBarPanel _currentCoolBarPanel;
    
    /**
     * The collection of CoolBars in this Group
     */
    private Vector _group;
    
    /**
     * Default Constructor 
     */
    public CoolBarPanelGroup() {
        
    }
    
    /**
     * Constructor allowing a group of CoolBars
     * 
     * @param coolBarpanels
     */
    public CoolBarPanelGroup(CoolBarPanel[] coolBarpanels) {
        for(int i = 0; i < coolBarpanels.length; i++) {
            addCoolBarPanel(coolBarpanels[i]);
        }
    }

    /**
     * Add a CoolBarPanel to the Grouping
     * @param coolBarPanel The CoolBarPanel to add
     */
    public void addCoolBarPanel(CoolBarPanel coolBarPanel) {
        if(coolBarPanel == null) {
            return;
        }
        
        if(_group == null) {
            _group = new Vector();
        }
        
        if(!_group.contains(coolBarPanel)) {
            _group.add(coolBarPanel);
            coolBarPanel.addComponentSelectionListener(this);
        }
    }
	
    /**
     * Remove a CoolBarPanel from the Grouping
     * @param coolBarPanel The CoolBarPanel to remove
     */
    public void removeCoolBarPanel(CoolBar coolBarPanel) {
        if(coolBarPanel == null) {
            return;
        }

        if(_group != null) {
            _group.remove(coolBarPanel);
            coolBarPanel.removeComponentSelectionListener(this);
        }
    }
    
    /* (non-Javadoc)
     * @see uk.ac.reload.dweezil.gui.IComponentSelectionListener#componentSelected(java.awt.Component)
     */
    public void componentSelected(Component component) {
        if(component instanceof CoolBarPanel) {
            CoolBarPanel coolBarPanel = (CoolBarPanel)component;
			if(_currentCoolBarPanel != null && _currentCoolBarPanel != coolBarPanel) {
			    _currentCoolBarPanel.setSelected(false);
			}
			_currentCoolBarPanel = coolBarPanel;
        }
    }
}
